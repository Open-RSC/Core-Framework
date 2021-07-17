package com.openrsc.server.util;

import org.apache.logging.log4j.LogManager;

public class SystemUtil {
    private SystemUtil() {}

    public static void exit(int statusCode) {
        // Shut down async loggers gracefully before killing process so that error logs are not flakily dropped.
        LogManager.shutdown();
        System.exit(statusCode);
    }
}
