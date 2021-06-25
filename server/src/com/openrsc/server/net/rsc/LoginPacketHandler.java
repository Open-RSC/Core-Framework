package com.openrsc.server.net.rsc;

import com.openrsc.server.Server;
import com.openrsc.server.database.struct.PlayerLoginData;
import com.openrsc.server.database.struct.PlayerRecoveryQuestions;
import com.openrsc.server.login.*;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.*;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
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
import java.util.concurrent.atomic.AtomicInteger;

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
			s.writeInt(loadedPlayer.isUsingCustomClient() ? 0 : 1);
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
		OpcodeIn opcode = ReverseOpcodeLookup.getOpcode(packet.getID());
		if (opcode == null)
			return;
		byte authenticClient;

		try {
			if (attachment.authenticClient.get() >= 0) {
				authenticClient = 1;
			} else {
				authenticClient = 0;
			}
		} catch (NullPointerException e) {
			authenticClient = 127;
		}

		Point firstTimeLocation;
		if (server.getConfig().ARRIVE_LUMBRIDGE) {
			firstTimeLocation = Point.location(server.getConfig().RESPAWN_LOCATION_X, server.getConfig().RESPAWN_LOCATION_Y);
		} else {
			firstTimeLocation = Point.location(216, 744);
		}

		switch (opcode) {
			/* Logging in */
			case LOGIN:
			case RELOGIN:
				if (authenticClient != 0) {
					LoginInfo loginInfo = new LoginInfo();

					// CASES
					// 0: Opcode 0 for login and 19 to reconnect, version not sent
					// 1: circa client 93 - 177 read short for version (Opcode 0 is login, 19 is reconnect)
					// 2: sometime around 177+ was with ISAAC read byte to see if first time or reconnect
					// 2.5: then short for client version
					// 2.75: after 204? was int for client version and XTEAs

					int info, info2, info3, valRead;
					final AtomicInteger clientVersion = new AtomicInteger();
					info = packet.readUnsignedByte();
					info2 = packet.readUnsignedByte();

					if (packet.getLength() == 30) {
						// we will assume 38
						clientVersion.set(38);
					} else if (packet.getLength() == 34) {
						// we will assume 61
						clientVersion.set(61);
					} else if (packet.getLength() == 38) {
						// we will assume 74
						clientVersion.set(74);
					} else if (!((info == 0 || info == 1) && (info2 == 0))) {
						// client likely between 93 and 177
						clientVersion.set(info << 8 | info2);
					} else {
						loginInfo.reconnecting = info == 1;
						info3 = packet.readUnsignedByte();
						valRead = info3;
						if (valRead != 0 && valRead <= 204) {
							clientVersion.set(valRead);
						} else {
							clientVersion.set(valRead << 16 | packet.readShort());
						}
					}

					LOGGER.info("Client version: " + clientVersion.get());

					if (clientVersion.get() >= 201) {
						// Different login block generation starting from mud201?
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
							username = new String(xteaBlock, 25, xteaBlock.length - 25, "UTF8");
						} catch (Exception e) {
							LOGGER.info("error parsing username in xtea block");
							e.printStackTrace();
						}

						ClientLimitations cl = new ClientLimitations(clientVersion.get());

						final LoginRequest request = new LoginRequest(server, channel, username, password, true, clientVersion.get(), opcode == OpcodeIn.RELOGIN) {
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

								attachment.player.set(loadedPlayer);

								attachment.authenticClient.set((short) clientVersion.get());

								if (loadedPlayer.getLastLogin() == 0L) {
									loadedPlayer.setInitialLocation(firstTimeLocation);
									loadedPlayer.setChangingAppearance(true);
								}

								loadedPlayer.setClientVersion((short) getVersion(clientVersion.get(), loadedPlayer));
								loadedPlayer.setClientLimitations(cl);

								initializePcapLogger(loadedPlayer, attachment);

								server.getPluginHandler().handlePlugin(loadedPlayer, "PlayerLogin", new Object[]{loadedPlayer});
								ActionSender.sendLogin(loadedPlayer);

								getServer().getPacketFilter().addLoggedInPlayer(loadedPlayer.getCurrentIP());
							}
						};
						server.getLoginExecutor().add(request);
					} else if (clientVersion.get() > 177) {
						// login block with initial ISAAC
						// TODO
					} else if (clientVersion.get() >= 93) {
						short referId = packet.readShort();
						long userHash = packet.readLong();
						final String username = DataConversions.hashToUsername(userHash);

						// Get encrypted block
						// password is always 20 characters long, with spaces at the end.
						// each blocks having encrypted 7 chars of password
						int blockLen;
						byte[] decBlock; // current decrypted block
						int session = attachment.sessionId.get() == null ? -1 : attachment.sessionId.get();
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
							LOGGER.info("error parsing password in login block");
							errored = true;
							e.printStackTrace();
						}

						packet.readInt(); // hashed random.dat associated to user request
						final int sessionId = session;

						channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket()); // not known what this should write

						if (!errored) {
							ClientLimitations cl = new ClientLimitations(clientVersion.get());
							final LoginRequest request = new LoginRequest(server, channel, username, password, true, clientVersion.get(), opcode == OpcodeIn.RELOGIN) {
								@Override
								public void loginValidated(int response) {
									loginResponse = response;
									Channel channel = getChannel();
									channel.writeAndFlush(new PacketBuilder().writeByte((byte) response).toPacket());
									if (response != 0 && response != 1) {
										channel.close();
									}
								}

								@Override
								public void loadingComplete(Player loadedPlayer) {
									attachment.player.set(loadedPlayer);

									attachment.authenticClient.set((short) clientVersion.get());

									if (loadedPlayer.getLastLogin() == 0L) {
										loadedPlayer.setInitialLocation(firstTimeLocation);
										loadedPlayer.setChangingAppearance(true);
									}

									loadedPlayer.setClientVersion((short) getVersion(clientVersion.get(), loadedPlayer));
									loadedPlayer.setClientLimitations(cl);
									loadedPlayer.sessionId = sessionId;

									initializePcapLogger(loadedPlayer, attachment);

									server.getPluginHandler().handlePlugin(loadedPlayer, "PlayerLogin", new Object[]{loadedPlayer});
									ActionSender.sendLogin(loadedPlayer);
									getServer().getPacketFilter().addLoggedInPlayer(loadedPlayer.getCurrentIP());
								}
							};
							server.getLoginExecutor().add(request);
						} else {
							channel.writeAndFlush(new PacketBuilder().writeByte((byte) RegisterLoginResponse.UNSUCCESSFUL).toPacket());
							channel.close();
						}
					}
					else if (packet.getLength() >= 30) {
						// mudclients between 38 and 93
						int upperNameHash, lowerNameHash;
						info3 = packet.readShort();
						long nameHash;
						upperNameHash = ((info & 0xff) << 24) | ((info2 & 0xff) << 16) | (info3 & 0xffff);
						lowerNameHash = packet.readInt();
						nameHash = ((upperNameHash & 0xffffffffL) << 32) | (lowerNameHash & 0xffffffffL);
						final String username = DataConversions.hashToUsername(nameHash);

						final String password = packet.readString(20).trim();

						if (packet.getLength() >= 34) {
							// local IP or some sort of seed
							packet.readByte();
							packet.readByte();
							packet.readByte();
							packet.readByte();
						}
						if (packet.getLength() >= 38) {
							packet.readInt(); // some other seed?
						}

						channel.writeAndFlush(new PacketBuilder().writeShort((short) 0).toPacket()); // not known what this should write

						ClientLimitations cl = new ClientLimitations(clientVersion.get());

						final LoginRequest request = new LoginRequest(server, channel, username, password, true, clientVersion.get(), opcode == OpcodeIn.RELOGIN) {
							@Override
							public void loginValidated(int response) {
								loginResponse = response;
								Channel channel = getChannel();
								channel.writeAndFlush(new PacketBuilder().writeByte((byte) response).toPacket());
								if (response != 0 && response != 1) {
									channel.close();
								}
							}

							@Override
							public void loadingComplete(Player loadedPlayer) {
								attachment.player.set(loadedPlayer);

								attachment.authenticClient.set((short) clientVersion.get());

								if (loadedPlayer.getLastLogin() == 0L) {
									loadedPlayer.setInitialLocation(firstTimeLocation);
									loadedPlayer.setChangingAppearance(true);
								}

								loadedPlayer.setClientVersion((short) getVersion(clientVersion.get(), loadedPlayer));
								loadedPlayer.setClientLimitations(cl);

								initializePcapLogger(loadedPlayer, attachment);

								server.getPluginHandler().handlePlugin(loadedPlayer, "PlayerLogin", new Object[]{loadedPlayer});
								ActionSender.sendLogin(loadedPlayer);

								getServer().getPacketFilter().addLoggedInPlayer(loadedPlayer.getCurrentIP());
							}
						};
						server.getLoginExecutor().add(request);
					}
					break;
				} else {
					if (opcode == OpcodeIn.RELOGIN) {
						// RELOGIN opcode implemented currently different for custom
						return;
					}
					// Inauthentic client
					boolean reconnecting = packet.readByte() == 1;
					int clientVersion = packet.readInt();

					final String username = getString(packet.getBuffer()).trim();
					final String password = getString(packet.getBuffer()).trim();

					long uid = packet.readLong(); // random data, not used...

					// determine if client is too out of date to support everything the server will want to show it
					ClientLimitations cl = new ClientLimitations(clientVersion);
					if (packet.getReadableBytes() > 0) {
						cl.maxAnimationId = packet.readShort();
						cl.maxItemId = packet.readInt();
						cl.maxNpcId = packet.readInt();
						cl.maxSceneryId = packet.readInt();
						cl.maxPrayerId = packet.readShort();
						cl.maxSpellId = packet.readShort();
						cl.maxSkillId = packet.readUnsignedByte() & 0xFF;
						cl.maxRoofId = packet.readShort();
						cl.maxTextureId = packet.readShort();
						cl.maxTileId = packet.readShort();
						cl.maxBoundaryId = packet.readInt();
						cl.maxTeleBubbleId = packet.readUnsignedByte() & 0xFF;
						cl.maxProjectileSprite = packet.readShort();
						cl.maxSkinColor = packet.readInt();
						cl.maxHairColor = packet.readInt();
						cl.maxClothingColor = packet.readInt();
						cl.maxQuestId = packet.readShort();
						cl.numberOfSounds = packet.readInt();
						cl.supportsModSprites = packet.readUnsignedByte() & 0xFF;
						cl.maxDialogueOptions = packet.readUnsignedByte() & 0xFF;
						cl.maxBankItems = packet.readInt();
						cl.mapHash = packet.readString();
					}

					final LoginRequest request = new LoginRequest(server, channel, username, password, false, clientVersion, opcode == OpcodeIn.RELOGIN) {
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
							ConnectionAttachment attachment = channel.attr(RSCConnectionHandler.attachment).get();
							attachment.player.set(loadedPlayer);

							/* Server Configs */
							ActionSender.sendServerConfigs(loadedPlayer);

							if (loadedPlayer.getLastLogin() == 0L) {
								loadedPlayer.setInitialLocation(firstTimeLocation);
								loadedPlayer.setChangingAppearance(true);
							}

							loadedPlayer.setClientVersion(clientVersion);
							loadedPlayer.setClientLimitations(cl);

							initializePcapLogger(loadedPlayer, attachment);

							server.getPluginHandler().handlePlugin(loadedPlayer, "PlayerLogin", new Object[]{loadedPlayer});
							ActionSender.sendLogin(loadedPlayer);

							getServer().getPacketFilter().addLoggedInPlayer(loadedPlayer.getCurrentIP());
						}
					};
					server.getLoginExecutor().add(request);
					break;

				}
				/* Registering */
			case REGISTER_ACCOUNT:
				LOGGER.info("Registration attempt from: " + IP);

				if (authenticClient != 0) {
					// CASES
					// 0: Opcode 2, newplayer with email field (len=40) and the 3 checkboxes info (only 2 used) as int (total len=80)
					// 0.75: client 73 or 74 - no longer sent email but some generated int (total len 32?)
					// 1: circa client 93 onwards read short for version + modern encrypted password

					if (packet.getLength() == 80) {
						// we will assume 38
						long userHash = packet.readLong();
						final String user = DataConversions.hashToUsername(userHash);
						final String pass = packet.readString(20).trim();
						final String email = packet.readString(40).trim();

						packet.readInt(); // ??
						final int wantNews = packet.readInt(); // want newsletter
						packet.readInt(); // unused

						channel.writeAndFlush(new PacketBuilder().writeShort((short) 0).toPacket()); // not known what this should write

						if (server.getPacketFilter().shouldAllowLogin(IP, true)) {
							CharacterCreateRequest characterCreateRequest = new CharacterCreateRequest(server, channel, user, pass, email, 38);
							server.getLoginExecutor().add(characterCreateRequest);
						}
					} else if (packet.getLength() == 32) {
						// mudclient at 73?-92?
						//TODO: verify
						long userHash = packet.readLong();
						final String user = DataConversions.hashToUsername(userHash);
						final String pass = packet.readString(20).trim();
						packet.readInt(); // some int

						channel.writeAndFlush(new PacketBuilder().writeShort((short) 0).toPacket()); // not known what this should write

						if (server.getPacketFilter().shouldAllowLogin(IP, true)) {
							CharacterCreateRequest characterCreateRequest = new CharacterCreateRequest(server, channel, user, pass, true, 74);
							server.getLoginExecutor().add(characterCreateRequest);
						}
					} else {
						// mudclients 93+
						// Handle register packet
						int clientVersion = packet.readUnsignedShort();
						if (clientVersion >= 204) { // note that register packet doesn't actually exist in 204+, but this is mostly copied from 127 + utilization of RSA
							long userHash = packet.readLong();
							final String username = DataConversions.hashToUsername(userHash);

							// Get encrypted block
							// password is always 20 characters long, with spaces at the end.
							// each blocks having encrypted 7 chars of password
							int blockLen;
							byte[] decBlock; // current decrypted block
							int session = attachment.sessionId.get() == null ? -1 : attachment.sessionId.get();
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
								LOGGER.info("error parsing password in login block");
								errored = true;
								e.printStackTrace();
							}

							packet.readInt(); // hashed random.dat associated to user request

							channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket()); // not known what this should write

							if (server.getPacketFilter().shouldAllowLogin(IP, true) && !errored) {
								CharacterCreateRequest characterCreateRequest = new CharacterCreateRequest(server, channel, username, password, true, clientVersion);
								server.getLoginExecutor().add(characterCreateRequest);
							} else {
								channel.writeAndFlush(new PacketBuilder().writeByte((byte) RegisterLoginResponse.UNSUCCESSFUL).toPacket());
								channel.close();
							}
						} else if (clientVersion <= 177) {
							long userHash = packet.readLong();
							final String username = DataConversions.hashToUsername(userHash);
							int referId = packet.readShort();

							// Get encrypted block
							// password is always 20 characters long, with spaces at the end.
							// each blocks having encrypted 7 chars of password
							int blockLen;
							byte[] decBlock; // current decrypted block
							int session = attachment.sessionId.get() == null ? -1 : attachment.sessionId.get();
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
								LOGGER.info("error parsing password in login block");
								errored = true;
								e.printStackTrace();
							}

							packet.readInt(); // hashed random.dat associated to user request

							channel.writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket()); // not known what this should write

							if (server.getPacketFilter().shouldAllowLogin(IP, true) && !errored) {
								CharacterCreateRequest characterCreateRequest = new CharacterCreateRequest(server, channel, username, password, true, clientVersion);
								server.getLoginExecutor().add(characterCreateRequest);
							} else {
								channel.writeAndFlush(new PacketBuilder().writeByte((byte) RegisterLoginResponse.UNSUCCESSFUL).toPacket());
								channel.close();
							}
						}
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

				if (authenticClient != 0) {
					// Handle forgot password packet
					long userHash = packet.readLong();
					final String username = DataConversions.hashToUsername(userHash);

					PlayerLoginData playerData = null;
					PlayerRecoveryQuestions recoveryQuestions = null;
					boolean errored = false;

					try {
						playerData = server.getDatabase().getPlayerLoginData(DataConversions.sanitizeUsername(username));
					} catch (Exception e) {
						LOGGER.info("error - trying to recover from non existent user");
						errored = true;
					}

					if (playerData != null) {
						int playerID = playerData.id;
						try {
							recoveryQuestions = server.getDatabase().getPlayerRecoveryData(playerID);
						} catch (Exception e) {
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

						String user = DataConversions.sanitizeUsername(
								getString(packet.getBuffer())
						);

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
					int session = attachment.sessionId.get() == null ? -1 : attachment.sessionId.get();
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
						expBlocks = (int) Math.ceil(answerLen / 7.0);
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
					String user = DataConversions.sanitizeUsername(
							getString(packet.getBuffer()).trim()
					);
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

	public int getVersion(int retrievedVersion, Player player) {
		if (retrievedVersion >= 93 && retrievedVersion <= 235) {
			return retrievedVersion;
		}
		if (retrievedVersion < 93) {
			// not known version from login info
			// retrievedVersion is more of a guess
			int lastSetVersion;
			if (player.getCache().hasKey("client_version") &&
				player.getCache().getInt("client_version") >= 14
				&& player.getCache().getInt("client_version") < 93) {
				// although the mudclient 14 has not been retrieved yet, this would allow it
				lastSetVersion = player.getCache().getInt("client_version");
			} else {
				lastSetVersion = retrievedVersion;
			}
			return  lastSetVersion;
		}
		return player.getWorld().getServer().getConfig().CLIENT_VERSION;
	}
}
