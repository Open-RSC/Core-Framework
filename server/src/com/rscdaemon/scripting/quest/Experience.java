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

import com.rscdaemon.scripting.Skill;

/**
 * A type of {@link QuestReward} that grants an experience bonus.
 * 
 * @author Zilent
 *
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface Experience
	extends
		QuestReward
{
	/**
	 * Retrieves the skill to grant experience to
	 * 
	 * @return the skill to grant experience to
	 * 
	 */
	Skill getSkill();
	
	/**
	 * Retrieves the amount of experience to grant
	 * 
	 * @return the amount of experience to grant
	 * 
	 */
	float getAmount();
}
