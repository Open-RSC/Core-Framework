package com.rscdaemon.internal;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLContext;

public class Utilities
{

	public static int createVBO(FloatBuffer data)
	{
		GL2ES2 gl = GLContext.getCurrentGL().getGL2ES2();
		int[] handle = new int[1];
		gl.glGenBuffers(1, handle, 0);
		gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, handle[0]);
		gl.glBufferData(GL2ES2.GL_ARRAY_BUFFER, data.capacity() * Platform.BYTES_PER_FLOAT, data, GL2ES2.GL_STATIC_DRAW);
		gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, 0);
		return handle[0];
	}
	
	public static int createIBO(ShortBuffer indices)
	{
		GL2ES2 gl = GLContext.getCurrentGL().getGL2ES2();
		int[] handle = new int[1];
		gl.glGenBuffers(1, handle, 0);
		gl.glBindBuffer(GL2ES2.GL_ELEMENT_ARRAY_BUFFER, handle[0]);
		gl.glBufferData(GL2ES2.GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * Platform.BYTES_PER_SHORT, indices, GL2ES2.GL_STATIC_DRAW);
		gl.glBindBuffer(GL2ES2.GL_ELEMENT_ARRAY_BUFFER, 0);
		return handle[0];
	}
}
