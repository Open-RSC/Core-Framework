package com.rscdaemon.scene;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;

/**
 * The default implementation of the {@link Node} interface provides a base 
 * from which to easily build new types of <code>Nodes</code>.
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
public abstract class DefaultNode<T extends GL>
	implements
		Node<T>
{
	
	private static final long serialVersionUID = -3245051797567910045L;

	/// The default (and only) proxy implementation
	private final InternalNodeProxy<T> proxy = 
			new InternalNodeProxy<T>()
	{
		@Override
		public final void setParent(Node<T> parent)
		{
			if(DefaultNode.this.parent != null)
			{
				throw new IllegalStateException("A node may only have one parent");
			}
			DefaultNode.this.parent = parent;
		}
	};

	/// The parent of this <code>Node</code> (set internally through proxy)
	private Node<T> parent;

	/// A list of the children of this <code>Node</code>
	private final List<Node<T>> children = new LinkedList<Node<T>>();
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Node<T> getParent()
	{
		return parent;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Collection<Node<T>> getChildren()
	{
		return Collections.unmodifiableCollection(children);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void add(Node<T> child)
	{
		children.add(child);
		child.getProxy().setParent(this);
		Collections.sort(children);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final InternalNodeProxy<T> getProxy()
	{
		return proxy;
	}
}
