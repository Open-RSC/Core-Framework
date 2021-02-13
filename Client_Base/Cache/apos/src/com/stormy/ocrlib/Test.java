package com.stormy.ocrlib;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public final class Test {

    public static void main(String[] args) throws Throwable {
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        try {
			/*br1 = new BufferedReader(new FileReader("./lib/Model.txt"));
			br2 = new BufferedReader(new FileReader("./lib/Dictionary.txt"));
			DictSearch ds = new DictSearch(br2);
			OCR ocr = new OCR(ds, br1);*/
            SimpleImage image = SimpleImageIO.readBMP(Files.readAllBytes(Paths.get("./HC.BMP")));
            //System.out.println(ocr.guess(image, true));
            display(splitToBuffered(image, true));
        } finally {
            if (br1 != null)
                br1.close();
            if (br2 != null)
                br2.close();
        }
    }

    private static BufferedImage toBufferedImage(SimpleImage src) {
        int width = src.Width();
        int height = src.Height();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = src.getPixel(x, height - y - 1);
                image.setRGB(x, y,
                        ((p & 0xFF) << 16) |
                                ((p & 0xFF) << 8) |
                                ((p & 0xFF) << 0));
            }
        }
        return image;
    }

    private static BufferedImage[] splitToBuffered(SimpleImage src, boolean use_new) {
        BufferedImage[] ai;
        if (use_new) {
            Segmentor s = new Segmentor();
            s.process(src);
            int count = s.getNrOfLetters();
            ai = new BufferedImage[count];
            for (int i = 0; i < count; ++i) {
                SimpleImage letter = s.getLetter(i, true);
                letter.center(1);
                letter.scale(15, 15);
                ai[i] = toBufferedImage(letter);
            }
        } else {
            List<Letter> split = Letter.splitLetters(src);
            int count = split.size();
            ai = new BufferedImage[count];
            for (int i = 0; i < count; ++i) {
                final Letter letter = split.get(i);
                letter.center(1);
                letter.scale(15, 15);
                ai[i] = toBufferedImage(letter);
            }
        }
        return ai;
    }

    private static void display(final BufferedImage[] ai) {
        int width = 0;
        int height = 0;
        for (BufferedImage image : ai) {
            int ih = image.getHeight() * 3;
            if (ih > height) {
                height = ih;
            }
            width += (image.getWidth() * 3);
        }
        final int fh = height;
        Component p = new Component() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                int y = 0;
                int x = 0;
                for (BufferedImage image : ai) {
                    int w = image.getWidth() * 3;
                    g.drawImage(image, x, y, w, image.getHeight() * 3, null);
                    x += w;
                }
                y += (fh + 15);
                x = 0;
                g.setColor(Color.BLACK);
                g.drawString("Number of letters: " + ai.length, x, y);
            }
        };
        p.setPreferredSize(new Dimension(width, height + 30));
        Frame f = new Frame("Output");
        f.add(p);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setResizable(false);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
