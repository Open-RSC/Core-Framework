package com.rscdaemon.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import com.rscdaemon.internal.Platform;
import com.rscdaemon.internal.Utilities;

public class Mesh
{	
	private final int[] buffers = new int[2];
	private final int vertexCount;
	
	private final static int XYZ_SIZE = 3;
	private final static int NORMAL_SIZE = 4;
	private final static int COLOR_SIZE = 4;
	private final static int TEX_SIZE = 2;
	
	private final static int VBO_STRIDE = XYZ_SIZE + 
										  NORMAL_SIZE + 
										  COLOR_SIZE + 
										  TEX_SIZE;
	
	public Mesh(List<Point3F> points, List<Vector3F> normals, List<Color3F> colors, List<Point2F> texCoords, List<VertexAttributes> indices)
	{
		vertexCount = indices.size();
		FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexCount * VBO_STRIDE * Platform.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ShortBuffer indexBuffer = ByteBuffer.allocateDirect(vertexCount * Platform.BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
		short i = 0;
		for(VertexAttributes attribs : indices)
		{
			Point3F point = points.get(attribs.getPositionIndex());
			vertexBuffer.put(point.getX()).put(point.getY()).put(point.getZ());
			
			Vector3F normal = normals.get(attribs.getNormalIndex());
			vertexBuffer.put(normal.getX()).put(normal.getY()).put(normal.getZ());

			int colorIndex = attribs.getColorIndex();
			if(colorIndex < 0)
			{
				vertexBuffer.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
			}
			else
			{
				Color3F color = colors.get(colorIndex);
				vertexBuffer.put(color.getX()).put(color.getY()).put(color.getZ()).put(1.0f);				
			}

			int textureIndex = attribs.getTexCoordsIndex();
			if(textureIndex < 0)
			{
				vertexBuffer.put(-1).put(-1);
			}
			else
			{
				Point2F texCoord = texCoords.get(textureIndex);
				vertexBuffer.put(texCoord.getX()).put(texCoord.getY());
			}
			// TODO: UV COORDS
			indexBuffer.put(i++);
		}
		vertexBuffer.position(0);
		indexBuffer.position(0);
		buffers[0] = Utilities.createVBO(vertexBuffer);
		buffers[1] = Utilities.createIBO(indexBuffer);
	}
		
	public int getVertexCount()
	{
		return vertexCount;
	}

	public int getVBO()
	{
		return buffers[0];
	}
	
	public int getIBO()
	{
		return buffers[1];
	}

}
