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
 * Used to run change password functionality on the Login thread
 *
 * @author Kenix
 */
public class PasswordChangeRequest {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final Channel channel;
	private Player player;
	private String oldPassword;
	private String newPassword;

	public PasswordChangeRequest(final Server server, final Channel channel, final Player player, final String oldPassword, final String newPassword) {
		this.server = server;
		this.channel = channel;
		this.setPlayer(player);
		this.setOldPassword(oldPassword);
		this.setNewPassword(newPassword);
	}

	public Player getPlayer() {
		return player;
	}

	private void setPlayer(Player player) {
		this.player = player;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	private void setOldPassword(final String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	private void setNewPassword(final String newPassword) {
		this.newPassword = newPassword;
	}

	public Server getServer() {
		return server;
	}

	public Channel getChannel() {
		return channel;
	}

	public void process() {
		try {
			LOGGER.info("Password change attempt from: " + getPlayer().getCurrentIP());
			PreparedStatement statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT id, pass, salt FROM " + getPlayer().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE username=?");
			statement.setString(1, getPlayer().getUsername());
			ResultSet result = statement.executeQuery();
			if (!result.next()) {
				LOGGER.info(getPlayer().getCurrentIP() + " - Pass change failed: Could not find player info in database.");
				return;
			}
			String lastDBPass = result.getString("pass");
			String DBsalt = result.getString("salt");
			String newDBPass;
			int playerID = result.getInt("id");
			if (!DataConversions.checkPassword(getOldPassword(), DBsalt, lastDBPass)) {
				LOGGER.info(getPlayer().getCurrentIP() + " - Pass change failed: The current password did not match players record.");
				ActionSender.sendMessage(getPlayer(), "No changes made, your current password did not match");
				return;
			}
			newDBPass = DataConversions.hashPassword(getNewPassword(), DBsalt);

			statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement(
				"UPDATE `" + getPlayer().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players` SET `pass`=? WHERE `id`=?");
			statement.setString(1, newDBPass);
			statement.setInt(2, playerID);
			statement.executeUpdate();

			statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT previous_pass FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery WHERE playerID=?");
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

				statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement(
					"UPDATE `" + getPlayer().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "player_recovery` SET `previous_pass`=?, `earlier_pass`=? WHERE `playerID`=?");
				statement.setString(1, lastPw);
				statement.setString(2, earlierPw);
				statement.setInt(3, playerID);
				statement.executeUpdate();
			}

			getPlayer().getWorld().getServer().getGameLogger().addQuery(new SecurityChangeLog(getPlayer(), SecurityChangeLog.ChangeEvent.PASSWORD_CHANGE,
				"From: " + lastDBPass + ", To: " + newDBPass));
			ActionSender.sendMessage(getPlayer(), "Your password was successfully changed!");
			LOGGER.info(getPlayer().getCurrentIP() + " - Password change successful");
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}
