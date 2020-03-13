package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.player.Player;
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
	private volatile Thread pluginThread;
	private volatile int ownerIndex;

	public PluginTask(final World world, final Player owner) {
		super(world, owner, 0, null, true);
		ownerIndex = owner == null ? -1 : owner.getIndex();
	}

	public Integer call() {
		synchronized(this) {
			pluginThread = Thread.currentThread();

			try {
				setInitialized(true);
				registerPluginThread();
				final int result = action();
				stop();
				return result;
			} catch(final PluginInterruptedException ex) {
				stop();
				return 1;
			} catch(final Exception ex) {
				LOGGER.catching(ex);
				stop();
				return 0;
			}
		}
	}

	public abstract int action();

	public void run() {
		setDelayTicks(0);
		notifyAll();
	}

	@Override
	public synchronized void stop() {
		super.stop();
		unregisterPluginThread();
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
		final String threadName = getPluginThread().getName();
		setThreadRunning(true);
		setTickCompleted(false);
		tasksMap.put(threadName, this);
	}

	private synchronized void unregisterPluginThread() {
		final String threadName = getPluginThread().getName();
		setThreadRunning(false);
		setTickCompleted(false);
		tasksMap.remove(threadName);
		getPluginThread().interrupt();
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

	public synchronized Thread getPluginThread() {
		return pluginThread;
	}

	public class PluginInterruptedException extends RuntimeException {}
}
