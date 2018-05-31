package com.rscdaemon.glsl;

/**
 * A class that encapsulates a GLSL vertex shader
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public final class VertexShader
	extends
		Shader
{

	/**
	 * Constructs a <code>VertexShader</code> with the provided source code
	 * 
	 * @param source the source code of this <code>VertexShader</code>
	 * 
	 * @throws CompilationException if this <code>VertexShader</code> fails 
	 * to compile
	 * 
	 * @since 1.0
	 * 
	 */
	public VertexShader(String source)
	{
		super(VERTEX_SHADER, source);
	}
}
