package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface CommandExecutiveListener {
	boolean blockCommand(String cmd, String[] args, Player player);
}
