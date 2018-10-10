package com.loader.openrsc.util;

import com.loader.openrsc.Constants;
import com.loader.openrsc.OpenRSC;
import com.loader.openrsc.frame.AppFrame;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

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
		JFrame gameFrame = new JFrame(Constants.GAME_NAME);
		gameFrame.setMinimumSize(new Dimension(512 + 16, 334 + 49));
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.getContentPane().add(applet);
                gameFrame.setAlwaysOnTop(false);
		applet.init();
		applet.start();
		gameFrame.pack();
		gameFrame.setVisible(true);
	}

	public static void startProcess() {
		try {
			loader = new URLClassLoader(new URL[] { new URL(Constants.CLIENT_URL) });
			mainClass = Class.forName("orsc.ORSCFrame", true, loader);
			if (loader == null) {
				OpenRSC.getPopup().setMessage("Client failed to launch. Please try again or notify staff.");
				OpenRSC.getPopup().showFrame();
				AppFrame.get().getLaunch().setEnabled(true);
				return;
			}
		}
		catch (Exception e) {
			OpenRSC.getPopup().setMessage("Client failed to launch. Please try again or notify staff.");
			OpenRSC.getPopup().showFrame();
			e.printStackTrace();
		}
	}
}
