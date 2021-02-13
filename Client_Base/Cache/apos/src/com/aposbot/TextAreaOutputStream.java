package com.aposbot;

import java.awt.*;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {

    private final TextArea area;

    public TextAreaOutputStream(TextArea area) {
        this.area = area;
    }

    @Override
    public void write(int b) {
        area.append(new String(new byte[]{
                (byte) b
        }).intern());
    }

    @Override
    public void write(byte[] b, int off, int len) {
        area.append(new String(b, off, len));
    }

    @Override
    public void write(byte[] b) {
        area.append(new String(b));
    }
}
