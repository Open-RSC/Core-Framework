package com.openrsc.server.service;

import com.openrsc.server.Server;
import com.openrsc.server.util.ServerAwareThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class PcapLoggerService implements Runnable {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private volatile AtomicBoolean running;
	private final BlockingQueue<Runnable> jobs;
	private final Server server;
	private ScheduledExecutorService scheduledExecutor;

	public PcapLoggerService(final Server server) {
		this.server = server;

		running = new AtomicBoolean(false);
		jobs = new ArrayBlockingQueue<>(10000);
	}

	public final Server getServer() {
		return server;
	}

	public void start() {
		synchronized (running) {
			running.set(true);
			scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
				new ServerAwareThreadFactory(
					server.getName() + " : PcapLogging",
					server.getConfig()
				)
			);
			scheduledExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
		}
	}

	public void stop() {
		synchronized (running) {
			scheduledExecutor.shutdown();
			try {
				final boolean terminationResult = scheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
				if (!terminationResult) {
					LOGGER.error("PcapLoggerService thread termination failed");
					List<Runnable> skippedTasks = scheduledExecutor.shutdownNow();
					LOGGER.error("{} task(s) never commenced execution", skippedTasks.size());
				}
			} catch (final InterruptedException e) {
				LOGGER.catching(e);
			}
			clearJobs();
			scheduledExecutor = null;
			running.set(false);
		}
	}

	private void clearJobs() {
		jobs.clear();
	}

	@Override
	public void run() {
		synchronized (running) {
			if (running.get()) {
				while (jobs.size() > 0) {
					pollNextJob();
				}
			}
		}
	}

	protected void pollNextJob() {
		runJob(jobs.poll());
	}

	protected void runJob(final Runnable runnable) {
		if (runnable != null) {
			runnable.run();
		}
	}

	public void addJob(final Runnable runnable) {
		if (!running.get()) {
			return;
		}
		jobs.add(runnable);
	}

	public void run(final Runnable runnable) {
		runJob(runnable);
	}
}
