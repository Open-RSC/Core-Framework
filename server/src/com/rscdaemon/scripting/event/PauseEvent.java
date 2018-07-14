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

import com.rscdaemon.scripting.Script;

/**
 * A type of {@link ChainableEvent} that causes a delay in the script execution
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class PauseEvent
	extends
		ChainableEvent
{
	/// The default delay (in milliseconds)
	private final static long DEFAULT_DELAY_MILLIS = 1000;

	/**
	 * Constructs a <code>PauseEvent</code> with the provided delay
	 * 
	 * @param script the {@link Script} that will be paused
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 */
	public PauseEvent(Script script, long delay)
	{
		super(script, delay);
	}
	
	/**
	 * Constructs a <code>PauseEvent</code> with a delay of 
	 * {@link PauseEvent#DEFAULT_DELAY_MILLIS} milliseconds
	 * 
	 * @param script the {@link Script} that will be paused
	 * 
	 */
	public PauseEvent(Script script)
	{
		this(script, DEFAULT_DELAY_MILLIS);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void run()
	{
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
}
