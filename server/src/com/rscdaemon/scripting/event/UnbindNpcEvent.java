package com.rscdaemon.scripting.event;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;
//TODO: Docs
public class UnbindNpcEvent
	extends
		ChainableEvent
{

	public UnbindNpcEvent(Script script)
	{
		super(script, 0);
	}

	@Override
	public void run()
	{
		super.script.__internal_unbind(ScriptVariable.NPC_TARGET);
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}

}
