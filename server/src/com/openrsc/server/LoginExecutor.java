package com.openrsc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.login.*;
import com.openrsc.server.model.entity.player.Group;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.sql.PlayerDatabase;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.LoginResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoginExecutor implements Runnable {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final ScheduledExecutorService loginThreadExecutor;

	private Queue<LoginRequest> loadRequests = new ConcurrentLinkedQueue<LoginRequest>();

	private Queue<Player> saveRequests = new ConcurrentLinkedQueue<Player>();

	private Queue<Player> removeRequests = new ConcurrentLinkedQueue<Player>();

	private Queue<CharacterCreateRequest> characterCreateRequests = new ConcurrentLinkedQueue<CharacterCreateRequest>();

	private Queue<RecoveryAttemptRequest> recoveryAttemptRequests = new ConcurrentLinkedQueue<RecoveryAttemptRequest>();

	private Queue<PasswordChangeRequest> passwordChangeRequests = new ConcurrentLinkedQueue<PasswordChangeRequest>();

	private Queue<RecoveryChangeRequest> recoveryChangeRequests = new ConcurrentLinkedQueue<RecoveryChangeRequest>();

	private PlayerDatabase playerDatabase;

	private Boolean running	= false;

	private final Server server;
	public final Server getServer() {
		return server;
	}

	public LoginExecutor(Server server) {
		this.server = server;
		this.playerDatabase = new PlayerDatabase(getServer());
		loginThreadExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getServer().getName()+" : LoginThread").build());
	}

	@Override
	public void run() {
		synchronized(running) {
			try {
				LoginRequest loginRequest = null;
				while ((loginRequest = loadRequests.poll()) != null) {
					int loginResponse = validateLogin(loginRequest);
					loginRequest.loginValidated(loginResponse);
					if ((loginResponse & 0x40) != LoginResponse.LOGIN_INSUCCESSFUL) {
						final Player loadedPlayer = playerDatabase.loadPlayer(loginRequest);

						LoginTask loginTask = new LoginTask(loginRequest, loadedPlayer);
						getServer().getGameEventHandler().add(new ImmediateEvent(getServer().getWorld(), "Login Player") {
							@Override
							public void action() {
								loginTask.run();
							}
						});

					}
					LOGGER.info("Processed login request for " + loginRequest.getUsername() + " response: " + loginResponse);
				}

				Player playerToSave = null;
				while ((playerToSave = saveRequests.poll()) != null) {
					getPlayerDatabase().savePlayer(playerToSave);
					//LOGGER.info("Saved player " + playerToSave.getUsername() + "");
				}

				Player playerToRemove = null;
				while ((playerToRemove = removeRequests.poll()) != null) {
					playerToRemove.remove();
					getServer().getWorld().getPlayers().remove(playerToRemove);
					LOGGER.info("Removed player " + playerToRemove.getUsername() + "");
				}

				CharacterCreateRequest characterCreateRequest = null;
				while ((characterCreateRequest = characterCreateRequests.poll()) != null) {
					characterCreateRequest.process();
				}

				RecoveryAttemptRequest recoveryAttemptRequest = null;
				while ((recoveryAttemptRequest = recoveryAttemptRequests.poll()) != null) {
					recoveryAttemptRequest.process();
				}

				PasswordChangeRequest passwordChangeRequest = null;
				while ((passwordChangeRequest = passwordChangeRequests.poll()) != null) {
					passwordChangeRequest.process();
				}

				RecoveryChangeRequest recoveryChangeRequest = null;
				while ((recoveryChangeRequest = recoveryChangeRequests.poll()) != null) {
					recoveryChangeRequest.process();
				}
			} catch (Throwable e) {
				LOGGER.catching(e);
			}
		}
	}

	public byte validateLogin(LoginRequest request) {
		PreparedStatement statement = null;
		ResultSet playerSet = null;
		int groupId = Group.USER;
		try {
			statement = getPlayerDatabase().getDatabaseConnection().prepareStatement(getPlayerDatabase().getDatabaseConnection().getGameQueries().playerLoginData);
			statement.setString(1, request.getUsername());
			playerSet = statement.executeQuery();
			if (!playerSet.next()) {
				return (byte) LoginResponse.INVALID_CREDENTIALS;
			}
			if (!DataConversions.checkPassword(request.getPassword(), playerSet.getString("salt"), playerSet.getString("pass"))) {
				return (byte) LoginResponse.INVALID_CREDENTIALS;
			}
			if (getServer().getWorld().getPlayer(request.getUsernameHash()) != null) {
				return (byte) LoginResponse.ACCOUNT_LOGGEDIN;
			}
			long banExpires = playerSet.getLong("banned");
			if (banExpires == -1) {
				return (byte) LoginResponse.ACCOUNT_PERM_DISABLED;
			}
			double timeBanLeft = (double) (banExpires - System.currentTimeMillis());
			if (timeBanLeft >= 1) {
				return (byte) LoginResponse.ACCOUNT_TEMP_DISABLED;
			}
			groupId = playerSet.getInt("group_id");
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		return (byte) LoginResponse.LOGIN_SUCCESSFUL[groupId];
	}

	public PlayerDatabase getPlayerDatabase() {
		return playerDatabase;
	}

	public void addLoginRequest(LoginRequest request) {
		loadRequests.add(request);
	}

	public void addSaveRequest(Player player) {
		saveRequests.add(player);
	}

	public void addRemoveRequest(Player player) {
		removeRequests.add(player);
	}

	public void addCharacterCreateRequest(CharacterCreateRequest request) {
		characterCreateRequests.add(request);
	}

	public void addRecoveryAttemptRequest(RecoveryAttemptRequest request) {
		recoveryAttemptRequests.add(request);
	}

	public void addPasswordChangeRequest(PasswordChangeRequest request) {
		passwordChangeRequests.add(request);
	}

	public void addRecoveryChangeRequest(RecoveryChangeRequest request) {
		recoveryChangeRequests.add(request);
	}

	public void start() {
		synchronized (running) {
			running = true;
			loginThreadExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
		}
	}

	public void stop() {
		synchronized (running) {
			running = false;
			loginThreadExecutor.shutdown();
		}
	}

	public final boolean isRunning() {
		return running;
	}
}
