package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.model.Skills;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.util.rsc.DataConversions;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Used to create a Character on the Login thread
 *
 * @author Kenix
 */
public class CharacterCreateRequest {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private String ipAddress;
	private String username;
	private String password;
	private String email;
	private Channel channel;

	public CharacterCreateRequest(final Server server, final Channel channel, final String username, final String password, final String email) {
		this.server = server;
		this.setEmail(email);
		this.setUsername(username);
		this.setPassword(password);
		this.setChannel(channel);
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

	public String getPassword() {
		return password;
	}

	private void setPassword(final String password) {
		this.password = password;
	}

	public Channel getChannel() {
		return channel;
	}

	private void setChannel(final Channel channel) {
		this.channel = channel;
	}

	public String getEmail() {
		return email;
	}

	private void setEmail(final String email) {
		this.email = email;
	}

	public Server getServer() {
		return server;
	}

	public void process() {
		try {
			if (getUsername().length() < 2 || getUsername().length() > 12) {
				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 7).toPacket());
				getChannel().close();
				return;
			}

			if (getPassword().length() < 4 || getPassword().length() > 64) {
				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 8).toPacket());
				getChannel().close();
				return;
			}

			if (getServer().getConfig().WANT_EMAIL) {
				if (!DataConversions.isValidEmailAddress(email)) {
					getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 6).toPacket());
					getChannel().close();
					return;
				}
			}


			ResultSet set = getServer().getDatabaseConnection().executeQuery("SELECT 1 FROM " + getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE creation_ip='" + getIpAddress()
				+ "' AND creation_date>'" + ((System.currentTimeMillis() / 1000) - 60) + "'"); // Checks to see if the player has been registered by the same IP address in the past 1 minute

			if (getServer().getConfig().WANT_REGISTRATION_LIMIT) {
				if (set.next()) {
					set.close();
					LOGGER.info(getIpAddress() + " - Registration failed: Registered recently.");
					getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 5).toPacket());
					getChannel().close();
					return;
				}
			}

			set = getServer().getDatabaseConnection().executeQuery("SELECT 1 FROM " + getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`='" + getUsername() + "'");
			if (set.next()) {
				set.close();
				LOGGER.info(getIpAddress() + " - Registration failed: Forum Username already in use.");
				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 2).toPacket());
				getChannel().close();
				return;
			}

			set = getServer().getDatabaseConnection().executeQuery("SELECT 1 FROM " + getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`='" + getUsername() + "'");
			if (set.next()) {
				set.close();
				LOGGER.info(getIpAddress() + " - Android registration failed: Character Username already in use.");
				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 2).toPacket());
				getChannel().close();
				return;
			}

			/* Create the game character */
			PreparedStatement statement = getServer().getDatabaseConnection().prepareStatement(
				"INSERT INTO `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "players` (`username`, email, `pass`, `creation_date`, `creation_ip`) VALUES (?, ?, ?, ?, ?)");
			statement.setString(1, getUsername());
			statement.setString(2, email);
			statement.setString(3, DataConversions.hashPassword(getPassword(), null));
			statement.setLong(4, System.currentTimeMillis() / 1000);
			statement.setString(5, getIpAddress());
			statement.executeUpdate();
			statement = null;

			/* PlayerID of the player account */
			statement = getServer().getDatabaseConnection().prepareStatement("SELECT id FROM " + getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE username=?");
			statement.setString(1, getUsername());

			set = statement.executeQuery();

			if (!set.next()) {
				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 6).toPacket());
				LOGGER.info(getIpAddress() + " - Registration failed: Player id not found.");
				return;
			}

			int playerID = set.getInt("id");

			statement = getServer().getDatabaseConnection().prepareStatement("INSERT INTO `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "curstats` (`playerID`) VALUES (?)");
			statement.setInt(1, playerID);
			statement.executeUpdate();

			statement = getServer().getDatabaseConnection().prepareStatement("INSERT INTO `" + getServer().getConfig().MYSQL_TABLE_PREFIX + "experience` (`playerID`) VALUES (?)");
			statement.setInt(1, playerID);
			statement.executeUpdate();

			//Don't rely on the default values of the database.
			//Update the stats based on their StatDef-----------------------------------------------
			statement = getServer().getDatabaseConnection().prepareStatement(getServer().getDatabaseConnection().getGameQueries().updateExperience);
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerID);
			Skills newGuy = new Skills(getServer().getWorld(), null);

			for (int index = 0; index < getServer().getConstants().getSkills().getSkillsCount(); index++)
				statement.setInt(index + 1, newGuy.getExperience(index));
			statement.executeUpdate();

			statement = getServer().getDatabaseConnection().prepareStatement(getServer().getDatabaseConnection().getGameQueries().updateStats);
			statement.setInt(getServer().getConstants().getSkills().getSkillsCount() + 1, playerID);
			for (int index = 0; index < getServer().getConstants().getSkills().getSkillsCount(); index++)
				statement.setInt(index + 1, newGuy.getLevel(index));
			statement.executeUpdate();
			//---------------------------------------------------------------------------------------

			LOGGER.info(getIpAddress() + " - Registration successful");
			getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
		} catch (Exception e) {
			LOGGER.catching(e);
			getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 5).toPacket());
			getChannel().close();
		}
	}
}
