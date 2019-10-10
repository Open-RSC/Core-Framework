package com.openrsc.server.net.rsc;

import com.openrsc.server.Server;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

	public void processLogin(Packet p, Channel channel, Server server) {
		final String IP = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();

		switch (p.getID()) {

			/* Logging in */
			case 0:
				boolean reconnecting = p.readByte() == 1;
				int clientVersion = p.readInt();

				final String username = getString(p.getBuffer()).trim();
				final String password = getString(p.getBuffer()).trim();

				ConnectionAttachment attachment = new ConnectionAttachment();
				channel.attr(RSCConnectionHandler.attachment).set(attachment);

				final LoginRequest request = new LoginRequest(server, channel, username, password, clientVersion) {
					@Override
					public void loginValidated(int response) {
						Channel channel = getChannel();
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) response).toPacket());
						if ((response & 0x40) == LoginResponse.LOGIN_INSUCCESSFUL) {
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

						server.getPluginHandler().handleAction("PlayerLogin", new Object[]{loadedPlayer});
						ActionSender.sendLogin(loadedPlayer);
					}
				};
				server.getLoginExecutor().addLoginRequest(request);
				break;

			/* Registering */
			case 78:
				LOGGER.info("Registration attempt from: " + IP);

				if(server.getPacketFilter().shouldAllowLogin(IP, true)) {
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 5).toPacket());
					channel.close();
				}

				String user = getString(p.getBuffer()).trim();
				String pass = getString(p.getBuffer()).trim();

				user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
				//pass = pass.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", "");

				String email = getString(p.getBuffer()).trim();

				CharacterCreateRequest characterCreateRequest = new CharacterCreateRequest(server, channel, user, pass, email);
				server.getLoginExecutor().addCharacterCreateRequest(characterCreateRequest);
				break;
				
			/* Forgot password */
			case 5:
				try {
					if (!server.getPacketFilter().shouldAllowPacket(channel, true)) {
						channel.close();

						return;
					}

					user = getString(p.getBuffer()).trim();
					user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
					
					PreparedStatement statement = server.getDatabaseConnection().prepareStatement("SELECT id FROM " + server.getConfig().MYSQL_TABLE_PREFIX + "players WHERE username=?");
					statement.setString(1, user);
					ResultSet res = statement.executeQuery();
					ResultSet res2 = null;
					boolean foundAndHasRecovery = false;
					
					if (res.next()) {
						statement = server.getDatabaseConnection().prepareStatement("SELECT * FROM " + server.getConfig().MYSQL_TABLE_PREFIX + "player_recovery WHERE playerID=?");
						statement.setInt(1, res.getInt("id"));
						res2 = statement.executeQuery();
						if (res2.next()) {
							foundAndHasRecovery = true;
						}
					}
					
					if (!foundAndHasRecovery) {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
						channel.close();
					} else {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) 1).toPacket());
						com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
						String st;
						for (int n = 0; n < 5; ++n) {
							st = res2.getString("question"+(n+1));
							s.writeByte((byte)st.length()+1);
							s.writeString(st);
						}
						channel.writeAndFlush(s.toPacket());
						channel.close();
					}
				} catch (Exception e) {
					LOGGER.catching(e);
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
					channel.close();
				}
				break;
			
			/* Attempt recover */
			case 7:
				user = getString(p.getBuffer()).trim();
				user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
				String oldPass = getString(p.getBuffer()).trim();
				String newPass = getString(p.getBuffer()).trim();
				Long uid = p.getBuffer().readLong();
				String answers[] = new String[5];
				for (int j=0; j<5; j++) {
					answers[j] = DataConversions.normalize(getString(p.getBuffer()).trim(), 50);
				}

				RecoveryAttemptRequest recoveryAttemptRequest = new RecoveryAttemptRequest(server, channel, user, oldPass, newPass, answers);
				server.getLoginExecutor().addRecoveryAttemptRequest(recoveryAttemptRequest);
				break;
		}
	}
}
