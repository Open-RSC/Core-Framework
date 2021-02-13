package com.stormy.ocrlib;

import java.util.ArrayDeque;

public class SimpleImage {

    private int width;
    private int height;
    private byte[] data;

    public SimpleImage(int width, int height) {
        this.width = width;
        this.height = height;
        data = new byte[width * height];
    }

    public SimpleImage(SimpleImage image) {
        copy(image);
    }

    private static double R(double x) {
        // Auxiliary function for scaling

        double a, b, c, d;

        // Implement Eq 4.3-5 in four parts

        if ((x + 2.0F) <= 0.0F) {
            a = 0.0F;
        } else {
            a = Math.pow((x + 2.0F), 3.0F);
        }

        if ((x + 1.0F) <= 0.0F) {
            b = 0.0F;
        } else {
            b = Math.pow((x + 1.0F), 3.0F);
        }

        if (x <= 0) {
            c = 0.0F;
        } else {
            c = Math.pow(x, 3.0F);
        }

        if ((x - 1.0F) <= 0.0F) {
            d = 0.0F;
        } else {
            d = Math.pow((x - 1.0F), 3.0F);
        }

        return (1.f / 6.f * (a - (4.0F * b) + (6.0F * c) - (4.0F * d)));
    }

    public int Width() {
        return width;
    }

    public int Height() {
        return height;
    }

    public void setPixel(int x, int y, int value) {
        data[y * width + x] = (byte) value;
    }

    public int getPixel(int x, int y) {
        return data[y * width + x] & 0xff;
    }

    void copy(SimpleImage srcImage) {
        final int area = srcImage.width * srcImage.height;
        if ((width != srcImage.width) || (height != srcImage.height)) {
            width = srcImage.width;
            height = srcImage.height;
            data = new byte[area];
        }
        System.arraycopy(srcImage.data, 0, data, 0, area);
    }

    void copy(SimpleImage srcImage, int srcX, int srcY, int srcW, int srcH,
              int destX, int destY) {
        for (int x = 0; x < srcW; ++x) {
            for (int y = 0; y < srcH; ++y) {
                setPixel(destX + x, destY + y, srcImage.getPixel((srcX + x) - srcW, srcY + y));
            }
        }
    }

    void rotate(double angle) {

        // This is the worst method I could implement to rotate an image, but it
        // works well enough. I wanted to implement a better method but it looks
        // like I don't have to.
        // Angle is given in radians.

        // these are the limits of the rotated image
        double minX, maxX, minY, maxY;

        double x, y;

        double sina, cosa;

        sina = Math.sin(angle);
        cosa = Math.cos(angle);

        // this is the first point (0,0)
        minX = 0.f;
        maxX = 0.f;
        minY = 0.f;
        maxY = 0.f;

        // check where the second point will go (0,height)
        x = sina * height;
        y = cosa * height;

        if (minX > x) {
            minX = x;
        }
        if (maxX < x) {
            maxX = x;
        }
        if (minY > y) {
            minY = y;
        }
        if (maxY < y) {
            maxY = y;
        }

        // check where the third point will go (width, height)

        x = cosa * width + sina * height;
        y = -sina * width + cosa * height;

        if (minX > x) {
            minX = x;
        }
        if (maxX < x) {
            maxX = x;
        }
        if (minY > y) {
            minY = y;
        }
        if (maxY < y) {
            maxY = y;
        }

        // check where the fourth point will go (width, 0)

        x = cosa * width;
        y = -sina * width;

        if (minX > x) {
            minX = x;
        }
        if (maxX < x) {
            maxX = x;
        }
        if (minY > y) {
            minY = y;
        }
        if (maxY < y) {
            maxY = y;
        }

        // round minimums towards -\infty and maximums towards +\infty

        minX = Math.floor(minX);
        minY = Math.floor(minY);
        maxX = Math.ceil(maxX);
        maxY = Math.ceil(maxY);

        final SimpleImage tmpIm = new SimpleImage((int) (maxX - minX), (int) (maxY - minY));

        for (double i = 0; i < maxY - minY; i++) {
            for (double j = 0; j < maxX - minX; j++) {
                x = cosa * (j + minX) - sina * (i + minY);
                y = sina * (j + minX) + cosa * (i + minY);

                // see if x and y are within the image
                final int xx = (int) x;
                final int yy = (int) y;

                if ((xx < 0) || (yy < 0) || (xx >= width) || (yy >= height)) {
                    continue;
                }

                // if we got here then xx and yy are valid
                tmpIm.setPixel((int) j, (int) i, getPixel(xx, yy));
            }
        }
        copy(tmpIm);
    }

    void scale(int sizeX, int sizeY) {
        final SimpleImage tmpImage = new SimpleImage(sizeX, sizeY);

        int ip, jp, m, n;
        double x, y, dx, dy;
        int i, j;

        for (ip = 0; ip < sizeY; ip++) {
            for (jp = 0; jp < sizeX; jp++) {

                x = jp * width / sizeX;
                dx = x - Math.floor(x);
                j = (int) Math.floor(x);

                y = ip * height / sizeY;
                dy = y - Math.floor(y);
                i = (int) Math.floor(y);

                double res = 0;

                for (m = -1; m <= 2; m++) {
                    for (n = -1; n <= 2; n++) {

                        int yy = i + m;
                        int xx = j + n;

                        // xx = xx < 0 ? 0 : xx;
                        if (xx < 0) {
                            xx = 0;
                        }
                        xx = xx >= width ? width - 1 : xx;

                        yy = yy < 0 ? 0 : yy;
                        yy = yy >= height ? height - 1 : yy;

                        res += getPixel(xx, yy) * R(n - dx) * R(m - dy);
                    }
                }
                tmpImage.setPixel(jp, ip, (int) res);
            }
        }
        copy(tmpImage);
    }

    void center(double x_y_ratio) {
        // The output will be an image with width/heigh = x_y_ratio. At first
        // all
        // the black pixels will be removed from the image, leaving the minimum
        // image that includes the white pixels. This image will be centered
        // into a
        // minimum rectangle whose width/height ratio is x_y_ratio and includes
        // it.

        // Seems to be perf.

        // First remove the black borders
        cropBorders();

        // Then find the enclosing rectangle
        double w1, h1;

        w1 = width;
        h1 = height;
        if (width < height * x_y_ratio) {
            w1 = height * x_y_ratio;
        } else {
            h1 = width / x_y_ratio;
        }

        // Now w1 and h1 are the dimensions of the new image
        w1 = Math.ceil(w1);
        h1 = Math.ceil(h1);

        final SimpleImage tmpIm = new SimpleImage((int) w1, (int) h1);

        // Now find the x and y offsets of the original image in the new image
        double offsetX, offsetY;

        offsetX = (w1 - width) / 2.f;
        offsetY = (h1 - height) / 2.f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tmpIm.setPixel((int) (x + offsetX), (int) (y + offsetY), getPixel(x, y));
            }
        }
        copy(tmpIm);
    }

    void cropBorders() {
        int minX = width, minY = height, maxX = -1, maxY = -1;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (getPixel(j, i) != 0) {
                    if (j < minX) {
                        minX = j;
                    }
                    if (j > maxX) {
                        maxX = j;
                    }
                    if (i < minY) {
                        minY = i;
                    }
                    if (i > maxY) {
                        maxY = i;
                    }
                }
            }
        }

        // Then we have at least a point that is not black
        if (minX < width) {
            final SimpleImage tmpIm = new SimpleImage(maxX - minX + 1, maxY - minY + 1);

            // Now copy the part of the image to tmpIm;
            for (int i = 0; i < maxY - minY + 1; i++) {
                for (int j = 0; j < maxX - minX + 1; j++) {
                    tmpIm.setPixel(j, i, getPixel(j + minX, i + minY));
                }
            }
            copy(tmpIm);
        } else {
            // The image is all black.
            final SimpleImage tmpIm = new SimpleImage(1, 1);
            copy(tmpIm);
        }
    }

    Region extract(int x, int y, boolean eightWay) {
        SimpleImage tmpImage = new SimpleImage(width, height);

        if ((x < 0) || (y < 0) || (x >= width) || (y >= height)) {
            return null;
        }

        if (getPixel(x, y) != 255) {
            return null;
        }

        // if the pixel at (x, y) is white, then we can do a flood fill

        int minX, maxX, minY, maxY;

        ArrayDeque<Point> floodStack = new ArrayDeque<>();

        Point p = new Point(x, y);

        minX = p.x;
        maxX = p.x;
        minY = p.y;
        maxY = p.y;

        tmpImage.setPixel(p.x, p.y, 255);

        floodStack.push(p);

        do {
            p = floodStack.getLast();
            floodStack.pop();

            // now push all unvisited neighbours on stack
            int i, j;
            for (i = -1; i <= 1; i++) {
                for (j = -1; j <= 1; j++) {
                    if (((p.x + i) < 0) || ((p.y + j) < 0)
                            || ((p.x + i) >= width) || ((p.y + j) >= height)) {
                        continue;
                    }

                    if (!eightWay) {
                        if ((i != 0) && (j != 0)) {
                            continue;
                        }
                    }

                    // now add that point
                    if ((tmpImage.getPixel(p.x + i, p.y + j) != 255)
                            && (getPixel(p.x + i, p.y + j) == 255)) {
                        // mark it as visited
                        tmpImage.setPixel(p.x + i, p.y + j, 255);

                        // and add it
                        floodStack.push(new Point(p.x + i, p.y + j));

                        // now update the min and max

                        if (p.x + i < minX)
                            minX = p.x + i;
                        if (p.x + i > maxX)
                            maxX = p.x + i;

                        if (p.y + j < minY)
                            minY = p.y + j;
                        if (p.y + j > maxY)
                            maxY = p.y + j;
                    }
                }
            }

        } while (!floodStack.isEmpty());

        // now we know the flood fill is included in the rectable delimited by
        // minX, maxX, minY, maxY

        // This will be the region that we'll return
        Region r = new Region(maxX - minX + 1, maxY - minY + 1, minX, minY);

        int i, j;
        for (j = minY; j <= maxY; j++) {
            for (i = minX; i <= maxX; i++) {
                r.setPixel(i - minX, j - minY, tmpImage.getPixel(i, j));
            }
        }

        return r;

    }

    void remove(Region reg) {
        int minX, minY, maxX, maxY;

        minX = reg.OffsetX();
        minY = reg.OffsetY();

        maxX = width < minX + reg.Width() ? width : minX + reg.Width();
        maxY = height < minY + reg.Height() ? height : minY + reg.Height();

        int i, j;

        for (j = minY; j < maxY; j++) {
            for (i = minX; i < maxX; i++) {
                if (reg.getPixel(i - minX, j - minY) == 255) {
                    setPixel(i, j, 0);
                }
            }
        }
    }
}
