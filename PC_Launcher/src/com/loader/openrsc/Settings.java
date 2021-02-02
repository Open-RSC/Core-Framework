package com.loader.openrsc;

import java.io.*;
import java.util.Properties;

public class Settings {
	// default settings
	public static boolean showBotButtons = false;
	public static boolean autoUpdate = true; // must be false if using ant to compile! (check launcherSettings)

	// props keys
	private static String showBotButtonsKey = "show_bot_buttons";
	private static String autoUpdateKey = "auto_update";

	static void loadSettings() {
		Properties props = new Properties();

		try {
			File file = new File(Constants.CONF_DIR);
			if (!file.exists()) {
				file.mkdir();
			}
			File configFile = new File(Constants.CONF_DIR + "/launcherSettings.conf");
			if (!configFile.exists()) {
				saveSettings();
				return;
			}

			FileInputStream in = new FileInputStream(Constants.CONF_DIR + "/launcherSettings.conf");
			props.load(in);
			in.close();
			showBotButtons = getPropBoolean(props, showBotButtonsKey, showBotButtons);
			autoUpdate = getPropBoolean(props, autoUpdateKey, autoUpdate);
		} catch (Exception e) {
			System.out.println("Warning: Error loading launcherSettings.conf!");
			e.printStackTrace();
		}
	}

	private static boolean getPropBoolean(Properties props, String key, boolean defaultProp) {
		String value = props.getProperty(key);
		if (value == null) return defaultProp;

		try {
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			return defaultProp;
		}
	}

	public static void saveSettings() {
		try {
			Properties props = new Properties();

			props.setProperty(showBotButtonsKey, Boolean.toString(showBotButtons));
			props.setProperty(autoUpdateKey, Boolean.toString(autoUpdate));

			FileOutputStream out = new FileOutputStream(Constants.CONF_DIR + "/launcherSettings.conf");
			props.store(out, "---openrsc launcher config---");
			out.close();
		} catch (IOException e) {
			System.out.println("Could not save launcher settings!");
			e.printStackTrace();
		}

	}
}
