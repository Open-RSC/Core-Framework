package launcher.elements;

import launcher.listeners.ButtonListener;
import launcher.Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LinkText extends JButton implements MouseListener {
	private static final long serialVersionUID = 3503904237989217533L;

	public LinkText(final String text, final Rectangle bounds) {
		super(text.toUpperCase());
		this.setHorizontalTextPosition(0);
		this.setFont(Utils.getFont("Helvetica.otf", 1, 12.0f));
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.setForeground(Color.WHITE);
		this.setFocusable(false);
		this.addActionListener(new ButtonListener());
		this.setBounds(bounds);
		this.addMouseListener(this);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}
}
