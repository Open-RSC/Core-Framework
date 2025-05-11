package com.openrsc.server.model.entity;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.entity.UnregisterForcefulness;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnregisterRequest {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();


	private final UnregisterForcefulness force;
	private final String reason;
	private final Player player;

	public UnregisterRequest(final Player player, UnregisterForcefulness force, String reason) {
		this.force = force;
		this.reason = reason;
		this.player = player;
	}

	public UnregisterForcefulness howForced() {
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
		if (force == UnregisterForcefulness.FORCED || player.canLogout()) {
			player.updateCacheTimersForLogout();
			player.alertQueuedSleepwordCancelledByLogout();
			LOGGER.info("Requesting unregistration for " + player.getUsername() + ": " + reason);
			player.setUnregistering(true);
		} else {
			if (force == UnregisterForcefulness.WAIT_UNTIL_COMBAT_ENDS) {
				if (player.getUnregisterEvent() != null) {
					return;
				}
				final long startDestroy = System.currentTimeMillis();

				DelayedEvent unregisterEvent = new DelayedEvent(player.getWorld(), player, 500, "Unregister Player") {
					@Override
					public void run() {
						if (getOwner().canLogout() ||
							(!(getOwner().inCombat() && getOwner().getDuel().isDuelActive()) && System.currentTimeMillis() - startDestroy > 60000) || // not in duel and unable to log out for 1 minute
							(System.currentTimeMillis() - startDestroy > 600000) // after 10 minutes the duel has been going on for too long and we will just end it, sorry folks
							) {
							getOwner().unregister(UnregisterForcefulness.FORCED, reason);
							running = false;
						}
					}
				};
				player.setUnregisterEvent(unregisterEvent);
				player.getWorld().getServer().getGameEventHandler().add(unregisterEvent);
			} else if (force == UnregisterForcefulness.FAIL_IN_COMBAT) {
				if (player.getClientVersion() >= 115) {
					ActionSender.sendCantLogout(player);
				}
			}
		}
	}
}
