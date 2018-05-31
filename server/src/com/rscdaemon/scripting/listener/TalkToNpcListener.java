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

import com.rscdaemon.scripting.Script;

/**
 * A type of {@link EventListener} that is invoked when a {@link Player} 
 * initiates a conversation with a {@link Npc}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface TalkToNpcListener
	extends
		Script
{

	/**
	 * Invoked when the provided {@link Player} initiates a conversation 
	 * with the provided {@link Npc}
	 * 
	 * @param player the {@link Player} that has initiated the conversation
	 * 
	 * @param npc the {@link Npc} that has been drawn into the conversation
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onTalkToNpc(Player player, Npc npc);
	
}
