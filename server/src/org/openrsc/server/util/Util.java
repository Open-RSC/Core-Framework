package org.openrsc.server.util;

import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ErrorLog;

public class Util {
	public static void banIP(String IP, String ... reason) {
		if (reason.length > 0)
			Logger.log(new ErrorLog(-1, -1, IP, "IP Banned: " + reason, DataConversions.getTimeStamp()));
		
		try {
			Runtime.getRuntime().exec("sudo /scripts/sudoroute.sh add " + IP + " gw 127.0.0.1");
			System.out.println("IP Banned: " + IP + "(" + reason + ")");
		} catch (Exception e) {
			System.out.println("Failed to IP ban: " + IP + "(" + reason + ")");
			e.printStackTrace();
		}						
	}
}