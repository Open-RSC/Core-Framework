package com.openrsc.server.plugins;

import com.openrsc.server.model.entity.player.Player;

public interface QuestInterface {
	/**
	 * Returns the ID of this quest
	 *
	 * @return
	 */
	public int getQuestId();

	/**
	 * Returns the name of this quest
	 *
	 * @return
	 */
	public String getQuestName();

	/**
	 * Returns true if this quest is a members-only quest.
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
