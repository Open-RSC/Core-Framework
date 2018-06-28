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

package com.rscdaemon.scripting;

import org.openrsc.server.entityhandling.defs.SpellDef;
import org.openrsc.server.model.GameObject;
import org.openrsc.server.model.Item;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.quest.Quest;

/**
 * <code>ScriptVariables</code> are special fields that are provided to 
 * <code>Script</code>s by the RSCDaemon framework.  Several variables are 
 * provided:
 * <ul>
 *  <li>OWNER - the player has has invoked this <code>Script</code></li>
 *  <li>PLAYER_TARGET - the player that is being targeted by OWNER</li>
 *  <li>NPC_TARGET - the npc that is being targeted by OWNER</li>
 *  <li>OBJECT_TARGET - the object that is being targeted by OWNER</li>
 *  <li>DOOR_TARGET - the door that is being targeted by OWNER</li>
 *  <li>GROUND_ITEM_TARGET - the item that is being targeted by OWNER</li>
 *  <li>SELECTED_ITEM - the item that OWNER has selected</li>
 *  <li>SELECTED_SPELL - the spell that OWNER has selected</li>
 *  <li>HELD_ITEM_TARGET - the item that is being targeted by OWNER</li>
 * </ul>
 * 
 * @author Zilent
 *
 * @version 1.0
 *
 * @since 3.3.0
 * 
 */
public enum ScriptVariable
{

	QUEST(Quest.class),
	PLAYER_TARGET(Player.class),
	NPC_TARGET(Npc.class),
	OBJECT_TARGET(GameObject.class),
	DOOR_TARGET(GameObject.class),
	SELECTED_ITEM(Integer.class),
	SELECTED_SPELL(SpellDef.class),
	GROUND_ITEM_TARGET(Item.class),
	HELD_ITEM_TARGET(Integer.class),
	OWNER(Player.class);
	
	/// The class of the value pointed at by this <code>ScriptVariable</code>
	private final Class<?> clazz;
	
	/**
	 * Constructs a <code>ScriptVariable</code> with the provided data type
	 * 
	 * @param clazz the data type of the expected corresponding value pointed 
	 * at by this <code>ScriptVariable</code>
	 * 
	 */
	ScriptVariable(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	/**
	 * Retrieves the expected data type of the value pointed at by this 
	 * <code>ScriptVariable</code>
	 * 
	 * @return the expected data type of the value pointed at by this 
	 * <code>ScriptVariable</code>
	 * 
	 */
	Class<?> getVariableType()
	{
		return clazz;
	}
}
