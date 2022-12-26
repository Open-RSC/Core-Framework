package launcher.listeners;

import launcher.Fancy.MainWindow;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class PositionListener implements MouseListener, MouseMotionListener {
  private static final int TOP_BAR_HEIGHT = 70; // The height you can click to move the window
	private static Point initialTopLeft;
  private static Point initialClick;
	private MainWindow frame;

	public PositionListener(final MainWindow frame) {
		this.frame = frame;
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
    // Get the initial position of the window from the top left corner
    initialTopLeft = this.frame.getLocationOnScreen();
    initialClick = new Point(e.getLocationOnScreen());
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
    if (initialClick.y > initialTopLeft.y + TOP_BAR_HEIGHT) {
      return;
    }
    // Keep the window in the same position relative to the mouse
    Point mouseOffset = new Point(e.getLocationOnScreen());
    mouseOffset.translate(-initialClick.x, -initialClick.y);
    this.frame.setLocation(initialTopLeft.x + mouseOffset.x, initialTopLeft.y + mouseOffset.y);
	}

	@Override
	public void mouseMoved(final MouseEvent arg0) {
	}
}
