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
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SecuritySettingsHandler implements PacketHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void handlePacket(Packet packet, Player player) throws Exception {
		switch (packet.getID()) {
		case 25: //change pass
			player.getWorld().getServer().getPacketFilter().shouldAllowLogin(player.getCurrentIP(), true);

			String oldPass = packet.readString().trim();
			String newPass = packet.readString().trim();

			PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(player.getWorld().getServer(), player.getChannel(), player, oldPass, newPass);
			player.getWorld().getServer().getLoginExecutor().add(passwordChangeRequest);

			break;
		case 196: //cancel recovery request
			LOGGER.info("Cancel recovery request from: " + player.getCurrentIP());
			int playerID = player.getDatabaseID();
			if (playerID == -1) {
				LOGGER.info(player.getCurrentIP() + " - Cancel recovery failed: Could not find player info in database.");
				return;
			}
			player.getWorld().getServer().getDatabase().cancelRecoveryChangeRequest(playerID);
			player.getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(player, ChangeEvent.RECOVERY_QUESTIONS_CHANGE, "Player canceled pending request"));
			ActionSender.sendMessage(player, "You no longer have pending recovery question changes.");
			LOGGER.info(player.getCurrentIP() + " - Cancel recovery request successful");

			break;
		case 200: //send recovery questions screen
			PlayerRecoveryQuestions recoveryQuestions = player.getWorld().getServer().getDatabase().getPlayerChangeRecoveryData(player.getID());
			if (recoveryQuestions == null || DataConversions.getDaysSinceTime(recoveryQuestions.dateSet) >= 14) {
				//no pending recovery questions change or wait time past, allow
				ActionSender.sendRecoveryScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have pending recovery questions to get applied");
			}

			break;
		case 201: //send contact details screen
			PlayerContactDetails contactDetails = player.getWorld().getServer().getDatabase().getContactDetails(player.getID());
			if (contactDetails == null || DataConversions.getDaysSinceTime(contactDetails.dateModified) >= 1) {
				//details not set or wait time past, allow
				ActionSender.sendDetailsScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have modified your details within 24 hours");
			}

			break;
		case 208: //change/set recovery questions
			player.getWorld().getServer().getPacketFilter().shouldAllowLogin(player.getCurrentIP(), true);

			String questions[] = new String[5];
			String answers[] = new String[5];
			for (int i=0; i<5; i++) {
				questions[i] = packet.readString().trim();
				answers[i] = DataConversions.normalize(packet.readString(), 50);
			}

			RecoveryChangeRequest recoveryChangeRequest = new RecoveryChangeRequest(player.getWorld().getServer(), player.getChannel(), player, questions, answers);
			player.getWorld().getServer().getLoginExecutor().add(recoveryChangeRequest);
			break;
		case 253: //change/set contact details
			LOGGER.info("Contact details change from: " + player.getCurrentIP());
			String fullName, zipCode, country, email;
			fullName = packet.readString();
			zipCode = packet.readString();
			country = packet.readString();
			email = DataConversions.maxLenString(packet.readString(), 255, false);

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
