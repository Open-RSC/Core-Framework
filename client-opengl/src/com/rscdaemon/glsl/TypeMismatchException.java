package com.rscdaemon.glsl;

import com.rscdaemon.core.GLException;

/**
 * A specialized {@link GLException} that is thrown when attempt is made to 
 * set either an attribute or uniform with an invalid data type.
 * 
 * @author Zilent
 *
 */
public class TypeMismatchException
	extends
		GLException
{

	private static final long serialVersionUID = -2409164218481876212L;

	/**
	 * Constructs a <code>TypeMismatchException</code> with the provided 
	 * variable name
	 * 
	 * @param variableName the name of the variable that was being set
	 * 
	 * @since 1.0
	 * 
	 */
	TypeMismatchException(String variableName)
	{
		super("Unable to set " + variableName + " - incorrect type provided");
	}
	
}
