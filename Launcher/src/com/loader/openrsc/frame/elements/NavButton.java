package com.loader.openrsc.frame.elements;

import com.loader.openrsc.frame.listeners.ButtonListener;
import com.loader.openrsc.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@SuppressWarnings("serial")
public class NavButton extends JButton implements MouseListener {
    public NavButton(final String text, final Rectangle bounds) {
        super(text.toUpperCase());
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setForeground(Color.WHITE);
        this.setHorizontalTextPosition(0);
        this.setFont(Utils.getFont("Exo-Regular.otf", 0, 10.0f));
        this.addMouseListener(this);
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setFocusable(false);
        this.setBounds(bounds);
        this.addActionListener(new ButtonListener());
    }

    @Override
    public void mouseClicked(final MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(final MouseEvent arg0) {
        this.setCursor(Cursor.getPredefinedCursor(12));
        this.setForeground(this.getForeground().darker());
    }

    @Override
    public void mouseExited(final MouseEvent arg0) {
        this.setCursor(Cursor.getPredefinedCursor(0));
        this.setForeground(this.getForeground().brighter());
    }

    @Override
    public void mousePressed(final MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(final MouseEvent arg0) {
    }
}
