package com.rscdaemon.scripting.event;

import com.rscdaemon.scripting.Predicate;
import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.util.FunctionPointer;

public class RecursivePredicateEvent
	extends
		ChainableEvent
{

	private Predicate p;
	private final FunctionPointer trueBranch;

	public RecursivePredicateEvent(Script script, Predicate p, FunctionPointer trueBranch)
	{
		super(script, 0);
		this.p = p;
		this.trueBranch = trueBranch;
	}
	
	@Override
	public void run()
	{
		
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
		
		if(rv)
		{
			super.script.__internal_push();
			trueBranch.invoke(script);
			script.__internal_get_scope().addLast(this);
		}
		scheduleNext();
	}

	@Override
	protected boolean isCancellable()
	{
		return false;
	}
}
