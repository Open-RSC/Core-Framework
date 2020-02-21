package com.openrsc.server.event.custom;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: Kenix
 */

// Send monitoring info as a game event so that it can be profiled.
public class MonitoringEvent {

	private static final Logger LOGGER	= LogManager.getLogger();
	private World world;

	public MonitoringEvent(final World world) {
		this.world = world;
	}

	public void run() {
		// Check if processing game events and state took longer than the tick

		final long ticksLate = getWorld().getServer().getTimeLate() / getWorld().getServer().getConfig().GAME_TICK;
		final boolean isServerLate = ticksLate >= 1;

		// Server fell behind, skip ticks
		if (isServerLate) {
			getWorld().getServer().skipTicks(ticksLate);
			final String message = "Can't keep up, we are " + getWorld().getServer().getTimeLate() + "ms behind; Skipping " + ticksLate + " ticks";
			final String devMessage = "Can't keep up: " +
				getWorld().getServer().getLastTickDuration() + "ms " +
				getWorld().getServer().getLastIncomingPacketsDuration() + "ms " +
				getWorld().getServer().getLastEventsDuration() + "ms " +
				getWorld().getServer().getLastGameStateDuration() + "ms " +
				getWorld().getServer().getLastOutgoingPacketsDuration() + "ms";

			// Warn logged in developers
			for (Player p : getWorld().getPlayers()) {
				if (!p.isDev()) {
					continue;
				}

				p.playerServerMessage(MessageType.QUEST, getWorld().getServer().getConfig().MESSAGE_PREFIX + devMessage);
			}

			LOGGER.warn(message);
			getWorld().getServer().getDiscordService().monitoringSendServerBehind(message);
		}
	}

	public World getWorld() { return this.world; }
}
