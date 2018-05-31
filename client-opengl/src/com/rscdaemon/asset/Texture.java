package com.rscdaemon.asset;

public class Texture
	implements
		Asset
{
	private static final long serialVersionUID = 112898113616321719L;
	
	private static Texture[] temp = new Texture[55];
	
	static
	{
		for(int i = 0; i < 55; ++i)
		{
			temp[i] = new Texture(i);
		}
	}
	
	public static Texture getTexture(int id)
	{
		return temp[id];
	}
	
	private final static int SPRITESHEET_WIDTH = 1024;
	private final static int TEX_DIM = 128;
	
	private final static int TEXTURES_PER_ROW = SPRITESHEET_WIDTH / TEX_DIM;
	
	private final float xStart, xEnd, yStart, yEnd;
	
	public Texture(int id)
	{
		xStart = id % TEXTURES_PER_ROW;
		yStart = id / TEXTURES_PER_ROW;
		xEnd = xStart + 128;
		yEnd = yStart + 128;
	}
	
	public final float[] getTexCoords()
	{
		return new float[] {xStart, yStart, xEnd, yEnd};
	}

	@Override
	public String getName()
	{
		return "";
	}
}
