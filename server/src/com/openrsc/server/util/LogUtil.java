package com.openrsc.server.util;

import com.openrsc.server.Server;
import com.openrsc.server.ServerConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.io.IoBuilder;

import java.io.File;
import java.nio.file.InvalidPathException;

public class LogUtil {
    private LogUtil() {
        // Stop anyone from instantiating this class.
    }

    public static void populateThreadContext(ServerConfiguration configuration) {
        ThreadContext.put("world.name", formatWorldName(configuration.SERVER_NAME));
        ThreadContext.put("world.number", String.valueOf(configuration.WORLD_NUMBER));
    }

    private static String formatWorldName(String worldName) {
        if (StringUtils.isNotBlank(worldName)) {
            return worldName.toLowerCase().trim().replaceAll(" ", "_");
        }
        return worldName;
    }

    public static void configure() {
        try {
            StringBuilder logPattern = new StringBuilder();
            if ("true".equalsIgnoreCase(System.getProperty("coloredLogging"))) {
                logPattern.append("%d{yyyy-MM-dd HH:mm:ss}");
                logPattern.append(" %highlight{[${LOG_LEVEL_PATTERN:-%5p}]}{FATAL=red, ERROR=red, WARN=yellow, INFO=green, DEBUG=green, TRACE=green}");
                logPattern.append("%style{[%t]}{magenta}");
                logPattern.append("%style{[%c{1}]}{cyan}");
                logPattern.append(" %m%n%ex");
            } else {
                logPattern.append("%d{YYYY-MM-dd HH:mm:ss} [%t] %-5p %c{1}:%L - %msg%n");
            }

            // Enables asynchronous, garbage-free logging.
			String fileUse;
            try {
            	// test if regular semi colon can be used
				new File("file:test.txt").toPath();
				fileUse = "log4j2.xml";
			} catch (InvalidPathException ipe) {
            	// cannot be used, file with unicode semi colons
            	fileUse = "log4j2b.xml";
			}

            System.setProperty("log4j.configurationFile", "conf/server/" + fileUse);
            System.setProperty(
                    "Log4jContextSelector",
                    "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
            );
            System.setProperty("logPattern", logPattern.toString());

            LoggerContext ctx = (LoggerContext) LogManager.getContext();
            ctx.reconfigure();

            Logger logger = LogManager.getLogger(Server.class);

            System.setOut(
                    IoBuilder.forLogger(logger)
                            .setLevel(Level.INFO)
                            .buildPrintStream()
            );
            System.setErr(
                    IoBuilder.forLogger(logger)
                            .setLevel(Level.ERROR)
                            .buildPrintStream()
            );
        } catch (final Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }
}
