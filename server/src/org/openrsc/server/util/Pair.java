package org.openrsc.server.util;

public class Pair<Type_A, Type_B>
{
	private final Type_A typeA;
	private final Type_B typeB;

	public Pair(Type_A typeA, Type_B typeB)
	{
		this.typeA = typeA;
		this.typeB = typeB;
	}

	public Type_A getFirst()
	{
		return this.typeA;
	}

	public Type_B getSecond()
	{
		return this.typeB;
	}

	public int hashCode()
	{
		int rv = 0;
		if(typeA != null)
		{
			rv += typeA.hashCode();
		}
		else
		{
			rv += 117;
		}
		if(typeB != null)
		{
			rv ^= typeB.hashCode();
		}
		return rv;
	}

	public boolean equals(Object rhs)
	{
		if ((rhs == null) || (!(rhs instanceof Pair)))
		{
			return false;
		}
		Pair<?,?> tuple = (Pair<?, ?>)rhs;
		return tuple.typeA != null ? tuple.typeA.equals(this.typeA) : this.typeA == null &&
			   tuple.typeB != null ? tuple.typeB.equals(this.typeB) : this.typeB == null;
	}
}
