package com.loader.openrsc.frame.elements;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JComponent;
import java.awt.Graphics;
import javax.swing.JButton;
import java.awt.Image;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.net.URL;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.Dimension;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import com.loader.openrsc.util.Utils;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.StyledDocument;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NewsBox extends JPanel
{
    private JLabel title;
    public StyledDocument styleDoc;
    public HTMLDocument doc;
    public HTMLEditorKit editorKit;
    private JScrollPane spane;
    private JTextPane textArea;
    
    public JLabel getTitle() {
        return this.title;
    }
    
    public NewsBox() {
        this.setLayout(null);
        this.setOpaque(false);
        (this.title = new JLabel("")).setBounds(5, 49, 217, 17);
        this.title.setFont(Utils.getFont("runescape_uf.ttf", 0, 14.0f));
        this.title.setForeground(Color.WHITE);
        this.add(this.title);
        (this.textArea = new JTextPane()).setEditable(false);
        this.textArea.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.textArea.setBackground(new Color(14, 14, 14));
        this.spane = new JScrollPane(this.textArea);
        this.spane.getHorizontalScrollBar().setUI(new CustomScrollBar());
        this.spane.getVerticalScrollBar().setUI(new CustomScrollBar());
        this.spane.getVerticalScrollBar().setPreferredSize(new Dimension(5, 5));
        this.spane.setBorder(null);
        this.spane.setBackground(new Color(14, 14, 14));
        this.textArea.setContentType("text/html");
        this.styleDoc = this.textArea.getStyledDocument();
        this.doc = (HTMLDocument)this.styleDoc;
        this.editorKit = (HTMLEditorKit)this.textArea.getEditorKit();
        this.spane.setBounds(3, 69, 223, 260); // News textbox boundaries
        this.add(this.spane);
        this.textArea.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(final HyperlinkEvent event) {
                if (HyperlinkEvent.EventType.ACTIVATED == event.getEventType()) {
                    Utils.openWebpage(event.getURL().toString());
                }
            }
        });
    }
    
    public void append(String message) {
        message = convertUrl(message);
        try {
            this.editorKit.insertHTML(this.doc, this.doc.getLength(), message, 0, 0, null);
            this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
        }
        catch (BadLocationException ex) {}
        catch (IOException ex2) {}
    }
    
    public static String convertUrl(final String message) {
        final StringBuilder sb = new StringBuilder();
        String[] split;
        for (int length = (split = message.split(" ")).length, i = 0; i < length; ++i) {
            final String urls = split[i];
            try {
                final URL url = new URL(urls);
                sb.append("<a style='color:rgb(43,54,72);text-decoration:none;' href=\"" + url.toString() + "\">" + url.toString() + "</a>").append(" ");
            }
            catch (Exception e) {
                sb.append(urls).append(" ");
            }
        }
        return sb.toString();
    }
    
    public class CustomScrollBar extends MetalScrollBarUI
    {
        private Image imageThumb;
        private Image imageTrack;
        private JButton b;
        
        public CustomScrollBar() {
            this.b = new JButton("^") {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(0, 0);
                }
            };
            this.imageThumb = FauxImage.create(5, 32, new Color(43, 54, 72));
            this.imageTrack = FauxImage.create(5, 32, new Color(10, 10, 10));
        }
        
        @Override
        protected void paintThumb(final Graphics g, final JComponent c, final Rectangle r) {
            g.setColor(Color.blue);
            ((Graphics2D)g).drawImage(this.imageThumb, r.x, r.y, r.width, r.height, null);
        }
        
        @Override
        protected void paintTrack(final Graphics g, final JComponent c, final Rectangle r) {
            ((Graphics2D)g).drawImage(this.imageTrack, r.x, r.y, r.width, r.height, null);
        }
        
        @Override
        protected JButton createDecreaseButton(final int orientation) {
            return this.b;
        }
        
        @Override
        protected JButton createIncreaseButton(final int orientation) {
            return this.b;
        }
    }
    
    private static class FauxImage
    {
        public static Image create(final int w, final int h, final Color c) {
            final BufferedImage bi = new BufferedImage(w, h, 2);
            final Graphics2D g2d = bi.createGraphics();
            g2d.setPaint(c);
            g2d.fillRect(0, 0, w, h);
            g2d.dispose();
            return bi;
        }
    }
}
