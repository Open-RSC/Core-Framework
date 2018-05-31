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

import java.awt.Rectangle;

import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.util.DataConversions;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

/**
 * A type of {@link ChainableEvent} that locates and binds a {@link Npc} to 
 * the {@link Script} of the provided {@link Player}.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class BindLocalNpcEvent
	extends
		ChainableEvent
{
	
	/// The ID of the {@link Npc} to locate and bind
	private final int id;
	
	/// The {@link BoundingBox} to search in
	private final Rectangle rectangle;

	/**
	 * Constructs a <code>BindLocalNpcEvent</code> with the provided 
	 * {@link Player}, ID, {@link BoundingBox}, and delay
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param npcID the ID of the Npc to locate and bind
	 * 
	 * @param rectangle the {@link BoundingBox} to search in for a Npc
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 */
	public BindLocalNpcEvent(Script script, int npcID, Rectangle rectangle, long delay)
	{
		super(script, delay);
		this.id = npcID;
		this.rectangle = rectangle;		
	}
	
	/**
	 * Constructs a <code>BindLocalNpcEvent</code> with the provided 
	 * {@link Player}, ID, and {@link BoundingBox} with a delay of 0
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param npcID the ID of the Npc to locate and bind
	 * 
	 * @param rectangle the {@link BoundingBox} to search in for a Npc
	 * 
	 */
	public BindLocalNpcEvent(Script script, int npcID, Rectangle rectangle)
	{
		this(script, npcID, rectangle, 0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void run()
	{
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		super.script.__internal_unbind(ScriptVariable.NPC_TARGET);
		Npc npc = World.getNpc(id, rectangle.x, rectangle.x + rectangle.width, rectangle.y, rectangle.y + rectangle.height);
		if(npc == null || npc.isBusy())
		{
			int x = 0, y = 0;
			do
			{
				x = DataConversions.random(rectangle.x, rectangle.x + rectangle.width);
				y = DataConversions.random(rectangle.y, rectangle.y + rectangle.height);
			}
			while(x == owner.getX() && y == owner.getY() && (rectangle.width > 0 || rectangle.height > 0));
			npc = new Npc(id, x, y);
			World.registerEntity(npc);
		}
		super.script.__internal_bind(ScriptVariable.NPC_TARGET, npc);
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}

}
