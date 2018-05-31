package org.rscemulation.client.gfx.components;

import java.awt.Rectangle;

import org.rscemulation.client.gfx.GraphicalComponent;

public class DrawSprite extends GraphicalComponent {
	private int spriteIndex;
	private int overlay;
	private int scaleX;
	private int scaleY;

	public DrawSprite(int spriteIndex, Rectangle bounds, int scaleX, int scaleY, int overlay) {
		this.setSpriteIndex(spriteIndex);
		this.setScaleX(scaleX);
		this.setScaleY(scaleY);
		this.setOverlay(overlay);
		this.setBoundarys(bounds);
	}

	@Override
	public void render() {
		if(!visible)
			return;
		/* TODO: This might be wrong clip method */
		mc.gameGraphics.spriteClip4(getX(), getY(), scaleX, scaleY, spriteIndex, overlay, 0, 0, false);
	}

	public int getScaleY() {
		return scaleY;
	}

	public void setScaleY(int scaleY) {
		this.scaleY = scaleY;
	}

	public int getScaleX() {
		return scaleX;
	}

	public void setScaleX(int scaleX) {
		this.scaleX = scaleX;
	}

	public int getOverlay() {
		return overlay;
	}

	public void setOverlay(int overlay) {
		this.overlay = overlay;
	}

	public int getSpriteIndex() {
		return spriteIndex;
	}

	public void setSpriteIndex(int spriteIndex) {
		this.spriteIndex = spriteIndex;
	}

}
