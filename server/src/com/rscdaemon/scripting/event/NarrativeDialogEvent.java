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

import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

/**
 * A type of {@link ChainableEvent} that presents a narrative message to the 
 * target {@link Player}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class NarrativeDialogEvent
	extends
		ChainableEvent
{
	/// The default delay (in milliseconds)
	private final static long DEFAULT_DELAY_MILLIS = 500;
	
	/// The message to send to the target {@link Player}
	private final String message;
	
	/**
	 * Constructs a <code>NarrativeDialogEvent</code> with the provided 
	 * {@link Player}, message, and delay
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param message the message to send to the target {@link Player}
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 */
	public NarrativeDialogEvent(Script script, String message, long delay)
	{
		super(script, delay);
		this.message = message;
	}
	
	/**
	 * Constructs a <code>NarrativeDialogEvent</code> with the provided 
	 * {@link Player}, message, with a delay of 
	 * {@link NarrativeDialogEvent#DEFAULT_DELAY_MILLIS} milliseconds
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param message the message to send to the target {@link Player}
	 * 
	 */
	public NarrativeDialogEvent(Script script, String message)
	{
		this(script, message, DEFAULT_DELAY_MILLIS);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void run()
	{
		((Player)super.script.__internal_get_variable(ScriptVariable.OWNER)).sendMessage(message);
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
}
