package com.openrsc.server.event.custom;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DailyShutdownEvent extends DailyEvent  {
	private static final Logger LOGGER = LogManager.getLogger();
	private int actionedResets;
	private final String eventMessage;

	public DailyShutdownEvent(final World world, final int lifeTime) {
		this(world, lifeTime, 0, null);
	}

	public DailyShutdownEvent(final World world, final int lifeTime, final int hour) {
		this(world, lifeTime, hour, null);
	}

	private DailyShutdownEvent(final World world, final int lifeTime, final int hour, final String eventMessage) {
		super(world, lifeTime, hour,"Daily Shutdown Event");
		this.eventMessage = eventMessage;
	}

	public void action() {
		LOGGER.info("Server shutdown (closeProcess) requested by DailyShutdownEvent");
		getWorld().getServer().closeProcess(60, "Daily Reboot");
		//getWorld().getServer().shutdown(300);

		for (final Player p : getWorld().getPlayers()) {
			if(!p.isAdmin()) {
				continue;
			}
			if (getWorld().getServer().getConfig().DEBUG)
				p.playerServerMessage(MessageType.QUEST, getWorld().getServer().getConfig().MESSAGE_PREFIX + "Automatic server shutdown triggered");
		}
	}

	public static boolean isOccurring(Player player) {
		for (GameTickEvent event : player.getWorld().getServer().getGameEventHandler().getEvents()) {
			if (!(event instanceof DailyShutdownEvent)) continue;
			return true;
		}
		return false;
	}

	private String getEventMessage() {
		return eventMessage;
	}
}
