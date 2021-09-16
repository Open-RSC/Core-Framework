package orsc;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Properties;

import static orsc.osConfig.F_ANDROID_BUILD;

public class Config {
	private static Properties prop = new Properties();

	public static boolean DEBUG = false; // Enables print out of the config being sent to the client
	static String WINDOW_TITLE = "Open RSC"; // Only set here due to config not set by server fast enough at load time
	public static String SERVER_NAME = "Runescape";
	public static String SERVER_NAME_WELCOME = "Runescape Classic";
	public static String WELCOME_TEXT = "You need a members account to use this server";
	public static String SERVER_IP = null; // Modify this to override "Cache/ip.txt"
	public static int SERVER_PORT; // Modify SERVER_IP above to override "Cache/port.txt" with this value
	public static final int CLIENT_VERSION = 10008;
	private static final int CACHE_VERSION = 4;
	public static boolean MEMBER_WORLD = false;
	public static boolean DISPLAY_LOGO_SPRITE = false;
	private static final boolean CUSTOM_CACHE_DIR_ENABLED = false;
	private static final boolean CACHE_APPEND_VERSION = false;
	private static final String CUSTOM_CACHE_DIR = System.getProperty("user.home") + File.separator + "OpenRSC";
	public static String F_CACHE_DIR = "";

	public static final int INVENTORY_TAB = 1;
	public static final int MINIMAP_AND_COMPASS_TAB = 2;
	public static final int SKILLS_AND_QUESTS_TAB = 3;
	public static final int MAGIC_AND_PRAYER_TAB = 4;
	public static final int FRIENDS_TAB = 5;
	public static final int OPTIONS_TAB = 6;

	/* Configurable: */
	public static boolean C_EXPERIENCE_DROPS = false;
	public static boolean C_NPC_KC = false;
	public static boolean C_BATCH_PROGRESS_BAR = false;
	public static boolean C_HIDE_ROOFS = false;
	public static boolean C_HIDE_FOG = false;
	public static int C_SHOW_GROUND_ITEMS = 0;
	public static boolean C_MESSAGE_TAB_SWITCH = false;
	public static boolean C_NAME_CLAN_TAG_OVERLAY = false;
	public static boolean C_SIDE_MENU_OVERLAY = false;
	public static boolean C_KILL_FEED = false;
	public static int C_FIGHT_MENU = 1;
	public static boolean C_INV_COUNT = false;
	public static boolean C_PARTY_INV = false;
	public static int C_ZOOM;
	public static boolean C_CUSTOM_UI = false; // Enables a osrs style UI
	public static boolean C_HIDE_LOGIN_BOX = false;
	public static boolean C_BLOCK_GLOBAL_FRIEND = false;
	public static int C_MOVE_PER_FRAME = 4;

	/* Experience Config Menu */
	public static int C_EXPERIENCE_COUNTER = 1;
	public static int C_EXPERIENCE_COUNTER_MODE = 0;
	public static int C_EXPERIENCE_COUNTER_COLOR = 0;
	public static int C_EXPERIENCE_DROP_SPEED = 1;
	public static boolean C_EXPERIENCE_CONFIG_SUBMENU = false;

	/* Server Defined: DOUBLE CHECK THESE ON SERVER */
	public static int S_PLAYER_LEVEL_LIMIT = 99;
	public static int S_PLAYER_SLOT_COUNT = 11;
	public static int S_PLAYER_INVENTORY_SLOTS = 30;
	public static boolean S_SPAWN_AUCTION_NPCS = false;
	public static boolean S_SPAWN_IRON_MAN_NPCS = false;
	public static boolean S_SHOW_FLOATING_NAMETAGS = false;
	public static boolean S_WANT_SKILL_MENUS = false;
	public static boolean S_WANT_QUEST_MENUS = false;
	public static boolean S_WANT_EXPERIENCE_ELIXIRS = false;
	public static int S_WANT_KEYBOARD_SHORTCUTS = 0;
	public static boolean S_WANT_CUSTOM_BANKS = false;
	public static boolean S_WANT_BANK_PINS = false;
	public static boolean S_WANT_BANK_NOTES = false;
	public static boolean S_WANT_CERT_DEPOSIT = false;
	public static boolean S_CUSTOM_FIREMAKING = false;
	public static boolean S_WANT_RUNECRAFT = false;
	public static boolean S_WANT_DROP_X = false;
	public static boolean S_WANT_EXP_INFO = false;
	public static boolean S_WANT_WOODCUTTING_GUILD = false;
	public static boolean S_WANT_DECANTING = false;
	public static boolean S_WANT_CERTS_TO_BANK = false;
	public static boolean S_WANT_CUSTOM_RANK_DISPLAY = false;
	public static boolean S_RIGHT_CLICK_BANK = false;
	public static boolean S_RIGHT_CLICK_TRADE = false;
	public static boolean S_WANT_EQUIPMENT_TAB = false;
	public static boolean S_WANT_BANK_PRESETS = false;
	public static boolean S_WANT_LEFTCLICK_WEBS = false;
	public static boolean S_NPC_KILL_MESSAGES = false;
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
	public static boolean S_WANT_GLOBAL_FRIEND = false;
	public static boolean S_WANT_HIDE_IP = false;
	public static boolean S_WANT_REMEMBER = false;
	public static boolean S_WANT_FIXED_OVERHEAD_CHAT = false;
	public static String C_LOGO_SPRITE_ID = "2010";
	public static int C_FPS = 50;
	public static boolean C_WANT_EMAIL = false;
	public static boolean S_WANT_REGISTRATION_LIMIT = false;
	public static boolean S_ALLOW_RESIZE = false;
	public static boolean S_LENIENT_CONTACT_DETAILS = false;
	public static boolean S_WANT_FATIGUE = true;
	public static boolean S_WANT_CUSTOM_SPRITES = false;
	public static boolean S_WANT_CUSTOM_LANDSCAPE = false;
	public static boolean S_WANT_PLAYER_COMMANDS = false;
	public static boolean S_WANT_PETS = false;
	public static int S_MAX_WALKING_SPEED = 1;
	public static boolean S_SHOW_UNIDENTIFIED_HERB_NAMES = false;
	public static boolean S_WANT_QUEST_STARTED_INDICATOR = false;
	public static boolean S_FISHING_SPOTS_DEPLETABLE = false;
	public static boolean S_IMPROVED_ITEM_OBJECT_NAMES = false;
	public static boolean S_WANT_PARTIES = false;
	public static boolean S_MINING_ROCKS_EXTENDED = false;
	public static boolean S_WANT_CUSTOM_UI = false;
	public static boolean S_HIDE_LOGIN_BOX = false;
	public static int S_CHARACTER_CREATION_MODE = 0;
	public static int S_SKILLING_EXP_RATE = 1;
	public static boolean S_WANT_HARVESTING = false;
	public static boolean S_FEATURES_SLEEP = true;
	public static boolean S_WANT_EXTENDED_CATS_BEHAVIOR = false;
	public static boolean S_WANT_CERT_AS_NOTES = false;
	public static boolean S_WANT_OPENPK_POINTS = false;
	public static int S_OPENPK_POINTS_TO_GP_RATIO = 1;

	public static void set(String key, Object value) {
		prop.setProperty(key, value.toString());
	}

	static void initConfig() {
		if (!F_ANDROID_BUILD) {
			if (CUSTOM_CACHE_DIR_ENABLED) {
				if (CACHE_APPEND_VERSION) {
					F_CACHE_DIR = CUSTOM_CACHE_DIR + "_v" + CACHE_VERSION;
				} else {
					F_CACHE_DIR = CUSTOM_CACHE_DIR;
				}
			} else {
				if (CACHE_APPEND_VERSION) {
					F_CACHE_DIR = "Cache" + "_v" + CACHE_VERSION;
				} else {
					F_CACHE_DIR = "Cache";
				}
			}
		} else {
			return;
		}
		setConfigurationFromProperties();
		saveConfiguration(false);
	}

	/**
	 * Saves configuration from the constants in this class
	 *
	 * @param force
	 */
	private static void saveConfiguration(boolean force) {
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
	}

	private static void setConfigurationFromProperties() {
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
					} catch (IllegalAccessException | IllegalArgumentException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}

	}

	static void updateServerConfiguration(Properties newConfig) {
		for (Map.Entry<Object, Object> p : newConfig.entrySet()) {
			prop.setProperty(String.valueOf(p.getKey()), String.valueOf(p.getValue()));
		}
		setConfigurationFromProperties();
	}

	public static String getServerIp() { return prop.getProperty("SERVER_IP"); }

	public static int getServerPort() { return Integer.parseInt(prop.getProperty("SERVER_PORT")); }

	public static String getServerName() {
		return prop.getProperty("SERVER_NAME");
	}

	public static String getServerNameWelcome() {
		return prop.getProperty("SERVER_NAME_WELCOME");
	}

	static String getWelcomeText() {
		return prop.getProperty("WELCOME_TEXT");
	}

	public static String getCommandPrefix() {
		return prop.getProperty("COMMAND_PREFIX");
	}

	static String getcLogoSpriteId() {
		return C_LOGO_SPRITE_ID;
	}

	public static int getFPS() {
		return C_FPS;
	}

	public static boolean wantMembers() {
		return MEMBER_WORLD;
	}

	public static boolean isAndroid() {
		return F_ANDROID_BUILD;
	}

	static boolean Remember() {
		return S_WANT_REMEMBER;
	}

	static boolean wantEmail() {
		return C_WANT_EMAIL;
	}

	static boolean allowResize1() {
		return S_ALLOW_RESIZE;
	}

	static boolean isLenientContactDetails() {
		return S_LENIENT_CONTACT_DETAILS;
	}
}
