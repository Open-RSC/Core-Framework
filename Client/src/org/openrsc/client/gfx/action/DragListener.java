package org.openrsc.client.gfx.action;

public interface DragListener {

	public boolean onDragging(int startX, int startY);

	public void stopDragging();

}
