package com.rscdaemon.scene;

import javax.media.opengl.GL;

import com.rscdaemon.Camera;
import com.rscdaemon.core.Axis;

/**
 * A type of {@link TransformGroup} that subjects its children to one or 
 * more rotations
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
public class RotationGroup<T extends GL>
	extends
		TransformGroup<T>
{

	private static final long serialVersionUID = -5131101761445717692L;

	/// The angle (in radians) to rotate
	private final float angle;
	
	/// A list of the axes to rotate around
	private final Axis[] axes;

	/**
	 * Constructs a <code>RotationGroup</code> with the provided angle and 
	 * list of axes
	 * 
	 * @param angle the number of radians to rotate
	 * 
	 * @param axes a list of the axes to rotate around
	 * 
	 */
	public RotationGroup(float angle, Axis... axes)
	{
		this.angle = angle;
		this.axes = axes;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void evaluate(T context, Camera camera)
	{
		// Apply the rotation to each specified axis
		for(Axis axis : axes)
		{
			camera.rotate(axis, angle);
		}
		for(Node<T> node : super.getChildren())
		{
			// Evaluate all children with defensive copies of the camera
			node.evaluate(context, new Camera(camera));
		}
	}
	
}
