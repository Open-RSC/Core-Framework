package com.loader.openrsc.util;

import java.applet.Applet;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JFrame;

import com.loader.openrsc.Constants;
import com.loader.openrsc.Launcher;
import com.loader.openrsc.frame.AppFrame;

public class ClientLauncher
{
	private static ClassLoader loader;
	private static Class<?> mainClass;
	private static JFrame frame;
	
    public static JFrame getFrame() {
		return frame;
	}

	public static void launchClient() throws InstantiationException,
	IllegalAccessException, 
	IllegalArgumentException, 
	InvocationTargetException,
	NoSuchMethodException, 
	SecurityException {

		startProcess();
		final Applet applet = Applet.class.cast(mainClass.getConstructor()
				.newInstance());
		AppFrame.get().dispose();
		JFrame gameFrame = new JFrame("Open RSC");
		gameFrame.setIconImage(Utils.getImage("RuneScape.png").getImage());
		gameFrame.setMinimumSize(new Dimension(512 + 16, 334 + 49));
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.getContentPane().add(applet);
		applet.init();
		applet.start();
		gameFrame.pack();
		gameFrame.setVisible(true);
	}

	public static void startProcess() {
		try {
			loader = new URLClassLoader(new URL[] { new URL(Constants.CLIENT_URL) });
			mainClass = Class.forName("rsc.RSCFrame", true, loader);
			if (loader == null) {
				Launcher.getPopup().setMessage("Client failed to launch!");
				Launcher.getPopup().showFrame();
				AppFrame.get().getLaunch().setEnabled(true);
				return;
			}
		}
		catch (Exception e) {
			Launcher.getPopup().setMessage("Client failed to launch!");
			Launcher.getPopup().showFrame();
			e.printStackTrace();
		}
	}
}
