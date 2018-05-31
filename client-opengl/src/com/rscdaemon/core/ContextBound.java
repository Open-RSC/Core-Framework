package com.rscdaemon.core;

import javax.media.opengl.GL;

/**
 * This interface represents an object that is bound to an OpenGL context
 * 
 * @author Zilent
 *
 * @param <T> the interface of the minimum OpenGL version
 * 
 * @version 1.0
 * 
 * @since 1.0
 * 
 */
public interface ContextBound<T extends GL>
{
	/**
	 * Retrieves the OpenGL context that this object is bound to.  Generally, 
	 * this is the context present on the thread that created this object.
	 * 
	 * <strong>It is an error to use this object with a context different 
	 * than the one returned by this method!</strong>
	 * 
	 * @return  the OpenGL context that this object is bound to
	 * 
	 * @since 1.0
	 * 
	 */
	T getContext();
}
