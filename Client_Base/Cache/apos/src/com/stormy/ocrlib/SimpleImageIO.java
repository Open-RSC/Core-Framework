package com.stormy.ocrlib;

import java.io.IOException;

public final class SimpleImageIO {

    private SimpleImageIO() {
    }

    public static SimpleImage readBMP(byte[] data) throws IOException {

        final int type = get2l(data, 0);
        if (type != 19778) {
            throw new IOException("Data not valid bitmap: no magic header");
        }

        final int width = get4l(data, 18);
        final int height = get4l(data, 22);

        final int bpp = get2l(data, 26);
        if (bpp != 1) {
            throw new IOException("Expected 1 bpp, got " + bpp);
        }

        // Calculate the size of the image data with padding

        // First calculate the size of one row
        int rowSize = width / 8;
        if ((width % 8) != 0) {
            rowSize++;
        }

        // Adjust the row size to the next multiple of 4 bytes
        if ((rowSize % 4) != 0) {
            rowSize += 4 - (rowSize % 4);
        }

        final int dataSize = height * rowSize;
        final byte[] tmpData = new byte[dataSize];

        int offset = 30;

        for (int i = 0; i < dataSize; ++i) {
            tmpData[i] = data[offset++];
        }

        final SimpleImage image = new SimpleImage(width, height);

        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int tmp = ((tmpData[y * rowSize + (x / 8)] & 0xff) >> (7 - x % 8)) & 1;
                if (tmp != 0) {
                    image.setPixel(x, y, 255);
                }
            }
        }

        return image;
    }

    private static int get2l(byte[] b, int offset) {
        return ((b[offset + 1] & 0xff) << 8) |
                (b[offset] & 0xff);
    }

    private static int get4l(byte[] b, int offset) {
        return ((b[offset + 3] & 0xff) << 24) |
                ((b[offset + 2] & 0xff) << 16) |
                ((b[offset + 1] & 0xff) << 8) |
                (b[offset] & 0xff);
    }
}
