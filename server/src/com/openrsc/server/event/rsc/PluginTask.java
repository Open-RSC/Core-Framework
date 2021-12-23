package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.ScriptContext;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.PluginInterruptedException;
import com.openrsc.server.util.LogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
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
	private volatile Thread pluginThread;

	private final ScriptContext scriptContext;
	private final Object[] data;
	private final Action action;
	private PluginTickEvent pluginTickEvent;
	private Future<Integer> future = null;

	public PluginTask(final World world, final Player owner, final String pluginInterface, final Object[] data) {
		super(world, owner, 0, null, DuplicationStrategy.ONE_PER_MOB);
		this.data = data;
		this.action = Action.getActionFromPlugin(pluginInterface);
		this.scriptContext = new ScriptContext(world, this, owner != null ? owner.getIndex() : null);

		if(this.action == null) {
			throw new IllegalArgumentException("Cannot locate action from Plugin: " + pluginInterface);
		}
	}

	public synchronized Integer call() {
		try {
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
		LogUtil.populateThreadContext(scriptContext.getWorld().getServer().getConfig());
		if(!isComplete()) {
			// Submitting in run because we want to only run game code on tick bounds so we start the execution inside of a tick
			if(getFuture() == null) {
				submit();
			}

			resetCountdown();
			setThreadRunning(true);
			notifyAll();
		}
	}

	@Override
	public synchronized void stop() {
		super.stop();
		cancel(false);
		unregisterPluginThread();
	}

	public synchronized void pause(final int ticks) {
		try {
			setDelayTicks(ticks);
			setThreadRunning(false);
			//LOGGER.info(getDescriptor() + " tick " + getWorld().getServer().getCurrentTick() + " pausing script...");
			wait();
			//LOGGER.info(getDescriptor() + " tick " + getWorld().getServer().getCurrentTick() + " resuming script...");
			setThreadRunning(true);
		} catch (final InterruptedException ex) {
			throw new PluginInterruptedException("pause() was interrupted", ex);
		}
	}

	private synchronized void registerPluginThread() {
		pluginThread = Thread.currentThread();
		final String threadName = getPluginThread().getName();
		setInitialized(true);
		setThreadRunning(true);
		tasksMap.put(threadName, this);
		getScriptContext().startScript(action, data);
	}

	private synchronized void unregisterPluginThread() {
		// Save the original thread to interrupt it after closing down data for the Plugin representation
		final Thread thread = getPluginThread();

		setThreadRunning(false);
		pluginThread = null;
		getScriptContext().endScript();

		if(thread != null) {
			tasksMap.remove(thread.getName());
			thread.interrupt();
		}
	}

	public synchronized Future<Integer> submit() {
		final Future<Integer> future = getWorld().getServer().getPluginHandler().submitPluginTask(this);
		setFuture(future);
		return future;
	}

	public synchronized void cancel(final boolean mayInterruptIfRunning) {
		if (getFuture() != null && !isInitialized()) {
			getFuture().cancel(mayInterruptIfRunning);
		}
	}

	public synchronized boolean isComplete() {
		return getFuture() != null && getFuture().isDone();
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

	private void setThreadRunning(final boolean threadRunning) {
		this.threadRunning.getAndSet(threadRunning);
	}

	public Thread getPluginThread() {
		return pluginThread;
	}

	public ScriptContext getScriptContext() {
		return scriptContext;
	}

	public synchronized PluginTickEvent getPluginTickEvent() {
		return pluginTickEvent;
	}

	protected void setPluginTickEvent(final PluginTickEvent pluginTickEvent) {
		this.pluginTickEvent = pluginTickEvent;
		this.setDescriptor(getPluginTickEvent().getDescriptor() + "-Task");
	}

	private synchronized Future<Integer> getFuture() {
		return future;
	}

	private synchronized void setFuture(final Future<Integer> future) {
		this.future = future;
	}
}
