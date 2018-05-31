package com.rscdaemon.unit_testing;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import com.rscdaemon.Camera;
import com.rscdaemon.glsl.FragmentShader;
import com.rscdaemon.glsl.Program;
import com.rscdaemon.glsl.Shader;
import com.rscdaemon.glsl.VertexShader;
import com.rscdaemon.internal.Platform;
import com.rscdaemon.internal.Utilities;

public class ShaderTest
	extends
		JFrame
	implements
		GLEventListener
{

	private static final long serialVersionUID = 4643417901524689562L;
	
	private final static int XYZ_OFFSET = 0;
	private final static int RGBA_OFFSET = 12;
	
	private final static int XYZ_SIZE = 3;
	private final static int RGBA_SIZE = 4;
	
	private final static int STRIDE = Platform.BYTES_PER_FLOAT * (XYZ_SIZE + RGBA_SIZE);
	
	private final FloatBuffer TRIANGLE_DATA = FloatBuffer.wrap(
		new float[]
		{
			0, 0, 0,	// First Vertex Position
			0, 0, 1, 1,	// First Vertex Color
			
			0, 1, 0,	// Second Vertex Position
			0, 1, 0, 1,	// Second Vertex Color
			
			1, 0, 0,	// Third Vertex Position
			1, 0, 0, 1	// Third Vertex Color
		}
	);
	
	private final ShortBuffer INDICES = ShortBuffer.wrap(
		new short[]
		{
			0, // First Vertex Data
			1, // Second Vertex Data
			2, // Third Vertex Data
		}
	);
	
	private final static String VERTEX_SHADER_SRC = 
		"attribute vec3 xyz_in;" + '\n' +
		"attribute vec4 rgba_in;" + '\n' +
		
		"uniform mat4 translation;" + '\n' +
		"uniform mat4 rotation;" + '\n' + 
		"uniform mat4 projection;" + '\n' +
	
		"varying vec4 rgba_out;" + '\n' +
	
		"void main()" + '\n' +
		"{" + '\n' +
			"gl_Position = projection * rotation * translation * vec4(xyz_in, 1);" + '\n' +
			"rgba_out = rgba_in;" + '\n' +
		"}";
	
	private final static String FRAGMENT_SHADER_SRC =
		"varying vec4 rgba_out;" + '\n' +
		
		"void main()" + '\n' +
		"{" + '\n' +
			"gl_FragColor = rgba_out;" + '\n' +
		"}";

	private Program program;
	private int vbo, ibo;
	private Camera camera = new Camera();
	
	@Override
	public void display(GLAutoDrawable drawable)
	{
		GL2ES2 gl = drawable.getGL().getGL2ES2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
		
		camera.position(0, 0, 4);
		
		// Bind our program to the context
		program.bind();
		
		// Set up our vertex buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo);

		// Set up our vertex buffer attributes
		program.setAttribute("xyz_in", XYZ_SIZE, XYZ_OFFSET, STRIDE);
		program.setAttribute("rgba_in", RGBA_SIZE, RGBA_OFFSET, STRIDE);
		
		// Set up program uniforms
		program.setUniform("translation", camera.getTranslationMatrix());
		program.setUniform("rotation", camera.getRotationMatrix());
		program.setUniform("projection", camera.getProjectionMatrix());
		
		// Set up our index buffer
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo);
		
		// Draw all of our polys
		gl.glDrawElements(GL2.GL_TRIANGLES, 3, GL2.GL_UNSIGNED_SHORT, 0);
		
		// Return context to pre-call state
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		program.unbind();
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
		program.dispose();
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		Shader vert = null;
		Shader frag = null;
		try
		{
			vert = new VertexShader(VERTEX_SHADER_SRC);
			frag = new FragmentShader(FRAGMENT_SHADER_SRC);
			program = new Program(vert, frag);			
			vbo = Utilities.createVBO(TRIANGLE_DATA);
			ibo = Utilities.createIBO(INDICES);			
		}
		finally
		{
			if(vert != null) vert.dispose();
			if(frag != null) frag.dispose();
		}
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		// Update the perspective projection of the camera
		camera.setProjection(0.01f, 10.0f, (float)width / (float)height, 45.0f);

		// Always render to the whole window
		drawable.getGL().getGL2().glViewport(x, y, width, height);
	}

	ShaderTest()
	{
		super.setSize(500, 500);
		GLCanvas panel = new GLCanvas();
		panel.addGLEventListener(this);
		super.getContentPane().add(panel);
		FPSAnimator animator = new FPSAnimator(60);
		animator.setUpdateFPSFrames(60, System.out);
		panel.setAnimator(animator);
		animator.add(panel);
		animator.start();
	}
	
	public static void main(String[] $)
	{
		ShaderTest test = new ShaderTest();
		test.setVisible(true);
	}
	
}
