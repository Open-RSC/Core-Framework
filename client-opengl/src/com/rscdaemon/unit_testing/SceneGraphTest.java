package com.rscdaemon.unit_testing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.rscdaemon.Camera;
import com.rscdaemon.core.Mesh;
import com.rscdaemon.core.Tuple3F;
import com.rscdaemon.glsl.FragmentShader;
import com.rscdaemon.glsl.Program;
import com.rscdaemon.glsl.Shader;
import com.rscdaemon.glsl.VertexShader;
import com.rscdaemon.io.MeshFactory;
import com.rscdaemon.scene.SceneGraph;
import com.rscdaemon.scene.TranslationGroup;
import com.rscdaemon.scene.gl2es2.MeshNode;

public class SceneGraphTest
	extends
		JFrame
	implements
		GLEventListener,
		KeyListener
{

	private static final long serialVersionUID = 4643417901524689562L;
	

	
	private final static String VERTEX_SHADER_SRC = 
		"attribute vec3 xyz_in;" + '\n' +
		"attribute vec3 normal_in;" + '\n' +
		"attribute vec4 color_in;" + '\n' + 
		"attribute vec2 uv_in;" + '\n' + 
		"uniform mat4 translation;" + '\n' +
		"uniform mat4 rotation;" + '\n' + 
		"uniform mat4 projection;" + '\n' +
		"uniform vec4 light_dir_in;" + '\n' + 
		
		"out vec4 color_out;" + '\n' + 
		"out vec2 uv_out;" + '\n' + 
		"void main()" + '\n' +
		"{" + '\n' +
			"gl_Position = projection * rotation * translation * vec4(xyz_in, 1);" + '\n' +
			"vec4 normal = normalize(projection * rotation * translation * vec4(normal_in, 0));" + '\n' + 
			"float cosAI = dot(normal, light_dir_in);" + '\n' +
			"cosAI = clamp(cosAI, 0, 1);" + '\n' + 
			"cosAI = max(cosAI, 0.4);" + '\n' + 
			"color_out = (color_in * cosAI);" + '\n' + 
			"uv_out = uv_in;" + '\n' + 
		"}";
	
	private final static String FRAGMENT_SHADER_SRC =
		"uniform sampler2D sampler;" + '\n' +
		"varying vec4 color_out;" + '\n' + 
		"varying vec2 uv_out;" + '\n' +
		"void main()" + '\n' +
		"{" + '\n' +
			"if(uv_out.x != -1)" + '\n' +
			"{" + '\n' +
				"gl_FragColor = color_out * texture2D(sampler, uv_out);" + '\n' +
			"}" + '\n' +
			"else" + '\n' + 
			"{" + '\n' +
				"gl_FragColor = vec4(color_out.r, color_out.g, color_out.b, 1);" + '\n' +
			"}" + '\n' + 
		"}";

	private Program program;
	private Camera camera = new Camera();
	
	private SceneGraph<GL2ES2> sceneGraph;
	
	@Override
	public void display(GLAutoDrawable drawable)
	{
		drawable.getGL().glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		texture.bind(drawable.getGL());
		sceneGraph.render(camera);
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
		program.dispose();
	}
	private Texture texture;
	@Override
	public void init(GLAutoDrawable drawable)
	{
		//drawable.getGL().getGL2ES2().glBlendColor(1.0f, 1.0f, 1.0f, 1.0f);
		drawable.getGL().glEnable(GL2.GL_BLEND);
		drawable.getGL().glEnable(GL.GL_DEPTH_TEST);
	//	drawable.getGL().getGL2ES2().glBlendFunc(GL2ES2.GL_ONE_MINUS_SRC_COLOR, GL2ES2.GL_SRC_COLOR);
		camera.rotateY((float)Math.toRadians(180.0f));
		camera.translate(new Tuple3F(0, 50.0f, -250.0f));
		sceneGraph = new SceneGraph<GL2ES2>(drawable.getGL().getGL2ES2());
		Shader vert = null;
		Shader frag = null;
		try
		{
			vert = new VertexShader(VERTEX_SHADER_SRC);
			frag = new FragmentShader(FRAGMENT_SHADER_SRC);
			program = new Program(vert, frag);
			Mesh mesh = null;
			try
			{
				// TODO: Texturing not working...
				texture = TextureIO.newTexture(new File("textures.png"), true);
				texture.enable(drawable.getGL());
				texture.setTexParameterf(drawable.getGL(), GL2ES2.GL_TEXTURE_MIN_FILTER, GL2ES2.GL_NEAREST);
				texture.setTexParameterf(drawable.getGL(), GL2ES2.GL_TEXTURE_MAG_FILTER, GL2ES2.GL_NEAREST);
				texture.setTexParameteri(drawable.getGL(), GL2ES2.GL_TEXTURE_WRAP_S, GL2ES2.GL_REPEAT);
				texture.setTexParameteri(drawable.getGL(), GL2ES2.GL_TEXTURE_WRAP_T, GL2ES2.GL_REPEAT);
				mesh = MeshFactory.getInstance("OBJ").loadMesh("test.obj");
			} catch (IOException e)
			{
				System.err.println("Error loading mesh: " + e.getMessage());
				System.exit(-1);
			}
			// 'mesh' is our table, so add a table to (0, 0, 0)
			sceneGraph.add(new MeshNode(mesh, program, true));
			
			// Make a translation group set to (100, 0, 0)
			TranslationGroup<GL2ES2> translation = new TranslationGroup<GL2ES2>(100, 0, 0);
			
			// Add another table to the translation (so a new table at (100, 0, 0)
			translation.add(new MeshNode(mesh, program, true));
			
			// Add the translation to the scenegraph
			sceneGraph.add(translation);
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
		camera.setProjection(1f, 1000.0f, (float)width / (float)height, 45.0f);

		// Always render to the whole window
		drawable.getGL().getGL2().glViewport(x, y, width, height);
	}

	SceneGraphTest()
	{
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setSize(500, 500);
		GLCanvas panel = new GLCanvas();
		panel.addGLEventListener(this);
		panel.addKeyListener(this);
		super.getContentPane().add(panel);
		FPSAnimator animator = new FPSAnimator(60);
		animator.setUpdateFPSFrames(60, System.out);
		panel.setAnimator(animator);
		animator.add(panel);
		animator.start();
	}
	
	public static void main(String[] $)
	{
		SceneGraphTest test = new SceneGraphTest();
		test.setVisible(true);
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		switch(arg0.getKeyCode())
		{
			case KeyEvent.VK_LEFT:
				camera.translate(new Tuple3F(-1, 0, 0));
				break;
			case KeyEvent.VK_RIGHT:
				camera.translate(new Tuple3F(1, 0, 0));
				break;
			case KeyEvent.VK_UP:
				camera.translate(new Tuple3F(0, 0, 1));
				break;
			case KeyEvent.VK_DOWN:
				camera.translate(new Tuple3F(0, 0, -1));
				break;
			default:
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
}
