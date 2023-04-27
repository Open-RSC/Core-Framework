package launcher;

import launcher.Utils.Defaults;
import launcher.Utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	// client names
	public static final String RSCPLUS = "rscplus";
	public static final String WINRUNE = "winrune";
	public static final String OPENRSC = "openrsc";
	public static final String MUD38 = "mudclient38";
	public static final String IDLERSC = "idlersc";
	public static final String WEBCLIENT = "webclient";
	public static final String APOSBOT = "aposbot";
	public static final String RSCTIMES = "rsctimes";

	// default settings
	public static boolean showBotButtons = false;
	public static boolean firstRun = true;
	public static boolean autoUpdate = true; // must be false if using ant to compile! (check launcherSettings)
	public static boolean showPrerelease = false;
	public static boolean undecoratedWindow = true; // "true" means don't draw normal window chrome
	public static boolean undecoratedWindowSave = undecoratedWindow;
	public static int logVerbosity = 3;
	public static String preferredClientPreservation = RSCPLUS;
	public static String preferredClientCabbage = OPENRSC;
	public static String preferredClient2001scape = RSCTIMES;
	public static String preferredClientKale; // TODO: define name when it is known
	public static String preferredClientOpenpk = OPENRSC;
	public static String preferredClientUranium = RSCPLUS;
	public static String preferredClientColeslaw = IDLERSC;

	// props keys
	private static final String showBotButtonsKey = "show_bot_buttons";
	private static final String autoUpdateKey = "auto_update";
	private static final String firstRunKey = "first_run";
	private static final String undecoratedWindowKey = "undecorated_window";
	private static final String showPrereleaseKey = "show_prerelease_servers";
	private static final String preferredClientPreservationKey = "preferred_client_preservation";
	private static final String preferredClientCabbageKey = "preferred_client_cabbage";
	private static final String preferredClient2001scapeKey = "preferred_client_2001scape";
	private static final String preferredClientKaleKey = "preferred_client_kale";
	private static final String preferredClientOpenpkKey = "preferred_client_openpk";
	private static final String preferredClientUraniumKey = "preferred_client_uranium";
	private static final String preferredClientColeslawKey = "preferred_client_coleslaw";

	static void loadSettings() {
		Properties props = new Properties();

		try {
			File configFile = new File(Main.configFileLocation + "/launcherSettings.conf");
      if (configFile.isDirectory()) {
        Logger.Error("launcherSettings.conf is a directory, not a file!");
        return;
      }
      if (!configFile.exists()) {
        Logger.Info("Creating settings for first launch at " + Main.configFileLocation + "/launcherSettings.conf");
        saveSettings();
        return;
      }

			FileInputStream in = new FileInputStream(Main.configFileLocation + "/launcherSettings.conf");
			props.load(in);
			in.close();
			showBotButtons = getPropBoolean(props, showBotButtonsKey, showBotButtons);
			autoUpdate = getPropBoolean(props, autoUpdateKey, autoUpdate);
			firstRun = getPropBoolean(props, firstRunKey, firstRun);
			undecoratedWindow = getPropBoolean(props, undecoratedWindowKey, undecoratedWindow);
			undecoratedWindowSave = undecoratedWindow;
			showPrerelease = getPropBoolean(props, showPrereleaseKey, showPrerelease);

			preferredClientPreservation = getPropString(props, preferredClientPreservationKey, preferredClientPreservation);
			preferredClientCabbage = getPropString(props, preferredClientCabbageKey, preferredClientCabbage);
			preferredClient2001scape = getPropString(props, preferredClient2001scapeKey, preferredClient2001scape);
			preferredClientKale = getPropString(props, preferredClientKaleKey, preferredClientKale);
			preferredClientOpenpk = getPropString(props, preferredClientOpenpkKey, preferredClientOpenpk);
			preferredClientUranium = getPropString(props, preferredClientUraniumKey, preferredClientUranium);
			preferredClientColeslaw = getPropString(props, preferredClientColeslawKey, preferredClientColeslaw);
		} catch (Exception e) {
			Logger.Warn("Warning: Error loading launcherSettings.conf!");
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

	private static String getPropString(Properties props, String key, String defaultProp) {
		String value = props.getProperty(key);
		if (value == null) return defaultProp;
		return value;
	}

	public static void saveSettings() {
		try {
			Properties props = new Properties();

			props.setProperty(showBotButtonsKey, Boolean.toString(showBotButtons));
			props.setProperty(autoUpdateKey, Boolean.toString(autoUpdate));
			props.setProperty(firstRunKey, Boolean.toString(false));
			props.setProperty(undecoratedWindowKey, Boolean.toString(undecoratedWindowSave));
			props.setProperty(showPrereleaseKey, Boolean.toString(showPrerelease));

			props.setProperty(preferredClientPreservationKey, preferredClientPreservation);
			props.setProperty(preferredClientCabbageKey, preferredClientCabbage);
			props.setProperty(preferredClient2001scapeKey, preferredClient2001scape);
			// props.setProperty(preferredClientKaleKey, preferredClientKale); // TODO: only uncomment this when name of Kale client is known
			props.setProperty(preferredClientOpenpkKey, preferredClientOpenpk);
			props.setProperty(preferredClientUraniumKey, preferredClientUranium);
			props.setProperty(preferredClientColeslawKey, preferredClientColeslaw);

      File cacheDir = new File(Main.configFileLocation);
      if (!cacheDir.exists()) {
        cacheDir.mkdirs();
      }

			FileOutputStream out = new FileOutputStream(Main.configFileLocation + "/launcherSettings.conf");
			props.store(out, "---open runescape classic launcher config---");
			out.close();
		} catch (IOException e) {
			Logger.Error("Could not save launcher settings!");
			e.printStackTrace();
		}

	}
}
