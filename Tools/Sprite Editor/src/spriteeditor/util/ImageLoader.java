package spriteeditor.util;

import java.awt.*;
import java.awt.image.*;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Handles image operations
 */
public class ImageLoader 
{
	/**
	 * @param file the image file to load from
	 * @return the loaded buffered image from the given file
	 */
	public static BufferedImage loadImage(File file) throws IOException 
	{
		Image image = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
		return toBufferedImage(image);
	}
	
	/**
	 * Saves the given image with the given format into the given file
	 * @param image the image to save
	 * @param format the format to save it in
	 * @param file the file to save it to
	 */
	public static void saveImage(BufferedImage image, String format, File file) throws IOException 
	{
		ImageIO.write(image, format, file);
	}

	/**
	 * @param image the awt.Image to convert
	 * @return the given awt.Image object converted into a BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image image) 
	{
		image = new ImageIcon(image).getImage();
		BufferedImage buffImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		
		Graphics g = buffImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		
		return buffImage;
	}
}