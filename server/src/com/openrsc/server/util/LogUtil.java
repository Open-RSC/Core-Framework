package com.openrsc.server.util;

import com.openrsc.server.ServerConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.ThreadContext;

public class LogUtil {
    private LogUtil() {}

    public static void populateThreadContext(ServerConfiguration configuration) {
        ThreadContext.put("world.name", formatWorldName(configuration.SERVER_NAME));
        ThreadContext.put("world.number", String.valueOf(configuration.WORLD_NUMBER));
    }

    private static String formatWorldName(String worldName) {
        if(StringUtils.isNotBlank(worldName)) {
            return worldName.toLowerCase().trim().replaceAll(" ", "_");
        }
        return worldName;
    }
}
