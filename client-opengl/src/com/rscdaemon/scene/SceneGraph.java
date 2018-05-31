package com.rscdaemon.scene;

import java.util.List;

import javax.media.opengl.GL;

import com.rscdaemon.Camera;
import com.rscdaemon.core.ContextBound;

/**
 * <code>SceneGraphs</code> allow for the organisation of a virtual world 
 * through a tree structure.  <code>SceneGraphs</code> are navigated from 
 * top to bottom, left to right.
 * 
 * @author Zilent
 *
 * @param <T> the OpenGL specification that this <code>SceneGraph</code> 
 * utilizes
 * 
 * @version 1.0
 * 
 * @since 1.0
 * 
 */
public class SceneGraph<T extends GL>
	implements
		ContextBound<T>
{
	/// The single {@link RootNode} of this <code>SceneGraph</code>
	private final Node<T> root = new RootNode<T>();
	
	/// The OpenGL Context that this <code>SceneGraph</code> is bound to
	private final T context;
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public T getContext()
	{
		return context;
	}	
	
	/**
	 * Constructs a <code>SceneGraph</code> with the provided context
	 * 
	 * @param context the OpenGL Context that this <code>SceneGraph</code> 
	 * is bound to
	 * 
	 */
	public SceneGraph(T context)
	{
		this.context = context;
	}
	
	/**
	 * Adds the provided {@link Node} to this <code>SceneGraph</code>
	 * 
	 * @param node the {@link Node} to add
	 * 
	 */
	public void add(Node<T> node)
	{
		root.add(node);
	}
	
	/**
	 * Renders the entire scenegraph with the provided {@link Camera} and 
	 * retrieves a list of all entities that are hovered over by the cursor.
	 * 
	 * @param camera the {@link Camera} to render the scene with
	 * 
	 * @return a list of all entities that are hovered over by the cursor
	 * 
	 */
	public List<Comparable<?>> render(Camera camera)
	{
		// Evaluate with a copy of the camera
		root.evaluate(context, new Camera(camera));
		return null; // TODO: Picking
	}

	
}