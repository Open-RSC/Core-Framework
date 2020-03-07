package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface CommandTrigger {
	void onCommand(String cmd, String[] args, Player player);
	boolean blockCommand(String cmd, String[] args, Player player);
}
