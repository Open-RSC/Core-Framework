package com.rscdaemon.util;

public class Pair<T, U>
{
	public Pair(T t, U u)
	{
		this.t = t;
		this.u = u;
	}
	
	public final T getFirst()
	{
		return t;
	}
	
	public final U getSecond()
	{
		return u;
	}
	
	@Override
	public final int hashCode()
	{
		return (t.hashCode() << 3) ^ u.hashCode();
	}
	
	@Override
	public final boolean equals(Object rhs)
	{
		if(!(rhs instanceof Pair))
		{
			return false;
		}
		Pair<?, ?> o = (Pair<?, ?>)rhs;
		return t.equals(o.t) && u.equals(o.u);
	}
	
	private final T t;
	private final U u;
}
