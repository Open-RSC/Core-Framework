package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageItemExecutiveListener {
	/**
	 * Return true if you wish to prevent the cast
	 *
	 * @return
	 */
	boolean blockPlayerMageItem(Player p, Integer itemID, Integer spellID);
}
