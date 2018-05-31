/*
 * Copyright (C) RSCDaemon - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCDaemon Team <dev@rscdaemon.com>, Unknown Date
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package org.rscemulation.installer.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicProgressBarUI;

import org.rscemulation.installer.Config;

/**
 * An extension of the {@link JProgressBar} class that supports gradients.  
 * It should be noted that most of the base UI for this class was copied 
 * out of the Oracle JDK source.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public class ProgressBar
	extends
		JProgressBar
{

	private static final long serialVersionUID = 8343210922545438864L;

	/// Override some {@link UIManager} properties
	static
	{
		UIManager.put("ProgressBar.background", new Color(255, 255, 255, 1)); //$NON-NLS-1$
		UIManager.put("ProgressBar.foreground", Config.PROGRESS_BAR_COLOR); //$NON-NLS-1$
		UIManager.put("ProgressBar.selectionBackground", new Color(255, 255, 255, 1)); //$NON-NLS-1$
		UIManager.put("ProgressBar.selectionForeground", Config.PROGRESS_BAR_COLOR); //$NON-NLS-1$
	}
	
	/**
	 * Constructs a <code>ProgressBar</code> with the provided bounds
	 * 
	 * @param x the x-coordinate of the upper-left hand corner
	 * 
	 * @param y the y-coordinate of the upper-left hand corner
	 * 
	 * @param width the width
	 * 
	 * @param height the height
	 * 
	 */
	public ProgressBar(int x, int y, int width, int height)
	{
		super.setBorderPainted(false);
		super.setOpaque(false);
		super.setIndeterminate(true);
		super.setBounds(x, y, width, height);
		super.setUI(new GradientProgressBarUI());
	}
	
	protected class GradientProgressBarUI
		extends
			BasicProgressBarUI
	{
	    @Override
	    public void paint(Graphics g, JComponent c)
	    {
	        Graphics2D g2 = (Graphics2D) g;
	        // for antialiasing geometric shapes
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
	
	        // for antialiasing text
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	
	        // to go for quality over speed
	        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
	                RenderingHints.VALUE_RENDER_QUALITY);
	        super.paint(g, c);
	    }
	    
	    @Override
	    protected void paintIndeterminate(Graphics g, JComponent c)
	    {
	        if (!(g instanceof Graphics2D))
	        {
	            return;
	        }
	
	        Insets b = progressBar.getInsets();
	        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
	        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);
	
	        if (barRectWidth <= 0 || barRectHeight <= 0)
	        {
	            return;
	        }
	
	        Graphics2D g2 = (Graphics2D)g;
	
	        boxRect = getBox(boxRect);
	        if (boxRect != null)
	        {
	            int width = progressBar.getWidth();
	            int height = progressBar.getHeight();
	            int arcSize = height / 2 - 1;
	            g2.setColor(progressBar.getBackground());
	            g2.fillRoundRect(0, 0, width - 1, height - 1, arcSize, arcSize);
	
	            Color color = progressBar.getForeground();
	            GradientPaint gradient = new GradientPaint(width / 2, 0, new Color(255, 255, 255, 120),
	                    width / 2, height / 4, color, false);
	            g2.setPaint(gradient);
	
	            g2.fillRoundRect(boxRect.x, boxRect.y,
	                    boxRect.width, boxRect.height, 15, 15);
	        }
	
	        // Deal with possible text painting
	        if (progressBar.isStringPainted())
	        {
	            if (progressBar.getOrientation() == JProgressBar.HORIZONTAL)
	            {
	            	paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, b);
	            }
	        }
	    }
	
	    @Override
	    protected void paintDeterminate(Graphics g, JComponent c)
	    {
	        if (progressBar.getOrientation() == JProgressBar.VERTICAL)
	        {
	            super.paintDeterminate(g, c);
	            return;
	        }
	        Insets b = progressBar.getInsets(); // area for border
	        int width = progressBar.getWidth();
	        int height = progressBar.getHeight();
	        int barRectWidth = width - (b.right + b.left);
	        int barRectHeight = height - (b.top + b.bottom);
	        int arcSize = height / 2 - 1;
	        // amount of progress to draw
	        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
	
	        Graphics2D g2 = (Graphics2D) g;
	
	        g2.setColor(progressBar.getBackground());
	        g2.fillRoundRect(0, 0, width - 1, height - 1, arcSize, arcSize);
	
	        // Set the gradient fill
	        Color color = progressBar.getForeground();
	        GradientPaint gradient = new GradientPaint(width / 2, 0, Color.white,
	                width / 2, height / 4, color, false);
	        g2.setPaint(gradient);
	
	        g2.fillRoundRect(b.left, b.top, amountFull - 1, barRectHeight - 1,
	                arcSize, arcSize);
	
	        // Deal with possible text painting
	        if (progressBar.isStringPainted())
	        {
	            paintString(g, b.left, b.top, barRectWidth, barRectHeight,
	                    amountFull, b);
	        }
	    }
	
	    /**
	     * {@inheritDoc}
	     * 
	     */
	    @Override
	    public Dimension getPreferredSize(JComponent c)
	    {
	        Dimension dim = super.getPreferredSize(c);
	        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL)
	        {
	            if (dim.width < dim.height * 4)
	            {
	                dim.width = dim.height * 4;
	            }
	        }
	        return dim;
	    }
	}
}
