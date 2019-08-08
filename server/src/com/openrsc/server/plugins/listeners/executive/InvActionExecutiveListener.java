package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface InvActionExecutiveListener {
	/**
	 * Return true to prevent inventory action
	 */
	public boolean blockInvAction(Item item, Player player, String command);
}
