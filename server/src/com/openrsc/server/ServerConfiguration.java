package com.openrsc.server;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ServerConfiguration {

	private static final Logger LOGGER = LogManager.getLogger();


	public boolean DEBUG = false; // enables print out of the config being sent to the client
	/**
	 * Avatar web directory (full path required)
	 */
	public final String AVATAR_DIR = "avatars/"; //located with the Server folder

	/**
	 * RSC GAME TICK.
	 */
	public int GAME_TICK = 640;
	/**
	 * Walking tick
	 */
	public int WALKING_TICK = 640;
	/**
	 * the servers name
	 */
	public String SERVER_NAME = "Runescape";
	public String SERVER_NAME_WELCOME = "Runescape Classic";
	public String WELCOME_TEXT = "You need a members account to use this server";
	/**
	 * Server prefix for messages
	 */
	public String MESSAGE_PREFIX = "@gre@Runescape Classic:@whi@ ";
	/**
	 * Server prefix for invalid arguments
	 */
	public String BAD_SYNTAX_PREFIX = MESSAGE_PREFIX +" Invalid Syntax: ::";
	/**
	 * whether or not this is a members world
	 */
	public boolean MEMBER_WORLD = false;
	/**
	 * this worlds 'number'
	 */
	public int WORLD_NUMBER = 2;
	/**
	 * the client version needed for login
	 */
	public int CLIENT_VERSION = 0;
	/**
	 * the maximum allowed players to connect
	 */
	public int MAX_PLAYERS = 100;
	/**
	 * the maximum allowed players per IP to connect
	 */
	public int MAX_PLAYERS_PER_IP = 5;
	/**
	 * the port the server is hosted on
	 */
	public int SERVER_PORT = 43594;
	/**
	 * idle timer to force a player logout for standing in the same spot
	 *
	 */
	public int IDLE_TIMER = 300000; // 5 minutes
	/**
	 * auto save interval
	 */
	public int AUTO_SAVE = 30000; // 30 seconds
	/**
	 * where the server is hosted (i.e. USA, Holland, etc.)
	 */
	public String SERVER_LOCATION = "USA";
	/**
	 * The HMAC SHA512 + Salt private key.
	 */
	public String HMAC_PRIVATE_KEY = "";
	/**
	 * AutoRestart hour, minute - let 0, 0 = 0000h, 13, 22 = 1322h (1pm)
	 */
	public int RESTART_HOUR;
	public int RESTART_HOUR_2;
	public int RESTART_MINUTE;
	public int RESTART_MINUTE_2;
	/**
	 * AutoRestart Delay in seconds, alert players
	 */
	public int RESTART_DELAY;
	public int RESTART_DELAY_2;
	/**
	 * Default tile range an aggressive NPC will attack a victim
	 */
	public int AGGRO_RANGE;
	/**
	 * the mysql database host
	 */
	public String MYSQL_HOST = "localhost";
	/**
	 * the mysql database name
	 */
	public String MYSQL_DB = "openrsc";
	/**
	 * the mysql username
	 */
	public String MYSQL_USER = "root";
	/**
	 * the mysql password
	 */
	public String MYSQL_PASS = "root";
	/**
	 * mysql prefix
	 */
	public String MYSQL_TABLE_PREFIX = "openrsc_";
	/**
	 * Player Skill Level Limit
	 */
	public int PLAYER_LEVEL_LIMIT = 99;
	/**
	 * the combat experience rate
	 */
	public double COMBAT_EXP_RATE = 1.0;
	/**
	 * the skilling experience rate
	 */
	public double SKILLING_EXP_RATE = 1.0;
	/**
	 * Wilderness extra boost multiplier (added to SKILLING_EXP_RATE)
	 */
	public double WILDERNESS_BOOST = 0;
	/**
	 * Skull extra boost multiplier (added to SKILLING_EXP_RATE)
	 */
	public double SKULL_BOOST = 0;
	/**
	 * Player view distance
	 */
	public int VIEW_DISTANCE = 2;
	/**
	 * Sprite used for client welcome screen logo
	 */
	public String LOGO_SPRITE_ID = "2010";
	/**
	 * NPC blocking
	 * 0 = No NPC blocks
	 * 1 = 2 * combat level + 1 blocks
	 * 2 = Any aggressive NPC blocks
	 * 3 = Any attackable NPC blocks
	 * 4 = All NPCs block
	 */
	public int NPC_BLOCKING = 2;
	/**
	 * The maximum amount of connections allowed at any one time per IP
	 */
	public static int MAX_CONNECTIONS_PER_IP = 6;
	/**
	 * The maximum amount of connection attempts per second for each IP address
	 */
	public static int MAX_CONNECTIONS_PER_SECOND = 5;
	/**
	 * The maximum amount of packets per second for each IP address
	 */
	public static int MAX_PACKETS_PER_SECOND = 20;
	/**
	 * The maximum amount of login attempts allowed per IP per second
	 */
	public static int MAX_LOGINS_PER_SECOND = 1;
	/**
	 * The maximum amount of password/recovery attempts allowed per IP per 5 minutes
	 */
	public static int MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES = 10;
	/**
	 * The amount of time in minutes that users who network flood the server will be IP banned for
	 */
	public static int NETWORK_FLOOD_IP_BAN_MINUTES = 15;
	/**
	 * The amount of time in minutes that users who are suspicious will be IP banned for
	 */
	public static int SUSPICIOUS_PLAYER_IP_BAN_MINUTES = 15;
	/**
	 * where the server will look for other configuration files
	 */
	public String CONFIG_DIR = "conf" + File.separator + "server";
	public long START_TIME = 0L;
	public boolean AVATAR_GENERATOR = false; // Not sent to client
	public boolean IS_DOUBLE_EXP = false;
	public boolean DISPLAY_LOGO_SPRITE = false;
	public boolean SPAWN_AUCTION_NPCS = false;
	public boolean SPAWN_IRON_MAN_NPCS = false;
	public boolean SHOW_FLOATING_NAMETAGS = false;
	public boolean WANT_CLANS = false;
	public boolean WANT_KILL_FEED = false;
	public static boolean FOG_TOGGLE = false;
	public boolean GROUND_ITEM_TOGGLE = false;
	public boolean AUTO_MESSAGE_SWITCH_TOGGLE = false;
	public boolean BATCH_PROGRESSION = false;
	public boolean SIDE_MENU_TOGGLE = false;
	public boolean INVENTORY_COUNT_TOGGLE = false;
	public boolean ZOOM_VIEW_TOGGLE = false;
	public boolean MENU_COMBAT_STYLE_TOGGLE = false;
	public boolean FIGHTMODE_SELECTOR_TOGGLE = false;
	public boolean EXPERIENCE_COUNTER_TOGGLE = false;
	public boolean EXPERIENCE_DROPS_TOGGLE = false;
	public boolean ITEMS_ON_DEATH_MENU = false;
	public boolean SHOW_ROOF_TOGGLE = false;
	public boolean WANT_HIDE_IP = false;
	public boolean WANT_REMEMBER = false;
	public boolean WANT_GLOBAL_CHAT = false;
	public boolean WANT_SKILL_MENUS = false;
	public boolean WANT_QUEST_MENUS = false;
	public boolean WANT_EXPERIENCE_ELIXIRS = false;
	public int WANT_KEYBOARD_SHORTCUTS = 0;
	public boolean WANT_CUSTOM_BANKS = false;
	public boolean WANT_BANK_PINS = false;
	public boolean WANT_BANK_NOTES = false;
	public boolean WANT_CERT_DEPOSIT = false;
	public boolean CUSTOM_FIREMAKING = false;
	public boolean WANT_DROP_X = false;
	public boolean WANT_EXP_INFO = false;
	public boolean WANT_WOODCUTTING_GUILD = false;
	public boolean WANT_MISSING_GUILD_GREETINGS = false;
	public boolean WANT_DECANTING = false;
	public boolean WANT_CERTER_BANK_EXCHANGE = false;
	public boolean MESSAGE_FULL_INVENTORY = false;
	public boolean NPC_DONT_RETREAT;
	public boolean NPC_KILL_LIST = false;
	public boolean NPC_KILL_MESSAGES = false;
	public boolean NPC_KILL_MESSAGES_FILTER = false;
	public String NPC_KILL_MESSAGES_NPCs = "";
	public boolean NPC_KILL_LOGGING = false;
	public boolean VALUABLE_DROP_MESSAGES = false;
	public double VALUABLE_DROP_RATIO = 0;
	public boolean VALUABLE_DROP_EXTRAS = false;
	public String VALUABLE_DROP_ITEMS = "";
	public boolean WANT_CUSTOM_RANK_DISPLAY = false;
	public boolean RIGHT_CLICK_BANK = false;
	public boolean FIX_OVERHEAD_CHAT = false;
	public boolean WANT_FATIGUE = true;
	public int STOP_SKILLING_FATIGUED = 1; //0 - No skills, 1 - Gathering, 2 - All non combat skills
	public boolean WANT_CUSTOM_SPRITES = false;
	public boolean WANT_CUSTOM_LANDSCAPE = false;
	public boolean PLAYER_COMMANDS = false;
	public boolean WANT_PETS = false;
	public boolean AUTO_SERVER_RESTART = false;
	public boolean AUTO_SERVER_RESTART_2 = false;
	public int MAX_WALKING_SPEED = 1;
	public boolean SHOW_UNIDENTIFIED_HERB_NAMES = false;
	public boolean WANT_QUEST_STARTED_INDICATOR = false;
	public boolean WANT_CUSTOM_QUESTS = false;
	public boolean FISHING_SPOTS_DEPLETABLE = false;
	public boolean PROPER_MAGIC_TREE_NAME = false;
	public boolean CRYSTAL_KEY_GIVES_XP = false;
	public boolean LOOTED_CHESTS_STUCK = false;
	public boolean WANT_RUNECRAFTING = false;
	public boolean WANT_DISCORD_AUCTION_UPDATES = false;
	public String DISCORD_AUCTION_WEBHOOK_URL = "";
	public boolean WANT_DISCORD_MONITORING_UPDATES = false;
	public String DISCORD_MONITORING_WEBHOOK_URL = "";
	public boolean WANT_EQUIPMENT_TAB = false;
	public boolean WANT_BANK_PRESETS = false;
	public boolean WANT_PARTIES = false;
	public boolean MINING_ROCKS_EXTENDED = false;
	public boolean WANT_NEW_RARE_DROP_TABLES = false;
	public boolean WANT_LEFTCLICK_WEBS = false;
	public boolean WANT_CUSTOM_WALK_SPEED = false;
	public int MAX_TICKS_UNTIL_FULL_WALKING_SPEED = 5;
	public boolean WANT_IMPROVED_PATHFINDING = false;
	public boolean WANT_PASSWORD_MASSAGE = true;
	//strict check on level requirements for "glitched" validations on rsc
	public boolean STRICT_CHECK_ALL = false;
	public boolean STRICT_PDART_CHECK = false;
	public boolean STRICT_PKNIFE_CHECK = false;
	public boolean STRICT_PSPEAR_CHECK = false;
	public int FPS = 50;
	public boolean WANT_EMAIL = false;
	public boolean WANT_REGISTRATION_LIMIT = false;
	public boolean ALLOW_RESIZE = false;
	public boolean LENIENT_CONTACT_DETAILS = false;
	//loosened checks
	public boolean LOOSE_SHALLOW_WATER_CHECK = false;
	public int PACKET_LIMIT = 30;
	public int CONNECTION_LIMIT = 10;
	public int CONNECTION_TIMEOUT = 15;
	//quest-minigame related
	public boolean WANT_GIANNE_BADGE = false;
	public boolean WANT_BLURBERRY_BADGE = false;
	public boolean WANT_SHOW_KITTENS_CIVILLIAN = false;
	public boolean WANT_BARTER_WORMBRAINS = false;

	public final int RING_OF_RECOIL_LIMIT = 40;
	public final int RING_OF_FORGING_USES = 75;
	public final int DWARVEN_RING_USES = 29;
	public final int DWARVEN_RING_BONUS = 3;
	public List<String> valuableDrops;


	public ImmutableList<String> IGNORED_NETWORK_EXCEPTIONS =
		ImmutableList.of("An existing connection was forcibly closed by the remote host",
			"An established connection was aborted by the software in your host machine",
			"Connection reset by peer");
	/**
	 * @param file
	 * @throws IOException
	 * Config file for server configurations.
	 */
	static Properties props = new Properties();

	void initConfig(String defaultFile) throws IOException {
		try { // Always try to load local.conf first
			props.loadFromXML(new FileInputStream("local.conf"));
		} catch (Exception e) { // Otherwise default to default.conf
			props.loadFromXML(new FileInputStream(defaultFile));
			LOGGER.info("Properties file local.conf not found, loading properties from default.conf");
		}

		// Initialization confs
		GAME_TICK = Integer.parseInt(props.getProperty("game_tick"));
		WALKING_TICK = Integer.parseInt(props.getProperty("walking_tick"));
		WANT_CUSTOM_WALK_SPEED = Boolean.parseBoolean(props.getProperty("want_custom_walking_speed"));
		IDLE_TIMER = Integer.parseInt(props.getProperty("idle_timer"));
		AUTO_SAVE = Integer.parseInt(props.getProperty("auto_save"));
		CLIENT_VERSION = Integer.parseInt(props.getProperty("client_version"));
		SERVER_PORT = Integer.parseInt(props.getProperty("server_port"));
		SERVER_NAME = props.getProperty("server_name");
		SERVER_NAME_WELCOME = props.getProperty("server_name_welcome");
		WELCOME_TEXT = props.getProperty("welcome_text");
		SERVER_LOCATION = props.getProperty("server_location");
		MAX_PLAYERS = Integer.parseInt(props.getProperty("max_players"));
		MAX_PLAYERS_PER_IP = Integer.parseInt(props.getProperty("max_players_per_ip"));
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
		WANT_KEYBOARD_SHORTCUTS = Integer.parseInt(props.getProperty("want_keyboard_shortcuts"));
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
		STOP_SKILLING_FATIGUED = Integer.parseInt(props.getProperty("stop_skilling_fatigued"));
		WANT_CUSTOM_SPRITES = Boolean.parseBoolean(props.getProperty("custom_sprites"));
		WANT_CUSTOM_LANDSCAPE = Boolean.parseBoolean(props.getProperty("custom_landscape"));
		WANT_PETS = Boolean.parseBoolean(props.getProperty("want_pets"));
		SHOW_UNIDENTIFIED_HERB_NAMES = Boolean.parseBoolean(props.getProperty("show_unidentified_herb_names"));
		WANT_QUEST_STARTED_INDICATOR = Boolean.parseBoolean(props.getProperty("want_quest_started_indicator"));
		FISHING_SPOTS_DEPLETABLE = Boolean.parseBoolean(props.getProperty("fishing_spots_depletable"));
		PROPER_MAGIC_TREE_NAME = Boolean.parseBoolean(props.getProperty("proper_magic_tree_name"));
		CRYSTAL_KEY_GIVES_XP = Boolean.parseBoolean(props.getProperty("crystal_key_gives_xp"));
		LOOTED_CHESTS_STUCK = Boolean.parseBoolean(props.getProperty("looted_chests_stuck"));
		WANT_RUNECRAFTING = Boolean.parseBoolean(props.getProperty("want_runecrafting"));
		WANT_DISCORD_AUCTION_UPDATES = Boolean.parseBoolean(props.getProperty("want_discord_auction_updates"));
		DISCORD_AUCTION_WEBHOOK_URL = props.getProperty("discord_auction_webhook_url");
		WANT_DISCORD_MONITORING_UPDATES = Boolean.parseBoolean(props.getProperty("want_discord_monitoring_updates"));
		DISCORD_MONITORING_WEBHOOK_URL = props.getProperty("discord_monitoring_webhook_url");
		WANT_EQUIPMENT_TAB = Boolean.parseBoolean(props.getProperty("want_equipment_tab"));
		WANT_BANK_PRESETS = Boolean.parseBoolean(props.getProperty("want_bank_presets"));
		WANT_PARTIES = Boolean.parseBoolean(props.getProperty("want_parties"));
		MINING_ROCKS_EXTENDED = Boolean.parseBoolean(props.getProperty("mining_rocks_extended"));
		WANT_NEW_RARE_DROP_TABLES = Boolean.parseBoolean(props.getProperty("want_new_rare_drop_tables"));
		WANT_LEFTCLICK_WEBS = Boolean.parseBoolean(props.getProperty("want_leftclick_webs"));
		WANT_CUSTOM_QUESTS = Boolean.parseBoolean(props.getProperty("want_custom_quests"));
		WANT_IMPROVED_PATHFINDING = Boolean.parseBoolean(props.getProperty("want_improved_pathfinding"));


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
		NPC_DONT_RETREAT = Boolean.parseBoolean(props.getProperty("npc_dont_retreat"));
		NPC_BLOCKING = Integer.parseInt(props.getProperty("npc_blocking"));
		AUTO_SERVER_RESTART = Boolean.parseBoolean(props.getProperty("auto_server_restart"));
		RESTART_HOUR = Integer.parseInt(props.getProperty("restart_hour"));
		RESTART_MINUTE = Integer.parseInt(props.getProperty("restart_minute"));
		RESTART_DELAY = Integer.parseInt(props.getProperty("restart_delay"));
		AUTO_SERVER_RESTART_2 = Boolean.parseBoolean(props.getProperty("auto_server_restart_2"));
		RESTART_HOUR_2 = Integer.parseInt(props.getProperty("restart_hour_2"));
		RESTART_MINUTE_2 = Integer.parseInt(props.getProperty("restart_minute_2"));
		RESTART_DELAY_2 = Integer.parseInt(props.getProperty("restart_delay_2"));
		AGGRO_RANGE = Integer.parseInt(props.getProperty("aggro_range"));

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

		// Walking/running related
		MAX_WALKING_SPEED = Integer.parseInt(props.getProperty("max_walking_speed"));
		MAX_TICKS_UNTIL_FULL_WALKING_SPEED = Integer.parseInt(props.getProperty("max_ticks_until_full_walking_speed"));

		MAX_CONNECTIONS_PER_IP = Integer.parseInt(props.getProperty("max_connections_per_ip"));
		MAX_CONNECTIONS_PER_SECOND = Integer.parseInt(props.getProperty("max_connections_per_second"));
		MAX_PACKETS_PER_SECOND = Integer.parseInt(props.getProperty("max_packets_per_second"));
		MAX_LOGINS_PER_SECOND = Integer.parseInt(props.getProperty("max_logins_per_second"));
		MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES = Integer.parseInt(props.getProperty("max_password_guesses_per_five_minutes"));
		NETWORK_FLOOD_IP_BAN_MINUTES = Integer.parseInt(props.getProperty("network_flood_ip_ban_minutes"));
		SUSPICIOUS_PLAYER_IP_BAN_MINUTES = Integer.parseInt(props.getProperty("suspicious_player_ip_ban_minutes"));

		String wantPasswordMassage = props.getProperty("want_password_massage");
		WANT_PASSWORD_MASSAGE = wantPasswordMassage != null ? Boolean.parseBoolean(props.getProperty("want_password_massage")) : WANT_PASSWORD_MASSAGE;

		// Make sure config doesn't exceed max values
		if (VIEW_DISTANCE > 4)
			VIEW_DISTANCE = 4;

		valuableDrops = Arrays.asList(VALUABLE_DROP_ITEMS.split(","));
	}
}
