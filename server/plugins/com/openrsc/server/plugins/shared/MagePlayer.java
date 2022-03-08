package com.openrsc.server.plugins.shared;

import com.openrsc.server.constants.Spells;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.SpellPlayerTrigger;

public class MagePlayer implements SpellPlayerTrigger {
	@Override
	public void onSpellPlayer(Player player, Player affectedPlayer, Spells spellEnum) {

	}

	@Override
	public boolean blockSpellPlayer(Player player, Player affectedPlayer, Spells spellEnum) {
		return AttackPlayer.attackPrevented(player, affectedPlayer);
	}
}
