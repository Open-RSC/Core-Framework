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

import org.rscemulation.server.model.Mob;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;

/**
 * A type of {@link EventListener} that is invoked when a {@link Player} 
 * attacks a {@link Npc}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface AttackNpcListener
	extends
		EventListener
{
	/**
	 * Invoked when the provided {@link Player} is attacking the provided 
	 * {@link Mob}
	 * 
	 * @param attacker the {@link Player} that is attacking
	 * 
	 * @param defender the {@link Npc} that is being attacked
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onAttackNpc(Player attacker, Npc defender);
}
