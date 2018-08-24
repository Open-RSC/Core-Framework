package com.rscl.web.client;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.net.URL;

public class Loader
extends Applet
implements Runnable,
AppletStub {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Thread loaderThread;
	private boolean all;
	private boolean img;
	private Image loadingImage;
	private MediaTracker mediaTracker;
	private Applet mudclient;

	@Override
	public void run() {
		try {
			this.setSize(512, 344);
			this.mediaTracker = new MediaTracker(this);
			this.mediaTracker.addImage(this.loadingImage, 0);
			Updater updater = new Updater(this);
			updater.init();
			updater.doneLoading();
			this.drawLoadingBar("Fetching client...", 0.0f);
			AllPermissionsClassLoader classLoader = new AllPermissionsClassLoader(new URL[]{new URL("http://wolfkingdom.net/client/client.jar")});
			this.drawLoadingBar("Fetching client...", 100.0f);
			Class<?> mainClass = Class.forName("com.rscl.client.pc.RSCFrame", true, classLoader);
			this.setLayout(new BorderLayout());
			this.mudclient = (Applet)mainClass.getConstructor(new Class[0]).newInstance(new Object[0]);
			this.mudclient.setPreferredSize(new Dimension(512, 344));
			this.mudclient.setStub(this);
			this.mudclient.setVisible(true);
			this.add(this.mudclient);
			this.mudclient.init();
			this.mudclient.start();
		}
		catch (Exception exception) {
			System.out.println(exception + " " + exception.getMessage());
			exception.printStackTrace();
			return;
		}
		this.validate();
	}

	public final void drawLoadingBar(String s, float percent) {
		Graphics g = this.getGraphics();
		Font font = new Font("Helvetica", 1, 13);
		FontMetrics fontmetrics = this.getFontMetrics(font);
		Font font1 = new Font("Helvetica", 0, 13);
		FontMetrics fontmetrics1 = this.getFontMetrics(font1);
		if (this.all || !this.img && this.mediaTracker.checkAll(true)) {
			this.all = false;

			g.setColor(Color.black);
			g.fillRect(0, 0, 512, 344);

			g.setColor(Color.white);
			g.setFont(font);
			String s1 = "RuneScape has been updated!";
			g.drawString(s1, 256 - fontmetrics.stringWidth(s1) / 2, 125);
			s1 = "Please wait - Fetching new files...";
			g.drawString(s1, 256 - fontmetrics.stringWidth(s1) / 2, 140);
			g.setFont(font1);
			s1 = "This may take a few minutes, but only";
			g.drawString(s1, 256 - fontmetrics1.stringWidth(s1) / 2, 165);
			s1 = "needs to be done when the game is updated.";
			g.drawString(s1, 256 - fontmetrics1.stringWidth(s1) / 2, 180);
		}
		Color color = new Color(140, 17, 17);
		g.setColor(color);
		g.drawRect(104, 190, 304, 34);
		g.fillRect(106, 192, (int)percent * 3, 30);
		g.setColor(Color.black);
		g.fillRect(106 + (int)percent * 3, 192, 300 - (int)percent * 3, 30);
		String s2 = "Loading " + s + " - " + (int)percent + "%";
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString(s2, 256 - fontmetrics.stringWidth(s2) / 2, 212);
	}

	@Override
	public void stop() {
		this.loaderThread.stop();
		this.loaderThread = null;
	}

	@Override
	public void appletResize(int width, int height) {
		this.resize(width, height);
	}

	@Override
	public final void start() {
		this.loaderThread = new Thread(this);
		this.loaderThread.start();
	}

	@Override
	public final void init() {
		this.all = true;
		this.img = false;
	}
}
