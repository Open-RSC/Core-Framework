package org.openrsc.client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Config 
{
    public static int PORT = 53595;
    //public static String IP = "game.openrsc.com";
    //public static String IP = "45.77.154.187"; // dev
    public static String IP = "127.0.0.1";
    public static InetSocketAddress ADDR = new InetSocketAddress(IP, PORT);
    
    public final static int DEFAULT_WINDOW_WIDTH = 512;
    public final static int DEFAULT_WINDOW_HEIGHT = 346;
    public final static int CLIENT_VERSION = 7;
    public final static int NOTE_ITEM_ID_BASE = 10000;
    
    private final static HashMap<String, String> configMap = new HashMap();
    
    public final static void updateConfiguration(HashMap<String, String> newConfig) {
        configMap.putAll(newConfig);
        
        System.out.println("Server Configuration Updated: ");
        for(Map.Entry<String, String> entry : configMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }
    
	public static String getServerName() {
		return configMap.get("SERVER_NAME");
	}
    
	public static String getCommandPrefix() {
		return configMap.get("COMMAND_PREFIX");
	}
}