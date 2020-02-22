package com.openrsc.server;

import com.google.common.collect.ImmutableList;
import com.openrsc.server.util.YMLReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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
	private int WORLD_NUMBER = 2;
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
	int SERVER_PORT = 43594;
	/**
	 * idle timer to force a player logout for standing in the same spot
	 *
	 */
	int IDLE_TIMER = 300000; // 5 minutes
	/**
	 * auto save interval
	 */
	int AUTO_SAVE = 30000; // 30 seconds
	/**
	 * where the server is hosted (i.e. USA, Holland, etc.)
	 */
	private String SERVER_LOCATION = "USA";
	/**
	 * The HMAC SHA512 + Salt private key.
	 */
	private String HMAC_PRIVATE_KEY = "";
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
	private static int SUSPICIOUS_PLAYER_IP_BAN_MINUTES = 15;
	/**
	 * where the server will look for other configuration files
	 */
	public String CONFIG_DIR = "conf" + File.separator + "server";
	private long START_TIME = 0L;
	public boolean AVATAR_GENERATOR = false; // Not sent to client
	public boolean IS_DOUBLE_EXP = false;
	public boolean DISPLAY_LOGO_SPRITE = false;
	public boolean SPAWN_AUCTION_NPCS = false;
	public boolean SPAWN_IRON_MAN_NPCS = false;
	public boolean WANT_PK_BOTS = false;
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
	public boolean WANT_GLOBAL_FRIEND = false;
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
	private String VALUABLE_DROP_ITEMS = "";
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
	public boolean IMPROVED_ITEM_OBJECT_NAMES = false;
	public boolean CRYSTAL_KEY_GIVES_XP = false;
	public boolean LOOTED_CHESTS_STUCK = false;
	public boolean WANT_RUNECRAFTING = false;
	public boolean WANT_HARVESTING = false;
	public boolean WANT_DISCORD_AUCTION_UPDATES = false;
	public String DISCORD_AUCTION_WEBHOOK_URL = "";
	public boolean WANT_DISCORD_MONITORING_UPDATES = false;
	public String DISCORD_MONITORING_WEBHOOK_URL = "";
	public boolean WANT_DISCORD_BOT = false;
	public long CROSS_CHAT_CHANNEL = 0;
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
	private int CONNECTION_LIMIT = 10;
	private int CONNECTION_TIMEOUT = 15;
	//quest-minigame related
	private boolean WANT_GIANNE_BADGE = false;
	private boolean WANT_BLURBERRY_BADGE = false;
	public boolean WANT_SHOW_KITTENS_CIVILLIAN = false;
	public boolean WANT_BARTER_WORMBRAINS = false;
	public boolean LOCKED_POST_QUEST_REGIONS_ACCESSIBLE = false;
	private boolean CAN_RETRIEVE_POST_QUEST_ITEMS = false;
	public boolean CAN_USE_CRACKER_ON_SELF = false;

	public final int RING_OF_RECOIL_LIMIT = 40;
	public final int RING_OF_FORGING_USES = 75;
	public final int DWARVEN_RING_USES = 29;
	public final int DWARVEN_RING_BONUS = 3;
	public List<String> valuableDrops;
	public boolean WANT_CUSTOM_UI = false;
	public int CHARACTER_CREATION_MODE = 0;

	public ImmutableList<String> IGNORED_NETWORK_EXCEPTIONS =
		ImmutableList.of("An existing connection was forcibly closed by the remote host",
			"An established connection was aborted by the software in your host machine",
			"Connection reset by peer");
	/**
	 * @param file
	 * @throws IOException
	 * Config file for server configurations.
	 */
	private static Properties props = new Properties();
	private static YMLReader serverProps = new YMLReader();
	private static YMLReader generalProps = new YMLReader();

	void initConfig(String defaultFile) throws IOException {
		try { // Always try to load local.conf first
//			props.loadFromXML(new FileInputStream("local.conf"));
			// This file should ALWAYS be here!
			generalProps.loadFromYML("connections.conf");
			serverProps.loadFromYML("local.conf");

		} catch (Exception e) { // Otherwise default to default.conf
//			props.loadFromXML(new FileInputStream(defaultFile));
			serverProps.loadFromYML(defaultFile);
			LOGGER.info("Properties file local.conf not found, loading properties from default.conf");
		}

		// Initialization confs
		GAME_TICK = Integer.parseInt(serverProps.getAttribute("game_tick"));
		WALKING_TICK = Integer.parseInt(serverProps.getAttribute("walking_tick"));
		WANT_CUSTOM_WALK_SPEED = Boolean.parseBoolean(serverProps.getAttribute("want_custom_walking_speed"));
		IDLE_TIMER = Integer.parseInt(serverProps.getAttribute("idle_timer"));
		AUTO_SAVE = Integer.parseInt(serverProps.getAttribute("auto_save"));
		CLIENT_VERSION = Integer.parseInt(serverProps.getAttribute("client_version"));
		SERVER_PORT = Integer.parseInt(serverProps.getAttribute("server_port"));
		SERVER_NAME = serverProps.getAttribute("server_name");
		SERVER_NAME_WELCOME = serverProps.getAttribute("server_name_welcome");
		WELCOME_TEXT = serverProps.getAttribute("welcome_text");
		SERVER_LOCATION = serverProps.getAttribute("server_location");
		MAX_PLAYERS = Integer.parseInt(serverProps.getAttribute("max_players"));
		MAX_PLAYERS_PER_IP = Integer.parseInt(serverProps.getAttribute("max_players_per_ip"));
		MYSQL_USER = generalProps.getAttribute("mysql_user");
		MYSQL_PASS = generalProps.getAttribute("mysql_pass");
		MYSQL_DB = serverProps.getAttribute("mysql_db");
		MYSQL_TABLE_PREFIX = generalProps.getAttribute("mysql_table_prefix");
		MYSQL_HOST = generalProps.getAttribute("mysql_host");
		HMAC_PRIVATE_KEY = serverProps.getAttribute("HMAC_PRIVATE_KEY");
		VIEW_DISTANCE = Integer.parseInt(serverProps.getAttribute("view_distance"));
		AVATAR_GENERATOR = Boolean.parseBoolean(serverProps.getAttribute("avatar_generator"));
		DISPLAY_LOGO_SPRITE = Boolean.parseBoolean(serverProps.getAttribute("display_logo_sprite"));
		LOGO_SPRITE_ID = serverProps.getAttribute("logo_sprite_id");
		FPS = Integer.parseInt(serverProps.getAttribute("client_fps"));

		// Game confs
		WORLD_NUMBER = Integer.parseInt(serverProps.getAttribute("world_number"));
		MEMBER_WORLD = Boolean.parseBoolean(serverProps.getAttribute("member_world"));
		PLAYER_LEVEL_LIMIT = Integer.parseInt(serverProps.getAttribute("player_level_limit"));
		COMBAT_EXP_RATE = Double.parseDouble(serverProps.getAttribute("combat_exp_rate"));
		SKILLING_EXP_RATE = Double.parseDouble(serverProps.getAttribute("skilling_exp_rate"));
		WILDERNESS_BOOST = Double.parseDouble(serverProps.getAttribute("wilderness_boost"));
		SKULL_BOOST = Double.parseDouble(serverProps.getAttribute("skull_boost"));
		IS_DOUBLE_EXP = Boolean.parseBoolean(serverProps.getAttribute("double_exp"));
		PLAYER_COMMANDS = Boolean.parseBoolean(serverProps.getAttribute("player_commands"));

		SPAWN_AUCTION_NPCS = Boolean.parseBoolean(serverProps.getAttribute("spawn_auction_npcs"));
		SPAWN_IRON_MAN_NPCS = Boolean.parseBoolean(serverProps.getAttribute("spawn_iron_man_npcs"));
		WANT_PK_BOTS = Boolean.parseBoolean(serverProps.getAttribute("want_pk_bots"));
		SHOW_FLOATING_NAMETAGS = Boolean.parseBoolean(serverProps.getAttribute("show_floating_nametags"));
		WANT_CLANS = Boolean.parseBoolean(serverProps.getAttribute("want_clans"));
		WANT_KILL_FEED = Boolean.parseBoolean(serverProps.getAttribute("want_kill_feed"));
		FOG_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("fog_toggle"));
		GROUND_ITEM_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("ground_item_toggle"));
		AUTO_MESSAGE_SWITCH_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("auto_message_switch_toggle"));
		BATCH_PROGRESSION = Boolean.parseBoolean(serverProps.getAttribute("batch_progression"));
		SIDE_MENU_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("side_menu_toggle"));
		INVENTORY_COUNT_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("inventory_count_toggle"));
		ZOOM_VIEW_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("zoom_view_toggle"));
		MENU_COMBAT_STYLE_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("menu_combat_style_toggle"));
		FIGHTMODE_SELECTOR_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("fightmode_selector_toggle"));
		EXPERIENCE_COUNTER_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("experience_counter_toggle"));
		EXPERIENCE_DROPS_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("experience_drops_toggle"));
		ITEMS_ON_DEATH_MENU = Boolean.parseBoolean(serverProps.getAttribute("items_on_death_menu"));
		SHOW_ROOF_TOGGLE = Boolean.parseBoolean(serverProps.getAttribute("show_roof_toggle"));
		WANT_HIDE_IP = Boolean.parseBoolean(serverProps.getAttribute("want_hide_ip"));
		WANT_REMEMBER = Boolean.parseBoolean(serverProps.getAttribute("want_remember"));
		WANT_GLOBAL_CHAT = Boolean.parseBoolean(serverProps.getAttribute("want_global_chat"));
		WANT_GLOBAL_FRIEND = Boolean.parseBoolean(serverProps.getAttribute("want_global_friend"));
		WANT_SKILL_MENUS = Boolean.parseBoolean(serverProps.getAttribute("want_skill_menus"));
		WANT_QUEST_MENUS = Boolean.parseBoolean(serverProps.getAttribute("want_quest_menus"));
		WANT_EXPERIENCE_ELIXIRS = Boolean.parseBoolean(serverProps.getAttribute("want_experience_elixirs"));
		WANT_KEYBOARD_SHORTCUTS = Integer.parseInt(serverProps.getAttribute("want_keyboard_shortcuts"));
		RIGHT_CLICK_BANK = Boolean.parseBoolean(serverProps.getAttribute("right_click_bank"));
		WANT_CUSTOM_BANKS = Boolean.parseBoolean(serverProps.getAttribute("want_custom_banks"));
		WANT_BANK_PINS = Boolean.parseBoolean(serverProps.getAttribute("want_bank_pins"));
		WANT_BANK_NOTES = Boolean.parseBoolean(serverProps.getAttribute("want_bank_notes"));
		WANT_CERT_DEPOSIT = Boolean.parseBoolean(serverProps.getAttribute("want_cert_deposit"));
		CUSTOM_FIREMAKING = Boolean.parseBoolean(serverProps.getAttribute("custom_firemaking"));
		WANT_DROP_X = Boolean.parseBoolean(serverProps.getAttribute("want_drop_x"));
		WANT_EXP_INFO = Boolean.parseBoolean(serverProps.getAttribute("want_exp_info"));
		WANT_WOODCUTTING_GUILD = Boolean.parseBoolean(serverProps.getAttribute("want_woodcutting_guild"));
		WANT_MISSING_GUILD_GREETINGS = Boolean.parseBoolean(serverProps.getAttribute("want_missing_guild_greetings"));
		WANT_DECANTING = Boolean.parseBoolean(serverProps.getAttribute("want_decanting"));
		WANT_CERTER_BANK_EXCHANGE = Boolean.parseBoolean(serverProps.getAttribute("want_certer_bank_exchange"));
		WANT_CUSTOM_RANK_DISPLAY = Boolean.parseBoolean(serverProps.getAttribute("want_custom_rank_display"));
		FIX_OVERHEAD_CHAT = Boolean.parseBoolean(serverProps.getAttribute("want_fixed_overhead_chat"));
		MESSAGE_FULL_INVENTORY = Boolean.parseBoolean(serverProps.getAttribute("message_full_inventory"));
		WANT_FATIGUE = Boolean.parseBoolean(serverProps.getAttribute("want_fatigue"));
		STOP_SKILLING_FATIGUED = Integer.parseInt(serverProps.getAttribute("stop_skilling_fatigued"));
		WANT_CUSTOM_SPRITES = Boolean.parseBoolean(serverProps.getAttribute("custom_sprites"));
		WANT_CUSTOM_LANDSCAPE = Boolean.parseBoolean(serverProps.getAttribute("custom_landscape"));
		WANT_PETS = Boolean.parseBoolean(serverProps.getAttribute("want_pets"));
		SHOW_UNIDENTIFIED_HERB_NAMES = Boolean.parseBoolean(serverProps.getAttribute("show_unidentified_herb_names"));
		WANT_QUEST_STARTED_INDICATOR = Boolean.parseBoolean(serverProps.getAttribute("want_quest_started_indicator"));
		FISHING_SPOTS_DEPLETABLE = Boolean.parseBoolean(serverProps.getAttribute("fishing_spots_depletable"));
		IMPROVED_ITEM_OBJECT_NAMES = Boolean.parseBoolean(serverProps.getAttribute("improved_item_object_names"));
		CRYSTAL_KEY_GIVES_XP = Boolean.parseBoolean(serverProps.getAttribute("crystal_key_gives_xp"));
		LOOTED_CHESTS_STUCK = Boolean.parseBoolean(serverProps.getAttribute("looted_chests_stuck"));
		WANT_RUNECRAFTING = Boolean.parseBoolean(serverProps.getAttribute("want_runecrafting"));
		WANT_HARVESTING = Boolean.parseBoolean(serverProps.getAttribute("want_harvesting"));
		WANT_DISCORD_AUCTION_UPDATES = Boolean.parseBoolean(serverProps.getAttribute("want_discord_auction_updates"));
		DISCORD_AUCTION_WEBHOOK_URL = generalProps.getAttribute("discord_auction_webhook_url");
		WANT_DISCORD_MONITORING_UPDATES = Boolean.parseBoolean(serverProps.getAttribute("want_discord_monitoring_updates"));
		DISCORD_MONITORING_WEBHOOK_URL = generalProps.getAttribute("discord_monitoring_webhook_url");
		WANT_DISCORD_BOT = Boolean.parseBoolean(serverProps.getAttribute("want_discord_bot"));
		CROSS_CHAT_CHANNEL = Long.parseLong(serverProps.getAttribute("cross_chat_channel"));
		WANT_EQUIPMENT_TAB = Boolean.parseBoolean(serverProps.getAttribute("want_equipment_tab"));
		WANT_BANK_PRESETS = Boolean.parseBoolean(serverProps.getAttribute("want_bank_presets"));
		WANT_PARTIES = Boolean.parseBoolean(serverProps.getAttribute("want_parties"));
		MINING_ROCKS_EXTENDED = Boolean.parseBoolean(serverProps.getAttribute("mining_rocks_extended"));
		WANT_NEW_RARE_DROP_TABLES = Boolean.parseBoolean(serverProps.getAttribute("want_new_rare_drop_tables"));
		WANT_LEFTCLICK_WEBS = Boolean.parseBoolean(serverProps.getAttribute("want_leftclick_webs"));
		WANT_CUSTOM_QUESTS = Boolean.parseBoolean(serverProps.getAttribute("want_custom_quests"));
		WANT_IMPROVED_PATHFINDING = Boolean.parseBoolean(serverProps.getAttribute("want_improved_pathfinding"));
		CHARACTER_CREATION_MODE = Integer.parseInt(serverProps.getAttribute("character_creation_mode"));

		NPC_KILL_LIST = Boolean.parseBoolean(serverProps.getAttribute("npc_kill_list"));
		NPC_KILL_MESSAGES = Boolean.parseBoolean(serverProps.getAttribute("npc_kill_messages"));
		NPC_KILL_MESSAGES_FILTER = Boolean.parseBoolean(serverProps.getAttribute("npc_kill_messages_filter"));
		NPC_KILL_MESSAGES_NPCs = serverProps.getAttribute("npc_kill_messages_npcs");
		NPC_KILL_LOGGING = Boolean.parseBoolean(serverProps.getAttribute("npc_kill_logging"));
		VALUABLE_DROP_MESSAGES = Boolean.parseBoolean(serverProps.getAttribute("valuable_drop_messages"));
		VALUABLE_DROP_RATIO = Double.parseDouble(serverProps.getAttribute("valuable_drop_ratio"));
		VALUABLE_DROP_EXTRAS = Boolean.parseBoolean(serverProps.getAttribute("valuable_drop_extras"));
		VALUABLE_DROP_ITEMS = serverProps.getAttribute("valuable_drop_items");
		START_TIME = System.currentTimeMillis();
		NPC_DONT_RETREAT = Boolean.parseBoolean(serverProps.getAttribute("npc_dont_retreat"));
		NPC_BLOCKING = Integer.parseInt(serverProps.getAttribute("npc_blocking"));
		AUTO_SERVER_RESTART = Boolean.parseBoolean(serverProps.getAttribute("auto_server_restart"));
		RESTART_HOUR = Integer.parseInt(serverProps.getAttribute("restart_hour"));
		RESTART_MINUTE = Integer.parseInt(serverProps.getAttribute("restart_minute"));
		RESTART_DELAY = Integer.parseInt(serverProps.getAttribute("restart_delay"));
		AUTO_SERVER_RESTART_2 = Boolean.parseBoolean(serverProps.getAttribute("auto_server_restart_2"));
		RESTART_HOUR_2 = Integer.parseInt(serverProps.getAttribute("restart_hour_2"));
		RESTART_MINUTE_2 = Integer.parseInt(serverProps.getAttribute("restart_minute_2"));
		RESTART_DELAY_2 = Integer.parseInt(serverProps.getAttribute("restart_delay_2"));
		AGGRO_RANGE = Integer.parseInt(serverProps.getAttribute("aggro_range"));

		STRICT_CHECK_ALL = Boolean.parseBoolean(serverProps.getAttribute("strict_check_all"));
		STRICT_PDART_CHECK = Boolean.parseBoolean(serverProps.getAttribute("strict_pdart_check"));
		STRICT_PKNIFE_CHECK = Boolean.parseBoolean(serverProps.getAttribute("strict_pknife_check"));
		STRICT_PSPEAR_CHECK = Boolean.parseBoolean(serverProps.getAttribute("strict_pspear_check"));

		LOOSE_SHALLOW_WATER_CHECK = Boolean.parseBoolean(serverProps.getAttribute("loose_shallow_water_check"));

		WANT_EMAIL = Boolean.parseBoolean(serverProps.getAttribute("want_email"));
		WANT_REGISTRATION_LIMIT = Boolean.parseBoolean(serverProps.getAttribute("want_registration_limit"));
		ALLOW_RESIZE = Boolean.parseBoolean(serverProps.getAttribute("allow_resize"));
		LENIENT_CONTACT_DETAILS = Boolean.parseBoolean(serverProps.getAttribute("lenient_contact_details"));

		PACKET_LIMIT = Integer.parseInt(serverProps.getAttribute("packet_limit"));
		CONNECTION_LIMIT = Integer.parseInt(serverProps.getAttribute("connection_limit"));
		CONNECTION_TIMEOUT = Integer.parseInt(serverProps.getAttribute("connection_timeout"));

		WANT_GIANNE_BADGE = Boolean.parseBoolean(serverProps.getAttribute("want_gianne_badge"));
		WANT_BLURBERRY_BADGE = Boolean.parseBoolean(serverProps.getAttribute("want_blurberry_badge"));
		WANT_SHOW_KITTENS_CIVILLIAN = Boolean.parseBoolean(serverProps.getAttribute("want_show_kittens_civillian"));
		WANT_BARTER_WORMBRAINS = Boolean.parseBoolean(serverProps.getAttribute("want_barter_wormbrains"));
		LOCKED_POST_QUEST_REGIONS_ACCESSIBLE = Boolean.parseBoolean(serverProps.getAttribute("locked_post_quest_regions_accessible"));
		CAN_RETRIEVE_POST_QUEST_ITEMS = Boolean.parseBoolean(serverProps.getAttribute("can_retrieve_post_quest_items"));
		CAN_USE_CRACKER_ON_SELF = Boolean.parseBoolean(serverProps.getAttribute("can_use_cracker_on_self"));

		// Walking/running related
		MAX_WALKING_SPEED = Integer.parseInt(serverProps.getAttribute("max_walking_speed"));
		MAX_TICKS_UNTIL_FULL_WALKING_SPEED = Integer.parseInt(serverProps.getAttribute("max_ticks_until_full_walking_speed"));

		MAX_CONNECTIONS_PER_IP = Integer.parseInt(serverProps.getAttribute("max_connections_per_ip"));
		MAX_CONNECTIONS_PER_SECOND = Integer.parseInt(serverProps.getAttribute("max_connections_per_second"));
		MAX_PACKETS_PER_SECOND = Integer.parseInt(serverProps.getAttribute("max_packets_per_second"));
		MAX_LOGINS_PER_SECOND = Integer.parseInt(serverProps.getAttribute("max_logins_per_second"));
		MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES = Integer.parseInt(serverProps.getAttribute("max_password_guesses_per_five_minutes"));
		NETWORK_FLOOD_IP_BAN_MINUTES = Integer.parseInt(serverProps.getAttribute("network_flood_ip_ban_minutes"));
		SUSPICIOUS_PLAYER_IP_BAN_MINUTES = Integer.parseInt(serverProps.getAttribute("suspicious_player_ip_ban_minutes"));

		String wantPasswordMassage = serverProps.getAttribute("want_password_massage");
		WANT_PASSWORD_MASSAGE = wantPasswordMassage != null ? Boolean.parseBoolean(serverProps.getAttribute("want_password_massage")) : WANT_PASSWORD_MASSAGE;

		// Make sure config doesn't exceed max values
		if (VIEW_DISTANCE > 4)
			VIEW_DISTANCE = 4;

		valuableDrops = Arrays.asList(VALUABLE_DROP_ITEMS.split(","));

		WANT_CUSTOM_UI = Boolean.parseBoolean(serverProps.getAttribute("want_custom_ui"));
	}
}
