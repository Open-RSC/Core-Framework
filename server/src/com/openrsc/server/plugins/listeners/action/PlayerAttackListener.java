package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface PlayerAttackListener {

	public void onPlayerAttack(Player p, Player affectedmob);
}
