package com.rscdaemon.scripting;


public abstract class AbstractPredicate
	implements
		Predicate
{
	private Predicate prev, next;
	private Operator operator;
	
	protected AbstractPredicate()
	{
		operator = Operator.NONE;
	}
	
	@Override
	public Predicate and(Predicate predicate)
	{
		((AbstractPredicate)predicate).operator = Operator.AND;
		((AbstractPredicate)predicate).prev = this;
		next = predicate;
		return this;
	}

	@Override
	public Predicate or(Predicate predicate)
	{
		((AbstractPredicate)predicate).operator = Operator.OR;
		((AbstractPredicate)predicate).prev = this;
		next = predicate;
		return this;
	}

	@Override
	public Operator getOperator()
	{
		return operator;
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public Predicate next() {
		return next;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(Predicate e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPrevious()
	{
		return prev != null;
	}

	@Override
	public int nextIndex()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate previous() {
		return prev;
	}

	@Override
	public int previousIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Predicate e) {
		throw new UnsupportedOperationException();
	}

}
