package org.rscemulation.client.gfx;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.rscemulation.client.mudclient;
import org.rscemulation.client.gfx.action.Action;
import org.rscemulation.client.gfx.action.DragListener;
import org.rscemulation.client.gfx.action.Hovering;
import org.rscemulation.client.gfx.action.KeyListener;
import org.rscemulation.client.gfx.action.ScrollListener;
import org.rscemulation.client.gfx.components.ScrollBar;

public abstract class GraphicalComponent {

	private int boarder = 0;

	private int fill = 0x989898;

	private int opaque = 160;

	private int fill_hovering = convertToJag(255, 0, 0);

	private int boarder_moving = convertToJag(102, 0, 204);

	private Rectangle bounds;

	public Action action;

	public Hovering hoveringCallback;

	public mudclient<?> mc;

	public boolean hovering;

	public boolean visible = true;

	private GraphicalComponent parent;

	private ScrollListener frameScroll = null;

	private KeyListener keyListener = null;

	private DragListener dragListener = null;

	private List<GraphicalComponent> components = new ArrayList<GraphicalComponent>();

	private boolean dragging;

	private int offsetX = mudclient.getInstance().getGameWidth() - 512,
			offsetY = mudclient.getInstance().getGameHeight() - 334;

	public void add(GraphicalComponent component) {
		component.setParent(this);
		component.mc = mc;
		getComponents().add(component);
	}

	public void addKeyListener(KeyListener keyListener) {
		this.keyListener = keyListener;
	}

	public void addScrollable(ScrollBar scrollBar) {
		if (getParent() != null) {
			getParent().addScrollable(scrollBar);
		} else {
			setFrameScroll(scrollBar);
		}

	}

	public int convertToJag(int red, int green, int blue) {
		return (red << 16) + (green << 8) + blue;
	}

	public boolean dragStop() {
		if (dragListener != null) {
			dragListener.stopDragging();
		}
		setDragging(false);
		return true;
	}

	public int getBoarder() {
		return boarder;
	}

	public int getBoarderMoving() {
		return boarder_moving;
	}

	public Rectangle getBoundarys() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	public List<GraphicalComponent> getComponents() {
		return components;
	}

	public DragListener getDragListener() {
		return dragListener;
	}

	public int getFill() {
		return fill;
	}

	public int getFillHovering() {
		return fill_hovering;
	}

	public ScrollListener getFrameScroll() {
		return frameScroll;
	}

	public int getHeight() {
		return bounds.height;
	}

	public KeyListener getKeyListener() {
		return keyListener;
	}

	public int getOpaque() {
		return opaque;
	}

	public GraphicalComponent getParent() {
		return parent;
	}

	public int getWidth() {
		return bounds.width;
	}

	public int getX() {
		if (parent == null)
			return bounds.x + (offsetX / 2);
		return parent.getX() + bounds.x;
	}

	public int getY() {
		if (parent == null)
			return bounds.y + (offsetY / 2);
		return parent.getY() + bounds.y;
	}

	public boolean isDragging() {
		return dragging;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean onDrag(int x, int y) {
		if (dragListener != null && getBoundarys().contains(new Point(x, y))
				|| dragListener != null && isDragging()) {
			setDragging(true);
			Rectangle b = bounds;
			if ((x - offsetX) > 0 && (y - offsetY) > 0
					&& (x - offsetX) < mc.getGameHeight()
					&& (y - offsetY) < mc.getGameWidth()) {
				b.x = x - offsetX;
				b.y = y - offsetX;
				dragListener.onDragging(b.x, b.y);
			}
			return true;
		}
		return false;
	}

	public boolean onKey(char keyChar, int key) {
		for (GraphicalComponent gc : getComponents()) {
			if (gc.onKey(keyChar, key))
				return true;
		}
		if (keyListener != null) {
			return keyListener.onKey(keyChar, key);
		}
		return false;
	}

	public void onRender() {
		try {
			render();
		} catch(Exception e) { /* o well */ }
		try {
			for (GraphicalComponent c : getComponents()) {
				if (visible)
					c.onRender();
			}
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}

	public List<GraphicalComponent> getAllChildComponents() {
		List<GraphicalComponent> comps = new ArrayList<GraphicalComponent>();
		for (GraphicalComponent c : getComponents()) {
			if (visible) {
				comps.add(c);
				comps.addAll(c.getAllChildComponents());
			}
		}
		return comps;
	}

	public void onResize(int widths, int heights) {
		for (GraphicalComponent c : getComponents()) {
			c.onResize(widths, heights);
		}
		offsetX = widths - 512;
		offsetY = heights - 334;
		if (offsetY < 0)
			offsetY = 0;
		if (offsetX < 0)
			offsetX = 0;
	}

	public abstract void render();

	public void setAction(Action action) {
		this.action = action;
	}

	public void setBoarder(int boarder) {
		this.boarder = boarder;
	}

	public void setBoarderMoving(int boarder_moving) {
		this.boarder_moving = boarder_moving;
	}

	public void setBoundarys(Rectangle bounds) {
		this.bounds = bounds;
	}

	public void setComponents(List<GraphicalComponent> components) {
		this.components = components;
	}

	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public void setDragListener(DragListener dragListener) {
		this.dragListener = dragListener;
	}

	public void setFill(int fill) {
		this.fill = fill;
	}

	public void setFillHovering(int fill_hovering) {
		this.fill_hovering = fill_hovering;
	}

	public void setFrameScroll(ScrollListener frameScroll) {
		this.frameScroll = frameScroll;
	}

	public void setHovering(Hovering hoveringCallback) {
		this.hoveringCallback = hoveringCallback;
	}

	public void setOpaque(int opaque) {
		this.opaque = opaque;
	}

	public void setParent(GraphicalComponent parent) {
		this.parent = parent;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}