package com.openrsc.server.plugins.listeners;


import com.openrsc.server.model.entity.player.Player;

public interface PlayerMageItemListener {
	/**
	 * Called when you cast on an item
	 */
	void onPlayerMageItem(Player p, Integer itemID, Integer spellID);
	/**
	 * Return true if you wish to prevent the cast
	 *
	 * @return
	 */
	boolean blockPlayerMageItem(Player p, Integer itemID, Integer spellID);
}
