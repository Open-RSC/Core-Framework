package com.openrsc.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.openrsc.server.model.container.Bank;

public final class Constants {

	// new area: 582, 157
	// upstairs: 576, 1097

	public static final class GameServer {
		/** 
		 * RSC GAME TICK.
		 */	    	
		public static int GAME_TICK = 620;
		/**
		 * the servers name
		 */
		public static String SERVER_NAME = "Open RSC";
		/**
		 * whether or not this is a members world
		 */
		public static boolean MEMBER_WORLD = true;
		/**
		 * this worlds 'number'
		 */
		public static int WORLD_NUMBER = 2;
		/**
		 * the client version needed for login
		 */
		public static int CLIENT_VERSION = 0;
		/**
		 * the maximum allowed players to connect
		 */
		public static int MAX_PLAYERS = 100;
		/**
		 * the port the server is hosted on
		 */
		public static int SERVER_PORT = 43594;
		/**
		 * where the server is hosted (i.e. USA, Holland, etc.)
		 */
		public static String SERVER_LOCATION = "USA";
		/**
		 * the mysql database host
		 */
		public static String MYSQL_HOST = "localhost";
		/**
		 * the mysql database name
		 */
		public static String MYSQL_DB = "openrsc_game";
		/**
		 * the mysql username
		 */
		public static String MYSQL_USER = "root";
		/**
		 * the mysql password
		 */
		public static String MYSQL_PASS = "root";
		/**
		 *  mysql prefix
		 */
		public static String MYSQL_TABLE_PREFIX = "openrsc_";
		/**
		 * The HMAC SHA512 + Salt private key.
		 */
		public static String HMAC_PRIVATE_KEY = "";
		/**
		 * the combat experience rate
		 */
		public static double COMBAT_EXP_RATE = 1.0;
		/**
		 * the skilling experience rate
		 */
		public static double SKILLING_EXP_RATE = 1.0;
		/**
		 * standard subscriber rate for members
		 */
		public static double SUBSCRIBER_EXP_RATE = 1.0;
		/**
		 * premium/ultimate subscriber rate for members
		 */
		public static double PREMIUM_EXP_RATE = 1.0;
		/**
		 * Wilderness extra boost multiplier
		 */
		public static double WILDERNESS_BOOST = 1.0;
		/**
		 * Skull extra boost multiplier
		 */
		public static double SKULL_BOOST = 1.0;
		/**
		 * A message players will receive upon login
		 */
		public static String MOTD = "Welcome to " + SERVER_NAME + "!";
		/**
		 * where the server will look for other configuration files
		 */
		public static String CONFIG_DIR = "conf" + File.separator + "server";

		/**
		 * Avatar web directory (full path required)
		 */
		public static final String AVATAR_DIR = "./avatars/";

		public static long START_TIME = 0L;
		/**
		 * ID's of all Undead-type of NPC's. (Used for crumble undead & sounds)
		 */
		public static final int[] UNDEAD_NPCS = {15, 53, 80, 178, 664, 41, 52, 68, 180, 214, 319, 40, 45, 46, 50, 179, 195 , 516, 542};
		/**
		 * ID's of all ARMOR type NPC's. (Used for armor hitting sounds)
		 */
		public static final int[] ARMOR_NPCS = {66, 102, 189, 277, 322, 401324, 323, 632, 633};
		/**
		 * Maximum hit for Crumble Undead (Magic) spell. (Against undead)
		 */
		public static final int CRUMBLE_UNDEAD_MAX = 12;

		public static final int[] NPCS_THAT_DO_RETREAT = {3, 89, 114, 40, 64, 0, 34, 241, 29, 116, 23, 21, 81, 351, 367, 52, 71, 666, 359, 188, 136, 190};
		/**
		 * Strikes, Bolts & Blast Spells.
		 * <p/>
		 * Remember, 30+ Magic damage gives you +1 damage, so these damages are
		 * -1 the absolute max. Level Requirement, Max Damage
		 */
		public static final int[][] SPELLS = {{1, 1}, {5, 2}, {9, 2}, {13, 3}, {17, 3}, {23, 4}, {29, 4}, {35, 5}, {41, 5}, {47, 6}, {53, 6}, {59, 7}, {62, 8}, {65, 9}, {70, 10}, {75, 11}};

		public static boolean IS_DOUBLE_EXP = false;
		public static boolean SPAWN_AUCTION_NPCS = false;
		public static boolean SPAWN_IRON_MAN_NPCS = false;
		public static boolean SPAWN_SUBSCRIPTION_NPCS = false;

		public static boolean SHOW_FLOATING_NAMETAGS = false;
		public static boolean WANT_CLANS = false;
		public static boolean WANT_KILL_FEED = false;
		public static boolean FOG_TOGGLE = false;
		public static boolean GROUND_ITEM_TOGGLE = false;
		public static boolean AUTO_MESSAGE_SWITCH_TOGGLE = false;
		public static boolean BATCH_PROGRESSION = false;
		public static boolean SIDE_MENU_TOGGLE = false;
		public static boolean INVENTORY_COUNT_TOGGLE = false;
		public static boolean ZOOM_VIEW_TOGGLE = false;
		public static boolean MENU_COMBAT_STYLE_TOGGLE = false;
		public static boolean FIGHTMODE_SELECTOR_TOGGLE = false;
		public static boolean EXPERIENCE_COUNTER_TOGGLE = false;
		public static boolean EXPERIENCE_DROPS_TOGGLE = false;

		/**
		 * 
		 * @param file
		 * @throws IOException
		 * Config file for server configurations.
		 */
		public static Properties props = new Properties();
		public static void initConfig(String file) throws IOException {
			props.loadFromXML(new FileInputStream(file));

			// Initialization confs
			GAME_TICK = Integer.parseInt(props.getProperty("game_tick"));
			CLIENT_VERSION = Integer.parseInt(props.getProperty("client_version"));
			SERVER_PORT = Integer.parseInt(props.getProperty("server_port"));
			SERVER_NAME = props.getProperty("server_name");
			SERVER_LOCATION = props.getProperty("server_location");
			MAX_PLAYERS = Integer.parseInt(props.getProperty("maxplayers"));
			MYSQL_USER = props.getProperty("mysql_user");
			MYSQL_PASS = props.getProperty("mysql_pass");
			MYSQL_DB = props.getProperty("mysql_db");
			MYSQL_TABLE_PREFIX = props.getProperty("mysql_table_prefix");
			MYSQL_HOST = props.getProperty("mysql_host");
			HMAC_PRIVATE_KEY = props.getProperty("HMAC_PRIVATE_KEY");

			// Game confs
			WORLD_NUMBER = Integer.parseInt(props.getProperty("world_number"));
			MEMBER_WORLD = Boolean.parseBoolean(props.getProperty("member_world"));
			COMBAT_EXP_RATE = Double.parseDouble(props.getProperty("combat_exp_rate"));
			SKILLING_EXP_RATE = Double.parseDouble(props.getProperty("skilling_exp_rate"));
			SUBSCRIBER_EXP_RATE = Double.parseDouble(props.getProperty("subscriber_exp_rate"));
			PREMIUM_EXP_RATE = Double.parseDouble(props.getProperty("premium_exp_rate"));
			WILDERNESS_BOOST = Double.parseDouble(props.getProperty("wilderness_boost"));
			SKULL_BOOST = Double.parseDouble(props.getProperty("skull_boost"));
			IS_DOUBLE_EXP = Boolean.parseBoolean(props.getProperty("double_exp"));

			SPAWN_AUCTION_NPCS = Boolean.parseBoolean(props.getProperty("spawn_auction_npcs"));
			SPAWN_IRON_MAN_NPCS = Boolean.parseBoolean(props.getProperty("spawn_iron_man_npcs"));
			SPAWN_SUBSCRIPTION_NPCS = Boolean.parseBoolean(props.getProperty("spawn_subscription_npcs"));

			SHOW_FLOATING_NAMETAGS = Boolean.parseBoolean(props.getProperty("show_floating_nametags"));
			WANT_CLANS = Boolean.parseBoolean(props.getProperty("want_clans"));
			WANT_KILL_FEED = Boolean.parseBoolean(props.getProperty("want_kill_feed"));
			FOG_TOGGLE = Boolean.parseBoolean(props.getProperty("fog_toggle"));
			GROUND_ITEM_TOGGLE = Boolean.parseBoolean(props.getProperty("fog_toggle"));
			AUTO_MESSAGE_SWITCH_TOGGLE = Boolean.parseBoolean(props.getProperty("auto_message_switch_toggle"));
			BATCH_PROGRESSION = Boolean.parseBoolean(props.getProperty("batch_progression"));
			SIDE_MENU_TOGGLE = Boolean.parseBoolean(props.getProperty("side_menu_toggle"));
			INVENTORY_COUNT_TOGGLE = Boolean.parseBoolean(props.getProperty("inventory_count_toggle"));
			ZOOM_VIEW_TOGGLE = Boolean.parseBoolean(props.getProperty("zoom_view_toggle"));
			MENU_COMBAT_STYLE_TOGGLE = Boolean.parseBoolean(props.getProperty("menu_combat_style_toggle"));
			FIGHTMODE_SELECTOR_TOGGLE = Boolean.parseBoolean(props.getProperty("fightmode_selector_toggle"));
			EXPERIENCE_COUNTER_TOGGLE = Boolean.parseBoolean(props.getProperty("experience_counter_toggle"));
			EXPERIENCE_DROPS_TOGGLE = Boolean.parseBoolean(props.getProperty("experience_drops_toggle"));

			START_TIME = System.currentTimeMillis();
		}
	}

	public static final class Quests {
		public static final int BLACK_KNIGHTS_FORTRESS = 0;
		public static final int COOKS_ASSISTANT = 1;
		public static final int DEMON_SLAYER = 2;
		public static final int DORICS_QUEST = 3;
		public static final int THE_RESTLESS_GHOST = 4;
		public static final int GOBLIN_DIPLOMACY = 5;
		public static final int ERNEST_THE_CHICKEN = 6;
		public static final int IMP_CATCHER = 7;
		public static final int PIRATES_TREASURE = 8;
		public static final int PRINCE_ALI_RESCUE = 9;
		public static final int ROMEO_N_JULIET = 10;
		public static final int SHEEP_SHEARER = 11;
		public static final int SHIELD_OF_ARRAV = 12;
		public static final int THE_KNIGHTS_SWORD = 13;
		public static final int VAMPIRE_SLAYER = 14;
		public static final int WITCHS_POTION = 15;
		public static final int DRAGON_SLAYER = 16;
		public static final int WITCHS_HOUSE = 17;
		public static final int LOST_CITY = 18;
		public static final int HEROS_QUEST = 19;
		public static final int DRUIDIC_RITUAL = 20;
		public static final int MERLINS_CRYSTAL = 21;
		public static final int SCORPION_CATCHER = 22;
		public static final int FAMILY_CREST = 23;
		public static final int TRIBAL_TOTEM = 24;
		public static final int FISHING_CONTEST = 25;
		public static final int MONKS_FRIEND = 26;
		public static final int TEMPLE_OF_IKOV = 27;
		public static final int CLOCK_TOWER = 28;
		public static final int THE_HOLY_GRAIL = 29;
		public static final int FIGHT_ARENA = 30;
		public static final int TREE_GNOME_VILLAGE = 31;
		public static final int THE_HAZEEL_CULT = 32;
		public static final int SHEEP_HERDER = 33;
		public static final int PLAGUE_CITY = 34;
		public static final int SEA_SLUG = 35;
		public static final int WATERFALL_QUEST = 36;
		public static final int BIOHAZARD = 37;
		public static final int JUNGLE_POTION = 38;
		public static final int GRAND_TREE = 39;
		public static final int SHILO_VILLAGE = 40;
		public static final int UNDERGROUND_PASS = 41;
		public static final int OBSERVATORY_QUEST = 42;
		public static final int TOURIST_TRAP = 43;
		public static final int WATCHTOWER = 44;
		public static final int DWARF_CANNON = 45;
		public static final int MURDER_MYSTERY = 46;
		public static final int DIGSITE = 47;
		public static final int GERTRUDES_CAT = 48;
		public static final int LEGENDS_QUEST = 49;
	}

	public static final class Skillcapes {
		public static final int ATTACK_CAPE = 2111;
		public static final int STRENGTH_CAPE = 2259;
		public static final int COOKING_CAPE = 2105;
		public static final int FISHING_CAPE = 2103;
		public static final int SMITHING_CAPE = 2258;
	}
}
