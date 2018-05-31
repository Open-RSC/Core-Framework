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

import java.util.UUID;

/**
 * A type of {@link QuestReward} that grants a privileged permission.
 * 
 * @author Zilent
 *
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface Permission
	extends
		QuestReward
{
	/**
	 * Retrieves the {@link UUID of this <code>Permission</code>
	 * 
	 * @return the <code>UUID</code> of this <code>Permission</code>
	 * 
	 */
	UUID getUID();
}
