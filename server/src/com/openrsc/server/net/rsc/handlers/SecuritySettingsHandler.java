package com.openrsc.server.net.rsc.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.SecurityChangeLog;
import com.openrsc.server.sql.query.logs.SecurityChangeLog.ChangeEvent;
import com.openrsc.server.util.rsc.DataConversions;

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
			statement = DatabaseConnection.getDatabase().prepareStatement("SELECT id, pass, salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE username=?");
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
			if (!DataConversions.hashPassword(oldPass, DBsalt).equals(lastDBPass)) {
				LOGGER.info(player.getCurrentIP() + " - Pass change failed: The current password did not match players record.");
				ActionSender.sendMessage(player, "No changes made, your current password did not match");
				return;
			}
			newDBPass = DataConversions.hashPassword(newPass, DBsalt);
			
			statement = DatabaseConnection.getDatabase().prepareStatement(
					"UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` SET `pass`=? WHERE `id`=?");
			statement.setString(1, newDBPass);
			statement.setInt(2, playerID);
			statement.executeUpdate();
			
			statement = DatabaseConnection.getDatabase().prepareStatement("SELECT previous_pass FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_recovery WHERE playerID=?");
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
				
				statement = DatabaseConnection.getDatabase().prepareStatement(
						"UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_recovery` SET `previous_pass`=?, `earlier_pass`=? WHERE `playerID`=?");
				statement.setString(1, lastPw);
				statement.setString(2, earlierPw);
				statement.setInt(3, playerID);
				statement.executeUpdate();
			}
			
			GameLogging.addQuery(new SecurityChangeLog(player, ChangeEvent.PASSWORD_CHANGE,
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
			statement = DatabaseConnection.getDatabase().prepareStatement(
					"DELETE FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_change_recovery` WHERE `playerID`=?");
			statement.setInt(1, playerID);
			statement.executeUpdate();
			GameLogging.addQuery(new SecurityChangeLog(player, ChangeEvent.RECOVERY_QUESTIONS_CHANGE, "Player canceled pending request"));
			ActionSender.sendMessage(player, "You no longer have pending recovery question changes.");
			LOGGER.info(player.getCurrentIP() + " - Cancel recovery request successful");
			
			break;
		case 200: //send recovery questions screen
			statement = DatabaseConnection.getDatabase().prepareStatement("SELECT playerID, date_set FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_change_recovery WHERE username=?");
			statement.setString(1, player.getUsername());
			result = statement.executeQuery();
			if (!result.next() || getDaysSinceTime(result.getLong("date_set")) >= 14) {
				//no pending recovery questions change or wait time past, allow
				ActionSender.sendRecoveryScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have pending recovery questions to get applied");
			}
			
			break;
		case 201: //send contact details screen
			statement = DatabaseConnection.getDatabase().prepareStatement("SELECT playerID, date_modified FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_contact_details WHERE username=?");
			statement.setString(1, player.getUsername());
			result = statement.executeQuery();
			if (!result.next() || getDaysSinceTime(result.getLong("date_modified")) >= 1) {
				//details not set or wait time past, allow
				ActionSender.sendDetailsScreen(player);
			} else {
				ActionSender.sendMessage(player, "You have modified your details within 24 hours");
			}
			
			break;
		case 208: //change/set recovery questions
			LOGGER.info("Recovery questions change from: " + player.getCurrentIP());
			boolean containsAllInfo = true;
			String questions[] = new String[5];
			String answers[] = new String[5];
			for (int i=0; i<5; i++) {
				questions[i] = p.readString().trim();
				answers[i] = normalize(p.readString(), 50);
				if (questions[i] == null || questions[i].trim().equals("")
						|| answers[i] == null || answers[i].trim().equals("")) {
					containsAllInfo = false;
				}
			}
			
			playerID = player.getDatabaseID();
			if (playerID == -1) {
				LOGGER.info(player.getCurrentIP() + " - Set recovery questions failed: Could not find player info in database.");
				return;
			}
			statement = DatabaseConnection.getDatabase().prepareStatement("SELECT 1 FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_recovery WHERE playerID=?");
			statement.setInt(1, playerID);
			result = statement.executeQuery();
			String table_suffix;
			if (!result.next()) {
				//player has not set recovery questions
				table_suffix = "player_recovery";
			} else {
				statement = DatabaseConnection.getDatabase().prepareStatement("SELECT date_set FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_change_recovery WHERE playerID=?");
				statement.setInt(1, playerID);
				result = statement.executeQuery();
				if (!result.next() || getDaysSinceTime(result.getLong("date_set")) >= 14) {
					table_suffix = "player_change_recovery";
				} else {
					ActionSender.sendMessage(player, "You have pending recovery questions to get applied");
					LOGGER.info(player.getCurrentIP() + " - Set recovery questions failed: There is a pending request to be applied.");
					return;
				}
			}
			
			if (!containsAllInfo) {
				ActionSender.sendMessage(player, "Could not set recovery questions, one or more fields empty");
				LOGGER.info(player.getCurrentIP() + " - Set recovery questions failed: One or more fields are empty.");
				return;
			}
			
			statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE id=?");
			statement.setInt(1, playerID);
			result = statement.executeQuery();
			result.next();
			String salt = result.getString("salt");
			for (int i=0; i<5; i++) {
				questions[i] = maxLenString(questions[i], 50, true);
				answers[i] = DataConversions.hashPassword(answers[i], salt);
			}
			
			statement = DatabaseConnection.getDatabase().prepareStatement(
					"INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + table_suffix + "` (`playerID`, `username`, `question1`, `answer1`, `question2`, `answer2`, `question3`, `answer3`, `question4`, `answer4`, `question5`, `answer5`, `date_set`, `ip_set`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setInt(1, playerID);
			statement.setString(2, player.getUsername());
			statement.setString(3, questions[0]);
			statement.setString(4, answers[0]);
			statement.setString(5, questions[1]);
			statement.setString(6, answers[1]);
			statement.setString(7, questions[2]);
			statement.setString(8, answers[2]);
			statement.setString(9, questions[3]);
			statement.setString(10, answers[3]);
			statement.setString(11, questions[4]);
			statement.setString(12, answers[4]);
			statement.setLong(13, System.currentTimeMillis() / 1000);
			statement.setString(14, player.getCurrentIP());
			statement.executeUpdate();
			
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<5; i++) {
				sb.append("(").append(questions[i]).append(",").append(answers[i]).append("), ");
			}
			
			GameLogging.addQuery(new SecurityChangeLog(player, ChangeEvent.RECOVERY_QUESTIONS_CHANGE,
					"Added questions/answers {" + sb.toString() + "}"));
			if (table_suffix.equals("player_recovery")) {
				ActionSender.sendMessage(player, "Recovery questions set successfully!");
			} else {
				ActionSender.sendMessage(player, "Your request to change recovery has been submitted");
			}
			LOGGER.info(player.getCurrentIP() + " - Recovery questions change successful");
			
			break;
		case 253: //change/set contact details
			LOGGER.info("Contact details change from: " + player.getCurrentIP());
			String fullName, zipCode, country, email;
			fullName = p.readString();
			zipCode = p.readString();
			country = p.readString();
			email = maxLenString(p.readString(), 255, false);
			
			playerID = player.getDatabaseID();
			if (playerID == -1) {
				LOGGER.info(player.getCurrentIP() + " - Set contact details failed: Could not find player info in database.");
				return;
			}
			
			if (!email.trim().equals("") && !isValidEmailAddress(email.trim())) {
				ActionSender.sendMessage(player, "Could not set details, invalid email!");
				LOGGER.info(player.getCurrentIP() + " - Set contact details failed: invalid email supplied.");
				return;
			}
			
			statement = DatabaseConnection.getDatabase().prepareStatement("SELECT fullname, zipCode, country, email FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_contact_details WHERE playerID=?");
			statement.setInt(1, playerID);
			result = statement.executeQuery();
			boolean isFirstSet = !result.next();
			
			PreparedStatement innerStatement;
			if (isFirstSet) {
				innerStatement = DatabaseConnection.getDatabase().prepareStatement(
						"INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_contact_details` (`playerID`, `username`, `fullname`, `zipCode`, `country`, `email`, `date_modified`, `ip`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				innerStatement.setInt(1, playerID);
				innerStatement.setString(2, player.getUsername());
				innerStatement.setString(3, maxLenString(fullName, 100, true));
				innerStatement.setString(4, maxLenString(zipCode, 10, true));
				innerStatement.setString(5, maxLenString(country, 100, true));
				innerStatement.setString(6, email.trim());
				innerStatement.setLong(7, System.currentTimeMillis() / 1000);
				innerStatement.setString(8, player.getCurrentIP());
				innerStatement.executeUpdate();
				ActionSender.sendMessage(player, "Your contact details were successfully set!");
			} else {
				fullName = updateIfEmpty(fullName, result.getString("fullname"));
				zipCode = updateIfEmpty(zipCode, result.getString("zipCode"));
				country = updateIfEmpty(country, result.getString("country"));
				email = updateIfEmpty(email, result.getString("email"));
				
				innerStatement = DatabaseConnection.getDatabase().prepareStatement(
						"UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "player_contact_details`" +
						"SET `fullname`=?, `zipCode`=?, `country`=?, `email`=?, `date_modified`=?, `ip`=? WHERE `playerID`=?");
				innerStatement.setString(1, maxLenString(fullName, 100, true));
				innerStatement.setString(2, maxLenString(zipCode, 10, true));
				innerStatement.setString(3, maxLenString(country, 100, true));
				innerStatement.setString(4, email.trim());
				innerStatement.setLong(5, System.currentTimeMillis() / 1000);
				innerStatement.setString(6, player.getCurrentIP());
				innerStatement.setInt(7, playerID);
				innerStatement.executeUpdate();
				ActionSender.sendMessage(player, "Your contact details were successfully updated!");
			}
			GameLogging.addQuery(new SecurityChangeLog(player, ChangeEvent.CONTACT_DETAILS_CHANGE));
			LOGGER.info(player.getCurrentIP() + " - Contact details change successful");
			break;
		}
	}
	
	private String updateIfEmpty(String checkedS, String otherS) {
		return (checkedS == null || checkedS.length() < 2) ? otherS : checkedS;
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
	
	private static String maxLenString(String s, int len, boolean trim) {
		String res = s;
		if (trim) res = s.trim();
		if (res.length() > len) {
			res = res.substring(0, len);
		}
		return res;
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
	
	private static int getDaysSinceTime(Long time) {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - time) / 86400);
	}
	
	private static boolean isValidEmailAddress(String email) {
		boolean stricterFilter = true;
		String stricterFilterString = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
		String laxString = ".+@.+\\.[A-Za-z]{2}[A-Za-z]*";
		String emailRegex = stricterFilter ? stricterFilterString : laxString;
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailRegex);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

}
