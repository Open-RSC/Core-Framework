package com.loader.openrsc.frame.elements;

import com.loader.openrsc.frame.listeners.ButtonListener;
import com.loader.openrsc.util.Utils;

import javax.swing.*;

public class ControlButton extends JButton {
	public ControlButton(final int buttonType, final int x, final int y, final int width, final int height) {
		final String image = (buttonType == 1) ? "minimize" : "close";
		if (buttonType != 3) {
			this.setIcon(Utils.getImage(image + ".png"));
			this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
		}
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.setHorizontalTextPosition(0);
		this.setActionCommand(image);
		if (buttonType != 3) {
			this.addActionListener(new ButtonListener());
		}
		this.setBounds(x, y, width, height);
	}
}
