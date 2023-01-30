package com.openrsc.server.model.entity;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnregisterRequest {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();


	private final boolean force;
	private final String reason;
	private final Player player;

	public UnregisterRequest(final Player player, boolean force, String reason) {
		this.force = force;
		this.reason = reason;
		this.player = player;
	}

	public boolean isForced() {
		return force;
	}

	public String getReason() {
		return reason;
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * Actually unregisters the player instance from the server
	 */
	public void executeUnregisterRequest() {
		if (force || player.canLogout()) {
			player.updateCacheTimersForLogout();
			player.alertQueuedSleepwordCancelledByLogout();
			LOGGER.info("Requesting unregistration for " + player.getUsername() + ": " + reason);
			player.setUnregistering(true);
		} else {
			if (player.getUnregisterEvent() != null) {
				return;
			}
			final long startDestroy = System.currentTimeMillis();

			DelayedEvent unregisterEvent = new DelayedEvent(player.getWorld(), player, 500, "Unregister Player") {
				@Override
				public void run() {
					if (getOwner().canLogout() || (!(getOwner().inCombat() && getOwner().getDuel().isDuelActive())
						&& System.currentTimeMillis() - startDestroy > 60000)) {
						getOwner().unregister(true, reason);
					}
					running = false;
				}
			};
			player.setUnregisterEvent(unregisterEvent);
			player.getWorld().getServer().getGameEventHandler().add(unregisterEvent);
		}
	}
}
