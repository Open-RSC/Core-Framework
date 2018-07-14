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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * As default implementation of the {@link Quest} interface, 
 * <code>AbstractQuest</code> provides default mechanisms for many of the 
 * various <code>Quest</code> operations.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public abstract class AbstractQuest
	implements
		Quest
{

	/// Compatible with version 1.0 and up
	private final static long serialVersionUID = 5186821820595703843L;
	
	/// @serial A map to hold the variables associated with this <code>Quest</code>
	private final Map<String, Serializable> variables;
		
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void setVariable(String key, Serializable value)
	{
		variables.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <T extends Serializable> T getVariable(String key)
	{

		return (T)variables.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(Quest other)
	{
		return getID() - other.getID();
	}
	
	@Override
	public Set<Entry<String, Serializable>> getVariables()
	{
		return variables.entrySet();
	}
	
	/**
	 * Constructs an <code>AbstractQuest</code>
	 * 
	 */
	protected AbstractQuest()
	{
		this.variables = new HashMap<>();
		this.variables.put(QUEST_STAGE, QUEST_NOT_STARTED);
	}
}
