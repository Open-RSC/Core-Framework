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
import java.awt.Font;

import javax.swing.JLabel;

/**
 * A specialized {@link JLabel} that automatically recenters itself when its 
 * text is changed.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public final class AutoCenteringLabel
	extends
		JLabel
{

	private static final long serialVersionUID = -2425854702231513361L;

	/// The x-coordinate of the center of this label
	private final int centerX;
	
	/// The y-coordinate of the center of this label
	private final int centerY;
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void setText(String text)
	{
		super.setText(text);
		super.setSize(super.getPreferredSize());
		super.setLocation(centerX - super.getWidth() / 2, centerY - super.getHeight() / 2);
	}

	/**
	 * Constructs an <code>AutoCenteringLabel</code> with the provided text, 
	 * font, color, and center coordinates
	 * 
	 * @param text the initial text of this label
	 * 
	 * @param font the font of this label
	 * 
	 * @param color the color of this label
	 * 
	 * @param centerX the x-coordinate of the center of this label
	 * 
	 * @param centerY the y-coordinate of the center of this label
	 * 
	 */
	public AutoCenteringLabel(String text, Font font, Color color, int centerX, int centerY)
	{
		super(text);
		super.setFont(font);
		super.setForeground(color);
		super.setSize(super.getPreferredSize());
		super.setLocation(centerX - super.getWidth() / 2, centerY - super.getHeight() / 2);
		this.centerX = centerX;
		this.centerY = centerY;
	}
}
