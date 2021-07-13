package com.openrsc.server;

import com.google.common.collect.ImmutableList;
import com.openrsc.server.database.DatabaseType;
import com.openrsc.server.util.SystemUtil;
import com.openrsc.server.util.YMLReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ServerConfiguration {

	private static final Logger LOGGER = LogManager.getLogger();


	public boolean DEBUG = false; // enables print out of the config being sent to the client
	/**
	 * Avatar web directory (full path required)
	 */
	public final String AVATAR_DIR = "avatars/"; //located with the Server folder

	public int GAME_TICK = 640;
	public int WALKING_TICK = 640;

	public String SERVER_NAME;
	public String SERVER_NAME_WELCOME;
	public String WELCOME_TEXT;
	public String MESSAGE_PREFIX = "@gre@Runescape Classic:@whi@ ";
	public String BAD_SYNTAX_PREFIX = MESSAGE_PREFIX +" Invalid Syntax: ::";
	public boolean MEMBER_WORLD;
	public int WORLD_NUMBER;
	public int CLIENT_VERSION;
	public int MAX_PLAYERS;
	public int MAX_PLAYERS_PER_IP;
	public int SESSION_ID_SENDER_TIMER;
	int SERVER_PORT;
	int IDLE_TIMER;
	int AUTO_SAVE;
	public int AGGRO_RANGE;
	public DatabaseType DB_TYPE;
	public String DB_HOST;
	public String DB_NAME;
	public String DB_USER;
	public String DB_PASS;
	public String DB_TABLE_PREFIX;
	public int PLAYER_LEVEL_LIMIT;
	public boolean WANT_EXPERIENCE_CAP;
	public int EXPERIENCE_LIMIT;
	public double COMBAT_EXP_RATE;
	public double SKILLING_EXP_RATE;
	public double WILDERNESS_BOOST;
	public double SKULL_BOOST;
	public double NPC_RESPAWN_MULTIPLIER;
	public int VIEW_DISTANCE;
	public String LOGO_SPRITE_ID;
	public int NPC_BLOCKING;
	public int PLAYER_BLOCKING;
	public int MAX_CONNECTIONS_PER_IP;
	public int MAX_CONNECTIONS_PER_SECOND;
	public int MAX_PACKETS_PER_SECOND;
	public int MAX_LOGINS_PER_SECOND;
	public int MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES;
	public int NETWORK_FLOOD_IP_BAN_MINUTES;
	public boolean WANT_PCAP_LOGGING;
	public boolean WANT_THREADING__BREAK_PID_PRIORITY;

	// Location of the server conf files.
	public String CONFIG_DIR = "conf" + File.separator + "server";

	private long START_TIME;
	public boolean AVATAR_GENERATOR; // Not sent to client
	public boolean IS_DOUBLE_EXP;
	public boolean DISPLAY_LOGO_SPRITE;
	public boolean SPAWN_AUCTION_NPCS;
	public boolean SPAWN_IRON_MAN_NPCS;
	public boolean SHOW_FLOATING_NAMETAGS;
	public boolean WANT_CLANS;
	public boolean WANT_KILL_FEED;
	public boolean FOG_TOGGLE;
	public boolean GROUND_ITEM_TOGGLE;
	public boolean AUTO_MESSAGE_SWITCH_TOGGLE;
	public boolean HIDE_LOGIN_BOX_TOGGLE;
	public boolean BATCH_PROGRESSION;
	public boolean CUSTOM_IMPROVEMENTS;
	public boolean SIDE_MENU_TOGGLE;
	public boolean INVENTORY_COUNT_TOGGLE;
	public boolean ZOOM_VIEW_TOGGLE;
	public boolean MENU_COMBAT_STYLE_TOGGLE;
	public boolean FIGHTMODE_SELECTOR_TOGGLE;
	public boolean EXPERIENCE_COUNTER_TOGGLE;
	public boolean EXPERIENCE_DROPS_TOGGLE;
	public boolean ITEMS_ON_DEATH_MENU;
	public boolean SHOW_ROOF_TOGGLE;
	public boolean WANT_HIDE_IP;
	public boolean WANT_REMEMBER;
	public boolean WANT_GLOBAL_CHAT;
	public boolean WANT_GLOBAL_FRIEND;
	public int GLOBAL_MESSAGE_COOLDOWN;
	public boolean WANT_SKILL_MENUS;
	public boolean WANT_QUEST_MENUS;
	public boolean WANT_EXPERIENCE_ELIXIRS;
	public int WANT_KEYBOARD_SHORTCUTS;
	public boolean WANT_CUSTOM_BANKS;
	public boolean WANT_BANK_PINS;
	public boolean WANT_BANK_NOTES;
	public boolean WANT_CERT_DEPOSIT;
	public boolean CUSTOM_FIREMAKING;
	public boolean WANT_DROP_X;
	public boolean WANT_EXP_INFO;
	public boolean WANT_WOODCUTTING_GUILD;
	public boolean WANT_MISSING_GUILD_GREETINGS;
	public boolean WANT_DECANTING;
	public boolean WANT_CERTER_BANK_EXCHANGE;
	public boolean MESSAGE_FULL_INVENTORY;
	public boolean NPC_DONT_RETREAT;
	public boolean NPC_KILL_LIST;
	public boolean NPC_KILL_MESSAGES;
	public boolean NPC_KILL_MESSAGES_FILTER;
	public String NPC_KILL_MESSAGES_NPCs;
	public boolean NPC_KILL_LOGGING;
	public boolean VALUABLE_DROP_MESSAGES;
	public double VALUABLE_DROP_RATIO;
	public boolean VALUABLE_DROP_EXTRAS;
	public boolean WANT_CUSTOM_RANK_DISPLAY;
	public boolean RIGHT_CLICK_BANK;
	public boolean RIGHT_CLICK_TRADE;
	public boolean FIX_OVERHEAD_CHAT;
	public boolean WANT_FATIGUE;
	public int STOP_SKILLING_FATIGUED;
	public boolean WANT_CUSTOM_SPRITES;
	public boolean WANT_CUSTOM_LANDSCAPE;
	public boolean PLAYER_COMMANDS;
	public boolean WANT_PETS;
	public int MAX_WALKING_SPEED;
	public boolean SHOW_UNIDENTIFIED_HERB_NAMES;
	public boolean WANT_QUEST_STARTED_INDICATOR;
	public boolean WANT_POISON_NPCS;
	public boolean WANT_CUSTOM_QUESTS;
	public boolean FISHING_SPOTS_DEPLETABLE;
	public boolean IMPROVED_ITEM_OBJECT_NAMES;
	public boolean CRYSTAL_KEY_GIVES_XP;
	public boolean LOOTED_CHESTS_STUCK;
	public boolean WANT_RUNECRAFT;
	public boolean WANT_HARVESTING;
	public boolean WANT_CUSTOM_LEATHER;
	public boolean WANT_BETTER_JEWELRY_CRAFTING;
	public boolean MORE_SHAFTS_PER_BETTER_LOG;
	public boolean FASTER_YOHNUS;
	public boolean WANT_DISCORD_AUCTION_UPDATES;
	public String DISCORD_AUCTION_WEBHOOK_URL;
	public boolean WANT_DISCORD_MONITORING_UPDATES;
	public String DISCORD_MONITORING_WEBHOOK_URL;
	public boolean WANT_DISCORD_BOT;
	public long CROSS_CHAT_CHANNEL;
	public boolean WANT_EQUIPMENT_TAB;
	public boolean WANT_BANK_PRESETS;
	public boolean WANT_PARTIES;
	public boolean WANT_OPENPK_POINTS;
	public int OPENPK_POINTS_TO_GP_RATIO;
	public boolean WANT_PK_BOTS;
	public int RESPAWN_LOCATION_X;
	public int RESPAWN_LOCATION_Y;
	public double PARTY_ADDITIONAL_XP_PERCENT_PER_PLAYER;
	public double PARTY_DISTANCE_PERCENT_DECREASE;
	public double PARTY_SAVE_XP_FOR_SKILLER_PERCENT;
	public int PARTY_SHARE_MAX_X;
	public int PARTY_SHARE_MAX_Y;
	public boolean PARTY_SHARE_INFINITE_RANGE;
	public boolean PARTY_SHARE_WITH_SAME_IP;
	public String PARTY_SHARE_SIZE_ALGORITHM;
	public String PARTY_SHARE_DISTANCE_ALGORITHM;
	public int PARTY_MAX_SIZE_FOR_ADDITIONAL_XP;
	public boolean PARTY_IRON_MAN_CAN_SHARE;
	public boolean MINING_ROCKS_EXTENDED;
	public boolean WANT_NEW_RARE_DROP_TABLES;
	public boolean WANT_LEFTCLICK_WEBS;
	public boolean WANT_CUSTOM_WALK_SPEED;
	public int MAX_TICKS_UNTIL_FULL_WALKING_SPEED;
	public boolean WANT_IMPROVED_PATHFINDING;
	//strict check on level requirements for "glitched" validations on rsc
	public boolean STRICT_CHECK_ALL;
	public boolean STRICT_PDART_CHECK;
	public boolean STRICT_PKNIFE_CHECK;
	public boolean STRICT_PSPEAR_CHECK;
	public int FPS;
	public boolean WANT_EMAIL;
	public boolean WANT_REGISTRATION_LIMIT;
	public boolean ALLOW_RESIZE;
	public boolean LENIENT_CONTACT_DETAILS;
	//loosened checks
	public boolean LOOSE_SHALLOW_WATER_CHECK;
	public boolean FEATURES_SLEEP;
	public int RESTRICT_ITEM_ID;
	public int PACKET_LIMIT;
	//quest-minigame related
	public boolean WANT_GIANNE_BADGE;
	public boolean WANT_BLURBERRY_BADGE;
	public boolean WANT_EXTENDED_CATS_BEHAVIOR;
	public boolean WANT_BARTER_WORMBRAINS;
	public boolean LOCKED_POST_QUEST_REGIONS_ACCESSIBLE;
	public boolean CAN_RETRIEVE_POST_QUEST_ITEMS;
	public boolean CAN_USE_CRACKER_ON_SELF;

	public int RING_OF_RECOIL_LIMIT;
	public int RING_OF_FORGING_USES;
	public int DWARVEN_RING_USES;
	public int DWARVEN_RING_BONUS;
	public List<String> valuableDrops;
	public boolean WANT_CUSTOM_UI;
	public int CHARACTER_CREATION_MODE;
	public int LOCATION_DATA;
	public boolean WANT_FIXED_BROKEN_MECHANICS;
	public boolean WANT_DECORATED_MOD_ROOM;
	public boolean WANT_AUTO_SERVER_SHUTDOWN;
	public int RESTART_HOUR;
	public boolean WANT_RESET_EVENT = false;
	public boolean CHAR_NAME_CAN_CONTAIN_MOD;
	public boolean CHAR_NAME_CAN_EQUAL_GLOBAL;
	public boolean WANT_CHAIN_LEGS;
	public boolean WANT_APOTHECARY_QOL;
	public boolean WANT_CERT_AS_NOTES;
	// public boolean CHECK_ADMIN_IP;
	// public String ADMIN_IP;
	// public List<String> adminIp;
	public boolean WANT_RANGED_FACE_PLAYER = false;
	public boolean ESTERS_BUNNIES_EVENT = false;
	public int BASED_MAP_DATA = 64;
	public int BASED_CONFIG_DATA = 85;

	public boolean GATHER_TOOL_ON_SCENERY;
	public boolean COIN_BANK;
	public boolean INFLUENCE_INSTEAD_QP;
	public boolean USES_CLASSES;
	public boolean USES_PK_MODE;
	public boolean ARRIVE_LUMBRIDGE;
	public boolean SCALED_WOODCUT_XP;
	public boolean OLD_PRAY_XP;
	public boolean DIVIDED_GOOD_EVIL;
	public boolean LACKS_PRAYERS;
	public boolean HAS_FEAR_SPELL;
	public boolean WAIT_TO_REBOOST;
	public boolean NO_LEVEL_REQUIREMENT_WIELD;
	public boolean FERMENTED_WINE;
	public boolean OLD_QUEST_MECHANICS;
	public boolean CAN_INFLUENCE_NPCS;
	public boolean NO_CONFIRM_TRADES;
	public boolean SHORT_MAX_STACKS;
	public boolean BLOCK_USE_MAGIC_IN_COMBAT;
	public boolean RAPID_CAST_SPELLS;
	public boolean MEAT_HEAL_LEVEL_DEPENDENT;
	public boolean ONLY_REGULAR_BONES;
	public boolean SHARED_GATHERING_RESOURCES;
	public boolean HAS_PLAYER_OWNED_HOUSES;

	public ImmutableList<String> IGNORED_NETWORK_EXCEPTIONS =
		ImmutableList.of("An existing connection was forcibly closed by the remote host",
			"An established connection was aborted by the software in your host machine");

	public ImmutableList<String> NETWORK_CONNECTION_RESET_EXCEPTIONS =
		ImmutableList.of("Connection reset by peer", "Connection reset");

	public String configFile;
	private final String[] deprecatedKeys = new String[]{
		"bank_size", "want_password_massage", "mysql_db",
		"mysql_host", "mysql_user", "mysql_pass", "mysql_table_prefix"
	};

	/**
	 * @param file
	 * @throws IOException
	 * Config file for server configurations.
	 */
	private final YMLReader serverProps = new YMLReader();

	void initConfig(String defaultFile) throws IOException {
		// Try to load the connections.conf
		try {
			serverProps.loadFromYML("connections.conf");
			LOGGER.info("Loaded connections.conf");
		} catch (Exception e) {
			LOGGER.info("Properties file connections.conf not found, terminating server.");
			SystemUtil.exit(1);
		}

		configFile = ServerConfiguration.loadServerProps(serverProps, defaultFile);

		notifyDeprecated();

		// Database settings
		DB_TYPE = DatabaseType.resolveType(tryReadString("db_type").orElse(null));
		DB_NAME = tryReadString("db_name").orElse("preservation");
		DB_HOST = tryReadString("db_host").orElse("localhost:3306");
		DB_USER = tryReadString("db_user").orElse("root");
		DB_PASS = tryReadString("db_pass").orElse("root");
		DB_TABLE_PREFIX = tryReadString("db_table_prefix").orElse("");

		// World settings
		SERVER_NAME = tryReadString("server_name").orElse("Runescape");
		SERVER_NAME_WELCOME = tryReadString("server_name_welcome").orElse("Runescape Classic");
		WELCOME_TEXT = tryReadString("welcome_text").orElse("You need a members account to use this server");
		DISPLAY_LOGO_SPRITE = tryReadBool("display_logo_sprite").orElse(true);
		LOGO_SPRITE_ID = tryReadString("logo_sprite_id").orElse("2010");
		GAME_TICK = tryReadInt("game_tick").orElse(640);
		WALKING_TICK = tryReadInt("walking_tick").orElse(640);
		WANT_CUSTOM_WALK_SPEED = tryReadBool("want_custom_walking_speed").orElse(false);
		IDLE_TIMER = tryReadInt("idle_timer").orElse(300000); // 5 minutes
		AUTO_SAVE = tryReadInt("auto_save").orElse(30000); // 30 seconds
		CLIENT_VERSION = tryReadInt("client_version").orElse(10008); // version 10008
		SERVER_PORT = tryReadInt("server_port").orElse(43594);
		MAX_CONNECTIONS_PER_IP = tryReadInt("max_connections_per_ip").orElse(20);
		MAX_CONNECTIONS_PER_SECOND = tryReadInt("max_connections_per_second").orElse(20);
		MAX_PACKETS_PER_SECOND = tryReadInt("max_packets_per_second").orElse(50);
		MAX_LOGINS_PER_SECOND = tryReadInt("max_logins_per_second").orElse(1);
		MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES = tryReadInt("max_password_guesses_per_five_minutes").orElse(10);
		NETWORK_FLOOD_IP_BAN_MINUTES = tryReadInt("network_flood_ip_ban_minutes").orElse(5);
		int SUSPICIOUS_PLAYER_IP_BAN_MINUTES = tryReadInt("suspicious_player_ip_ban_minutes").orElse(60);
		String SERVER_LOCATION = tryReadString("server_location").orElse("USA");
		MAX_PLAYERS = tryReadInt("max_players").orElse(2000);
		MAX_PLAYERS_PER_IP = tryReadInt("max_players_per_ip").orElse(10);
		SESSION_ID_SENDER_TIMER = tryReadInt("session_id_sender_timer").orElse(640);
		AVATAR_GENERATOR = tryReadBool("avatar_generator").orElse(false);
		MEMBER_WORLD = tryReadBool("member_world").orElse(true);
		WANT_PCAP_LOGGING = tryReadBool("want_pcap_logging").orElse(false);
		WANT_THREADING__BREAK_PID_PRIORITY = tryReadBool("want_threading__break_pid_priority").orElse(false);
		WORLD_NUMBER = tryReadInt("world_number").orElse(1);
		PLAYER_LEVEL_LIMIT = tryReadInt("player_level_limit").orElse(99);
		WANT_EXPERIENCE_CAP = tryReadBool("want_experience_cap").orElse(false);
		EXPERIENCE_LIMIT = tryReadInt("experience_limit").orElse(-1);
		COMBAT_EXP_RATE = tryReadDouble("combat_exp_rate").orElse(1.0);
		SKILLING_EXP_RATE = tryReadDouble("skilling_exp_rate").orElse(1.0);
		WILDERNESS_BOOST = tryReadDouble("wilderness_boost").orElse(0.0);
		SKULL_BOOST = tryReadDouble("skull_boost").orElse(0.0);
		IS_DOUBLE_EXP = tryReadBool("double_exp").orElse(false);
		NPC_RESPAWN_MULTIPLIER = tryReadDouble("npc_respawn_multiplier").orElse(1.0);
		WANT_REGISTRATION_LIMIT = tryReadBool("want_registration_limit").orElse(false);
		PACKET_LIMIT = tryReadInt("packet_limit").orElse(100);
		GLOBAL_MESSAGE_COOLDOWN = tryReadInt("global_message_cooldown").orElse(0);
		int CONNECTION_LIMIT = tryReadInt("connection_limit").orElse(10);
		int CONNECTION_TIMEOUT = tryReadInt("connection_timeout").orElse(15);
		WANT_FATIGUE = tryReadBool("want_fatigue").orElse(true);
		STOP_SKILLING_FATIGUED = tryReadInt("stop_skilling_fatigued").orElse(1);
		AGGRO_RANGE = tryReadInt("aggro_range").orElse(1);
		CHARACTER_CREATION_MODE = tryReadInt("character_creation_mode").orElse(0);
		RING_OF_RECOIL_LIMIT = tryReadInt("ring_of_recoil_limit").orElse(40);
		RING_OF_FORGING_USES = tryReadInt("ring_of_forging_uses").orElse(75);
		DWARVEN_RING_USES = tryReadInt("dwarven_ring_uses").orElse(29);
		DWARVEN_RING_BONUS = tryReadInt("dwarven_ring_bonus").orElse(3);
		//CHECK_ADMIN_IP = tryReadBool("check_admin_ip").orElse(false);
		//ADMIN_IP = tryReadString("admin_ip").orElse("127.0.0.0,10.0.0.0,172.16.0.0,192.168.0.0");
		LOCATION_DATA = tryReadInt("location_data").orElse(0);
		WANT_FIXED_BROKEN_MECHANICS = tryReadBool("want_fixed_broken_mechanics").orElse(false);
		WANT_DECORATED_MOD_ROOM = tryReadBool("want_decorated_mod_room").orElse(false);
		WANT_AUTO_SERVER_SHUTDOWN = tryReadBool("want_auto_server_shutdown").orElse(false);
		RESTART_HOUR = tryReadInt("restart_hour").orElse(7);
		WANT_RESET_EVENT = tryReadBool("want_reset_event").orElse(false);
		BASED_MAP_DATA = tryReadInt("based_map_data").orElse(100);
		BASED_CONFIG_DATA = tryReadInt("based_config_data").orElse(85);
		FEATURES_SLEEP = tryReadBool("features_sleep").orElse(true);
		RESTRICT_ITEM_ID = tryReadInt("restrict_item_id").orElse(1289);

		// Client
		VIEW_DISTANCE = tryReadInt("view_distance").orElse(2);
		ZOOM_VIEW_TOGGLE = tryReadBool("zoom_view_toggle").orElse(false);
		FOG_TOGGLE = tryReadBool("fog_toggle").orElse(false);
		GROUND_ITEM_TOGGLE = tryReadBool("ground_item_toggle").orElse(false);
		MENU_COMBAT_STYLE_TOGGLE = tryReadBool("menu_combat_style_toggle").orElse(false);
		FIGHTMODE_SELECTOR_TOGGLE = tryReadBool("fightmode_selector_toggle").orElse(false);
		EXPERIENCE_COUNTER_TOGGLE = tryReadBool("experience_counter_toggle").orElse(false);
		EXPERIENCE_DROPS_TOGGLE = tryReadBool("experience_drops_toggle").orElse(false);
		ITEMS_ON_DEATH_MENU = tryReadBool("items_on_death_menu").orElse(false);
		SHOW_ROOF_TOGGLE = tryReadBool("show_roof_toggle").orElse(false);
		WANT_SKILL_MENUS = tryReadBool("want_skill_menus").orElse(false);
		WANT_QUEST_MENUS = tryReadBool("want_quest_menus").orElse(false);
		WANT_QUEST_STARTED_INDICATOR = tryReadBool("want_quest_started_indicator").orElse(false);
		WANT_HIDE_IP = tryReadBool("want_hide_ip").orElse(false);
		WANT_REMEMBER = tryReadBool("want_remember").orElse(false);
		FPS = tryReadInt("client_fps").orElse(50);
		WANT_EMAIL = tryReadBool("want_email").orElse(false);
		ALLOW_RESIZE = tryReadBool("allow_resize").orElse(false);
		LENIENT_CONTACT_DETAILS = tryReadBool("lenient_contact_details").orElse(false);
		CHAR_NAME_CAN_CONTAIN_MOD = tryReadBool("char_name_can_contain_mod").orElse(false);
		CHAR_NAME_CAN_EQUAL_GLOBAL = tryReadBool("char_name_can_equal_global").orElse(false);

		// Retro features
		GATHER_TOOL_ON_SCENERY = tryReadBool("gather_tool_on_scenery").orElse(false);
		COIN_BANK = tryReadBool("coin_bank").orElse(false);
		INFLUENCE_INSTEAD_QP = tryReadBool("influence_instead_qp").orElse(false);
		USES_CLASSES = tryReadBool("uses_classes").orElse(false);
		USES_PK_MODE = tryReadBool("uses_pk_mode").orElse(false);
		ARRIVE_LUMBRIDGE = tryReadBool("arrive_lumbridge").orElse(false);
		SCALED_WOODCUT_XP = tryReadBool("scaled_woodcut_xp").orElse(false);
		OLD_PRAY_XP = tryReadBool("old_pray_xp").orElse(false);
		DIVIDED_GOOD_EVIL = tryReadBool("divided_good_evil").orElse(false);
		LACKS_PRAYERS = tryReadBool("lacks_prayers").orElse(false);
		HAS_FEAR_SPELL = tryReadBool("has_fear_spell").orElse(false);
		WAIT_TO_REBOOST = tryReadBool("wait_to_reboost").orElse(false);
		NO_LEVEL_REQUIREMENT_WIELD = tryReadBool("no_level_requirement_wield").orElse(false);
		FERMENTED_WINE = tryReadBool("fermented_wine").orElse(false);
		OLD_QUEST_MECHANICS = tryReadBool("old_quest_mechanics").orElse(false);
		CAN_INFLUENCE_NPCS = tryReadBool("can_influence_npcs").orElse(false);
		NO_CONFIRM_TRADES = tryReadBool("no_confirm_trades").orElse(false);
		SHORT_MAX_STACKS = tryReadBool("short_max_stacks").orElse(false);
		BLOCK_USE_MAGIC_IN_COMBAT = tryReadBool("block_use_magic_in_combat").orElse(false);
		RAPID_CAST_SPELLS = tryReadBool("rapid_cast_spells").orElse(false);
		MEAT_HEAL_LEVEL_DEPENDENT = tryReadBool("meat_heal_level_dependent").orElse(false);
		ONLY_REGULAR_BONES = tryReadBool("only_regular_bones").orElse(false);
		SHARED_GATHERING_RESOURCES = tryReadBool("shared_gathering_resources").orElse(false);
		HAS_PLAYER_OWNED_HOUSES = tryReadBool("has_player_owned_houses").orElse(false);

		// Custom features
		WANT_CUSTOM_SPRITES = tryReadBool("custom_sprites").orElse(false);
		WANT_CUSTOM_UI = tryReadBool("want_custom_ui").orElse(false);
		WANT_CUSTOM_QUESTS = tryReadBool("want_custom_quests").orElse(false);
		SPAWN_AUCTION_NPCS = tryReadBool("spawn_auction_npcs").orElse(false);
		SPAWN_IRON_MAN_NPCS = tryReadBool("spawn_iron_man_npcs").orElse(false);
		SHOW_FLOATING_NAMETAGS = tryReadBool("show_floating_nametags").orElse(false);
		WANT_CLANS = tryReadBool("want_clans").orElse(false);
		WANT_KILL_FEED = tryReadBool("want_kill_feed").orElse(false);
		SIDE_MENU_TOGGLE = tryReadBool("side_menu_toggle").orElse(false);
		INVENTORY_COUNT_TOGGLE = tryReadBool("inventory_count_toggle").orElse(false);
		AUTO_MESSAGE_SWITCH_TOGGLE = tryReadBool("auto_message_switch_toggle").orElse(false);
		HIDE_LOGIN_BOX_TOGGLE = tryReadBool("hide_login_box_toggle").orElse(false);
		BATCH_PROGRESSION = tryReadBool("batch_progression").orElse(false);
		CUSTOM_IMPROVEMENTS = tryReadBool("custom_improvements").orElse(false);
		WANT_GLOBAL_CHAT = tryReadBool("want_global_chat").orElse(false);
		WANT_GLOBAL_FRIEND = tryReadBool("want_global_friend").orElse(false);
		WANT_EXPERIENCE_ELIXIRS = tryReadBool("want_experience_elixirs").orElse(false);
		WANT_KEYBOARD_SHORTCUTS = tryReadInt("want_keyboard_shortcuts").orElse(0);
		WANT_CUSTOM_RANK_DISPLAY = tryReadBool("want_custom_rank_display").orElse(false);
		CUSTOM_FIREMAKING = tryReadBool("custom_firemaking").orElse(false);
		WANT_DROP_X = tryReadBool("want_drop_x").orElse(false);
		WANT_EXP_INFO = tryReadBool("want_exp_info").orElse(false);
		WANT_WOODCUTTING_GUILD = tryReadBool("want_woodcutting_guild").orElse(false);
		WANT_MISSING_GUILD_GREETINGS = tryReadBool("want_missing_guild_greetings").orElse(false);
		WANT_DECANTING = tryReadBool("want_decanting").orElse(false);
		PLAYER_COMMANDS = tryReadBool("player_commands").orElse(false);
		NPC_BLOCKING = tryReadInt("npc_blocking").orElse(2);
		PLAYER_BLOCKING = tryReadInt("player_blocking").orElse(1);
		NPC_DONT_RETREAT = tryReadBool("npc_dont_retreat").orElse(false);
		MESSAGE_FULL_INVENTORY = tryReadBool("message_full_inventory").orElse(false);
		WANT_PETS = tryReadBool("want_pets").orElse(false);
		MAX_WALKING_SPEED = tryReadInt("max_walking_speed").orElse(1);
		MAX_TICKS_UNTIL_FULL_WALKING_SPEED = tryReadInt("max_ticks_until_full_walking_speed").orElse(0);
		SHOW_UNIDENTIFIED_HERB_NAMES = tryReadBool("show_unidentified_herb_names").orElse(false);
		FISHING_SPOTS_DEPLETABLE = tryReadBool("fishing_spots_depletable").orElse(false);
		IMPROVED_ITEM_OBJECT_NAMES = tryReadBool("improved_item_object_names").orElse(false);
		CRYSTAL_KEY_GIVES_XP = tryReadBool("crystal_key_gives_xp").orElse(false);
		LOOTED_CHESTS_STUCK = tryReadBool("looted_chests_stuck").orElse(false);
		WANT_RUNECRAFT = tryReadBool("want_runecraft").orElse(false);
		WANT_HARVESTING = tryReadBool("want_harvesting").orElse(false);
		WANT_CUSTOM_LEATHER = tryReadBool("want_custom_leather").orElse(false);
		WANT_CUSTOM_LANDSCAPE = tryReadBool("custom_landscape").orElse(false);
		WANT_EQUIPMENT_TAB = tryReadBool("want_equipment_tab").orElse(false);
		WANT_BANK_PRESETS = tryReadBool("want_bank_presets").orElse(false);
		MINING_ROCKS_EXTENDED = tryReadBool("mining_rocks_extended").orElse(false);
		WANT_NEW_RARE_DROP_TABLES = tryReadBool("want_new_rare_drop_tables").orElse(false);
		WANT_LEFTCLICK_WEBS = tryReadBool("want_leftclick_webs").orElse(false);
		WANT_IMPROVED_PATHFINDING = tryReadBool("want_improved_pathfinding").orElse(false);
		CAN_USE_CRACKER_ON_SELF = tryReadBool("can_use_cracker_on_self").orElse(false);
		FIX_OVERHEAD_CHAT = tryReadBool("fix_overhead_chat").orElse(false);
		WANT_BETTER_JEWELRY_CRAFTING = tryReadBool("want_better_jewelry_crafting").orElse(false);
		MORE_SHAFTS_PER_BETTER_LOG = tryReadBool("more_shafts_per_better_log").orElse(false);
		FASTER_YOHNUS = tryReadBool("faster_yohnus").orElse(false);
		WANT_CHAIN_LEGS = tryReadBool("want_chain_legs").orElse(false);
		WANT_APOTHECARY_QOL = tryReadBool("want_apothecary_qol").orElse(false);
		WANT_RANGED_FACE_PLAYER = tryReadBool("want_ranged_face_player").orElse(false);
		WANT_POISON_NPCS = tryReadBool("want_poison_npcs").orElse(false);
		WANT_OPENPK_POINTS = tryReadBool("want_openpk_points").orElse(false);
		OPENPK_POINTS_TO_GP_RATIO = tryReadInt("openpk_points_to_gp_ratio").orElse(1);
		WANT_PK_BOTS = tryReadBool("want_pk_bots").orElse(false);
		RESPAWN_LOCATION_X = tryReadInt("respawn_location_x").orElse(120);
		RESPAWN_LOCATION_Y = tryReadInt("respawn_location_y").orElse(648);

		// Party settings
		WANT_PARTIES = tryReadBool("want_parties").orElse(false);
		PARTY_ADDITIONAL_XP_PERCENT_PER_PLAYER = tryReadDouble("party_additional_xp_percent_per_player")
			.orElse(0.1);
		PARTY_DISTANCE_PERCENT_DECREASE = tryReadDouble("party_distance_percent_decrease")
			.orElse(0.05);
		PARTY_SAVE_XP_FOR_SKILLER_PERCENT = tryReadDouble("party_save_xp_for_skiller_percent")
			.orElse(0.1);
		PARTY_SHARE_MAX_X = tryReadInt("party_share_max_x").orElse(20);
		PARTY_SHARE_MAX_Y = tryReadInt("party_share_max_y").orElse(20);
		PARTY_SHARE_INFINITE_RANGE = tryReadBool("party_share_infinite_range").orElse(false);
		PARTY_SHARE_WITH_SAME_IP = tryReadBool("party_share_with_same_ip").orElse(false);
		PARTY_SHARE_SIZE_ALGORITHM = tryReadString("party_share_size_algorithm")
			.orElse("exponential");
		PARTY_SHARE_DISTANCE_ALGORITHM = tryReadString("party_share_distance_algorithm")
			.orElse("exponential");
		PARTY_MAX_SIZE_FOR_ADDITIONAL_XP = tryReadInt("party_max_size_for_additional_xp")
			.orElse(1000);
		PARTY_IRON_MAN_CAN_SHARE = tryReadBool("party_iron_man_can_share").orElse(false);

		// Discord settings
		DISCORD_AUCTION_WEBHOOK_URL = tryReadString("discord_auction_webhook_url").orElse("null");
		DISCORD_MONITORING_WEBHOOK_URL = tryReadString("discord_monitoring_webhook_url").orElse("null");
		WANT_DISCORD_AUCTION_UPDATES = tryReadBool("want_discord_auction_updates").orElse(false);
		WANT_DISCORD_MONITORING_UPDATES = tryReadBool("want_discord_monitoring_updates").orElse(false);
		WANT_DISCORD_BOT = tryReadBool("want_discord_bot").orElse(false);
		CROSS_CHAT_CHANNEL = tryReadInt("cross_chat_channel").orElse(0);

		// Bank
		RIGHT_CLICK_BANK = tryReadBool("right_click_bank").orElse(false);
		WANT_CUSTOM_BANKS = tryReadBool("want_custom_banks").orElse(false);
		WANT_BANK_PINS = tryReadBool("want_bank_pins").orElse(false);
		WANT_BANK_NOTES = tryReadBool("want_bank_notes").orElse(false);
		WANT_CERT_DEPOSIT = tryReadBool("want_cert_deposit").orElse(false);
		WANT_CERTER_BANK_EXCHANGE = tryReadBool("want_certer_bank_exchange").orElse(false);
		WANT_CERT_AS_NOTES = tryReadBool("want_cert_as_notes").orElse(false);

		//Shop
		RIGHT_CLICK_TRADE = tryReadBool("right_click_trade").orElse(false);

		// NPC kills
		NPC_KILL_LIST = tryReadBool("npc_kill_list").orElse(false);
		NPC_KILL_MESSAGES = tryReadBool("npc_kill_messages").orElse(false);
		NPC_KILL_MESSAGES_FILTER = tryReadBool("npc_kill_messages_filter").orElse(false);
		NPC_KILL_MESSAGES_NPCs = tryReadString("npc_kill_messages_npcs").orElse("King Black Dragon,Black Dragon");
		NPC_KILL_LOGGING = tryReadBool("npc_kill_logging").orElse(true);

		// Valuable drops
		VALUABLE_DROP_MESSAGES = tryReadBool("valuable_drop_messages").orElse(false);
		VALUABLE_DROP_RATIO = tryReadDouble("valuable_drop_ratio").orElse(0.0);
		VALUABLE_DROP_EXTRAS = tryReadBool("valuable_drop_extras").orElse(false);
		String VALUABLE_DROP_ITEMS = tryReadString("valuable_drop_items").orElse("Half of a key,Half Dragon Square Shield");
		valuableDrops = Arrays.asList(VALUABLE_DROP_ITEMS.split(","));

		// Glitch checks
		STRICT_CHECK_ALL = tryReadBool("strict_check_all").orElse(true);
		STRICT_PDART_CHECK = tryReadBool("strict_pdart_check").orElse(true);
		STRICT_PKNIFE_CHECK = tryReadBool("strict_pknife_check").orElse(true);
		STRICT_PSPEAR_CHECK = tryReadBool("strict_pspear_check").orElse(true);
		LOOSE_SHALLOW_WATER_CHECK = tryReadBool("loose_shallow_water_check").orElse(false);

		// Custom quests and minigames
		WANT_GIANNE_BADGE = tryReadBool("want_gianne_badge").orElse(false);
		WANT_BLURBERRY_BADGE = tryReadBool("want_blurberry_badge").orElse(false);
		WANT_EXTENDED_CATS_BEHAVIOR = tryReadBool("want_extended_cats_behavior").orElse(false);
		WANT_BARTER_WORMBRAINS = tryReadBool("want_barter_wormbrains").orElse(false);
		LOCKED_POST_QUEST_REGIONS_ACCESSIBLE = tryReadBool("locked_post_quest_regions_accessible").orElse(false);
		CAN_RETRIEVE_POST_QUEST_ITEMS = tryReadBool("can_retrieve_post_quest_items").orElse(false);
		ESTERS_BUNNIES_EVENT = tryReadBool("esters_bunnies").orElse(false);
		// adminIp = Arrays.asList(ADMIN_IP.split(","));
	}

	protected static String loadServerProps(YMLReader reader, String defaultFile) {
		// Always try to load from local.conf first.
		try {
			reader.loadFromYML("local.conf");
			return "local.conf";
		} catch (Exception e) { // Otherwise try to load from command line.
			try {
				reader.loadFromYML(defaultFile);
				LOGGER.info("Properties file local.conf not found, loading properties from " + defaultFile);
				return defaultFile;
			} catch (Exception ex) { // If not, we use the defaults listed below.
				LOGGER.info("Properties file local.conf not found, no other properties file provided." +
					" Using default properties.");
				return "Default values";
			}
		}
	}

	// Notify the user if they have any deprecated
	// keys in their config files.
	private void notifyDeprecated() {
		for (String deprecatedKey : deprecatedKeys) {
			if (serverProps.keyExists(deprecatedKey)) {
				LOGGER.info(deprecatedKey + " is a deprecated key. You can remove it from " +
					configFile + ".");
			}
		}
	}

	// Attempt to read in an integer property
	// If we can't parse it, we terminate the server. If we
	// cannot find it, we return an empty optional so that
	// we can use the default.
	private Optional<Integer> tryReadInt(String key) {
		try {
			if (serverProps.keyExists(key))
				return Optional.of(Integer.parseInt(serverProps.getAttribute(key)));
		}
		catch (NumberFormatException ex) {
			LOGGER.error("Error reading value for key \"" + key + "\" " + ex.getMessage() +
				". Should be an integer. Terminating server.");
			SystemUtil.exit(1);
		}
		LOGGER.info("Key: \"" + key + "\" does not exist in the provided conf file. Using default.");
		return Optional.empty();
	}

	// Attempt to read in an double property
	// If we can't parse it, we terminate the server. If we
	// cannot find it, we return an empty optional so that
	// we can use the default.
	private Optional<Double> tryReadDouble(String key) {
		try {
			if (serverProps.keyExists(key))
				return Optional.of(Double.parseDouble(serverProps.getAttribute(key)));
		}
		catch (NumberFormatException ex) {
			LOGGER.error("Error reading value for key \"" + key + "\" " + ex.getMessage() +
				". Should be a double. Terminating server.");
			SystemUtil.exit(1);
		}
		LOGGER.info("Key: \"" + key + "\" does not exist in the provided conf file. Using default.");
		return Optional.empty();
	}

	// Attempt to read in an bool property
	// If we cannot read the value (it isn't true or false), we terminate the server.
	// If we cannot find the key, we return an
	// empty optional so that we can use the default.
	private Optional<Boolean> tryReadBool(String key) {
		if (serverProps.keyExists(key)) {
			String value = serverProps.getAttribute(key);
			// If the value we read in is either true or false...
			if (value.compareToIgnoreCase("true") == 0 || value.compareToIgnoreCase("false") == 0) {
				return Optional.of(Boolean.parseBoolean(value));
			}
			else {
				LOGGER.error("Error reading value for key \"" + key + "\" for input string \"" +
					serverProps.getAttribute(key) + ".\"" +
					" Should be true or false. Terminating server.");
				SystemUtil.exit(1);
			}
		}
		LOGGER.info("Key: \"" + key + "\" does not exist in the provided conf file. Using default.");
		return Optional.empty();
	}

	// Attempt to read a string property
	// If it doesn't exist, we return an empty
	// optional so we can use the default.
	private Optional<String> tryReadString(String key) {
		if(serverProps.keyExists(key)) {
			return Optional.of(serverProps.getAttribute(key));
		}
		LOGGER.info("Key: \"" + key + "\" does not exist in the provided conf file. Using default.");
		return Optional.empty();
	}
}
