package org.rscemulation.client.gfx.components;

import java.awt.Rectangle;

import org.rscemulation.client.gfx.GraphicalComponent;

public class GameFrame extends GraphicalComponent {
	private boolean centered = true;
	private String title;

	public GameFrame(String title, Rectangle bounds) {
		this.title = title;
		this.setBoundarys(bounds);
	}

	@Override
	public void render() {
		if (!visible)
			return;
		mc.gameGraphics.drawBox(getX(), getY(), getWidth(), 16, 190);
		mc.gameGraphics.drawBoxAlpha(getX(), getY() + 16, getWidth(),
				getHeight(), this.getFill(), this.getOpaque());

		if (centered)
			mc.gameGraphics.drawCenteredString(title, getX() + (getWidth() / 2),
					getY() + 12, 1, 0xffffff);
		else
			mc.gameGraphics.drawString(title, getX() + 1, getY() + 12, 1,
					0xffffff);
	}

	public boolean isCentered() {
		return centered;
	}

	public void setTextCentered(boolean centered) {
		this.centered = centered;
	}

}
