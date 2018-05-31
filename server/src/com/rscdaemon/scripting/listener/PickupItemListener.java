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

import org.rscemulation.server.model.Item;
import org.rscemulation.server.model.Player;

/**
 * A type of {@link EventListener} that is invoked when a {@link Player} 
 * attempts to pick up an {@link Item}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface PickupItemListener
	extends
		EventListener
{
	/**
	 * Invoked when the provided {@link Player} attempts to pick up the 
	 * provided {@link Item}
	 * 
	 * @param player the {@link Player} that is picking up the item
	 * 
	 * @param item the {@link Item} that is being picked up
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onItemPickedUp(Player player, Item item);
}
