package org.rscemulation.client.gfx.components;

import java.awt.Rectangle;

import org.rscemulation.client.gfx.GraphicalComponent;

public class Box extends GraphicalComponent {

	private boolean centered = true;

	private String text;

	private int textColor;

	private int fontSize = 1;

	public boolean selected = false;

	public Box(Rectangle bounds) {
		setBoundarys(bounds);
	}

	public int getFontSize() {
		return fontSize;
	}

	public int getTextColor() {
		return textColor;
	}

	@Override
	public void render() {
		if (!visible)
			return;

		mc.gameGraphics
				.drawBoxAlpha(getX(), getY(), getWidth(), getHeight(),
						hovering ? this.getFillHovering() : getFill(),
						this.getOpaque());
		mc.gameGraphics.drawBoxEdge(getX() - 1, getY() - 1, getWidth() + 1,
				getHeight() + 1, this.getBoarder());
		if (text != null) {
			if (centered)
				mc.gameGraphics.drawCenteredString(text, getX() + (getWidth() / 2),
						getY() + (getHeight() / 2) + 4, getFontSize(), textColor);
			else
				mc.gameGraphics.drawString(text, getX() + 3, getY() + (getHeight() / 2) + 4, getFontSize(),
						this.getTextColor());
		}
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public void setText(String text, int color) {
		this.text = text;
		this.setTextColor(color);
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public boolean isCentered() {
		return centered;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
	}
}