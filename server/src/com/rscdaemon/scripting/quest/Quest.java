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
import java.util.Map.Entry;
import java.util.Set;

import org.openrsc.server.model.Player;

/**
 * The <code>Quest</code> interface defines the basis for all transactions 
 * between players and the game that must retain state between gaming 
 * sessions.  <code>Quests</code> provide a means by which to retain user 
 * defined variables after a game session ends.  Several predefined variables 
 * are provided for convenience including:
 * <ul>
 * <li>QUEST_STAGE - an object representing the quest progression</li>
 * <li>QUEST_NOT_STARTED - an object representing that a quest has not 
 * been started</li>
 * <li>QUEST_FINISHED - an object representing that a quest has been 
 * complete</li>
 * </ul>
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public interface Quest
	extends
		Comparable<Quest>,
		Serializable
{
	
	/// The key used to store the stage of this <code>Quest</code>
	public final static String QUEST_STAGE = "__stage";

	/// An object that represents that a quest has not yet been started
	public final static int QUEST_NOT_STARTED = Integer.MIN_VALUE;
	
	/// The key used to store the finished state of this <code>Quest</code>
	public final static int QUEST_FINISHED = Integer.MAX_VALUE;
		
	/**
	 * Associates the provided key with the provided value.  If a value was 
	 * already set with the provided key, then it is discarded.  <strong>Keys 
	 * may not begin with the sequence "__" (double underscore), as this 
	 * sequence is reserved for internal use.
	 * 
	 * @param key the key by which to access the provided value
	 * 
	 * @param value the value to store
	 *  
	 */
	void setVariable(String key, Serializable value);

	/**
	 * Retrieves the value at the provided key
	 * 
	 * @param <T> the expected type of the value at the provided key
	 * 
	 * @param key the key to access
	 * 
	 * @return the value at the provided key, or null if no such key exists
	 * 
	 * @throws ClassCastException if the value at the provided key is not 
	 * convertible to the generic argument
	 * 
	 */
	<T extends Serializable> T getVariable(String key);
	
	Set<Entry<String, Serializable>> getVariables();

	/**
	 * Retrieves the ID of this <code>Quest</code>.  Developers should access 
	 * <code>Quests</code> by ID when necessary rather than by name.
	 * 
	 * @return the ID of this <code>Quest</code>
	 * 
	 */
	int getID();
	
	/**
	 * Retrieves the name of this <code>Quest</code>.  This is the formal 
	 * name of the <code>Quest</code> that is presented to the players.
	 * 
	 * @return the name of this <code>Quest</code>
	 * 
	 */
	String getName();
	
	/**
	 * Retrieves a list of {@link QuestReward rewards} that are issued to 
	 * a {@link Player} upon completing this <code>Quest</code>
	 * 
	 * @return a list of <code>QuestRewards</code> that are issued to a 
	 * <code>Player</code> upon completing this <code>Quest</code>
	 * 
	 */
	QuestReward[] getRewards();

	/**
	 * Retrieves the number of quest points that this <code>Quest</code> 
	 * is worth
	 * 
	 * @return the number of quest points that this <code>Quest</code> 
	 * is worth
	 * 
	 */
	int getQuestPoints();
}
