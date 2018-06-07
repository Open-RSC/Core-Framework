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
 * 
 */
package com.rscdaemon.scripting.quest;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.UUID;

import org.openrsc.server.model.Player;

/**
 * The default implementation of the {@link Permission} interface.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class DefaultPermission
	implements
		Permission
{
	
	/// Compatible with version 1.0 and up
	private final static long	serialVersionUID	= -6926592280759504155L;
	
	/// A proxy class to allow immutable serialization (internal, no docs.)
	private final static class SerializationProxy
		implements
			Serializable
	{
		private static final long	serialVersionUID	= 443765782668830961L;
		
		private final String message;
		private final UUID uID;
		
		private SerializationProxy(DefaultPermission obj)
		{
			this.message = obj.message;
			this.uID = obj.uID;
		}
		
		private Object readResolve()
		{
            return new DefaultPermission(message, uID);
        }
	}
	
	/// The message that is presented to the player upon being granted
	private final String message;
	
	/// The {@link UUID} of the permission being granted
	private final UUID uID;

	/// @see java.io.Serializable
	private Object writeReplace()
			throws
				ObjectStreamException
	{
        return new SerializationProxy(this);
    }

	/**
	 * Constructs a <code>DefaultPermission</code> with the provided message 
	 * and {@link UUID}
	 * 
	 * @param message the message to present when granted
	 * 
	 * @param uID the <code>UUID</code> of this <code>DefaultPermission</code>
	 * 
	 */
	public DefaultPermission(String message, UUID uID)
	{
		this.message = message;
		this.uID = uID;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void grant(Player recipient)
	{
		recipient.sendMessage(message);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public UUID getUID()
	{
		return uID;
	}
}
