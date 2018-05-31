package com.rscdaemon.glsl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLException;

import com.rscdaemon.core.ContextBound;
import com.rscdaemon.core.Disposable;

/**
 * A class that encapsulates a GLSL shader program.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public final class Program
	implements
		ContextBound<GL2ES2>,
		Disposable
{
	
	/// A constant defining the return value for when a uniform is not found
	private final static int GL_UNIFORM_LOCATION_NOT_FOUND = -1;
	
	/// A constant defining the return value for when an attribute is not found
	private final static int GL_ATTRIBUTE_LOCATION_NOT_FOUND = -1;
	
	/// The OpenGL context that this <code>Program</code> is bound to
	private final GL2ES2 context;
	
	/// The OpenGL handle to our <code>Program</code>
	private int handle;
	
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void dispose()
	{
		if (handle != 0)
		{
			context.glDeleteProgram(handle);
			handle = 0;
		}
	}
	
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
					+ "Program.dispose()V was never called on this object)";
			context.glDeleteProgram(handle);
		}
	}
	
	/**
	 * Attempts to attach the provided {@link Shader shaders} to this 
	 * <code>Program</code>
	 * 
	 * @param shaders the array of {@link Shader shaders} to attach
	 * 
	 * @throws LinkingException if one or more of the provided shaders are 
	 * duplicates
	 * 
	 * @since 1.0
	 * 
	 */
	private final void attachShaders(Shader... shaders)
	{
		for(Shader shader : shaders)
		{
			context.glAttachShader(handle, shader.handle);
			switch(context.glGetError())
			{
				default:
					break;
				case GL2ES2.GL_INVALID_VALUE:
					// Should never happen - we provide all handles internally
					assert false : "An internal error occurred "
							+ "(invalid handles)";
					break;
				case GL2ES2.GL_INVALID_OPERATION:
					throw new LinkingException("Unable to attach duplicate "
							+ "shader to this program.");
			}
		}
	}
	
	/**
	 * Attempts to link this <code>Program</code>
	 * 
	 * @throws LinkingException if this <code>Program</code> fails to link
	 * 
	 * @since 1.0
	 * 
	 */
	private final void link()
	{
		context.glLinkProgram(handle);
		final int[] linkStatus = new int[1];
		context.glGetProgramiv(handle, GL2ES2.GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] == GL2ES2.GL_FALSE)
		{
			IntBuffer intBuffer = IntBuffer.allocate(1);
			context.glGetProgramiv(handle, GL2ES2.GL_INFO_LOG_LENGTH, 
					intBuffer);
			int size = intBuffer.get(0);
			ByteBuffer byteBuffer = ByteBuffer.allocate(size);
			context.glGetProgramInfoLog(handle, size, intBuffer, byteBuffer);
			throw new LinkingException(new String(byteBuffer.array()));
		}
	}

	/**
	 * Attempts to detach the provided {@link Shader shaders} from this 
	 * <code>Program</code>
	 * 
	 * @param shaders the array of {@link Shader shaders} to detach
	 * 
	 * @since 1.0
	 * 
	 */
	private final void detachShaders(Shader... shaders)
	{
		for(Shader shader : shaders)
		{
			context.glDetachShader(this.handle, shader.handle);
		}
		// Swallow up any error flag - we use exceptions, so there is no need
		// to explicitly check archaic return values.
		context.glGetError();
	}

	/**
	 * Runs the provided transaction while preserving the state machine of the 
	 * provided OpenGL context
	 * 
	 * @param t the <code>Transaction</code> to run
	 * 
	 * @throws AttributeNotFoundException if the underlying transaction 
	 * throws it
	 * 
	 * @throws UniformNotFoundException if the underlying transaction throws it
	 * 
	 * @throws TypeMismatchException if the underlying transaction throws it
	 * 
	 * @since 1.0
	 * 
	 */
	private final void runTransaction(Runnable t)
	{
		int[] preserved = new int[1];
		context.glGetIntegerv(GL2ES2.GL_CURRENT_PROGRAM, preserved, 0);
		context.glUseProgram(handle);
		try
		{
			t.run();
		}
		finally
		{
			context.glUseProgram(preserved[0]);
		}
	}
	
	/**
	 * Creates a shader program from the provided shaders.  This program 
	 * <strong>does not own</strong> the shaders that are provided to it, 
	 * and as such, disposing of the shaders is the caller's responsibility.
	 * 
	 * @param shaders a varargs representation of an array of shaders from 
	 * which to create this program from.
	 * 
	 * @throws GLException if there is no OpenGL context bound to the 
	 * current thread
	 * 
	 * @throws LinkingException if this program fails to link correctly.
	 * 
	 * @since 1.0
	 * 
	 */
	public Program(Shader... shaders)
	{
		this.context = GLContext.getCurrentGL().getGL2();
		handle = context.glCreateProgram();
		try
		{
			attachShaders(shaders);
			link();
		}
		finally
		{
			detachShaders(shaders);
		}
	}

	/**
	 * Attempts to set the provided uniform to the provided value
	 * 
	 * @param name the name of the uniform to set
	 * 
	 * @param x the value to set
	 * 
	 * @throws TypeMismatchException if the type of the provided uniform 
	 * is incompatible with the data type provided by this method
	 * 
	 * @throws UniformNotFoundException if the provided uniform is not found
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setUniform(final String name, final float x)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int uniformHandle = context.glGetUniformLocation(handle, name);
					if(uniformHandle == GL_UNIFORM_LOCATION_NOT_FOUND)
					{
						throw new UniformNotFoundException(name);
					}
					context.glUniform1f(uniformHandle, x);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}

	/**
	 * Attempts to set the provided uniform to the provided values
	 * 
	 * @param name the name of the uniform to set
	 * 
	 * @param x the first element of the uniform
	 * 
	 * @param y the second element of the uniform
	 * 
	 * @throws TypeMismatchException if the type of the provided uniform 
	 * is incompatible with the data type provided by this method
	 * 
	 * @throws UniformNotFoundException if the provided uniform is not found
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setUniform(final String name, final float x, final float y)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int uniformHandle = context.glGetUniformLocation(handle, name);
					if(uniformHandle == GL_UNIFORM_LOCATION_NOT_FOUND)
					{
						throw new UniformNotFoundException(name);
					}
					context.glUniform2f(uniformHandle, x, y);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}

	/**
	 * Attempts to set the provided uniform to the provided values
	 * 
	 * @param name the name of the uniform to set
	 * 
	 * @param x the first element of the uniform
	 * 
	 * @param y the second element of the uniform
	 * 
	 * @param z the third element of the uniform
	 * 
	 * @throws TypeMismatchException if the type of the provided uniform 
	 * is incompatible with the data type provided by this method
	 * 
	 * @throws UniformNotFoundException if the provided uniform is not found
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setUniform(final String name, final float x, final float y, final float z)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int uniformHandle = context.glGetUniformLocation(handle, name);
					if(uniformHandle == GL_UNIFORM_LOCATION_NOT_FOUND)
					{
						throw new UniformNotFoundException(name);
					}
					context.glUniform3f(uniformHandle, x, y, z);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}

	/**
	 * Attempts to set the provided uniform to the provided values
	 * 
	 * @param name the name of the uniform to set
	 * 
	 * @param x the first element of the uniform
	 * 
	 * @param y the second element of the uniform
	 * 
	 * @param z the third element of the uniform
	 * 
	 * @param w the fourth element of the uniform
	 * 
	 * @throws TypeMismatchException if the type of the provided uniform 
	 * is incompatible with the data type provided by this method
	 * 
	 * @throws UniformNotFoundException if the provided uniform is not found
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setUniform(final String name, final float x, final float y, final float z, final float w)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int uniformHandle = context.glGetUniformLocation(handle, name);
					if(uniformHandle == GL_UNIFORM_LOCATION_NOT_FOUND)
					{
						throw new UniformNotFoundException(name);
					}
					context.glUniform4f(uniformHandle, x, y, z, w);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}

	/**
	 * Attempts to set the provided uniform to the provided matrix
	 * 
	 * @param name the name of the uniform to set
	 * 
	 * @param matrix the matrix to set the uniform to
	 * 
	 * @throws TypeMismatchException if the type of the provided uniform 
	 * is incompatible with the data type provided by this method
	 * 
	 * @throws UniformNotFoundException if the provided uniform is not found
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setUniform(final String name, final float[] matrix)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int uniformHandle = context.glGetUniformLocation(handle, name);
					if(uniformHandle == GL_UNIFORM_LOCATION_NOT_FOUND)
					{
						throw new UniformNotFoundException(name);
					}
					context.glUniformMatrix4fv(uniformHandle, 1, false, 
								matrix, 0);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}

	/**
	 * Attempts to set the provided uniform to the provided value
	 * 
	 * @param name the name of the uniform to set
	 * 
	 * @param x the value to set
	 * 
	 * @throws TypeMismatchException if the type of the provided uniform 
	 * is incompatible with the data type provided by this method
	 * 
	 * @throws UniformNotFoundException if the provided uniform is not found
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setUniform(final String name, final int x)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int uniformHandle = context.glGetUniformLocation(handle, name);
					if(uniformHandle == GL_UNIFORM_LOCATION_NOT_FOUND)
					{
						throw new UniformNotFoundException(name);
					}
					context.glUniform1i(uniformHandle, x);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}

	/**
	 * Attempts to set the provided uniform to the provided values
	 * 
	 * @param name the name of the uniform to set
	 * 
	 * @param x the first element of the uniform
	 * 
	 * @param y the second element of the uniform
	 * 
	 * @throws TypeMismatchException if the type of the provided uniform 
	 * is incompatible with the data type provided by this method
	 * 
	 * @throws UniformNotFoundException if the provided uniform is not found
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setUniform(final String name, final int x, final int y)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int uniformHandle = context.glGetUniformLocation(handle, name);
					if(uniformHandle == GL_UNIFORM_LOCATION_NOT_FOUND)
					{
						throw new UniformNotFoundException(name);
					}
					context.glUniform2i(uniformHandle, x, y);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}
	
	/**
	 * Attempts to set the provided uniform to the provided values
	 * 
	 * @param name the name of the uniform to set
	 * 
	 * @param x the first element of the uniform
	 * 
	 * @param y the second element of the uniform
	 * 
	 * @param z the third element of the uniform
	 * 
	 * @throws TypeMismatchException if the type of the provided uniform 
	 * is incompatible with the data type provided by this method
	 * 
	 * @throws UniformNotFoundException if the provided uniform is not found
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setUniform(final String name, final int x, final int y, final int z)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int uniformHandle = context.glGetUniformLocation(handle, name);
					if(uniformHandle == GL_UNIFORM_LOCATION_NOT_FOUND)
					{
						throw new UniformNotFoundException(name);
					}
					context.glUniform3i(uniformHandle, x, y, z);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}
	
	/**
	 * Attempts to set the provided uniform to the provided values
	 * 
	 * @param name the name of the uniform to set
	 * 
	 * @param x the first element of the uniform
	 * 
	 * @param y the second element of the uniform
	 * 
	 * @param z the third element of the uniform
	 * 
	 * @param w the fourth element of the uniform
	 * 
	 * @throws TypeMismatchException if the type of the provided uniform 
	 * is incompatible with the data type provided by this method
	 * 
	 * @throws UniformNotFoundException if the provided uniform is not found
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setUniform(final String name, final int x, final int y, final int z, final int w)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int uniformHandle = context.glGetUniformLocation(handle, name);
					if(uniformHandle == GL_UNIFORM_LOCATION_NOT_FOUND)
					{
						throw new UniformNotFoundException(name);
					}
					context.glUniform4i(uniformHandle, x, y, z, w);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}
	
	/**
	 * Attempts to set the provided attribute pointer
	 * 
	 * @param name the name of the attribute to modify
	 * 
	 * @param valueSize the size (in bytes) of the attribute data type
	 * 
	 * @param offset the offset (in bytes) of the first element of the 
	 * attribute
	 * 
	 * @param stride the total length (in bytes) of an entry of all enabled 
	 * attributes of the vertex buffer array.
	 * 
	 * @since 1.0
	 * 
	 */
	public final void setAttribute(final String name, final int valueSize, final int offset, final int stride)
	{
		runTransaction(
			new Runnable()
			{
				@Override
				public final void run()
				{
					int attribHandle = context.glGetAttribLocation(handle, name);
					if(attribHandle == GL_ATTRIBUTE_LOCATION_NOT_FOUND)
					{
						throw new AttributeNotFoundException(name);
					}
					context.glVertexAttribPointer(attribHandle, valueSize, GL2ES2.GL_FLOAT, false, stride, offset);
					context.glEnableVertexAttribArray(attribHandle);
					if(context.glGetError() != GL2ES2.GL_NO_ERROR)
					{
						throw new TypeMismatchException(name);
					}
				}
			}
		);
	}

	/**
	 * Binds this program to the current OpenGL context
	 * 
	 * @since 1.0
	 * 
	 */
	public final void bind()
	{
		context.glUseProgram(handle);
		assert context.glGetError() == GL2ES2.GL_NO_ERROR;
	}
	
	/**
	 * Unbinds this program from the current OpenGL context
	 * 
	 * @since 1.0
	 * 
	 */
	public final void unbind()
	{
		context.glUseProgram(0);
		assert context.glGetError() == GL2ES2.GL_NO_ERROR;
	}
}