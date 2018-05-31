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
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * A convenience class for the 'Play' button
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public final class TransparencyEnabledButton
	extends
		JButton
{

	private static final long serialVersionUID = -6099541202153519724L;
	
	/**
	 * Constructs a <code>TransparencyEnabledButton</code> with the provided 
	 * caption, location, font, text color, and images
	 * 
	 * @param caption the initial caption of this button
	 * 
	 * @param x the x-position of the center of this button
	 * 
	 * @param y the y-position of the center of this button
	 * 
	 * @param font the font of this button
	 * 
	 * @param textColor the color of the text of this button
	 * 
	 * @param defaultImage the default image of this button
	 * 
	 * @param rolloverImage the rollover image of this button
	 * 
	 */
	public TransparencyEnabledButton(String caption, 
							  int x,
							  int y,
							  Font font, 
							  Color textColor,
							  Image defaultImage, 
							  Image rolloverImage)
	{
		super(caption);
		super.setForeground(textColor);
		super.setFont(font);
		super.setSize(super.getPreferredSize());
		super.setHorizontalTextPosition(JButton.CENTER);
		super.setVerticalTextPosition(JButton.CENTER);
		super.setLocation(x - super.getWidth() / 2, 
				y - super.getHeight() / 2);
		super.setBorderPainted(false);
		super.setOpaque(false);
		super.setContentAreaFilled(false);
		super.setRolloverIcon(new ImageIcon(
				rolloverImage.getScaledInstance(super.getWidth(), 
				  super.getHeight(), 
				  Image.SCALE_SMOOTH)));
		super.setIcon(new ImageIcon(
				defaultImage.getScaledInstance(super.getWidth(), 
				  super.getHeight(), 
				  Image.SCALE_SMOOTH)));
	}
}
