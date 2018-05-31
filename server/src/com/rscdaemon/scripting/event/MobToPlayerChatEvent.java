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

import org.rscemulation.server.model.Mob;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

/**
 * A type of {@link ChainableEvent} that sends a chat message from 
 * the target {@link Mob} to the target {@link Player}
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class MobToPlayerChatEvent
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
	 * Constructs a <code>MobToPlayerChatEvent</code> with the provided 
	 * {@link Player}, message, and delay.
	 * 
	 * @param receiver the {@link Player} that receives the message
	 * 
	 * @param message the message to send
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 */
	public MobToPlayerChatEvent(Script script, String message, long delay)
	{
		super(script, delay);
		this.message = message;
	}

	/**
	 * Constructs a <code>MobToPlayerChatEvent</code> with the provided 
	 * {@link Player} and message with a delay of either 
	 * {@link MobToPlayerChatEvent#EMPTY_EVENT_CHAIN_DELAY_MILLIS} milliseconds or 
	 * {@link MobToPlayerChatEvent#NONEMPTY_EVENT_CHAIN_DELAY_MILLIS} milliseconds 
	 * depending on the state of the underlying {@link EventChain}.
	 * 
	 * @param receiver the {@link Player} that receives the message
	 * 
	 * @param message the message to send
	 * 
	 */
	public MobToPlayerChatEvent(Script script, String message)
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
		Player receiver = (Player)super.script.__internal_get_variable(ScriptVariable.OWNER);
		Npc sender = (Npc)super.script.__internal_get_variable(ScriptVariable.NPC_TARGET);

		// Quick check to make sure the script wasn't cancelled
		// Face each-other
		sender.updateSprite(receiver.getX(), receiver.getY());
		receiver.updateSprite(sender.getX(), sender.getY());
		
		// Show everyone around the message
		for(Player player : sender.getViewArea().getPlayersInView())
		{
			player.watchChatMessage(sender,  receiver, message);
		}
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
}
