package com.rscdaemon.glsl;

import com.rscdaemon.core.GLException;

/**
 * A specialized {@link GLException} that is thrown when an GLSL attribute 
 * cannot be found.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class AttributeNotFoundException
	extends
		GLException
{

	private static final long serialVersionUID = -1899337826190255346L;

	/**
	 * (Package Private) Constructs an <code>AttributeNotFoundException</code> 
	 * with the provided attribute name.
	 * 
	 * @param attributeName the name of the attribute that could not be found
	 * 
	 * @since 1.0
	 * 
	 */
	AttributeNotFoundException(String attributeName)
	{
		super("attribute \"" + attributeName + "\" was not found");
		assert attributeName != null;
	}
}
