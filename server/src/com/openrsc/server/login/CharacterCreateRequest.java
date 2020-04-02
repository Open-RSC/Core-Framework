package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.net.PacketBuilder;
import com.openrsc.server.util.rsc.DataConversions;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to create a Character on the Login thread
 *
 * @author Kenix
 */
public class CharacterCreateRequest extends LoginExecutorProcess{

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

	protected void processInternal() {
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

			if (getServer().getConfig().WANT_REGISTRATION_LIMIT) {
				boolean recentlyRegistered = getServer().getDatabase().checkRecentlyRegistered(getIpAddress());
				if (recentlyRegistered) {
					LOGGER.info(getIpAddress() + " - Registration failed: Registered recently.");
					getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 5).toPacket());
					getChannel().close();
					return;
				}
			}

			boolean usernameExists = getServer().getDatabase().playerExists(getUsername());
			if (usernameExists) {
				LOGGER.info(getIpAddress() + " - Registration failed: Forum Username already in use.");
				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 2).toPacket());
				getChannel().close();
				return;
			}

			/* Create the game character */
			final int playerId = getServer().getDatabase().createPlayer(getUsername(), getEmail(),
				DataConversions.hashPassword(getPassword(), null),
				System.currentTimeMillis() / 1000, getIpAddress());

			if (playerId == -1) {
				getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 6).toPacket());
				LOGGER.info(getIpAddress() + " - Registration failed: Player id not found.");
				return;
			}

			LOGGER.info(getIpAddress() + " - Registration successful");
			getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 0).toPacket());
		} catch (Exception e) {
			LOGGER.catching(e);
			getChannel().writeAndFlush(new PacketBuilder().writeByte((byte) 5).toPacket());
			getChannel().close();
		}
	}
}
