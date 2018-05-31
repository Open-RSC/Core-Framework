package com.rscdaemon.core;

import java.io.Serializable;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;
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
public class Tuple2F
	implements
		EpsilonEquals,
		Serializable
{
	private static final long serialVersionUID = 2037610443494814337L;

	/// Default format example: "(x, y)"
	private final static StringFormatter FORMATTER =
			new StringFormatter(Tuple2F.class, "(1$, 2$)");

	// Quickly fail if the formatter override is invalid
	static
	{
		try
		{
			FORMATTER.format(0.0f, 0.0f);
		}
		catch(IllegalFormatException ife)
		{
			System.err.println("An illegal string format has been provided "
					+ "for class " + Tuple2F.class.getName() + " " + FORMATTER.getFormat());
		}
	}

	/// The first value of this tuple
	private float x;
	
	/// The second value of this tuple
	private float y;

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
		long rv = 11;
		// Float.floatToIntBits returns different values for -0.0 and +0.0
		// So intercept both those cases to meet the method contract
		rv += x == 0 ? 0 : Float.floatToIntBits(x);
		rv *= 11;
		rv += y == 0 ? 0 : Float.floatToIntBits(y);
		return (int) (rv ^ (rv >> 32));
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
		if(!(o instanceof Tuple2F))
		{
			return false;
		}
		Tuple2F rhs = (Tuple2F)o;
		return rhs.x == x && rhs.y == y;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public boolean equalsWithEpsilon(EpsilonEquals other, float epsilon)
	{
		if(epsilon < 0)
		{
			throw new IllegalArgumentException("Epsilon must not be negative");
		}
		if(!(other instanceof Tuple2F))
		{
			return false;
		}
		Tuple2F rhs = (Tuple2F)other;
		return Math.abs(x - rhs.x) <= epsilon && 
			   Math.abs(y - rhs.y) <= epsilon;
	}
	
	/**
	 * Constructs a <code>Tuple2F</code> with both elements set to 0
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2F()
	{
		// "Generally" most JREs default instance ints to '0'...
		// but it's not in the JRE specification, so do it explicitly
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Constructs a <code>Tuple2F</code> from the provided values
	 * 
	 * @param x the first element of this tuple
	 * 
	 * @param y the second element of this tuple
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2F(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Constructs a <code>Tuple2F</code> from the provided array, starting 
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
	public Tuple2F(float[] buffer, int offset)
	{
		this.x = buffer[offset];
		this.y = buffer[offset + 1];
	}
	
	/**
	 * Constructs a <code>Tuple2F</code> from the provided array, starting 
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
	public Tuple2F(float[] buffer)
	{
		this(buffer, 0);
	}

	/**
	 * Constructs a <code>Tuple2F</code> from the provided {@link FloatBuffer}
	 * 
	 * @param buffer the {@link FloatBuffer} to extract values from
	 * 
	 * @throws BufferUnderflowException if there are fewer than 2 elements in 
	 * the provided buffer
	 * 
	 * @throws NullPointerException if the provided buffer is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2F(FloatBuffer buffer)
	{
		this.x = buffer.get();
		this.y = buffer.get();
	}
	
	/**
	 * Constructs a <code>Tuple2F</code> from the provided <code>Tuple2F</code>
	 * 
	 * @param other the other tuple to extract values from
	 * 
	 * @throws NullPointerException if the provided tuple is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple2F(Tuple2F other)
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
	public final float getX()
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
	public final float getY()
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
	public void set(float[] buffer, int offset)
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
	public void set(float[] buffer)
	{
		set(buffer, 0);
	}
	
	/**
	 * Inserts the elements of this tuple into the provided {@link FloatBuffer}
	 * 
	 * @param buffer the {@link FloatBuffer} to insert values into
	 * 
	 * @throws BufferOverflowException if there is not enough room in the 
	 * {@link FloatBuffer}
	 * 
	 * @throws NullPointerException if the provided {@link FloatBuffer} is null
	 * 
	 * @since 1.0
	 * 
	 */
	public void set(FloatBuffer buffer)
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
	public final void setX(float x)
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
	public final void setY(float y)
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
	public final void set(float x, float y)
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
	public void get(float[] buffer, int offset)
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
	public void get(float[] buffer)
	{
		get(buffer, 0);
	}
	
	/**
	 * Sets the elements of this tuple from the provided {@link FloatBuffer}
	 * 
	 * @param buffer the {@link FloatBuffer} to extract values from
	 * 
	 * @throws BufferUnderflowException if there are fewer than 2 values 
	 * remaining in the buffer
	 * 
	 * @throws NullPointerException if the provided {@link FloatBuffer} is null
	 * 
	 * @since 1.0
	 * 
	 */
	public void get(FloatBuffer buffer)
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
	public void scale(float factor)
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
	public final void add(Tuple2F other)
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
	public final void subtract(Tuple2F other)
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
	public void clamp(float minimum, float maximum)
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
