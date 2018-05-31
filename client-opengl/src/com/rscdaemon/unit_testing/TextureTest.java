	

    package com.rscdaemon.unit_testing;
     
    import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.rscdaemon.Camera;
import com.rscdaemon.core.Tuple3F;
import com.rscdaemon.core.Vector3F;
import com.rscdaemon.glsl.FragmentShader;
import com.rscdaemon.glsl.Program;
import com.rscdaemon.glsl.Shader;
import com.rscdaemon.glsl.VertexShader;
import com.rscdaemon.internal.Platform;
import com.rscdaemon.internal.Utilities;
     
    public class TextureTest
            extends
                    JFrame
            implements
                    GLEventListener,
                    KeyListener
    {
     
            private static final long serialVersionUID = 4643417901524689562L;
           
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
           
            private final static float[] VERTEX_BUFFER = 
        	{
        		0.0f, 0.0f, 0.0f,		// x, y, z
        		0.0f, 0.0f, 1.0f,		// nx, ny, nz
        		1.0f, 1.0f, 1.0f, 1.0f,	// r, g, b, a
        		0.0f, 0.0f,				// u, v
        		
        		1.0f, 0.0f, 0.0f,
        		0.0f, 0.0f, 1.0f,
        		1.0f, 1.0f, 1.0f, 1.0f,
        		1.0f, 0.0f,
        		
        		1.0f, 1.0f, 0.0f,
        		0.0f, 0.0f, 1.0f,
        		1.0f, 1.0f, 1.0f, 1.0f,
        		1.0f, 1.0f,
        		0.0f, 1.0f, 0.0f,
        		0.0f, 0.0f, 1.0f,
        		1.0f, 1.0f, 1.0f, 1.0f,
        		0.0f, 1.0f
        	};
            
            private final static short[] INDEX_BUFFER =
	    	{
            	0, 1, 2, 2, 3, 0
	    	};
            
            private final static String VERTEX_SHADER_SRC =
            		"#version 110" + '\n' + 
                    "attribute vec3 xyz_in;" + '\n' +
                    "attribute vec3 normal_in;" + '\n' +
                    "attribute vec4 color_in;" + '\n' +
                    "attribute vec2 uv_in;" + '\n' +
                    "uniform mat4 translation;" + '\n' +
                    "uniform mat4 rotation;" + '\n' +
                    "uniform mat4 projection;" + '\n' +
                    "uniform vec4 light_dir_in;" + '\n' +
                   
                    "varying vec4 color_out;" + '\n' +
                    "varying vec2 uv_out;" + '\n' +
                    "void main()" + '\n' +
                    "{" + '\n' +
                            "gl_Position = projection * rotation * translation * vec4(xyz_in, 1);" + '\n' +
                            "vec4 normal = normalize(projection * rotation * translation * vec4(normal_in, 0));" + '\n' +
                            "float cosAI = dot(normal, light_dir_in);" + '\n' +
                            "cosAI = clamp(cosAI, 0.0, 1.0);" + '\n' +
                            "cosAI = max(cosAI, 0.4);" + '\n' +
                            "color_out = (color_in * cosAI);" + '\n' +
                            "uv_out = uv_in;" + '\n' +
                    "}";
           
             private final static String FRAGMENT_SHADER_SRC =
             		"#version 110" + '\n' + 
                              "uniform sampler2D sampler;" + '\n' +
                              "varying vec4 color_out;" + '\n' +
                              "varying vec2 uv_out;" + '\n' +
                              "void main()" + '\n' +
                              "{" + '\n' +
                               "if(abs(uv_out.x + 1.0) > 0.001)" + '\n' +
                               "{" + '\n' +
                                "gl_FragColor = color_out * texture2D(sampler, uv_out);" + '\n' +
                               "}" + '\n' +
                               "else" + '\n' +
                               "{" + '\n' +
                                "gl_FragColor = vec4(color_out.r, color_out.g, color_out.b, 1);" + '\n' +
                               "}" + '\n' +
                              "}";
     
            private Program program;
            private int vbo, ibo;
            private int polyCount;
            private Camera camera = new Camera();
           
            // Pointing straight down
            private Vector3F lightDirection = new Vector3F(0.0f, 0.0f, 1.0f);
            
            @Override
            public void display(GLAutoDrawable drawable)
            {
                    GL2ES2 gl = drawable.getGL().getGL2ES2();
                    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
                    gl.glEnable(GL2ES2.GL_TEXTURE_2D);
                    // Bind our program to the context
                    program.bind();
                   
                    // Set up our vertex buffer
                    gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo);
     
                    // Set up our vertex buffer attributes
                    program.setAttribute("xyz_in", XYZ_SIZE, XYZ_OFFSET * Platform.BYTES_PER_FLOAT, STRIDE);
                    program.setAttribute("normal_in", NORMAL_SIZE, NORMAL_OFFSET * Platform.BYTES_PER_FLOAT, STRIDE);
                    program.setAttribute("color_in", COLOR_SIZE, COLOR_OFFSET * Platform.BYTES_PER_FLOAT, STRIDE);
                    program.setAttribute("uv_in", UV_SIZE, UV_OFFSET * Platform.BYTES_PER_FLOAT, STRIDE);
                   
                    // Set up program uniforms
                    program.setUniform("light_dir_in", lightDirection.getX(), lightDirection.getY(), lightDirection.getZ(), 0);
                    program.setUniform("translation", camera.getTranslationMatrix());
                    program.setUniform("rotation", camera.getRotationMatrix());
                    program.setUniform("projection", camera.getProjectionMatrix());
                    program.setUniform("sampler", 0);
     
                    // Set up our index buffer
                    gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo);
                   
                    // Draw all of our polys
                    gl.glDrawElements(GL2.GL_TRIANGLES, polyCount, GL2.GL_UNSIGNED_SHORT, 0);
                   
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
     
            private Texture texture;
           
            @Override
            public void init(GLAutoDrawable drawable)
            {
                    camera.translate(new Tuple3F(0, 0, 2));
                    drawable.getGL().glEnable(GL2ES2.GL_TEXTURE_2D);
                    //camera.rotateX((float)Math.toRadians(-30.0f));
                    //camera.rotateY((float)Math.toRadians(-30.0f));
                    Shader vert = null;
                    Shader frag = null;
                    try
                    {
                            // TODO: Texturing not working...
                            texture = TextureIO.newTexture(new File("textures.png"), true);
                            texture.enable(drawable.getGL());
                            texture.setTexParameterf(drawable.getGL(), GL2ES2.GL_TEXTURE_MIN_FILTER, GL2ES2.GL_NEAREST);
                            texture.setTexParameterf(drawable.getGL(), GL2ES2.GL_TEXTURE_MAG_FILTER, GL2ES2.GL_NEAREST);
                            texture.setTexParameteri(drawable.getGL(), GL2ES2.GL_TEXTURE_WRAP_S, GL2ES2.GL_REPEAT);
                            texture.setTexParameteri(drawable.getGL(), GL2ES2.GL_TEXTURE_WRAP_T, GL2ES2.GL_REPEAT);
                            vert = new VertexShader(VERTEX_SHADER_SRC);
                            frag = new FragmentShader(FRAGMENT_SHADER_SRC);
                            program = new Program(vert, frag);
                            vbo = Utilities.createVBO(Buffers.newDirectFloatBuffer(VERTEX_BUFFER));
                            ibo = Utilities.createIBO(Buffers.newDirectShortBuffer(INDEX_BUFFER));
                            polyCount = INDEX_BUFFER.length;
                    } catch (GLException | IOException e1)
                    {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
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
     
            TextureTest()
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
                    new TextureTest().setVisible(true);
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

