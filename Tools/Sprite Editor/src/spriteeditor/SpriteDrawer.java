package spriteeditor;

import java.awt.Component;

/**
 * Draws the sprites in the editor.
 */
public class SpriteDrawer extends SpriteHandler 
{
	/**
	 * Constructs a sprite drawer with the given information
	 * @param component the parent component to render to
	 * @param width the width of the drawing region
	 * @param height the height of the drawing region
	 */
	public SpriteDrawer(Component component, int width, int height) 
	{
		super(component, width, height);
	}
	
	/**
	 * Draws the given sprite at the given coordinates with the given colour overlay
	 * @param x the x coord to render at
	 * @param y the y coord to render at
	 * @param sprite the sprite object to render
	 * @param overlay the colour overlay to cast over the sprite
	 */
	public void drawSprite(int x, int y, Sprite sprite, int overlay) 
	{
		if(sprite.requiresShift()) 
		{
			x += sprite.getXShift();
			y += sprite.getYShift();
		}
		
		int spriteWidth = sprite.getWidth();
		int spriteHeight = sprite.getHeight();
		int l = x + y * super.WIDTH;
		int i1 = 0;
		int l1 = super.WIDTH - spriteWidth;
		int i2 = 0;
		
		if(y < 0) 
		{
			int j2 = 0 - y;
			spriteHeight -= j2;
			y = 0;
			i1 += j2 * spriteWidth;
			l += j2 * super.WIDTH;
		}
		
		if(y + spriteHeight >= super.HEIGHT) 
			spriteHeight -= ((y + spriteHeight) - super.HEIGHT) + 1;

		if(x < 0) 
		{
			int k2 = 0 - x;
			spriteWidth -= k2;
			x = 0;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		
		if(x + spriteWidth >= super.WIDTH) 
		{
			int l2 = ((x + spriteWidth) - super.WIDTH) + 1;
			spriteWidth -= l2;
			i2 += l2;
			l1 += l2;
		}
		
		if(spriteWidth <= 0 || spriteHeight <= 0) 
		{
			return;
		}
		
		mergePixels(imagePixelArray, sprite.getPixels(), i1, l, spriteWidth, spriteHeight, l1, i2, 1, overlay);
	}
	
	/**
	 * Merges the given pixels
	 */
	private void mergePixels(int existingPixels[], int newPixels[], int j, int k, int spriteWidth, int spriteHeight, int j1, int k1, int scanSize, int overlay) 
	{
		int[] overData = overlay != 0 ? new int[]{overlay & 0xff, overlay >> 8 & 0xff, overlay >> 16 & 0xff} : null;
		
		for(int y = -spriteHeight;y < 0; y += scanSize) 
		{
			for(int x = -spriteWidth;x < 0;x++) 
			{
				int buffer = newPixels[j++];
				if(buffer != 0) 
				{
					int[] underData = {buffer & 0xff, buffer >> 8 & 0xff, buffer >> 16 & 0xff};
					if(overData != null && underData[2] == underData[1] && underData[1] == underData[0])
						existingPixels[k] = ((underData[2] * overData[2] >> 8) << 16) + ((underData[1] * overData[1] >> 8) << 8) + (underData[0] * overData[0] >> 8);
					else
						existingPixels[k] = buffer;
				}
				
				k++;
			}
			
			k += j1;
			j += k1;
		}
	}
}