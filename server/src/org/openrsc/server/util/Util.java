package org.openrsc.server.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ErrorLog;

public class Util {
	public static void banIP(String IP, String ... reason) {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
		if (reason.length > 0)
			Logger.log(new ErrorLog(-1, -1, IP, "IP Banned: " + reason, DataConversions.getTimeStamp()));
		
		try {
			//Runtime.getRuntime().exec("sudo /scripts/sudoroute.sh add " + IP + " gw 127.0.0.1");
			//System.out.println(dateFormat.format(date)+": IP Banned: " + IP + "(" + reason + ")");
		} catch (Exception e) {
			//System.out.println(dateFormat.format(date)+": Failed to IP ban: " + IP + "(" + reason + ")");
			e.printStackTrace();
		}						
	}
}