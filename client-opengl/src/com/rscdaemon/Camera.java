package com.rscdaemon;

import java.util.Arrays;

import com.rscdaemon.core.Axis;
import com.rscdaemon.core.Tuple3F;

// Sort of hacky IMO...will have to think about it.
public class Camera
{

	static final private float[] getIdentityMatrix()
	{
		return new float[]
		{
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1
		};
	}
	
	static final private float[] getZeroMatrix()
	{
		return new float[]
		{
			0, 0, 0, 0,
			0, 0, 0, 0,
			0, 0, 0, 0,
			0, 0, 0, 0
		};
	}

	/// A matrix representing the x, y, and z translation of the camera
	private final float[] translationMatrix;
	
	/// A matrix representing the x, y, and z scales of the camera
	private final float[] scaleMatrix;

	/// A matrix representing the x, y, and z rotations of the camera
	private final float[] rotationMatrix;
	
	/// A matrix representing the projection from eye space to clip space
	private final float[] projectionMatrix;

	public Camera(Camera camera)
	{
		this.translationMatrix = Arrays.copyOf(camera.translationMatrix, 16);
		this.scaleMatrix = Arrays.copyOf(camera.scaleMatrix,  16);
		this.rotationMatrix = Arrays.copyOf(camera.rotationMatrix, 16);
		this.projectionMatrix = Arrays.copyOf(camera.projectionMatrix, 16);
	}
	
	public Camera()
	{
		this.translationMatrix = getIdentityMatrix();
		this.scaleMatrix = getIdentityMatrix();
		this.rotationMatrix = getIdentityMatrix();
		this.projectionMatrix = getZeroMatrix();
	}
	
	/**
	 * Positions the camera at the provided position
	 * 
	 * @param x the x-coordinate
	 * 
	 * @param y the y-coordinate
	 * 
	 * @param z the z-coordinate
	 * 
	 */
	public void position(float x, float y, float z)
	{
		translationMatrix[12] = -x;
		translationMatrix[13] = -y;
		translationMatrix[14] = -z;
	}

	/**
	 * Retrieves the translation matrix of this camera
	 * 
	 * @return the translation matrix of this camera
	 * 
	 */
	public float[] getTranslationMatrix()
	{
		return translationMatrix;
	}

	/**
	 * Sets the scale of each axis
	 * 
	 * @param x the scale of the x-axis
	 * 
	 * @param y the scale of the y-axis
	 * 
	 * @param z the scale of the z-axis
	 * 
	 */
	public void scale(float x, float y, float z)
	{
		scaleMatrix[0] = x;
		scaleMatrix[5] = y;
		scaleMatrix[10] = z;
	}
	
	/**
	 * Retrieves the scale matrix of this camera
	 * 
	 * @return the scale matrix of this camera
	 * 
	 */
	public float[] getScaleMatrix()
	{
		return scaleMatrix;
	}
	
	/**
	 * Rotates this camera theta radians around the x-axis
	 * 
	 * @param theta the angle (in radians) to rotate
	 * 
	 */
	public void rotateX(float theta)
	{
		rotationMatrix[5] = rotationMatrix[10] = (float)Math.cos(theta);
		rotationMatrix[9] = (float)Math.sin(theta);
		rotationMatrix[6] = -rotationMatrix[9];
	}
	
	/**
	 * Rotates this camera theta radians around the y-axis
	 * 
	 * @param theta the angle (in radians) to rotate
	 * 
	 */
	public void rotateY(float theta)
	{
		rotationMatrix[0] = rotationMatrix[10] = (float)Math.cos(theta);
		rotationMatrix[2] = (float)Math.sin(theta);
		rotationMatrix[8] = -rotationMatrix[2];
	}
	
	/**
	 * Rotates this camera theta radians around the z-axis
	 * 
	 * @param theta the angle (in radians) to rotate
	 * 
	 */
	public void rotateZ(float theta)
	{
		rotationMatrix[0] = rotationMatrix[5] = (float)Math.cos(theta);
		rotationMatrix[1] = (float)Math.sin(theta);
		rotationMatrix[4] = -rotationMatrix[1];
	}
	

	public void rotate(Axis axis, float angle)
	{
		switch(axis)
		{
		case X:
			rotateX(angle);
			break;
		case Y:
			rotateY(angle);
			break;
		case Z:
			rotateZ(angle);
			break;
		default:
			assert false;
		}
	}
	
	public float[] getRotationMatrix()
	{
		return rotationMatrix;
	}
	
	/**
	 * Applies the perspective projection to this camera
	 * 
	 * @param zNear the minimum clip distance
	 * 
	 * @param zFar the maximum clip distance
	 * 
	 * @param aspectRatio the aspect ratio of the screen
	 * 
	 * @param angleOfView the angle of the viewing frustrum
	 * 
	 */
	public void setProjection(float zNear, float zFar, float aspectRatio, float angleOfView)
	{
		float size = zNear * (float)Math.tan(Math.toRadians(angleOfView)) / 2.0f;
		float left = size;
		float right = -left;
		float top = size / aspectRatio;
		float bottom = -top;
		projectionMatrix[0] = 2 * zNear / (right - left);
		projectionMatrix[5] = 2 * zNear / (top - bottom);
		projectionMatrix[8] = (right + left) / (right - left);
		projectionMatrix[9] = (top + bottom) / (top - bottom);
		projectionMatrix[10] = -(zFar - zNear) / (zFar - zNear);
		projectionMatrix[11] = -1;
		projectionMatrix[14] = -(2 * zFar * zNear) / (zFar - zNear);
	}
	
	public float[] getProjectionMatrix()
	{
		return projectionMatrix;
	}

	public void translate(Tuple3F translation)
	{
		translationMatrix[12] -= translation.getX();
		translationMatrix[13] -= translation.getY();
		translationMatrix[14] -= translation.getZ();
	}

}
