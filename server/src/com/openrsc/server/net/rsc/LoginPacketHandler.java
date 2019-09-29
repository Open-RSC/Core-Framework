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
import com.openrsc.server.util.rsc.LoginResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author n0m
 */
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
		String IP = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
		switch (p.getID()) {

			/* Logging in */
			case 0:
				boolean reconnecting = p.readByte() == 1;
				int clientVersion = p.readInt();

				final String username = getString(p.getBuffer()).trim();
				final String password = getString(p.getBuffer()).trim();

				if (clientVersion != server.getConfig().CLIENT_VERSION) {
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) LoginResponse.CLIENT_UPDATED).toPacket());
					channel.close();
					return;
				}
				long i = server.timeTillShutdown();
				if (i > 0 && i < 30000) {
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) LoginResponse.WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS).toPacket());
					channel.close();
					return;
				}

				ConnectionAttachment attachment = new ConnectionAttachment();
				channel.attr(RSCConnectionHandler.attachment).set(attachment);

				final LoginRequest request = new LoginRequest(username, password, clientVersion, channel) {
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
						ConnectionAttachment attachment = (ConnectionAttachment) channel.attr(RSCConnectionHandler.attachment).get();
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
				server.getPlayerDataProcessor().addLoginRequest(request);
				break;

			/* Registering */
			case 78:
				LOGGER.info("Registration attempt from: " + IP);

				String user = getString(p.getBuffer()).trim();
				String pass = getString(p.getBuffer()).trim();

				user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
				//pass = pass.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", "");

				String email = getString(p.getBuffer()).trim();

				CharacterCreateRequest characterCreateRequest = new CharacterCreateRequest(server, user, pass, email, channel);
				server.getPlayerDataProcessor().addCharacterCreateRequest(characterCreateRequest);
				break;
				
			/* Forgot password */
			case 5:
				try {
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
					answers[j] = normalize(getString(p.getBuffer()).trim(), 50);
				}

				RecoveryAttemptRequest recoveryAttemptRequest = new RecoveryAttemptRequest(server, user, oldPass, newPass, answers, channel);
				server.getPlayerDataProcessor().addRecoveryAttemptRequest(recoveryAttemptRequest);
				break;
		}
	}
	
	private static String normalize(String s, int len) {
		String res = addCharacters(s, len);
		res = res.replaceAll("[\\s_]+","_");
		char[] chars = res.trim().toCharArray();
		if (chars.length > 0 && chars[0] == '_')
			chars[0] = ' ';
		if (chars.length > 0 && chars[chars.length-1] == '_')
			chars[chars.length-1] = ' ';
	    return String.valueOf(chars).toLowerCase().trim();  
	}
	
	public static String addCharacters(String s, int i) {
		String s1 = "";
		for (int j = 0; j < i; j++)
			if (j >= s.length()) {
				s1 = s1 + " ";
			} else {
				char c = s.charAt(j);
				if (c >= 'a' && c <= 'z')
					s1 = s1 + c;
				else if (c >= 'A' && c <= 'Z')
					s1 = s1 + c;
				else if (c >= '0' && c <= '9')
					s1 = s1 + c;
				else
					s1 = s1 + '_';
			}

		return s1;
	}
}
