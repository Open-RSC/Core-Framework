package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.entity.player.Player;

public interface QuestDecoy {

	/**
	 * Called when a player has accumulated sufficient activity to advance kitten growth
	 */
	public void questDecoy(Player p);
}
