package com.openrsc.server.sql;

import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.sql.query.Query;
import com.openrsc.server.sql.query.ResultQuery;

public final class GameLogging implements Runnable {
	
	/**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

	private final static BlockingQueue<Query> queries = new ArrayBlockingQueue<Query>(10000);

	private final static AtomicBoolean running = new AtomicBoolean(true);

	private static GameLogging singleton;

	private final Thread thread = new Thread(this, "Database logging thread");

	private final static Object lock = new Object();
	private static DatabaseConnection loggingConnection;

	public static GameLogging singleton() {
		return singleton;
	}

	public static void load() {
		singleton = new GameLogging();
		singleton.start();
	}

	public synchronized void start() {
		loggingConnection = new DatabaseConnection("game-logging");
		running.set(true);
		thread.start();
	}

	public synchronized void shutdown() {
		running.set(false);
	}

	@Override
	public synchronized void run() {
		while (running.get()) {
			if (queries.size() > 0 && loggingConnection.isConnected()) {
				try {
					Query log = queries.poll();
					if (log != null) {
						if (log instanceof ResultQuery) {
							ResultQuery rq = (ResultQuery) log;
							try {
								rq.onResult(rq.prepareStatement(loggingConnection.getConnection()).executeQuery());
							} catch (SQLException e) {
								LOGGER.catching(e);
							}
						} else {
							log.prepareStatement(loggingConnection.getConnection()).execute();
						}
					}

				} catch (Exception e) {
					LOGGER.catching(e);
				}
			} else {
				while (queries.size() <= 0) {
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							LOGGER.catching(e);
						}
					}
				}
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		LOGGER.info("Shutting down database thread.. executing remaining queries");
		while (queries.size() < 1 && loggingConnection.isConnected()) {
			try {
				Query log = queries.poll();
				if (log != null) {
					if (log instanceof ResultQuery) {
						ResultQuery rq = (ResultQuery) log;
						try {
							rq.onResult(rq.prepareStatement(loggingConnection.getConnection()).executeQuery());
						} catch (SQLException e) {
							LOGGER.catching(e);
						}
					} else {
						log.prepareStatement(loggingConnection.getConnection()).execute();
					}
				}
			} catch (Exception e) {
				LOGGER.catching(e);
			}
		}
	}

	public static void addQuery(Query log) {
		if (!running.get()) {
			return;
		}
		queries.add(log);
		synchronized (lock) {
			lock.notifyAll();
		}
	}

}