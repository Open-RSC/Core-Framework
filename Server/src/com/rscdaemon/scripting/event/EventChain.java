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

import com.rscdaemon.scripting.event.ChainableEvent;

/**
 * A specialized {@link ArrayDeque} implementation that allows for 
 * the cancellation of all pending <code>Events</code> in it.
 * 
 * @author Zilent
 *
 * @param <T> the type of {@link ChainableEvent} to operate with
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 * 
 */
public class EventChain<T extends ChainableEvent>
	extends
		ArrayDeque<T>
{

	private static final long serialVersionUID = -6170549967250133971L;

	/// Is this <code>EventChain</code> still active?
	private boolean active = true;
	
	/**
	 * Is this <code>EventChain</code> still active?
	 * 
	 * @return True if this <code>EventChain</code> still active, 
	 * otherwise false
	 * 
	 */
	public boolean isActive()
	{
		return active;
	}
	
	/**
	 * Cancels all pending <code>Events</code> in this <code>EventChain</code>
	 * 
	 */
	public void cancel()
	{
		active = false;
	}
}
