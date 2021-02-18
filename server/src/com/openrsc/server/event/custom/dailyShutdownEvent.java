package com.openrsc.server.event.custom;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

import java.time.LocalDateTime;

import static com.openrsc.server.plugins.Functions.changeloc;

public class ShutdownEvent extends DelayedEvent {

	public ShutdownEvent(World world, int delay) {
		super(world, null, delay, "Shutdown event");
	}

	public static boolean isOccurring(Player player) {
		for (GameTickEvent event : player.getWorld().getServer().getGameEventHandler().getEvents().values()) {
			if (!(event instanceof ShutdownEvent)) continue;
			return true;
		}
		return false;
	}

	public void run() {
		if (getWorld().getServer().getConfig().WANT_AUTO_SERVER_SHUTDOWN) {
			int hour = LocalDateTime.now().getHour();
			int minute = LocalDateTime.now().getMinute();
			if (hour == getWorld().getServer().getConfig().RESTART_HOUR && minute == 0)
				getWorld().getServer().shutdown(300);
		}
	}
}
