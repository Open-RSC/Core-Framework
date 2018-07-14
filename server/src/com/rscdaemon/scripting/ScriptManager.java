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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rscdaemon.scripting.listener.EventListener;
import com.rscdaemon.scripting.util.RecursiveClassLoader;

/**
 * A front-end for the scripting system, the <code>ScriptManager</code> 
 * provides a means of loading and releasing external resources.  All 
 * operations within this class make the strong exception guarantee such 
 * that the manager is guaranteed to retain its pre-called state in the 
 * event of an exception being thrown by a mutative method.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public final class ScriptManager
{
	abstract class Predicate
	{
		protected Object[] args;
		
		protected Predicate(Object... args)
		{
			this.args = args;
		}
		
		protected abstract void eval(Object... args);
	}
	
	/// A mapping of listeners to lists of scripts which handle events
	private final Map<Class<? extends EventListener>, 
					  List<Script>> eventListeners;

	/**
	 * Registers the provided {@link EventListener} with this 
	 * <code>ScriptManager</code>.  All currently loaded 
	 * scripts are added to the new listener category if 
	 * they are eligible (read: implement the required interface).
	 * 
	 * @param listener the new <code>EventListener</code> to add
	 * 
	 * @return a list of scripts created from currently loaded scripts
	 * 
	 */
	private List<Script> registerEventListener(
									Class<? extends EventListener> listener)
	{
		ArrayList<Script> scripts = new ArrayList<>();
		for(List<Script> scriptGroup : eventListeners.values())
		{
			for(Script script : scriptGroup)
			{
				if(script.getClass().isAssignableFrom(listener) && 
											!scripts.contains(script))
				{
					scripts.add(script);
				}
			}
		}
		return scripts;
	}
	
	/**
	 * 
	 * Attempts to load all existing listeners in the provided package.
	 * <br><br>
	 * It is worth noting that the new listener groups will be populated 
	 * with the scripts that are currently loaded if they implement the 
	 * newly loaded interface.
	 * 
	 * @param packageName the package to load the new listeners from
	 * 
	 * @throws ScriptError if any error occurs during the loading process
	 * 
	 */
	public void loadListeners(String packageName)
		throws
			ScriptError
	{
		Map<Class<? extends EventListener>, List<Script>> temp = 
				new HashMap<>(eventListeners);
		try
		{
			for(Class<? extends EventListener> listener : 
				RecursiveClassLoader.findClasses(packageName, 
														EventListener.class))
			{
				temp.put(listener, registerEventListener(listener));
			}
		}
		catch (Throwable t)
		{
			throw (ScriptError)new ScriptError(null, 
					"Unable to load listeners from \"" + packageName + 
					"\": " + t.getMessage()).initCause(t);
		}
		eventListeners.putAll(temp);
	}

	/**
	 * Attempts to load all existing scripts in the provided package.
	 * 
	 * @param packageName the package to load scripts from
	 * 
	 * @throws ScriptError if any error occurs during the loading process
	 * 
	 */
	public void loadScripts(String packageName)
		throws
			ScriptError
	{
		Map<Class<? extends EventListener>, List<Script>> temp =
				new HashMap<>(eventListeners);
		try
		{
			for(Class<? extends Script> c : 
				RecursiveClassLoader.findClasses(packageName, Script.class))
			{
				Script instance = c.newInstance();
				for(Entry<Class<? extends EventListener>, 
						List<Script>> entry : temp.entrySet())
				{
					if(entry.getKey().isAssignableFrom(c))
					{
						entry.getValue().add(instance);
					}
				}
			}
		}
		catch(Throwable t)
		{
			throw (ScriptError)new ScriptError(null, 
					"Unable to load scripts from \"" + packageName + 
					"\": " + t.getMessage()).initCause(t);			
		}
		eventListeners.putAll(temp);
	}

	/**
	 * 
	 * Attempts to add the provided listener
	 * <br><br>
	 * It is worth noting that the new listener group will be populated 
	 * with the scripts that are currently loaded if they implement the 
	 * newly loaded interface.
	 * 
	 * @param listener the new <code>EventListener</code> type to add 
	 * 
	 * @throws ScriptError if any error occurs while adding
	 * 
	 */
	public void addListener(Class<? extends EventListener> listener)
		throws
			ScriptError
	{
		eventListeners.put(listener, registerEventListener(listener));
	}
	
	/**
	 * Attempts to add the provided script to this 
	 * <code>ScriptManager</code>
	 * 
	 * @param script the script to add
	 * 
	 * @throws ScriptError if any error occurs while adding
	 * 
	 */
	public void addScript(Script script)
	{
		for(Entry<Class<? extends EventListener>, List<Script>> e : 
													eventListeners.entrySet())
		{
			if(e.getKey().isAssignableFrom(script.getClass()))
			{
				e.getValue().add(script);
			}
		}
	}
	
	/**
	 * Attempts to remove the provided listener (along with all of the 
	 * scripting functionality that it provides) from this 
	 * <code>ScriptManager</code>
	 * 
	 * @param listener the <code>EventListener</code> to remove
	 * 
	 * @throws ScriptError if any error occurs while removing
	 * 
	 */
	public void removeListener(Class<? extends EventListener> listener)
		throws
			ScriptError
	{
		eventListeners.remove(listener);
	}
	
	/**
	 * Attempts to remove the provided script from this 
	 * <code>ScriptManager</code>
	 * 
	 * @param script the script to remove
	 * 
	 * @throws ScriptError if any error occurs while removing
	 * 
	 */
	public void removeScript(EventListener script)
		throws
			ScriptError
	{
		for(Entry<Class<? extends EventListener>, List<Script>> e : 
													eventListeners.entrySet())
		{
			if(!script.getClass().isAssignableFrom(e.getKey()))
			{
				continue;
			}
			e.getValue().remove(script);
		}
	}
	
	/**
	 * Retrieves the 
	 * @param listenerType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends EventListener> List<T> 
					getListeners(Class<? extends EventListener> listenerType)
	{
		return (List<T>)eventListeners.get(listenerType);
	}
	
	/**
	 * Constructs a <code>ScriptManager</code>
	 */
	public ScriptManager()
		throws
			ScriptError
	{
		eventListeners = new HashMap<>();
	}
}
