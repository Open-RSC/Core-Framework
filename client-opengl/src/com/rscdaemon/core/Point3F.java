package com.rscdaemon.core;

import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;
import java.util.IllegalFormatException;

import com.rscdaemon.internal.StringFormatter;

public class Point3F
	extends
		Tuple3F
{

	private static final long serialVersionUID = 2069271439341796036L;
	
	/// Default format example: "(x, y, z)"
	private final static StringFormatter FORMATTER =
			new StringFormatter(Point3F.class, "(%s0, %s1, %s2)");

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
					+ "for class " + Point3F.class.getName() + " .");
		}
	}
	
	@Override
	public String toString()
	{
		return FORMATTER.format(super.getX(), super.getY(), super.getZ());
	}
	
	/**
	 * Constructs a <code>Point3F</code> with both elements set to 0
	 * 
	 * @since 1.0
	 * 
	 */
	public Point3F()
	{
		super();
	}
	
	/**
	 * Constructs a <code>Point3F</code> from the provided values
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
	public Point3F(float x, float y, float z)
	{
		super(x, y, z);
	}
	
	/**
	 * Constructs a <code>Point3F</code> from the provided array, starting 
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
	public Point3F(float[] buffer, int offset)
	{
		super(buffer, offset);
	}
	
	/**
	 * Constructs a <code>Point3F</code> from the provided array, starting 
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
	public Point3F(float[] buffer)
	{
		this(buffer, 0);
	}

	/**
	 * Constructs a <code>Point3F</code> from the provided {@link FloatBuffer}
	 * 
	 * @param buffer the {@link FloatBuffer} to extract values from
	 * 
	 * @throws BufferUnderflowException if there are fewer than 3 elements in 
	 * the provided buffer
	 * 
	 * @throws NullPointerException if the provided buffer is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Point3F(FloatBuffer buffer)
	{
		super(buffer);
	}
	
	/**
	 * Constructs a <code>Point3F</code> from the provided <code>Point3F</code>
	 * 
	 * @param other the other tuple to extract values from
	 * 
	 * @throws NullPointerException if the provided tuple is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Point3F(Point3F other)
	{
		super(other);
	}

	/**
	 * Calculates the distance between this point and the provided one
	 * 
	 * @param other the point to compare distance to
	 * 
	 * @return the distance between this point and the provided one
	 * 
	 */
	public final float distance(Point3F other)
	{
		return (float)Math.sqrt(
						Math.pow(super.getX() - other.getX(), 2) +
						Math.pow(super.getY() - other.getY(), 2) +
						Math.pow(super.getZ() - other.getZ(), 2));
	}
}
