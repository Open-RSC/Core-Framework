package org.rscemulation.client.gfx.components;

import java.awt.Rectangle;

import org.rscemulation.client.gfx.GraphicalComponent;
import org.rscemulation.client.gfx.action.Action;
import org.rscemulation.client.gfx.action.ScrollListener;

public class ScrollBar extends GraphicalComponent implements ScrollListener {

	private int size = 49;

	private ScrollListener scroll;

	private int menuListTextCount = 500;

	private int firstIndex = 0;

	public ScrollBar(Rectangle rect) {
		this.setBoundarys(rect);
		this.setAction(extracted());
	}

	public void addScrollListener(ScrollListener scroll) {
		this.scroll = scroll;
		addScrollable(this);
	}

	protected void drawSlider(int x, int y, int w, int h, int sliderY,
			int sliderHeight) {
		int newX = x + w - 12;

		mc.gameGraphics.drawBoxEdge(newX, y, 12, h, 0);
		mc.gameGraphics.drawPicture(newX + 1, y + 1, 2100);
		mc.gameGraphics.drawPicture(newX + 1, y + h - 12, 2101);
		mc.gameGraphics.drawLineX(newX, y + 13, 12, 0);
		mc.gameGraphics.drawLineX(newX, y + h - 13, 12, 0);
		mc.gameGraphics
				.drawGradient(newX + 1, y + 14, 11, h - 27,
						this.convertToJag(114, 114, 176),
						this.convertToJag(14, 14, 62));

		mc.gameGraphics.drawBox(newX + 3, sliderY + y + 14, 7, sliderHeight,
				this.convertToJag(96, 129, 184));
		mc.gameGraphics.drawLineY(newX + 2, sliderY + y + 14, sliderHeight,
				this.convertToJag(200, 208, 232));
		mc.gameGraphics.drawLineY(newX + 2 + 8, sliderY + y + 14, sliderHeight,
				this.convertToJag(53, 95, 115));
	}

	private Action extracted() {
		return new Action() {

			@Override
			public void action(int x, int y, int button) {
				int visibleTextLines = getHeight() / getSize();
				if (visibleTextLines < menuListTextCount) {
					if (getIndex() + visibleTextLines > menuListTextCount) {
						setFirstIndex(getIndex() - 1);
					}
					int startX = getX();
					int startY = getY();
					int sliderX = startX + getWidth() - 12;
					int sliderHeight = (getHeight() - 27) * visibleTextLines
							/ menuListTextCount;
					if (sliderHeight < 6)
						sliderHeight = 6;
					/*
					 * int sliderY = (getHeight() - 27 - sliderHeight)
					 * getIndex() / (menuListTextCount - visibleTextLines);
					 */
					if ((button == 1) && (x >= sliderX) && (y <= sliderX + 12)) {
						if ((y > startY) && (y < startY + 12)
								&& (getIndex() > 0)) {
							setFirstIndex(getIndex() - 1);
							if (scroll != null) {
								scroll.onScrollUpdate(getIndex());
							}
						}
						if ((y > startY + getHeight() - 12)
								&& (y < startY + getHeight())
								&& (getIndex() < menuListTextCount
										- visibleTextLines)) {
							setFirstIndex(getIndex() + 1);
							if (scroll != null) {
								scroll.onScrollUpdate(getIndex());
							}
						}
					}

					if ((button == 1)
							&& (((x >= sliderX) && (x <= sliderX + 12)) || ((x >= sliderX - 12) && (x <= sliderX + 24)))) {
						if ((y > startY + 12)
								&& (y < startY + getHeight() - 12)) {
							int l3 = y - startY - 12 - sliderHeight / 2;
							setFirstIndex(l3 * menuListTextCount
									/ (getHeight() - 24));
							if (getIndex() < 0)
								setFirstIndex(0);
							if (getIndex() > menuListTextCount
									- visibleTextLines)
								setFirstIndex(menuListTextCount
										- visibleTextLines);
							if (scroll != null) {
								scroll.onScrollUpdate(getIndex());
							}
						}
					}
				}
			}

		};
	}

	public int getIndex() {
		return firstIndex;
	}

	public ScrollListener getScrollListener() {
		return scroll;
	}

	public int getSize() {
		return size;
	}

	@Override
	public void onScrollUpdate(int index) {

	}

	@Override
	public void render() {
		if(!visible)
			return;
		int visibleTextLines = getHeight() / getSize();
		if (visibleTextLines < menuListTextCount) {
			int sliderHeight = (getHeight() - 27) * visibleTextLines
					/ menuListTextCount;
			if (sliderHeight < 6)
				sliderHeight = 6;
			int sliderY = (getHeight() - 27 - sliderHeight) * getIndex()
					/ (menuListTextCount - visibleTextLines);

			drawSlider(getX(), getY(), getWidth(), getHeight(), sliderY,
					sliderHeight);
		}
	}

	@Override
	public void scrolling(int type) {
		if (type == 1) {
			setFirstIndex(getIndex() - 1);
		} else {
			setFirstIndex(getIndex() + 1);
		}
		if (getScrollListener() != null) {
			getScrollListener().scrolling(type);
			getScrollListener().onScrollUpdate(firstIndex);
		}
	}

	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setMenuListTextCount(int i) {
		menuListTextCount = i;
	}
	
	public int getMenuListTextCount() {
		return menuListTextCount;
	}
}
