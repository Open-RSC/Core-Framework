package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.LoginResponse;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class LoginRequest {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final Channel channel;
	protected Player loadedPlayer;
	private String ipAddress;
	private String username;
	private String password;
	private long usernameHash;
	private int clientVersion;


	protected LoginRequest(final Server server, final Channel channel, final String username, final String password, final int clientVersion) {
		this.server = server;
		this.channel = channel;
		this.setUsername(username);
		this.setPassword(password);
		this.setIpAddress(((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress());
		this.setClientVersion(clientVersion);
		this.setUsernameHash(DataConversions.usernameToHash(username));
	}

	public String getIpAddress() {
		return ipAddress;
	}

	private void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}

	public long getUsernameHash() {
		return usernameHash;
	}

	private void setUsernameHash(long usernameHash) {
		this.usernameHash = usernameHash;
	}

	public Channel getChannel() {
		return channel;
	}

	public Server getServer() {
		return server;
	}

	public int getClientVersion() {
		return clientVersion;
	}

	private void setClientVersion(int clientVersion) {
		this.clientVersion = clientVersion;
	}

	public abstract void loginValidated(int response);

	public abstract void loadingComplete(Player loadedPlayer);

	public void process() {
		int loginResponse = validateLogin();
		loginValidated(loginResponse);
		if ((loginResponse & 0x40) != LoginResponse.LOGIN_INSUCCESSFUL) {
			final Player loadedPlayer = getServer().getLoginExecutor().getPlayerDatabase().loadPlayer(this);

			getServer().getGameEventHandler().add(new ImmediateEvent(getServer().getWorld(), "Login Player") {
				@Override
				public void action() {
					loadingComplete(loadedPlayer);
				}
			});

		}
		LOGGER.info("Processed login request for " + getUsername() + " response: " + loginResponse);
	}

	public byte validateLogin() {
		PreparedStatement statement = null;
		ResultSet playerSet = null;
		int groupId = Group.USER;
		try {
			if(!getServer().getPacketFilter().shouldAllowLogin(getIpAddress(), false)) {
				return (byte) LoginResponse.LOGIN_ATTEMPTS_EXCEEDED;
			}

			statement = getServer().getLoginExecutor().getPlayerDatabase().getDatabaseConnection().prepareStatement(
				getServer().getLoginExecutor().getPlayerDatabase().getDatabaseConnection().getGameQueries().playerLoginData
			);
			statement.setString(1, getUsername());
			playerSet = statement.executeQuery();

			boolean isAdmin = getServer().getPacketFilter().isHostAdmin(getIpAddress());
			if(playerSet.first()) {
				groupId = playerSet.getInt("group_id");
				isAdmin = isAdmin || groupId == Group.OWNER || groupId == Group.ADMIN;
			}

			if(getServer().getPacketFilter().getPasswordAttemptsCount(getIpAddress()) >= getServer().getConfig().MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES && !isAdmin) {
				return (byte) LoginResponse.LOGIN_ATTEMPTS_EXCEEDED;
			}

			if (getServer().getPacketFilter().isHostIpBanned(getIpAddress()) && !isAdmin) {
				return (byte) LoginResponse.ACCOUNT_TEMP_DISABLED;
			}

			if (getClientVersion() != getServer().getConfig().CLIENT_VERSION && !isAdmin) {
				return (byte) LoginResponse.CLIENT_UPDATED;
			}

			long i = getServer().timeTillShutdown();
			if (i > 0 && i < 30000 && !isAdmin) {
				return (byte) LoginResponse.WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS;
			}

			if (!playerSet.first()) {
				server.getPacketFilter().addPasswordAttempt(getIpAddress());
				return (byte) LoginResponse.INVALID_CREDENTIALS;
			}

			if(getServer().getWorld().getPlayers().size() >= getServer().getConfig().MAX_PLAYERS && !isAdmin) {
				return (byte) LoginResponse.WORLD_IS_FULL;
			}

			if (getServer().getWorld().getPlayer(getUsernameHash()) != null) {
				return (byte) LoginResponse.ACCOUNT_LOGGEDIN;
			}

			if(getServer().getPacketFilter().getPlayersCount(getIpAddress()) >= getServer().getConfig().MAX_PLAYERS_PER_IP && !isAdmin) {
				return (byte) LoginResponse.IP_IN_USE;
			}

			long banExpires = playerSet.getLong("banned");
			if (banExpires == -1 && !isAdmin) {
				return (byte) LoginResponse.ACCOUNT_PERM_DISABLED;
			}

			double timeBanLeft = (double) (banExpires - System.currentTimeMillis());
			if (timeBanLeft >= 1 && !isAdmin) {
				return (byte) LoginResponse.ACCOUNT_TEMP_DISABLED;
			}

			if (!DataConversions.checkPassword(getPassword(), playerSet.getString("salt"), playerSet.getString("pass"))) {
				server.getPacketFilter().addPasswordAttempt(getIpAddress());
				return (byte) LoginResponse.INVALID_CREDENTIALS;
			}

			// Doing this at end because we only want to flag the host as an admin _IF_ they know the password.
			if(isAdmin) {
				getServer().getPacketFilter().addAdminHost(getIpAddress());
			}

		} catch (SQLException e) {
			LOGGER.catching(e);
			return (byte) LoginResponse.LOGIN_INSUCCESSFUL;
		}
		return (byte) LoginResponse.LOGIN_SUCCESSFUL[groupId];
	}
}
