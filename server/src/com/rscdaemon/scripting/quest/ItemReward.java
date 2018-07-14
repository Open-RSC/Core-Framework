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
 * A type of {@link QuestReward} that grants an item
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface ItemReward
	extends
		QuestReward
{
	/**
	 * Retrieves the ID of the item to grant
	 * 
	 * @return the ID of the item to grant
	 * 
	 */
	int getID();
	
	/**
	 * Retrieves the amount of the item to grant
	 * 
	 * @return the amount of the item to grant
	 * 
	 */
	int getAmount();
}
