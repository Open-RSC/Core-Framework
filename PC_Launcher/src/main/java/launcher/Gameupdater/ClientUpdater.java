package launcher.Gameupdater;

import launcher.Utils.Defaults;
import java.io.*;
import java.util.ArrayList;

public class ClientUpdater {
  private static String _CACHE_DIR;
  private static Downloader gameUpdater;

  public ClientUpdater(String cacheDir) {
    _CACHE_DIR = cacheDir;
  }

  public void updateOpenRSCClient() {
    File gamePath = new File(_CACHE_DIR);
    if (!gamePath.exists() || !gamePath.isDirectory())
      gamePath.mkdir();

    if (gameUpdater == null) {
      gameUpdater = new Downloader(_CACHE_DIR, new ArrayList<>());
    }
    gameUpdater.initOpenRSCClientUpdate();
  }

  private static void executeUpdate(String gamePath, String fileName, String repositoryDL,
      String versionName) throws SecurityException, IOException {
    File _GAME_PATH = new File(_CACHE_DIR + gamePath);
    String _FILE_NAME = fileName;

    ClientDownloader.downloadOrUpdate(_GAME_PATH, _FILE_NAME, repositoryDL, versionName);
  }

  public static void updateRSCPlus() throws SecurityException, IOException {
    File _PRESERVATION_CONFIG = new File(_CACHE_DIR + "/extras/rscplus/worlds/01_RSC Preservation.ini");
    File _URANIUM_CONFIG = new File(_CACHE_DIR + "/extras/rscplus/worlds/02_RSC Uranium.ini");
    File _DEFAULT_CONFIG = new File(_CACHE_DIR + "/extras/rscplus/worlds/01_World 1.ini");

    executeUpdate("/extras/rscplus/", "rscplus-master.zip", Defaults._RSCPLUS_REPOSITORY_DL,
        "_RSCPLUS_VERSION");

    ConfigCreator.createPreservationConfig(_PRESERVATION_CONFIG);
    ConfigCreator.createUraniumConfig(_URANIUM_CONFIG);

    if (_DEFAULT_CONFIG.exists()) // Delete legacy config
      _DEFAULT_CONFIG.delete();
  }

  public static void updateAPOS() throws SecurityException, IOException {
    executeUpdate("/extras/apos/", "apos-master.zip", Defaults._APOS_REPOSITORY_DL,
        "_APOS_VERSION");
  }

  public static void updateIdleRSC() throws SecurityException, IOException {
    executeUpdate("/extras/idlersc/", "IdleRSC.zip", Defaults._IDLERSC_REPOSITORY_DL,
        "_IDLERSC_VERSION");
  }

  public static void updateWinRune() throws SecurityException, IOException {
    executeUpdate("/extras/winrune/", "winrune-master.zip", Defaults._WINRUNE_REPOSITORY_DL,
        "_WINRUNE_VERSION");
  }

  public static void updateRSCTimes() throws SecurityException, IOException {
    File _2001SCAPE_CONFIG = new File(_CACHE_DIR + "/extras/rsctimes/worlds/01_2001scape.ini");
    File _DEFAULT_CONFIG = new File(_CACHE_DIR + "/extras/rsctimes/worlds/01_World 1.ini");

    executeUpdate("/extras/rsctimes/", "rsctimes-master.zip", Defaults._RSCTIMES_REPOSITORY_DL,
        "_RSCTIMES_VERSION");

    ConfigCreator.create2001scapeConfig(_2001SCAPE_CONFIG);

    if (_DEFAULT_CONFIG.exists()) // Delete legacy config
      _DEFAULT_CONFIG.delete();
  }

  public static void updateFleaCircus() throws SecurityException, IOException {
    executeUpdate("/extras/fleacircus/", "fleacircus.zip", Defaults._FLEACIRCUS_REPOSITORY_DL,
        "_FLEACIRCUS_VERSION");
  }
}
