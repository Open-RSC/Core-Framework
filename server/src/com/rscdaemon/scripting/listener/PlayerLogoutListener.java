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
 * A type of {@link EventListener} that is invoked when a {@link Player} 
 * signs out of a game session
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface PlayerLogoutListener
	extends
		EventListener
{
	/**
	 * Invoked when the provided {@link Player} signs out of a game session
	 * 
	 * @param player the {@link Player} that has signed out
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onPlayerLogout(Player player);
}
