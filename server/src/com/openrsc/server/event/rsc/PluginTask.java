package com.openrsc.server.event.rsc;

import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PluginTask extends GameTickEvent implements Callable<Integer> {
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

	private volatile boolean initialized = false;
	private volatile boolean threadRunning = false;
	private volatile boolean tickCompleted = false;

	public PluginTask(final World world) {
		super(world, null, 0, null, true);
	}

	public Integer call() {
		synchronized(this) {
			try {
				setInitialized(true);
				registerPluginThread();
				final int result = action();
				unregisterPluginThread();
				return result;
			} catch (final Exception ex) {
				LOGGER.catching(ex);
				unregisterPluginThread();
				return 0;
			}
		}
	}

	public abstract int action();

	public void run() {
		setDelayTicks(0);
		notifyAll();
	}

	public synchronized void pause(final int ticks) {
		try {
			setDelayTicks(ticks);
			setThreadRunning(false);
			setTickCompleted(true);
			wait();
			setThreadRunning(true);
			setTickCompleted(false);
		} catch (final InterruptedException ex) {
			throw new PluginInterruptedException();
		}
	}

	private synchronized void registerPluginThread() {
		final String threadName = Thread.currentThread().getName();
		setThreadRunning(true);
		setTickCompleted(false);
		tasksMap.put(threadName, this);
	}

	private synchronized void unregisterPluginThread() {
		// TODO: Need a way to stop the plugin Thread
		final String threadName = Thread.currentThread().getName();
		setThreadRunning(false);
		setTickCompleted(false);
		tasksMap.remove(threadName);
	}

	public synchronized boolean isInitialized() {
		return initialized;
	}

	private synchronized void setInitialized(final boolean started) {
		initialized = started;
	}

	public synchronized boolean isThreadRunning() {
		return threadRunning;
	}

	private synchronized void setThreadRunning(boolean threadRunning) {
		this.threadRunning = threadRunning;
	}

	public synchronized boolean isTickCompleted() {
		return tickCompleted;
	}

	private synchronized void setTickCompleted(boolean tickCompleted) {
		this.tickCompleted = tickCompleted;
	}

	public class PluginInterruptedException extends RuntimeException {}
}
