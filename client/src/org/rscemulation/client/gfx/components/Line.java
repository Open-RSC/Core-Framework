package org.rscemulation.client.gfx.components;

import org.rscemulation.client.gfx.GraphicalComponent;

public class Line extends GraphicalComponent {

	private int x, y, endx = 0;

	public Line(int x, int y, int endx) {
		this.x = x;
		this.y = y;
		this.endx = endx;
	}

	@Override
	public void render() {
		if (!visible)
			return;
		mc.gameGraphics.drawLineX(x, y, endx, this.convertToJag(0, 0, 0));
	}
}