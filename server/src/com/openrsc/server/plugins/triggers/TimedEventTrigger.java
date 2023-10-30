package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface TimedEventTrigger {
	void onTimedEvent(Player player);
	boolean blockTimedEvent(Player player);
}
