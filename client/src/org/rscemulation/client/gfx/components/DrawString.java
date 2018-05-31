package org.rscemulation.client.gfx.components;

import java.awt.Rectangle;

import org.rscemulation.client.gfx.GraphicalComponent;

public class DrawString extends GraphicalComponent {
	private String text;
	private boolean centered;
	private int size;

	public DrawString(String text, boolean centered, Rectangle bounds) {
		this.size = 1;
		this.text = text;
		this.centered = centered;
		this.setBoundarys(bounds);
	}

	@Override
	public void render() {
		if (visible) {
			if (centered) {
				mc.gameGraphics.drawCenteredString(text, getX(), getY() + 10, size,
						hovering ? this.getFillHovering() : this.getFill());
			} else
				mc.gameGraphics.drawString(text, getX(), getY() + 10, size,
						hovering ? this.getFillHovering() : this.getFill());
		}
	}

	public void setColor(int color) {
		this.setFill(color);
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void setSize(int size) {
		this.size = size;
	}

}
