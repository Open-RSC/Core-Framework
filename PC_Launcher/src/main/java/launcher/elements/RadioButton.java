package launcher.elements;

import launcher.listeners.ButtonListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RadioButton extends JRadioButton implements MouseListener {
	public RadioButton(final Rectangle bounds) {
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setForeground(Color.WHITE);
		this.addMouseListener(this);
		this.setFocusable(false);
		this.setBounds(bounds);
		this.addActionListener(new ButtonListener());
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.setForeground(this.getForeground().darker());
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.setForeground(this.getForeground().brighter());
	}

	@Override
	public void mousePressed(final MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
	}
}
