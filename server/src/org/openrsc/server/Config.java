package org.openrsc.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	public static String STAFF_TELEPORT_LOCATION_DATABASE = "`openrsc`.`teleport_locations`";
	public static String AUCTIONS_TABLE = "`openrsc`.`rscd_auctions`";
	public static int SHUTDOWN_TIME_MILLIS;
	public static int RUNECRAFTING_AMOUNT_MULTIPLIER;
	/** The maximum number of players allowed in the wilderness per IP */
	public static int ALLOWED_CONCURRENT_IPS_IN_WILDERNESS = 2;
	
	public static final String WILDERNESS_ENTRY_BLOCKED_MESSAGE = "You may only enter the wilderness on " + Config.ALLOWED_CONCURRENT_IPS_IN_WILDERNESS + " character(s) at a time.";
	
	public static String AVATAR_DIR = "/usr/share/nginx/html/";
	
	public static void initConfig(File file) throws IOException {
		SERVER_NAME    = "Open RSC";
		PREFIX         = "@gre@Open RSC:@whi@ "; // Prefix that is sent before every custom (non-RSC) message (such as commands).
		START_TIME     = System.currentTimeMillis();
		LOGGING        = true; // Should in-game events be logged to the logs database?
		MAX_PLAYERS    = 1000;
		COMMAND_PREFIX = "::";

		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(file));
		SHUTDOWN_TIME_MILLIS = Integer.parseInt(props.getProperty("shutdown_time_millis"));

		String dir = props.getProperty("avatar_dir");

		if(dir != null)
		{
			AVATAR_DIR = dir;
		}
		
		SERVER_VERSION = Integer.parseInt(props.getProperty("version"));
		SERVER_IP      = props.getProperty("ip");
		SERVER_PORT    = Integer.parseInt(props.getProperty("port"));
		WEB_PORT       = Integer.parseInt(props.getProperty("webport"));
		
		DB_HOST  = props.getProperty("dbhost");
		DB_NAME  = props.getProperty("dbname");
		DB_LOGIN = props.getProperty("dblogin");
		DB_PASS  = props.getProperty("dbpass");
		
		CONFIG_DB_NAME = props.getProperty("configdbname");
		LOG_DB_NAME    = props.getProperty("logdbname");
		TOOLS_DB_NAME = props.getProperty("toolsdbname");
		
		combat_xp = Float.parseFloat(props.getProperty("combat_xp"));
		combat_xp_sub = Float.parseFloat(props.getProperty("combat_xp_sub"));
		skill_xp = Float.parseFloat(props.getProperty("skill_xp"));
		skill_xp_sub = Float.parseFloat(props.getProperty("skill_xp_sub"));
		wild_xp_bonus = Float.parseFloat(props.getProperty("wild_xp_bonus"));
		skulled_xp_bonus = Float.parseFloat(props.getProperty("skulled_xp_bonus"));
		
		
		RUNECRAFTING_AMOUNT_MULTIPLIER    = Integer.parseInt(props.getProperty("runecrafting_amount_multiplier"));
		
		if(props.containsKey("staff_teleport_locations_db"))
		{
			STAFF_TELEPORT_LOCATION_DATABASE = props.getProperty("staff_teleport_locations_db");
		}
		props.clear();
	}

	public static String SERVER_IP, COMMAND_PREFIX, SERVER_NAME, PREFIX, DB_HOST, DB_NAME, DB_LOGIN, DB_PASS, CONFIG_DB_NAME, LOG_DB_NAME, TOOLS_DB_NAME, IRC_SERVER, IRC_CHANNEL, IRC_USERNAME, IRC_PASSWORD, IRC_GREET_1, IRC_GREET_2, IRC_GREET_3;
	public static int WEB_PORT, SERVER_PORT, SERVER_VERSION, MAX_PLAYERS;
	public static float combat_xp, combat_xp_sub, skill_xp, skill_xp_sub, wild_xp_bonus, skulled_xp_bonus;
	public static long START_TIME;
	public static boolean LOGGING, IRC, IRC_DEBUG, IRC_NOTIFY, PK_MODE;
	
	public static boolean ALLOW_WEAKENS = false, ALLOW_GODSPELLS = false;
}