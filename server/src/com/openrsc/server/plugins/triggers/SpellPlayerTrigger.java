package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface SpellPlayerTrigger {
	/**
	 * Called when you mage a Player
	 */
	void onSpellPlayer(Player player, Player affectedPlayer, Integer spell);
	/**
	 * Return true if you wish to prevent the cast
	 *
	 * @return
	 */
	boolean blockSpellPlayer(Player player, Player affectedPlayer, Integer spell);
}
