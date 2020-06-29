package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.ScriptContext;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.PluginInterruptedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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

	private AtomicBoolean initialized = new AtomicBoolean(false);
	private AtomicBoolean threadRunning = new AtomicBoolean(false);
	private AtomicBoolean tickCompleted = new AtomicBoolean(false);
	private volatile Thread pluginThread;

	private final ScriptContext scriptContext;
	private final Object[] data;
	private final Action action;

	public PluginTask(final World world, final Player owner, final String pluginInterface, final Object[] data) {
		super(world, owner, 0, null, true);
		this.data = data;
		this.action = Action.getActionFromPlugin(pluginInterface);
		this.scriptContext = new ScriptContext(world, this, owner != null ? owner.getIndex() : null);

		if(this.action == null) {
			throw new IllegalArgumentException("Cannot locate action from Plugin: " + pluginInterface);
		}
	}

	public synchronized Integer call() {
		try {
			setInitialized(true);
			registerPluginThread();
			final int result = action();
			stop();
			return result;
		} catch(final Exception ex) {
			LOGGER.catching(ex);
			stop();
			return 0;
		}
	}

	public abstract int action();

	public void run() {
		setDelayTicks(0);
		setThreadRunning(true);
		setTickCompleted(false);
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
			throw new PluginInterruptedException("pause() was interrupted", ex);
		}
	}

	private synchronized void registerPluginThread() {
		pluginThread = Thread.currentThread();
		final String threadName = getPluginThread().getName();
		setThreadRunning(true);
		setTickCompleted(false);
		tasksMap.put(threadName, this);
		getScriptContext().startScript(action, data);
	}

	private synchronized void unregisterPluginThread() {
		// Save the original thread to interrupt it after closing down data for the Plugin representation
		final Thread thread = getPluginThread();

		if(thread != null) {
			setThreadRunning(false);
			setTickCompleted(false);
			tasksMap.remove(thread.getName());
			pluginThread = null;
			getScriptContext().endScript();

			thread.interrupt();
		}
	}

	public boolean isInitialized() {
		return initialized.get();
	}

	private void setInitialized(final boolean started) {
		initialized.getAndSet(started);
	}

	public boolean isThreadRunning() {
		return threadRunning.get();
	}

	private void setThreadRunning(boolean threadRunning) {
		this.threadRunning.getAndSet(threadRunning);
	}

	public boolean isTickCompleted() {
		return tickCompleted.get();
	}

	private synchronized void setTickCompleted(boolean tickCompleted) {
		this.tickCompleted.getAndSet(tickCompleted);
	}

	public Thread getPluginThread() {
		return pluginThread;
	}

	public ScriptContext getScriptContext() {
		return scriptContext;
	}
}
