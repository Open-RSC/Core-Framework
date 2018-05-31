package org.rscemulation.installer.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ImageScrollBarUI
	extends
		BasicScrollBarUI
{
	private final Image track;
	private final Image grip;
	
	public ImageScrollBarUI(Image track, Image grip)
	{
		this.track = track;
		this.grip = grip;
	}
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createButton();
    }

    @Override    
    protected JButton createIncreaseButton(int orientation) {
        return createButton();
    }

    private JButton createButton() {
        JButton jbutton = new JButton();
        jbutton.setPreferredSize(new Dimension(0, 0));
        jbutton.setMinimumSize(new Dimension(0, 0));
        jbutton.setMaximumSize(new Dimension(0, 0));
        return jbutton;
    }
    
    @Override
    public void paint(Graphics g, JComponent c)
       {
         paintTrack(g, c, getTrackBounds());
         paintThumb(g, c, getThumbBounds());
     
       }
    
	 @Override
	    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
	 {
	        g.translate(thumbBounds.x, thumbBounds.y);
	        ((Graphics2D)g).drawImage(grip, AffineTransform.getScaleInstance((double)thumbBounds.width/grip.getWidth(null),(double)thumbBounds.height/grip.getHeight(null)), null);
	        g.translate( -thumbBounds.x, -thumbBounds.y );
	    }

	    @Override
	    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
	    {
	    	g.translate(trackBounds.x, trackBounds.y);
	        ((Graphics2D)g).drawImage(track,AffineTransform.getScaleInstance(1,(double)trackBounds.height/track.getHeight(null)),null);
	        g.translate( -trackBounds.x, -trackBounds.y );
	    }
}
