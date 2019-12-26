package com.openrsc.server.event.rsc;

import com.openrsc.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PluginTask implements Callable<Integer> {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * List of PluginTasks indexed by thread to retrieve from a static context
	 */
	private final static ConcurrentHashMap<String, PluginTask> tasksMap = new ConcurrentHashMap<String, PluginTask>();

	public static PluginTask getContextPluginTask() {
		return PluginTask.tasksMap.get(Thread.currentThread().getName());
	}

	private final Server server;
	private int delayTicks = 0;
	private int ticksBeforeRun = 0;
	private volatile boolean threadIsStarted = false;

	public PluginTask(final Server server) {
		this.server = server;
	}

	public Integer call() {
		synchronized(this) {
			final String threadName = Thread.currentThread().getName();

			try {
				setStarted();
				tasksMap.put(threadName, this);
				final int result = action();
				tasksMap.remove(threadName);
				return result;
			} catch (final Exception ex) {
				LOGGER.catching(ex);
				tasksMap.remove(threadName);
				return 0;
			}
		}
	}

	public abstract int action();

	public synchronized void resetCountdown() {
		ticksBeforeRun = delayTicks;
	}

	public synchronized void setDelayTicks(int delayTicks) {
		resetCountdown();
		this.ticksBeforeRun = this.delayTicks = delayTicks;
	}

	public synchronized void tick() {
		ticksBeforeRun--;
	}

	public synchronized boolean canRun() {
		return ticksBeforeRun <= 0;
	}

	private synchronized void setStarted() {
		threadIsStarted = true;
	}

	public synchronized int getDelayTicks() {
		return delayTicks;
	}

	public synchronized boolean isThreadStarted() {
		return threadIsStarted;
	}

	public final Server getServer() {
		return server;
	}
}
