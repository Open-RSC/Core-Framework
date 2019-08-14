package com.openrsc.server.sql;

import com.openrsc.server.Server;
import com.openrsc.server.sql.query.Query;
import com.openrsc.server.sql.query.ResultQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GameLogger implements Runnable {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final BlockingQueue<Query> queries;
	private final AtomicBoolean running;
	private final Object lock;
	private final Thread thread;
	private final Server server;

	public GameLogger(Server server) {
		this.server = server;

		thread = new Thread(this, getServer().getName()+" : DatabaseLogging");
		lock = new Object();
		running = new AtomicBoolean(false);
		queries = new ArrayBlockingQueue<Query>(10000);
	}

	public final Server getServer() {
		return server;
	}

	public void start() {
		synchronized(running) {
			running.set(true);
			thread.start();
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
				if (queries.size() > 0 && DatabaseConnection.getDatabase().isConnected()) {
					pollNextQuery();
				}
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		LOGGER.info("Shutting down database thread.. executing remaining queries");
		while (queries.size() > 0 && DatabaseConnection.getDatabase().isConnected()) {
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
						rq.onResult(rq.prepareStatement(DatabaseConnection.getDatabase().getConnection()).executeQuery());
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
				} else {
					log.prepareStatement(DatabaseConnection.getDatabase().getConnection()).execute();
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
