package org.openrsc.client;

import java.net.InetSocketAddress;

public class Config 
{
        //public static String IP = "game.openrsc.com";
        public static String IP = "127.0.0.1";
        public static int PORT = 53595;
        public static InetSocketAddress ADDR = new InetSocketAddress(IP, PORT);	
        public static int CLIENT_VERSION = 7;  
        public final static int DEFAULT_WINDOW_WIDTH = 512;
        public final static int DEFAULT_WINDOW_HEIGHT = 346;    
        public static final int NOTE_ITEM_ID_BASE = 10000;
}
