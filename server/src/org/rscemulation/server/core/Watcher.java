package org.rscemulation.server.core;

import org.rscemulation.server.model.Mob;

public interface Watcher
{
	/**
	 * Alerts this <code>Watcher</code> that an item bubble has appeared
	 * 
	 * @param playerIndex the index of the player that owns the bubble
	 * 
	 * @param itemID the ID of the item that is inside the bubble
	 * 
	 */
	void watchItemBubble(int playerIndex, int itemID);
		
	/**
	 * Alerts this <code>Watcher</code> that a <code>Mob</code> has spoken 
	 * out, but not publicly.  This method should be used when a mob is 
	 * either speaking to a specific mob, or themselves.
	 * 
	 * @param sender the mob that has spoken
	 * 
	 * @param recipient the mob that has been spoken to
	 * 
	 * @param message the message that was spoken
	 * 
	 */
	void watchChatMessage(Mob sender, Mob recipient, String message);
	
}
