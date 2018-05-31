package com.rscdaemon.glsl;

/**
 * A class that encapsulates a GLSL fragment shader
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public final class FragmentShader
	extends
		Shader
{

	/**
	 * Constructs a <code>FragmentShader</code> with the provided source code
	 * 
	 * @param source the source code of this <code>FragmentShader</code>
	 * 
	 * @throws CompilationException if this <code>FragmentShader</code> fails 
	 * to compile
	 * 
	 * @since 1.0
	 * 
	 */
	public FragmentShader(String source)
	{
		super(FRAGMENT_SHADER, source);
	}

}
