package com.openrsc.server.plugins.triggers;


import com.openrsc.server.model.entity.player.Player;

public interface SpellInvTrigger {
	/**
	 * Called when you cast on an item
	 */
	void onSpellInv(Player p, Integer itemID, Integer spellID);
	/**
	 * Return true if you wish to prevent the cast
	 *
	 * @return
	 */
	boolean blockSpellInv(Player p, Integer itemID, Integer spellID);
}
