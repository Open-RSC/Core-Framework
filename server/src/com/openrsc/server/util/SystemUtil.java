package com.openrsc.server.util;

import org.apache.logging.log4j.LogManager;
import java.time.LocalDate;
import java.time.Month;

public class SystemUtil {
    private SystemUtil() {}

    public static void exit(int statusCode) {
        // Shut down async loggers gracefully before killing process so that error logs are not flakily dropped.
        LogManager.shutdown();
        System.exit(statusCode);
    }

    public static boolean isJune() {
    	return LocalDate.now().getMonth() == Month.JUNE;
	}
}
