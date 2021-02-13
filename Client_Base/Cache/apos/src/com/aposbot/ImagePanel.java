package com.aposbot;

import java.awt.*;

public class ImagePanel extends Panel {

    private static final long serialVersionUID = 4557522767188007469L;
    private final Image image;

    public ImagePanel(Image image) {
        this.image = image;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }
}
