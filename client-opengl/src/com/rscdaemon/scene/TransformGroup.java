package com.rscdaemon.scene;

import javax.media.opengl.GL;

/**
 * A type of {@link Node} that subjects its children to a certain 
 * transformation.  <code>TransformGroups</code> are always pickable and 
 * are always evaluated in the exact order that they are added to their 
 * {@link SceneGraph}.  For example:
 * 
 * <pre>
 * SceneGraph sg = new SceneGraph();
 * TransformGroup translate = new TranslationGroup(1, 0, 0);
 * TransformGroup rotate = new Rotation(Math.toRadians(90), Axis.X);
 *  
 *  if(translateThenRotate)
 *  {
 *    translate.add(rotate);
 *    sg.add(translate);
 *  }
 *  else if(rotateThenTranslate)
 *  {
 *  	rotate.add(translate);
 *  	sg.add(rotate);
 *  }
 * </pre>
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
abstract class TransformGroup<T extends GL>
	extends
		DefaultNode<T>
{

	private static final long serialVersionUID = 1977906743057446484L;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isPickable()
	{
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final int compareTo(Node<T> other)
	{
		return 0;
	}
}
