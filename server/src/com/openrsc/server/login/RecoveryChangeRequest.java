package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.database.impl.mysql.queries.logging.SecurityChangeLog;
import com.openrsc.server.database.struct.PlayerRecoveryQuestions;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to recovery character passwords on the Login thread
 */
public class RecoveryChangeRequest extends LoginExecutorProcess{

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

	protected void processInternal() {
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

			PlayerRecoveryQuestions recoveryQuestions = getPlayer().getWorld().getServer().getDatabase().getPlayerRecoveryData(playerID);
			boolean recoverySet;
			if (recoveryQuestions == null) {
				//player has not set recovery questions
				recoverySet = false;
			} else {
				PlayerRecoveryQuestions recoveryChangeQuestions = getPlayer().getWorld().getServer().getDatabase().getPlayerChangeRecoveryData(playerID);
				if (recoveryChangeQuestions == null || DataConversions.getDaysSinceTime(recoveryChangeQuestions.dateSet) >= 14) {
					recoverySet = true;
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

			String salt = getPlayer().getWorld().getServer().getDatabase().getPlayerLoginData(getPlayer().getUsername()).salt;
			for (int i = 0; i < 5; i++) {
				getQuestions()[i] = DataConversions.maxLenString(getQuestions()[i], 50, true);
				getAnswers()[i] = DataConversions.hashPassword(getAnswers()[i], salt);
			}

			PlayerRecoveryQuestions newRecovery = new PlayerRecoveryQuestions();
			newRecovery.username = getPlayer().getUsername();
			newRecovery.question1 = getQuestions()[0];
			newRecovery.question2 = getQuestions()[1];
			newRecovery.question3 = getQuestions()[2];
			newRecovery.question4 = getQuestions()[3];
			newRecovery.question5 = getQuestions()[4];
			for (int i = 0; i < getAnswers().length; i++) {
				newRecovery.answers[i] = getAnswers()[i];
			}
			newRecovery.dateSet = System.currentTimeMillis() / 1000;
			newRecovery.ipSet = getPlayer().getCurrentIP();

			if (recoverySet) {
				getPlayer().getWorld().getServer().getDatabase().newPlayerChangeRecoveryData(playerID, newRecovery);
			} else {
				getPlayer().getWorld().getServer().getDatabase().newPlayerRecoveryData(playerID, newRecovery);
			}

		/*StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			sb.append("(").append(getQuestions()[i]).append(",").append(getAnswers()[i]).append("), ");
		}
		getPlayer().getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(getPlayer(), SecurityChangeLog.ChangeEvent.RECOVERY_QUESTIONS_CHANGE,
			"Added questions/answers {" + sb.toString() + "}"));*/

			getPlayer().getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(getPlayer(), SecurityChangeLog.ChangeEvent.RECOVERY_QUESTIONS_CHANGE,
				"Added questions/answers {" + getPlayer().getUsername() + "}"));

			if (!recoverySet) {
				ActionSender.sendMessage(getPlayer(), "Recovery questions set successfully!");
			} else {
				ActionSender.sendMessage(getPlayer(), "Your request to change recovery has been submitted");
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}
