package com.openrsc.server;

import com.openrsc.server.login.CharacterCreateRequest;
import com.openrsc.server.login.LoginExecutorProcess;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.login.PlayerSaveRequest;
import com.openrsc.server.util.ServerAwareThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class LoginExecutor implements Runnable {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private ScheduledExecutorService scheduledExecutor;

	private final Set<LoginExecutorProcess> genericRequests;

	private final Set<LoginExecutorProcess> loginRequests;

	private final Set<PlayerSaveRequest> saveRequests;

	private boolean running;

	private final Server server;

	private int loginsProcessedThisTick = 0;
	public final Server getServer() {
		return server;
	}

	public LoginExecutor(final Server server) {
		this.server = server;
		this.running = false;
		this.genericRequests = ConcurrentHashMap.newKeySet();
		this.loginRequests = ConcurrentHashMap.newKeySet();
		this.saveRequests = ConcurrentHashMap.newKeySet();
	}

	public boolean add(final LoginExecutorProcess request) {
		if (isRunning()) {
			//Separate queues for saving and logins so they can be executed and limited as appropriate.
			if (request instanceof LoginRequest || request instanceof CharacterCreateRequest) return loginRequests.add(request);
			else if (request instanceof PlayerSaveRequest) return saveRequests.add((PlayerSaveRequest) request);
			else return genericRequests.add(request);
		}
		return false;
	}

	@Override
	public void run() {
		try {
			// Save requests should be run BEFORE logout requests or else we get duplication glitch because a user can login before they've saved, but after they've logged out.
			// See Player.logout, save requests are added first before removal so we are good.

			processAndClearSet(saveRequests, request -> request.process());
			processAndClearSet(genericRequests, request -> request.process());
			processAndClearSet(loginRequests, request -> {
				if (loginsProcessedThisTick < server.getConfig().MAX_LOGINS_PER_SERVER_PER_TICK) {
					request.process();
					++loginsProcessedThisTick;
				}
			});

		} catch (final Throwable e) {
			LOGGER.catching(e);
		}
	}

	public void start() {
		clearRequests();
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
				new ServerAwareThreadFactory(
						server.getName()+" : LoginThread",
						server.getConfig()
				)
		);
		scheduledExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
		running = true;
	}

	public void stop() {
		scheduledExecutor.shutdown();
		try {
			final boolean terminationResult = scheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
			if (!terminationResult) {
				LOGGER.error("LoginExecutor thread termination failed");
				List<Runnable> skippedTasks = scheduledExecutor.shutdownNow();
				LOGGER.error("{} task(s) never commenced execution", skippedTasks.size());
			}
		} catch (final InterruptedException e) {
			LOGGER.catching(e);
		}

		scheduledExecutor = null;
		running = false;

		if (genericRequests.size() > 0 || saveRequests.size() > 0 || loginRequests.size() > 0) {
			run();
		}

		if (genericRequests.size() > 0 || saveRequests.size() > 0 || loginRequests.size() > 0) {
			LOGGER.error("There were " + (genericRequests.size() + saveRequests.size() + loginRequests.size()) + " unprocessed requests. (Very bad!!!!!!!!!!)");
			processAndClearSet(saveRequests, saveRequest -> {
				LOGGER.error("Could not save " + saveRequest.getPlayer() + " during LoginExecutor shutdown.");
			});
			clearRequests();
		}
	}

	private void clearRequests() {
		genericRequests.clear();
		saveRequests.clear();
		loginRequests.clear();
	}

	public final boolean isRunning() {
		return running;
	}

	public void resetRequestsThisTick() {
		loginsProcessedThisTick = 0;
	}

	private <T> void processAndClearSet(Set<T> set, Consumer<T> processor) {
		Iterator<T> iterator = set.iterator();
		while (iterator.hasNext()) {
			T item = iterator.next();
			processor.accept(item);
			iterator.remove();
		}
	}
}
