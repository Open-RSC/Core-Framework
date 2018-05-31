package com.rscdaemon.glsl;

import com.rscdaemon.core.GLException;

/**
 * A specialized {@link GLException} that is thrown when the linking of a 
 * {@link Program} fails
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class LinkingException
	extends
		RuntimeException
{
	private static final long serialVersionUID = 8317585356096841079L;
	
	/**
	 * (Package Private) Constructs a <code>LinkingException</code> 
	 * with the provided error message
	 * 
	 * @param error the linking error message
	 * 
	 * @since 1.0
	 * 
	 */
	LinkingException(String error)
	{
		super(error);
	}
}
