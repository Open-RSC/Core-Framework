package com.stormy.ocrlib;

class Region extends SimpleImage {

    private final int offset_x;
    private final int offset_y;

    Region(int width, int height) {
        super(width, height);
        offset_x = 0;
        offset_y = 0;
    }

    Region(int w, int h, int ox, int oy) {
        super(w, h);
        offset_x = ox;
        offset_y = oy;
    }

    Region(SimpleImage image) {
        super(image);
        offset_x = 0;
        offset_y = 0;
    }

    int OffsetX() {
        return offset_x;
    }

    int OffsetY() {
        return offset_y;
    }

    @Override
    public String toString() {
        return String.format("Region [offset_x=%s, offset_y=%s]", offset_x,
                offset_y);
    }
}
