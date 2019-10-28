package com.openrsc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.login.*;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.sql.PlayerDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
					loginRequest.process();
				}

				Player playerToSave = null;
				while ((playerToSave = saveRequests.poll()) != null) {
					getPlayerDatabase().savePlayer(playerToSave);
					//LOGGER.info("Saved player " + playerToSave.getUsername() + "");
				}

				Player playerToRemove = null;
				while ((playerToRemove = removeRequests.poll()) != null) {
					getServer().getPacketFilter().removeLoggedInPlayer(playerToRemove.getCurrentIP());

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
