package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface CommandListener {
	void onCommand(String cmd, String[] args, Player player);
	boolean blockCommand(String cmd, String[] args, Player player);
}
