package org.rscangel.client.model;

import org.rscangel.client.util.PersistenceManager;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

public class Sprite
{
	private static final int TRANSPARENT = Color.BLACK.getRGB();

	private int[] pixels;
	private int width;
	private int height;

	/**
	 * WARNING: packageName, id, xShift, yShift, something1, something2 are lost
	 * when loading from img. *
	 */

	private String packageName = "unknown";
	private int id = -1;

	private boolean requiresShift;
	private int xShift = 0;
	private int yShift = 0;

	private int something1 = 0;
	private int something2 = 0;

	public Sprite()
	{
		pixels = new int[0];
		width = 0;
		height = 0;
	}

	public Sprite( int[] pixels, int width, int height )
	{
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}

	public void setSomething( int something1, int something2 )
	{
		this.something1 = something1;
		this.something2 = something2;
	}

	public int getSomething1()
	{
		return something1;
	}

	public int getSomething2()
	{
		return something2;
	}

	public void setName( int id, String packageName )
	{
		this.id = id;
		this.packageName = packageName;
	}

	public int getID()
	{
		return id;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setShift( int xShift, int yShift )
	{
		this.xShift = xShift;
		this.yShift = yShift;
	}

	public void setRequiresShift( boolean requiresShift )
	{
		this.requiresShift = requiresShift;
	}

	public boolean requiresShift()
	{
		return requiresShift;
	}

	public int getXShift()
	{
		return xShift;
	}

	public int getYShift()
	{
		return yShift;
	}

	public int[] getPixels()
	{
		return pixels;
	}

	public int getPixel( int i )
	{
		return pixels[i];
	}

	public void setPixel( int i, int val )
	{
		pixels[i] = val;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public String toString()
	{
		return "id = " + id + "; package = " + packageName;
	}

	/** IO Operations * */

	public void serializeTo( File file ) throws IOException
	{
		PersistenceManager.write( file, this );
	}

	public static Sprite deserializeFrom( File file ) throws IOException,
			ClassNotFoundException
	{
		return (Sprite) PersistenceManager.load( file );
	}

	public BufferedImage toImage()
	{
		BufferedImage img = new BufferedImage( width, height,
				BufferedImage.TYPE_INT_RGB );
		for( int y = 0; y < height; y++ )
		{
			for( int x = 0; x < width; x++ )
			{
				img.setRGB( x, y, pixels[x + y * width] );
			}
		}
		return img;
	}

	public static Sprite fromImage( BufferedImage img )
	{
		int[] pixels = new int[img.getWidth() * img.getHeight()];
		for( int y = 0; y < img.getHeight(); y++ )
		{
			for( int x = 0; x < img.getWidth(); x++ )
			{
				int rgb = img.getRGB( x, y );
				if( rgb == TRANSPARENT )
				{
					rgb = 0;
				}
				pixels[x + y * img.getWidth()] = rgb;
			}
		}
		return new Sprite( pixels, img.getWidth(), img.getHeight() );
	}

	/**
	 * Writes the sprites raw data into a ByteBuffer
	 */
	public ByteBuffer pack() throws IOException
	{
		ByteBuffer out = ByteBuffer.allocate( 25 + (pixels.length * 4) );

		out.putInt( width );
		out.putInt( height );

		out.put( (byte) (requiresShift ? 1 : 0) );
		out.putInt( xShift );
		out.putInt( yShift );

		out.putInt( something1 );
		out.putInt( something2 );

		for( int c = 0; c < pixels.length; c++ )
		{
			out.putInt( pixels[c] );
		}

		out.flip();
		return out;
	}

	/**
	 * Create a new sprite from raw data packed into the given ByteBuffer
	 */
	public static Sprite unpack( ByteBuffer in ) throws IOException
	{
		if( in.remaining() < 25 )
		{
			throw new IOException(
					"Provided buffer too short - Headers missing" );
		}
		int width = in.getInt();
		int height = in.getInt();

		boolean requiresShift = in.get() == 1;
		int xShift = in.getInt();
		int yShift = in.getInt();

		int something1 = in.getInt();
		int something2 = in.getInt();

		int[] pixels = new int[width * height];
		if( in.remaining() < (pixels.length * 4) )
		{
			throw new IOException( "Provided buffer too short - Pixels missing" );
		}
		for( int c = 0; c < pixels.length; c++ )
		{
			pixels[c] = in.getInt();
		}

		Sprite sprite = new Sprite( pixels, width, height );
		sprite.setRequiresShift( requiresShift );
		sprite.setShift( xShift, yShift );
		sprite.setSomething( something1, something2 );

		return sprite;
	}
}
