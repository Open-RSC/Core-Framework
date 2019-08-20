package com.openrsc.server.sql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.Server;
import com.openrsc.server.sql.query.Query;
import com.openrsc.server.sql.query.ResultQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GameLogger implements Runnable {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final BlockingQueue<Query> queries;
	private final AtomicBoolean running;
	private final Server server;
	private final ScheduledExecutorService scheduledExecutor;

	public GameLogger(Server server) {
		this.server = server;

		scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getServer().getName()+" : DatabaseLogging").build());
		running = new AtomicBoolean(false);
		queries = new ArrayBlockingQueue<Query>(10000);
	}

	public final Server getServer() {
		return server;
	}

	public void start() {
		synchronized(running) {
			running.set(true);
			scheduledExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
		}
	}

	public void stop() {
		synchronized(running) {
			running.set(false);
		}
	}

	@Override
	public void run() {
		while (running.get()) {
			synchronized(running) {
				if (queries.size() > 0 && getServer().getDatabaseConnection().isConnected()) {
					pollNextQuery();
				}
			}
		}
		LOGGER.info("Shutting down database thread.. executing remaining queries");
		while (queries.size() > 0 && getServer().getDatabaseConnection().isConnected()) {
			pollNextQuery();
		}
	}

	protected void pollNextQuery() {
		try {
			Query log = queries.poll();
			if (log != null) {
				if (log instanceof ResultQuery) {
					ResultQuery rq = (ResultQuery) log;
					try {
						rq.onResult(rq.prepareStatement(getServer().getDatabaseConnection().getConnection()).executeQuery());
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
				} else {
					log.prepareStatement(getServer().getDatabaseConnection().getConnection()).execute();
				}
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public void addQuery(Query log) {
		if (!running.get()) {
			return;
		}
		queries.add(log);
	}
}
