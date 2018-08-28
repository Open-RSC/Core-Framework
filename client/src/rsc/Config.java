package rsc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {
	private static Properties prop = new Properties();

	public static final String SERVER_NAME = "Open RSC";
	public static final String SERVER_IP = "localhost";
	public static final int SERVER_PORT = 43594;
	public static final int CLIENT_VERSION = 1;
	public static final boolean MEMBERS_FEATURES = true;


	public static boolean F_ANDROID_BUILD = false;
	public static String F_CACHE_DIR = System.getProperty("user.home") + File.separator + "OpenRSC";

	/* Configurable: */
	public static boolean EXPERIENCE_DROPS = false;
	public static boolean BATCH_PROGRESS_BAR = true;
	public static boolean SHOW_ROOF = true;
	public static boolean SHOW_FOG = false;
	public static int SHOW_GROUND_ITEMS = 0;
	public static boolean MESSAGE_TAB_SWITCH = true;
	public static boolean NAME_CLAN_TAG_OVERLAY = true;
	public static boolean SIDE_MENU_OVERLAY = false;
	public static boolean KILL_FEED = true;
	public static int FIGHT_MENU = 1;
	public static int ZOOM = 0;
	public static boolean INV_COUNT = false;

	/* Android: */
	public static boolean F_SHOWING_KEYBOARD = false;
	public static int F_LONG_PRESS_CALC;
	public static boolean HOLD_AND_CHOOSE = true;
	public static int LONG_PRESS_TIMER = 400;
	public static int MENU_SIZE = 6;
	public static boolean SWIPE_TO_SCROLL = true;
	public static boolean SWIPE_TO_ROTATE = true;

	/* Experience Config Menu */
	public static int EXPERIENCE_COUNTER = 1;
	public static int EXPERIENCE_COUNTER_MODE = 0;
	public static int EXPERIENCE_COUNTER_COLOR = 0;
	public static int EXPERIENCE_DROP_SPEED = 1;
	public static boolean EXPERIENCE_CONFIG_SUBMENU = true;

	/* Server Defined: DOUBLE CHECK THESE ON SERVER */
	public static boolean SPAWN_AUCTION_NPCS = false;
	public static boolean SPAWN_IRON_MAN_NPCS = false;
	public static boolean SPAWN_SUBSCRIPTION_NPCS = false;

	public static void set(String key, Object value) {
		prop.setProperty(key, value.toString());
	}

	public static void initConfig() {
		try {
			File file = new File(F_CACHE_DIR + File.separator + "client.properties");
			if (!file.exists()) {
				file.createNewFile();
				saveConfiguration(true);
			}
			prop.load(new FileInputStream(F_CACHE_DIR + File.separator + "client.properties"));
			setConfigurationFromProperties();
			saveConfiguration(false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveConfigProperties() {
		try {
			File file = new File(F_CACHE_DIR + File.separator + "client.properties");
			if (file.exists()) {
				file.delete();
			}
			prop.store(new FileOutputStream(F_CACHE_DIR + File.separator + "client.properties"), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves configuration from the constants in this class
	 *
	 * @param force
	 */
	public static void saveConfiguration(boolean force) {
		Field[] fields = Config.class.getDeclaredFields();
		for (Field f : fields) {
			if (f.getName().startsWith("F_"))
				continue;
			if (Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers())) {
				try {
					if (force || !prop.containsKey(f.getName())
							|| !prop.get(f.getName()).toString().equalsIgnoreCase(f.get(null).toString())) {
						Class<?> t = f.getType();

						if (t == int.class) {
							set(f.getName(), f.getInt(null));
						} else if (t == long.class) {
							set(f.getName(), f.getLong(null));
						} else if (t == float.class) {
							set(f.getName(), f.getFloat(null));
						} else if (t == double.class) {
							set(f.getName(), f.getDouble(null));
						} else if (t == boolean.class) {
							set(f.getName(), f.getBoolean(null));
						}
					}
				} catch (Exception e) {
					System.out.println("Unable to save setting: " + f.getName() + "");
					e.printStackTrace();
				}
			}
		}
		saveConfigProperties();
	}

	public static void setConfigurationFromProperties() {
		Field[] fields = Config.class.getDeclaredFields();
		boolean found = false;
		for (Map.Entry<Object, Object> entry : prop.entrySet()) {
			for (Field f : fields) {
				if (f.getName().startsWith("F_"))
					continue;
				if (f.getName().equals(entry.getKey())) {
					found = true;
					try {
						Class<?> t = f.getType();
						if (t == int.class) {
							f.set(null, Integer.parseInt((String) entry.getValue()));
						} else if (t == float.class) {
							f.set(null, Float.parseFloat((String) entry.getValue()));
						} else if (t == double.class) {
							f.set(null, Double.parseDouble((String) entry.getValue()));
						} else if (t == boolean.class) {
							f.set(null, Boolean.parseBoolean((String) entry.getValue()));
						} else if (t == long.class) {
							f.set(null, Long.parseLong((String) entry.getValue()));
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
				if (found == true) break;
			}
		}

	}

	public final static void updateServerConfiguration(Properties newConfig) {
		for (Map.Entry<Object, Object> p : newConfig.entrySet()) {
			prop.setProperty(String.valueOf(p.getKey()), String.valueOf(p.getValue()));
		}
		setConfigurationFromProperties();
	}

	public static String getServerName() {
		return prop.getProperty("SERVER_NAME");
	}
    
	public static String getCommandPrefix() {
		return prop.getProperty("COMMAND_PREFIX");
	}

	public static boolean isAndroid() {
		return F_ANDROID_BUILD;
	}
}
