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

import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

/**
 * A type of {@link ChainableEvent} that sends a chat message from 
 * the target {@link Player} to the target {@link Mob}.  It is worth 
 * noting that the target <code>Mob</code> <strong>may be null</strong>.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class PlayerToMobChatEvent
	extends
		ChainableEvent
{
	
	/// The default delay (in milliseconds) for an empty event chain
	private final static long EMPTY_EVENT_CHAIN_DELAY_MILLIS = 500;
	
	/// The default delay (in milliseconds) for a non-empty event chain
	private final static long NONEMPTY_EVENT_CHAIN_DELAY_MILLIS = 2500;
	
	/// The message to send from {@link Mob} to {@link Player}
	private final String message;

	/**
	 * Constructs a <code>PlayerToMobChatEvent</code> with the provided 
	 * {@link Player}, message, and delay.
	 * 
	 * @param receiver the {@link Player} that sends the message
	 * 
	 * @param message the message to send
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 */
	public PlayerToMobChatEvent(Script script, String message, long delay)
	{
		super(script, delay);
		this.message = message;
	}

	/**
	 * Constructs a <code>PlayerToMobChatEvent</code> with the provided 
	 * {@link Player} and message with a delay of either 
	 * {@link PlayerToMobChatEvent#EMPTY_EVENT_CHAIN_DELAY_MILLIS} milliseconds or 
	 * {@link PlayerToMobChatEvent#NONEMPTY_EVENT_CHAIN_DELAY_MILLIS} milliseconds 
	 * depending on the state of the underlying {@link EventChain}.
	 * 
	 * @param receiver the {@link Player} that sends the message
	 * 
	 * @param message the message to send
	 * 
	 */
	public PlayerToMobChatEvent(Script script, String message)
	{
		this(script, message, script.__internal_get_scope().isEmpty() ? EMPTY_EVENT_CHAIN_DELAY_MILLIS : NONEMPTY_EVENT_CHAIN_DELAY_MILLIS);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void run()
	{
		Player owner = (Player)super.script.__internal_get_variable(ScriptVariable.OWNER);
		Npc receiver = (Npc)super.script.__internal_get_variable(ScriptVariable.NPC_TARGET);
		if(receiver != null)
		{
			owner.updateSprite(receiver.getX(), receiver.getY());
			receiver.updateSprite(owner.getX(), owner.getY());
		}
		for(Player player : owner.getViewArea().getPlayersInView())
		{
			player.watchChatMessage(owner, receiver, message);
		}
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
}
