package com.rscdaemon.core;

public class VertexAttributes
{
	private final int position;
	private final int normal;
	private final int color;
	private final int texCoords;
	
	public VertexAttributes(int position, int normal, int color, int texCoords)
	{
		this.position = position;
		this.normal = normal;
		this.color = color;
		this.texCoords = texCoords;
	}
	
	public final int getPositionIndex()
	{
		return position;
	}
	
	public final int getNormalIndex()
	{
		return normal;
	}
	
	public final int getColorIndex()
	{
		return color;
	}
	
	public final int getTexCoordsIndex()
	{
		return texCoords;
	}
}
