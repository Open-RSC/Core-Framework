package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.queries.logging.LoginLog;
import com.openrsc.server.database.struct.InvoluntaryChangeDetails;
import com.openrsc.server.database.struct.PlayerLoginData;
import com.openrsc.server.database.struct.UsernameChangeType;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
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
	private boolean isSimLogin;
	private int[] nonces;
	private UsernameChangeType usernameChangeType = UsernameChangeType.NOT_RENAMED;

	protected LoginRequest(final Server server, final Channel channel, final String username, final String password, final boolean isAuthenticClient, final int clientVersion, final boolean reconnecting, final int[] nonces) {
		this.server = server;
		this.channel = channel;
		this.setUsername(DataConversions.sanitizeUsername(username));
		this.setPassword(password);
		this.setAuthenticClient(isAuthenticClient);
		this.setIpAddress(((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress());
		this.setClientVersion(clientVersion);
		this.setUsernameHash(DataConversions.usernameToHash(username));
		this.reconnecting = reconnecting;
		this.isSimLogin = false;
		this.nonces = nonces;
	}

	// only used for sim login
	protected LoginRequest(final Server server, final String username, final String ip, final int clientVersion) {
		this.server = server;
		this.channel = null;
		this.setUsername(DataConversions.sanitizeUsername(username));
		this.setAuthenticClient(clientVersion <= 235);
		this.setIpAddress(ip);
		this.setClientVersion(clientVersion);
		this.setUsernameHash(DataConversions.usernameToHash(username));
		this.reconnecting = false;
		this.isSimLogin = true;
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
		ValidatedLogin vl = validateLogin();
		int loginResponse = vl.responseCode;

		if (clientVersion <= 204) {
			loginResponse = RegisterLoginResponse.translateNewToOld(loginResponse, clientVersion, false);
		}
		loginValidated(loginResponse);

		if (!isSimLogin && isLoginSuccessful(loginResponse)) {
			final Player loadedPlayer = getServer().getPlayerService().loadPlayer(this);
			loadedPlayer.setLoggedIn(true);

			LOGGER.info("Player Loaded: " + getUsername() +  String.format("; Client Version: %d", clientVersion));

			getServer().getGameEventHandler().add(new ImmediateEvent(getServer().getWorld(), "Login Player") {
				@Override
				public void action() {
					loadingComplete(loadedPlayer);
					loadedPlayer.desertHeatInit();
					ActionSender.sendReleasedNameExplanation(loadedPlayer, usernameChangeType);
				}
			});

		}
		LOGGER.info("Processed login request for " + getUsername() + " response: " + loginResponse);
	}

	public ValidatedLogin validateLogin() {
		PlayerLoginData playerData;
		int groupId = Group.USER;
		try {
			if (getServer().isRestarting() || getServer().isShuttingDown() || !getServer().getLoginExecutor().isRunning()) {
				return new ValidatedLogin(LoginResponse.WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS);
			}

			if(!getServer().getPacketFilter().shouldAllowLogin(getIpAddress(), false)) {
				return new ValidatedLogin(LoginResponse.LOGIN_ATTEMPTS_EXCEEDED);
			}

			playerData = getServer().getDatabase().getPlayerLoginData(username);

			if (playerData == null) {
				// didn't find player that exists, try again by former name
				InvoluntaryChangeDetails oldUsername = getServer().getDatabase().queryFormerNameInvoluntaryChange(username);
				if (null != oldUsername) {
					username = oldUsername.username;
					playerData = getServer().getDatabase().getPlayerLoginData(username);
					usernameChangeType = oldUsername.changeType;
				}
			} else {
				// check for released name that has been reassigned
				// (password will almost always mismatch, else they've logged into the new account)
				if (!DataConversions.checkPassword(getPassword(), playerData.salt, playerData.password)) {
					// TODO: could support searching more than just one formerPlayerData if more than one person has the same released name
					// wouldn't be an issue until years from now.
					PlayerLoginData formerPlayerData = getServer().getDatabase().getPlayerLoginDataByFormerName("$" + username);
					if (null != formerPlayerData && DataConversions.checkPassword(getPassword(), formerPlayerData.salt, formerPlayerData.password)) {
						// password matches for a player who used to be named this, and had their account name freed without their knowledge.
						usernameChangeType = UsernameChangeType.RELEASED;
						username = formerPlayerData.username;
						playerData = formerPlayerData;
					}
				}
			}

			boolean isAdmin = getServer().getPacketFilter().isHostAdmin(getIpAddress());
			if (playerData != null) {
				groupId = playerData.groupId;
				isAdmin = isAdmin || groupId == Group.OWNER || groupId == Group.ADMIN;
			}

			if(!getIpAddress().equals("127.0.0.1") && getServer().getPacketFilter().getPasswordAttemptsCount(getIpAddress()) >= getServer().getConfig().MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES && !isAdmin) {
				return new ValidatedLogin(LoginResponse.LOGIN_ATTEMPTS_EXCEEDED);
			}

			if (getServer().getPacketFilter().isHostIpBanned(getIpAddress()) && !isAdmin) {
				LOGGER.debug(getIpAddress() + " denied for being host ip banned...!");
				return new ValidatedLogin(LoginResponse.ACCOUNT_TEMP_DISABLED);
			}


			if (getClientVersion() != getServer().getConfig().CLIENT_VERSION && !isAdmin) {
				if (getClientVersion() > 10000) {
					if (getServer().getConfig().ENFORCE_CUSTOM_CLIENT_VERSION) {
						return new ValidatedLogin(LoginResponse.CLIENT_UPDATED);
					}
				} else {
					if (getServer().getConfig().WANT_CUSTOM_SPRITES) {
						return new ValidatedLogin(LoginResponse.CLIENT_UPDATED);
					}
				}
			}

			final long i = getServer().getTimeUntilShutdown();
			if (i > 0 && i < 30000 && !isAdmin) {
				return new ValidatedLogin(LoginResponse.WORLD_DOES_NOT_ACCEPT_NEW_PLAYERS);
			}

			if (playerData == null) {
				server.getPacketFilter().addPasswordAttempt(getIpAddress());
				return new ValidatedLogin(LoginResponse.INVALID_CREDENTIALS);
			}

			if(getServer().getWorld().getPlayers().size() >= getServer().getConfig().MAX_PLAYERS && !isAdmin) {
					return new ValidatedLogin(LoginResponse.WORLD_IS_FULL);
			}

			if (getServer().getWorld().getPlayer(getUsernameHash()) != null) {
				return new ValidatedLogin(LoginResponse.ACCOUNT_LOGGEDIN);
			}

			int playersCount = getServer().getPacketFilter().getPlayersCount(getIpAddress());
			if (!isAdmin && !getIpAddress().equals("127.0.0.1") && playersCount >= getServer().getConfig().MAX_PLAYERS_PER_IP) {
				LOGGER.info(getIpAddress() + " is using " + playersCount + " out of " + getServer().getConfig().MAX_PLAYERS_PER_IP + " allowed sessions, rejecting additional connection.");
				return new ValidatedLogin(LoginResponse.IP_IN_USE);
			}

			final long banExpires = playerData.banned;
			if (banExpires == -1) {
				return new ValidatedLogin(LoginResponse.ACCOUNT_PERM_DISABLED);
			}

			final double timeBanLeft = (double) (banExpires - System.currentTimeMillis());
			if (timeBanLeft >= 1) {
				LOGGER.debug(getIpAddress() + " denied for being *actually* temp banned...!");
				return new ValidatedLogin(LoginResponse.ACCOUNT_TEMP_DISABLED);
			}

			if (!isSimLogin && !DataConversions.checkPassword(getPassword(), playerData.salt, playerData.password)) {
				server.getPacketFilter().addPasswordAttempt(getIpAddress());
				return new ValidatedLogin(LoginResponse.INVALID_CREDENTIALS);
			}

			// all other checks passed, check cryptographic nonces have not been used before by inserting into a UNIQUE column
			if (!isSimLogin && !getServer().getDatabase().queryInsertLoginAttempt(new LoginLog(playerData.id, getIpAddress(), clientVersion, nonces))) {
				return new ValidatedLogin(LoginResponse.INVALID_CREDENTIALS);
			}

			// Doing this at end because we only want to flag the host as an admin _IF_ they know the password.
			if(isAdmin) {
				getServer().getPacketFilter().addAdminHost(getIpAddress());
			}


		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
			return new ValidatedLogin(LoginResponse.LOGIN_UNSUCCESSFUL);
		}

		if (reconnecting && clientVersion <= 204) {
			return new ValidatedLogin(LoginResponse.RECONNECT_SUCCESFUL);
		}
		getServer().getPacketFilter().addLoggedInPlayer(getIpAddress(), getUsernameHash());
		return new ValidatedLogin(LoginResponse.LOGIN_SUCCESSFUL[groupId]);
	}

	public boolean isLoginSuccessful(int loginResponse) {
		return (loginResponse & 0x40) != LoginResponse.LOGIN_UNSUCCESSFUL ||
			((loginResponse == 0 || loginResponse == 1) && clientVersion <= 204);
	}
}
