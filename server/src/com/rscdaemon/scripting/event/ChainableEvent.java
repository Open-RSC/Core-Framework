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
package com.rscdaemon.scripting.event;

import java.util.ArrayDeque;

import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;

import com.rscdaemon.Event;
import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

/**
 * A specialized {@link Event} that is expected to be used in a chain pattern 
 * in order to allow for delayed event submission on a single thread.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public abstract class ChainableEvent
	extends
		Event
{
	/// A reference to the associated {@link Script}
	protected final Script script;
	
	/**
	 * Constructs a <code>ChainableEvent</code> with the provided delay
	 * 
	 * @param delay the number of milliseconds to wait before this event is 
	 * eligible to be processed
	 * 
	 */
	public ChainableEvent(Script script, long delay)
	{
		super(delay);
		this.script = script;
	}
	
	/**
	 * This method should be called after an event has finished in order to:
	 * <ul>
	 * <li>Schedule the submission of the next link if one exists</li>
	 * <li>Perform cleanup operations if this is the last link</li>
	 * </ul>
	 * 
	 */
	protected void scheduleNext()
	{
		/// Nothing left in the current scope, so let's pop the stack
		ArrayDeque<ChainableEvent> scope = script.__internal_get_scope();
		while(scope != null && scope.isEmpty())
		{
			script.__internal_pop();
			scope = script.__internal_get_scope();
		}
		if((!script.__internal_is_cancelled() || !isCancellable()) && scope != null && !scope.isEmpty())
		{
			World.getEventQueue().offer(scope.pollFirst());
		}
		else
		{
			((Player)script.__internal_get_variable(ScriptVariable.OWNER)).setScript(null);
			script.__internal_unbind_all();
		}
	}
	
	protected abstract boolean isCancellable();

	public Script getScript()
	{
		return script;
	}
}
