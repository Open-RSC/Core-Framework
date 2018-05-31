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
import com.rscdaemon.scripting.quest.Quest;
import com.rscdaemon.scripting.quest.QuestReward;

/**
 * A type of {@link ChainableEvent} that marks a {@link Quest} as finished.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.3.0
 *
 */
public class FinishQuestEvent
	extends
		ChainableEvent
{

	/**
	 * Constructs a <code>FinishQuestEvent</code> with the provided 
	 * {@link Player}, {@link Quest}, and delay
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param quest the {@link Quest} that is being finished
	 * 
	 * @param delay the number of milliseconds that should pass before 
	 * running this <code>Event</code>
	 * 
	 */
	public FinishQuestEvent(Script script, long delay)
	{
		super(script, delay);
	}
	
	/**
	 * Constructs a <code>FinishQuestEvent</code> with the provided 
	 * {@link Player} and {@link Quest} with a delay of 0
	 * 
	 * @param owner the {@link Player} who is the target of this event
	 * 
	 * @param quest the {@link Quest} that is being finished
	 * 
	 */
	public FinishQuestEvent(Script script)
	{
		this(script, 0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void run()
	{
		Player owner = super.script.__internal_get_variable(ScriptVariable.OWNER);
		Quest quest = super.script.__internal_get_variable(ScriptVariable.QUEST);
		owner.sendMessage("Well done, you have completed the " + quest.getName() + " quest");
		owner.sendMessage("@gre@You have gained " + quest.getQuestPoints() + " quest points");
		for(QuestReward reward : quest.getRewards())
		{
			reward.grant(owner);
		}
		quest.setVariable(Quest.QUEST_STAGE, Quest.QUEST_FINISHED);
		owner.getActionSender().sendQuestFinished(quest.getID());
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}

}
