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
 * A type of {@link ChainableEvent} that opens the bank account of the 
 * target {@link Player}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class OpenBankEvent
	extends
		ChainableEvent
{
	/// The default delay (in milliseconds)
	private final static long DEFAULT_DELAY_MILLIS = 0;
	
	/**
	 * Constructs an <code>OpenBankEvent</code> with the provided 
	 * {@link Player} and delay
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 */
	public OpenBankEvent(Script script, long delay)
	{
		super(script, delay);
	}
	
	/**
	 * Constructs an <code>OpenBankEvent</code> with the provided 
	 * {@link Player} and a delay of 
	 * {@link OpenBankEvent#DEFAULT_DELAY_MILLIS} milliseconds
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 */
	public OpenBankEvent(Script script)
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
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		owner.setAccessingBank(true);
		owner.showBank();
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
}
