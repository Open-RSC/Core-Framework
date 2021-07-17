package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.PlayerLoginData;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.LoginResponse;
import com.openrsc.server.util.rsc.RegisterLoginResponse;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public abstract class LoginRequest extends LoginExecutorProcess{
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
	private boolean authenticClient;
	private boolean reconnecting;

	protected LoginRequest(final Server server, final Channel channel, final String username, final String password, final boolean isAuthenticClient, final int clientVersion, final boolean reconnecting) {
		this.server = server;
		this.channel = channel;
		this.setUsername(DataConversions.sanitizeUsername(username));
		this.setPassword(password);
		this.setAuthenticClient(isAuthenticClient);
		this.setIpAddress(((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress());
		this.setClientVersion(clientVersion);
		this.setUsernameHash(DataConversions.usernameToHash(username));
		this.reconnecting = reconnecting;
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

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	private void setPassword(final String password) {
		this.password = password;
	}

	public long getUsernameHash() {
		return usernameHash;
	}

	private void setUsernameHash(final long usernameHash) {
		this.usernameHash = usernameHash;
	}

	public boolean getAuthenticClient() {
		return authenticClient;
	}

	private void setAuthenticClient(final boolean authenticClient) {
		this.authenticClient = authenticClient;
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

	private void setClientVersion(final int clientVersion) {
		this.clientVersion = clientVersion;
	}

	public abstract void loginValidated(int response);

	public abstract void loadingComplete(Player loadedPlayer);

	protected void processInternal() {
		int loginResponse = validateLogin();

		if (clientVersion <= 204) {
			loginResponse = RegisterLoginResponse.translateNewToOld(loginResponse);
		}
		loginValidated(loginResponse);

		if (isLoginSuccessful(loginResponse)) {
			final Player loadedPlayer = getServer().getPlayerService().loadPlayer(this);
			loadedPlayer.setLoggedIn(true);

			LOGGER.info("Player Loaded: " + getUsername() +  String.format("; Client Version: %d", clientVersion));

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
		PlayerLoginData playerData;
		int groupId = Group.USER;
		try {
			if (getServer().isRestarting() || getServer().isShuttingDown() || !getServer().getLoginExecutor().isRunning()) {
				return (byte) LoginResponse.WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS;
			}

			if(!getServer().getPacketFilter().shouldAllowLogin(getIpAddress(), false)) {
				return (byte) LoginResponse.LOGIN_ATTEMPTS_EXCEEDED;
			}

			playerData = getServer().getDatabase().getPlayerLoginData(username);

			boolean isAdmin = getServer().getPacketFilter().isHostAdmin(getIpAddress());
			if (playerData != null) {
				groupId = playerData.groupId;
				isAdmin = isAdmin || groupId == Group.OWNER || groupId == Group.ADMIN;
			}

			if(getServer().getPacketFilter().getPasswordAttemptsCount(getIpAddress()) >= getServer().getConfig().MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES && !isAdmin) {
				return (byte) LoginResponse.LOGIN_ATTEMPTS_EXCEEDED;
			}

			if (getServer().getPacketFilter().isHostIpBanned(getIpAddress()) && !isAdmin) {
				LOGGER.debug(getIpAddress() + " denied for being host ip banned...!");
				return (byte) LoginResponse.ACCOUNT_TEMP_DISABLED;
			}

			if (getClientVersion() != getServer().getConfig().CLIENT_VERSION
				&& !isAdmin && getClientVersion() < 14) {
				return (byte) LoginResponse.CLIENT_UPDATED;
			}

			final long i = getServer().getTimeUntilShutdown();
			if (i > 0 && i < 30000 && !isAdmin) {
				return (byte) LoginResponse.WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS;
			}

			if (playerData == null) {
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
				LOGGER.debug(getIpAddress() + " is using " + getServer().getPacketFilter().getPlayersCount(getIpAddress()) + " out of " + getServer().getConfig().MAX_PLAYERS_PER_IP + " allowed sessions.");
				return (byte) LoginResponse.IP_IN_USE;
			}

			final long banExpires = playerData.banned;
			if (banExpires == -1 && !isAdmin) {
				return (byte) LoginResponse.ACCOUNT_PERM_DISABLED;
			}

			final double timeBanLeft = (double) (banExpires - System.currentTimeMillis());
			if (timeBanLeft >= 1 && !isAdmin) {
				LOGGER.debug(getIpAddress() + " denied for being *actually* temp banned...!");
				return (byte) LoginResponse.ACCOUNT_TEMP_DISABLED;
			}

			if (!DataConversions.checkPassword(getPassword(), playerData.salt, playerData.password)) {
				server.getPacketFilter().addPasswordAttempt(getIpAddress());
				return (byte) LoginResponse.INVALID_CREDENTIALS;
			}

			// Doing this at end because we only want to flag the host as an admin _IF_ they know the password.
			if(isAdmin) {
				getServer().getPacketFilter().addAdminHost(getIpAddress());
			}


		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
			return (byte) LoginResponse.LOGIN_UNSUCCESSFUL;
		}

		if (reconnecting && clientVersion <= 204) {
			return (byte) LoginResponse.RECONNECT_SUCCESFUL;
		}

		return (byte) LoginResponse.LOGIN_SUCCESSFUL[groupId];
	}

	public boolean isLoginSuccessful(int loginResponse) {
		return (loginResponse & 0x40) != LoginResponse.LOGIN_UNSUCCESSFUL ||
			((loginResponse == 0 || loginResponse == 1) && clientVersion <= 204);
	}
}
