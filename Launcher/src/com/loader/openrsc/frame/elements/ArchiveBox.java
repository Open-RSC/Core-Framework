package com.loader.openrsc.frame.elements;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.Color;

import java.awt.Rectangle;
import java.awt.event.MouseListener;
import javax.swing.JLabel;

import com.loader.openrsc.net.xml.FeedMessage;
import com.loader.openrsc.util.Utils;

@SuppressWarnings("serial")
public class ArchiveBox extends JLabel implements MouseListener
{
	private String url;
    
    public ArchiveBox(final FeedMessage message, final Rectangle bounds) {
        super(Utils.getImage("oldnews.png"));
        this.url = message.getLink();
        this.setLayout(null);
        this.setFocusable(false);
        JLabel title = new JLabel(message.getTitle());
        title.setBounds(9, 8, 175, 12);
        title.setFont(Utils.getFont("runescape_uf.ttf", 0, 11.0f));
        title.setForeground(new Color(220, 220, 220));
        this.add(title);
        title = new JLabel(message.getSplitDate());
        title.setBounds(9, 20, 163, 11);
        title.setFont(Utils.getFont("runescape_uf.ttf", 0, 9.0f));
        title.setForeground(new Color(150, 150, 150));
        this.add(title);
        this.addMouseListener(this);
        this.setBounds(bounds);
    }
    
    @Override
    public void mouseClicked(final MouseEvent arg0) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent e) {
        this.setIcon(Utils.getImage("oldnews_hover.png"));
        this.setCursor(Cursor.getPredefinedCursor(12));
    }
    
    @Override
    public void mouseExited(final MouseEvent e) {
        this.setIcon(Utils.getImage("oldnews.png"));
        this.setCursor(Cursor.getPredefinedCursor(0));
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(final MouseEvent e) {
        Utils.openWebpage(this.url);
    }
}
