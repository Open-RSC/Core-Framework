package com.rscdaemon.glsl;

import com.rscdaemon.core.GLException;

/**
 * A specialized {@link GLException} that is thrown when the compilation of 
 * a {@link Shader} fails.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class CompilationException
	extends
		RuntimeException
{

	private static final long serialVersionUID = 7052603148114561931L;
	
	/**
	 * (Package Private) Constructs a <code>CompilationException</code> 
	 * with the provided shader type, source, and error message
	 * 
	 * @param shaderType the type of shader that failed to compile
	 * 
	 * @param src the source of the shader that failed to compile
	 * 
	 * @param error the compilation error message
	 * 
	 * @since 1.0
	 * 
	 */
	CompilationException(String shaderType, String src, String error)
	{
		super(
			new StringBuilder("Unable to compile ")
				.append(shaderType)
				.append(" shader:\n\n")
				.append(src)
				.append("\n\n")
				.append(error)
				.toString());
		assert shaderType != null && src != null && error != null;
	}
}
