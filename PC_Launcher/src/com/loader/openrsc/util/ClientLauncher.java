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

	public static void launchClient() throws IllegalArgumentException,	SecurityException {

		startProcess();
	}

	private static void exit() {
		System.exit(0);
	}

	private static void startProcess() {
		try {
			File f = new File(Constants.CONF_DIR + File.separator + Constants.CLIENT_FILENAME);
			ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java", "-jar", f.getAbsolutePath());
			pb.start();
			//exit();
		} catch (Exception e) {
			Launcher.getPopup().setMessage("Client failed to launch. Please try again or notify staff.");
			Launcher.getPopup().showFrame();
			e.printStackTrace();
		}
	}
}
