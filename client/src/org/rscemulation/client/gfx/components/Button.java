package org.rscemulation.client.gfx.components;

import java.awt.Rectangle;

import org.rscemulation.client.gfx.GraphicalComponent;

public class Button extends GraphicalComponent {

	private String text;

	public boolean selected = false;
	
	private int textColor = convertToJag(48, 244, 255);

	public Button(Rectangle bounds) {
		setBoundarys(bounds);
	}

	@Override
	public void render() {
		if (!visible)
			return;
		mc.gameGraphics.drawBox(getX(), getY(), getWidth() + 1, getHeight() + 1,
				hovering ? this.getFillHovering() : this.getBoarder());

		mc.gameGraphics.drawBoxAlpha(getX() + 1, getY() + 1, getWidth() - 1,
				getHeight() - 1, this.getFill(), this.getOpaque());

		if (text != null) {
			mc.gameGraphics.drawCenteredString(text, getX() + (getWidth() / 2), getY() + getHeight() - 5,
					3, textColor);
		}

	}
	
	public void setTextColor(int color) {
		this.textColor = color;
	}
	
	public int getTextColor() {
		return this.textColor;
	}

	public void setText(String text) {
		this.text = text;
	}
}