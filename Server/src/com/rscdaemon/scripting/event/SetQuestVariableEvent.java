package com.rscdaemon.scripting.event;

import java.io.Serializable;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;
import com.rscdaemon.scripting.quest.Quest;

public class SetQuestVariableEvent
	extends
		ChainableEvent
{

	private final String key;
	private final Serializable value;
	
	public SetQuestVariableEvent(Script script, String key, Serializable value)
	{
		super(script, 0);
		this.key = key;
		this.value = value;
	}

	@Override
	public void run()
	{
		((Quest)super.script.__internal_get_variable(ScriptVariable.QUEST)).setVariable(key, value);
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}

}
