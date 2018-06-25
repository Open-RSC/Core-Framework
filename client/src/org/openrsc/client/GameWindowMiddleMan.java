package org.openrsc.client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;

import org.openrsc.client.util.DataConversions;

public abstract class GameWindowMiddleMan<Delegate_T extends ImplementationDelegate> extends openrsc<Delegate_T> {

	public final void sendGeneralPurposeMessage(long target, String message) {
		streamClass.createPacket(86);
		streamClass.addLong(target);
		// vv impressive to whip this bad boy out at parties. lol
		String[] messages = message.split("(?<=\\G.{80})");
		if (messages.length > 10) {
			messages = Arrays.copyOf(messages, 10);
		}
		streamClass.add4ByteInt(messages.length);
		for (String s : messages) {
			streamClass.addString(s);
		}
		streamClass.formatPacket();
	}

	public final void login(String user, String pass, boolean reconnecting) {
		if (socketTimeout > 0) {
			loginScreenPrint("Please wait...", "Connecting to server");
			try {
				Thread.sleep(2000L);
			} catch (Exception _ex) {
			}
			loginScreenPrint("Sorry! The server is currently full.", "Please try again later");
			return;
		}
		long sessionID = 0;
		try {
			username = user;
			user = DataOperations.addCharacters(user, 20);
			password = pass;
			pass = DataOperations.addCharacters(pass, 20);

			if (user.trim().length() == 0) {
				loginScreenPrint("You must enter both a username", "and a password - Please try again");
				return;
			}
			loginScreenPrint("Please wait...", "Connecting to server");

			Socket socket = new Socket();// new
											// Socket(InetAddress.getByName(Config.SERVER_IP),
											// Config.SERVER_PORT);
			socket.setSoTimeout(30000);
			socket.setTcpNoDelay(true);
			socket.connect(Config.ADDR);
			streamClass = new StreamClass(socket, this);
			streamClass.maxPacketReadCount = maxPacketReadCount;
			long l = DataOperations.stringLength12ToLong(user);
			streamClass.createPacket(2);
			streamClass.addByte((int) (l >> 16 & 31L));
			streamClass.addString(new String(pingpacket));
			streamClass.finalisePacket();
			sessionID = streamClass.read8ByteLong();
			if (sessionID == 0L) {
				loginScreenPrint("Login server offline.", "Please try again in a few mins");
				return;
			}
			int sessionRotationKeys[] = new int[4];
			sessionRotationKeys[0] = (int) (Math.random() * 99999999D);
			sessionRotationKeys[1] = (int) (Math.random() * 99999999D);
			sessionRotationKeys[2] = (int) (sessionID >> 32);
			sessionRotationKeys[3] = (int) sessionID;
			DataEncryption dataEncryption = new DataEncryption(new byte[500]);
			dataEncryption.offset = 0;
			dataEncryption.add4ByteInt(sessionRotationKeys[0]);
			dataEncryption.add4ByteInt(sessionRotationKeys[1]);
			dataEncryption.add4ByteInt(sessionRotationKeys[2]);
			dataEncryption.add4ByteInt(sessionRotationKeys[3]);
			dataEncryption.add4ByteInt(gW);
			dataEncryption.addString(user);
			dataEncryption.addString(pass);
			dataEncryption.encryptPacketWithKeys(key, modulus);
			streamClass.createPacket(75);
			if (reconnecting)
				streamClass.addByte(1);
			else
				streamClass.addByte(0);
			streamClass.add2ByteInt(Config.CLIENT_VERSION);
			streamClass.addBytes(dataEncryption.packet, 0, dataEncryption.offset);
			streamClass.finalisePacket();
			int loginResponse = streamClass.readInputStream();
			System.out.println("Login Response: " + loginResponse);
			if (loginResponse == 99) {
				reconnectTries = 0;
				resetVars();
			} else if (loginResponse == 0) {
				reconnectTries = 0;
				resetVars();
			} else if (loginResponse == 1) {
				reconnectTries = 0;
			} else if (reconnecting) {
				// user = "";
				// pass = "";
				resetIntVars();
			} else if (loginResponse == -1) {
				loginScreenPrint("Error unable to login.", "Server timed out");
			} else if (loginResponse == 2) {
				loginScreenPrint("Invalid username or password.", "Try again, or create a new account");
			} else if (loginResponse == 3) {
				loginScreenPrint("That username is already logged in.", "Wait 60 seconds then retry");
			} else if (loginResponse == 4) {
				loginScreenPrint("The client has been updated.", "Please download the newest one");
			} else if (loginResponse == 5) {
				loginScreenPrint("Error unable to login.", "Server rejected session");
			} else if (loginResponse == 6) {
				loginScreenPrint("Character banned.", "Please post a topic in \"Offence Appeal\"");
			} else if (loginResponse == 7) {
				loginScreenPrint("Failed to decode character.", "Please post a topic in \"Support\"");
			} else if (loginResponse == 8) {
				loginScreenPrint("IP Already in use.", "You may only login once at a time");
			} else if (loginResponse == 9) {
				loginScreenPrint("Account already in use.", "You may only login to one character at a time");
			} else if (loginResponse == 10) {
				loginScreenPrint("Server full!.", "Please try again later.");
			} else if (loginResponse == 11) {
				loginScreenPrint("Character banned.", "Please post a topic in \"Offence Appeal\"");
			} else if (loginResponse == 12) {
				loginScreenPrint("IP banned.", "Please post a topic in \"Offence Appeal\"");
			} else if (loginResponse == 13) {
				loginScreenPrint("Client dimensions are too large.", "Please subscribe if you want a larger client.");
			} else if (loginResponse == 14) { // Database Service Offline..
				loginScreenPrint("Login Service Offline", "Please try again in a few mins");
			} else if (loginResponse == 15) {
				loginScreenPrint(Config.TOO_MANY_CHARS_IN_WILDERNESS_TEXT_A,
						Config.TOO_MANY_CHARS_IN_WILDERNESS_TEXT_B);
			} else {
				loginScreenPrint("Error unable to login.", "Unrecognised response code.");
			}
		} catch (Exception exception) {
			if (sessionID == 0) {
				loginScreenPrint("Sorry! Unable to connect.", "Check internet settings or try another world");
			} else {
				loginScreenPrint("Login Service Error", "Please try again in a few mins");
			}
		}
	}

	public void lostConnection() {
		resetIntVars();
	}

    public final void sendPingPacketReadPacketData() {
        long l = System.currentTimeMillis();
        if (streamClass.containsData())
            lastPing = l;
        if (l - lastPing > 5000) {
            lastPing = l;
            streamClass.createPacket(5);
            streamClass.formatPacket();
        }
        try {
            streamClass.writePacket(20);
        } catch (IOException _ex) {
            lostConnection();
            return;
        }
        int packetLength = streamClass.readPacket(packetData);
        if (packetLength > 0) {
            checkIncomingPacket(packetData[0] & 0xff, packetLength);
        }
    }

	public final void checkIncomingPacket(int command, int length) {
		// long affected;
		// int offset = 1;

		if (command == 48) {
			String s = new String(packetData, 1, length - 1);
			handleServerMessage(s);
		}
		if (command == 136) {
			cantLogout();
			return;
		}
		if (command == 235) {
			String mobName = DataConversions.hashToUsername(DataOperations.getUnsigned8Bytes(packetData, 1));
			int rank = packetData[9] & 0xFF;
			String message = new String(packetData, 10, length - 10);
			displayGlobalChat(mobName, rank, message);
		}
		if (command == 249) {
			friendsCount = DataOperations.getUnsignedByte(packetData[1]);
			for (int k = 0; k < friendsCount; k++) {
				friendsListLongs[k] = DataOperations.getUnsigned8Bytes(packetData, 2 + k * 9);
				friendsListOnlineStatus[k] = DataOperations.getUnsignedByte(packetData[10 + k * 9]);
			}

			reOrderFriendsListByOnlineStatus();
			return;
		}
		if (command == 25) {
			long friend = DataOperations.getUnsigned8Bytes(packetData, 1);
			int status = packetData[9] & 0xff;
			for (int i2 = 0; i2 < friendsCount; i2++)
				if (friendsListLongs[i2] == friend) {
					if (friendsListOnlineStatus[i2] == 0 && status != 0)
						displayGenericMessage("@pri@@cya@" + DataOperations.longToString(friend) + " has logged in", 6);
					if (friendsListOnlineStatus[i2] != 0 && status == 0)
						displayGenericMessage("@pri@@cya@" + DataOperations.longToString(friend) + " has logged out",
								6);
					friendsListOnlineStatus[i2] = status;
					length = 0;
					reOrderFriendsListByOnlineStatus();
					return;
				}

			friendsListLongs[friendsCount] = friend;
			friendsListOnlineStatus[friendsCount] = status;
			friendsCount++;
			reOrderFriendsListByOnlineStatus();
			return;
		}
		if (command == 2) {
			ignoreListCount = DataOperations.getUnsignedByte(packetData[1]);
			for (int i1 = 0; i1 < ignoreListCount; i1++) {
				ignoreListLongs[i1] = DataOperations.getUnsigned8Bytes(packetData, 2 + i1 * 8);
			}
			return;
		}
		if (command == 158) {
			blockChatMessages = ((packetData[1] & 1) != 0 ? true : false);
			blockPrivateMessages = ((packetData[1] & 2) != 0 ? true : false);
			blockTradeRequests = ((packetData[1] & 4) != 0 ? true : false);
			blockDuelRequests = ((packetData[1] & 8) != 0 ? true : false);
			blockGlobalMessages = ((packetData[1] & 16) != 0 ? true : false);
			return;
		}
		if (command == 170) {
			long mobUsernameHash = DataOperations.getUnsigned8Bytes(packetData, 1);
			int rank = DataOperations.readInt(packetData, 9);
			String message = DataConversions.byteToString(packetData, 13, length - 13);
			lastPrivateMessageSender = mobUsernameHash;
			displayPrivateMessage(mobUsernameHash, message, rank, false);
			return;
		} else if (command == 175) {
			long mobUsernameHash = DataOperations.getUnsigned8Bytes(packetData, 1);
			int rank = DataOperations.readInt(packetData, 9);
			String message = DataConversions.byteToString(packetData, 13, length - 13);
			lastPrivateMessageSender = mobUsernameHash;
			displayPrivateMessage(mobUsernameHash, message, rank, true);
			return;
		} else {
			handleIncomingPacket(command, length, packetData);
			return;
		}
	}

	public final void reOrderFriendsListByOnlineStatus() {
		boolean flag = true;
		while (flag) {
			flag = false;
			for (int i = 0; i < friendsCount - 1; i++)
				if (friendsListOnlineStatus[i] < friendsListOnlineStatus[i + 1]) {
					int j = friendsListOnlineStatus[i];
					friendsListOnlineStatus[i] = friendsListOnlineStatus[i + 1];
					friendsListOnlineStatus[i + 1] = j;
					long l = friendsListLongs[i];
					friendsListLongs[i] = friendsListLongs[i + 1];
					friendsListLongs[i + 1] = l;
					flag = true;
				}

		}
	}

	public final void addToIgnoreList(String s) {
		long l = DataOperations.stringLength12ToLong(s);

		for (int i = 0; i < friendsCount; i++) {
			if (l == friendsListLongs[i]) {
				handleServerMessage("Please remove " + s + " from your friends list first");
				return;
			}
		}

		for (int i = 0; i < ignoreListCount; i++) {
			if (l == ignoreListLongs[i]) {
				handleServerMessage(s + " is already on your ignore list");
				return;
			}
		}

		if (ignoreListCount >= 200) {
			handleServerMessage("Ignore list full");
			return;
		}

		streamClass.createPacket(46);
		streamClass.addLong(l);
		streamClass.formatPacket();
		for (int i = 0; i < ignoreListCount; i++)
			if (ignoreListLongs[i] == l)
				return;

		if (ignoreListCount >= ignoreListLongs.length - 1) {
			return;
		} else {
			ignoreListLongs[ignoreListCount++] = l;
			return;
		}
	}

	public final void removeFromIgnoreList(long l) {
		streamClass.createPacket(47);
		streamClass.addLong(l);
		streamClass.formatPacket();
		for (int i = 0; i < ignoreListCount; i++)
			if (ignoreListLongs[i] == l) {
				ignoreListCount--;
				for (int j = i; j < ignoreListCount; j++)
					ignoreListLongs[j] = ignoreListLongs[j + 1];

				return;
			}

	}

	public final void addToFriendsList(String s) {

		long l = DataOperations.stringLength12ToLong(s);

		for (int i = 0; i < ignoreListCount; i++) {
			if (l == ignoreListLongs[i]) {
				handleServerMessage("Please remove " + s + " from your ignore list first.");
				return;
			}
		}

		for (int i = 0; i < friendsCount; i++) {
			if (l == friendsListLongs[i]) {
				handleServerMessage(s + " is already on your friend list.");
				return;
			}
		}

		if (friendsCount >= 200) {
			handleServerMessage("Friend list full");
			return;
		}

		streamClass.createPacket(44);
		streamClass.addLong(l);
		streamClass.formatPacket();
		for (int i = 0; i < friendsCount; i++)
			if (friendsListLongs[i] == l)
				return;

		if (friendsCount >= friendsListLongs.length - 1) {
			return;
		} else {
			friendsListLongs[friendsCount] = l;
			friendsListOnlineStatus[friendsCount] = 0;
			friendsCount++;
			return;
		}
	}

	public final void removeFromFriends(long l) {
		streamClass.createPacket(45);
		streamClass.addLong(l);
		streamClass.formatPacket();
		for (int i = 0; i < friendsCount; i++) {
			if (friendsListLongs[i] != l)
				continue;
			friendsCount--;
			for (int j = i; j < friendsCount; j++) {
				friendsListLongs[j] = friendsListLongs[j + 1];
				friendsListOnlineStatus[j] = friendsListOnlineStatus[j + 1];
			}

			break;
		}
	}

	public final void sendPrivateMessage(long user, byte message[], int messageLength) {
		streamClass.createPacket(48);
		streamClass.addLong(user);
		streamClass.addBytes(message, 0, messageLength);
		streamClass.formatPacket();
	}

	public final void sendPrivateMessage(long user, String msg) {
		byte[] data = DataConversions.stringToByteArray(msg);
		sendPrivateMessage(user, data, data.length);
	}

	public final void sendChatMessage(byte abyte0[], int i) {
		streamClass.createPacket(9);
		streamClass.addBytes(abyte0, 0, i);
		streamClass.formatPacket();
	}

	public void sendChatString(String s) {
		streamClass.createPacket(12);
		streamClass.addString(s);
		streamClass.formatPacket();
	}

	public final void sendPartyChat(String message) {
		streamClass.createPacket(84);
		streamClass.addByte(5);
		streamClass.addString(message);
		streamClass.formatPacket();
	}

	public final void createAuction(int id, long amount, long price) {
		streamClass.createPacket(87);
		streamClass.add2ByteInt(id);
		streamClass.addLong(amount);
		streamClass.addLong(price);
		streamClass.formatPacket();
	}

	public final void requestAuctionHouse() {
		streamClass.createPacket(88);
		streamClass.formatPacket();
	}

	public final void closeAuctionHouse() {
		streamClass.createPacket(89);
		streamClass.formatPacket();
	}

	public final void buyFromAuctionHouse(int index, int itemID, long itemPrice, long amount) {
		streamClass.createPacket(90);
		streamClass.add2ByteInt(index);
		streamClass.add4ByteInt(itemID);
		streamClass.addLong(itemPrice);
		streamClass.addLong(amount);
		streamClass.formatPacket();
	}

	public final void cancelAuction(int index) {
		streamClass.createPacket(91);
		streamClass.add2ByteInt(index);
		streamClass.formatPacket();
	}

	public abstract void loginScreenPrint(String s, String s1);

	public abstract void resetVars();

	public abstract void resetIntVars();

	public abstract void cantLogout();

	public abstract void handleIncomingPacket(int command, int length, byte[] abyte0);

	public abstract void handleServerMessage(String s);

	public abstract void displayPrivateMessage(long mobUsernameHash, String message, int rank, boolean sent);

	public abstract void displayNpcMessage(String npcMessage);

	public abstract void displayGenericMessage(String message, int chatTab);

	public abstract void displayGlobalChat(String mobName, int rank, String message);

	public GameWindowMiddleMan(Delegate_T c) {
		super(c);
		username = "";
		password = "";
		packetData = new byte[25000];
		friendsListLongs = new long[400];
		friendsListOnlineStatus = new int[400];
		ignoreListLongs = new long[200];
	}

	public Long lastPrivateMessageSender = null;

	public static int maxPacketReadCount;
	public byte[] pingpacket = { 82, 83, 67, 69 };
	String username;
	String password;
	public StreamClass streamClass;
	public byte[] packetData;
	int reconnectTries;
	long lastPing;
	public long ping;
	long lastPinging;
	public int friendsCount;
	public long[] friendsListLongs;
	public int[] friendsListOnlineStatus;
	public int ignoreListCount;
	public long[] ignoreListLongs;
	public boolean blockChatMessages;
	public boolean blockGlobalMessages;
	public boolean blockPrivateMessages;
	public boolean blockTradeRequests;
	public boolean blockDuelRequests;
	public boolean blockServerAnnouncements;
	public boolean blockPartyInvitations = false;
	public static BigInteger key = new BigInteger(
			"1370158896620336158431733257575682136836100155721926632321599369132092701295540721504104229217666225601026879393318399391095704223500673696914052239029335");
	public static BigInteger modulus = new BigInteger(
			"1549611057746979844352781944553705273443228154042066840514290174539588436243191882510185738846985723357723362764835928526260868977814405651690121789896823");
	public int socketTimeout;
	public int gW;
	public int gH;
}
