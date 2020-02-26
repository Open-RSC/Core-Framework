package com.openrsc.server;

import com.google.common.collect.ImmutableList;
import com.openrsc.server.util.YMLReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// Basically Rewritten By Ryan

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
	private int WORLD_NUMBER;
	public int CLIENT_VERSION;
	public int MAX_PLAYERS;
	public int MAX_PLAYERS_PER_IP;
	int SERVER_PORT;
	int IDLE_TIMER;
	int AUTO_SAVE;
	private String SERVER_LOCATION;
	private String HMAC_PRIVATE_KEY;
	public int RESTART_HOUR;
	public int RESTART_HOUR_2;
	public int RESTART_MINUTE;
	public int RESTART_MINUTE_2;
	public int RESTART_DELAY;
	public int RESTART_DELAY_2;
	public int AGGRO_RANGE;
	public String MYSQL_HOST;
	public String MYSQL_DB;
	public String MYSQL_USER;
	public String MYSQL_PASS;
	public String MYSQL_TABLE_PREFIX;
	public int PLAYER_LEVEL_LIMIT;
	public double COMBAT_EXP_RATE;
	public double SKILLING_EXP_RATE;
	public double WILDERNESS_BOOST;
	public double SKULL_BOOST;
	public int VIEW_DISTANCE;
	public String LOGO_SPRITE_ID;
	public int NPC_BLOCKING;
	public static int MAX_CONNECTIONS_PER_IP;
	public static int MAX_CONNECTIONS_PER_SECOND;
	public static int MAX_PACKETS_PER_SECOND;
	public static int MAX_LOGINS_PER_SECOND;
	public static int MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES;
	public static int NETWORK_FLOOD_IP_BAN_MINUTES;
	private static int SUSPICIOUS_PLAYER_IP_BAN_MINUTES;

	// Location of the server conf files.
	public String CONFIG_DIR = "conf" + File.separator + "server";

	private long START_TIME;
	public boolean AVATAR_GENERATOR; // Not sent to client
	public boolean IS_DOUBLE_EXP;
	public boolean DISPLAY_LOGO_SPRITE;
	public boolean SPAWN_AUCTION_NPCS;
	public boolean SPAWN_IRON_MAN_NPCS;
	public boolean WANT_PK_BOTS;
	public boolean SHOW_FLOATING_NAMETAGS;
	public boolean WANT_CLANS;
	public boolean WANT_KILL_FEED;
	public static boolean FOG_TOGGLE;
	public boolean GROUND_ITEM_TOGGLE;
	public boolean AUTO_MESSAGE_SWITCH_TOGGLE;
	public boolean BATCH_PROGRESSION;
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
	private String VALUABLE_DROP_ITEMS;
	public boolean WANT_CUSTOM_RANK_DISPLAY;
	public boolean RIGHT_CLICK_BANK;
	public boolean FIX_OVERHEAD_CHAT;
	public boolean WANT_FATIGUE;
	public int STOP_SKILLING_FATIGUED;
	public boolean WANT_CUSTOM_SPRITES;
	public boolean WANT_CUSTOM_LANDSCAPE;
	public boolean PLAYER_COMMANDS;
	public boolean WANT_PETS;
	public boolean AUTO_SERVER_RESTART;
	public boolean AUTO_SERVER_RESTART_2;
	public int MAX_WALKING_SPEED;
	public boolean SHOW_UNIDENTIFIED_HERB_NAMES;
	public boolean WANT_QUEST_STARTED_INDICATOR;
	public boolean WANT_CUSTOM_QUESTS;
	public boolean FISHING_SPOTS_DEPLETABLE;
	public boolean IMPROVED_ITEM_OBJECT_NAMES;
	public boolean CRYSTAL_KEY_GIVES_XP;
	public boolean LOOTED_CHESTS_STUCK;
	public boolean WANT_RUNECRAFTING;
	public boolean WANT_HARVESTING;
	public boolean WANT_DISCORD_AUCTION_UPDATES;
	public String DISCORD_AUCTION_WEBHOOK_URL;
	public boolean WANT_DISCORD_MONITORING_UPDATES;
	public String DISCORD_MONITORING_WEBHOOK_URL;
	public boolean WANT_DISCORD_BOT;
	public long CROSS_CHAT_CHANNEL;
	public boolean WANT_EQUIPMENT_TAB;
	public boolean WANT_BANK_PRESETS;
	public boolean WANT_PARTIES;
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
	public int PACKET_LIMIT;
	private int CONNECTION_LIMIT;
	private int CONNECTION_TIMEOUT;
	//quest-minigame related
	private boolean WANT_GIANNE_BADGE;
	private boolean WANT_BLURBERRY_BADGE;
	public boolean WANT_SHOW_KITTENS_CIVILLIAN;
	public boolean WANT_BARTER_WORMBRAINS;
	public boolean LOCKED_POST_QUEST_REGIONS_ACCESSIBLE;
	private boolean CAN_RETRIEVE_POST_QUEST_ITEMS;
	public boolean CAN_USE_CRACKER_ON_SELF;

	public int RING_OF_RECOIL_LIMIT;
	public int RING_OF_FORGING_USES;
	public int DWARVEN_RING_USES;
	public int DWARVEN_RING_BONUS;
	public List<String> valuableDrops;
	public boolean WANT_CUSTOM_UI;
	public int CHARACTER_CREATION_MODE;

	public ImmutableList<String> IGNORED_NETWORK_EXCEPTIONS =
		ImmutableList.of("An existing connection was forcibly closed by the remote host",
			"An established connection was aborted by the software in your host machine",
			"Connection reset by peer");
	/**
	 * @param file
	 * @throws IOException
	 * Config file for server configurations.
	 */
	private YMLReader serverProps = new YMLReader();

	void initConfig(String defaultFile) throws IOException {
		try {
			// connections.conf file should ALWAYS be here!
			serverProps.loadFromYML("connections.conf");
			LOGGER.info("Loaded connections.conf");
		}

		catch (Exception e) {
			LOGGER.catching(e);
			System.exit(1);
		}

		try { // Always try to load local.conf first
			serverProps.loadFromYML("local.conf");

		} catch (Exception e) { // Otherwise default to default.conf
			serverProps.loadFromYML(defaultFile);
			LOGGER.info("Properties file local.conf not found, loading properties from " + defaultFile);
		}

		// Database settings
		MYSQL_DB = tryReadString("mysql_db").orElse("openrsc");
		MYSQL_HOST = tryReadString("mysql_host").orElse("localhost:3306");
		MYSQL_USER = tryReadString("mysql_user").orElse("root");
		MYSQL_PASS = tryReadString("mysql_pass").orElse("root");
		MYSQL_TABLE_PREFIX = tryReadString("mysql_table_prefix").orElse("openrsc_");

		// Discord settings
		DISCORD_AUCTION_WEBHOOK_URL = tryReadString("discord_auction_webhook_url").orElse("null");
		DISCORD_MONITORING_WEBHOOK_URL = tryReadString("discord_monitoring_webhook_url").orElse("null");
		WANT_DISCORD_AUCTION_UPDATES = tryReadBool("want_discord_auction_updates").orElse(false);
		WANT_DISCORD_MONITORING_UPDATES = tryReadBool("want_discord_monitoring_updates").orElse(false);
		WANT_DISCORD_BOT = tryReadBool("want_discord_bot").orElse(false);
		CROSS_CHAT_CHANNEL = tryReadInt("cross_chat_channel").orElse(0);

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
		CLIENT_VERSION = tryReadInt("client_version").orElse(6);
		SERVER_PORT = tryReadInt("server_port").orElse(43594);
		MAX_CONNECTIONS_PER_IP = tryReadInt("max_connections_per_ip").orElse(10);
		MAX_CONNECTIONS_PER_SECOND = tryReadInt("max_connections_per_second").orElse(10);
		MAX_PACKETS_PER_SECOND = tryReadInt("max_packets_per_second").orElse(1000);
		MAX_LOGINS_PER_SECOND = tryReadInt("max_logins_per_second").orElse(1);
		MAX_PASSWORD_GUESSES_PER_FIVE_MINUTES = tryReadInt("max_password_guesses_per_five_minutes").orElse(10);
		NETWORK_FLOOD_IP_BAN_MINUTES = tryReadInt("network_flood_ip_ban_minutes").orElse(20);
		SUSPICIOUS_PLAYER_IP_BAN_MINUTES = tryReadInt("suspicious_player_ip_ban_minutes").orElse(60);
		SERVER_LOCATION = tryReadString("server_location").orElse("USA");
		MAX_PLAYERS = tryReadInt("max_players").orElse(100);
		MAX_PLAYERS_PER_IP = tryReadInt("max_players_per_ip").orElse(10);
		AVATAR_GENERATOR = tryReadBool("avatar_generator").orElse(false);
		MEMBER_WORLD = tryReadBool("member_world").orElse(true);
		WORLD_NUMBER = tryReadInt("world_number").orElse(1);
		PLAYER_LEVEL_LIMIT = tryReadInt("player_level_limit").orElse(99);
		COMBAT_EXP_RATE = tryReadDouble("combat_exp_rate").orElse(1.0);
		SKILLING_EXP_RATE = tryReadDouble("skilling_exp_rate").orElse(1.0);
		WILDERNESS_BOOST = tryReadDouble("wilderness_boost").orElse(0.0);
		SKULL_BOOST = tryReadDouble("skull_boost").orElse(0.0);
		IS_DOUBLE_EXP = tryReadBool("double_xp").orElse(false);
		// BANK_SIZE??
		HMAC_PRIVATE_KEY = tryReadString("HMAC_PRIVATE_KEY").orElse("root");
		WANT_REGISTRATION_LIMIT = tryReadBool("want_registration_limit").orElse(false);
		PACKET_LIMIT = tryReadInt("packet_limit").orElse(100);
		CONNECTION_LIMIT = tryReadInt("connection_limit").orElse(10);
		CONNECTION_TIMEOUT = tryReadInt("connection_timeout").orElse(15);
		WANT_FATIGUE = tryReadBool("want_fatigue").orElse(true);
		STOP_SKILLING_FATIGUED = tryReadInt("stop_skilling_fatigued").orElse(1);
		AUTO_SERVER_RESTART = tryReadBool("auto_server_restart").orElse(false);
		RESTART_HOUR = tryReadInt("restart_hour").orElse(23);
		RESTART_MINUTE = tryReadInt("restart_minute").orElse(55);
		RESTART_DELAY = tryReadInt("restart_delay").orElse(300);
		AUTO_SERVER_RESTART_2 = tryReadBool("auto_server_restart_2").orElse(false);
		RESTART_HOUR_2 = tryReadInt("restart_hour_2").orElse(11);
		RESTART_MINUTE_2 = tryReadInt("restart_minute_2").orElse(55);
		RESTART_DELAY_2 = tryReadInt("restart_delay_2").orElse(300);
		AGGRO_RANGE = tryReadInt("aggro_range").orElse(1);
		CHARACTER_CREATION_MODE = tryReadInt("character_creation_mode").orElse(0);
		RING_OF_RECOIL_LIMIT = tryReadInt("ring_of_recoil_limit").orElse(40);
		RING_OF_FORGING_USES = tryReadInt("ring_of_forging_uses").orElse(75);
		DWARVEN_RING_USES = tryReadInt("dwarven_ring_uses").orElse(29);
		DWARVEN_RING_BONUS = tryReadInt("dwarven_ring_bonus").orElse(3);

		valuableDrops = Arrays.asList(VALUABLE_DROP_ITEMS.split(","));
	}

	// Attempt to read in an integer property
	// If we can't parse it or find it, we return an
	// empty optional so that we can use the default.
	private Optional<Integer> tryReadInt(String key) {
		try {
			if (serverProps.keyExists(key))
				return Optional.of(Integer.parseInt(serverProps.getAttribute(key)));
		}
		catch (NumberFormatException ex) {
			LOGGER.info("Error reading value for key \"" + key + "\" " + ex.getMessage() +
				". Should be integer. Using default.");
		}
		return Optional.empty();
	}

	private Optional<Double> tryReadDouble(String key) {
		try {
			if (serverProps.keyExists(key))
				return Optional.of(Double.parseDouble(serverProps.getAttribute(key)));
		}
		catch (NumberFormatException ex) {
			LOGGER.info("Error reading value for key \"" + key + "\" " + ex.getMessage() +
				". Should be double. Using default.");
		}
		return Optional.empty();
	}

	// Attempt to read in an integer property
	// If the value isn't true or false, or we can't find it, we return an
	// empty optional so that we can use the default.
	private Optional<Boolean> tryReadBool(String key) {
		if (serverProps.keyExists(key)) {
			String value = serverProps.getAttribute(key);
			// If the value we read in is either true or false...
			if (value.compareToIgnoreCase("true") == 0 || value.compareToIgnoreCase("false") == 0) {
				return Optional.of(Boolean.parseBoolean(value));
			}
			else {
				LOGGER.info("Error reading value for key \"" + key + ".\"" +
					" Should be true or false. Using default.");
			}
		}
		return Optional.empty();
	}

	// Attempt to read a string property
	// If it doesn't exist, we return an empty
	// optional so we can use the default.
	private Optional<String> tryReadString(String key) {
		if(serverProps.keyExists(key)) {
			return Optional.of(serverProps.getAttribute(key));
		}
		return Optional.empty();
	}
}
