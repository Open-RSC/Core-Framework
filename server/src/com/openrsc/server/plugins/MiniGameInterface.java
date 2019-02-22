package com.openrsc.server.plugins;

import com.openrsc.server.model.entity.player.Player;

public interface MiniGameInterface {
	/**
	 * Returns the ID of this miniquest/minigame
	 *
	 * @return
	 */
	public int getMiniGameId();

	/**
	 * Returns the name of this miniquest/minigame
	 *
	 * @return
	 */
	public String getMiniGameName();

	/**
	 * Returns true if this miniquest/minigame is members-only
	 *
	 * @return
	 */
	public boolean isMembers();

	/**
	 * Handles rewards upon completion
	 *
	 * @param player
	 */
	public void handleReward(Player player);
}
