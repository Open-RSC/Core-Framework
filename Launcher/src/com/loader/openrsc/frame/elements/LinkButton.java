package com.loader.openrsc.frame.elements;

import com.loader.openrsc.frame.listeners.ButtonListener;
import com.loader.openrsc.util.Utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class LinkButton extends JButton implements MouseListener {
	private static final long serialVersionUID = 3503904237989217533L;

	public LinkButton(final String text, final Rectangle bounds) {
		super(text.toUpperCase());
		this.setIcon(Utils.getImage("button.png"));
		this.setRolloverIcon(Utils.getImage("button_hover.png"));
		this.setHorizontalTextPosition(0);
		this.setFont(Utils.getFont("Exo-Regular.otf", 1, 12.0f));
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
