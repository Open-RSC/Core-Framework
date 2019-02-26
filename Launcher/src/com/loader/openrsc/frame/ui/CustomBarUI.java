package com.loader.openrsc.frame.ui;

import com.loader.openrsc.util.Utils;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.ProgressBarUI;

public class CustomBarUI extends ProgressBarUI {
	private static final int MINIMUM_BORDER_THICKNESS = 0;
	private static final int MAXIMUM_BORDER_THICKNESS = 10;
	private Color backgroundColor;
	private Color borderColor;
	private Color barColor;
	private int borderThickness;

	public CustomBarUI() {
		this.setBarColor(new Color(20, 20, 20));
		this.setBackgroundColor(new Color(50, 50, 50));
		this.setBorderColor(new Color(50, 50, 50));
		this.setBorderThickness();
	}

	private void setBackgroundColor(final Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	private void setBorderColor(final Color borderColor) {
		this.borderColor = borderColor;
	}

	private void setBarColor(final Color barColor) {
		this.barColor = barColor;
	}

	private void setBorderThickness() {
		this.borderThickness = Math.max(0, Math.min(10, 0));
	}

	@Override
	public void paint(final Graphics g, final JComponent component) {
		this.update(g, component);
	}

	@Override
	public void update(final Graphics g, final JComponent component) {
		final int WIDTH = (component.getWidth() - 235);
		final int HEIGHT = component.getHeight();
		g.setColor(this.borderColor);
		g.fillRect(0, 0, WIDTH, this.borderThickness);
		g.fillRect(0, HEIGHT - this.borderThickness, WIDTH, HEIGHT);
		g.fillRect(0, this.borderThickness, this.borderThickness, HEIGHT - 2 * this.borderThickness);
		g.fillRect(WIDTH - this.borderThickness, this.borderThickness, this.borderThickness, HEIGHT - 2 * this.borderThickness);
		final double percentageReady = ((JProgressBar) component).getPercentComplete();
		g.setColor(this.barColor);
		g.fillRect(this.borderThickness, this.borderThickness, WIDTH - 2 * this.borderThickness, HEIGHT - 2 * this.borderThickness);
		if (percentageReady < 1.0) {
			g.setColor(this.backgroundColor);
			g.fillRect(1, this.borderThickness, (int) (WIDTH * percentageReady), HEIGHT - 2 * this.borderThickness);
		}
		g.setFont(Utils.getFont("Exo-Regular.otf", 0, 10.0f));
		g.setColor(new Color(175, 175, 175));
		g.setXORMode(this.barColor);
		final FontMetrics fm = g.getFontMetrics(g.getFont());
		if (percentageReady == 0.0) {
			final String str = "";
			final int stringWidth = fm.stringWidth(str);
			g.drawString(str, WIDTH / 2 - stringWidth / 2, 15);
		} else {
			final String str = "Updating: " + (int) (percentageReady * 100.0) + "%";
			final int stringWidth = fm.stringWidth(str);
			g.drawString(str, WIDTH / 2 - stringWidth / 2, 11);
		}
	}
}
