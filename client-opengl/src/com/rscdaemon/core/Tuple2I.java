package com.rscdaemon.core;

import java.io.Serializable;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.IntBuffer;
import java.util.IllegalFormatException;

import com.rscdaemon.internal.StringFormatter;

/**
 * A general base class for constructs that involve two elements.  Default 
 * operations are provided that can be used interchangeably between derived 
 * classes.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class Tuple2I
	implements
		Serializable
{
	private static final long serialVersionUID = 2037610443494814337L;

	/// Default format example: "(x, y)"
	private final static StringFormatter FORMATTER =
			new StringFormatter(Tuple2I.class, "(1$, 2$)");

	// Quickly fail if the formatter override is invalid
	static
	{
		try
		{
			FORMATTER.format(0, 0);
		}
		catch(IllegalFormatException ife)
		{
			System.err.println("An illegal string format has been provided "
					+ "for class " + Tuple2I.class.getName() + " .");
		}
	}

	/// The first value of this tuple
	private int x;
	
	/// The second value of this tuple
	private int y;

	/**
	 * {@inheritDoc}
	 * 
	 * Returns a string defined by the formatter statically bound to this class
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public String toString()
	{
		return FORMATTER.format(x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public int hashCode()
	{
		return ((11 + x) * (11 + y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Tuple2I))
		{
			return false;
		}
		Tuple2I rhs = (Tuple2I)o;
		return rhs.x == x && rhs.y == y;
	}
		
	/**
	 * Constructs a <code>Tuple2I</code> with both elements set to 0
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2I()
	{
		// "Generally" most JREs default instance ints to '0'...
		// but it's not in the JRE specification, so do it explicitly
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Constructs a <code>Tuple2I</code> from the provided values
	 * 
	 * @param x the first element of this tuple
	 * 
	 * @param y the second element of this tuple
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2I(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Constructs a <code>Tuple2I</code> from the provided array, starting 
	 * at the provided offset
	 * 
	 * @param buffer the array to extract values from
	 * 
	 * @param offset the offset to begin extracting at
	 * 
	 * @throws IndexOutOfBoundsException if the offset is less than 2 
	 * elements from the end of the provided array
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2I(int[] buffer, int offset)
	{
		this.x = buffer[offset];
		this.y = buffer[offset + 1];
	}
	
	/**
	 * Constructs a <code>Tuple2I</code> from the provided array, starting 
	 * at index 0
	 * 
	 * @param buffer the array to extract values from
	 * 
	 * @throws IndexOutOfBoundsException if the provided array has a length of 
	 * less than 2
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2I(int[] buffer)
	{
		this(buffer, 0);
	}

	/**
	 * Constructs a <code>Tuple2I</code> from the provided {@link IntBuffer}
	 * 
	 * @param buffer the {@link IntBuffer} to extract values from
	 * 
	 * @throws BufferUnderflowException if there are fewer than 2 elements in 
	 * the provided buffer
	 * 
	 * @throws NullPointerException if the provided buffer is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2I(IntBuffer buffer)
	{
		this.x = buffer.get();
		this.y = buffer.get();
	}
	
	/**
	 * Constructs a <code>Tuple2I</code> from the provided <code>Tuple2I</code>
	 * 
	 * @param other the other tuple to extract values from
	 * 
	 * @throws NullPointerException if the provided tuple is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2I(Tuple2I other)
	{
		this.x = other.x;
		this.y = other.y;
	}
	
	/**
	 * Retrieves the first element of this tuple
	 * 
	 * @return the first element of this tuple
	 * 
	 * @since 1.0
	 * 
	 */
	public final int getX()
	{
		return x;
	}
	
	/**
	 * Retrieves the second element of this tuple
	 * 
	 * @return the second element of this tuple
	 * 
	 * @since 1.0
	 * 
	 */
	public final int getY()
	{
		return y;
	}
	
	/**
	 * Inserts the elements of this tuple into the provided array at the 
	 * provided offset
	 * 
	 * @param buffer the array to insert the elements of this tuple into
	 * 
	 * @param offset the offset to start inserting at
	 * 
	 * @throws IndexOutOfBoundsException if offset is fewer than 2 elements 
	 * from the end of the provided array
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @since 1.0
	 * 
	 */
	public void set(int[] buffer, int offset)
	{
		buffer[offset] = x;
		buffer[offset + 1] = y;
	}
	
	/**
	 * Inserts the elements of this tuple into the provided array at offset 0
	 * 
	 * @param buffer the array to insert the elements of this tuple into
	 * 
	 * @throws IndexOutOfBoundsException if the provided array has a length of 
	 * less than 2
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @since 1.0
	 * 
	 */
	public void set(int[] buffer)
	{
		set(buffer, 0);
	}
	
	/**
	 * Inserts the elements of this tuple into the provided {@link IntBuffer}
	 * 
	 * @param buffer the {@link IntBuffer} to insert values into
	 * 
	 * @throws BufferOverflowException if there is not enough room in the 
	 * {@link IntBuffer}
	 * 
	 * @throws NullPointerException if the provided {@link IntBuffer} is null
	 * 
	 * @since 1.0
	 * 
	 */
	public void set(IntBuffer buffer)
	{
		buffer.put(x).put(y);
	}
	
	/**
	 * Sets the first element of this tuple
	 * 
	 * @param x the new value to set
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 * Sets the second element of this tuple
	 * 
	 * @param y the new value to set
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setY(int y)
	{
		this.y = y;
	}
	
	/**
	 * Sets the elements of this tuple
	 * 
	 * @param x the new value of the first element
	 * 
	 * @param y the new value of the second element
	 * 
	 * @since 1.0
	 * 
	 */
	public final void set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets the elements of this tuple from the provided array at the provided 
	 * offset
	 * 
	 * @param buffer the array to extract values from
	 * 
	 * @param offset the offset to start extracting values at
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @throws IndexOutOfBoundsException if the provided offset is fewer than 
	 * 2 elements from the end of the provided array
	 * 
	 * @since 1.0
	 * 
	 */
	public void get(int[] buffer, int offset)
	{
		this.x = buffer[offset];
		this.y = buffer[offset + 1];
	}
	
	/**
	 * Sets the elements of this tuple from the provided array at offset 0
	 * 
	 * @param buffer the array to extract values from
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @throws IndexOutOfBoundsException if the provided array has a length of 
	 * less than 2
	 * 
	 * @since 1.0
	 * 
	 */
	public void get(int[] buffer)
	{
		get(buffer, 0);
	}
	
	/**
	 * Sets the elements of this tuple from the provided {@link IntBuffer}
	 * 
	 * @param buffer the {@link IntBuffer} to extract values from
	 * 
	 * @throws BufferUnderflowException if there are fewer than 2 values 
	 * remaining in the buffer
	 * 
	 * @throws NullPointerException if the provided {@link IntBuffer} is null
	 * 
	 * @since 1.0
	 * 
	 */
	public void get(IntBuffer buffer)
	{
		this.x = buffer.get();
		this.y = buffer.get();
	}
	
	/**
	 * Performs an element-wise linear scaling on this tuple
	 * 
	 * @param factor the factor by which to scale this tuple
	 * 
	 * @since 1.0
	 * 
	 */
	public void scale(int factor)
	{
		this.x *= factor;
		this.y *= factor;
	}
	
	/**
	 * Performs an element-wise negation on this tuple.
	 * 
	 * @since 1.0
	 * 
	 */
	public void negate()
	{
		this.x  = -this.x;
		this.y = -this.y;
	}
	
	/**
	 * Adds the provided tuple to this one
	 * 
	 * @param other the tuple to add
	 * 
	 * @throws NullPointerException if the provided tuple is null
	 * 
	 * @since 1.0
	 */
	public final void add(Tuple2I other)
	{
		this.x += other.x;
		this.y += other.y;
	}
	
	/**
	 * Subtracts the provided tuple from this one
	 * 
	 * @param other the tuple to subtract
	 * 
	 * @throws NullPointerException if the provided tuple is null
	 * 
	 * @since 1.0
	 * 
	 */
	public final void subtract(Tuple2I other)
	{
		this.x -= other.x;
		this.y -= other.y;
	}
	
	/**
	 * Clamps each element of this tuple to the provided range
	 * 
	 * @param minimum the minimum (inclusive) value to clamp to
	 * 
	 * @param maximum the maximum (inclusive) value to clamp to
	 * 
	 * @since 1.0
	 * 
	 */
	public void clamp(int minimum, int maximum)
	{
		if(x < minimum)
		{
			x = minimum;
		}
		else if(x > maximum)
		{
			x = maximum;
		}
		if(y < minimum)
		{
			y = minimum;
		}
		else if(y > maximum)
		{
			y = maximum;
		}
	}
}
