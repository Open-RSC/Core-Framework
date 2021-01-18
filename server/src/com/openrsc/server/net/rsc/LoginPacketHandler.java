package com.openrsc.server.net.rsc;

import com.openrsc.server.Server;
import com.openrsc.server.database.struct.PlayerLoginData;
import com.openrsc.server.database.struct.PlayerRecoveryQuestions;
import com.openrsc.server.login.*;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.*;
import com.openrsc.server.util.rsc.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static com.openrsc.server.net.PcapLogger.VIRTUAL_OPCODE_SERVER_METADATA;

public class LoginPacketHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private int loginResponse = -1;

	public String getString(ByteBuf payload) {
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (payload.isReadable() && (b = payload.readByte()) != 10)
			bldr.append((char) b);
		return bldr.toString();
	}

	public int bytesToInt(byte b1, byte b2, byte b3, byte b4) {
        long val = Byte.toUnsignedInt(b1) << 24;
        val += Byte.toUnsignedInt(b2) << 16;
        val += Byte.toUnsignedInt(b3) << 8;
        val += Byte.toUnsignedInt(b4);
	    return (int)val;
    }

    private void initializePcapLogger(Player loadedPlayer, ConnectionAttachment attachment) {
		if (loadedPlayer.getWorld().getServer().getConfig().WANT_PCAP_LOGGING) {
			long startTime = System.currentTimeMillis();
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss ").format(new Date());
			String fname = timeStamp + loadedPlayer.getUsername();
			attachment.pcapLogger.set(new PcapLogger(fname));

			com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
			s.setID(VIRTUAL_OPCODE_SERVER_METADATA);
			s.writeInt(loginResponse);
			s.writeInt(loadedPlayer.getClientVersion());
			s.writeInt(loadedPlayer.isUsingAuthenticClient() ? 1 : 0);
			s.writeLong(startTime);
			s.writeInt(loadedPlayer.getWorld().getServer().getConfig().WORLD_NUMBER);
			s.writeZeroQuotedString(loadedPlayer.getWorld().getServer().getName());
			s.writeZeroQuotedString(loadedPlayer.getUsername());
			s.writeZeroQuotedString(loadedPlayer.getCurrentIP());
			attachment.pcapLogger.get().addPacket(s.toPacket(), true);
		}
	}

	public void processLogin(Packet packet, Channel channel, Server server) {
		final String IP = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
		ConnectionAttachment attachment = channel.attr(RSCConnectionHandler.attachment).get();
        //ConnectionAttachment attachment = new ConnectionAttachment();
        //channel.attr(RSCConnectionHandler.attachment).set(attachment);
		OpcodeIn opcode = OpcodeIn.getFromList(packet.getID(),
			OpcodeIn.LOGIN, OpcodeIn.REGISTER_ACCOUNT,
			OpcodeIn.FORGOT_PASSWORD, OpcodeIn.RECOVERY_ATTEMPT);
		if (opcode == null)
			return;
		byte authenticClient;
		switch (opcode) {

			/* Logging in */
			case LOGIN:
                try {
                    if (attachment.authenticClient.get()) {
                        authenticClient = 1;
                    } else {
                        authenticClient = 0;
                    }
                } catch (NullPointerException e) {
                    authenticClient = 127;
                }
			    if (authenticClient != 0) {
                    LoginInfo loginInfo = new LoginInfo();

                    // Handle login packet
                    loginInfo.reconnecting = packet.readByte() == 1;
                    int clientVersion = packet.readInt();

                    // Decrypt login block
                    int rsaLength = packet.readUnsignedShort();
                    byte[] loginBlock = Crypto.decryptRSA(packet.readBytes(rsaLength), 0, rsaLength);

                    // Handle login block
                    int checksum = loginBlock[0];
                    if (checksum != 10) { // Authentic client will only send 10 here, probably as a 99.6% reliable "checksum" that it was able to decrypt correctly
                        // return LOGIN_REJECT;
                    }
                    for (int i = 0; i < 4; i++) {
						loginInfo.keys[i] = bytesToInt(loginBlock[1 + i * 4], loginBlock[2 + i * 4], loginBlock[3 + i * 4], loginBlock[4 + i * 4]);
					}
//                    loginInfo.keys[0] = bytesToInt(loginBlock[1], loginBlock[2], loginBlock[3], loginBlock[4]);
//                    loginInfo.keys[1] = bytesToInt(loginBlock[5], loginBlock[6], loginBlock[7], loginBlock[8]);
//                    loginInfo.keys[2] = bytesToInt(loginBlock[9], loginBlock[10], loginBlock[11], loginBlock[12]);
//                    loginInfo.keys[3] = bytesToInt(loginBlock[13], loginBlock[14], loginBlock[15], loginBlock[16]);
                    String password = "";
                    try {
                        // Fun fact: password is always 20 characters long, with spaces at the end.
                        // Spaces in your password are converted to underscores.
                        password = new String(loginBlock, 17, 20, "UTF8").trim();
                    } catch (Exception e) {
                        LOGGER.info("error parsing password in login block");
                        e.printStackTrace();
                    }
                    // TODO: there are ignored nonces at the end of the login block.
                    // If we cared about the cryptographic security gained by checking that those nonces haven't been used before,
                    // we would want that logic here.

                    // Decrypt XTEA block
                    int xteaLength = packet.readUnsignedShort();
                    byte[] xteaBlock = Crypto.decryptXTEA(packet.readBytes(xteaLength), 0, xteaLength, loginInfo.keys);

                    // TODO: there are also ignored nonces at the beginning of the xtea block

                    String username = "";
                    try {
                        username = new String(xteaBlock, 25, xteaBlock.length - 25, "UTF8").trim();
                    } catch (Exception e) {
                        LOGGER.info("error parsing username in xtea block");
                        e.printStackTrace();
                    }

                    final LoginRequest request = new LoginRequest(server, channel, username, password, clientVersion) {
                        @Override
                        public void loginValidated(int response) {
							loginResponse = response;
                            Channel channel = getChannel();
                            channel.writeAndFlush(new PacketBuilder().writeByte((byte) response).toPacket());
                            if ((response & 0x40) == LoginResponse.LOGIN_UNSUCCESSFUL) {
                                channel.close();
                            }
                        }

                        @Override
                        public void loadingComplete(Player loadedPlayer) {
                            ISAACCipher incomingCipher = new ISAACCipher();
                            incomingCipher.setKeys(loginInfo.keys);
                            ISAACCipher outgoingCipher = new ISAACCipher();
                            outgoingCipher.setKeys(loginInfo.keys);
                            attachment.ISAAC.set(new ISAACContainer(incomingCipher, outgoingCipher));

                            getServer().getPacketFilter().addLoggedInPlayer(loadedPlayer.getCurrentIP());
                            attachment.player.set(loadedPlayer);

                            /* Server Configs */
                            if (clientVersion != 235) {
                                attachment.authenticClient.set(false);
                                ActionSender.sendServerConfigs(loadedPlayer);
                            } else {
                                attachment.authenticClient.set(true);
                            }

                            if (loadedPlayer.getLastLogin() == 0L) {
                                loadedPlayer.setInitialLocation(Point.location(216, 744));
                                loadedPlayer.setChangingAppearance(true);
                            }

                            loadedPlayer.setClientVersion(clientVersion);

							initializePcapLogger(loadedPlayer, attachment);

                            server.getPluginHandler().handlePlugin(loadedPlayer, "PlayerLogin", new Object[]{loadedPlayer});
                            ActionSender.sendLogin(loadedPlayer);
                        }
                    };
                    server.getLoginExecutor().add(request);
                    break;
                } else {
			        // Inauthentic client
                    boolean reconnecting = packet.readByte() == 1;
                    int clientVersion = packet.readInt();

                    final String username = getString(packet.getBuffer()).trim();
                    final String password = getString(packet.getBuffer()).trim();

                    final LoginRequest request = new LoginRequest(server, channel, username, password, clientVersion) {
                        @Override
                        public void loginValidated(int response) {
							loginResponse = response;
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
                            attachment.player.set(loadedPlayer);

                            /* Server Configs */
                            ActionSender.sendServerConfigs(loadedPlayer);

                            if (loadedPlayer.getLastLogin() == 0L) {
                                loadedPlayer.setInitialLocation(Point.location(216, 744));
                                loadedPlayer.setChangingAppearance(true);
                            }

                            loadedPlayer.setClientVersion(clientVersion);

							initializePcapLogger(loadedPlayer, attachment);

                            server.getPluginHandler().handlePlugin(loadedPlayer, "PlayerLogin", new Object[]{loadedPlayer});
                            ActionSender.sendLogin(loadedPlayer);
                        }
                    };
                    server.getLoginExecutor().add(request);
                    break;

                }
			/* Registering */
			case REGISTER_ACCOUNT:
				LOGGER.info("Registration attempt from: " + IP);
				try {
					if (attachment.authenticClient.get()) {
						authenticClient = 1;
					} else {
						authenticClient = 0;
					}
				} catch (NullPointerException e) {
					authenticClient = 127;
				}
				if (authenticClient != 0) {
					// Handle register packet
					int clientVersion = packet.readUnsignedShort();
					long userHash = packet.readLong();
					final String username = DataConversions.hashToUsername(userHash);

					// Get encrypted block
					// password is always 20 characters long, with spaces at the end.
					// each blocks having encrypted 7 chars of password
					int blockLen;
					byte[] decBlock; // current decrypted block
					int session = -1; // TODO: should be TCP session to check if request should be processed
					int receivedSession;
					boolean errored = false;
					byte[] passData = new byte[21];
					for (int i = 0; i < 3; i++) {
						blockLen = packet.readUnsignedByte();
						decBlock = Crypto.decryptRSA(packet.readBytes(blockLen), 0, blockLen);
						// TODO: there are ignored nonces at the beginning of the decrypted block
						receivedSession = ByteBuffer.wrap(Arrays.copyOfRange(decBlock, 4, 8)).getInt();
						// decrypted packet must be of length 15
						if (session == -1 && decBlock.length == 15) {
							session = receivedSession;
						} else if (session != receivedSession || decBlock.length != 15) {
							errored = true; // decryption error occurred
						}

						if (!errored) {
							System.arraycopy(decBlock, 8, passData, i * 7, 7);
						}
					}

					String password = "";
					try {
						password = new String(passData, "UTF8").trim();
					} catch (Exception e) {
						LOGGER.info("error parsing password in register block");
						errored = true;
						e.printStackTrace();
					}

					packet.readInt(); // hashed random.dat associated to user request

					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket()); // not known what this should write

					if (server.getPacketFilter().shouldAllowLogin(IP, true) && !errored) {
						CharacterCreateRequest characterCreateRequest = new CharacterCreateRequest(server, channel, username, password, true, clientVersion);
						server.getLoginExecutor().add(characterCreateRequest);
					} else {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) RegisterResponse.REGISTER_UNSUCCESSFUL).toPacket());
						channel.close();
					}

					break;
				} else {
					// Inauthentic client
					String user = getString(packet.getBuffer()).trim();
					String pass = getString(packet.getBuffer()).trim();

					user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");
					//pass = pass.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", "");

					String email = getString(packet.getBuffer()).trim();

					if (server.getPacketFilter().shouldAllowLogin(IP, true)) {
						CharacterCreateRequest characterCreateRequest = new CharacterCreateRequest(server, channel, user, pass, email, server.getConfig().CLIENT_VERSION);
						server.getLoginExecutor().add(characterCreateRequest);
					}
					break;
				}
			/* Forgot password */
			case FORGOT_PASSWORD:
				LOGGER.info("Forgot password attempt from: " + IP);
				try {
					if (attachment.authenticClient.get()) {
						authenticClient = 1;
					} else {
						authenticClient = 0;
					}
				} catch (NullPointerException e) {
					authenticClient = 127;
				}

				if (authenticClient != 0) {
					// Handle forgot password packet
					long userHash = packet.readLong();
					final String username = DataConversions.hashToUsername(userHash);

					PlayerLoginData playerData = null;
					PlayerRecoveryQuestions recoveryQuestions = null;
					boolean errored = false;

					try {
						playerData = server.getDatabase().getPlayerLoginData(username);
					} catch(Exception e) {
						LOGGER.info("error - trying to recover from non existent user");
						errored = true;
					}

					if (playerData != null) {
						int playerID = playerData.id;
						try {
							recoveryQuestions = server.getDatabase().getPlayerRecoveryData(playerID);
						} catch(Exception e) {
							LOGGER.info("error - trying to recover from user without questions set");
							errored = true;
						}
					}
					errored = errored || (recoveryQuestions == null);

					channel.writeAndFlush(new PacketBuilder().writeShort((short) 0).toPacket()); // not known what this should write

					if (server.getPacketFilter().shouldAllowLogin(IP, true) && !errored) {
						String[] questions = new String[5];

						questions[0] = recoveryQuestions.question1;
						questions[1] = recoveryQuestions.question2;
						questions[2] = recoveryQuestions.question3;
						questions[3] = recoveryQuestions.question4;
						questions[4] = recoveryQuestions.question5;

						channel.writeAndFlush(new PacketBuilder().writeByte((byte) ForgotPasswordResponse.FORGOT_PASSWORD_SUCCESSFUL).toPacket());
						com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
						String st;
						for (int n = 0; n < 5; ++n) {
							st = questions[n];
							s.writeByte((byte) st.length());
							s.writeBytes(st.getBytes());
						}
						channel.writeAndFlush(s.toPacket());
						channel.close();
					} else {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) ForgotPasswordResponse.FORGOT_PASSWORD_UNSUCCESSFUL).toPacket());
						channel.close();
					}
					break;
				} else {
					// Inauthentic client
					try {
						if (!server.getPacketFilter().shouldAllowPacket(channel, true)) {
							channel.close();

							return;
						}

						String user = getString(packet.getBuffer()).trim();
						user = user.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ");

						PlayerLoginData player = server.getDatabase().getPlayerLoginData(user);
						if (player == null) {
							channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
							channel.close();
							return;
						}
						int playerID = player.id;

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
				}
			/* Attempt recover */
			case RECOVERY_ATTEMPT:
				LOGGER.info("Recovery attempt from: " + IP);
				try {
					if (attachment.authenticClient.get()) {
						authenticClient = 1;
					} else {
						authenticClient = 0;
					}
				} catch (NullPointerException e) {
					authenticClient = 127;
				}

				if (authenticClient != 0) {
					// Handle recovery packet
					long userHash = packet.readLong();
					final String username = DataConversions.hashToUsername(userHash);

					packet.readInt(); // hashed random.dat associated to user request

					// Get encrypted block
					// old + new password is always 40 characters long, with spaces at the end.
					// each blocks having encrypted 7 chars of password
					int blockLen;
					byte[] decBlock; // current decrypted block
					int session = -1; // TODO: should be TCP session to check if request should be processed
					int receivedSession;
					boolean errored = false;
					byte[] concatPassData = new byte[42];
					for (int i = 0; i < 6; i++) {
						blockLen = packet.readUnsignedByte();
						decBlock = Crypto.decryptRSA(packet.readBytes(blockLen), 0, blockLen);
						// TODO: there are ignored nonces at the beginning of the decrypted block
						receivedSession = ByteBuffer.wrap(Arrays.copyOfRange(decBlock, 4, 8)).getInt();
						// decrypted packet must be of length 15
						if (session == -1 && decBlock.length == 15) {
							session = receivedSession;
						} else if (session != receivedSession || decBlock.length != 15) {
							errored = true; // decryption error occurred
						}

						if (!errored) {
							System.arraycopy(decBlock, 8, concatPassData, i * 7, 7);
						}
					}

					String oldPassword = "";
					String newPassword = "";
					try {
						oldPassword = new String(Arrays.copyOfRange(concatPassData, 0, 20), "UTF8").trim();
						newPassword = new String(Arrays.copyOfRange(concatPassData, 20, 42), "UTF8").trim();
					} catch (Exception e) {
						LOGGER.info("error parsing passwords in recovery block");
						errored = true;
						e.printStackTrace();
					}

					// Get the 5 recovery answers
					int answerLen = 0;
					int expBlocks = 0;
					byte[] answerData;
					String answers[] = new String[5];
					for (int i = 0; i < 5; i++) {
						answerLen = packet.readUnsignedByte();
						expBlocks = (int)Math.ceil(answerLen / 7.0);
						answerData = new byte[expBlocks * 7];
						for (int j = 0; j < expBlocks; j++) {
							blockLen = packet.readUnsignedByte();
							decBlock = Crypto.decryptRSA(packet.readBytes(blockLen), 0, blockLen);
							// TODO: there are ignored nonces at the beginning of the decrypted block
							receivedSession = ByteBuffer.wrap(Arrays.copyOfRange(decBlock, 4, 8)).getInt();
							// decrypted packet must be of length 15
							if (session != receivedSession || decBlock.length != 15) {
								errored = true; // decryption error occurred
							}

							if (!errored) {
								System.arraycopy(decBlock, 8, answerData, j * 7, 7);
							}
						}

						try {
							answers[i] = new String(answerData, "UTF8").trim();
						} catch (Exception e) {
							LOGGER.info("error parsing answer " + i + " in recover block");
							errored = true;
							e.printStackTrace();
						}
					}

					channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket()); // not known what this should write

					if (server.getPacketFilter().shouldAllowLogin(IP, true) && !errored) {
						RecoveryAttemptRequest recoveryAttemptRequest = new RecoveryAttemptRequest(server, channel, username, oldPassword, newPassword, answers);
						server.getLoginExecutor().add(recoveryAttemptRequest);
					} else {
						channel.writeAndFlush(new PacketBuilder().writeByte((byte) RecoverResponse.RECOVER_UNSUCCESSFUL).toPacket());
						channel.close();
					}

					break;
				} else {
					// Inauthentic client
					String user = getString(packet.getBuffer()).trim();
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
}
