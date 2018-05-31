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

import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

/**
 * A type of {@link ChainableEvent} that removes an item from the inventory 
 * of the target {@link Player}
 * 
 * @author Zilent
 *
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class RemoveFromInventoryEvent
	extends
		ChainableEvent
{
	
	/// The default delay (in milliseconds)
	private final static long DEFAULT_DELAY_MILLIS = 0;
		
	/// The ID of the item to remove
	private final int id;
	
	/// The amount of the item to remove
	private final long amount;

	/**
	 * Constructs a <code>RemoveFromInventoryEvent</code> with the provided 
	 * {@link Player}, item ID, item amount, and delay
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param id the ID of the item to remove
	 * 
	 * @param amount the amount of the item to remove
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 */
	public RemoveFromInventoryEvent(Script script, int id, long amount, long delay)
	{
		super(script, delay);
		this.id = id;
		this.amount = amount;
	}
	
	/**
	 * Constructs a <code>RemoveFromInventoryEvent</code> with the provided 
	 * {@link Player, item ID, and item amount with a delay of 
	 * {@link RemoveFromInventoryEvent#DEFAULT_DELAY_MILLIS} milliseconds
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param id the ID of the item to remove
	 * 
	 * @param amount the amount of the item to remove
	 */
	public RemoveFromInventoryEvent(Script script, int id, long amount)
	{
		this(script, id, amount, DEFAULT_DELAY_MILLIS);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void run()
	{
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		owner.getInventory().remove(id, amount);
		owner.sendInventory();
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}
}
