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
    
    public static final int NOTE_ITEM_ID_BASE = 10000;
	
	public static final String WILDERNESS_ENTRY_BLOCKED_MESSAGE = "You may only enter the wilderness on " + Config.ALLOWED_CONCURRENT_IPS_IN_WILDERNESS + " character(s) at a time.";
	
	public static void initConfig(File file) throws IOException {
		SERVER_NAME    = "Open RSC";
		PREFIX         = "@gre@OpenRSC:@whi@ "; // Prefix that is sent before every custom (non-RSC) message (such as commands).
		START_TIME     = System.currentTimeMillis();
		LOGGING        = true; // Should in-game events be logged to the logs database?
		MAX_PLAYERS    = 1000;
		COMMAND_PREFIX = "::";

		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(file));
		SHUTDOWN_TIME_MILLIS = Integer.parseInt(props.getProperty("shutdown_time_millis"));
		SERVER_VERSION = Integer.parseInt(props.getProperty("version"));
		SERVER_IP      = props.getProperty("ip");
		SERVER_PORT    = Integer.parseInt(props.getProperty("port"));
		WEB_PORT       = Integer.parseInt(props.getProperty("webport"));
		
		DB_HOST  = props.getProperty("dbhost");
		DB_NAME  = props.getProperty("dbname");
		DB_LOGIN = props.getProperty("dblogin");
		DB_PASS  = props.getProperty("dbpass");
                
                HMAC_PASS  = props.getProperty("hmac_pass");
		
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

	public static String SERVER_IP, COMMAND_PREFIX, SERVER_NAME, PREFIX, DB_HOST, DB_NAME, DB_LOGIN, DB_PASS, HMAC_PASS, CONFIG_DB_NAME, LOG_DB_NAME, TOOLS_DB_NAME, IRC_SERVER, IRC_CHANNEL, IRC_USERNAME, IRC_PASSWORD, IRC_GREET_1, IRC_GREET_2, IRC_GREET_3;
	public static int WEB_PORT, SERVER_PORT, SERVER_VERSION, MAX_PLAYERS;
	public static float combat_xp, combat_xp_sub, skill_xp, skill_xp_sub, wild_xp_bonus, skulled_xp_bonus;
	public static long START_TIME;
	public static boolean LOGGING, IRC, IRC_DEBUG, IRC_NOTIFY, PK_MODE;
	
	public static boolean ALLOW_WEAKENS = true, ALLOW_GODSPELLS = true;
    
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
		public static final int ROMEO_AND_JULIET = 10;
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
        
        public static final int JOIN_BLACKARM_GANG = 50;
        public static final int JOIN_PHOENIX_GANG = 51;
        
        public static final int TUTORIAL_ISLAND = 100;
	}
}