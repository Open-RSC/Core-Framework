package com.rscdaemon.scripting.event;

import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;

public class BindQuestEvent
	extends
		ChainableEvent
{
	
	private final int questID;

	public BindQuestEvent(Script script, int questID)
	{
		super(script, 0);
		this.questID = questID;
	}

	@Override
	public void run()
	{
		super.script.__internal_bind(ScriptVariable.QUEST, ((Player)super.script.__internal_get_variable(ScriptVariable.OWNER)).getScriptableQuest(questID));
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
}
