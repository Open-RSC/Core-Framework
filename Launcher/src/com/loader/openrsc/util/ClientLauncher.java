package com.loader.openrsc.util;

import com.loader.openrsc.Constants;
import com.loader.openrsc.Launcher;
import com.loader.openrsc.frame.AppFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class ClientLauncher {
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
		final JFrame applet = (JFrame) mainClass.getConstructor()
			.newInstance();
		AppFrame.get().dispose();
		JFrame gameFrame = new JFrame(Constants.Title);
		gameFrame.setMinimumSize(new Dimension(512 + 16, 334 + 49));
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.getContentPane().add(applet);
		gameFrame.setAlwaysOnTop(false);
		gameFrame.pack();
		gameFrame.setVisible(true);
	}

	private static void exit() {
		System.exit(0);
	}

	private static void startProcess() {
		try {
			File f = new File(Constants.CONF_DIR + File.separator + Constants.CLIENT_FILENAME);
			ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java", "-Xms312m", "-jar", f.getAbsolutePath());
			pb.start();
			//exit(); // No need to close on successful launch, some players may want multiple clients open at once.
		} catch (Exception e) {
			Launcher.getPopup().setMessage("Client failed to launch. Please try again or notify staff.");
			Launcher.getPopup().showFrame();
			e.printStackTrace();
		}
	}
}
