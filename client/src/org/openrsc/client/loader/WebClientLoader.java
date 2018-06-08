package org.openrsc.client.loader;

import java.applet.Applet;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;

import org.openrsc.client.mudclient;
import org.openrsc.client.loader.various.AppletUtils;
import org.openrsc.client.loader.various.ProgressCallback;
import org.openrsc.client.loader.various.VirtualBrowser;
import org.openrsc.client.ImplementationDelegate;

public class WebClientLoader extends Applet implements Runnable, ImplementationDelegate, ComponentListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Thread applet_thread = null;

	private mudclient<WebClientLoader> instance;

	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public final void componentResized(ComponentEvent e)
	{
		Insets insets = super.getInsets();
		instance.onResize(super.getWidth() - insets.left - insets.right, super.getHeight() - insets.top - insets.bottom - 11);
	}

	@Override
	public void componentShown(ComponentEvent arg0) {}

	public static void downloadCache() {
		AppletUtils.DISPLAY_MESSAGE = "Checking dictionarys";
		if (AppletUtils.doDirChecks())
			try {
				AppletUtils.DISPLAY_MESSAGE = "Deleting old cache";
				for (final File file : AppletUtils.CACHE.listFiles())
					file.delete();
				AppletUtils.DISPLAY_MESSAGE = "Downloading cache ";
				new VirtualBrowser().getRaw(new URL("http://openrsc.com/play/openrsc.zip"), new ProgressCallback() {
					@Override
					public void onComplete(byte[] bytes) {
						try {
							final FileOutputStream fos = new FileOutputStream(AppletUtils.CACHEFILE.getPath());
							fos.write(bytes);
							fos.close();
						} catch (final IOException e) {
							AppletUtils.percentage = 0;
							AppletUtils.DISPLAY_MESSAGE = "Failed to save cache";
						}
						AppletUtils.DISPLAY_MESSAGE = "Cache downloaded...";
						AppletUtils.extractFolder(AppletUtils.CACHEFILE.getPath(), AppletUtils.CACHE.toString());
					}

					@Override
					public void update(int pos, int length) {
						AppletUtils.percentage = pos * 100 / length;
					}
				});
			} catch (final MalformedURLException e) {
				AppletUtils.percentage = 0;
				AppletUtils.DISPLAY_MESSAGE = "Failed to grab cache";
			}
		else
			AppletUtils.DISPLAY_MESSAGE = "Dictionarys can not be created";
	}

	@Override
	public final Container getContainerImpl() {
		return this;
	}

	@Override
	public void init() {
		AppletUtils.isApplet = true;
		applet_thread = new Thread(this);
		applet_thread.start();
	}

	@Override
	public final void keyPressed(KeyEvent e) {
		instance.keyDown(e.isShiftDown(), e.isControlDown(), e.isActionKey(), e.getKeyCode(), e.getKeyChar(), e);
	}

	@Override
	public final void keyReleased(KeyEvent e) {
		instance.keyUp(e.getKeyCode());
	}

	@Override
	public final void keyTyped(KeyEvent e) {}

	@Override
	public final void mouseClicked(MouseEvent e) {}

	@Override
	public final void mouseDragged(MouseEvent e) {
		instance.mouseDrag(e.isMetaDown(), e.getX() - super.getInsets().left, e.getY() - super.getInsets().top);
	}

	@Override
	public final void mouseEntered(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public final void mouseExited(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public final void mouseMoved(MouseEvent e) {
		instance.mouseMove(e.getX() - super.getInsets().left, e.getY() - super.getInsets().top);
	}

	@Override
	public final void mousePressed(MouseEvent e) {
		instance.mouseDown(e.isMetaDown(), e.getX() - super.getInsets().left, e.getY() - super.getInsets().top);
	}

	@Override
	public final void mouseReleased(MouseEvent e) {
		instance.mouseUp(e.getButton(), e.getX() - super.getInsets().left, e.getY() - super.getInsets().top);
	}

	@Override
	public final void mouseWheelMoved(MouseWheelEvent e) {
		instance.mouseWheelMoved(e.getWheelRotation());
	}

	@Override
	public void onLogin() {
		if(this.instance.isSubscriber())
		{
//			super.setResizable(true);
			super.setSize(super.getSize().width, super.getSize().height + 15);
		}
		else
		{
			// hack maybe?
			super.setSize(super.getSize().width, super.getSize().height + 15);
		}
		componentResized(null);
	}

	@Override
	public void onLogout() {
		Insets insets = super.getInsets();
		super.setSize(defaultWidth + insets.left + insets.right, defaultHeight + insets.top + insets.bottom + 11);
//		super.setResizable(false);
		/* todo: Resizable */
	}

	@Override
	public void paint(Graphics g) {
		if (instance != null)
			return;
		AppletUtils.render(g);
		AppletUtils.drawPercentage(g, AppletUtils.percentage, AppletUtils.DISPLAY_MESSAGE);
	}
	private int defaultWidth = 512, defaultHeight = 334;

	@Override
	public void run() {
		if (!AppletUtils.CACHEFILE.exists())
			downloadCache();
		AppletUtils.DISPLAY_MESSAGE = "Loading client";
		System.out.println(AppletUtils.width + " " + AppletUtils.height);
		super.setBackground(Color.BLACK); // hack;
		super.setVisible(true);
		Insets insets = super.getInsets();
		this.defaultWidth = AppletUtils.width;
		this.defaultHeight = AppletUtils.height;
		super.setSize(defaultWidth + insets.left + insets.right, defaultHeight + insets.top + insets.bottom);
		super.setPreferredSize(new Dimension(defaultWidth + insets.left + insets.right, defaultHeight + insets.top + insets.bottom));
		instance = new mudclient<>(this, AppletUtils.width, AppletUtils.height);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		instance.run();
	}

	@Override
	public final void update(Graphics g) {
		paint(g);
	}

}