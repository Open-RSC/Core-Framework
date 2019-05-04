package com.openrsc.server;

import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public final class Constants {

	public static final class GameServer {

		public static boolean DEBUG = false; // enables print out of the config being sent to the client
		/**
		 * Avatar web directory (full path required)
		 */
		public static final String AVATAR_DIR = "avatars/"; //located with the Server folder
		/**
		 * ID's of all Undead-type of NPC's. (Used for crumble undead & sounds)
		 */
		public static final int[] UNDEAD_NPCS = {15, 53, 80, 178, 664, 41, 52, 68, 180, 214, 319, 40, 45, 46, 50, 179, 195, 516, 542};
		/**
		 * ID's of all ARMOR type NPC's. (Used for armor hitting sounds)
		 */
		public static final int[] ARMOR_NPCS = {66, 102, 189, 277, 322, 401324, 323, 632, 633};
		/**
		 * Maximum hit for Crumble Undead (Magic) spell. (Against undead)
		 */
		public static final int CRUMBLE_UNDEAD_MAX = 12;
		/* Normal list */
		public static final int[] NPCS_THAT_RETREAT_NORM = {NpcId.CHICKEN.id(), NpcId.IMP.id(), NpcId.UNICORN.id(), NpcId.BLACK_UNICORN.id(),
				NpcId.GOBLIN_OBSERVATORY.id(), NpcId.DUNGEON_RAT.id(), NpcId.HIGHWAYMAN.id(), NpcId.BEAR_LVL24.id(), NpcId.BEAR_LVL26.id(),
				NpcId.UGTHANKI.id(), NpcId.SPIDER.id(), NpcId.RAT_WITCHES_POTION.id(), NpcId.THIEF_GENERIC.id(), NpcId.THIEF_BLANKET.id(),
				NpcId.MUGGER.id(), NpcId.SCORPION.id(), NpcId.GIANT_SPIDER_LVL8.id(), NpcId.RAT_LVL8.id(), NpcId.ROGUE.id(),
				NpcId.OOMLIE_BIRD.id(), NpcId.FIREBIRD.id(), NpcId.COW_ATTACKABLE.id(), NpcId.CHAOS_DWARF.id(),
				NpcId.MONK.id(), NpcId.SHANTAY_PASS_GUARD_MOVING.id(), NpcId.FORESTER.id(),
				NpcId.WIZARD.id(), NpcId.ZOMBIE_LVL19.id(), NpcId.SKELETON_LVL21.id()
		};
		/* List for extremely low health 5% - min 1hp*/
		public static final int[] NPCS_THAT_RETREAT_LOW = {NpcId.ZOMBIE_LVL24_GEN.id(), NpcId.SHADOW_SPIDER.id(), NpcId.DEADLY_RED_SPIDER.id(),
				NpcId.ICE_SPIDER.id(), NpcId.JUNGLE_SPIDER.id(), NpcId.GIANT_SPIDER_LVL31.id(), NpcId.POISON_SCORPION.id(),NpcId.POISON_SCORPION.id(),
				NpcId.KING_SCORPION.id(), NpcId.DONNY_THE_LAD.id(), NpcId.SPEEDY_KEITH.id(), NpcId.BLACK_HEATHER.id(),
				NpcId.ZOMBIE_LVL32.id(), NpcId.ZOMBIE_WMAZEKEY.id(), NpcId.GIANT_BAT.id(), NpcId.DEATH_WING.id(),
				NpcId.RAT_LVL13.id(), NpcId.HOBGOBLIN_LVL32.id(), NpcId.OTHERWORLDLY_BEING.id(), NpcId.WYSON_THE_GARDENER.id()
		};
		/**
		 * Strikes, Bolts & Blast Spells.
		 * <p/>
		 * Remember, 30+ Magic damage gives you +1 damage, so these damages are
		 * -1 the absolute max. Level Requirement, Max Damage
		 */
		public static final int[][] SPELLS = {{1, 1}, {5, 2}, {9, 3}, {13, 4}, {17, 5}, {23, 5}, {29, 6}, {35, 6}, {41, 7}, {47, 7}, {53, 8}, {59, 8}, {62, 9}, {65, 9}, {70, 10}, {75, 10}};
		/**
		 * RSC GAME TICK.
		 */
		public static int GAME_TICK = 600;
		/**
		 * the servers name
		 */
		public static String SERVER_NAME = "Runescape";
		public static String SERVER_NAME_WELCOME = "Runescape Classic";
		public static String WELCOME_TEXT = "You need a members account to use this server";
		/**
		 * Server prefix for messages
		 */
		public static String MESSAGE_PREFIX = "@gre@Runescape Classic:@whi@ ";
		/**
		 * whether or not this is a members world
		 */
		public static boolean MEMBER_WORLD = false;
		/**
		 * this worlds 'number'
		 */
		static int WORLD_NUMBER = 2;
		/**
		 * the client version needed for login
		 */
		public static int CLIENT_VERSION = 0;
		/**
		 * the maximum allowed players to connect
		 */
		static int MAX_PLAYERS = 100;
		/**
		 * the port the server is hosted on
		 */
		static int SERVER_PORT = 43594;
		/**
		 * idle timer to force a player logout for standing in the same spot
		 */
		static int IDLE_TIMER = 300000; // 5 minutes
		/**
		 * auto save interval
		 */
		static int AUTO_SAVE = 30000; // 30 seconds
		/**
		 * where the server is hosted (i.e. USA, Holland, etc.)
		 */
		static String SERVER_LOCATION = "USA";
		/**
		 * The HMAC SHA512 + Salt private key.
		 */
		static String HMAC_PRIVATE_KEY = "";
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
		 * mysql prefix
		 */
		public static String MYSQL_TABLE_PREFIX = "openrsc_";
		/**
		 * Player Skill Level Limit
		 */
		public static int PLAYER_LEVEL_LIMIT = 99;
		/**
		 * the combat experience rate
		 */
		public static double COMBAT_EXP_RATE = 1.0;
		/**
		 * the skilling experience rate
		 */
		public static double SKILLING_EXP_RATE = 1.0;
		/**
		 * Wilderness extra boost multiplier (added to SKILLING_EXP_RATE)
		 */
		public static double WILDERNESS_BOOST = 0;
		/**
		 * Skull extra boost multiplier (added to SKILLING_EXP_RATE)
		 */
		public static double SKULL_BOOST = 0;
		/**
		 * Player view distance
		 */
		public static int VIEW_DISTANCE = 2;
		/**
		 * Sprite used for client welcome screen logo
		 */
		public static String LOGO_SPRITE_ID = "2010";
		/**
		 * NPC blocking
		 * 0 = No NPC blocks
		 * 1 = 2 * combat level + 1 blocks
		 * 2 = Any aggressive NPC blocks
		 * 3 = Any attackable NPC blocks
		 * 4 = All NPCs block
		 */
		public static int NPC_BLOCKING = 2;
		/**
		 * where the server will look for other configuration files
		 */
		public static String CONFIG_DIR = "conf" + File.separator + "server";
		static long START_TIME = 0L;
		public static boolean AVATAR_GENERATOR = false; // Not sent to client
		public static boolean PLAYER_COMMANDS = false; // Not sent to client
		public static boolean IS_DOUBLE_EXP = false;
		public static boolean DISPLAY_LOGO_SPRITE = false;
		public static boolean SPAWN_AUCTION_NPCS = false;
		public static boolean SPAWN_IRON_MAN_NPCS = false;
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
		public static boolean ITEMS_ON_DEATH_MENU = false;
		public static boolean SHOW_ROOF_TOGGLE = false;
		public static boolean WANT_HIDE_IP = false;
		public static boolean WANT_REMEMBER = false;
		public static boolean WANT_GLOBAL_CHAT = false;
		public static boolean WANT_SKILL_MENUS = false;
		public static boolean WANT_QUEST_MENUS = false;
		public static boolean WANT_EXPERIENCE_ELIXIRS = false;
		public static boolean WANT_KEYBOARD_SHORTCUTS = false;
		public static boolean WANT_CUSTOM_BANKS = false;
		public static boolean WANT_BANK_PINS = false;
		public static boolean WANT_BANK_NOTES = false;
		public static boolean WANT_CERT_DEPOSIT = false;
		public static boolean CUSTOM_FIREMAKING = false;
		public static boolean WANT_DROP_X = false;
		public static boolean WANT_EXP_INFO = false;
		public static boolean WANT_WOODCUTTING_GUILD = false;
		public static boolean WANT_MISSING_GUILD_GREETINGS = false;
		public static boolean WANT_DECANTING = false;
		public static boolean WANT_CERTER_BANK_EXCHANGE = false;
		public static boolean MESSAGE_FULL_INVENTORY = false;
		public static boolean NPC_KILL_LIST = false;
		public static boolean NPC_KILL_MESSAGES = false;
		public static boolean NPC_KILL_MESSAGES_FILTER = false;
		public static String NPC_KILL_MESSAGES_NPCs = "";
		public static boolean NPC_KILL_LOGGING = false;
		public static boolean VALUABLE_DROP_MESSAGES = false;
		public static double VALUABLE_DROP_RATIO = 0;
		public static boolean VALUABLE_DROP_EXTRAS = false;
		public static String VALUABLE_DROP_ITEMS = "";
		public static boolean WANT_CUSTOM_RANK_DISPLAY = false;
		public static boolean RIGHT_CLICK_BANK = false;
		public static boolean FIX_OVERHEAD_CHAT = false;
		public static boolean WANT_FATIGUE = true;
		//strict check on level requirements for "glitched" validations on rsc
		public static boolean STRICT_CHECK_ALL = false;
		public static boolean STRICT_PDART_CHECK = false;
		public static boolean STRICT_PKNIFE_CHECK = false;
		public static boolean STRICT_PSPEAR_CHECK = false;
		public static int FPS = 50;
		public static boolean WANT_EMAIL = false;
		public static boolean WANT_REGISTRATION_LIMIT = false;
		public static boolean ALLOW_RESIZE = false;
		public static boolean LENIENT_CONTACT_DETAILS = false;
		//loosened checks
		public static boolean LOOSE_SHALLOW_WATER_CHECK = false;
		public static int PACKET_LIMIT = 30;
		public static int CONNECTION_LIMIT = 10;
		public static int CONNECTION_TIMEOUT = 15;
		//quest-minigame related
		public static boolean WANT_GIANNE_BADGE = false;
		public static boolean WANT_BLURBERRY_BADGE = false;
		public static boolean WANT_SHOW_KITTENS_CIVILLIAN = false;
		public static boolean WANT_BARTER_WORMBRAINS = false;

		/**
		 * @param file
		 * @throws IOException
		 * Config file for server configurations.
		 */
		static Properties props = new Properties();

		static void initConfig(String defaultFile) throws IOException {
			try { // Always try to load local.conf first
				props.loadFromXML(new FileInputStream("local.conf"));
			} catch (Exception e) { // Otherwise default to default.conf
				props.loadFromXML(new FileInputStream(defaultFile));
			}

			// Initialization confs
			GAME_TICK = Integer.parseInt(props.getProperty("game_tick"));
			IDLE_TIMER = Integer.parseInt(props.getProperty("idle_timer"));
			AUTO_SAVE = Integer.parseInt(props.getProperty("auto_save"));
			CLIENT_VERSION = Integer.parseInt(props.getProperty("client_version"));
			SERVER_PORT = Integer.parseInt(props.getProperty("server_port"));
			SERVER_NAME = props.getProperty("server_name");
			SERVER_NAME_WELCOME = props.getProperty("server_name_welcome");
			WELCOME_TEXT = props.getProperty("welcome_text");
			SERVER_LOCATION = props.getProperty("server_location");
			MAX_PLAYERS = Integer.parseInt(props.getProperty("maxplayers"));
			MYSQL_USER = props.getProperty("mysql_user");
			MYSQL_PASS = props.getProperty("mysql_pass");
			MYSQL_DB = props.getProperty("mysql_db");
			MYSQL_TABLE_PREFIX = props.getProperty("mysql_table_prefix");
			MYSQL_HOST = props.getProperty("mysql_host");
			HMAC_PRIVATE_KEY = props.getProperty("HMAC_PRIVATE_KEY");
			VIEW_DISTANCE = Integer.parseInt(props.getProperty("view_distance"));
			AVATAR_GENERATOR = Boolean.parseBoolean(props.getProperty("avatar_generator"));
			DISPLAY_LOGO_SPRITE = Boolean.parseBoolean(props.getProperty("display_logo_sprite"));
			LOGO_SPRITE_ID = props.getProperty("logo_sprite_id");
			FPS = Integer.parseInt(props.getProperty("client_fps"));

			// Game confs
			WORLD_NUMBER = Integer.parseInt(props.getProperty("world_number"));
			MEMBER_WORLD = Boolean.parseBoolean(props.getProperty("member_world"));
			PLAYER_LEVEL_LIMIT = Integer.parseInt(props.getProperty("player_level_limit"));
			COMBAT_EXP_RATE = Double.parseDouble(props.getProperty("combat_exp_rate"));
			SKILLING_EXP_RATE = Double.parseDouble(props.getProperty("skilling_exp_rate"));
			WILDERNESS_BOOST = Double.parseDouble(props.getProperty("wilderness_boost"));
			SKULL_BOOST = Double.parseDouble(props.getProperty("skull_boost"));
			IS_DOUBLE_EXP = Boolean.parseBoolean(props.getProperty("double_exp"));
			PLAYER_COMMANDS = Boolean.parseBoolean(props.getProperty("player_commands"));

			SPAWN_AUCTION_NPCS = Boolean.parseBoolean(props.getProperty("spawn_auction_npcs"));
			SPAWN_IRON_MAN_NPCS = Boolean.parseBoolean(props.getProperty("spawn_iron_man_npcs"));
			SHOW_FLOATING_NAMETAGS = Boolean.parseBoolean(props.getProperty("show_floating_nametags"));
			WANT_CLANS = Boolean.parseBoolean(props.getProperty("want_clans"));
			WANT_KILL_FEED = Boolean.parseBoolean(props.getProperty("want_kill_feed"));
			FOG_TOGGLE = Boolean.parseBoolean(props.getProperty("fog_toggle"));
			GROUND_ITEM_TOGGLE = Boolean.parseBoolean(props.getProperty("ground_item_toggle"));
			AUTO_MESSAGE_SWITCH_TOGGLE = Boolean.parseBoolean(props.getProperty("auto_message_switch_toggle"));
			BATCH_PROGRESSION = Boolean.parseBoolean(props.getProperty("batch_progression"));
			SIDE_MENU_TOGGLE = Boolean.parseBoolean(props.getProperty("side_menu_toggle"));
			INVENTORY_COUNT_TOGGLE = Boolean.parseBoolean(props.getProperty("inventory_count_toggle"));
			ZOOM_VIEW_TOGGLE = Boolean.parseBoolean(props.getProperty("zoom_view_toggle"));
			MENU_COMBAT_STYLE_TOGGLE = Boolean.parseBoolean(props.getProperty("menu_combat_style_toggle"));
			FIGHTMODE_SELECTOR_TOGGLE = Boolean.parseBoolean(props.getProperty("fightmode_selector_toggle"));
			EXPERIENCE_COUNTER_TOGGLE = Boolean.parseBoolean(props.getProperty("experience_counter_toggle"));
			EXPERIENCE_DROPS_TOGGLE = Boolean.parseBoolean(props.getProperty("experience_drops_toggle"));
			ITEMS_ON_DEATH_MENU = Boolean.parseBoolean(props.getProperty("items_on_death_menu"));
			SHOW_ROOF_TOGGLE = Boolean.parseBoolean(props.getProperty("show_roof_toggle"));
			WANT_HIDE_IP = Boolean.parseBoolean(props.getProperty("want_hide_ip"));
			WANT_REMEMBER = Boolean.parseBoolean(props.getProperty("want_remember"));
			WANT_GLOBAL_CHAT = Boolean.parseBoolean(props.getProperty("want_global_chat"));
			WANT_SKILL_MENUS = Boolean.parseBoolean(props.getProperty("want_skill_menus"));
			WANT_QUEST_MENUS = Boolean.parseBoolean(props.getProperty("want_quest_menus"));
			WANT_EXPERIENCE_ELIXIRS = Boolean.parseBoolean(props.getProperty("want_experience_elixirs"));
			WANT_KEYBOARD_SHORTCUTS = Boolean.parseBoolean(props.getProperty("want_keyboard_shortcuts"));
			RIGHT_CLICK_BANK = Boolean.parseBoolean(props.getProperty("right_click_bank"));
			WANT_CUSTOM_BANKS = Boolean.parseBoolean(props.getProperty("want_custom_banks"));
			WANT_BANK_PINS = Boolean.parseBoolean(props.getProperty("want_bank_pins"));
			WANT_BANK_NOTES = Boolean.parseBoolean(props.getProperty("want_bank_notes"));
			WANT_CERT_DEPOSIT = Boolean.parseBoolean(props.getProperty("want_cert_deposit"));
			CUSTOM_FIREMAKING = Boolean.parseBoolean(props.getProperty("custom_firemaking"));
			WANT_DROP_X = Boolean.parseBoolean(props.getProperty("want_drop_x"));
			WANT_EXP_INFO = Boolean.parseBoolean(props.getProperty("want_exp_info"));
			WANT_WOODCUTTING_GUILD = Boolean.parseBoolean(props.getProperty("want_woodcutting_guild"));
			WANT_MISSING_GUILD_GREETINGS = Boolean.parseBoolean(props.getProperty("want_missing_guild_greetings"));
			WANT_DECANTING = Boolean.parseBoolean(props.getProperty("want_decanting"));
			WANT_CERTER_BANK_EXCHANGE = Boolean.parseBoolean(props.getProperty("want_certer_bank_exchange"));
			WANT_CUSTOM_RANK_DISPLAY = Boolean.parseBoolean(props.getProperty("want_custom_rank_display"));
			FIX_OVERHEAD_CHAT = Boolean.parseBoolean(props.getProperty("want_fixed_overhead_chat"));
			MESSAGE_FULL_INVENTORY = Boolean.parseBoolean(props.getProperty("message_full_inventory"));
			WANT_FATIGUE = Boolean.parseBoolean(props.getProperty("want_fatigue"));

			NPC_KILL_LIST = Boolean.parseBoolean(props.getProperty("npc_kill_list"));
			NPC_KILL_MESSAGES = Boolean.parseBoolean(props.getProperty("npc_kill_messages"));
			NPC_KILL_MESSAGES_FILTER = Boolean.parseBoolean(props.getProperty("npc_kill_messages_filter"));
			NPC_KILL_MESSAGES_NPCs = props.getProperty("npc_kill_messages_npcs");
			NPC_KILL_LOGGING = Boolean.parseBoolean(props.getProperty("npc_kill_logging"));
			VALUABLE_DROP_MESSAGES = Boolean.parseBoolean(props.getProperty("valuable_drop_messages"));
			VALUABLE_DROP_RATIO = Double.parseDouble(props.getProperty("valuable_drop_ratio"));
			VALUABLE_DROP_EXTRAS = Boolean.parseBoolean(props.getProperty("valuable_drop_extras"));
			VALUABLE_DROP_ITEMS = props.getProperty("valuable_drop_items");
			START_TIME = System.currentTimeMillis();
			NPC_BLOCKING = Integer.parseInt(props.getProperty("npc_blocking"));

			STRICT_CHECK_ALL = Boolean.parseBoolean(props.getProperty("strict_check_all"));
			STRICT_PDART_CHECK = Boolean.parseBoolean(props.getProperty("strict_pdart_check"));
			STRICT_PKNIFE_CHECK = Boolean.parseBoolean(props.getProperty("strict_pknife_check"));
			STRICT_PSPEAR_CHECK = Boolean.parseBoolean(props.getProperty("strict_pspear_check"));

			LOOSE_SHALLOW_WATER_CHECK = Boolean.parseBoolean(props.getProperty("loose_shallow_water_check"));

			WANT_EMAIL = Boolean.parseBoolean(props.getProperty("want_email"));
			WANT_REGISTRATION_LIMIT = Boolean.parseBoolean(props.getProperty("want_registration_limit"));
			ALLOW_RESIZE = Boolean.parseBoolean(props.getProperty("allow_resize"));
			LENIENT_CONTACT_DETAILS = Boolean.parseBoolean(props.getProperty("lenient_contact_details"));

			PACKET_LIMIT = Integer.parseInt(props.getProperty("packet_limit"));
			CONNECTION_LIMIT = Integer.parseInt(props.getProperty("connection_limit"));
			CONNECTION_TIMEOUT = Integer.parseInt(props.getProperty("connection_timeout"));
			
			WANT_GIANNE_BADGE = Boolean.parseBoolean(props.getProperty("want_gianne_badge"));
			WANT_BLURBERRY_BADGE = Boolean.parseBoolean(props.getProperty("want_blurberry_badge"));
			WANT_SHOW_KITTENS_CIVILLIAN = Boolean.parseBoolean(props.getProperty("want_show_kittens_civillian"));
			WANT_BARTER_WORMBRAINS = Boolean.parseBoolean(props.getProperty("want_barter_wormbrains"));

			// Make sure config doesn't exceed max values
			if (VIEW_DISTANCE > 4)
				VIEW_DISTANCE = 4;
		}
	}

	public static final class Poison {
		static final int POISON_SCORPION = 271;
		static final int POISON_SPIDER = 292;
		static final int DUNGEON_SPIDER = 656;
		static final int TRIBESMAN = 421;
		static final int JUNGLE_SAVAGE = 776;

		public static final HashMap<Integer, Integer> npcData = new HashMap<Integer, Integer>() {{
			put(POISON_SCORPION, 38);
			put(POISON_SPIDER, 68);
			put(DUNGEON_SPIDER, 38);
			put(TRIBESMAN, 68);
			put(JUNGLE_SAVAGE, 68);
		}};
	}
	
	public static final class Minigames {
		public static final int ALFRED_GRIMHANDS_BARCRAWL = 0;
		public static final int MAGE_ARENA = 1;
		public static final int BLURBERRYS_BAR = 2;
		public static final int GNOME_RESTAURANT = 3;
		public static final int FISHING_TRAWLER = 4;
		public static final int GNOME_BALL = 5;
		public static final int KITTEN_CARE = 6;
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

		public static final int MAPIDX_QP = 0;
		public static final int MAPIDX_SKILL = 1;
		public static final int MAPIDX_BASE = 2;
		public static final int MAPIDX_VAR = 3;

		public static final HashMap<Integer, int[]> questData = new HashMap<Integer, int[]>() {{
			// QuestID -> Quest Points, Exp Skill ID, Base Exp, Variable Exp
			put(BLACK_KNIGHTS_FORTRESS, new int[]{3, -1, 0, 0});
			put(COOKS_ASSISTANT, new int[]{1, Skills.COOKING, 1000, 200});
			put(DEMON_SLAYER, new int[]{3, -1, 0, 0});
			put(DORICS_QUEST, new int[]{1, Skills.MINING, 700, 300});
			put(THE_RESTLESS_GHOST, new int[]{1, Skills.PRAYER, 2000, 250});
			put(GOBLIN_DIPLOMACY, new int[]{5, Skills.CRAFTING, 500, 60});
			put(ERNEST_THE_CHICKEN, new int[]{4, -1, 0, 0});
			put(IMP_CATCHER, new int[]{1, Skills.MAGIC, 1500, 400});
			put(PIRATES_TREASURE, new int[]{2, -1, 0, 0});
			put(PRINCE_ALI_RESCUE, new int[]{3, -1, 0, 0});
			put(ROMEO_N_JULIET, new int[]{5, -1, 0, 0});
			put(SHEEP_SHEARER, new int[]{1, Skills.CRAFTING, 500, 100});
			put(SHIELD_OF_ARRAV, new int[]{1, -1, 0, 0});
			put(THE_KNIGHTS_SWORD, new int[]{1, Skills.SMITHING, 1400, 1500});
			put(VAMPIRE_SLAYER, new int[]{3, Skills.ATTACK, 1300, 600});
			put(WITCHS_POTION, new int[]{1, Skills.MAGIC, 900, 200});
			put(DRAGON_SLAYER, new int[]{2, -1, 2600, 1200}); // Skill ID in Dragon Slayer files
			put(WITCHS_HOUSE, new int[]{4, Skills.HITPOINTS, 1300, 600});
			put(LOST_CITY, new int[]{3, -1, 0, 0});
			put(HEROS_QUEST, new int[]{1, -1, 300, 200}); // Skill ID Handled in Heros files
			put(DRUIDIC_RITUAL, new int[]{4, Skills.HERBLAW, 1000, 0});
			put(MERLINS_CRYSTAL, new int[]{6, -1, 0, 0});
			put(SCORPION_CATCHER, new int[]{1, Skills.STRENGTH, 1500, 500});
			put(FAMILY_CREST, new int[]{1, -1, 0, 0});
			put(TRIBAL_TOTEM, new int[]{1, Skills.THIEVING, 800, 300});
			put(FISHING_CONTEST, new int[]{1, Skills.FISHING, 0, 300}); // Base XP Handled in Fishing Contest files
			put(MONKS_FRIEND, new int[]{1, Skills.WOODCUT, 500, 500});
			put(TEMPLE_OF_IKOV, new int[]{1, -1, 2000, 1000}); // Skill ID Handled in Ikov files
			put(CLOCK_TOWER, new int[]{1, -1, 0, 0});
			put(THE_HOLY_GRAIL, new int[]{2, -1, 0, 0}); // XP Handled in Grail files
			put(FIGHT_ARENA, new int[]{2, -1, 700, 800}); // Skill ID Handled in Arena files
			put(TREE_GNOME_VILLAGE, new int[]{2, Skills.ATTACK, 800, 900});
			put(THE_HAZEEL_CULT, new int[]{1, Skills.THIEVING, 2000, 200});
			put(SHEEP_HERDER, new int[]{4, -1, 0, 0});
			put(PLAGUE_CITY, new int[]{1, Skills.MINING, 700, 300});
			put(SEA_SLUG, new int[]{1, Skills.FISHING, 700, 800});
			put(WATERFALL_QUEST, new int[]{1, -1, 1000, 900}); // Skill ID Handled in Waterfall files
			put(BIOHAZARD, new int[]{3, Skills.THIEVING, 2000, 200});
			put(JUNGLE_POTION, new int[]{1, Skills.HERBLAW, 1600, 500});
			put(GRAND_TREE, new int[]{5, -1, 0, 0}); // XP Handled in Grade Tree files
			put(SHILO_VILLAGE, new int[]{2, Skills.CRAFTING, 500, 500});
			put(UNDERGROUND_PASS, new int[]{5, -1, 2000, 200}); // Skill ID Handled in Pass files
			put(OBSERVATORY_QUEST, new int[]{2, -1, 1000, 400}); // Skill ID and adjustments Handled in Observatory files
			put(TOURIST_TRAP, new int[]{2, -1, 600, 600}); // Skill ID Handled in Trap files
			put(WATCHTOWER, new int[]{4, Skills.MAGIC, 1000, 1000});
			put(DWARF_CANNON, new int[]{1, Skills.CRAFTING, 1000, 200});
			put(MURDER_MYSTERY, new int[]{3, Skills.CRAFTING, 750, 150});
			put(DIGSITE, new int[]{2, -1, 0, 0}); // XP Handled in Digsite files
			put(GERTRUDES_CAT, new int[]{1, Skills.COOKING, 700, 180});
			put(LEGENDS_QUEST, new int[]{4, -1, 600, 600}); // Skill ID Handled in Legends files
		}};
	}

	//public static final class Skillcapes {
	//	public static final int ATTACK_CAPE = 2111;
	//	public static final int STRENGTH_CAPE = 2259;
	//	public static final int COOKING_CAPE = 2105;
	//	public static final int FISHING_CAPE = 2103;
	//	public static final int SMITHING_CAPE = 2258;
	//}
}
