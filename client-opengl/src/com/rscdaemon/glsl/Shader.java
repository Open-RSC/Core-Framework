package com.rscdaemon.glsl;

import java.util.Map;
import java.util.TreeMap;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLContext;

import com.rscdaemon.core.ContextBound;
import com.rscdaemon.core.Disposable;

/**
 * A class that encapsulates a GLSL shader
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public abstract class Shader
	implements
		ContextBound<GL2ES2>,
		Disposable
{

	protected final static int FRAGMENT_SHADER = GL2ES2.GL_FRAGMENT_SHADER;
	protected final static int VERTEX_SHADER = GL2ES2.GL_VERTEX_SHADER;
	
	/// A mapping of OpenGL shader type enums to 'pretty' strings
	private final static Map<Integer, String> SHADERS = 
			new TreeMap<Integer, String>();
	
	static
	{
		SHADERS.put(GL2ES2.GL_VERTEX_SHADER, "Vertex Shader");
		SHADERS.put(GL2ES2.GL_FRAGMENT_SHADER, "Fragment Shader");
	}
	
	/// The OpenGL context that this <code>Shader</code> is bound to
	private GL2ES2 context;
	
	/// The OpenGL handle to our shader	
	int handle;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected final void finalize()
	{
		if (handle != 0)
		{
			// (hopefully? will fail early in development)
			assert false : "Resource leak detected (most likely "
					+ "Shader.dispose()V was never called on this object)";
			context.glDeleteShader(handle);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void dispose()
	{
		if(handle != 0)
		{
			context.glDeleteShader(handle);
			handle = 0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public GL2ES2 getContext()
	{
		return context;
	}
	
	/**
	 * Constructs a <code>Shader</code> of the provided type with the provided 
	 * source code
	 * 
	 * @param type the type of <code>Shader</code> to create
	 * 
	 * @param source the source code of this <code>Shader</code>
	 * 
	 * @throws CompilationException if this <code>Shader</code> fails to 
	 * compile
	 * 
	 * @since 1.0
	 * 
	 */
	protected Shader(int type, String source)
	{
		assert SHADERS.containsKey(type);
		this.context = GLContext.getCurrentGL().getGL2ES2();
		if(source == null)
		{
			throw new CompilationException(SHADERS.get(type), "", 
					"null source");
		}
		handle = context.glCreateShader(type);
		if(handle == 0)
		{
			throw new CompilationException(SHADERS.get(type), source, 
					"unable to generate handle");
		}
		context.glShaderSource(handle, 1, new String[] { source }, 
				new int[] { source.length() }, 0);
		context.glCompileShader(handle);
		final int[] result = new int[1];
		context.glGetShaderiv(handle, GL2ES2.GL_COMPILE_STATUS, result, 0);
		if (result[0] == GL2ES2.GL_FALSE)
		{

			int[] logLength = new int[1];
			context.glGetShaderiv(handle, GL2ES2.GL_INFO_LOG_LENGTH, 
					logLength, 0);

			byte[] log = new byte[logLength[0]];
			context.glGetShaderInfoLog(handle, logLength[0], (int[])null, 
					0, log, 0);

			context.glDeleteShader(handle);
			handle = 0;
			throw new CompilationException(SHADERS.get(type), source, 
					new String(log));
		}
	}
}