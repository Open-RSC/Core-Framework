package launcher.elements;

import launcher.listeners.ButtonListener;
import launcher.Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.awt.Cursor.*;

public class LaunchButton extends JButton implements MouseListener {
	private static final long serialVersionUID = -3245141651685683983L;

	public LaunchButton(String game) {
		this.setIcon(Utils.getImage(game + "_logo.png"));
		this.setRolloverIcon(Utils.getImage(game + "_logo_hover.png"));
		this.setHorizontalTextPosition(0);
		this.setFont(Utils.getFont("Helvetica.otf", 1, 18.0f));
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.setForeground(Color.WHITE);
		this.setActionCommand(game);
		this.addActionListener(new ButtonListener());
		this.addMouseListener(this);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		this.setCursor(getPredefinedCursor(HAND_CURSOR));
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		this.setCursor(getPredefinedCursor(DEFAULT_CURSOR));
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}
}
