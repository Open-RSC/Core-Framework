package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface UnWieldExecutiveListener {
	/**
	 * Return true if you wish to prevent a user from unwielding an item
	 */
	boolean blockUnWield(Player player, Item item, Boolean sound, Boolean fromBank);
}
