package com.openrsc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.login.LoginExecutorProcess;
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

	public void add(final LoginExecutorProcess request) {
		requests.add(request);
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
			scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getServer().getName()+" : LoginThread").build());
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
			clearRequests();
			scheduledExecutor = null;
			running = false;
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
