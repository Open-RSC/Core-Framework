package launcher.elements;

import launcher.listeners.ButtonListener;
import launcher.Utils.Utils;

import javax.swing.*;

public class ControlButton extends JButton {
	public static final int MINIMIZE = 1;
	public static final int CLOSE = 2;
	public static final int DELETE_CACHE = 3;

	public ControlButton(final int buttonType, final int x, final int y, final int width, final int height) {
		switch (buttonType) {
			case MINIMIZE: {
				final String image = "minimize";
				this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
				this.setIcon(Utils.getImage(image + ".png"));
				this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
				this.setActionCommand(image);
				this.addActionListener(new ButtonListener());
				break;
			}
			case CLOSE: {
				final String image = "close";
				this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
				this.setIcon(Utils.getImage(image + ".png"));
				this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
				this.setActionCommand(image);
				this.setFocusable(false);
				this.addActionListener(new ButtonListener());
				break;
			}
			case DELETE_CACHE: {
				final String image = "delete";
				this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
				this.setIcon(Utils.getImage(image + ".png"));
				this.setRolloverIcon(Utils.getImage(image + "_hover.png"));
				this.setActionCommand("delete");
				this.setFocusable(false);
				this.addActionListener(new ButtonListener());
				break;
			}
		}
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.setHorizontalTextPosition(0);
		this.setBounds(x, y, width, height);
	}
}
