package com.openrsc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.login.LoginExecutorProcess;
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

	private final Queue<LoginExecutorProcess> requests;

	private final PlayerDatabase playerDatabase;

	private Boolean running;

	private final Server server;
	public final Server getServer() {
		return server;
	}

	public LoginExecutor(Server server) {
		this.server = server;
		playerDatabase = new PlayerDatabase(getServer());
		running = false;
		loginThreadExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getServer().getName()+" : LoginThread").build());
		requests = new ConcurrentLinkedQueue<LoginExecutorProcess>();
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
			} catch (Throwable e) {
				LOGGER.catching(e);
			}
		}
	}

	public PlayerDatabase getPlayerDatabase() {
		return playerDatabase;
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
