package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface CatGrowthTrigger {

	/**
	 * Called when a player has accumulated sufficient activity to advance kitten growth
	 */
	public void onCatGrowth(Player player);

	/**
	 * Called when a player has accumulated sufficient activity to advance kitten growth
	 *
	 * @return
	 */
	boolean blockCatGrowth(Player player);
}
