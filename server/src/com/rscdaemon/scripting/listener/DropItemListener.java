/*
 * Copyright (C) RSCDaemon - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCDaemon Team <dev@rscdaemon.com>, Unknown Date
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package com.rscdaemon.scripting.listener;

import org.openrsc.server.model.Player;

/**
 * A type of {@link EventListener} that is invoked when an item is dropped 
 * by a {@link Player}
 * 
 * @author Zilent
 *
 */
public interface DropItemListener
	extends
		EventListener
{
	
	/**
	 * Invoked when the provided {@link Player} drops the item at the 
	 * provided index
	 * 
	 * @param player the player that is dropping the item
	 * 
	 * @param index the index of the item that is being dropped
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onDropItem(Player player, int index);
}
