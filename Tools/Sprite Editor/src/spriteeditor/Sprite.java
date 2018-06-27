package spriteeditor;

import spriteeditor.util.PersistenceManager;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

/**
 * Defines a Sprite object
 *
 * WARNING: 
 *	 * packageName, id, xShift, yShift, width2, height2 
 *	 * are lost when loading from img. packageName doesn't 
 *	 * matter, but the variables others do.
 */
public class Sprite 
{
	/**
	 * Declares which colour to render transparent
	 */
	private static final int TRANSPARENT = Color.BLACK.getRGB();
	/**
	 * Holds all the pixel values for the sprite image
	 */
	private int[] pixels;
	/**
	 * The sprite's width
	 */
	private int width;
	/**
	 * The sprite's height
	 */
	private int height;
	/**
	 * The package name of this sprite (unimportant)
	 */
	private String packageName = "unknown";
	/**
	 * The id of this sprite
	 */
	private int id = -1;
	/**
	 * Whether or not this sprite requires a coordinate shift when rendering
	 */
	private boolean requiresShift;
	/**
	 * If it does require a shift, this is how many x pixels it shifts by
	 */
	private int xShift = 0;
	/**
	 * If it does require a shift, this is how many y pixels it shifts by
	 */
	private int yShift = 0;
	/**
	 * This is not a fully-understood variable, but it seems to usually 
	 * represent the variable 'width's value
	 */
	private int width2 = 0;
	/**
	 * This is not a fully-understood variable, but it seems to usually 
	 * represent the variable 'height's value
	 */
	private int height2 = 0;
	
	/**
	 * Constructs a new sprite with no variable settings
	 */
	public Sprite() 
	{
		pixels = new int[0];
		width = 0;
		height = 0;
	}
	
	/**
	 * Constructs a new sprite with the given pixel data, width and height
	 */
	public Sprite(int[] pixels, int width, int height) 
	{
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Sets the width and height 'clones' to the given values
	 * @param width2 the new width2 value
	 * @param height2 the new height2 value
	 */
	public void setSomething(int width2, int height2) 
	{
		this.width2 = width2;
		this.height2 = height2;
	}
	
	/**
	 * @return this sprite's width2 var
	 */
	public int getWidth2() 
	{
		return width2;
	}
	
	/**
	 * @return this sprite's height2 var
	 */
	public int getHeight2() 
	{
		return height2;
	}
	
	/**
	 * Sets this sprite's id and package name
	 * @param this sprite's new id
	 * @param packageName this sprite's package name
	 */
	public void setName(int id, String packageName) 
	{
		this.id = id;
		this.packageName = packageName;
	}
	
	/**
	 * @return this sprite's ID
	 */
	public int getID() 
	{
		return id;
	}
	
	/**
	 * @return this sprite's package name
	 */
	public String getPackageName() 
	{
		return packageName;
	}
	
	/**
	 * Sets this sprite's shift vars
	 * @param xShift how far to shift this sprite along x axis
	 * @param yShift how far to shift this sprite along y axis
	 */
	public void setShift(int xShift, int yShift) 
	{
		this.xShift = xShift;
		this.yShift = yShift;
	}
	
	/**
	 * Sets whether or not this sprite needs to shift
	 * @param requiresShift the shift flag
	 */
	public void setRequiresShift(boolean requiresShift)
	{
		this.requiresShift = requiresShift;
	}
	
	/**
	 * @return if this sprite needs to be shifted
	 */
	public boolean requiresShift() 
	{
		return requiresShift;
	}
	
	/**
	 * @return this sprite's x shift value
	 */
	public int getXShift() 
	{
		return xShift;
	}
	
	/**
	 * @return this sprite's y shift value
	 */
	public int getYShift() 
	{
		return yShift;
	}
	
	/**
	 * @return all this sprite's pixel data
	 */
	public int[] getPixels() 
	{
		return pixels;
	}
	
	/**
	 * @param i the array index to get
	 * @return the given pixel from the pixel data array
	 */
	public int getPixel(int i) 
	{
		if(i < 0 || i >= pixels.length)
		{
			System.out.println("getPixel(" + i + ") out of bounds: max = " + pixels.length);
			return -1;
		}
		
		return pixels[i];
	}
	
	/**
	 * Sets the given pixel index to the given value
	 * @param i the pixel index
	 * @param val the pixel value
	 */
	public void setPixel(int i, int val) 
	{
		if(i < 0 || i >= pixels.length)
		{
			System.out.println("setPixel(" + i + ", " + val + ") out of bounds: max = " + pixels.length);
			return;
		}
		
		pixels[i] = val;
	}
	
	/**
	 * @return this sprite's width
	 */
	public int getWidth() 
	{
		return width;
	}
	
	/**
	 * @return this sprite's height
	 */
	public int getHeight() 
	{
		return height;
	}
	
	/**
	 * @return a string representation of this sprite object
	 */
	public String toString() 
	{
		return "id = " + id + "; package = " + packageName;
	}
	
	/** IO Operations **/
	
	/**
	 * Serializes this sprite object into the given file
	 * @param file the file to save this sprite object in to
	 */
	public void serializeTo(File file) throws IOException 
	{
		PersistenceManager.write(file, this);
	}
	
	/**
	 * @param file the file to read from
	 * @return a sprite deserialized from the given file
	 */
	public static Sprite deserializeFrom(File file) throws IOException, ClassNotFoundException 
	{
		return (Sprite)PersistenceManager.load(file);
	}
	
	/**
	 * @return this sprite's data into a buffered image
	 */
	public BufferedImage toImage() 
	{
  		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  		
  		for(int y = 0; y < height; y++) 
  		{
  			for(int x = 0; x < width; x++)
  				img.setRGB(x, y, pixels[x + y * width]);
  		}
  		
  		return img;
	}
	
	/**
	 * @return a sprite object from a buffered image's data
	 */
	public static Sprite fromImage(BufferedImage img) 
	{
  		int[] pixels = new int[img.getWidth() * img.getHeight()];
  		
  		for(int y = 0; y < img.getHeight(); y++) 
  		{
  			for(int x = 0; x < img.getWidth(); x++) 
  			{
  				int rgb = img.getRGB(x, y);
  				
  				if(rgb == TRANSPARENT)
  					rgb = 0;
  					
  				pixels[x + y * img.getWidth()] = rgb;
  			}
  		}
  		
  		return new Sprite(pixels, img.getWidth(), img.getHeight());
	}
	
	/**
	 * Writes the sprites raw data into a ByteBuffer
	 */
	public ByteBuffer pack() throws IOException 
	{
		ByteBuffer out = ByteBuffer.allocate(25 + (pixels.length * 4));
		
		out.putInt(width);
		out.putInt(height);
		
		out.put((byte)(requiresShift ? 1 : 0));
		out.putInt(xShift);
		out.putInt(yShift);
		
		out.putInt(width2);
		out.putInt(height2);		
		
		for(int pixel = 0; pixel < pixels.length; pixel++)
			out.putInt(pixels[pixel]);

		out.flip();
		return out;
	}
	
	/**
	 * Create a new sprite from raw data packed into the given ByteBuffer
	 * @param in the byte buffer to read from
	 */
	public static Sprite unpack(ByteBuffer in) throws IOException 
	{
		if(in.remaining() < 25)
			throw new IOException("Provided buffer too short - Headers missing");

		int width = in.getInt();
		int height = in.getInt();
		
		boolean requiresShift = in.get() == 1;
		int xShift = in.getInt();
		int yShift = in.getInt();
		
		int width2 = in.getInt();
		int height2 = in.getInt();
		
		int[] pixels = new int[width * height];
		
		if(in.remaining() < (pixels.length * 4))
			throw new IOException("Provided buffer too short - Pixels missing");

		for(int pixel = 0; pixel < pixels.length; pixel++)
			pixels[pixel] = in.getInt();

		Sprite sprite = new Sprite(pixels, width, height);
		sprite.setRequiresShift(requiresShift);
		sprite.setShift(xShift, yShift);
		sprite.setSomething(width2, height2);
		
		return sprite;
	}
}