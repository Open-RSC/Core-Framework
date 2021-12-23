package com.openrsc.server.event.rsc;

import com.openrsc.server.model.action.WalkToAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PluginTickEvent extends GameTickEvent {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final PluginTask pluginTask;
	private final WalkToAction walkToAction; // Storing the Player's context walkToAction so we can cancel the plugin if the walking is cancelled
	private final String pluginName;

	public PluginTickEvent(final World world, final Mob owner, final String pluginName, final WalkToAction walkToAction, final PluginTask pluginTask) {
		super(world, owner, 0, pluginName, DuplicationStrategy.ONE_PER_MOB);
		this.walkToAction = walkToAction;
		this.pluginTask = pluginTask;
		this.getPluginTask().setPluginTickEvent(this);
		this.pluginName = pluginName;
	}

	public void run() {
		// We want to cancel this plugin event if the most recently executed walk to action is not the same as this plugin's context walk to action.
		if (walkToAction != null && walkToAction != getPlayerOwner().getLastExecutedWalkToAction()) {
			if (!getPluginTask().isInitialized()) {
				stop();
				return;
			}
		}

		// Restart the plugin thread if it has waited long enough
		synchronized(getPluginTask()) {
			//LOGGER.info(getDescriptor() + "  tick " + getWorld().getServer().getCurrentTick() + " ticking PluginTask...");
			getPluginTask().doRun();
		}

		// Wait for the plugin to get to a pause point or finish completely. This also waits for the PluginTask to start which is also intended to run plugin code on tick bounds.
		while((!getPluginTask().isInitialized() || getPluginTask().isThreadRunning()) && !getPluginTask().isComplete()) {
			try {
				//LOGGER.info(getDescriptor() + " tick " + getWorld().getServer().getCurrentTick() + " waiting for PluginTask on tick " + " (" + getPluginTask().isInitialized() + ", " + getPluginTask().isThreadRunning() + ", " + getPluginTask().isComplete() + ")");
				Thread.sleep(1);
			} catch (final InterruptedException ex) {
				LOGGER.catching(ex);
			}
		}

		//LOGGER.info(getDescriptor() + " tick " + getWorld().getServer().getCurrentTick() + " ending event tick");

		// Stop this event if the future/thread has completed.
		if (getPluginTask().isComplete()) {
			stop();
			return;
		}
	}

	public void stop() {
		super.stop();
		getPluginTask().stop();
	}

	public final PluginTask getPluginTask() {
		return pluginTask;
	}

	public String getPluginName() {
		return pluginName;
	}
}
