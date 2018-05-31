package com.rscdaemon.scene;

import javax.media.opengl.GL;

import com.rscdaemon.Camera;

/**
 * A special type of {@link Node} that is top-most in the {@link SceneGraph} 
 * hierarchy.  <code>RootNodes</code> are pickable, and are always considered 
 * to be greater than every other node in the hierarchy.  This class should 
 * not be used outside of this package.
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
final class RootNode<T extends GL>
	extends
		DefaultNode<T>
{
	
	private static final long serialVersionUID = -501103386864037520L;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isPickable()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void evaluate(T context, Camera camera)
	{
		for(Node<T> child : super.getChildren())
		{
			child.evaluate(context, camera);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compareTo(Node<T> arg0)
	{
		return Integer.MAX_VALUE;
	}
}
