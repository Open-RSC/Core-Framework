package launcher.elements;

import launcher.listeners.ButtonListener;
import launcher.Utils.Utils;

import javax.swing.*;

public class ControlButton extends JButton {
	public ControlButton(final int buttonType, final int x, final int y, final int width, final int height) {
		if (buttonType == 1) {
			final String image = "minimize";
			this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
			this.setIcon(Utils.getImage(image + ".png"));
			this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
			this.setActionCommand(image);
			this.addActionListener(new ButtonListener());
		}
		if (buttonType == 2) {
			final String image = "close";
			this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
			this.setIcon(Utils.getImage(image + ".png"));
			this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
			this.setActionCommand(image);
			this.addActionListener(new ButtonListener());
		}
		if (buttonType == 3) {
			final String image = "delete";
			this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
			this.setIcon(Utils.getImage(image + ".png"));
			this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
			this.setActionCommand("delete");
			this.addActionListener(new ButtonListener());
		}
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.setHorizontalTextPosition(0);
		this.setBounds(x, y, width, height);
	}
}
