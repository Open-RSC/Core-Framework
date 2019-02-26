package com.loader.openrsc.frame.elements;

import com.loader.openrsc.frame.listeners.ButtonListener;
import com.loader.openrsc.util.Utils;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import static java.awt.Cursor.DEFAULT_CURSOR;
import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;

public class LaunchButton extends JButton implements MouseListener {
	private static final long serialVersionUID = -3245141651685683983L;

	public LaunchButton() {
		super("");
		this.setIcon(Utils.getImage("launch.png"));
		this.setRolloverIcon(Utils.getImage("launch_hover.png"));
		this.setHorizontalTextPosition(0);
		this.setFont(Utils.getFont("Exo-Regular.otf", 1, 20.0f));
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.setForeground(Color.WHITE);
		this.setActionCommand("launch");
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
