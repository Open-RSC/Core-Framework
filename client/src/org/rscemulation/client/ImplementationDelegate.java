package org.rscemulation.client;

import java.awt.Container;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * An ImplementationDelegate encapsulates the AWT / Swing / etc container 
 * that:
 * 
 * A) Delegates user events to the game
 * B) Displays the game
 * 
 * The two stock implementations include 
 * {@linkplain org.rscemulation.client.AppletLoader} and
 * {@linkplain org.rscemulation.client.JFrameDelegate}, but 
 * there are many more possibilities for back-ends.
 * 
 * @author Freelancer
 *
 */
public interface ImplementationDelegate
	extends
		MouseListener,
		MouseMotionListener,
		MouseWheelListener,
		KeyListener
{
	/**
	 * Retrieves the underlying container
	 * 
	 * @return the underlying container
	 * 
	 */
	Container getContainerImpl();
	
	void onLogin();
	void onLogout();

}
