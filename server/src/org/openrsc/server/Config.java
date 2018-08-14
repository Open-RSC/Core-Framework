package org.openrsc.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;

public class Config {
	public static final int NOTE_ITEM_ID_BASE = 10000;
	private static final int ALLOWED_CONCURRENT_IPS_IN_WILDERNESS = 2;
	private static final String WILDERNESS_ENTRY_BLOCKED_MESSAGE = "You may only enter the wilderness on " + Config.getAllowedConcurrentIpsInWilderness() + " character(s) at a time.";

    private static final HashMap<String, String> configMap = new HashMap();
    private static final String[] safeConfigNames = {"DISABLE_FATIGUE", "DISABLE_SLEEP_WORDS", "BAN_FAILED_SLEEP", "ALLOW_WEAKENS", "ALLOW_GODSPELLS", "MAX_LOGINS_PER_IP", "SHUTDOWN_TIME_MILLIS", "MAX_PLAYERS", "COMBAT_XP_RATE", "COMBAT_XP_SUB", "SKILL_XP_RATE", "SKILL_XP_SUB", "WILD_XP_BONUS", "SKULLED_XP_BONUS", "COMMAND_PREFIX", "SERVER_NAME", "PREFIX"};
    
	public static void initConfig(File file) throws IOException {
		setStartTime(System.currentTimeMillis());
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(file));
        
        configMap.put("STAFF_TELEPORT_LOCATION_DATABASE", "`openrsc`.`teleport_locations`");
        configMap.put("AUCTIONS_TABLE", "`openrsc`.`rscd_auctions`");

		// Booleans
		setDisableFatigue(Boolean.parseBoolean(props.getProperty("DISABLE_FATIGUE")), false);
		setDisableSleepWords(Boolean.parseBoolean(props.getProperty("DISABLE_SLEEP_WORDS")), false);
		setBanFailedSleep(Boolean.parseBoolean(props.getProperty("BAN_FAILED_SLEEP")), false);
		setAllowWeakens(Boolean.parseBoolean(props.getProperty("ALLOW_WEAKENS")), false);
		setAllowGodspells(Boolean.parseBoolean(props.getProperty("ALLOW_GODSPELLS")), false);
        setLogging(Boolean.parseBoolean(props.getProperty("LOGGING")));

		// Integers
		setServerVersion(Integer.parseInt(props.getProperty("SERVER_VERSION")));
		setServerPort(Integer.parseInt(props.getProperty("SERVER_PORT")));
		setWebPort(Integer.parseInt(props.getProperty("WEB_PORT")));
		setMaxLoginsPerIp(Integer.parseInt(props.getProperty("MAX_LOGINS_PER_IP")), false);
		setShutdownTimeMillis(Integer.parseInt(props.getProperty("SHUTDOWN_TIME_MILLIS")), false);
        setMaxPlayers(Integer.parseInt(props.getProperty("MAX_PLAYERS")), false);
        setSkillLoopMode(Integer.parseInt(props.getProperty("SKILL_LOOP_MODE")),false);

		// Floats
		setCombatXpRate(Float.parseFloat(props.getProperty("COMBAT_XP_RATE")), false);
		setCombatXpSub(getCombatXpRate(), false);
		setSkillXpRate(Float.parseFloat(props.getProperty("SKILL_XP_RATE")), false);
		setSkillXpSub(getSkillXpRate(), false);
		setWildXpBonus(Float.parseFloat(props.getProperty("WILD_XP_BONUS")), false);
		setSkulledXpBonus(Float.parseFloat(props.getProperty("SKULLED_XP_BONUS")), false);

		// Strings
        setCommandPrefix(props.getProperty("COMMAND_PREFIX"), false);
        setServerName(props.getProperty("SERVER_NAME"), false);
		setPrefix(props.getProperty("PREFIX"), false);
		setServerIp(props.getProperty("SERVER_IP"));
		setDbHost(props.getProperty("DB_HOST"));
		setDbName(props.getProperty("DB_NAME"));
		setDbLogin(props.getProperty("DB_LOGIN"));
		setDbPass(props.getProperty("DB_PASS"));
		setConfigDbName(props.getProperty("CONFIG_DB_NAME"));
		setLogDbName(props.getProperty("LOG_DB_NAME"));
		setToolsDbName(props.getProperty("TOOLS_DB_NAME"));
		setAvatarDir(props.getProperty("AVATAR_DIR"));
        
		props.clear();
	}
    
    public static final HashMap<String, String> getAllConfigs() {
        HashMap<String, String> configs = new HashMap();
        for(String safeConfigName : safeConfigNames)
            configs.put(safeConfigName, configMap.get(safeConfigName));
        
        return configs;
    }
    
    public static void sendConfiguration() {
        sendConfiguration(getAllConfigs());
    }
    
    public static void sendConfiguration(String key, String value) {
        HashMap<String, String> sendConfig = new HashMap();
        sendConfig.put(key, value);
        sendConfiguration(sendConfig);
    }
    
    public static void sendConfiguration(HashMap<String, String> sendConfig) {
        synchronized (World.getPlayers()) {
            for (Player p : World.getPlayers()) {
                p.sendConfiguration(sendConfig);
            }
        }
    }
    
	public static int getAllowedConcurrentIpsInWilderness() {
		return ALLOWED_CONCURRENT_IPS_IN_WILDERNESS;
	}

	/*public static void setAllowedConcurrentIpsInWilderness(int allowedConcurrentIpsInWilderness) {
		ALLOWED_CONCURRENT_IPS_IN_WILDERNESS = allowedConcurrentIpsInWilderness;
	}*/

	public static String getWildernessEntryBlockedMessage() {
		return WILDERNESS_ENTRY_BLOCKED_MESSAGE;
	}

	public static String getStaffTeleportLocationDatabase() {
		return configMap.get("STAFF_TELEPORT_LOCATION_DATABASE");
	}
    
	public static void setStaffTeleportLocationDatabase(String staffTeleportLocationDatabase) {
        setStaffTeleportLocationDatabase(staffTeleportLocationDatabase, true);
    }

	public static void setStaffTeleportLocationDatabase(String staffTeleportLocationDatabase, boolean sendConfiguration) {
        String key  = "STAFF_TELEPORT_LOCATION_DATABASE";
        configMap.put(key, staffTeleportLocationDatabase);
        
        if(sendConfiguration)
            sendConfiguration(key, staffTeleportLocationDatabase);
	}

	public static String getAuctionsTable() {
		return configMap.get("AUCTIONS_TABLE");
	}
    
	public static void setAuctionsTable(String auctionsTable) {
        setAuctionsTable(auctionsTable, true);
    }

	public static void setAuctionsTable(String auctionsTable, boolean sendConfiguration) {
        String key  = "AUCTIONS_TABLE";
        configMap.put(key, auctionsTable);
        
        if(sendConfiguration)
            sendConfiguration(key, auctionsTable);
	}

	public static int getShutdownTimeMillis() {
		return Integer.parseInt(configMap.get("SHUTDOWN_TIME_MILLIS"));
	}
    
	public static void setShutdownTimeMillis(int shutdownTimeMillis) {
        setShutdownTimeMillis(shutdownTimeMillis, true);
    }

	public static void setShutdownTimeMillis(int shutdownTimeMillis, boolean sendConfiguration) {
        String key      = "SHUTDOWN_TIME_MILLIS";
        String value    = Integer.toString(shutdownTimeMillis);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static String getAvatarDir() {
		return configMap.get("AVATAR_DIR");
	}
    
	private static void setAvatarDir(String avatarDir) {
        String key  = "AVATAR_DIR";
        configMap.put(key, avatarDir);
	}

	public static String getServerIp() {
		return configMap.get("SERVER_IP");
	}
    
	private static void setServerIp(String serverIp) {
        String key  = "SERVER_IP";
        configMap.put(key, serverIp);
	}

	public static String getCommandPrefix() {
		return configMap.get("COMMAND_PREFIX");
	}
    
	public static void setCommandPrefix(String commandPrefix) {
        setCommandPrefix(commandPrefix, true);
    }

	public static void setCommandPrefix(String commandPrefix, boolean sendConfiguration) {
        String key  = "COMMAND_PREFIX";
        configMap.put(key, commandPrefix);
        
        if(sendConfiguration)
            sendConfiguration(key, commandPrefix);
	}

	public static String getServerName() {
		return configMap.get("SERVER_NAME");
	}
    
	public static void setServerName(String serverName) {
        setServerName(serverName, true);
    }

	public static void setServerName(String serverName, boolean sendConfiguration) {
        String key  = "SERVER_NAME";
        configMap.put(key, serverName);
        
        if(sendConfiguration)
            sendConfiguration(key, serverName);
	}

	public static String getPrefix() {
		return configMap.get("PREFIX");
	}
    
	public static void setPrefix(String prefix) {
        setPrefix(prefix, true);
    }

	public static void setPrefix(String prefix, boolean sendConfiguration) {
        String key  = "PREFIX";
        configMap.put(key, prefix);
        
        if(sendConfiguration)
            sendConfiguration(key, prefix);
	}

	public static String getDbHost() {
		return configMap.get("DB_HOST");
	}
    
	public static void setDbHost(String dbHost) {
        String key  = "DB_HOST";
        configMap.put(key, dbHost);
	}

	public static String getDbName() {
		return configMap.get("DB_NAME");
	}
    
	private static void setDbName(String dbName) {
        String key  = "DB_NAME";
        configMap.put(key, dbName);
	}

	public static String getDbLogin() {
		return configMap.get("DB_LOGIN");
	}
    
	private static void setDbLogin(String dbLogin) {
        String key  = "DB_LOGIN";
        configMap.put(key, dbLogin);
	}

	public static String getDbPass() {
		return configMap.get("DB_PASS");
	}
    
	private static void setDbPass(String dbPass) {
        String key  = "DB_PASS";
        configMap.put(key, dbPass);
	}

	public static String getConfigDbName() {
		return configMap.get("CONFIG_DB_NAME");
	}
    
	private static void setConfigDbName(String configDbName) {
        String key  = "CONFIG_DB_NAME";
        configMap.put(key, configDbName);
	}

	public static String getLogDbName() {
		return configMap.get("LOG_DB_NAME");
	}
    
	private static void setLogDbName(String logDbName) {
        String key  = "LOG_DB_NAME";
        configMap.put(key, logDbName);
	}

	public static String getToolsDbName() {
		return configMap.get("TOOLS_DB_NAME");
	}
    
	private static void setToolsDbName(String logDbName) {
        String key  = "TOOLS_DB_NAME";
        configMap.put(key, logDbName);
	}

	public static int getWebPort() {
		return Integer.parseInt(configMap.get("WEB_PORT"));
	}
    
	private static void setWebPort(int webPort) {
        String key      = "WEB_PORT";
        String value    = Integer.toString(webPort);
        configMap.put(key, value);
	}

	public static int getServerPort() {
		return Integer.parseInt(configMap.get("SERVER_PORT"));
	}
    
	private static void setServerPort(int serverPort) {
        String key      = "SERVER_PORT";
        String value    = Integer.toString(serverPort);
        configMap.put(key, value);
	}

	public static int getServerVersion() {
		return Integer.parseInt(configMap.get("SERVER_VERSION"));
	}
    
	private static void setServerVersion(int serverVersion) {
        String key      = "SERVER_VERSION";
        String value    = Integer.toString(serverVersion);
        configMap.put(key, value);
	}

	public static int getMaxPlayers() {
		return Integer.parseInt(configMap.get("MAX_PLAYERS"));
	}
    
	public static void setMaxPlayers(int maxPlayers) {
        setMaxPlayers(maxPlayers, true);
    }

	public static void setMaxPlayers(int maxPlayers, boolean sendConfiguration) {
        String key      = "MAX_PLAYERS";
        String value    = Integer.toString(maxPlayers);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static int getMaxLoginsPerIp() {
		return Integer.parseInt(configMap.get("MAX_LOGINS_PER_IP"));
	}
    
	public static void setMaxLoginsPerIp(int maxLoginsPerIp) {
        setMaxLoginsPerIp(maxLoginsPerIp, true);
    }

	public static void setMaxLoginsPerIp(int maxLoginsPerIp, boolean sendConfiguration) {
        String key      = "MAX_LOGINS_PER_IP";
        String value    = Integer.toString(maxLoginsPerIp);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

    public static int getSkillLoopMode() {
        return Integer.parseInt(configMap.get("SKILL_LOOP_MODE"));
    }

    public static void setSkillLoopMode(int skillLoopMode) {
        setSkillLoopMode(skillLoopMode, true);
    }

    public static void setSkillLoopMode(int skillLoopMode, boolean sendConfiguration) {
        String key      = "SKILL_LOOP_MODE";
        String value    = Integer.toString(skillLoopMode);
        configMap.put(key, value);

        if(sendConfiguration)
            sendConfiguration(key, value);
    }

	public static float getCombatXpRate() {
		return Float.parseFloat(configMap.get("COMBAT_XP_RATE"));
	}
    
	public static void setCombatXpRate(float combatXpRate) {
        setCombatXpRate(combatXpRate, true);
    }

	public static void setCombatXpRate(float combatXpRate, boolean sendConfiguration) {
        String key      = "COMBAT_XP_RATE";
        String value    = Float.toString(combatXpRate);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static float getCombatXpSub() {
		return Float.parseFloat(configMap.get("COMBAT_XP_SUB"));
	}
    
	public static void setCombatXpSub(float combatXpSub) {
        setCombatXpSub(combatXpSub, true);
    }

	public static void setCombatXpSub(float combatXpSub, boolean sendConfiguration) {
        String key      = "COMBAT_XP_SUB";
        String value    = Float.toString(combatXpSub);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}
    
	public static float getSkillXpRate() {
		return Float.parseFloat(configMap.get("SKILL_XP_RATE"));
	}
    
	public static void setSkillXpRate(float skillXpRate) {
        setSkillXpRate(skillXpRate, true);
    }

	public static void setSkillXpRate(float skillXpRate, boolean sendConfiguration) {
        String key      = "SKILL_XP_RATE";
        String value    = Float.toString(skillXpRate);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static float getSkillXpSub() {
		return Float.parseFloat(configMap.get("SKILL_XP_SUB"));
	}
    
	public static void setSkillXpSub(float skillXpSub) {
        setSkillXpSub(skillXpSub, true);
    }

	public static void setSkillXpSub(float skillXpSub, boolean sendConfiguration) {
        String key      = "SKILL_XP_SUB";
        String value    = Float.toString(skillXpSub);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static float getWildXpBonus() {
		return Float.parseFloat(configMap.get("WILD_XP_BONUS"));
	}
    
	public static void setWildXpBonus(float wildXpBonus) {
        setWildXpBonus(wildXpBonus, true);
    }

	public static void setWildXpBonus(float wildXpBonus, boolean sendConfiguration) {
        String key      = "WILD_XP_BONUS";
        String value    = Float.toString(wildXpBonus);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static float getSkulledXpBonus() {
		return Float.parseFloat(configMap.get("SKULLED_XP_BONUS"));
	}
    
	public static void setSkulledXpBonus(float skulledXpBonus) {
        setSkulledXpBonus(skulledXpBonus, true);
    }

	public static void setSkulledXpBonus(float skulledXpBonus, boolean sendConfiguration) {
        String key      = "SKULLED_XP_BONUS";
        String value    = Float.toString(skulledXpBonus);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static long getStartTime() {
		return Long.parseLong(configMap.get("START_TIME"));
	}
    
	private static void setStartTime(long startTime) {
        setStartTime(startTime, true);
    }

	private static void setStartTime(long startTime, boolean sendConfiguration) {
        String key      = "START_TIME";
        String value    = Long.toString(startTime);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static boolean isLogging() {
		return Boolean.parseBoolean(configMap.get("LOGGING"));
	}
    
	public static void setLogging(boolean logging) {
        String key      = "LOGGING";
        String value    = Boolean.toString(logging);
        configMap.put(key, value);
	}

	public static boolean isPkMode() {
		return Boolean.parseBoolean(configMap.get("PK_MODE"));
	}
    
	public static void setPkMode(boolean pkMode) {
        setPkMode(pkMode, true);
    }

	public static void setPkMode(boolean pkMode, boolean sendConfiguration) {
        String key      = "PK_MODE";
        String value    = Boolean.toString(pkMode);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static boolean isBanFailedSleep() {
		return Boolean.parseBoolean(configMap.get("BAN_FAILED_SLEEP"));
	}
    
	public static void setBanFailedSleep(boolean banFailedSleep) {
        setBanFailedSleep(banFailedSleep, true);
    }

	public static void setBanFailedSleep(boolean banFailedSleep, boolean sendConfiguration) {
        String key      = "BAN_FAILED_SLEEP";
        String value    = Boolean.toString(banFailedSleep);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static boolean isAllowWeakens() {
		return Boolean.parseBoolean(configMap.get("ALLOW_WEAKENS"));
	}
    
	public static void setAllowWeakens(boolean allowWeakens) {
        setAllowWeakens(allowWeakens, true);
    }

	public static void setAllowWeakens(boolean allowWeakens, boolean sendConfiguration) {
        String key      = "ALLOW_WEAKENS";
        String value    = Boolean.toString(allowWeakens);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static boolean isAllowGodspells() {
		return Boolean.parseBoolean(configMap.get("ALLOW_GODSPELLS"));
	}
    
	public static void setAllowGodspells(boolean allowGodspells) {
        setAllowGodspells(allowGodspells, true);
    }

	public static void setAllowGodspells(boolean allowGodspells, boolean sendConfiguration) {
        String key      = "ALLOW_GODSPELLS";
        String value    = Boolean.toString(allowGodspells);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static boolean isDisableSleepWords() {
		return Boolean.parseBoolean(configMap.get("DISABLE_SLEEP_WORDS"));
	}
    
	public static void setDisableSleepWords(boolean disableSleepWords) {
        setDisableSleepWords(disableSleepWords, true);
    }

	public static void setDisableSleepWords(boolean disableSleepWords, boolean sendConfiguration) {
        String key      = "DISABLE_SLEEP_WORDS";
        String value    = Boolean.toString(disableSleepWords);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}

	public static boolean isDisableFatigue() {
		return Boolean.parseBoolean(configMap.get("DISABLE_FATIGUE"));
	}
    
	public static void setDisableFatigue(boolean disableFatigue) {
        setDisableFatigue(disableFatigue, true);
    }

	public static void setDisableFatigue(boolean disableFatigue, boolean sendConfiguration) {
        String key      = "DISABLE_FATIGUE";
        String value    = Boolean.toString(disableFatigue);
        configMap.put(key, value);
        
        if(sendConfiguration)
            sendConfiguration(key, value);
	}
}
