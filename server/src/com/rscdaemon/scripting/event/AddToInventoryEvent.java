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

import org.rscemulation.server.entityhandling.EntityHandler;
import org.rscemulation.server.entityhandling.defs.ItemDef;
import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

/**
 * A type of {@link ChainableEvent} that adds an item to the inventory of 
 * the target player.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class AddToInventoryEvent
	extends
		ChainableEvent
{

	/// The ID of the item to add
	private final int id;
	
	/// The amount of the item to add
	private final long amount;

	/**
	 * Constructs an <code>AddToInventoryEvent</code> with the provided 
	 * target, ID, and amount with the provided delay.
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param id the ID of the item to add
	 * 
	 * @param amount the amount of the item to add
	 * 
	 * @param delay the number of milliseconds to wait before adding the item
	 * 
	 */
	public AddToInventoryEvent(Script script, int id, long amount, int delay)
	{
		super(script, delay);
		this.id = id;
		this.amount = amount;
	}
	
	/**
	 * Constructs an <code>AddToInventoryEvent</code> with the provided 
	 * target, ID, and amount with no delay.  This constructor behaves 
	 * exactly the same as:
	 * <pre>AddToInventoryEvent(owner, id, amount, 0)</pre>
	 * 
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param id the ID of the item to add
	 * 
	 * @param amount the amount of the item to add
	 * 
	 */
	public AddToInventoryEvent(Script script, int id, long amount)
	{
		this(script, id, amount, 0);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void run()
	{
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		if(amount > 1)
		{
			ItemDef def = EntityHandler.getItemDef(id);
			if(!def.isStackable())
			{
				for(int i = 0; i < amount; ++i)
				{
					owner.getInventory().add(id, 1);
				}
			}
			else
			{
				owner.getInventory().add(id, amount);
			}
		}
		else
		{
			owner.getInventory().add(id, amount);
		}
		owner.sendInventory();
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}
}
