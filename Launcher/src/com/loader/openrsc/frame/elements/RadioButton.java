package com.loader.openrsc.frame.elements;

import com.loader.openrsc.frame.listeners.ButtonListener;
import com.loader.openrsc.util.Utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JRadioButton;

public class RadioButton extends JRadioButton implements MouseListener {
	public RadioButton(final String text, final Rectangle bounds) {
		super(text);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setForeground(Color.WHITE);
		this.setIcon(Utils.getImage("toggle-off.png"));
		this.setSelectedIcon(Utils.getImage("toggle-on.png"));
		this.setFont(Utils.getFont("Exo-Regular.otf", 0, 14.0f));
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
