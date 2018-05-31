package com.rscdaemon.scripting;

import java.util.ListIterator;


public interface Predicate
	extends
		ListIterator<Predicate>
{
	static enum Operator
	{
		NONE,
		AND,
		OR
	}
	
	boolean evaluate();
	Operator getOperator();

	Predicate and(Predicate predicate);
	Predicate or(Predicate predicate);
}
