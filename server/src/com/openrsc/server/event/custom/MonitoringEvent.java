package com.openrsc.server.event.custom;

import com.openrsc.server.Server;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Author: Kenix
 */

// Send monitoring info as a game event so that it can be profiled.
public class MonitoringEvent extends GameTickEvent {
	private static final Logger LOGGER	= LogManager.getLogger();

	public MonitoringEvent() {
		super(null, 0, "Server Monitoring");
	}

	public void run() {
		// Processing game events and state took longer than the tick
		if(Server.getServer().getLastTickDuration() >= Server.getServer().getConfig().GAME_TICK) {
			final String message = "Can't keep up: " +
				Server.getServer().getLastTickDuration() + "ms " +
				Server.getServer().getLastIncomingPacketsDuration() + "ms " +
				Server.getServer().getLastEventsDuration() + "ms " +
				Server.getServer().getLastGameStateDuration() + "ms " +
				Server.getServer().getLastOutgoingPacketsDuration() + "ms";

			// Warn logged in developers
			for (Player p : World.getWorld().getPlayers()) {
				if (!p.isDev()) {
					continue;
				}

				p.playerServerMessage(MessageType.QUEST, Server.getServer().getConfig().MESSAGE_PREFIX + message);
			}
		}

		final long ticksLate = Server.getServer().getTimeLate() / Server.getServer().getConfig().GAME_TICK;
		final boolean isServerLate = ticksLate >= 1;

		// Server fell behind, skip ticks
		if (isServerLate) {
			Server.getServer().skipTicks(ticksLate);
			final String message = "Can't keep up, we are " + Server.getServer().getTimeLate() + "ms behind; Skipping " + ticksLate + " ticks";

			// Send monitoring info as a game event so that it can be profiled.
			Server.getServer().getGameEventHandler().add(new GameTickEvent(null, 0, "Server Fell Behind") {
				@Override
				public void run() {
					// Warn logged in developers
					for (Player p : World.getWorld().getPlayers()) {
						if (!p.isDev()) {
							continue;
						}

						p.playerServerMessage(MessageType.QUEST, Server.getServer().getConfig().MESSAGE_PREFIX + message);
					}

					Server.getServer().getDiscordService().monitoringSendServerBehind(message);

					stop();
				}
			});

			if (Server.getServer().getConfig().DEBUG) {
				LOGGER.warn(message);
			}
		}
	}
}
