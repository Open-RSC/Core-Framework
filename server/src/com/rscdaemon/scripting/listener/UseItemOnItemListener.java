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

import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.Player;

/**
 * A type of {@link EventListener} that is invoked when a {@link Player} 
 * attempts to use an {@link InvItem} on another {@link InvItem}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface UseItemOnItemListener
	extends
		EventListener
{
	/**
	 * Invoked when the provided {@link Player} attempts to use the 
	 * {@link InvItem} at the provided index with the other {@link InvItem} at 
	 * the other provided index
	 * 
	 * @param player the {@link Player} that is using the items
	 * 
	 * @param usedIndex the index of the item that is being used
	 * 
	 * @param usedOnIndex the index of the other item that is being used
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onItemUsedOnItem(Player player, int usedIndex, int usedOnIndex);
}
