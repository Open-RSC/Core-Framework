package com.openrsc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.login.LoginExecutorProcess;
import com.openrsc.server.login.PlayerSaveRequest;
import com.openrsc.server.util.ServerAwareThreadFactory;
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

	private ScheduledExecutorService scheduledExecutor;

	private final Queue<LoginExecutorProcess> requests;

	private volatile Boolean running;

	private final Server server;
	public final Server getServer() {
		return server;
	}

	public LoginExecutor(final Server server) {
		this.server = server;
		this.running = false;
		this.requests = new ConcurrentLinkedQueue<>();
	}

	public boolean add(final LoginExecutorProcess request) {
		if (isRunning()) {
			return requests.add(request);
		}
		return false;
	}

	@Override
	public void run() {
		synchronized(running) {
			try {
				// Save requests should be run BEFORE logout requests or else we get duplication glitch because a user can login before they've saved, but after they've logged out.
				// See Player.logout, save requests are added first before removal so we are good.
				LoginExecutorProcess request;
				while ((request = requests.poll()) != null) {
					request.process();
				}
			} catch (final Throwable e) {
				LOGGER.catching(e);
			}
		}
	}

	public void start() {
		synchronized (running) {
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
	}

	public void stop() {
		synchronized (running) {
			scheduledExecutor.shutdown();
			try {
				final boolean terminationResult = scheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
				if (!terminationResult) {
					LOGGER.error("LoginExecutor thread termination failed");
				}
			} catch (final InterruptedException e) {
				LOGGER.catching(e);
			}

			scheduledExecutor = null;
			running = false;

			if (requests.size() > 0) {
				run();
			}

			if (requests.size() > 0) {
				LOGGER.error("There were " + requests.size() + " unprocessed requests. (Very bad!!!!!!!!!!)");
				LoginExecutorProcess request;
				while ((request = requests.poll()) != null) {
					final PlayerSaveRequest playerSave = (PlayerSaveRequest)request;

					if (playerSave != null) {
						LOGGER.error("Could not save " + playerSave.getPlayer() + " during LoginExecutor shutdown.");
					}
				}
				clearRequests();
			}
		}
	}

	private void clearRequests() {
		synchronized (running) {
			requests.clear();
		}
	}

	public final boolean isRunning() {
		return running;
	}
}
