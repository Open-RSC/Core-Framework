package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.player.Player;

public interface WineFermentTrigger {

	/**
	 * Called when a player has accumulated sufficient progress for a step in wine fermentation
	 */
	public void onWineFerment(Player player);

	/**
	 * Called when a player has accumulated sufficient progress for a step in wine fermentation
	 *
	 * @return
	 */
	boolean blockWineFerment(Player player);
}
