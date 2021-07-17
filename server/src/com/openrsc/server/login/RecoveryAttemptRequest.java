package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.database.impl.mysql.queries.logging.SecurityChangeLog;
import com.openrsc.server.database.struct.PlayerLoginData;
import com.openrsc.server.database.struct.PlayerRecoveryQuestions;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.util.rsc.DataConversions;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to recovery character passwords on the Login thread
 */
public class RecoveryAttemptRequest extends LoginExecutorProcess{

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final Channel channel;
	private String ipAddress;
	private String username;
	private String newPassword;
	private String oldPassword;
	private String[] answers;

	public RecoveryAttemptRequest(final Server server, final Channel channel, final String username, final String oldPassword, final String newPassword, final String[] answers) {
		this.server = server;
		this.channel = channel;
		this.setUsername(username);
		this.setNewPassword(newPassword);
		this.setOldPassword(oldPassword);
		this.setAnswers(answers);
		this.setIpAddress(getChannel().remoteAddress().toString());
	}

	public String getIpAddress() {
		return ipAddress;
	}

	private void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUsername() {
		return username;
	}

	private void setUsername(final String username) {
		this.username = username;
	}

	public String getNewPassword() {
		return newPassword;
	}

	private void setNewPassword(final String password) {
		this.newPassword = password;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	private void setOldPassword(final String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public Channel getChannel() {
		return channel;
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

	protected void processInternal() {
		try {
			server.getPacketFilter().addPasswordAttempt(getIpAddress());

			if(!getServer().getPacketFilter().shouldAllowLogin(getIpAddress(), false)) {
				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
				getChannel().close();
				return;
			}

			int pid = -1;

			final PlayerLoginData playerLoginData = getServer().getDatabase().getPlayerLoginData(getUsername());
			pid = playerLoginData.id;

			boolean foundAndHasRecovery = false;

			final PlayerRecoveryQuestions recoveryQuestions = getServer().getDatabase().getPlayerRecoveryData(pid);
			if (recoveryQuestions != null) {
				foundAndHasRecovery = true;
			}

			Player player = getServer().getWorld().getPlayer(pid);

			if (player != null || !foundAndHasRecovery) {
				if(!foundAndHasRecovery) {
					LOGGER.info("Recovery attempt for " + getUsername() + " aborted due to not having recovery.");
				} else if(player != null) {
					LOGGER.info("Recovery attempt for " + getUsername() + " aborted due to player logged in.");
				}

				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
				getChannel().close();
			} else {
				String salt = playerLoginData.salt;
				String currDBPass = playerLoginData.password;

				int numCorrect = (
					DataConversions.checkPassword(getOldPassword(), salt, recoveryQuestions.previousPass) ||
						DataConversions.checkPassword(getOldPassword(), salt, recoveryQuestions.earlierPass)
				) ? 1 : 0;
				for (int j=0; j<5; j++) {
					numCorrect += DataConversions.checkPassword(getAnswers()[j], salt, recoveryQuestions.answers[j]) ? 1 : 0;
				}

				int tryID = getServer().getDatabase().newRecoveryAttempt(pid, getUsername(), System.currentTimeMillis() / 1000, getIpAddress());

				//enough threshold to allow pass change for recovery
				if (numCorrect >= 4) {
					if (getNewPassword().length() < 4 || getNewPassword().length() > 20) {
						LOGGER.info("Recover attempt for " + getUsername() + " is not successful. Password is either too long or too short");
						getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
						getChannel().close();
						return;
					}
					final String hashedNewPassword = DataConversions.hashPassword(getNewPassword(), salt);
					getServer().getDatabase().saveNewPassword(pid, hashedNewPassword);
					getServer().getDatabase().saveLastRecoveryTryId(pid, tryID);

					try {
						String logMessage = "(@Recovery) From: " + currDBPass + ", To: " + hashedNewPassword;
						//log password change
						getServer().getGameLogger().addQuery(new SecurityChangeLog(getServer(), pid, getIpAddress(), SecurityChangeLog.ChangeEvent.PASSWORD_CHANGE, logMessage));
						LOGGER.info("Recovery attempt for " + getUsername() + " is successful with " + numCorrect + " correct guesses : " + logMessage);
						getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 1).toPacket());

					} finally {
						getChannel().close();
					}

				} else {
					LOGGER.info("Recovery attempt for " + getUsername() + " is NOT successful with " + numCorrect + " correct guesses.");

					getServer().getDatabase().saveLastRecoveryTryId(pid, tryID);

					getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
					getChannel().close();
				}
			}
		} catch (Exception e) {
			LOGGER.catching(e);
			getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
			getChannel().close();
		}
	}
}
