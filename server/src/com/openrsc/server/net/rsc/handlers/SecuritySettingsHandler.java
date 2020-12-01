package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.database.impl.mysql.queries.logging.SecurityChangeLog;
import com.openrsc.server.database.impl.mysql.queries.logging.SecurityChangeLog.ChangeEvent;
import com.openrsc.server.database.struct.PlayerContactDetails;
import com.openrsc.server.database.struct.PlayerRecoveryQuestions;
import com.openrsc.server.login.PasswordChangeRequest;
import com.openrsc.server.login.RecoveryChangeRequest;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.Crypto;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SecuritySettingsHandler implements PacketHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void handlePacket(Packet packet, Player player) throws Exception {
		switch (packet.getID()) {
		case 25: //change pass
			LOGGER.info("Change password request from: " + player.getCurrentIP());
			player.getWorld().getServer().getPacketFilter().shouldAllowLogin(player.getCurrentIP(), true);

			if (player.isUsingAuthenticClient()) {
				// Get encrypted block
				// old + new password is always 40 characters long, with spaces at the end.
				// each blocks having encrypted 7 chars of password
				int blockLen;
				byte[] decBlock; // current decrypted block
				int session = -1; // TODO: should be players stored TCP session to check if request should be processed
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
					LOGGER.info("error parsing passwords in change password block");
					errored = true;
					e.printStackTrace();
				}

				if (!errored) {
					PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(player.getWorld().getServer(), player.getChannel(), player, oldPassword, newPassword);
					player.getWorld().getServer().getLoginExecutor().add(passwordChangeRequest);
				}
				break;
			} else {
				String oldPass = packet.readString().trim();
				String newPass = packet.readString().trim();

				PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(player.getWorld().getServer(), player.getChannel(), player, oldPass, newPass);
				player.getWorld().getServer().getLoginExecutor().add(passwordChangeRequest);
				break;
			}
		case 196: //cancel recovery request
			LOGGER.info("Cancel recovery request from: " + player.getCurrentIP());
			int playerID = player.getDatabaseID();
			if (playerID == -1) {
				LOGGER.info(player.getCurrentIP() + " - Cancel recovery failed: Could not find player info in database.");
				return;
			}
			PlayerRecoveryQuestions recoveryData = player.getWorld().getServer().getDatabase().getPlayerChangeRecoveryData(player.getID());
			if (recoveryData == null || DataConversions.getDaysSinceTime(recoveryData.dateSet) >= 14) {
				LOGGER.info(player.getCurrentIP() + " - Cancel recovery failed: Recovery questions not set or they are not recent");
				return;
			}
			player.getWorld().getServer().getDatabase().cancelRecoveryChangeRequest(playerID);
			player.getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(player, ChangeEvent.RECOVERY_QUESTIONS_CHANGE, "Player canceled pending request"));
			ActionSender.sendMessage(player, "You no longer have pending recovery question changes.");
			LOGGER.info(player.getCurrentIP() + " - Cancel recovery request successful");

			break;
		case 197: //send recovery questions screen
			PlayerRecoveryQuestions recoveryQuestions = player.getWorld().getServer().getDatabase().getPlayerChangeRecoveryData(player.getID());
			if (recoveryQuestions == null || DataConversions.getDaysSinceTime(recoveryQuestions.dateSet) >= 14) {
				//no pending recovery questions change or wait time past, allow
				ActionSender.sendRecoveryScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have pending recovery questions to get applied");
			}

			break;
		case 247: //send contact details screen
			PlayerContactDetails contactDetails = player.getWorld().getServer().getDatabase().getContactDetails(player.getID());
			if (contactDetails == null || DataConversions.getDaysSinceTime(contactDetails.dateModified) >= 1) {
				//details not set or wait time past, allow
				ActionSender.sendDetailsScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have modified your details within 24 hours");
			}

			break;
		case 208: //change/set recovery questions
			if (!player.isChangingRecovery()) {
				player.setSuspiciousPlayer(true, "player recovery questions packet without changing recovery");
				return;
			}
			player.setChangingRecovery(false);
			LOGGER.info("Change recovery questions request from: " + player.getCurrentIP());
			player.getWorld().getServer().getPacketFilter().shouldAllowLogin(player.getCurrentIP(), true);

			if (player.isUsingAuthenticClient()) {
				// Get the 5 recovery answers
				int blockLen;
				byte[] decBlock; // current decrypted block
				int session = -1; // TODO: should be players stored TCP session to check if request should be processed
				int receivedSession;
				boolean errored = false;
				int questLen = 0;
				int answerLen = 0;
				int expBlocks = 0;
				byte[] answerData;
				String questions[] = new String[5];
				String answers[] = new String[5];
				for (int i = 0; i < 5; i++) {
					questLen = packet.readUnsignedByte();
					questions[i] = new String(packet.readBytes(questLen));
					answerLen = packet.readUnsignedByte();
					// Get encrypted block for answers
					expBlocks = (int)Math.ceil(answerLen / 7.0);
					answerData = new byte[expBlocks * 7];
					for (int j = 0; j < expBlocks; j++) {
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
							System.arraycopy(decBlock, 8, answerData, j * 7, 7);
						}
					}

					try {
						answers[i] = new String(answerData, "UTF8").trim();
					} catch (Exception e) {
						LOGGER.info("error parsing answer " + i + " in change recovery block");
						errored = true;
						e.printStackTrace();
					}
				}

				if (!errored) {
					RecoveryChangeRequest recoveryChangeRequest = new RecoveryChangeRequest(player.getWorld().getServer(), player.getChannel(), player, questions, answers);
					player.getWorld().getServer().getLoginExecutor().add(recoveryChangeRequest);
				}
				break;
			} else {
				String questions[] = new String[5];
				String answers[] = new String[5];
				for (int i=0; i<5; i++) {
					questions[i] = packet.readString().trim();
					answers[i] = DataConversions.normalize(packet.readString(), 50);
				}

				RecoveryChangeRequest recoveryChangeRequest = new RecoveryChangeRequest(player.getWorld().getServer(), player.getChannel(), player, questions, answers);
				player.getWorld().getServer().getLoginExecutor().add(recoveryChangeRequest);
				break;
			}
		case 253: //change/set contact details
			if (!player.isChangingDetails()) {
				player.setSuspiciousPlayer(true, "player contact details packet without change details");
				return;
			}
			player.setChangingDetails(false);
			LOGGER.info("Change contact details request from: " + player.getCurrentIP());
			String fullName, zipCode, country, email;
			boolean errored = false;
			int expLen = 0;
			String details[] = new String[4];

			if (player.isUsingAuthenticClient()) {
				for (int i = 0; i < 4; i++) {
					expLen = packet.readUnsignedByte();
					details[i] = new String(packet.readBytes(expLen));
					if (details[i].length() != expLen) errored = true;
				}
				fullName = details[0];
				zipCode = details[1];
				country = details[2];
				email = DataConversions.maxLenString(details[3], 255, false);
			} else {
				fullName = packet.readString();
				zipCode = packet.readString();
				country = packet.readString();
				email = DataConversions.maxLenString(packet.readString(), 255, false);
			}

			if (errored) {
				LOGGER.info(player.getCurrentIP() + " - Set contact details failed: error receiving correctly contact details");
				return;
			}

			playerID = player.getDatabaseID();
			if (playerID == -1) {
				LOGGER.info(player.getCurrentIP() + " - Set contact details failed: Could not find player info in database.");
				return;
			}

			if (!email.trim().equals("") && !DataConversions.isValidEmailAddress(email.trim())) {
				ActionSender.sendMessage(player, "Could not set details, invalid email!");
				LOGGER.info(player.getCurrentIP() + " - Set contact details failed: invalid email supplied.");
				return;
			}

			contactDetails = player.getWorld().getServer().getDatabase().getContactDetails(playerID);
			if (contactDetails == null) {
				PlayerContactDetails newContactDetails = new PlayerContactDetails();
				newContactDetails.username = player.getUsername();
				newContactDetails.fullName = DataConversions.maxLenString(fullName, 100, true);
				newContactDetails.zipCode = DataConversions.maxLenString(zipCode, 10, true);
				newContactDetails.country = DataConversions.maxLenString(country, 100, true);
				newContactDetails.email = email.trim();
				newContactDetails.dateModified = System.currentTimeMillis() / 1000;
				newContactDetails.ip = player.getCurrentIP();

				player.getWorld().getServer().getDatabase().newContactDetails(playerID, newContactDetails);

				ActionSender.sendMessage(player, "Your contact details were successfully set!");
			} else {
				fullName = DataConversions.updateIfEmpty(fullName, contactDetails.fullName);
				zipCode = DataConversions.updateIfEmpty(zipCode, contactDetails.zipCode);
				country = DataConversions.updateIfEmpty(country, contactDetails.country);
				email = DataConversions.updateIfEmpty(email, contactDetails.email);

				PlayerContactDetails newContactDetails = new PlayerContactDetails();
				newContactDetails.username = player.getUsername();
				newContactDetails.fullName = DataConversions.maxLenString(fullName, 100, true);
				newContactDetails.zipCode = DataConversions.maxLenString(zipCode, 10, true);
				newContactDetails.country = DataConversions.maxLenString(country, 100, true);
				newContactDetails.email = email.trim();
				newContactDetails.dateModified = System.currentTimeMillis() / 1000;
				newContactDetails.ip = player.getCurrentIP();

				player.getWorld().getServer().getDatabase().updateContactDetails(playerID, newContactDetails);

				ActionSender.sendMessage(player, "Your contact details were successfully updated!");
			}
			player.getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(player, ChangeEvent.CONTACT_DETAILS_CHANGE));
			LOGGER.info(player.getCurrentIP() + " - Contact details change successful");

			break;
		}
	}
}
