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
	//public static String F_CACHE_DIR = System.getProperty("user.home") + File.separator + "OpenRSC";
        public static String F_CACHE_DIR = "Cache";

	/* Configurable: */
	public static boolean C_EXPERIENCE_DROPS = false;
	public static boolean C_BATCH_PROGRESS_BAR = false;
	public static boolean C_SHOW_ROOF = false;
	public static boolean C_SHOW_FOG = false;
	public static int C_SHOW_GROUND_ITEMS = 0;
	public static boolean C_MESSAGE_TAB_SWITCH = false;
	public static boolean C_NAME_CLAN_TAG_OVERLAY = false;
	public static boolean C_SIDE_MENU_OVERLAY = false;
	public static boolean C_KILL_FEED = false;
	public static int C_FIGHT_MENU = 1;
	public static int C_ZOOM = 0;
	public static boolean C_INV_COUNT = false;

	/* Android: */
	public static boolean F_SHOWING_KEYBOARD = false;
	public static int F_LONG_PRESS_CALC;
	public static boolean C_HOLD_AND_CHOOSE = true;
	public static int C_LONG_PRESS_TIMER = 400;
	public static int C_MENU_SIZE = 6;
	public static boolean C_SWIPE_TO_SCROLL = true;
	public static boolean C_SWIPE_TO_ROTATE = true;

	/* Experience Config Menu */
	public static int C_EXPERIENCE_COUNTER = 1;
	public static int C_EXPERIENCE_COUNTER_MODE = 0;
	public static int C_EXPERIENCE_COUNTER_COLOR = 0;
	public static int C_EXPERIENCE_DROP_SPEED = 1;
	public static boolean C_EXPERIENCE_CONFIG_SUBMENU = false;

	/* Server Defined: DOUBLE CHECK THESE ON SERVER */
	public static boolean S_SPAWN_AUCTION_NPCS = false;
	public static boolean S_SPAWN_IRON_MAN_NPCS = false;
	public static boolean S_SPAWN_SUBSCRIPTION_NPCS = false;
	public static boolean S_SHOW_FLOATING_NAMETAGS = false;
	public static boolean S_WANT_SKILL_MENUS = false;
	public static boolean S_WANT_QUEST_MENUS = false;
	public static boolean S_WANT_EXPERIENCE_ELIXIRS = false;
	public static boolean S_WANT_KEYBOARD_SHORTCUTS = false;
	public static boolean S_WANT_CUSTOM_BANKS = false;
	public static boolean S_WANT_BANK_PINS = false;
	public static boolean S_CUSTOM_FIREMAKING = false;
	public static boolean S_WANT_DROP_X = false;
	public static boolean S_WANT_EXP_INFO = false;
	public static boolean S_WANT_WOODCUTTING_GUILD = false;
	public static boolean S_WANT_DECANTING = false;

	// if you change these, and the config file,
	// they will also change the options menu to
	// 2-tabs (3 on android). (Not enough room for
	// additional options on the 1-tab layout.)
	public static boolean S_WANT_CLANS = false;
	public static boolean S_WANT_KILL_FEED = false;
	public static boolean S_FOG_TOGGLE = false;
	public static boolean S_GROUND_ITEM_TOGGLE = false;
	public static boolean S_AUTO_MESSAGE_SWITCH_TOGGLE = false;
	public static boolean S_BATCH_PROGRESSION = false;
	public static boolean S_SIDE_MENU_TOGGLE = false;
	public static boolean S_INVENTORY_COUNT_TOGGLE = false;
	public static boolean S_ZOOM_VIEW_TOGGLE = false;
	public static boolean S_MENU_COMBAT_STYLE_TOGGLE = false;
	public static boolean S_FIGHTMODE_SELECTOR_TOGGLE = false;
	public static boolean S_EXPERIENCE_COUNTER_TOGGLE = false;
	public static boolean S_EXPERIENCE_DROPS_TOGGLE = false;
	public static boolean S_ITEMS_ON_DEATH_MENU = false;
	public static boolean S_SHOW_ROOF_TOGGLE = false;
	public static boolean S_WANT_GLOBAL_CHAT = false;

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
		for (Map.Entry<Object, Object> entry : prop.entrySet()) {
			for (Field f : fields) {
				if (f.getName().startsWith("F_"))
					continue;
				if (f.getName().equals(entry.getKey())) {
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
					break;
				}
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
