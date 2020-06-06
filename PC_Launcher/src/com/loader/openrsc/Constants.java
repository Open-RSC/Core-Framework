package com.loader.openrsc;

import java.io.File;

public class Constants {
	public static final String RSCC_WORLD_STATS_URL = "https://runescapeclassic.dev"; // Used for the world statistics
	public static final String ORSC_WORLD_STATS_URL = "https://runescapeclassic.dev"; // Used for the world statistics
	public static final String RSCP_WORLD_STATS_URL = "https://runescapeclassic.dev"; // Used for the world statistics
	public static final String DEV_WORLD_STATS_URL = "https://runescapeclassic.dev"; // Used for the world statistics
	public static final String Title = "Open RSC Game Launcher";
	public static final String BASE_URL = "http://game.openrsc.com/static/"; // Cache and client jar download locations depend on this
	public static final String CONF_DIR = "Cache";
	public static final String CLIENT_FILENAME = "Open_RSC_Client.jar";
	public static final String CACHE_URL = BASE_URL + "downloads/";
	public static final Double VERSION_NUMBER = 20200606.132200; //YYYYMMDD.HHMMSS format
	public static final String VERSION_UPDATE_URL = "https://orsc.dev/open-rsc/Game/raw/master/PC_Launcher/src/com/loader/openrsc/Constants.java";
	public static final String UPDATE_JAR_URL = "http://game.openrsc.com/static/downloads/OpenRSC.jar";
	public static final String JAR_FILENAME = "OpenRSC.jar";
	public static final String MD5_TABLENAME = "MD5.SUM";
	public static final String SPRITEPACK_DIR = CONF_DIR + File.separator + "video" + File.separator + "spritepacks";
}
