package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface CommandTrigger {
	void onCommand(Player player, String cmd, String[] args);
	boolean blockCommand(Player player, String cmd, String[] args);
}
