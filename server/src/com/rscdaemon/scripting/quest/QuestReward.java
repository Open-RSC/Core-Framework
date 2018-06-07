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
package com.rscdaemon.scripting.quest;

import java.io.Serializable;

import org.openrsc.server.model.Player;

/**
 * The <code>QuestReward</code> interface is the base for all types of rewards 
 * granted to a player upon the completion of a quest.  Several predefined 
 * reward types are available, but users are free to extend the functionality 
 * of the framework through the use of this interface.
 * 
 * @author Zilent
 *
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface QuestReward
	extends
		Serializable
{
	/**
	 * Fired when this <code>QuestReward</code> is granted to the provided 
	 * {@link Player}
	 * 
	 * @param recipient the <code>Player</code> who has been granted this 
	 * <code>QuestReward</code>
	 * 
	 */
	void grant(Player recipient);
}
