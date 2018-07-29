package org.openrsc.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.openrsc.client.model.Sprite;

public class Raster implements ImageProducer, ImageObserver {
	
    public Sprite[] sprites;
	Map<Integer, Sprite> temp = new TreeMap<Integer, Sprite>();
	
    public void resize(int clipWidth, int clipHeight, Component component)
    {
        imageX = 0;
        imageY = 0;
    	this.subClipWidth = this.clipWidth = clipWidth;
    	this.subClipHeight = this.clipHeight = clipHeight;
    	this.imagePixelArray = new int[clipWidth * clipHeight];
    	this.image = component.createImage(this);
    	this.imageConsumer.setDimensions(clipWidth, clipHeight);
    	completePixels();
    	component.prepareImage(image, component);
    	completePixels();
    	component.prepareImage(image, component);
    	completePixels();
    	component.prepareImage(image, component);
    }
	
    public Raster(int width, int height, int spriteCount, Component component) {
        f1Toggle = false;
        drawStringShadows = false;
        subClipHeight = height;
        subClipWidth = width;
        clipWidth = width;
        clipHeight = height;
        imagePixelArray = new int[width * height];
        for (int i1 = 0; i1 < imagePixelArray.length; i1++) {
            imagePixelArray[i1] = 0;
        }
        sprites = new Sprite[spriteCount];
        if (width > 1 && height > 1 && component != null) {
            colourModel = new DirectColorModel(32, 0xff0000, 65280, 255);
            image = component.createImage(this);

            completePixels();
            component.prepareImage(image, component);
            completePixels();
            component.prepareImage(image, component);
            completePixels();
            component.prepareImage(image, component);
        }
        
       	ZipInputStream is = new ZipInputStream(Resources.load("/sprites"));
        ZipEntry e = null;
        try {
			while((e = is.getNextEntry()) != null)
			{
				temp.put(Integer.parseInt(e.getName()), new Sprite(is));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
    public void finishedLoadingSprites()
    {
    	temp = null;
    }

    public boolean loadSprite(int id) {
    	if(temp.containsKey(id))
    	{
    		sprites[id] = temp.get(id);
    		return true;
    	}
    	return false;
    }

    public synchronized void addConsumer(ImageConsumer imageconsumer) {
        imageConsumer = imageconsumer;
        imageconsumer.setDimensions(clipWidth, clipHeight);
        imageconsumer.setProperties(null);
        imageconsumer.setColorModel(colourModel);
        imageconsumer.setHints(14);
    }

    public synchronized boolean isConsumer(ImageConsumer imageconsumer) {
        return imageConsumer == imageconsumer;
    }

    public synchronized void removeConsumer(ImageConsumer imageconsumer) {
        if (imageConsumer == imageconsumer) {
            imageConsumer = null;
        }
    }

    public void startProduction(ImageConsumer imageconsumer) {
        addConsumer(imageconsumer);
    }

    public void requestTopDownLeftRightResend(ImageConsumer imageconsumer) { }

    public synchronized void completePixels() {
        if (imageConsumer != null) {
			imageConsumer.setPixels(0, 0, clipWidth, clipHeight, colourModel, imagePixelArray, 0, clipWidth);
			imageConsumer.imageComplete(2);
		}
    }

    public void setDimensions(int x, int y, int width, int height) {
        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (width > clipWidth)
            width = clipWidth;
        if (height > clipHeight)
            height = clipHeight;
        imageX = x;
        imageY = y;
        subClipWidth = width;
        subClipHeight = height;
    }

    public void resetDimensions() {
        imageX = 0;
        imageY = 0;
        subClipWidth = clipWidth;
        subClipHeight = clipHeight;
    }
	
	public void drawBufferedImage(Graphics g, int x, int y, BufferedImage img) {
		g.drawImage(img, x, y, this);
	}
    
	public void drawImage(Graphics g, int x, int y, Image img) {
        g.drawImage(img, x, y, this);
    }

    public void drawImage(Graphics g, int x, int y) {
        completePixels();
        g.drawImage(image, x, y, this);
    }
	
	public void drawCenteredAlphaBox(int x, int y, int width, int height, int color, int opacity) {
		drawBoxAlpha(x - (width / 2), y - (height / 2), width, height, color, opacity);
	}

	public void drawCenteredBoxEdge(int x, int y, int width, int height, int colour) {
		drawLineX(x - width / 2, y - height / 2, width, colour);
		drawLineX(x - width / 2, ((y - height / 2) + height) - 1, width, colour);
		drawLineY(x - width / 2, y - height / 2, height, colour);
		drawLineY(((x - width / 2) + width) - 1, y - height / 2, height, colour);
	}
	
	public void drawCenteredBox(int x, int y, int width, int height, int color) {
		int halfWidth = width / 2, halfHeight = height / 2;
		drawBox(x - halfWidth, y - halfHeight, width, height, color);
	}

    public void method211() {
        int i = clipWidth * clipHeight;
        if (!f1Toggle) {
            for (int j = 0; j < i; j++)
                imagePixelArray[j] = 0;

            return;
        }
        int k = 0;
        for (int l = -clipHeight; l < 0; l += 2) {
            for (int i1 = -clipWidth; i1 < 0; i1++)
                imagePixelArray[k++] = 0;

            k += clipWidth;
        }

    }

	public void rounded_rectangle(int x, int y, int width, int height,
			int lineColor, int cornerRadius) {
		/*
		 * corners: p1 - p2 | | p4 - p3
		 */
		int p1 = x;
		int p2 = x + width;
		int p3 = p2 + height;
		int p4 = x + height;


		// draw straight lines

		this.drawLineX(x + cornerRadius, y + cornerRadius, width, lineColor);
		this.drawLineX(x + cornerRadius, y + height - cornerRadius, width, lineColor);
		this.drawLineY(x + cornerRadius, y + cornerRadius,  height, lineColor);
		this.drawLineY(x + width + cornerRadius, y + cornerRadius, height, lineColor);
		draw_ellipse(x + (width / 2), y + (height / 2), width / 2, height / 2, lineColor);
		/*
		 * line(src, Point (p1.x+cornerRadius,p1.y), Point
		 * (p2.x-cornerRadius,p2.y), lineColor, thickness, lineType); line(src,
		 * Point (p2.x,p2.y+cornerRadius), Point (p3.x,p3.y-cornerRadius),
		 * lineColor, thickness, lineType); line(src, Point
		 * (p4.x+cornerRadius,p4.y), Point (p3.x-cornerRadius,p3.y), lineColor,
		 * thickness, lineType); line(src, Point (p1.x,p1.y+cornerRadius), Point
		 * (p4.x,p4.y-cornerRadius), lineColor, thickness, lineType);
		 * 
		 * // draw arcs ellipse( src, p1+Point(cornerRadius, cornerRadius),
		 * Size( cornerRadius, cornerRadius ), 180.0, 0, 90, lineColor,
		 * thickness, lineType ); ellipse( src, p2+Point(-cornerRadius,
		 * cornerRadius), Size( cornerRadius, cornerRadius ), 270.0, 0, 90,
		 * lineColor, thickness, lineType ); ellipse( src,
		 * p3+Point(-cornerRadius, -cornerRadius), Size( cornerRadius,
		 * cornerRadius ), 0.0, 0, 90, lineColor, thickness, lineType );
		 * ellipse( src, p4+Point(cornerRadius, -cornerRadius), Size(
		 * cornerRadius, cornerRadius ), 90.0, 0, 90, lineColor, thickness,
		 * lineType );
		 */
	}
	
	public void draw_ellipse(int x, int y, int a, int b, int color) {
		int wx, wy;
		int thresh;
		int asq = a * a;
		int bsq = b * b;
		int xa, ya;

		setPixelColour(x, y + b, color);
		setPixelColour(x, y - b, color);

		wx = 0;
		wy = b;
		xa = 0;
		ya = asq * 2 * b;
		thresh = asq / 4 - asq * b;

		for (;;) {
			thresh += xa + bsq;

			if (thresh >= 0) {
				ya -= asq * 2;
				thresh -= ya;
				wy--;
			}

			xa += bsq * 2;
			wx++;

			if (xa >= ya)
				break;

			setPixelColour(x + wx, y - wy, color);
			setPixelColour(x - wx, y - wy, color);
			setPixelColour(x + wx, y + wy, color);
			setPixelColour(x - wx, y + wy, color);
		}

		setPixelColour(x + a, y, color);
		setPixelColour(x - a, y, color);

		wx = a;
		wy = 0;
		xa = bsq * 2 * a;

		ya = 0;
		thresh = bsq / 4 - bsq * a;

		for (;;) {
			thresh += ya + asq;

			if (thresh >= 0) {
				xa -= bsq * 2;
				thresh = thresh - xa;
				wx--;
			}

			ya += asq * 2;
			wy++;

			if (ya > xa)
				break;

			setPixelColour(x + wx, y - wy, color);
			setPixelColour(x - wx, y - wy, color);
			setPixelColour(x + wx, y + wy, color);
			setPixelColour(x - wx, y + wy, color);
		}
	}


	public void drawPixel(int x, int y, int color) {
		imagePixelArray[x + y * clipWidth] = color;
	}
	
    public void method212(int i, int j, int k, int l, int i1) {
        int j1 = 256 - i1;
        int k1 = (l >> 16 & 0xff) * i1;
        int l1 = (l >> 8 & 0xff) * i1;
        int i2 = (l & 0xff) * i1;
        int i3 = j - k;
        if (i3 < 0)
            i3 = 0;
        int j3 = j + k;
        if (j3 >= clipHeight)
            j3 = clipHeight - 1;
        byte byte0 = 1;
        if (f1Toggle) {
            byte0 = 2;
            if ((i3 & 1) != 0)
                i3++;
        }
        for (int k3 = i3; k3 <= j3; k3 += byte0) {
            int l3 = k3 - j;
            int i4 = (int) Math.sqrt(k * k - l3 * l3);
            int j4 = i - i4;
            if (j4 < 0)
                j4 = 0;
            int k4 = i + i4;
            if (k4 >= clipWidth)
                k4 = clipWidth - 1;
            int l4 = j4 + k3 * clipWidth;
            for (int i5 = j4; i5 <= k4; i5++) {
                int j2 = (imagePixelArray[l4] >> 16 & 0xff) * j1;
                int k2 = (imagePixelArray[l4] >> 8 & 0xff) * j1;
                int l2 = (imagePixelArray[l4] & 0xff) * j1;
                int j5 = ((k1 + j2 >> 8) << 16) + ((l1 + k2 >> 8) << 8) + (i2 + l2 >> 8);
                imagePixelArray[l4++] = j5;
            }

        }

    }

    public void drawBoxAlpha(Rectangle r, int i1, int j1)
    {
    	drawBoxAlpha(r.x, r.y, r.width, r.height, i1, j1);
    }
    
    public void drawBoxAlpha(int i, int j, int k, int l, int i1, int j1) {
        if (i < imageX) {
            k -= imageX - i;
            i = imageX;
        }
        if (j < imageY) {
            l -= imageY - j;
            j = imageY;
        }
        if (i + k > subClipWidth)
            k = subClipWidth - i;
        if (j + l > subClipHeight)
            l = subClipHeight - j;
        int k1 = 256 - j1;
        int l1 = (i1 >> 16 & 0xff) * j1;
        int i2 = (i1 >> 8 & 0xff) * j1;
        int j2 = (i1 & 0xff) * j1;
        int j3 = clipWidth - k;
        byte byte0 = 1;
        if (f1Toggle) {
            byte0 = 2;
            j3 += clipWidth;
            if ((j & 1) != 0) {
                j++;
                l--;
            }
        }
        int k3 = i + j * clipWidth;
        for (int l3 = 0; l3 < l; l3 += byte0) {
            for (int i4 = -k; i4 < 0; i4++) {
                int k2 = (imagePixelArray[k3] >> 16 & 0xff) * k1;
                int l2 = (imagePixelArray[k3] >> 8 & 0xff) * k1;
                int i3 = (imagePixelArray[k3] & 0xff) * k1;
                int j4 = ((l1 + k2 >> 8) << 16) + ((i2 + l2 >> 8) << 8) + (j2 + i3 >> 8);
                imagePixelArray[k3++] = j4;
            }

            k3 += j3;
        }

    }

    public void drawGradient(int i, int j, int k, int l, int i1, int j1) {
        if (i < imageX) {
            k -= imageX - i;
            i = imageX;
        }
        if (i + k > subClipWidth)
            k = subClipWidth - i;
        int k1 = j1 >> 16 & 0xff;
        int l1 = j1 >> 8 & 0xff;
        int i2 = j1 & 0xff;
        int j2 = i1 >> 16 & 0xff;
        int k2 = i1 >> 8 & 0xff;
        int l2 = i1 & 0xff;
        int i3 = clipWidth - k;
        byte byte0 = 1;
        if (f1Toggle) {
            byte0 = 2;
            i3 += clipWidth;
            if ((j & 1) != 0) {
                j++;
                l--;
            }
        }
        int j3 = i + j * clipWidth;
        for (int k3 = 0; k3 < l; k3 += byte0)
            if (k3 + j >= imageY && k3 + j < subClipHeight) {
                int l3 = ((k1 * k3 + j2 * (l - k3)) / l << 16) + ((l1 * k3 + k2 * (l - k3)) / l << 8) + (i2 * k3 + l2 * (l - k3)) / l;
                for (int i4 = -k; i4 < 0; i4++)
                    imagePixelArray[j3++] = l3;

                j3 += i3;
            } else {
                j3 += clipWidth;
            }

    }

    public void drawBox(int i, int j, int k, int l, int i1) {
        if (i < imageX) {
            k -= imageX - i;
            i = imageX;
        }
        if (j < imageY) {
            l -= imageY - j;
            j = imageY;
        }
        if (i + k > subClipWidth)
            k = subClipWidth - i;
        if (j + l > subClipHeight)
            l = subClipHeight - j;
        int j1 = clipWidth - k;
        byte byte0 = 1;
        if (f1Toggle) {
            byte0 = 2;
            j1 += clipWidth;
            if ((j & 1) != 0) {
                j++;
                l--;
            }
        }
        int k1 = i + j * clipWidth;
        for (int l1 = -l; l1 < 0; l1 += byte0) {
            for (int i2 = -k; i2 < 0; i2++)
                imagePixelArray[k1++] = i1;

            k1 += j1;
        }

    }

    public void drawBoxEdge(int x1, int y1, int x2, int y2, int colour) {
        drawLineX(x1, y1, x2, colour);
        drawLineX(x1, (y1 + y2) - 1, x2, colour);
        drawLineY(x1, y1, y2, colour);
        drawLineY((x1 + x2) - 1, y1, y2, colour);
    }

    public void drawLineX(int x1, int y1, int x2, int colour) {
        if (y1 < imageY || y1 >= subClipHeight)
            return;
        if (x1 < imageX) {
            x2 -= imageX - x1;
            x1 = imageX;
        }
        if (x1 + x2 > subClipWidth)
            x2 = subClipWidth - x1;
        int xPixel = x1 + y1 * clipWidth;
        for (int yPixel = 0; yPixel < x2; yPixel++)
            imagePixelArray[xPixel + yPixel] = colour;

    }

    public void drawLineY(int x1, int y1, int y2, int colour) {
        if (x1 < imageX || x1 >= subClipWidth)
            return;
        if (y1 < imageY) {
            y2 -= imageY - y1;
            y1 = imageY;
        }
        if (y1 + y2 > subClipWidth)
            y2 = subClipHeight - y1;
        int xPixel = x1 + y1 * clipWidth;
        for (int yPixel = 0; yPixel < y2; yPixel++)
            imagePixelArray[xPixel + yPixel * clipWidth] = colour;

    }

    public void setPixelColour(int x, int y, int colour) {
        if (x < imageX || y < imageY || x >= subClipWidth || y >= subClipHeight) {
            return;
        }
        imagePixelArray[x + y * clipWidth] = colour;
    }

    public void fadePixels() {
        int k = clipWidth * clipHeight;
        for (int j = 0; j < k; j++) {
            int i = imagePixelArray[j] & 0xffffff;
            imagePixelArray[j] = (i >>> 1 & 0x7f7f7f) + (i >>> 2 & 0x3f3f3f) + (i >>> 3 & 0x1f1f1f) + (i >>> 4 & 0xf0f0f);
        }
    }
    
	public void method866(int x, int y, int picture) {
        try {
            if (sprites[picture].requiresShift()) {
                x += sprites[picture].getXShift();
                y += sprites[picture].getYShift();
            }
            int l = x + y * clipWidth;
            int i1 = 0;
            int j1 = sprites[picture].getHeight();
            int k1 = sprites[picture].getWidth();
            int l1 = clipWidth - k1;
            int i2 = 0;
            if (y < imageY) {
                int j2 = imageY - y;
                j1 -= j2;
                y = imageY;
                i1 += j2 * k1;
                l += j2 * clipWidth;
            }
            if (y + j1 >= subClipHeight)
                j1 -= ((y + j1) - subClipHeight) + 1;
            if (x < imageX) {
                int k2 = imageX - x;
                k1 -= k2;
                x = imageX;
                i1 += k2;
                l += k2;
                i2 += k2;
                l1 += k2;
            }
            if (x + k1 >= subClipWidth) {
                int l2 = ((x + k1) - subClipWidth) + 1;
                k1 -= l2;
                i2 += l2;
                l1 += l2;
            }
            if (k1 <= 0 || j1 <= 0)
                return;
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                l1 += clipWidth;
                i2 += sprites[picture].getWidth();
                if ((y & 1) != 0) {
                    l += clipWidth;
                    j1--;
                }
            }
            method235(imagePixelArray, sprites[picture].getPixels(), 0, i1, l, k1, j1, l1, i2, byte0);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void method221(int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = k; k1 < k + i1; k1++) {
            for (int l1 = l; l1 < l + j1; l1++) {
                int i2 = 0;
                int j2 = 0;
                int k2 = 0;
                int l2 = 0;
                for (int i3 = k1 - i; i3 <= k1 + i; i3++)
                    if (i3 >= 0 && i3 < clipWidth) {
                        for (int j3 = l1 - j; j3 <= l1 + j; j3++)
                            if (j3 >= 0 && j3 < clipHeight) {
                                int k3 = imagePixelArray[i3 + clipWidth * j3];
                                i2 += k3 >> 16 & 0xff;
                                j2 += k3 >> 8 & 0xff;
                                k2 += k3 & 0xff;
                                l2++;
                            }

                    }

                imagePixelArray[k1 + clipWidth * l1] = (i2 / l2 << 16) + (j2 / l2 << 8) + k2 / l2;
            }

        }

    }

    public static int convertRGBToLong(int red, int green, int blue) {
        return (red << 16) + (green << 8) + blue;
    }

    public void cleanupSprites() {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = null;
        }
    }

    public void storeSpriteHoriz(int index, int startX, int startY, int width, int height) {
        int[] pixels = new int[width * height];
        int pixel = 0;
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                pixels[pixel++] = imagePixelArray[x + y * clipWidth];
            }
        }

        Sprite sprite = new Sprite(pixels, width, height);
        sprite.setShift(0, 0);
        sprite.setRequiresShift(false);
        sprite.setSomething(width, height);

        sprites[index] = sprite;
    }
    
    public void removeAllPixels() {
        int k = clipWidth * clipHeight;
        for (int j = 0; j < k; j++) {
            imagePixelArray[j] = 0;
        }
    }

    public void storeSpriteVert(int index, int startX, int startY, int width, int height) {
        int[] pixels = new int[width * height];
        int pixel = 0;
        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                pixels[pixel++] = imagePixelArray[x + y * clipWidth];
            }
        }

        Sprite sprite = new Sprite(pixels, width, height);
        sprite.setShift(0, 0);
        sprite.setRequiresShift(false);
        sprite.setSomething(width, height);

        sprites[index] = sprite;
    }
	
	public void drawSleepWord(int x, int y, Sprite sleepy) {
		try {
			if (sleepy.requiresShift()) {
				x += sleepy.getXShift();
				y += sleepy.getYShift();
			}
			int l = x + y * clipWidth;
			int i1 = 0;
			int j1 = sleepy.getHeight();
			int k1 = sleepy.getWidth();
			int l1 = clipWidth - k1;
			int i2 = 0;
			if (y < imageY) {
				int j2 = imageY - y;
				j1 -= j2;
				y = imageY;
				i1 += j2 * k1;
				l += j2 * clipWidth;
			}
			if (y + j1 >= subClipHeight)
				j1 -= ((y + j1) - subClipHeight) + 1;
			if (x < imageX) {
				int k2 = imageX - x;
				k1 -= k2;
				x = imageX;
				i1 += k2;
				l += k2;
				i2 += k2;
				l1 += k2;
			}
			if (x + k1 >= subClipWidth) {
				int l2 = ((x + k1) - subClipWidth) + 1;
				k1 -= l2;
				i2 += l2;
				l1 += l2;
			}
            if (k1 <= 0 || j1 <= 0)
                return;
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                l1 += clipWidth;
                i2 += sleepy.getWidth();
                if ((y & 1) != 0) {
                    l += clipWidth;
                    j1--;
                }
            }
            method235(imagePixelArray, sleepy.getPixels(), 0, i1, l, k1, j1, l1, i2, byte0);
        } catch (Exception e) { }
	}
   
    public void drawPicture(int x, int y, int picture) {
        try {
            if (sprites[picture].requiresShift()) {
                x += sprites[picture].getXShift();
                y += sprites[picture].getYShift();
            }
            int l = x + y * clipWidth;
            int i1 = 0;
            int j1 = sprites[picture].getHeight();
            int k1 = sprites[picture].getWidth();
            int l1 = clipWidth - k1;
            int i2 = 0;
            if (y < imageY) {
                int j2 = imageY - y;
                j1 -= j2;
                y = imageY;
                i1 += j2 * k1;
                l += j2 * clipWidth;
            }
            if (y + j1 >= subClipHeight)
                j1 -= ((y + j1) - subClipHeight) + 1;
            if (x < imageX) {
                int k2 = imageX - x;
                k1 -= k2;
                x = imageX;
                i1 += k2;
                l += k2;
                i2 += k2;
                l1 += k2;
            }
            if (x + k1 >= subClipWidth) {
                int l2 = ((x + k1) - subClipWidth) + 1;
                k1 -= l2;
                i2 += l2;
                l1 += l2;
            }
            if (k1 <= 0 || j1 <= 0)
                return;
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                l1 += clipWidth;
                i2 += sprites[picture].getWidth();
                if ((y & 1) != 0) {
                    l += clipWidth;
                    j1--;
                }
            }
            method235(imagePixelArray, sprites[picture].getPixels(), 0, i1, l, k1, j1, l1, i2, byte0);
        }
        catch (Exception e) { }
    }

    public void spriteClip1(int i, int j, int k, int l, int i1) {
        try {
            int j1 = sprites[i1].getWidth();
            int k1 = sprites[i1].getHeight();
            int l1 = 0;
            int i2 = 0;
            int j2 = (j1 << 16) / k;
            int k2 = (k1 << 16) / l;
            if (sprites[i1].requiresShift()) {
                int l2 = sprites[i1].getSomething1();
                int j3 = sprites[i1].getSomething2();
                j2 = (l2 << 16) / k;
                k2 = (j3 << 16) / l;
                i += ((sprites[i1].getXShift() * k + l2) - 1) / l2;
                j += ((sprites[i1].getYShift() * l + j3) - 1) / j3;
                if ((sprites[i1].getXShift() * k) % l2 != 0)
                    l1 = (l2 - (sprites[i1].getXShift() * k) % l2 << 16) / k;
                if ((sprites[i1].getYShift() * l) % j3 != 0)
                    i2 = (j3 - (sprites[i1].getYShift() * l) % j3 << 16) / l;
                k = (k * (sprites[i1].getWidth() - (l1 >> 16))) / l2;
                l = (l * (sprites[i1].getHeight() - (i2 >> 16))) / j3;
            }
            int i3 = i + j * clipWidth;
            int k3 = clipWidth - k;
            if (j < imageY) {
                int l3 = imageY - j;
                l -= l3;
                j = 0;
                i3 += l3 * clipWidth;
                i2 += k2 * l3;
            }
            if (j + l >= subClipHeight)
                l -= ((j + l) - subClipHeight) + 1;
            if (i < imageX) {
                int i4 = imageX - i;
                k -= i4;
                i = 0;
                i3 += i4;
                l1 += j2 * i4;
                k3 += i4;
            }
            if (i + k >= subClipWidth) {
                int j4 = ((i + k) - subClipWidth) + 1;
                k -= j4;
                k3 += j4;
            }
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                k3 += clipWidth;
                k2 += k2;
                if ((j & 1) != 0) {
                    i3 += clipWidth;
                    l--;
                }
            }
            plotSale1(imagePixelArray, sprites[i1].getPixels(), 0, l1, i2, i3, k3, k, l, j2, k2, j1, byte0);
            return;
        }
        catch (Exception _ex) { }
    }

    public void method232(int i, int j, int k, int l) {
        if (sprites[k].requiresShift()) {
            i += sprites[k].getXShift();
            j += sprites[k].getYShift();
        }
        int i1 = i + j * clipWidth;
        int j1 = 0;
        int k1 = sprites[k].getHeight();
        int l1 = sprites[k].getWidth();
        int i2 = clipWidth - l1;
        int j2 = 0;
        if (j < imageY) {
            int k2 = imageY - j;
            k1 -= k2;
            j = imageY;
            j1 += k2 * l1;
            i1 += k2 * clipWidth;
        }
        if (j + k1 >= subClipHeight)
            k1 -= ((j + k1) - subClipHeight) + 1;
        if (i < imageX) {
            int l2 = imageX - i;
            l1 -= l2;
            i = imageX;
            j1 += l2;
            i1 += l2;
            j2 += l2;
            i2 += l2;
        }
        if (i + l1 >= subClipWidth) {
            int i3 = ((i + l1) - subClipWidth) + 1;
            l1 -= i3;
            j2 += i3;
            i2 += i3;
        }
        if (l1 <= 0 || k1 <= 0)
            return;
        byte byte0 = 1;
        if (f1Toggle) {
            byte0 = 2;
            i2 += clipWidth;
            j2 += sprites[k].getWidth();
            if ((j & 1) != 0) {
                i1 += clipWidth;
                k1--;
            }
        }
        method238(imagePixelArray, sprites[k].getPixels(), 0, j1, i1, l1, k1, i2, j2, byte0, l);
    }

    public void spriteClip2(int i, int j, int k, int l, int i1, int j1) {
        try {
            int k1 = sprites[i1].getWidth();
            int l1 = sprites[i1].getHeight();
            int i2 = 0;
            int j2 = 0;
            int k2 = (k1 << 16) / k;
            int l2 = (l1 << 16) / l;
            if (sprites[i1].requiresShift()) {
                int i3 = sprites[i1].getSomething1();
                int k3 = sprites[i1].getSomething2();
                k2 = (i3 << 16) / k;
                l2 = (k3 << 16) / l;
                i += ((sprites[i1].getXShift() * k + i3) - 1) / i3;
                j += ((sprites[i1].getYShift() * l + k3) - 1) / k3;
                if ((sprites[i1].getXShift() * k) % i3 != 0)
                    i2 = (i3 - (sprites[i1].getXShift() * k) % i3 << 16) / k;
                if ((sprites[i1].getYShift() * l) % k3 != 0)
                    j2 = (k3 - (sprites[i1].getYShift() * l) % k3 << 16) / l;
                k = (k * (sprites[i1].getWidth() - (i2 >> 16))) / i3;
                l = (l * (sprites[i1].getHeight() - (j2 >> 16))) / k3;
            }
            int j3 = i + j * clipWidth;
            int l3 = clipWidth - k;
            if (j < imageY) {
                int i4 = imageY - j;
                l -= i4;
                j = 0;
                j3 += i4 * clipWidth;
                j2 += l2 * i4;
            }
            if (j + l >= subClipHeight)
                l -= ((j + l) - subClipHeight) + 1;
            if (i < imageX) {
                int j4 = imageX - i;
                k -= j4;
                i = 0;
                j3 += j4;
                i2 += k2 * j4;
                l3 += j4;
            }
            if (i + k >= subClipWidth) {
                int k4 = ((i + k) - subClipWidth) + 1;
                k -= k4;
                l3 += k4;
            }
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                l3 += clipWidth;
                l2 += l2;
                if ((j & 1) != 0) {
                    j3 += clipWidth;
                    l--;
                }
            }
            tranScale(imagePixelArray, sprites[i1].getPixels(), 0, i2, j2, j3, l3, k, l, k2, l2, k1, byte0, j1);
            return;
        }
        catch (Exception _ex) { }
    }

    public void spriteClip3(int i, int j, int k, int l, int i1, int j1) {
        try {
            int k1 = sprites[i1].getWidth();
            int l1 = sprites[i1].getHeight();
            int i2 = 0;
            int j2 = 0;
            int k2 = (k1 << 16) / k;
            int l2 = (l1 << 16) / l;
            if (sprites[i1].requiresShift()) {
                int i3 = sprites[i1].getSomething1();
                int k3 = sprites[i1].getSomething2();
                k2 = (i3 << 16) / k;
                l2 = (k3 << 16) / l;
                i += ((sprites[i1].getXShift() * k + i3) - 1) / i3;
                j += ((sprites[i1].getYShift() * l + k3) - 1) / k3;
                if ((sprites[i1].getXShift() * k) % i3 != 0)
                    i2 = (i3 - (sprites[i1].getXShift() * k) % i3 << 16) / k;
                if ((sprites[i1].getYShift() * l) % k3 != 0)
                    j2 = (k3 - (sprites[i1].getYShift() * l) % k3 << 16) / l;
                k = (k * (sprites[i1].getWidth() - (i2 >> 16))) / i3;
                l = (l * (sprites[i1].getHeight() - (j2 >> 16))) / k3;
            }
            int j3 = i + j * clipWidth;
            int l3 = clipWidth - k;
            if (j < imageY) {
                int i4 = imageY - j;
                l -= i4;
                j = 0;
                j3 += i4 * clipWidth;
                j2 += l2 * i4;
            }
            if (j + l >= subClipHeight)
                l -= ((j + l) - subClipHeight) + 1;
            if (i < imageX) {
                int j4 = imageX - i;
                k -= j4;
                i = 0;
                j3 += j4;
                i2 += k2 * j4;
                l3 += j4;
            }
            if (i + k >= subClipWidth) {
                int k4 = ((i + k) - subClipWidth) + 1;
                k -= k4;
                l3 += k4;
            }
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                l3 += clipWidth;
                l2 += l2;
                if ((j & 1) != 0) {
                    j3 += clipWidth;
                    l--;
                }
            }
            plotScale2(imagePixelArray, sprites[i1].getPixels(), 0, i2, j2, j3, l3, k, l, k2, l2, k1, byte0, j1);
            return;
        }
        catch (Exception _ex) { }
    }

    private void method235(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
        int i2 = -(l >> 2);
        l = -(l & 3);
        for (int j2 = -i1; j2 < 0; j2 += l1) {
            for (int k2 = i2; k2 < 0; k2++) {
                i = ai1[j++];
                if (i != 0)
                    ai[k++] = i;
                else
                    k++;
                i = ai1[j++];
                if (i != 0)
                    ai[k++] = i;
                else
                    k++;
                i = ai1[j++];
                if (i != 0)
                    ai[k++] = i;
                else
                    k++;
                i = ai1[j++];
                if (i != 0)
                    ai[k++] = i;
                else
                    k++;
            }

            for (int l2 = l; l2 < 0; l2++) {
                i = ai1[j++];
                if (i != 0)
                    ai[k++] = i;
                else
                    k++;
            }

            k += j1;
            j += k1;
        }

    }

    private void plotSale1(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2, int k2) {
        try {
            int l2 = j;
            for (int i3 = -k1; i3 < 0; i3 += k2) {
                int j3 = (k >> 16) * j2;
                for (int k3 = -j1; k3 < 0; k3++) {
                    i = ai1[(j >> 16) + j3];
                    if (i != 0)
                        ai[l++] = i;
                    else
                        l++;
                    j += l1;
                }

                k += i2;
                j = l2;
                l += i1;
            }

            return;
        }
        catch (Exception _ex) { }
    }

    private void method238(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
        int j2 = 256 - i2;
        for (int k2 = -i1; k2 < 0; k2 += l1) {
            for (int l2 = -l; l2 < 0; l2++) {
                i = ai1[j++];
                if (i != 0) {
                    int i3 = ai[k];
                    ai[k++] = ((i & 0xff00ff) * i2 + (i3 & 0xff00ff) * j2 & 0xff00ff00) + ((i & 0xff00) * i2 + (i3 & 0xff00) * j2 & 0xff0000) >> 8;
                } else {
                    k++;
                }
            }

            k += j1;
            j += k1;
        }

    }

    private void tranScale(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2) {
        int i3 = 256 - l2;
        try {
            int j3 = j;
            for (int k3 = -k1; k3 < 0; k3 += k2) {
                int l3 = (k >> 16) * j2;
                for (int i4 = -j1; i4 < 0; i4++) {
                    i = ai1[(j >> 16) + l3];
                    if (i != 0) {
                        int j4 = ai[l];
                        ai[l++] = ((i & 0xff00ff) * l2 + (j4 & 0xff00ff) * i3 & 0xff00ff00) + ((i & 0xff00) * l2 + (j4 & 0xff00) * i3 & 0xff0000) >> 8;
                    } else {
                        l++;
                    }
                    j += l1;
                }

                k += i2;
                j = j3;
                l += i1;
            }

            return;
        }
        catch (Exception _ex) { }
    }

    private void plotScale2(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2) {
        int i3 = l2 >> 16 & 0xff;
        int j3 = l2 >> 8 & 0xff;
        int k3 = l2 & 0xff;
        try {
            int l3 = j;
            for (int i4 = -k1; i4 < 0; i4 += k2) {
                int j4 = (k >> 16) * j2;
                for (int k4 = -j1; k4 < 0; k4++) {
                    i = ai1[(j >> 16) + j4];
                    if (i != 0) {
                        int l4 = i >> 16 & 0xff;
                        int i5 = i >> 8 & 0xff;
                        int j5 = i & 0xff;
                        if (l4 == i5 && i5 == j5)
                            ai[l++] = ((l4 * i3 >> 8) << 16) + ((i5 * j3 >> 8) << 8) + (j5 * k3 >> 8);
                        else
                            ai[l++] = i;
                    } else {
                        l++;
                    }
                    j += l1;
                }

                k += i2;
                j = l3;
                l += i1;
            }

            return;
        }
        catch (Exception _ex) { }
    }

    public void method242(int i, int j, int k, int l, int i1) {
        int j1 = clipWidth;
        int k1 = clipHeight;
        if (anIntArray339 == null) {
            anIntArray339 = new int[512];
            for (int l1 = 0; l1 < 256; l1++) {
                anIntArray339[l1] = (int) (Math.sin((double) l1 * 0.02454369D) * 32768D);
                anIntArray339[l1 + 256] = (int) (Math.cos((double) l1 * 0.02454369D) * 32768D);
            }

        }
        int i2 = -sprites[k].getSomething1() / 2;
        int j2 = -sprites[k].getSomething2() / 2;
        if (sprites[k].requiresShift()) {
            i2 += sprites[k].getXShift();
            j2 += sprites[k].getYShift();
        }
        int k2 = i2 + sprites[k].getWidth();
        int l2 = j2 + sprites[k].getHeight();
        int i3 = k2;
        int j3 = j2;
        int k3 = i2;
        int l3 = l2;
        l &= 0xff;
        int i4 = anIntArray339[l] * i1;
        int j4 = anIntArray339[l + 256] * i1;
        int k4 = i + (j2 * i4 + i2 * j4 >> 22);
        int l4 = j + (j2 * j4 - i2 * i4 >> 22);
        int i5 = i + (j3 * i4 + i3 * j4 >> 22);
        int j5 = j + (j3 * j4 - i3 * i4 >> 22);
        int k5 = i + (l2 * i4 + k2 * j4 >> 22);
        int l5 = j + (l2 * j4 - k2 * i4 >> 22);
        int i6 = i + (l3 * i4 + k3 * j4 >> 22);
        int j6 = j + (l3 * j4 - k3 * i4 >> 22);
        if (i1 == 192 && (l & 0x3f) == (anInt348 & 0x3f))
            anInt346++;
        else if (i1 == 128)
            anInt348 = l;
        else
            anInt347++;
        int k6 = l4;
        int l6 = l4;
        if (j5 < k6)
            k6 = j5;
        else if (j5 > l6)
            l6 = j5;
        if (l5 < k6)
            k6 = l5;
        else if (l5 > l6)
            l6 = l5;
        if (j6 < k6)
            k6 = j6;
        else if (j6 > l6)
            l6 = j6;
        if (k6 < imageY)
            k6 = imageY;
        if (l6 > subClipHeight)
            l6 = subClipHeight;
        if (anIntArray340 == null || anIntArray340.length != k1 + 1) {
            anIntArray340 = new int[k1 + 1];
            anIntArray341 = new int[k1 + 1];
            anIntArray342 = new int[k1 + 1];
            anIntArray343 = new int[k1 + 1];
            anIntArray344 = new int[k1 + 1];
            anIntArray345 = new int[k1 + 1];
        }
        for (int i7 = k6; i7 <= l6; i7++) {
            anIntArray340[i7] = 0x5f5e0ff;
            anIntArray341[i7] = 0xfa0a1f01;
        }

        int i8 = 0;
        int k8 = 0;
        int i9 = 0;
        int j9 = sprites[k].getWidth();
        int k9 = sprites[k].getHeight();
        i2 = 0;
        j2 = 0;
        i3 = j9 - 1;
        j3 = 0;
        k2 = j9 - 1;
        l2 = k9 - 1;
        k3 = 0;
        l3 = k9 - 1;
        if (j6 != l4) {
            i8 = (i6 - k4 << 8) / (j6 - l4);
            i9 = (l3 - j2 << 8) / (j6 - l4);
        }
        int j7;
        int k7;
        int l7;
        int l8;
        if (l4 > j6) {
            l7 = i6 << 8;
            l8 = l3 << 8;
            j7 = j6;
            k7 = l4;
        } else {
            l7 = k4 << 8;
            l8 = j2 << 8;
            j7 = l4;
            k7 = j6;
        }
        if (j7 < 0) {
            l7 -= i8 * j7;
            l8 -= i9 * j7;
            j7 = 0;
        }
        if (k7 > k1 - 1)
            k7 = k1 - 1;
        for (int l9 = j7; l9 <= k7; l9++) {
            anIntArray340[l9] = anIntArray341[l9] = l7;
            l7 += i8;
            anIntArray342[l9] = anIntArray343[l9] = 0;
            anIntArray344[l9] = anIntArray345[l9] = l8;
            l8 += i9;
        }

        if (j5 != l4) {
            i8 = (i5 - k4 << 8) / (j5 - l4);
            k8 = (i3 - i2 << 8) / (j5 - l4);
        }
        int j8;
        if (l4 > j5) {
            l7 = i5 << 8;
            j8 = i3 << 8;
            j7 = j5;
            k7 = l4;
        } else {
            l7 = k4 << 8;
            j8 = i2 << 8;
            j7 = l4;
            k7 = j5;
        }
        if (j7 < 0) {
            l7 -= i8 * j7;
            j8 -= k8 * j7;
            j7 = 0;
        }
        if (k7 > k1 - 1)
            k7 = k1 - 1;
        for (int i10 = j7; i10 <= k7; i10++) {
            if (l7 < anIntArray340[i10]) {
                anIntArray340[i10] = l7;
                anIntArray342[i10] = j8;
                anIntArray344[i10] = 0;
            }
            if (l7 > anIntArray341[i10]) {
                anIntArray341[i10] = l7;
                anIntArray343[i10] = j8;
                anIntArray345[i10] = 0;
            }
            l7 += i8;
            j8 += k8;
        }

        if (l5 != j5) {
            i8 = (k5 - i5 << 8) / (l5 - j5);
            i9 = (l2 - j3 << 8) / (l5 - j5);
        }
        if (j5 > l5) {
            l7 = k5 << 8;
            j8 = k2 << 8;
            l8 = l2 << 8;
            j7 = l5;
            k7 = j5;
        } else {
            l7 = i5 << 8;
            j8 = i3 << 8;
            l8 = j3 << 8;
            j7 = j5;
            k7 = l5;
        }
        if (j7 < 0) {
            l7 -= i8 * j7;
            l8 -= i9 * j7;
            j7 = 0;
        }
        if (k7 > k1 - 1)
            k7 = k1 - 1;
        for (int j10 = j7; j10 <= k7; j10++) {
            if (l7 < anIntArray340[j10]) {
                anIntArray340[j10] = l7;
                anIntArray342[j10] = j8;
                anIntArray344[j10] = l8;
            }
            if (l7 > anIntArray341[j10]) {
                anIntArray341[j10] = l7;
                anIntArray343[j10] = j8;
                anIntArray345[j10] = l8;
            }
            l7 += i8;
            l8 += i9;
        }

        if (j6 != l5) {
            i8 = (i6 - k5 << 8) / (j6 - l5);
            k8 = (k3 - k2 << 8) / (j6 - l5);
        }
        if (l5 > j6) {
            l7 = i6 << 8;
            j8 = k3 << 8;
            l8 = l3 << 8;
            j7 = j6;
            k7 = l5;
        } else {
            l7 = k5 << 8;
            j8 = k2 << 8;
            l8 = l2 << 8;
            j7 = l5;
            k7 = j6;
        }
        if (j7 < 0) {
            l7 -= i8 * j7;
            j8 -= k8 * j7;
            j7 = 0;
        }
        if (k7 > k1 - 1)
            k7 = k1 - 1;
        for (int k10 = j7; k10 <= k7; k10++) {
            if (l7 < anIntArray340[k10]) {
                anIntArray340[k10] = l7;
                anIntArray342[k10] = j8;
                anIntArray344[k10] = l8;
            }
            if (l7 > anIntArray341[k10]) {
                anIntArray341[k10] = l7;
                anIntArray343[k10] = j8;
                anIntArray345[k10] = l8;
            }
            l7 += i8;
            j8 += k8;
        }

        int l10 = k6 * j1;
        int ai[] = sprites[k].getPixels();
        for (int i11 = k6; i11 < l6; i11++) {
            int j11 = anIntArray340[i11] >> 8;
            int k11 = anIntArray341[i11] >> 8;
            if (k11 - j11 <= 0) {
                l10 += j1;
            } else {
                int l11 = anIntArray342[i11] << 9;
                int i12 = ((anIntArray343[i11] << 9) - l11) / (k11 - j11);
                int j12 = anIntArray344[i11] << 9;
                int k12 = ((anIntArray345[i11] << 9) - j12) / (k11 - j11);
                if (j11 < imageX) {
                    l11 += (imageX - j11) * i12;
                    j12 += (imageX - j11) * k12;
                    j11 = imageX;
                }
                if (k11 > subClipWidth)
                    k11 = subClipWidth;
                if (!f1Toggle || (i11 & 1) == 0)
                    if (!sprites[k].requiresShift())
                        method243(imagePixelArray, ai, 0, l10 + j11, l11, j12, i12, k12, j11 - k11, j9);
                    else
                        method244(imagePixelArray, ai, 0, l10 + j11, l11, j12, i12, k12, j11 - k11, j9);
                l10 += j1;
            }
        }

    }

    private void method243(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
        for (i = k1; i < 0; i++) {
            imagePixelArray[j++] = ai1[(k >> 17) + (l >> 17) * l1];
            k += i1;
            l += j1;
        }

    }

    private void method244(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
        for (int i2 = k1; i2 < 0; i2++) {
            i = ai1[(k >> 17) + (l >> 17) * l1];
            if (i != 0)
                imagePixelArray[j++] = i;
            else
                j++;
            k += i1;
            l += j1;
        }

    }

    public void method245(int i, int j, int k, int l, int i1, int j1, int k1) {
        spriteClip1(i, j, k, l, i1);
    }
    
    public void spriteClip4(int i, int j, int k, int l, int i1, int overlay, int k1, int l1, boolean flag) {
        spriteClip4(i, j, k, l, i1, overlay, k1, l1, flag, 256);
    }

    public void spriteClip4(int i, int j, int k, int l, int i1, int overlay, int k1, int l1, boolean flag, int opacity) {
        try {
            if (overlay == 0)
                overlay = 0xffffff;
            if (k1 == 0)
                k1 = 0xffffff;
            int i2 = sprites[i1].getWidth();
            int j2 = sprites[i1].getHeight();
            int k2 = 0;
            int l2 = 0;
            int i3 = l1 << 16;
            int j3 = (i2 << 16) / k;
            int k3 = (j2 << 16) / l;
            int l3 = -(l1 << 16) / l;
            if (sprites[i1].requiresShift()) {
                int i4 = sprites[i1].getSomething1();
                int k4 = sprites[i1].getSomething2();
                j3 = (i4 << 16) / k;
                k3 = (k4 << 16) / l;
                int j5 = sprites[i1].getXShift();
                int k5 = sprites[i1].getYShift();
                if (flag)
                    j5 = i4 - sprites[i1].getWidth() - j5;
                i += ((j5 * k + i4) - 1) / i4;
                int l5 = ((k5 * l + k4) - 1) / k4;
                j += l5;
                i3 += l5 * l3;
                if ((j5 * k) % i4 != 0)
                    k2 = (i4 - (j5 * k) % i4 << 16) / k;
                if ((k5 * l) % k4 != 0)
                    l2 = (k4 - (k5 * l) % k4 << 16) / l;
                k = ((((sprites[i1].getWidth() << 16) - k2) + j3) - 1) / j3;
                l = ((((sprites[i1].getHeight() << 16) - l2) + k3) - 1) / k3;
            }
            int j4 = j * clipWidth;
            i3 += i << 16;
            if (j < imageY) {
                int l4 = imageY - j;
                l -= l4;
                j = imageY;
                j4 += l4 * clipWidth;
                l2 += k3 * l4;
                i3 += l3 * l4;
            }
            if (j + l >= subClipHeight)
                l -= ((j + l) - subClipHeight) + 1;
            int i5 = j4 / clipWidth & 1;
            if (!f1Toggle)
                i5 = 2;
            if (k1 == 0xffffff) {
                if (!flag) {
                    spritePlotTransparent(opacity, imagePixelArray, sprites[i1].getPixels(), 0, k2, l2, j4, k, l, j3, k3, i2, overlay, i3, l3, i5);
                    return;
                } else {
                    spritePlotTransparent(opacity, imagePixelArray, sprites[i1].getPixels(), 0, (sprites[i1].getWidth() << 16) - k2 - 1, l2, j4, k, l, -j3, k3, i2, overlay, i3, l3, i5);
                    return;
                }
            }
            if (!flag) {
                spritePlotTransparent(opacity, imagePixelArray, sprites[i1].getPixels(), 0, k2, l2, j4, k, l, j3, k3, i2, overlay, k1, i3, l3, i5);
                return;
            } else {
                spritePlotTransparent(opacity, imagePixelArray, sprites[i1].getPixels(), 0, (sprites[i1].getWidth() << 16) - k2 - 1, l2, j4, k, l, -j3, k3, i2, overlay, k1, i3, l3, i5);
                return;
            }
        }
        catch (Exception _ex) { }
    }
    
    private void spritePlotTransparent(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2, int i3) {
        spritePlotTransparent(256, ai, ai1, i, j, k, l, i1, j1, k1, l1, i2, overlay, k2, l2, i3);
    }
    
    private void spritePlotTransparent(int opacity, int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2, int i3) {
        int i4 = overlay >> 16 & 0xff; //R
        int j4 = overlay >> 8 & 0xff;  //G
        int k4 = overlay & 0xff;       //B
        try {
            int l4 = j;
            for (int i5 = -j1; i5 < 0; i5++) {
                int j5 = (k >> 16) * i2;
                int k5 = k2 >> 16;
                int l5 = i1;
                if (k5 < imageX) {
                    int i6 = imageX - k5;
                    l5 -= i6;
                    k5 = imageX;
                    j += k1 * i6;
                }
                if (k5 + l5 >= subClipWidth) {
                    int j6 = (k5 + l5) - subClipWidth;
                    l5 -= j6;
                }
                i3 = 1 - i3;
                if (i3 != 0) {
                    for (int k6 = k5; k6 < k5 + l5; k6++) {
                        i = ai1[(j >> 16) + j5];
                        if (i != 0) {
                            int inverseOpacity = 256 - opacity;
                            
                            int j3 = i >> 16 & 0xff;
                            int k3 = i >> 8 & 0xff;
                            int l3 = i & 0xff;
                            
                            if (j3 == k3 && k3 == l3){
                                j3 = (j3 * i4) >> 8;
                                k3 = (k3 * j4) >> 8;
                                l3 = (l3 * k4) >> 8;
                            }
                            
                            int spriteR = j3 * opacity;
                            int spriteG = k3 * opacity;
                            int spriteB = l3 * opacity;
                            
                            int canvasR = (ai[k6 + l] >> 16 & 0xff) * inverseOpacity;
                            int canvasG = (ai[k6 + l] >> 8 & 0xff) * inverseOpacity;
                            int canvasB = (ai[k6 + l] & 0xff) * inverseOpacity;
                            
                            int finalColour = (((spriteR + canvasR) >> 8) << 16) + (((spriteG + canvasG) >> 8) << 8) + ((spriteB + canvasB) >> 8);
                            ai[k6 + l] = finalColour;
                               /*
                            int j3 = i >> 16 & 0xff;
                            int k3 = i >> 8 & 0xff;
                            int l3 = i & 0xff;
                            if (j3 == k3 && k3 == l3)
                                ai[k6 + l] = ((j3 * i4 >> 8) << 16) + ((k3 * j4 >> 8) << 8) + (l3 * k4 >> 8);
                            else
                                ai[k6 + l] = i;
                            */
                        }
                        j += k1;
                    }

                }
                k += l1;
                j = l4;
                l += clipWidth;
                k2 += l2;
            }

            return;
        }
        catch (Exception _ex) { }
    }

    private void spritePlotTransparent(int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2, int i3, int j3){
        spritePlotTransparent(256, ai, ai1, i, j, k, l, i1, j1, k1, l1, i2, overlay, k2, l2, i3, j3);
    }
    
    private void spritePlotTransparent(int opacity, int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2, int i3, int j3) {
        int j4 = overlay >> 16 & 0xff;
        int k4 = overlay >> 8 & 0xff;
        int l4 = overlay & 0xff;
        int i5 = k2 >> 16 & 0xff;
        int j5 = k2 >> 8 & 0xff;
        int k5 = k2 & 0xff;
        try {
            int l5 = j;
            for (int i6 = -j1; i6 < 0; i6++) {
                int j6 = (k >> 16) * i2;
                int k6 = l2 >> 16;
                int l6 = i1;
                if (k6 < imageX) {
                    int i7 = imageX - k6;
                    l6 -= i7;
                    k6 = imageX;
                    j += k1 * i7;
                }
                if (k6 + l6 >= subClipWidth) {
                    int j7 = (k6 + l6) - subClipWidth;
                    l6 -= j7;
                }
                j3 = 1 - j3;
                if (j3 != 0) {
                    for (int k7 = k6; k7 < k6 + l6; k7++) {
                        i = ai1[(j >> 16) + j6];
                        if (i != 0) {
                            int inverseOpacity = 256 - opacity;
                            
                            int k3 = i >> 16 & 0xff;
                            int l3 = i >> 8 & 0xff;
                            int i4 = i & 0xff;
                            if (k3 == l3 && l3 == i4){
                                k3 = (k3 * j4) >> 8;
                                l3 = (l3 * k4) >> 8;
                                i4 = (i4 * l4) >> 8;
                            } else if(k3 == 255 && l3 == i4) {
                                k3 = (k3 * i5) >> 8;
                                l3 = (l3 * j5) >> 8;
                                i4 = (i4 * k5) >> 8;
                            }
                                
                            int spriteR = k3 * opacity;
                            int spriteG = l3 * opacity;
                            int spriteB = i4 * opacity;
                            
                            int canvasR = (ai[k7 + l] >> 16 & 0xff) * inverseOpacity;
                            int canvasG = (ai[k7 + l] >> 8 & 0xff) * inverseOpacity;
                            int canvasB = (ai[k7 + l] & 0xff) * inverseOpacity;
                            
                            int finalColour = (((spriteR + canvasR) >> 8) << 16) + (((spriteG + canvasG) >> 8) << 8) + ((spriteB + canvasB) >> 8);
                            ai[k7 + l] = finalColour;
                            /*
                            int k3 = i >> 16 & 0xff;
                            int l3 = i >> 8 & 0xff;
                            int i4 = i & 0xff;
                            if (k3 == l3 && l3 == i4)
                                ai[k7 + l] = ((k3 * j4 >> 8) << 16) + ((l3 * k4 >> 8) << 8) + (i4 * l4 >> 8);
                            else if (k3 == 255 && l3 == i4)
                                ai[k7 + l] = ((k3 * i5 >> 8) << 16) + ((l3 * j5 >> 8) << 8) + (i4 * k5 >> 8);
                            else
                                ai[k7 + l] = i;
                            */
                        }
                        j += k1;
                    }

                }
                k += l1;
                j = l5;
                l += clipWidth;
                l2 += i3;
            }

            return;
        }
        catch (Exception _ex) { }
    }

    public static void loadFont(String smallName, int fontNumber, ImplementationDelegate gameWindow) {
        boolean flag = false;
        boolean addCharWidth = false;
        smallName = smallName.toLowerCase();
        if (smallName.startsWith("helvetica"))
            smallName = smallName.substring(9);
        if (smallName.startsWith("h"))
            smallName = smallName.substring(1);
        if (smallName.startsWith("f")) {
            smallName = smallName.substring(1);
            flag = true;
        }
        if (smallName.startsWith("d")) {
            smallName = smallName.substring(1);
            addCharWidth = true;
        }
        if (smallName.endsWith(".jf"))
            smallName = smallName.substring(0, smallName.length() - 3);
        int style = 0;
        if (smallName.endsWith("b")) {
            style = 1;
            smallName = smallName.substring(0, smallName.length() - 1);
        }
        if (smallName.endsWith("p"))
            smallName = smallName.substring(0, smallName.length() - 1);
        int size = Integer.parseInt(smallName);
        Font font = new Font("Helvetica", style, size);
        FontMetrics fontmetrics = gameWindow.getContainerImpl().getFontMetrics(font);
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
        currentFont = 855;
        for (int charSetOffset = 0; charSetOffset < 95; charSetOffset++)
            drawLetter(font, fontmetrics, charSet.charAt(charSetOffset), charSetOffset, gameWindow, fontNumber, addCharWidth);

        fontArray[fontNumber] = new byte[currentFont];
        for (int i1 = 0; i1 < currentFont; i1++)
            fontArray[fontNumber][i1] = aByteArray351[i1];

        if (style == 1 && aBooleanArray349[fontNumber]) {
            aBooleanArray349[fontNumber] = false;
            loadFont("f" + size + "p", fontNumber, gameWindow);
        }
        if (flag && !aBooleanArray349[fontNumber]) {
            aBooleanArray349[fontNumber] = false;
            loadFont("d" + size + "p", fontNumber, gameWindow);
        }
    }

    public static void drawLetter(Font font, FontMetrics fontmetrics, char letter, int charSetOffset, ImplementationDelegate gameWindow, int fontNumber, boolean addCharWidth) {
        int charWidth = fontmetrics.charWidth(letter);
        int oldCharWidth = charWidth;
        if (addCharWidth)
            try {
                if (letter == '/')
                    addCharWidth = false;
                if (letter == 'f' || letter == 't' || letter == 'w' || letter == 'v' || letter == 'k' || letter == 'x' || letter == 'y' || letter == 'A' || letter == 'V' || letter == 'W')
                    charWidth++;
            }
            catch (Exception _ex) {
            }
        int i1 = fontmetrics.getMaxAscent();
        int j1 = fontmetrics.getMaxAscent() + fontmetrics.getMaxDescent();
        int k1 = fontmetrics.getHeight();
        Image image = gameWindow.getContainerImpl().createImage(charWidth, j1);
        Graphics g = image.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, charWidth, j1);
        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(String.valueOf(letter), 0, i1);
        if (addCharWidth)
            g.drawString(String.valueOf(letter), 1, i1);
        int ai[] = new int[charWidth * j1];
        PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, charWidth, j1, ai, 0, charWidth);
        try {
            pixelgrabber.grabPixels();
        }
        catch (InterruptedException _ex) {
            return;
        }
        image.flush();
        image = null;
        int l1 = 0;
        int i2 = 0;
        int j2 = charWidth;
        int k2 = j1;
        label0:
        for (int l2 = 0; l2 < j1; l2++) {
            for (int i3 = 0; i3 < charWidth; i3++) {
                int k3 = ai[i3 + l2 * charWidth];
                if ((k3 & 0xffffff) == 0)
                    continue;
                i2 = l2;
                break label0;
            }

        }

        label1:
        for (int j3 = 0; j3 < charWidth; j3++) {
            for (int l3 = 0; l3 < j1; l3++) {
                int j4 = ai[j3 + l3 * charWidth];
                if ((j4 & 0xffffff) == 0)
                    continue;
                l1 = j3;
                break label1;
            }

        }

        label2:
        for (int i4 = j1 - 1; i4 >= 0; i4--) {
            for (int k4 = 0; k4 < charWidth; k4++) {
                int i5 = ai[k4 + i4 * charWidth];
                if ((i5 & 0xffffff) == 0)
                    continue;
                k2 = i4 + 1;
                break label2;
            }

        }
        label3:
        for (int l4 = charWidth - 1; l4 >= 0; l4--) {
            for (int j5 = 0; j5 < j1; j5++) {
                int l5 = ai[l4 + j5 * charWidth];
                if ((l5 & 0xffffff) == 0)
                    continue;
                j2 = l4 + 1;
                break label3;
            }

        }
        aByteArray351[charSetOffset * 9] = (byte) (currentFont / 16384);
        aByteArray351[charSetOffset * 9 + 1] = (byte) (currentFont / 128 & 0x7f);
        aByteArray351[charSetOffset * 9 + 2] = (byte) (currentFont & 0x7f);
        aByteArray351[charSetOffset * 9 + 3] = (byte) (j2 - l1);
        aByteArray351[charSetOffset * 9 + 4] = (byte) (k2 - i2);
        aByteArray351[charSetOffset * 9 + 5] = (byte) l1;
        aByteArray351[charSetOffset * 9 + 6] = (byte) (i1 - i2);
        aByteArray351[charSetOffset * 9 + 7] = (byte) oldCharWidth;
        aByteArray351[charSetOffset * 9 + 8] = (byte) k1;
        for (int k5 = i2; k5 < k2; k5++) {
            for (int i6 = l1; i6 < j2; i6++) {
                int j6 = ai[i6 + k5 * charWidth] & 0xff;
                if (j6 > 30 && j6 < 230)
                    aBooleanArray349[fontNumber] = true;
                aByteArray351[currentFont++] = (byte) j6;
            }
        }
    }
    
	public void drawCenteredBoxAlpha(int x, int y, int width, int height, int color, int trans) {
		drawBoxAlpha(x - width / 2, y - height / 2, width, height, color, trans);
	}
    
	public void drawCenteredLineX(int x, int y, int len, int col) {
		drawLineX(x - (len / 2), y, len, col);
	}

	public void drawCenteredLineY(int x, int y, int len, int col) {
		drawLineY(x, y - (len / 2), len, col);
	}

    public void drawBoxTextRight(String s, int i, int j, int k, int l) {
        drawString(s, i - textWidth(s, k), j, k, l);
    }

    public void drawCenteredString(String s, int i, int j, int k, int l) {
        drawString(s, i - textWidth(s, k) / 2, j, k, l);
    }

    public void drawBoxTextColour(String s, int x, int y, int font, int colour, int wrapWidth) {
        try {
            int width = 0;
            byte[] fontData = fontArray[font];
            int lastLineEndsAt = 0;
            int lastBreak = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '@' && i + 4 < s.length() && s.charAt(i + 4) == '@')
                    i += 4;
                else if (s.charAt(i) == '~' && i + 4 < s.length() && s.charAt(i + 4) == '~')
                    i += 4;
                else if (s.charAt(i) == '~' && i + 5 < s.length() && s.charAt(i + 5) == '~')
                {
                	i += 5;
                }
                else
                    width += fontData[charIndexes[s.charAt(i)] + 7];
                if (s.charAt(i) == ' ')
                    lastBreak = i;
                if (s.charAt(i) == '%') {
                    lastBreak = i;
                    width = 1000;
                }
                if (width > wrapWidth) {
                    int lineEndsAt  = lastBreak;
                    if (lastBreak <= lastLineEndsAt)
                    {
                        lineEndsAt = lastBreak = i;
                        lineEndsAt++; // There has been no break at which to start the new line. We will split a word. When splitting a word, increment line ends at because of the indexing substr uses. We do NOT want this in the other cases because it would include the % sign
                    }
                    
                    StringBuilder colourCode    = new StringBuilder();
                    StringBuilder regexBuilder  = new StringBuilder(s.substring(0, lastLineEndsAt));
                    String regexCheck           = regexBuilder.reverse().toString();
                    Pattern regex               = Pattern.compile("(@.{3}@)");
                    Matcher match               = regex.matcher(regexCheck);

                    if(match.find())
                        colourCode  = colourCode.append(match.group(0)).reverse();
                    
                    drawCenteredString(colourCode + s.substring(lastLineEndsAt, lineEndsAt), x, y, font, colour);
                    width = 0;
                    lastLineEndsAt = i = lastBreak + 1;
                    y += messageFontHeight(font);
                }
            }

            if (width > 0) {
                StringBuilder colourCode    = new StringBuilder();
                StringBuilder regexBuilder  = new StringBuilder(s.substring(0, lastLineEndsAt));
                String regexCheck           = regexBuilder.reverse().toString();
                Pattern regex               = Pattern.compile("(@.{3}@)");
                Matcher match               = regex.matcher(regexCheck);

                if(match.find())
                    colourCode  = colourCode.append(match.group(0)).reverse();
                
                drawCenteredString(colourCode + s.substring(lastLineEndsAt), x, y, font, colour);
            }
        }
        catch (Exception exception) { }
    }

    public void drawString(String string, Point location, int k, int colour)
    {
    	drawString(string, location.x, location.y, k, colour);
    }
    
    public void drawString(String string, int x, int y, int k, int colour) {
        try {
            byte abyte0[] = fontArray[k];
            for (int offset = 0; offset < string.length(); offset++)
                if (string.charAt(offset) == '@' && offset + 4 < string.length() && string.charAt(offset + 4) == '@') {
                    if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("red"))
                        colour = 0xff0000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("lre"))
                        colour = 0xff9040;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("yel"))
                        colour = 0xffff00;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("gre"))
                        colour = 65280;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("blu"))
                        colour = 255;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("cya"))
                        colour = 65535;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("mag"))
                        colour = 0xff00ff;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("whi"))
                        colour = 0xffffff;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("bla"))
                        colour = 0;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("dre"))
                        colour = 0xc00000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("ora"))
                        colour = 0xff9040;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("ran"))
                        colour = (int) (Math.random() * 16777215D);
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("or1"))
                        colour = 0xffb000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("or2"))
                        colour = 0xff7000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("or3"))
                        colour = 0xff3000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("gr1"))
                        colour = 0xc0ff00;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("gr2"))
                        colour = 0x80ff00;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("gr3"))
                        colour = 0x40ff00;
					else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("sub"))
						colour = 0xEEDDDD;
					else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("eve"))
						colour = 0x4DBD33;
                    offset += 4;
                } else
                if (string.charAt(offset) == '~' && offset + 4 < string.length() && string.charAt(offset + 4) == '~') {
                    char c = string.charAt(offset + 1);
                    char c1 = string.charAt(offset + 2);
                    char c2 = string.charAt(offset + 3);
                    if (c >= '0' && c <= '9' && c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9')
                        x = Integer.parseInt(string.substring(offset + 1, offset + 4));
                    offset += 4;
                }
                else if(string.charAt(offset) == '~' && offset + 5 < string.length() && string.charAt(offset + 5) == '~')
                {
                    char c = string.charAt(offset + 1);
                    char c1 = string.charAt(offset + 2);
                    char c2 = string.charAt(offset + 3);
                    char c3 = string.charAt(offset + 4);
                    if (c >= '0' && c <= '9' && c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9' && c3 >= '0' && c3 <= '9')
                        x = Integer.parseInt(string.substring(offset + 1, offset + 5));
                    offset += 5;
                }
                else
                if (string.charAt(offset) == '#' && offset + 4 < string.length() && string.charAt(offset + 4) == '#' && string.substring(offset + 1, offset + 4).equalsIgnoreCase("adm")) {
                    //spriteClip4(x - 12, y - 16, 30, 20, 2339, -256, 0, 0, false);
					spriteClip4(x - 1, y - 10, 13, 11, 3150, -256, 0, 0, false);
					x += 14;
					offset += 4;
                } else if (string.charAt(offset) == '#' && offset + 4 < string.length() && string.charAt(offset + 4) == '#' && string.substring(offset + 1, offset + 4).equalsIgnoreCase("mod")) {
                    //spriteClip4(x - 12, y - 16, 30, 20, 2339, 0, 0, 0, false);
                    spriteClip4(x - 1, y - 10, 13, 11, 3150, -2302756, 0, 0, false);
                    x += 14;
                    offset += 4;
                } else if (string.charAt(offset) == '#' && offset + 4 < string.length() && string.charAt(offset + 4) == '#' && string.substring(offset + 1, offset + 4).equalsIgnoreCase("dev")) {
					//spriteClip4(x - 12, y - 16, 30, 20, 2339, 16711680, 0, 0, false);
					spriteClip4(x - 1, y - 10, 13, 11, 3150, 16711680, 0, 0, false);
                    x += 14;
                    offset += 4;
                } else if (string.charAt(offset) == '#' && offset + 4 < string.length() && string.charAt(offset + 4) == '#' && string.substring(offset + 1, offset + 4).equalsIgnoreCase("eve")) {
					//spriteClip4(x - 12, y - 16, 30, 20, 2339, 16711680, 0, 0, false);
					spriteClip4(x - 1, y - 10, 13, 11, 3150, 0x4DBD33, 0, 0, false);
                    x += 14;
                    offset += 4;
                } else {
                    int charIndex = charIndexes[string.charAt(offset)];
                    if (drawStringShadows && !aBooleanArray349[k] && colour != 0)
                        method257(charIndex, x + 1, y, 0, abyte0, aBooleanArray349[k]);
                    if (drawStringShadows && !aBooleanArray349[k] && colour != 0)
                        method257(charIndex, x, y + 1, 0, abyte0, aBooleanArray349[k]);
                    method257(charIndex, x, y, colour, abyte0, aBooleanArray349[k]);
                    x += abyte0[charIndex + 7];
                }

            return;
        }
        catch (Exception exception) { }
    }

    private void method257(int i, int j, int k, int l, byte abyte0[], boolean flag) {
        int i1 = j + abyte0[i + 5];
        int j1 = k - abyte0[i + 6];
        int k1 = abyte0[i + 3];
        int l1 = abyte0[i + 4];
        int i2 = abyte0[i] * 16384 + abyte0[i + 1] * 128 + abyte0[i + 2];
        int j2 = i1 + j1 * clipWidth;
        int k2 = clipWidth - k1;
        int l2 = 0;
        if (j1 < imageY) {
            int i3 = imageY - j1;
            l1 -= i3;
            j1 = imageY;
            i2 += i3 * k1;
            j2 += i3 * clipWidth;
        }
        if (j1 + l1 >= subClipHeight)
            l1 -= ((j1 + l1) - subClipHeight) + 1;
        if (i1 < imageX) {
            int j3 = imageX - i1;
            k1 -= j3;
            i1 = imageX;
            i2 += j3;
            j2 += j3;
            l2 += j3;
            k2 += j3;
        }
        if (i1 + k1 >= subClipWidth) {
            int k3 = ((i1 + k1) - subClipWidth) + 1;
            k1 -= k3;
            l2 += k3;
            k2 += k3;
        }
        if (k1 > 0 && l1 > 0) {
            if (flag) {
                method259(imagePixelArray, abyte0, l, i2, j2, k1, l1, k2, l2);
                return;
            }
            plotLetter(imagePixelArray, abyte0, l, i2, j2, k1, l1, k2, l2);
        }
    }

    private void plotLetter(int ai[], byte abyte0[], int i, int j, int k, int l, int i1, int j1, int k1) {
        try {
            int l1 = -(l >> 2);
            l = -(l & 3);
            for (int i2 = -i1; i2 < 0; i2++) {
                for (int j2 = l1; j2 < 0; j2++) {
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;
                }
                for (int k2 = l; k2 < 0; k2++)
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;

                k += j1;
                j += k1;
            }
            return;
        }
        catch (Exception exception) { }
    }

    private void method259(int ai[], byte abyte0[], int i, int j, int k, int l, int i1, int j1, int k1) {
        for (int l1 = -i1; l1 < 0; l1++) {
            for (int i2 = -l; i2 < 0; i2++) {
                int j2 = abyte0[j++] & 0xff;
                if (j2 > 30) {
                    if (j2 >= 230) {
                        ai[k++] = i;
                    } else {
                        int k2 = ai[k];
                        ai[k++] = ((i & 0xff00ff) * j2 + (k2 & 0xff00ff) * (256 - j2) & 0xff00ff00) + ((i & 0xff00) * j2 + (k2 & 0xff00) * (256 - j2) & 0xff0000) >> 8;
                    }
                } else {
                    k++;
                }
            }

            k += j1;
            j += k1;
        }
    }
    
    public static int loadFont_(byte font_arr[]) {
    	if(font_arr == null) {
    		return -1;
    	}
		fontArray[currentFont] = font_arr;
		return currentFont++;
	}

    public int messageFontHeight(int messageType) {
        if (messageType == 0)
            return 12;
        if (messageType == 1)
            return 14;
        if (messageType == 2)
            return 14;
        if (messageType == 3)
            return 15;
        if (messageType == 4)
            return 15;
        if (messageType == 5)
            return 19;
        if (messageType == 6)
            return 24;
        if (messageType == 7)
            return 29;
        else
            return fontHeight(messageType);
    }

    public static int fontHeight(int i) {
        if (i == 0) {
            return fontArray[i][8] - 2;
        } else {
            return fontArray[i][8] - 1;
        }
    }

    public static int textWidth(String s, int i) {
        int j = 0;
        byte abyte0[] = fontArray[i];
        for (int k = 0; k < s.length(); k++) {
            if (s.charAt(k) == '@' && k + 4 < s.length() && s.charAt(k + 4) == '@') {
                k += 4;
            } else if (s.charAt(k) == '~' && k + 4 < s.length() && s.charAt(k + 4) == '~') {
                k += 4;
            } 
            else if (s.charAt(k) == '~' && k + 5 < s.length() && s.charAt(k + 5) == '~')
            {
            	k += 5;
            }
            else {
                j += abyte0[charIndexes[s.charAt(k)] + 7];
            }
        }
        return j;
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1) {
        return true;
    }
   

    public int clipWidth;
    public int clipHeight;
    ColorModel colourModel;
    public int imagePixelArray[];
    ImageConsumer imageConsumer;
    public Image image;
    private int imageY;
    private int subClipHeight;
    private int imageX;
    private int subClipWidth;
    public boolean f1Toggle;
    static byte fontArray[][] = new byte[50][];
    static int charIndexes[];
    public boolean drawStringShadows;
    int anIntArray339[];
    int anIntArray340[];
    int anIntArray341[];
    int anIntArray342[];
    int anIntArray343[];
    int anIntArray344[];
    int anIntArray345[];
    public static int anInt346;
    public static int anInt347;
    public static int anInt348;
    private static boolean aBooleanArray349[] = new boolean[12];
    private static int currentFont;
    private static byte aByteArray351[] = new byte[0x186a0];
    public static int anInt352;
	public static int font_count;

    static {
        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
        charIndexes = new int[256];
        for (int i = 0; i < 256; i++) {
            int j = s.indexOf(i);
            if (j == -1) {
                j = 74;
            }
            charIndexes[i] = j * 9;
        }
    }

	public void drawBoxEdge(Rectangle dimensions, int c) {
		drawBoxEdge(dimensions.x, dimensions.y, dimensions.width, dimensions.height, c);

	}
}
