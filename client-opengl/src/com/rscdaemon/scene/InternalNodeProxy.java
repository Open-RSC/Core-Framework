package com.rscdaemon.scene;

import javax.media.opengl.GL;

/**
 * A package-private interface that allows privileged internal mutations of 
 * {@link Node nodes}
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
interface InternalNodeProxy<T extends GL>
{
	/**
	 * Sets the parent of the the {@link Node} that is the target of this 
	 * proxy to the provided one
	 * 
	 * @param parent the {@link Node} to set as a parent
	 * 
	 * @throws IllegalStateException if the {@link Node} that is the target of 
	 * this proxy already has a parent
	 * 
	 */
	void setParent(Node<T> parent);
}
