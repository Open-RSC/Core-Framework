package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.sql.query.logs.SecurityChangeLog;
import com.openrsc.server.util.rsc.DataConversions;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Used to recovery character passwords on the Login thread
 *
 * @author Kenix
 */
public class RecoveryChangeRequest {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final Channel channel;
	private Player player;
	private String[] questions;
	private String[] answers;

	public RecoveryChangeRequest(final Server server, final Channel channel, final Player player, final String[] questions, final String[] answers) {
		this.server = server;
		this.channel = channel;
		this.setPlayer(player);
		this.setAnswers(answers);
		this.setQuestions(questions);
	}

	public Player getPlayer() {
		return player;
	}

	private void setPlayer(Player player) {
		this.player = player;
	}

	public String[] getQuestions() {
		return questions;
	}

	private void setQuestions(String[] questions) {
		this.questions = questions;
	}

	public String[] getAnswers() {
		return answers;
	}

	private void setAnswers(final String[] answers) {
		this.answers = answers;
	}

	public Server getServer() {
		return server;
	}

	public Channel getChannel() {
		return channel;
	}

	public void process() {
		try {
			LOGGER.info("Recovery questions change from: " + getPlayer().getCurrentIP());

			boolean containsAllInfo = true;

			for(int i = 0; i < getQuestions().length; i++) {
				if (getQuestions()[i] == null || getQuestions()[i].trim().isEmpty()) {
					containsAllInfo = false;
				}
			}

			for(int i = 0; i < getAnswers().length; i++) {
				if (getAnswers()[i] == null || getAnswers()[i].trim().isEmpty()) {
					containsAllInfo = false;
				}
			}

			int playerID = getPlayer().getDatabaseID();
			if (playerID == -1) {
				LOGGER.info(getPlayer().getCurrentIP() + " - Set recovery questions failed: Could not find player info in database.");
				return;
			}
			PreparedStatement statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT 1 FROM " + getPlayer().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery WHERE playerID=?");
			statement.setInt(1, playerID);
			ResultSet result = statement.executeQuery();
			String table_suffix;
			if (!result.next()) {
				//player has not set recovery questions
				table_suffix = "player_recovery";
			} else {
				statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT date_set FROM " + getPlayer().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_change_recovery WHERE playerID=?");
				statement.setInt(1, playerID);
				result = statement.executeQuery();
				if (!result.next() || DataConversions.getDaysSinceTime(result.getLong("date_set")) >= 14) {
					table_suffix = "player_change_recovery";
				} else {
					ActionSender.sendMessage(getPlayer(), "You have pending recovery questions to get applied");
					LOGGER.info(getPlayer().getCurrentIP() + " - Set recovery questions failed: There is a pending request to be applied.");
					return;
				}
			}

			if (!containsAllInfo) {
				ActionSender.sendMessage(getPlayer(), "Could not set recovery questions, one or more fields empty");
				LOGGER.info(getPlayer().getCurrentIP() + " - Set recovery questions failed: One or more fields are empty.");
				return;
			}

			statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + getPlayer().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE id=?");
			statement.setInt(1, playerID);
			result = statement.executeQuery();
			result.next();
			String salt = result.getString("salt");
			for (int i = 0; i < 5; i++) {
				getQuestions()[i] = DataConversions.maxLenString(getQuestions()[i], 50, true);
				getAnswers()[i] = DataConversions.hashPassword(getAnswers()[i], salt);
			}

			statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement(
				"INSERT INTO `" + getPlayer().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + table_suffix + "` (`playerID`, `username`, `question1`, `answer1`, `question2`, `answer2`, `question3`, `answer3`, `question4`, `answer4`, `question5`, `answer5`, `date_set`, `ip_set`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setInt(1, playerID);
			statement.setString(2, getPlayer().getUsername());
			statement.setString(3, getQuestions()[0]);
			statement.setString(4, getAnswers()[0]);
			statement.setString(5, getQuestions()[1]);
			statement.setString(6, getAnswers()[1]);
			statement.setString(7, getQuestions()[2]);
			statement.setString(8, getAnswers()[2]);
			statement.setString(9, getQuestions()[3]);
			statement.setString(10, getAnswers()[3]);
			statement.setString(11, getQuestions()[4]);
			statement.setString(12, getAnswers()[4]);
			statement.setLong(13, System.currentTimeMillis() / 1000);
			statement.setString(14, getPlayer().getCurrentIP());
			statement.executeUpdate();

			/*StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 5; i++) {
				sb.append("(").append(getQuestions()[i]).append(",").append(getAnswers()[i]).append("), ");
			}
			getPlayer().getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(getPlayer(), SecurityChangeLog.ChangeEvent.RECOVERY_QUESTIONS_CHANGE,
				"Added questions/answers {" + sb.toString() + "}"));*/

			getPlayer().getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(getPlayer(), SecurityChangeLog.ChangeEvent.RECOVERY_QUESTIONS_CHANGE,
				"Added questions/answers {" + getPlayer().getUsername() + "}"));

			if (table_suffix.equals("player_recovery")) {
				ActionSender.sendMessage(getPlayer(), "Recovery questions set successfully!");
			} else {
				ActionSender.sendMessage(getPlayer(), "Your request to change recovery has been submitted");
			}
			LOGGER.info(getPlayer().getCurrentIP() + " - Recovery questions change successful");
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}
