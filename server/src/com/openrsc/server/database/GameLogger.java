package com.openrsc.server.database;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.Server;
import com.openrsc.server.database.impl.mysql.MySqlGameDatabase;
import com.openrsc.server.database.impl.mysql.queries.Query;
import com.openrsc.server.database.impl.mysql.queries.ResultQuery;
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

	private volatile AtomicBoolean running;
	private final BlockingQueue<Query> queries;
	private final Server server;
	private final ScheduledExecutorService scheduledExecutor;
	private final GameDatabase database;

	public GameLogger(final Server server) {
		this.server = server;

		scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getServer().getName() + " : DatabaseLogging").build());
		running = new AtomicBoolean(false);
		queries = new ArrayBlockingQueue<>(10000);
		database = new MySqlGameDatabase(getServer());
	}

	public final Server getServer() {
		return server;
	}

	public void start() {
		synchronized (running) {
			running.set(true);
			scheduledExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
		}
	}

	public void stop() {
		synchronized (running) {
			running.set(false);
		}
	}

	@Override
	public void run() {
		synchronized (running) {
			while (running.get()) {
				if (queries.size() > 0 && getServer().getDatabase().isConnected()) {
					pollNextQuery();
				}
			}
		}
		LOGGER.info("Shutting down database thread.. executing remaining queries");
		while (queries.size() > 0 && getServer().getDatabase().isConnected()) {
			pollNextQuery();
		}
	}

	protected void pollNextQuery() {
		runQuery(queries.poll());
	}

	protected void runQuery(final Query query) {
		try {
			if (query != null) {
				if (query instanceof ResultQuery) {
					final ResultQuery rq = (ResultQuery) query;
					rq.onResult(rq.prepareStatement(getServer().getDatabase().getConnection().getConnection()).executeQuery());
				} else {
					query.prepareStatement(getServer().getDatabase().getConnection().getConnection()).execute();
				}
			}
		/*} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
		*/} catch (final SQLException ex) {
			LOGGER.catching(ex);
		}
	}

	// Runs a query on the database thread. This is mostly useful for non-critical data like game logs that we don't make further calculations on.
	public void addQuery(final Query query) {
		synchronized(running) {
			if (!running.get()) {
				return;
			}
			queries.add(query);
		}
	}

	// Runs a query on whatever program thread initiated the request. This is mostly useful for playing loading/saving to ensure data is returned.
	public void run(final Query query) {
		runQuery(query);
	}

	public GameDatabase getDatabase() {
		return database;
	}
}
