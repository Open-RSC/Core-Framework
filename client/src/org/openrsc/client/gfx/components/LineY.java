package org.openrsc.client.gfx.components;

import org.openrsc.client.gfx.GraphicalComponent;

public class LineY extends GraphicalComponent {

	private int x, y, endx = 0;

	public LineY(int x, int y, int endx) {
		this.x = x;
		this.y = y;
		this.endx = endx;
	}

	@Override
	public void render() {
		if (!visible)
			return;
		mc.gameGraphics.drawLineY(x, y, endx, this.convertToJag(0, 0, 0));
	}
}