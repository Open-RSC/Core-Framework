package com.rscdaemon.scene;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;

import javax.media.opengl.GL;

import com.rscdaemon.Camera;

/**
 * Nodes are nestable objects that populate {@link SceneGraph scene graphs}.
 * Nodes may only have a single parent which must be higher in 
 * the {@link SceneGraph} than themselves.  Sibling nodes (that is, nodes 
 * with the same parent), are allowed to sort themselves via the 
 * {@link Comparable} interface in order to determine the order of their 
 * evaluation.
 * 
 * @author Zilent
 *
 * @param <T> the OpenGL specification that this node supports
 * 
 * @version 1.0
 * 
 * @since 1.0
 * 
 * @see Comparable
 * 
 * @see GL
 * 
 * @see SceneGraph
 * 
 * @see Serializable
 * 
 */
public interface Node<T extends GL>
	extends
		Comparable<Node<T>>,
		Serializable
{
	/**
	 * Retrieves the parent of this <code>Node</code>.  Note that this method 
	 * may return null if this node has no parent.
	 * 
	 * @return the parent of this <code>Node</code>, or null if no such parent 
	 * exists
	 * 
	 */
	Node<T> getParent();
		
	/**
	 * Retrieves a read-only collection of the children of this 
	 * <code>Node</code>
	 * 
	 * @return a read-only collection of the children of this <code>Node</code>
	 * 
	 */
	Collection<Node<T>> getChildren();
	
	/**
	 * Attempts to add the provided <code>Node</code> as a child of this 
	 * <code>Node</code>.  Upon successful addition, the children of this 
	 * <code>Node</code> are sorted based on their implementations of the 
	 * {@link Comparator} interface.
	 * 
	 * @param child the <code>Node</code> to add
	 * 
	 * @throws IllegalStateException if the provided <code>Node</code> already 
	 * has a parent
	 * 
	 * @throws UnsupportedOperationException if this <code>Node</code> does 
	 * not allow the addition of children
	 * 
	 */
	void add(Node<T> child);

	/**
	 * Are this <code>Node</code> and its children eligible for picking?
	 * 
	 * @return true if picking can occur, otherwise false
	 * 
	 */
	boolean isPickable();

	/**
	 * Invoked by the {@link SceneGraph} that hosts this <code>Node</code>, 
	 * this method is charged with the following requirements:
	 * <ul>
	 * 	<li>Apply any local transformations to the provided {@link Camera}</li>
	 * 	<li>Perform any operations on itself and its children that should 
	 * happen during the traversal of its branch</li>
	 * 	<li>Leave the provided context in the same state that it was in prior 
	 *  to this method.</li>
	 * </ul>
	 * 
	 * @param context the OpenGL context to which this node is bound
	 * 
	 * @param camera the {@link Camera} that defines the transformation 
	 * process between world space and clip space
	 * 
	 */
	void evaluate(T context, Camera camera);

	/**
	 * <strong>Internal Use Only</strong>
	 * <br>
	 * <br>
	 * Retrieves an instance of a package-private proxy that is capable of 
	 * mutating <code>Nodes</code> at the interface level without providing 
	 * external packages the means by which to do so.  This pattern allows 
	 * internal operations with little to no code duplication.
	 * 
	 * @return a package-private proxy object
	 * 
	 */
	InternalNodeProxy<T> getProxy();
}
