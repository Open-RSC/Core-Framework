package com.loader.openrsc.frame.listeners;

import com.loader.openrsc.frame.AppFrame;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class PositionListener implements MouseListener, MouseMotionListener
{
    private AppFrame frame;
    private static Point initialClick;
    
    public PositionListener(final AppFrame frame) {
        this.frame = frame;
    }
    
    @Override
    public void mouseClicked(final MouseEvent arg0) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent arg0) {
    }
    
    @Override
    public void mouseExited(final MouseEvent arg0) {
    }
    
    @Override
    public void mousePressed(final MouseEvent e) {
        PositionListener.initialClick = e.getPoint();
        this.frame.getComponentAt(PositionListener.initialClick);
    }
    
    @Override
    public void mouseReleased(final MouseEvent arg0) {
    }
    
    @Override
    public void mouseDragged(final MouseEvent e) {
        final int iX = PositionListener.initialClick.x;
        final int iY = PositionListener.initialClick.y;
        if (iX >= 0 && iX <= this.frame.getWidth() && iY >= 0 && iY <= 70) {
            final int thisX = this.frame.getLocation().x;
            final int thisY = this.frame.getLocation().y;
            final int xMoved = thisX + e.getX() - (thisX + PositionListener.initialClick.x);
            final int yMoved = thisY + e.getY() - (thisY + PositionListener.initialClick.y);
            final int X = thisX + xMoved;
            final int Y = thisY + yMoved;
            this.frame.setLocation(X, Y);
        }
    }
    
    @Override
    public void mouseMoved(final MouseEvent arg0) {
    }
}
