package launcher.Utils;

import java.io.File;

public class Defaults {

    // DEFAULT FOLDERS
    public final static String _DEFAULT_CONFIG_DIR = "Cache";
    public static final String SPRITEPACK_DIR = _DEFAULT_CONFIG_DIR + File.separator + "video" + File.separator + "spritepacks";

    // URLS
    public static final String _VERSION_UPDATE_URL = "https://raw.githubusercontent.com/Open-RSC/Core-Framework/develop/PC_Launcher/src/main/java/launcher/Utils/Defaults.java";
    public final static String _GAME_FILES_SERVER = "http://game.openrsc.com/static/downloads/";

    // EXTRAS
	public final static String _RSCPLUS_REPOSITORY_DL = "https://github.com/RSCPlus/rscplus/releases/download/Latest/rscplus-windows.zip";
    public final static String _APOS_REPOSITORY_DL = "https://github.com/Open-RSC/APOS/archive/master.zip";
	public final static String _IDLERSC_REPOSITORY_DL = "https://github.com/Open-RSC/IdleRSC/archive/master.zip";

    // STRINGS
    public final static String _TITLE = "Open RuneScape Classic Game Launcher";

    // FILES
    public final static String _CLIENT_FILENAME = "Open_RSC_Client";
    public final static String _LAUNCHER_FILENAME = "OpenRSC.jar";
    public final static String _MD5_TABLE_FILENAME = "MD5.SUM";

    // VERSIONS
    public final static Double _CURRENT_VERSION = 20210602.185500;

    // Only update versions below as-needed
	public final static Double _RSCPLUS_VERSION = 20210214.175053;
	public final static Double _APOS_VERSION = 20210521.081500;
	public final static Double _IDLERSC_VERSION = 20210521.081500;
}
