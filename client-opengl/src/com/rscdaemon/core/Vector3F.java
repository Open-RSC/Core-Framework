package com.rscdaemon.core;

import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;
import java.util.IllegalFormatException;

import com.rscdaemon.internal.StringFormatter;

public class Vector3F
	extends
		Tuple3F
{

	private static final long serialVersionUID = 4787034170449811613L;

	/// Default format example: "(x, y, z)"
	private final static StringFormatter FORMATTER =
			new StringFormatter(Vector3F.class, "(%s0, %s1, %s2)");

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
					+ "for class " + Vector3F.class.getName() + " .");
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String toString()
	{
		return FORMATTER.format(super.getX(), super.getY(), super.getZ());
	}
	
	/**
	 * Constructs a <code>Vector3F</code> with both elements set to 0
	 * 
	 * @since 1.0
	 * 
	 */
	public Vector3F()
	{
		super();
	}
	
	/**
	 * Constructs a <code>Vector3F</code> from the provided values
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
	public Vector3F(float x, float y, float z)
	{
		super(x, y, z);
	}
	
	/**
	 * Constructs a <code>Vector3F</code> from the provided array, starting 
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
	public Vector3F(float[] buffer, int offset)
	{
		super(buffer, offset);
	}
	
	/**
	 * Constructs a <code>Vector3F</code> from the provided array, starting 
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
	public Vector3F(float[] buffer)
	{
		this(buffer, 0);
	}

	/**
	 * Constructs a <code>Vector3F</code> from the provided {@link FloatBuffer}
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
	public Vector3F(FloatBuffer buffer)
	{
		super(buffer);
	}
	
	/**
	 * Constructs a <code>Vector3F</code> from the provided <code>Vector3F</code>
	 * 
	 * @param other the other tuple to extract values from
	 * 
	 * @throws NullPointerException if the provided tuple is null
	 * 
	 * @since 1.0
	 * 
	 */
	public Vector3F(Vector3F other)
	{
		super(other);
	}

	/**
	 * Retrieves the length of this <code>Vector3F</code>
	 * 
	 * @return the length of this<code>Vector3F</code>
	 * 
	 */
	public final float getLength()
	{
		return (float) Math.sqrt(
				Math.pow(super.getX(), 2) + 
				Math.pow(super.getY(),  2) + 
				Math.pow(super.getZ(), 2));
	}

	/**
	 * Crosses the provided <code>Vector3F</code> with this one
	 * 
	 * @param other the other <code>Vector3F</code> to cross
	 * 
	 */
	public final void cross(Vector3F other)
	{
		float x0 = super.getX(),
				y0 = super.getY(),
				z0 = super.getZ(),
				x1 = other.getX(),
				y1 = other.getY(),
				z1 = other.getZ();

		super.set(y0 * z1 - z0 * y1,
				x1 * z0 - z1 * x0,
				x0 * y1 - y0 * x1);
	}

	/**
	 * Calculates the dot product between the provided <code>Vector3F</code> 
	 * and this one
	 * 
	 * @param other the other <code>Vector3F</code> to calculate the dot 
	 * product with
	 * 
	 * @return the dot product between the provided <code>Vector3F</code> 
	 * and this one
	 * 
	 */
	public final float dot(Vector3F other)
	{
		return 
				(getX() * other.getX() + 
				getY() * other.getY() + 
				getZ() * other.getZ());
	}

	/**
	 * Normalizes this <code>Vector3F</code>
	 * 
	 */
	public final void normalize()
	{
		float val = 1.0f / this.getLength();
		super.set(super.getX() * val, super.getY() * val, super.getZ() * val);
	}

	/**
	 * Retrieves the angle (in radians) between this <code>Vector3F</code> 
	 * and the provided one.  It is noteworthy that the angle will be in the 
	 * range of [0, PI].
	 * 
	 * @param other the other <code>Vector3F</code> to compare
	 * 
	 * @return the angle (in radians) between this <code>Vector3F</code> 
	 * and the provided one.
	 * 
	 */
	public final float angle(Vector3F other) 
	{ 
		double vDot = dot(other) / (getLength() * other.getLength() );
		if( vDot < -1.0)
		{
			vDot = -1.0;
		}
		else if( vDot >  1.0)
		{
			vDot =  1.0;
		}
		return((float)Math.acos(vDot));
	}
}
