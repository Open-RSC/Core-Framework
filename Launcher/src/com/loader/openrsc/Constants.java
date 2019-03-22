package com.loader.openrsc;

public class Constants {

	// ORSC
	public static final String ORSC_GAME_NAME = "Open RSC";
	public static final String ORSC_WORLD_STATS_URL = "https://openrsc.com/stats"; // Used for the world statistics
	static final String ORSC_SERVER_DOMAIN = "game.openrsc.com"; // Used for the server status display
	static final int ORSC_SERVER_PORT = 43594;

	// RSCC
	public static final String RSCC_GAME_NAME = "RSC Cabbage";
	public static final String RSCC_WORLD_STATS_URL = "https://cabbage.openrsc.com/stats"; // Used for the world statistics
	static final String RSCC_SERVER_DOMAIN = "cabbage.openrsc.com"; // Used for the server status display
	static final int RSCC_SERVER_PORT = 43594;

	// Launcher + Cache
	public static final String Title = "Open RSC Game Launcher";
	public static final String BASE_URL = "https://game.openrsc.com/"; // Cache and client jar download locations depend on this
	public static final String CONF_DIR = "Cache";
	public static final String CLIENT_FILENAME = "Open_RSC_Client.jar";
	public static final String CACHE_URL = BASE_URL + "downloads/cache/";
	public static final Double VERSION_NUMBER = 20190310.195300; //YYYYMMDD.HHMMSS format
	//public static final Double VERSION_NUMBER = 20190321.180000; //YYYYMMDD.HHMMSS format
	public static final String VERSION_UPDATE_URL = "https://gitlab.openrsc.com/open-rsc/Game/raw/2.0.0/Launcher/src/com/loader/openrsc/Constants.java";
	public static final String UPDATE_JAR_URL = "https://game.openrsc.com/downloads/OpenRSC.jar";
	public static final String JAR_FILENAME = "OpenRSC.jar";
}
