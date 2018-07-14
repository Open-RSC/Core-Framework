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

/**
 * The <code>QuestStage</code> interface represents a {@link Player player's} 
 * progress through a <code>Quest</code>.  Typically, this interface should 
 * be implemented through a private enumeration within the <code>Quest</code> 
 * itself.
 * 
 * @author Zilent
 *
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface QuestStage
{
	/**
	 * Retrieves the ID of this <code>QuestStage</code>
	 * 
	 * @return the ID of this <code>QuestStage</code>
	 * 
	 */
	int getID();
}
