package com.rscdaemon.scene;

import javax.media.opengl.GL;

import com.rscdaemon.Camera;
import com.rscdaemon.core.Tuple3F;

/**
 * A type of {@link TransformGroup} that subjects its children to a 
 * linear translation.
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
public final class TranslationGroup<T extends GL>
	extends
		TransformGroup<T>
{
	private static final long serialVersionUID = -6990105192513369263L;
	
	/// The tuple that defines the translation
	// TODO: Finish core items and make this a vector object
	private Tuple3F translation;

	/**
	 * Constructs a <code>TranslationGroup</code> with the provided values
	 * 
	 * @param xTranslation the number of units to translate along the x-axis
	 * 
	 * @param yTranslation the number of units to translate long the y-axis
	 * 
	 * @param zTranslation the number of units to translate long the z-axis
	 * 
	 */
	public TranslationGroup(float xTranslation, float yTranslation, float zTranslation)
	{
		this.translation = new Tuple3F(xTranslation, yTranslation, zTranslation);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void evaluate(T context, Camera camera)
	{
		// Apply this translation to the camera
		camera.translate(translation);
		for(Node<T> child : super.getChildren())
		{
			// Evaluate all children with defensive copies of the camera
			child.evaluate(context, new Camera(camera));
		}
	}
}
