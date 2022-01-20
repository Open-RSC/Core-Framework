package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.database.impl.mysql.queries.logging.SecurityChangeLog;
import com.openrsc.server.database.impl.mysql.queries.logging.SecurityChangeLog.ChangeEvent;
import com.openrsc.server.database.struct.PlayerContactDetails;
import com.openrsc.server.database.struct.PlayerRecoveryQuestions;
import com.openrsc.server.login.PasswordChangeRequest;
import com.openrsc.server.login.RecoveryChangeRequest;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.SecuritySettingsStruct;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SecuritySettingsHandler implements PayloadProcessor<SecuritySettingsStruct, OpcodeIn> {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void process(SecuritySettingsStruct payload, Player player) throws Exception {
		switch (payload.getOpcode()) {
		case CHANGE_PASS: //change pass
			LOGGER.info("Change password request from: " + player.getCurrentIP());
			player.getWorld().getServer().getPacketFilter().shouldAllowLogin(player.getCurrentIP(), true);

			if (!player.isUsingCustomClient()) {
				if (payload.passwords != null) {
					PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(player.getWorld().getServer(), player.getChannel(), player, payload.passwords[0], payload.passwords[1]);
					player.getWorld().getServer().getLoginExecutor().add(passwordChangeRequest);
				}
				break;
			} else {
				PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(player.getWorld().getServer(), player.getChannel(), player, payload.passwords[0], payload.passwords[1]);
				player.getWorld().getServer().getLoginExecutor().add(passwordChangeRequest);
				break;
			}
		case CANCEL_RECOVERY_REQUEST: //cancel recovery request
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
		case CHANGE_RECOVERY_REQUEST: //send recovery questions screen
			// Should help with ISAAC desync
			if (System.currentTimeMillis() - player.getCurrentLogin() <= 2000) {
				return;
			}
			PlayerRecoveryQuestions recoveryQuestions = player.getWorld().getServer().getDatabase().getPlayerChangeRecoveryData(player.getID());
			if (recoveryQuestions == null || DataConversions.getDaysSinceTime(recoveryQuestions.dateSet) >= 14) {
				//no pending recovery questions change or wait time past, allow
				ActionSender.sendRecoveryScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have pending recovery questions to get applied");
			}

			break;
		case CHANGE_DETAILS_REQUEST: //send contact details screen
			PlayerContactDetails contactDetails = player.getWorld().getServer().getDatabase().getContactDetails(player.getID());
			if (contactDetails == null || DataConversions.getDaysSinceTime(contactDetails.dateModified) >= 1) {
				//details not set or wait time past, allow
				ActionSender.sendDetailsScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have modified your details within 24 hours");
			}

			break;
		case SET_RECOVERY: //change/set recovery questions
			if (!player.isChangingRecovery()) {
				player.setSuspiciousPlayer(true, "player recovery questions packet without changing recovery");
				return;
			}
			player.setChangingRecovery(false);
			LOGGER.info("Change recovery questions request from: " + player.getCurrentIP());
			player.getWorld().getServer().getPacketFilter().shouldAllowLogin(player.getCurrentIP(), true);

			if (!player.isUsingCustomClient()) {
				if (payload.questions != null && payload.answers != null) {
					RecoveryChangeRequest recoveryChangeRequest = new RecoveryChangeRequest(player.getWorld().getServer(), player.getChannel(), player, payload.questions, payload.answers);
					player.getWorld().getServer().getLoginExecutor().add(recoveryChangeRequest);
				}
				break;
			} else {
				RecoveryChangeRequest recoveryChangeRequest = new RecoveryChangeRequest(player.getWorld().getServer(), player.getChannel(), player, payload.questions, payload.answers);
				player.getWorld().getServer().getLoginExecutor().add(recoveryChangeRequest);
				break;
			}
		case SET_DETAILS: //change/set contact details
			if (!player.isChangingDetails()) {
				player.setSuspiciousPlayer(true, "player contact details packet without change details");
				return;
			}
			player.setChangingDetails(false);
			LOGGER.info("Change contact details request from: " + player.getCurrentIP());
			String fullName, zipCode, country, email;

			if (payload.details == null) {
				LOGGER.info(player.getCurrentIP() + " - Set contact details failed: error receiving correctly contact details");
				return;
			}

			fullName = payload.details[0];
			zipCode = payload.details[1];
			country = payload.details[2];
			email = DataConversions.maxLenString(payload.details[3], 255, false);

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
