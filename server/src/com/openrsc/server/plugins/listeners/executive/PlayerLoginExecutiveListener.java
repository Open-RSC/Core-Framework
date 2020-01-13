package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerLoginExecutiveListener {
	boolean blockPlayerLogin(Player player);
}
