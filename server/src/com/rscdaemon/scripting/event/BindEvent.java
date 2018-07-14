package com.rscdaemon.scripting.event;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;


public class BindEvent
	extends
		ChainableEvent
{

	private final ScriptVariable key;
	private final Object value;
	
	public BindEvent(Script script, ScriptVariable key, Object value)
	{
		super(script, 0);
		this.key = key;
		this.value = value;
	}

	@Override
	public void run()
	{
		super.script.__internal_bind(key, value);
		super.scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return true;
	}
}
