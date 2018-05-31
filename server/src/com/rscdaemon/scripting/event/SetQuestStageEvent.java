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

import java.io.Serializable;

import org.rscemulation.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;
import com.rscdaemon.scripting.quest.Quest;

/**
 * A type of {@link ChainableEvent} that sets the stage of the currently 
 * bound {@link Quest}
 * 
 * @author Zilent
 *
 */
public class SetQuestStageEvent
	extends
		ChainableEvent
{
	private final static long DEFAULT_DELAY_MILLIS = 0;
	
	/// The new stage to set
	private final Serializable stage;
	
	public SetQuestStageEvent(Script script, Serializable stage, long delay)
	{
		super(script, delay);
		this.stage = stage;		
	}
	
	public SetQuestStageEvent(Script script, Serializable stage)
	{
		this(script, stage, DEFAULT_DELAY_MILLIS);
	}

	@Override
	public void run()
	{
		Quest quest = super.script.__internal_get_variable(ScriptVariable.QUEST);
		if(quest.getVariable(Quest.QUEST_STAGE).equals(Quest.QUEST_NOT_STARTED))
		{
			Player player = super.script.__internal_get_variable(ScriptVariable.OWNER);
			player.sendQuestStarted(quest.getID());
		}
		quest.setVariable(Quest.QUEST_STAGE, stage);
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}
}
