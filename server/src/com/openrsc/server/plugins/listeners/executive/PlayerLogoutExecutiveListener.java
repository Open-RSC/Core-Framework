package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerLogoutExecutiveListener {
	public boolean blockPlayerLogout(Player player);
}
