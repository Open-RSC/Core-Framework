package spriteeditor;

import java.awt.image.*;
import java.awt.*;

/**
 * Handles rendering of sprites
 */
public class SpriteHandler implements ImageProducer, ImageObserver 
{
	/**
	 * The width of this sprite handler
	 */
	public int WIDTH;
	/**
	 * The height of this sprite handler
	 */
	public int HEIGHT;
	/**
	 * The pixel array to render
	 */
	protected int[] imagePixelArray;
	/**
	 * The image to render
	 */
	private Image image;
	/**
	 * The image consumer
	 */
	private ImageConsumer imageConsumer;
	/**
	 * the colour model to work with
	 */
	private ColorModel colorModel;
	
	/**
	 * Constructs a new sprite handler with the given info
	 * @param component the component to render to
	 * @param width the width of the render region
	 * @param height the height of the render region
	 */
	public SpriteHandler(Component component, int width, int height) 
	{
		image = component.createImage(this);
		this.WIDTH = width;
		this.HEIGHT = height;
		colorModel = new DirectColorModel(32, 0xff0000, 65280, 255);
		reset();
	}
	
	/**
	 * @return the pixel at the given array
	 */
	public int getPixel(int i) 
	{
		return imagePixelArray[i];
	}
	
	/**
	 * @return the pixel data array
	 */
	public int[] getPixels() 
	{
		return imagePixelArray;
	}
	
	/**
	 * Resets the pixel data
	 */
	public void reset() 
	{
		imagePixelArray = new int[WIDTH * HEIGHT];
		
		for(int c = 0;c < imagePixelArray.length;c++)
			imagePixelArray[c] = 0;
	}
	
	/**
	 * Completes the pixels using the image consumer
	 */
	private synchronized void completePixels() 
	{
		if(imageConsumer == null)
			return;
		
		imageConsumer.setPixels(0, 0, WIDTH, HEIGHT, colorModel, imagePixelArray, 0, WIDTH);
		imageConsumer.imageComplete(2);
	}
	
	/**
	 * Draws the image to the given graphics object
	 * @param g the Graphics object to draw to
	 * @param x the x coordinate to draw at
	 * @param y the y coordinate to draw at
	 */
	public void drawImage(Graphics g, int x, int y) 
	{
		completePixels();
		g.drawImage(image, x, y, this);
	}
	
	/**
	 * Adds the given image consumer to the sprite handler
	 * @param imageconsumer the image consumer 
	 */
	public synchronized void addConsumer(ImageConsumer imageconsumer) 
	{
		imageConsumer = imageconsumer;
		imageconsumer.setDimensions(WIDTH, HEIGHT);
		imageconsumer.setProperties(null);
		imageconsumer.setColorModel(colorModel);
		imageconsumer.setHints(14);
	}
	
	/**
	 * @param imageconsumer the image consumer to compare
	 * @return if the given comsumer is our consumer
	 */
	public synchronized boolean isConsumer(ImageConsumer imageconsumer) 
	{
		return imageConsumer == imageconsumer;
	}
	
	/**
	 * Removes the given image consumer from the sprite handler if it's the right consumer
	 * @param imageconsumer the image consumer to remove
	 */
	public synchronized void removeConsumer(ImageConsumer imageconsumer) 
	{
		if(imageConsumer == imageconsumer) // If it's not ours, don't remove it
			imageConsumer = null;
	}
	
	/**
	 * Adds the image consumer to our sprite handler
	 * @param imageconsumer the image consumer to render with
	 */
	public void startProduction(ImageConsumer imageconsumer) 
	{
		addConsumer(imageconsumer);
	}
	
	/**
	 * Requests top down left right resend. Whatever the fuck that means.
	 * @param imageconsumer the image consumer to do nothing with?
	 */
	public void requestTopDownLeftRightResend(ImageConsumer imageconsumer) 
	{
		System.out.println("TDLR - wtf?");
	}
	
	/**
	 * @return always true?
	 */
	public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1) 
	{
		return true;
	}
}