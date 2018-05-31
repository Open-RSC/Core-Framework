package org.rscemulation.client;

import java.net.InetSocketAddress;

public class Config 
{
	public static String IP = "127.0.0.1";//"144.217.14.43";
	public static int PORT = 53595;
	public static InetSocketAddress ADDR = new InetSocketAddress(IP, PORT);	
	public static int CLIENT_VERSION = 6;

	public final static String TOO_MANY_CHARS_IN_WILDERNESS_TEXT_A = "Wilderness IP Limit Exceeded";
	public final static String TOO_MANY_CHARS_IN_WILDERNESS_TEXT_B = "";
}
