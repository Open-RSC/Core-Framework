package com.openrsc.server.plugins.shared;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.PlayerRangePlayerTrigger;

public class RangePlayer implements PlayerRangePlayerTrigger {
	@Override
	public void onPlayerRangePlayer(Player player, Player affectedMob) {
	}

	@Override
	public boolean blockPlayerRangePlayer(Player player, Player affectedMob) {
		return AttackPlayer.attackPrevented(player, affectedMob);
	}
}
