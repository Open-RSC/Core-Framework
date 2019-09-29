package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.PacketBuilder;
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
public class RecoveryAttemptRequest {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private String ipAddress;
	private String username;
	private String newPassword;
	private String oldPassword;
	private String[] answers;
	private Channel channel;

	public RecoveryAttemptRequest(final Server server, final String username, final String oldPassword, final String newPassword, final String[] answers, final Channel channel) {
		this.server = server;
		this.setUsername(username);
		this.setNewPassword(newPassword);
		this.setOldPassword(oldPassword);
		this.setAnswers(answers);
		this.setIpAddress(channel.remoteAddress().toString());
		this.setChannel(channel);
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

	private void setChannel(final Channel channel) {
		this.channel = channel;
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

	public void process() {
		try {
			int pid = -1;

			PreparedStatement statement = getServer().getDatabaseConnection().prepareStatement("SELECT id, pass, salt FROM " + getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE username=?");
			statement.setString(1, getUsername());
			ResultSet res = statement.executeQuery();
			ResultSet res2 = null;
			boolean foundAndHasRecovery = false;

			if (res.next()) {
				pid = res.getInt("id");
				statement = getServer().getDatabaseConnection().prepareStatement("SELECT * FROM " + getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery WHERE playerID=?");
				statement.setInt(1, pid);
				res2 = statement.executeQuery();
				if (res2.next()) {
					foundAndHasRecovery = true;
				}
			}

			Player player = getServer().getWorld().getPlayer(pid);

			if (player == null || !foundAndHasRecovery) {
				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
				getChannel().close();
			} else {
				String salt = res.getString("salt");
				String currDBPass = res.getString("pass");
				String hashedNewPassword = DataConversions.hashPassword(getNewPassword(), salt);

				int numCorrect = (
					DataConversions.checkPassword(getOldPassword(), salt, res2.getString("previous_pass")) ||
						DataConversions.checkPassword(getOldPassword(), salt, res2.getString("earlier_pass"))
				) ? 1 : 0;
				for (int j=0; j<5; j++) {
					numCorrect += DataConversions.checkPassword(getAnswers()[j], salt, res2.getString("answer"+(j+1))) ? 1 : 0;
				}

				PreparedStatement attempt = getServer().getDatabaseConnection().prepareStatement("INSERT INTO `" + getServer().getConfig().MYSQL_TABLE_PREFIX
					+ "recovery_attempts`(`playerID`, `username`, `time`, `ip`) VALUES(?, ?, ?, ?)", new String[]{"dbid"});
				attempt.setInt(1, pid);
				attempt.setString(2, getUsername());
				attempt.setLong(3, System.currentTimeMillis() / 1000);
				attempt.setString(4, getIpAddress());
				attempt.executeUpdate();
				ResultSet set = attempt.getGeneratedKeys();

				int tryID = -1;
				if (set.next()) {
					tryID = set.getInt(1);
				}

				PreparedStatement innerStatement;

				//enough treshold to allow pass change for recovery
				if (numCorrect >= 4) {
					innerStatement = getServer().getDatabaseConnection().prepareStatement(
						"UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "players` SET `pass`=?, `lastRecoveryTryId`=? WHERE `id`=?");
					innerStatement.setString(1, hashedNewPassword);
					innerStatement.setInt(2, tryID);
					innerStatement.setInt(3, pid);
					innerStatement.executeUpdate();

					//log password change
					getServer().getGameLogger().addQuery(new SecurityChangeLog(player, SecurityChangeLog.ChangeEvent.PASSWORD_CHANGE,
						"(@Recovery) From: " + currDBPass + ", To: " + hashedNewPassword));

					getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 1).toPacket());
					getChannel().close();
				} else {
					innerStatement = getServer().getDatabaseConnection().prepareStatement(
						"UPDATE `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "players` SET `lastRecoveryTryId`=? WHERE `id`=?");
					innerStatement.setInt(1, tryID);
					innerStatement.setInt(2, pid);
					innerStatement.executeUpdate();

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
