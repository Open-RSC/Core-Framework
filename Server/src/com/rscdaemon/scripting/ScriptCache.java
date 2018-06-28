package com.rscdaemon.scripting;

import java.util.TreeMap;

import com.rscdaemon.scripting.listener.EventListener;


public class ScriptCache<T extends EventListener>
	extends TreeMap<Integer, T>
{
	private static final long serialVersionUID = 3408950346924015191L;

	public ScriptCache()
	{
		
	}
}
