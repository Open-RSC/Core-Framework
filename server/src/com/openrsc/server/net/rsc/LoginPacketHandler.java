package com.openrsc.server.net.rsc;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.ConnectionAttachment;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.net.RSCConnectionHandler;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.DatabasePlayerLoader;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.SecurityChangeLog;
import com.openrsc.server.sql.query.logs.SecurityChangeLog.ChangeEvent;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.LoginResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * @author n0m
 */
public class LoginPacketHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private static boolean isValidEmailAddress(String email) {
		boolean stricterFilter = true;
		String stricterFilterString = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
		String laxString = ".+@.+\\.[A-Za-z]{2}[A-Za-z]*";
		String emailRegex = stricterFilter ? stricterFilterString : laxString;
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailRegex);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	public String getString(ByteBuf payload) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (payload.isReadable() && (b = payload.readByte()) != 10)
			bldr.append((char) b);
		return bldr.toString();
	}

	public void processLogin(Packet p, Channel channel) throws Exception {
		String IP = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
		switch (p.getID()) {

			/* Logging in */
			case 0:
				boolean reconnecting = p.readByte() == 1;
				int clientVersion = p.readInt();

				final String username = getString(p.getBuffer()).trim();
				final String password = getString(p.getBuffer()).trim();

				if (clientVersion != Constants.GameServer.CLIENT_VERSION) {
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) LoginResponse.CLIENT_UPDATED).toPacket());
					channel.close();
					return;
				}
				int i = Server.getServer().timeTillShutdown();
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

						PluginHandler.getPluginHandler().handleAction("PlayerLogin", new Object[]{loadedPlayer});
						ActionSender.sendLogin(loadedPlayer);
					}
				};
				Server.getPlayerDataProcessor().addLoginRequest(request);
				break;

			/* Registering */
			case 78:
				LOGGER.info("Registration attempt from: " + IP);

				String user = getString(p.getBuffer()).trim();
				String pass = getString(p.getBuffer()).trim();

				user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
				//pass = pass.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", "");

				String email = getString(p.getBuffer()).trim();

				if (user.length() < 2 || user.length() > 12) {
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 7).toPacket());
					channel.close();
					return;
				}

				if (pass.length() < 4 || pass.length() > 64) {
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 8).toPacket());
					channel.close();
					return;
				}

				if (Constants.GameServer.WANT_EMAIL) {
					if (!isValidEmailAddress(email)) {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) 6).toPacket());
						channel.close();
						return;
					}
				}


				ResultSet set = DatabaseConnection.getDatabase().executeQuery("SELECT 1 FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE creation_ip='" + IP
					+ "' AND creation_date>'" + ((System.currentTimeMillis() / 1000) - 60) + "'"); // Checks to see if the player has been registered by the same IP address in the past 1 minute

				if (Constants.GameServer.WANT_REGISTRATION_LIMIT) {
					if (set.next()) {
						set.close();
						LOGGER.info(IP + " - Registration failed: Registered recently.");
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) 5).toPacket());
						channel.close();
						return;
					}
				}

				set = DatabaseConnection.getDatabase().executeQuery("SELECT 1 FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`='" + user + "'");
				if (set.next()) {
					set.close();
					LOGGER.info(IP + " - Registration failed: Forum Username already in use.");
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 2).toPacket());
					channel.close();
					return;
				}

				set = DatabaseConnection.getDatabase().executeQuery("SELECT 1 FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`='" + user + "'");
				if (set.next()) {
					set.close();
					LOGGER.info(IP + " - Android registration failed: Character Username already in use.");
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 2).toPacket());
					channel.close();
					return;
				}

				String newSalt = DataConversions.generateSalt();

				/* Create the game character */
				try {
					PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement(
						"INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` (`username`, email, `pass`, `salt`, `creation_date`, `creation_ip`) VALUES (?, ?, ?, ?, ?, ?)");
					statement.setString(1, user);
					statement.setString(2, email);
					statement.setString(3, DataConversions.hashPassword(pass, newSalt));
					statement.setString(4, newSalt);
					statement.setLong(5, System.currentTimeMillis() / 1000);
					statement.setString(6, IP);
					statement.executeUpdate();
					statement = null;

					/* PlayerID of the player account */
					statement = DatabaseConnection.getDatabase().prepareStatement("SELECT id FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE username=?");
					statement.setString(1, user);

					set = statement.executeQuery();

					if (!set.next()) {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) 6).toPacket());
						LOGGER.info(IP + " - Registration failed: Player id not found.");
						return;
					}

					int playerID = set.getInt("id");

					statement = DatabaseConnection.getDatabase().prepareStatement("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "curstats` (`playerID`) VALUES (?)");
					statement.setInt(1, playerID);
					statement.executeUpdate();

					statement = DatabaseConnection.getDatabase().prepareStatement("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "experience` (`playerID`) VALUES (?)");
					statement.setInt(1, playerID);
					statement.executeUpdate();

					//Don't rely on the default values of the database.
					//Update the stats based on their StatDef-----------------------------------------------
					statement = DatabaseConnection.getDatabase().prepareStatement(DatabasePlayerLoader.Statements.updateExperience);
					statement.setInt(Skills.getSkillCount() + 1, playerID);
					Skills newGuy = new Skills(null);

					for (int index = 0; index < Skills.getSkillCount(); index++)
						statement.setInt(index + 1, newGuy.getExperience(index));
					statement.executeUpdate();

					statement = DatabaseConnection.getDatabase().prepareStatement(DatabasePlayerLoader.Statements.updateStats);
					statement.setInt(Skills.getSkillCount() + 1, playerID);
					for (int index = 0; index < Skills.getSkillCount(); index++)
						statement.setInt(index + 1, newGuy.getLevel(index));
					statement.executeUpdate();
					//---------------------------------------------------------------------------------------

					LOGGER.info(IP + " - Registration successful");
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
				} catch (Exception e) {
					LOGGER.catching(e);
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 5).toPacket());
					channel.close();
				}
				break;
				
			/* Forgot password */
			case 5:
				try {
					user = getString(p.getBuffer()).trim();
					user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
					
					PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT id FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE username=?");
					statement.setString(1, user);
					ResultSet res = statement.executeQuery();
					ResultSet res2 = null;
					boolean foundAndHasRecovery = false;
					
					if (res.next()) {
						statement = DatabaseConnection.getDatabase().prepareStatement("SELECT * FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_recovery WHERE playerID=?");
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
				try {
					user = getString(p.getBuffer()).trim();
					user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
					String oldPass = getString(p.getBuffer()).trim();
					String newPass = getString(p.getBuffer()).trim();
					Long uid = p.getBuffer().readLong();
					String answers[] = new String[5];
					for (i=0; i<5; i++) {
						answers[i] = normalize(getString(p.getBuffer()).trim(), 50);
					}
					
					int pid = -1;
					
					PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT id, pass, salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE username=?");
					statement.setString(1, user);
					ResultSet res = statement.executeQuery();
					ResultSet res2 = null;
					boolean foundAndHasRecovery = false;
					
					if (res.next()) {
						pid = res.getInt("id");
						statement = DatabaseConnection.getDatabase().prepareStatement("SELECT * FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_recovery WHERE playerID=?");
						statement.setInt(1, pid);
						res2 = statement.executeQuery();
						if (res2.next()) {
							foundAndHasRecovery = true;
						}
					}
					
					if (!foundAndHasRecovery) {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
						channel.close();
					} else {
						String salt = res.getString("salt");
						String currDBPass = res.getString("pass");
						oldPass = DataConversions.hashPassword(oldPass, salt);
						newPass = DataConversions.hashPassword(newPass, salt);
						for (i=0; i<5; i++) {
							answers[i] = DataConversions.hashPassword(answers[i], salt);
						}
						
						int numCorrect = (oldPass.equals(res2.getString("previous_pass"))
								|| oldPass.equals(res2.getString("earlier_pass"))) ? 1 : 0;
						for (i=0; i<5; i++) {
							numCorrect += (answers[i].equals(res2.getString("answer"+(i+1))) ? 1 : 0);
						}
						
						PreparedStatement attempt = DatabaseConnection.getDatabase().prepareStatement("INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
						+ "recovery_attempts`(`playerID`, `username`, `time`, `ip`) VALUES(?, ?, ?, ?)", new String[]{"dbid"});
						attempt.setInt(1, pid);
						attempt.setString(2, user);
						attempt.setLong(3, System.currentTimeMillis() / 1000);
						attempt.setString(4, IP);
						attempt.executeUpdate();
						set = attempt.getGeneratedKeys();

						int tryID = -1;
						if (set.next()) {
							tryID = set.getInt(1);
						}
						
						PreparedStatement innerStatement;
						
						//enough treshold to allow pass change for recovery
						if (numCorrect >= 4) {
							innerStatement = DatabaseConnection.getDatabase().prepareStatement(
									"UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` SET `pass`=?, `lastRecoveryTryId`=? WHERE `id`=?");
							innerStatement.setString(1, newPass);
							innerStatement.setInt(2, tryID);
							innerStatement.setInt(3, pid);
							innerStatement.executeUpdate();
							
							//log password change
							GameLogging.addQuery(new SecurityChangeLog(pid, ChangeEvent.PASSWORD_CHANGE, IP,
								"(@Recovery) From: " + currDBPass + ", To: " + newPass));
							
							channel.writeAndFlush(new PacketBuilder().writeByte((byte) 1).toPacket());
							channel.close();
						} else {
							innerStatement = DatabaseConnection.getDatabase().prepareStatement(
									"UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` SET `lastRecoveryTryId`=? WHERE `id`=?");
							innerStatement.setInt(1, tryID);
							innerStatement.setInt(2, pid);
							innerStatement.executeUpdate();
							
							channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
							channel.close();
						}
					}
					
				} catch (Exception e) {
					LOGGER.catching(e);
					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
					channel.close();
				}
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
