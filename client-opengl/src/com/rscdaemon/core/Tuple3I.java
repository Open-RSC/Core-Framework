package com.rscdaemon.core;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.IntBuffer;
import java.util.IllegalFormatException;

import com.rscdaemon.internal.StringFormatter;

// TODO: Finish
public class Tuple3I
	extends
		Tuple2I
{

	private static final long serialVersionUID = -7370373436360126681L;
	
	/// Default format example: "(x, y, z)"
	private final static StringFormatter FORMATTER =
			new StringFormatter(Tuple3I.class, "(1$, 2$, 3$)");

	// Quickly fail if the formatter override is invalid
	static
	{
		try
		{
			FORMATTER.format(0, 0, 0);
		}
		catch(IllegalFormatException ife)
		{
			System.err.println("An illegal string format has been provided "
					+ "for class " + Tuple3I.class.getName() + " .");
		}
	}
	
	private int z;

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
		return FORMATTER.format(super.getX(), super.getY(), z);
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
		return super.hashCode() + (z * 11);
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
		if(!(o instanceof Tuple3I))
		{
			return false;
		}
		Tuple3I rhs = (Tuple3I)o;
		return super.equals(rhs) && rhs.z == z;
	}
	
	/**
	 * Constructs a <code>Tuple3I</code> with both elements set to 0
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple3I()
	{
		super();
		// "Generally" most JREs default instance ints to '0'...
		// but it's not in the JRE specification, so do it explicitly
		this.z = 0;
	}
	
	/**
	 * Constructs a <code>Tuple3I</code> from the provided values
	 * 
	 * @param x the first element of this tuple
	 * 
	 * @param y the second element of this tuple
	 * 
	 * @param z the third element of this tuple
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple3I(int x, int y, int z)
	{
		super(x, y);
		this.z = z;
	}
	
	/**
	 * Constructs a <code>Tuple3I</code> from the provided array, starting 
	 * at the provided offset
	 * 
	 * @param buffer the array to extract values from
	 * 
	 * @param offset the offset to begin extracting at
	 * 
	 * @throws IndexOutOfBoundsException if the offset is less than 3 
	 * elements from the end of the provided array
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple3I(int[] buffer, int offset)
	{
		super(buffer, offset);
		this.z = buffer[offset + 2];
	}
	
	/**
	 * Constructs a <code>Tuple3I</code> from the provided array, starting 
	 * at index 0
	 * 
	 * @param buffer the array to extract values from
	 * 
	 * @throws IndexOutOfBoundsException if the provided array has a length of 
	 * less than 3
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple3I(int[] buffer)
	{
		this(buffer, 0);
	}

	/**
	 * Constructs a <code>Tuple3I</code> from the provided {@link IntBuffer}
	 * 
	 * @param buffer the {@link IntBuffer} to extract values from
	 * 
	 * @throws BufferUnderflowException if there are fewer than 3 elements in 
	 * the provided buffer
	 * 
	 * @throws NullPointerException if the provided buffer is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple3I(IntBuffer buffer)
	{
		super(buffer);
		this.z = buffer.get();
	}
	
	/**
	 * Constructs a <code>Tuple3I</code> from the provided <code>Tuple3I</code>
	 * 
	 * @param other the other tuple to extract values from
	 * 
	 * @throws NullPointerException if the provided tuple is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Tuple3I(Tuple3I other)
	{
		super(other);
		this.z = other.z;
	}
	
	/**
	 * Retrieves the third element of this tuple
	 * 
	 * @return the third element of this tuple
	 * 
	 * @since 1.0
	 * 
	 */
	public final int getZ()
	{
		return z;
	}
	
	/**
	 * Inserts the elements of this tuple into the provided array at the 
	 * provided offset
	 * 
	 * @param buffer the array to insert the elements of this tuple into
	 * 
	 * @param offset the offset to start inserting at
	 * 
	 * @throws IndexOutOfBoundsException if offset is fewer than 3 elements 
	 * from the end of the provided array
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public void set(int[] buffer, int offset)
	{
		super.set(buffer, offset);
		buffer[offset + 2] = z;
	}
	
	/**
	 * Inserts the elements of this tuple into the provided array at offset 0
	 * 
	 * @param buffer the array to insert the elements of this tuple into
	 * 
	 * @throws IndexOutOfBoundsException if the provided array has a length of 
	 * less than 3
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
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
	@Override
	public void set(IntBuffer buffer)
	{
		super.set(buffer);
		buffer.put(z);
	}
	
	/**
	 * Sets the third element of this tuple
	 * 
	 * @param z the new value to set
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setZ(int z)
	{
		this.z = z;
	}
	
	/**
	 * Sets the elements of this tuple
	 * 
	 * @param x the new value of the first element
	 * 
	 * @param y the new value of the second element
	 * 
	 * @param z the new value of the third element
	 * 
	 * @since 1.0
	 * 
	 */
	public final void set(int x, int y, int z)
	{
		super.set(x, y);
		this.z = z;
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
	 * 3 elements from the end of the provided array
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public void get(int[] buffer, int offset)
	{
		super.get(buffer, offset);
		this.z = buffer[offset + 2];
	}
	
	/**
	 * Sets the elements of this tuple from the provided array at offset 0
	 * 
	 * @param buffer the array to extract values from
	 * 
	 * @throws NullPointerException if the provided array is null
	 * 
	 * @throws IndexOutOfBoundsException if the provided array has a length of 
	 * less than 3
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public void get(int[] buffer)
	{
		get(buffer, 0);
	}
	
	/**
	 * Sets the elements of this tuple from the provided {@link IntBuffer}
	 * 
	 * @param buffer the {@link IntBuffer} to extract values from
	 * 
	 * @throws BufferUnderflowException if there are fewer than 3 values 
	 * remaining in the buffer
	 * 
	 * @throws NullPointerException if the provided {@link IntBuffer} is null
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public void get(IntBuffer buffer)
	{
		super.get(buffer);
		this.z = buffer.get();
	}
	
	/**
	 * Performs an element-wise linear scaling on this tuple
	 * 
	 * @param factor the factor by which to scale this tuple
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public void scale(int factor)
	{
		super.scale(factor);
		this.z *= factor;
	}
	
	/**
	 * Performs an element-wise negation on this tuple.
	 * 
	 * @since 1.0
	 * 
	 */
	@Override
	public void negate()
	{
		super.negate();
		this.z = -this.z;
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
	public final void add(Tuple3I other)
	{
		super.add(other);
		this.z += other.z;
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
	public final void subtract(Tuple3I other)
	{
		super.subtract(other);
		this.z -= other.z;
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
	@Override
	public void clamp(int minimum, int maximum)
	{
		super.clamp(minimum, maximum);
		
		if(z < minimum)
		{
			z = minimum;
		}
		else if(z > maximum)
		{
			z = maximum;
		}
	}
}
