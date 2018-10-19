package com.loader.openrsc;

public class Constants {
    public static Double VERSION_NUMBER = 20181015.190000;
    public static final String SERVER_DOMAIN = "game.openrsc.com"; // Only used for the server status display
    public static final int SERVER_PORT = 43594;
    public static final String GAME_NAME = "Open RSC";
    public static final String base_url = "https://openrsc.com/"; // Cache and client jar download locations depend on this
    //public static final String rss_url = "https://openrsc.com/blog/rss"; // RSS feed
    public static final String CONF_DIR = "Cache";
    public static String CACHE_URL = base_url + "downloads/cache/";
    public static String CLIENT_URL = base_url + "downloads/Open_RSC_Client.jar";
}
