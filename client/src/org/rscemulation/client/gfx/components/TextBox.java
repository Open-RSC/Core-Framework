package org.rscemulation.client.gfx.components;

import java.awt.Rectangle;

import org.rscemulation.client.gfx.GraphicalComponent;

public class TextBox extends GraphicalComponent {

	private String text = "";

	private int textColor = 50000;

	private int fontSize = 1;

	private boolean selected = false;

	public TextBox(Rectangle bounds) {
		setBoundarys(bounds);

	}

	public void append(String text) {
		if(!selected)
			return;
		if (text.equals("DELETE") && (this.text.length() - 2) >= 0) {
			String test = this.text.substring(0, this.text.length() - 2);
			test += "*";
			this.text = test;

		} else if (!text.equals("DELETE")) {
			String test = this.text.substring(0, this.text.length() - 1);
			test += text + "*";
			this.text = test;
		}
	}

	public int getFontSize() {
		return fontSize;
	}

	public String getText() {
		return text;
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
			if (text.length() < 50)
				mc.gameGraphics.drawString(text, getX() + 3, getY() + (getHeight() / 2) + 5,
						getFontSize(), this.getTextColor());
		}
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public void setText(String text) {
		this.text = text + "*";
	}
	
	public void setTextUnchangable(String text) {
		this.text = text;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean flag) {
		if(flag)
			text = "*";
		else
			text = text.replaceAll("\\*", "");
		this.selected = flag;
	}
}