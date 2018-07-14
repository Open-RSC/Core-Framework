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

import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;

/**
 * A type of {@link EventListener} that is invoked when a {@link Npc} is 
 * killed by another {@link Mob}
 * 
 * @author Zilent
 *
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface KillNpcListener
	extends
		EventListener
{
	/**
	 * Invoked when the provided {@link Npc} is killed by the provided 
	 * {@link Mob}
	 * 
	 * @param killed the {@link Npc} that was killed
	 * 
	 * @param killer the {@link Mob} that has killed the provided {@link Npc}
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onNpcKilled(Npc killed, Mob killer);
}
