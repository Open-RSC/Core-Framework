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
package com.rscdaemon.scripting.quest;

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.Skill;

/**
 * The default implementation of the {@link Experience} interface.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class DefaultExperience
	implements
		Experience
{
	/// Compatible with version 1.0 and up
	private static final long	serialVersionUID	= -6165132416913574529L;

	/// A proxy class to allow immutable serialization (internal, no docs.)
	private final static class Proxy
		implements
			Serializable
	{
		private static final long	serialVersionUID	= 5490364223522609322L;

		private final Skill skill;
		private final float amount;
		
		Proxy(DefaultExperience obj)
		{
			this.skill = obj.skill;
			this.amount = obj.amount;
		}
		
		private Object readResolve()
		{
            return new DefaultExperience(skill, amount);
        }
	}

	/// The {@link Skill} to grant experience in
	private final Skill skill;
	
	/// The amount of experience to grant
	private final float amount;

	/// @see java.io.Serializable
	private Object writeReplace()
		throws
			ObjectStreamException
	{
        return new Proxy(this);
    }
	
	/**
	 * Constructs a <code>DefaultExperience</code> with the provided skill 
	 * and amount
	 * 
	 * @param skill the skill to grant experience to
	 * 
	 * @param amount the amount of experience to grant
	 * 
	 */
	public DefaultExperience(Skill skill, float amount)
	{
		this.skill = skill;
		this.amount = amount;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void grant(Player recipient)
	{
		
		recipient.increaseXP(skill.ordinal(), amount, 0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Skill getSkill()
	{
		return skill;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final float getAmount()
	{
		return amount;
	}
}
