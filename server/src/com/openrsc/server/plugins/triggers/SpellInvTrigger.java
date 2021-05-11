package com.openrsc.server.plugins.triggers;


import com.openrsc.server.constants.Spells;
import com.openrsc.server.model.entity.player.Player;

public interface SpellInvTrigger {
	/**
	 * Called when you cast on an item
	 */
	void onSpellInv(Player player, Integer invIndex, Integer itemID, Spells spellEnum);
	/**
	 * Return true if you wish to prevent the cast
	 *
	 * @return
	 */
	boolean blockSpellInv(Player player, Integer invIndex, Integer itemID, Spells spellEnum);
}
