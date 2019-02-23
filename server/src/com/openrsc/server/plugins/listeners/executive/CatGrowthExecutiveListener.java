package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.entity.player.Player;

public interface CatGrowthExecutiveListener {
	/**
	 * Called when a player has accumulated sufficient activity to advance kitten growth
	 * 
	 * @return
	 */
	public boolean blockCatGrowth(Player p);
}

