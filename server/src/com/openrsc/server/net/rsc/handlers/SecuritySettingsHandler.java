package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.login.RecoveryChangeRequest;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.sql.query.logs.SecurityChangeLog;
import com.openrsc.server.sql.query.logs.SecurityChangeLog.ChangeEvent;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SecuritySettingsHandler implements PacketHandler {
	
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void handlePacket(Packet p, Player player) throws Exception {
		PreparedStatement statement;
		ResultSet result;
		
		switch (p.getID()) {
		case 25: //change pass
			LOGGER.info("Password change attempt from: " + player.getCurrentIP());
			String oldPass = p.readString().trim();
			String newPass = p.readString().trim();
			statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT id, pass, salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE username=?");
			statement.setString(1, player.getUsername());
			result = statement.executeQuery();
			if (!result.next()) {
				LOGGER.info(player.getCurrentIP() + " - Pass change failed: Could not find player info in database.");
				return;
			}
			String lastDBPass = result.getString("pass");
			String DBsalt = result.getString("salt");
			String newDBPass;
			int playerID = result.getInt("id");
			if (!DataConversions.checkPassword(oldPass, DBsalt, lastDBPass)) {
				LOGGER.info(player.getCurrentIP() + " - Pass change failed: The current password did not match players record.");
				ActionSender.sendMessage(player, "No changes made, your current password did not match");
				return;
			}
			newDBPass = DataConversions.hashPassword(newPass, DBsalt);
			
			statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(
					"UPDATE `" + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players` SET `pass`=? WHERE `id`=?");
			statement.setString(1, newDBPass);
			statement.setInt(2, playerID);
			statement.executeUpdate();
			
			statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT previous_pass FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery WHERE playerID=?");
			statement.setInt(1, playerID);
			result = statement.executeQuery();
			String lastPw, earlierPw;
			if (result.next()) {
				//move passwords one step down and update table
				try {
					earlierPw = result.getString("previous_pass");
				} catch(Exception e) {
					earlierPw = "";
				}
				lastPw = lastDBPass;
				
				statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(
						"UPDATE `" + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery` SET `previous_pass`=?, `earlier_pass`=? WHERE `playerID`=?");
				statement.setString(1, lastPw);
				statement.setString(2, earlierPw);
				statement.setInt(3, playerID);
				statement.executeUpdate();
			}

			player.getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(player, ChangeEvent.PASSWORD_CHANGE,
					"From: " + lastDBPass + ", To: " + newDBPass));
			ActionSender.sendMessage(player, "Your password was successfully changed!");
			LOGGER.info(player.getCurrentIP() + " - Password change successful");
			
			break;
		case 196: //cancel recovery request
			LOGGER.info("Cancel recovery request from: " + player.getCurrentIP());
			playerID = player.getDatabaseID();
			if (playerID == -1) {
				LOGGER.info(player.getCurrentIP() + " - Cancel recovery failed: Could not find player info in database.");
				return;
			}
			statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(
					"DELETE FROM `" + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_change_recovery` WHERE `playerID`=?");
			statement.setInt(1, playerID);
			statement.executeUpdate();
			player.getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(player, ChangeEvent.RECOVERY_QUESTIONS_CHANGE, "Player canceled pending request"));
			ActionSender.sendMessage(player, "You no longer have pending recovery question changes.");
			LOGGER.info(player.getCurrentIP() + " - Cancel recovery request successful");
			
			break;
		case 200: //send recovery questions screen
			statement =player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT playerID, date_set FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_change_recovery WHERE username=?");
			statement.setString(1, player.getUsername());
			result = statement.executeQuery();
			if (!result.next() || DataConversions.getDaysSinceTime(result.getLong("date_set")) >= 14) {
				//no pending recovery questions change or wait time past, allow
				ActionSender.sendRecoveryScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have pending recovery questions to get applied");
			}
			
			break;
		case 201: //send contact details screen
			statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT playerID, date_modified FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_contact_details WHERE username=?");
			statement.setString(1, player.getUsername());
			result = statement.executeQuery();
			if (!result.next() || DataConversions.getDaysSinceTime(result.getLong("date_modified")) >= 1) {
				//details not set or wait time past, allow
				ActionSender.sendDetailsScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have modified your details within 24 hours");
			}
			
			break;
		case 208: //change/set recovery questions
			String questions[] = new String[5];
			String answers[] = new String[5];
			for (int i=0; i<5; i++) {
				questions[i] = p.readString().trim();
				answers[i] = DataConversions.normalize(p.readString(), 50);
			}

			RecoveryChangeRequest recoveryChangeRequest = new RecoveryChangeRequest(player.getWorld().getServer(), player, questions, answers);
			player.getWorld().getServer().getPlayerDataProcessor().addRecoveryChangeRequest(recoveryChangeRequest);
			break;
		case 253: //change/set contact details
			LOGGER.info("Contact details change from: " + player.getCurrentIP());
			String fullName, zipCode, country, email;
			fullName = p.readString();
			zipCode = p.readString();
			country = p.readString();
			email = DataConversions.maxLenString(p.readString(), 255, false);
			
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
			
			statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT fullname, zipCode, country, email FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_contact_details WHERE playerID=?");
			statement.setInt(1, playerID);
			result = statement.executeQuery();
			boolean isFirstSet = !result.next();
			
			PreparedStatement innerStatement;
			if (isFirstSet) {
				innerStatement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(
						"INSERT INTO `" + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_contact_details` (`playerID`, `username`, `fullname`, `zipCode`, `country`, `email`, `date_modified`, `ip`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				innerStatement.setInt(1, playerID);
				innerStatement.setString(2, player.getUsername());
				innerStatement.setString(3, DataConversions.maxLenString(fullName, 100, true));
				innerStatement.setString(4, DataConversions.maxLenString(zipCode, 10, true));
				innerStatement.setString(5, DataConversions.maxLenString(country, 100, true));
				innerStatement.setString(6, email.trim());
				innerStatement.setLong(7, System.currentTimeMillis() / 1000);
				innerStatement.setString(8, player.getCurrentIP());
				innerStatement.executeUpdate();
				ActionSender.sendMessage(player, "Your contact details were successfully set!");
			} else {
				fullName = DataConversions.updateIfEmpty(fullName, result.getString("fullname"));
				zipCode = DataConversions.updateIfEmpty(zipCode, result.getString("zipCode"));
				country = DataConversions.updateIfEmpty(country, result.getString("country"));
				email = DataConversions.updateIfEmpty(email, result.getString("email"));
				
				innerStatement = player.getWorld().getServer().getDatabaseConnection().prepareStatement(
						"UPDATE `" + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_contact_details`" +
						"SET `fullname`=?, `zipCode`=?, `country`=?, `email`=?, `date_modified`=?, `ip`=? WHERE `playerID`=?");
				innerStatement.setString(1, DataConversions.maxLenString(fullName, 100, true));
				innerStatement.setString(2, DataConversions.maxLenString(zipCode, 10, true));
				innerStatement.setString(3, DataConversions.maxLenString(country, 100, true));
				innerStatement.setString(4, email.trim());
				innerStatement.setLong(5, System.currentTimeMillis() / 1000);
				innerStatement.setString(6, player.getCurrentIP());
				innerStatement.setInt(7, playerID);
				innerStatement.executeUpdate();
				ActionSender.sendMessage(player, "Your contact details were successfully updated!");
			}
			player.getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(player, ChangeEvent.CONTACT_DETAILS_CHANGE));
			LOGGER.info(player.getCurrentIP() + " - Contact details change successful");
			break;
		}
	}
}
