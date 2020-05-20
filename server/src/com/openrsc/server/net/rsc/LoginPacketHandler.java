package com.openrsc.server.net.rsc;

import com.openrsc.server.Server;
import com.openrsc.server.database.struct.PlayerRecoveryQuestions;
import com.openrsc.server.login.CharacterCreateRequest;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.login.RecoveryAttemptRequest;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.ConnectionAttachment;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.net.RSCConnectionHandler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.LoginResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class LoginPacketHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	public String getString(ByteBuf payload) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (payload.isReadable() && (b = payload.readByte()) != 10)
			bldr.append((char) b);
		return bldr.toString();
	}

	public void processLogin(Packet packet, Channel channel, Server server) {
		final String IP = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();

		switch (packet.getID()) {

			/* Logging in */
			case 0:
				boolean reconnecting = packet.readByte() == 1;
				int clientVersion = packet.readInt();

				final String username = getString(packet.getBuffer()).trim();
				final String password = getString(packet.getBuffer()).trim();

				ConnectionAttachment attachment = new ConnectionAttachment();
				channel.attr(RSCConnectionHandler.attachment).set(attachment);

				final LoginRequest request = new LoginRequest(server, channel, username, password, clientVersion) {
					@Override
					public void loginValidated(int response) {
						Channel channel = getChannel();
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) response).toPacket());
						if ((response & 0x40) == LoginResponse.LOGIN_UNSUCCESSFUL) {
							channel.close();
						}
					}

					@Override
					public void loadingComplete(Player loadedPlayer) {
						getServer().getPacketFilter().addLoggedInPlayer(loadedPlayer.getCurrentIP());

						ConnectionAttachment attachment = channel.attr(RSCConnectionHandler.attachment).get();
						// attachment.ISAAC.set(new ISAACContainer(incomingCipher,
						// outgoingCipher));
						attachment.player.set(loadedPlayer);

						/* Server Configs */
						ActionSender.sendServerConfigs(loadedPlayer);

						if (loadedPlayer.getLastLogin() == 0L) {
							loadedPlayer.setInitialLocation(Point.location(216, 744));
							loadedPlayer.setChangingAppearance(true);
						}

						server.getPluginHandler().handlePlugin(loadedPlayer, "PlayerLogin", new Object[]{loadedPlayer});
						ActionSender.sendLogin(loadedPlayer);
					}
				};
				server.getLoginExecutor().add(request);
				break;

			/* Registering */
			case 78:
				LOGGER.info("Registration attempt from: " + IP);

				String user = getString(packet.getBuffer()).trim();
				String pass = getString(packet.getBuffer()).trim();

				user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
				//pass = pass.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", "");

				String email = getString(packet.getBuffer()).trim();

				if (server.getPacketFilter().shouldAllowLogin(IP, true)) {
					CharacterCreateRequest characterCreateRequest = new CharacterCreateRequest(server, channel, user, pass, email);
					server.getLoginExecutor().add(characterCreateRequest);
				}
				break;

			/* Forgot password */
			case 5:
				try {
					if (!server.getPacketFilter().shouldAllowPacket(channel, true)) {
						channel.close();

						return;
					}

					user = getString(packet.getBuffer()).trim();
					user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");

					int playerID = server.getDatabase().getPlayerLoginData(user).id;

					String[] questions = new String[5];
					boolean foundAndHasRecovery = false;
					PlayerRecoveryQuestions recoveryQuestions = server.getDatabase().getPlayerRecoveryData(playerID);

					if (recoveryQuestions != null) {
						questions[0] = recoveryQuestions.question1;
						questions[1] = recoveryQuestions.question2;
						questions[2] = recoveryQuestions.question3;
						questions[3] = recoveryQuestions.question4;
						questions[4] = recoveryQuestions.question5;

						foundAndHasRecovery = true;
					}

					if (!foundAndHasRecovery) {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
					} else {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) 1).toPacket());
						com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
						String st;
						for (int n = 0; n < 5; ++n) {
							st = questions[n];
							s.writeByte((byte) st.length() + 1);
							s.writeString(st);
						}
						channel.writeAndFlush(s.toPacket());
					}
					channel.close();

				} catch (Exception e) {
					LOGGER.catching(e);
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
					channel.close();
				}
				break;

			/* Attempt recover */
			case 7:
				user = getString(packet.getBuffer()).trim();
				user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
				String oldPass = getString(packet.getBuffer()).trim();
				String newPass = getString(packet.getBuffer()).trim();
				Long uid = packet.getBuffer().readLong();
				String answers[] = new String[5];
				for (int j = 0; j < 5; j++) {
					answers[j] = DataConversions.normalize(getString(packet.getBuffer()).trim(), 50);
				}

				RecoveryAttemptRequest recoveryAttemptRequest = new RecoveryAttemptRequest(server, channel, user, oldPass, newPass, answers);
				server.getLoginExecutor().add(recoveryAttemptRequest);
				break;
		}
	}
}
