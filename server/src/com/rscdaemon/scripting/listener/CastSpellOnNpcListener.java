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

import org.openrsc.server.entityhandling.defs.SpellDef;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;

/**
 * A type of {@link EventListener} that is invoked when a {@link Player} 
 * casts a spell on a {@link Npc}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface CastSpellOnNpcListener
	extends
		EventListener
{
	/**
	 * Invoked when the provided {@link SpellDef} is casted on the provided 
	 * {@link Npc}
	 * 
	 * @param player the {@link Player} that is casting the spell
	 * 
	 * @param spell the spell that was casted
	 * 
	 * @param npc the {@link Npc} that the spell was casted on
	 * 
	 * @return true if this listener has successfully handled the event, 
	 * otherwise false
	 * 
	 */
	boolean onSpellCastedOnNpc(Player player, SpellDef spell, Npc npc);
}
