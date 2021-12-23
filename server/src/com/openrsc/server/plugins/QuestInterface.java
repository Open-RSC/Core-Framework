package com.openrsc.server.plugins;

import com.openrsc.server.model.entity.player.Player;

public interface QuestInterface {
	/**
	 * Returns the ID of this quest
	 *
	 * @return
	 */
	int getQuestId();

	/**
	 * Returns the name of this quest
	 *
	 * @return
	 */
	String getQuestName();

	/**
	 * Returns the quest points of this quest
	 *
	 * @return
	 */
	int getQuestPoints();

	/**
	 * Returns true if this quest is a members-only quest.
	 *
	 * @return
	 */
	boolean isMembers();

	/**
	 * Handles rewards upon completion
	 *
	 * @param player
	 */
	void handleReward(Player player);
}
