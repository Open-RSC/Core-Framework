package com.rscdaemon.core;

/**
 * An extension of the {@link javax.media.opengl.GLException} class that 
 * is used throughout this framework and acts to reserve future functionality 
 * that its superclass does not offer.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class GLException
	extends
		javax.media.opengl.GLException
{
	
	private static final long serialVersionUID = -8713194476458322029L;

	/**
	 * Default constructor - no message provided
	 * 
	 * @since 1.0
	 * 
	 */
	public GLException()
	{
		super();
	}

	/**
	 * Constructs a <code>GLException</code> with the provided message
	 * 
	 * @param message a brief message about this exception
	 * 
	 * @since 1.0
	 * 
	 */
	public GLException(String message)
	{
		super(message);
	}

	/**
	 * Constructs a <code>GLException</code> with the provided message and 
	 * cause
	 * 
	 * @param message a brief message about this exception
	 * 
	 * @param cause the underlying cause of this <code>GLException</code>
	 * 
	 * @since 1.0
	 * 
	 */
	public GLException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Constructs a <code>GLException</code> with the provided cause
	 * 
	 * @param cause the underlying cause of this <code>GLException</code>
	 * 
	 * @since 1.0
	 * 
	 */
	public GLException(Throwable cause)
	{
		super(cause);
	}
}
