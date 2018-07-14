

package org.openrsc.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * An <code>ImplementationDelegate</code> that is backed by a 
 * <code>JFrame</code>
 * 
 * @author Freelancer
 *
 */
public class JFrameDelegate
	extends
		JFrame
	implements
		ComponentListener,
		WindowListener,
		ImplementationDelegate
{

	private static final long serialVersionUID = 1230839399346384755L;
	
	static
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/// The game to pass events to
	private final openrsc<?> instance;
	
	private final int defaultWidth, defaultHeight;
	/**
	 * Constructs a <code>JFrameDelegate</code>.  Note that the actual 
	 * size of the container will be augmented to cater to the game area 
	 * that is defined by the provided width and height.
	 * 
	 * @param width the width of the game area
	 * 
	 * @param height the height of the game area
	 * 
	 */
	public JFrameDelegate(int width, int height)
	{
		super("RuneScape");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setResizable(false);
		super.setBackground(Color.BLACK); // hack;
		super.setVisible(true);
		Insets insets = super.getInsets();
		this.defaultWidth = width;
		this.defaultHeight = height;
		super.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
		super.setPreferredSize(new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom));
		super.pack();
		this.instance = new mudclient<JFrameDelegate>(this, width, height);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		addComponentListener(this);
		addWindowListener(this);
		instance.run();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void keyPressed(KeyEvent e)
	{
		instance.keyDown(e.isShiftDown(), e.isControlDown(), e.isActionKey(), e.getKeyCode(), e.getKeyChar(), e);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void keyReleased(KeyEvent e)
	{
		instance.keyUp(e.getKeyCode());
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void keyTyped(KeyEvent e) { /* Intentionally Empty */ }

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void mouseMoved(MouseEvent e)
	{
		instance.mouseMove(e.getX() - super.getInsets().left, e.getY() - super.getInsets().top);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void mouseDragged(MouseEvent e)
	{
		instance.mouseDrag(e.isMetaDown(), e.getX() - super.getInsets().left, e.getY() - super.getInsets().top);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void mouseExited(MouseEvent e)
	{
		mouseMoved(e);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void mouseEntered(MouseEvent e)
	{
		mouseMoved(e);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void mousePressed(MouseEvent e)
	{
		instance.mouseDown(SwingUtilities.isRightMouseButton(e), e.getX() - super.getInsets().left, e.getY() - super.getInsets().top);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void mouseReleased(MouseEvent e)
	{
		instance.mouseUp(e.getButton(), e.getX() - super.getInsets().left, e.getY() - super.getInsets().top);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void mouseClicked(MouseEvent e) { /* Intentionally Empty */ }
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void mouseWheelMoved(MouseWheelEvent e)
	{
		instance.onMouseWheelMoved(e.getWheelRotation());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Container getContainerImpl()
	{
		return this;
	}
		
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Graphics getGraphics()
	{
		Graphics gfx = super.getGraphics();
		gfx.translate(super.getInsets().left, super.getInsets().top);
		return gfx;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void paint(Graphics gfx)
	{
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void update(Graphics gfx)
	{
		paint(gfx);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void componentResized(ComponentEvent e)
	{
		Insets insets = super.getInsets();
		instance.onResize(super.getWidth() - insets.left - insets.right, super.getHeight() - insets.top - insets.bottom - 11);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void componentMoved(ComponentEvent e) { /* Intentionally Empty */ }

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void componentShown(ComponentEvent e) { /* Intentionally Empty */ }

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void componentHidden(ComponentEvent e) { /* Intentionally Empty */ }

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void windowOpened(WindowEvent e) { /* Intentionally Empty */ }

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void windowClosing(WindowEvent e)
	{
		instance.destroy();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void windowClosed(WindowEvent e) { /* Intentionally Empty */ }

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void windowIconified(WindowEvent e)
	{
		instance.onPaused();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void windowDeiconified(WindowEvent e)
	{
		instance.onRestored();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void windowActivated(WindowEvent e) { /* Intentionally Empty */ }

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void windowDeactivated(WindowEvent e) { /* Intentionally Empty */ }

	@Override
	public void onLogin() {
		/*if(this.instance.isSubscriber())
		{
			super.setResizable(true);
			super.setSize(super.getSize().width, super.getSize().height + 15);
		}
		else
		{
			// hack maybe?
			super.setSize(super.getSize().width, super.getSize().height + 15);
		}*/
        super.setResizable(true);
        super.setSize(super.getSize().width, super.getSize().height + 15);
		componentResized(null);
	}

	@Override
	public void onLogout() {
		Insets insets = super.getInsets();
		super.setSize(defaultWidth + insets.left + insets.right, defaultHeight + insets.top + insets.bottom + 11);
		super.setResizable(false);
	}

}