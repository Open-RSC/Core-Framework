package com.rscdaemon.scripting.event;

import com.rscdaemon.scripting.Predicate;
import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.util.FunctionPointer;

public class PredicateEvent
	extends
		ChainableEvent
{

	private Predicate p;
	private final FunctionPointer trueBranch, falseBranch;
	
	public PredicateEvent(Script script, Predicate p, FunctionPointer trueBranch, FunctionPointer falseBranch)
	{
		super(script, 0);
		this.p = p;
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}

	@Override
	public void run()
	{
		super.script.__internal_push();
		
		while(p.hasPrevious())
		{
			p = p.previous();
		}

		boolean rv = p.evaluate();
		while(rv && p.hasNext())
		{
			p = p.next();
			switch(p.getOperator())
			{
				case AND:
					rv &= p.evaluate();
					break;
				case OR:
					rv |= p.evaluate();
					break;
				case NONE:
					rv = p.evaluate();
					break;
			}
		}
		
		if(rv && trueBranch != null)
		{
			trueBranch.invoke(script);
		}
		else if(falseBranch != null)
		{
			falseBranch.invoke(script);
		}
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}
}
