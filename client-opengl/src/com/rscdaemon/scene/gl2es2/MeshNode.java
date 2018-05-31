package com.rscdaemon.scene.gl2es2;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLContext;

import com.rscdaemon.Camera;
import com.rscdaemon.core.Mesh;
import com.rscdaemon.glsl.Program;
import com.rscdaemon.internal.Platform;
import com.rscdaemon.scene.Node;

/**
 * A {@link com.rscdaemon.scene.MeshNode MeshNode} whose implementation is 
 * provided by the {@link GL2ES2 OpenGL ES 2.0 Specification}.
 * 
 * @author Zilent
 *
 * @param <T> the OpenGL specification that this node supports
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class MeshNode
	extends
		com.rscdaemon.scene.MeshNode<GL2ES2>
{

	private static final long serialVersionUID = -8852963340193863487L;
	private final static int XYZ_OFFSET = 0;
	private final static int XYZ_SIZE = 3;
	private final static int NORMAL_OFFSET = XYZ_OFFSET + XYZ_SIZE;
	private final static int NORMAL_SIZE = 3;
	private final static int COLOR_OFFSET = NORMAL_OFFSET + NORMAL_SIZE;
	private final static int COLOR_SIZE = 4;
	private final static int UV_OFFSET = COLOR_OFFSET + COLOR_SIZE;
	private final static int UV_SIZE = 2;
	
	private final static int STRIDE = Platform.BYTES_PER_FLOAT *
										(XYZ_SIZE +
										 NORMAL_SIZE +
										 COLOR_SIZE + 
										 UV_SIZE);
	
	private final Program program;
	private final Mesh mesh;
	
	public MeshNode(Mesh mesh, Program program, boolean pickable)
	{
		super(GLContext.getCurrentGL().getGL2ES2(), mesh, pickable);
		this.mesh = mesh;
		this.program = program;
	}

	@Override
	public void evaluate(GL2ES2 context, Camera camera)
	{
		// Bind our program to the context
		program.bind();
		
		// Set up our vertex buffer
		context.glBindBuffer(GL2.GL_ARRAY_BUFFER, mesh.getVBO());

		// Set up our vertex buffer attributes
		program.setAttribute("xyz_in", XYZ_SIZE, XYZ_OFFSET * Platform.BYTES_PER_FLOAT, STRIDE);
		program.setAttribute("normal_in", NORMAL_SIZE, NORMAL_OFFSET * Platform.BYTES_PER_FLOAT, STRIDE);
		program.setAttribute("color_in", COLOR_SIZE, COLOR_OFFSET * Platform.BYTES_PER_FLOAT, STRIDE);
		program.setAttribute("uv_in", UV_SIZE, UV_OFFSET * Platform.BYTES_PER_FLOAT, STRIDE);
		
		// Set up program uniforms
		//program.setUniform("light_dir_in", lightDirection.getX(), lightDirection.getY(), lightDirection.getZ(), 0);
		program.setUniform("translation", camera.getTranslationMatrix());
		program.setUniform("rotation", camera.getRotationMatrix());
		program.setUniform("projection", camera.getProjectionMatrix());
		program.setUniform("sampler", 0);
		
		// Set up our index buffer
		context.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBO());
		
		// Draw all of our polys
		context.glDrawElements(GL2.GL_TRIANGLES, mesh.getVertexCount(), GL2.GL_UNSIGNED_SHORT, 0);
		
		// Return context to pre-call state
		context.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
		context.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
		program.unbind();
	}

	@Override
	public int compareTo(Node<GL2ES2> o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
