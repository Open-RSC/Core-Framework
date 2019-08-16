package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface CommandListener {
	public void onCommand(String cmd, String[] args, Player player);

	public void handleCommand(String cmd, String[] args, Player player);
	public boolean isCommandAllowed(Player player, String cmd);
}
