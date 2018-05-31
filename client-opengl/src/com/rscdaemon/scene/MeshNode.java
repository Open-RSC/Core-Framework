package com.rscdaemon.scene;

import javax.media.opengl.GL;

import com.rscdaemon.core.ContextBound;
import com.rscdaemon.core.Mesh;

/**
 * A type of {@link Node} that represents a piece of concrete geometry.
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
public abstract class MeshNode<T extends GL>
	extends
		DefaultNode<T>
	implements
		ContextBound<T>
{

	private static final long serialVersionUID = 2326760464681050566L;
	
	/// The OpenGL context that this <code>MeshNode</code> is bound to
	private final T context;
	
	/// Is this <code>MeshNode</code> pickable?
	private boolean pickable;
		
	/**
	 * Constructs a <code>MeshNode</code> with the provided {@link Mesh} and 
	 * pickable flag.
	 * 
	 * @param mesh the {@link Mesh} that holds the geometry of this 
	 * <code>MeshNode</code>
	 * 
	 * @param pickable Should this <code>MeshNode</code> be subject to picking?
	 * 
	 */
	public MeshNode(T context, Mesh mesh, boolean pickable)
	{
		this.context = context;
		this.pickable = pickable;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isPickable()
	{
		return pickable;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final T getContext()
	{
		return context;
	}
}
