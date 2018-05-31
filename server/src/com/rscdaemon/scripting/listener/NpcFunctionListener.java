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

import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;

/**
 * A type of {@link EventListener} that is invoked when a {@link Player} 
 * attempts to use a function offered by a {@link Npc}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface NpcFunctionListener
	extends
		EventListener
{
	/**
	 * Invoked when the provided {@link Player} attempts to use a function 
	 * offered by the provided {@link Npc}
	 * 
	 * @param player the {@link Player} that is attempting to use the function
	 * 
	 * @param npc the {@link Npc} that is offering the function
	 * 
	 * @param functionIndex the index of the requested function
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onNpcCommandUsed(Player player, Npc npc, int functionIndex);
}
