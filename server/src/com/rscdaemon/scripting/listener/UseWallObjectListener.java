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

import org.rscemulation.server.model.GameObject;
import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.Script;

/**
 * A type of {@link EventListener} that is invoked when a {@link Player} 
 * attempts to use a {@link GameObject}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface UseWallObjectListener
	extends
		Script
{
	/**
	 * Invoked when the provided {@link Player} attempts to use the provided 
	 * {@link GameObject}
	 * 
	 * @param player the {@link Player} that is attempting to use the object
	 * 
	 * @param object the {@link GameObject} that is being used
	 * 
	 * @param functionIndex the index of the function that is being used
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onWallObjectUsed(Player player, GameObject object, int commandIndex);

}
