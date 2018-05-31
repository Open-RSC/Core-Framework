package com.rscdaemon.glsl;

import com.rscdaemon.core.GLException;

/**
 * A specialized {@link GLException} that is thrown when an GLSL uniform 
 * cannot be found.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class UniformNotFoundException
	extends
		GLException
{
	
	private static final long serialVersionUID = -7283698674087951576L;

	/**
	 * (Package Private) Constructs an <code>UniformNotFoundException</code> 
	 * with the provided uniform name.
	 * 
	 * @param uniformName the name of the uniform that could not be found
	 * 
	 * @since 1.0
	 * 
	 */
	UniformNotFoundException(String uniformName)
	{
		super("uniform \"" + uniformName + "\" was not found");
	}
}
