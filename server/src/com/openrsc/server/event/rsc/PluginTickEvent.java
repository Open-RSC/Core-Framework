package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PluginTickEvent extends GameTickEvent {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final PluginTask pluginTask;
	private ExecutorService executor = null;
	private Future<Integer> future = null;

	public PluginTickEvent(final World world, final Mob owner, final String descriptor, final PluginTask pluginTask) {
		super(world, owner, 1, descriptor, true);
		this.pluginTask = pluginTask;
	}

	public void run() {
		// Submitting in run because we want to only run game code on tick bounds so we start the execution inside of a tick
		if(future == null) {
			executor = Executors.newSingleThreadExecutor(getWorld().getServer().getPluginHandler().getThreadFactory());
			future = executor.submit(pluginTask);
		}

		// Wait for the PluginTask to start
		while(!pluginTask.isThreadStarted()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
				LOGGER.catching(ex);
			}
		}

		synchronized(pluginTask) {
			pluginTask.tick();

			if(pluginTask.canRun() && !future.isDone()) {
				setDelayTicks(0);
				pluginTask.notifyAll();
			} else if (future.isDone()) {
				stop();
				executor.shutdown();
				return;
			}
		}
	}
}
