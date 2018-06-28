package com.rscdaemon.scripting.event;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;


public class UnbindEvent
	extends
		ChainableEvent
{

	private final ScriptVariable key;
	
	public UnbindEvent(Script script, ScriptVariable key)
	{
		super(script, 0);
		this.key = key;
	}

	@Override
	public void run()
	{
		super.script.__internal_unbind(key);
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
}
