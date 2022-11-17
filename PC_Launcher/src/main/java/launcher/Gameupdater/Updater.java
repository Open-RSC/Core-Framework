package launcher.Gameupdater;

import launcher.Gameupdater.Downloader;
import launcher.Utils.ClientLauncher;
import launcher.Utils.Defaults;
import launcher.Utils.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static launcher.Launcher.fetchLatestExtrasVersionNumber;

public class Updater {
	private static String _CACHE_DIR = null;
	private static Downloader gameUpdater;

	public Updater(String cacheDir, String gameVersion) {
		_CACHE_DIR = cacheDir;
	}

	public void updateOpenRSCClient() {
		File gamePath = new File(_CACHE_DIR);
		if (!gamePath.exists() || !gamePath.isDirectory())
			gamePath.mkdir();

		if (null == gameUpdater) {
			gameUpdater = new Downloader(_CACHE_DIR, new ArrayList<>());
		}
		gameUpdater.initOpenRSCClientUpdate();
	}

	public static void updateRSCPlus() throws SecurityException, IOException {
		try {
			// Set variables
			File _GAME_PATH = new File(_CACHE_DIR + "/extras/rscplus/");
			String _FILE_NAME = "rscplus-master.zip";
			String _URL = Defaults._RSCPLUS_REPOSITORY_DL;
			Double _EXTRA_VERSION = Defaults._RSCPLUS_VERSION;
			File _PRESERVATION_CONFIG = new File( _CACHE_DIR + "/extras/rscplus/worlds/01_RSC Preservation.ini");
			File _URANIUM_CONFIG = new File(_CACHE_DIR + "/extras/rscplus/worlds/02_RSC Uranium.ini");
			File _DEFAULT_CONFIG = new File(_CACHE_DIR + "/extras/rscplus/worlds/01_World 1.ini");

			// Download new or update existing, then execute
			downloadOrUpdate(_GAME_PATH, _FILE_NAME, _URL, _EXTRA_VERSION);

			// if (!_PRESERVATION_CONFIG.exists()) {
				createPreservationConfig(_PRESERVATION_CONFIG);
			// }
			// if (!_URANIUM_CONFIG.exists()) {
				createUraniumConfig(_URANIUM_CONFIG);
			// }

			if (_DEFAULT_CONFIG.exists())
				_DEFAULT_CONFIG.delete();

			ClientLauncher.launchRSCPlus();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public static void updateAPOS() throws SecurityException, IOException {
		try {
			// Set variables
			File _GAME_PATH = new File(_CACHE_DIR + "/extras/apos/");
			String _FILE_NAME = "apos-master.zip";
			String _URL = Defaults._APOS_REPOSITORY_DL;
			Double _EXTRA_VERSION = Defaults._APOS_VERSION;

			// Download new or update existing, then execute
			downloadOrUpdate(_GAME_PATH, _FILE_NAME, _URL, _EXTRA_VERSION);
			ClientLauncher.launchAPOS();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public static void updateIdleRSC() throws SecurityException, IOException {
		try {
			// Set variables
			File _GAME_PATH = new File(_CACHE_DIR + "/extras/idlersc/");
			String _FILE_NAME = "idlersc-master.zip";
			String _URL = Defaults._IDLERSC_REPOSITORY_DL;
			Double _EXTRA_VERSION = Defaults._IDLERSC_VERSION;

			// Download new or update existing, then execute
			downloadOrUpdate(_GAME_PATH, _FILE_NAME, _URL, _EXTRA_VERSION);
			ClientLauncher.launchIdleRSC();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public static void updateWinRune() throws SecurityException, IOException {
		try {
			// Set variables
			File _GAME_PATH = new File(_CACHE_DIR + "/extras/winrune/");
			String _FILE_NAME = "winrune-master.zip";
			String _URL = Defaults._WINRUNE_REPOSITORY_DL;
			Double _EXTRA_VERSION = Defaults._WINRUNE_VERSION;

			// Download new or update existing, execute elsewhere
			downloadOrUpdate(_GAME_PATH, _FILE_NAME, _URL, _EXTRA_VERSION);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public static void updateRSCTimes() throws SecurityException, IOException {
		try {
			// Set variables
			File _GAME_PATH = new File(_CACHE_DIR + "/extras/rsctimes/");
			String _FILE_NAME = "rsctimes-master.zip";
			String _URL = Defaults._RSCTIMES_REPOSITORY_DL;
			Double _EXTRA_VERSION = Defaults._RSCTIMES_VERISION;
			File _2001SCAPE_CONFIG = new File( _CACHE_DIR + "/extras/rsctimes/worlds/01_2001scape.ini");
			File _DEFAULT_CONFIG = new File(_CACHE_DIR + "/extras/rsctimes/worlds/01_World 1.ini");

			// Download new or update existing, then execute
			downloadOrUpdate(_GAME_PATH, _FILE_NAME, _URL, _EXTRA_VERSION);

			//if (!_2001SCAPE_CONFIG.exists()) {
				create2001scapeConfig(_2001SCAPE_CONFIG);
			//}

			if (_DEFAULT_CONFIG.exists())
				_DEFAULT_CONFIG.delete();

			ClientLauncher.launchRSCTimes();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public static void updateFleaCircus() throws SecurityException, IOException {
		try {
			// Set variables
			File _GAME_PATH = new File(_CACHE_DIR + "/extras/fleacircus/");
			String _FILE_NAME = "fleacircus.zip";
			String _URL = Defaults._FLEACIRCUS_REPOSITORY_DL;
			Double _EXTRA_VERSION = Defaults._FLEACIRCUS_VERISION;

			// Download new or update existing, execute elsewhere
			downloadOrUpdate(_GAME_PATH, _FILE_NAME, _URL, _EXTRA_VERSION);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private static void downloadOrUpdate(File _GAME_PATH, String _FILE_NAME, String _URL, Double _EXTRA_VERSION) {
    Downloader.currently_updating = true;
		ProgressBar.initProgressBar();
		ProgressBar.setDownloadProgress("Checking for updates", 100.0f);
    // Start a thread to download the file to prevent the GUI from freezing
    Thread thread = new Thread(() -> {
      try {
        // If the folder does not exist, download, extract, and launch.
        if (!_GAME_PATH.exists() || !_GAME_PATH.isDirectory()) {
		      ProgressBar.setDownloadProgress("Creating directories", 0.0f);
          if (_GAME_PATH.getParentFile() != null) {
            try {
              Files.createDirectories(_GAME_PATH.toPath());
            } catch (IOException e) {
              Logger.Warn("Could not make required directories, trying with alternative method");
              _GAME_PATH.mkdirs();
              e.printStackTrace();
            }
          }
          try {
            ProgressBar.setDownloadProgress("Connecting to server...", 50.0f);
            URLConnection connection = new URL(_URL).openConnection();
            String description = getDescription(new File(_FILE_NAME));
            int fileSize = connection.getContentLength();
            try (BufferedInputStream inputStream = new BufferedInputStream(new URL(_URL).openStream());
              FileOutputStream fileOS = new FileOutputStream(_GAME_PATH + File.separator + _FILE_NAME)) {
              byte[] data = new byte[1024];
              int byteContent;
              int totalRead = 0;
              while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                totalRead += byteContent;
                fileOS.write(data, 0, byteContent);
                float percent = (float) (totalRead / fileSize) * 100;
                ProgressBar.setDownloadProgress(description, percent);
              }
            } catch (Exception error) {
              error.printStackTrace();
            }
          } catch (Exception error) {
            error.printStackTrace();
          }
          unZipUpdate(_GAME_PATH + File.separator + _FILE_NAME, String.valueOf(_GAME_PATH));
        }

        // If the folder already exists, check if there is an updated version. If so, download latest, extract, and launch.
        if (_GAME_PATH.exists() || _GAME_PATH.isDirectory()) {
          double latestVersion = fetchLatestExtrasVersionNumber(_EXTRA_VERSION);
          if (Defaults._CURRENT_VERSION < latestVersion) {
            try {
              URLConnection connection = new URL(_URL).openConnection();
              String description = getDescription(new File(_FILE_NAME));
              int fileSize = connection.getContentLength();
              try (BufferedInputStream inputStream = new BufferedInputStream(new URL(_URL).openStream());
                FileOutputStream fileOS = new FileOutputStream(_GAME_PATH + File.separator + _FILE_NAME)) {
                byte[] data = new byte[1024];
                int byteContent;
                int totalRead = 0;
                while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                  totalRead += byteContent;
                  fileOS.write(data, 0, byteContent);
                  float percent = (float) (totalRead / fileSize) * 100;
                  ProgressBar.setDownloadProgress(description, percent);
                }
              } catch (Exception error) {
                error.printStackTrace();
              }
            } catch (Exception error) {
              error.printStackTrace();
            }
            unZipUpdate(_GAME_PATH + File.separator + _FILE_NAME, String.valueOf(_GAME_PATH));
          }
        }
        ProgressBar.setDownloadProgress("Done", 100.0f);
      } catch (Exception error) {
        error.printStackTrace();
      } finally {
        Downloader.currently_updating = false;
      }
    });
    thread.start();
	}

	private static void unZipUpdate(String pathToUpdateZip, String destinationPath) {
		byte[] byteBuffer = new byte[1024];

		try {
			ZipInputStream inZip = new ZipInputStream(new FileInputStream(pathToUpdateZip));
			ZipEntry inZipEntry = inZip.getNextEntry();
      int unzipCount = 0;
			while (inZipEntry != null) {
        unzipCount += 1;
				String fileName = inZipEntry.getName();
				File unZippedFile = new File(destinationPath + File.separator + fileName);
				Logger.Info("Unzipping: " + unZippedFile.getAbsoluteFile());
		    ProgressBar.setDownloadProgress("Unzipping: " + unZippedFile.getName(), unzipCount * 5);
				if (inZipEntry.isDirectory()) {
					unZippedFile.mkdirs();
				} else {
					new File(unZippedFile.getParent()).mkdirs();
					unZippedFile.createNewFile();
					FileOutputStream unZippedFileOutputStream = new FileOutputStream(unZippedFile);
					int length;
					while ((length = inZip.read(byteBuffer)) > 0) {
						unZippedFileOutputStream.write(byteBuffer, 0, length);
					}
					unZippedFileOutputStream.close();
				}
				inZipEntry = inZip.getNextEntry();
			}
			//inZipEntry.close();
			inZip.close();
			Logger.Info("Finished Unzipping");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getDescription(File ref) {
		int index = ref.getName().lastIndexOf('.');
		if (index == -1)
			return "Downloading component";
		else {
			String extension = ref.getName().substring(index + 1);
			return "General";

		}
	}

	public static void createPropsConfig(File config, Properties props) {
		if (!config.exists()) {
			if (config.getParentFile() != null) {
				try {
					Files.createDirectories(config.getParentFile().toPath());
				} catch (IOException e) {
					config.getParentFile().mkdirs();
					e.printStackTrace();
				}
			}
		}

		try {
			FileOutputStream out = new FileOutputStream(config);
			props.store(out, null);
			out.close();
		} catch (IOException e) {
			Logger.Error("Could not save world config!");
			e.printStackTrace();
		}
	}

	public static void createPreservationConfig(File config) {
		Properties props = new Properties();
		props.put("rsa_pub_key", "7112866275597968156550007489163685737528267584779959617759901583041864787078477876689003422509099353805015177703670715380710894892460637136582066351659813");
		props.put("port", "43596");
		props.put("rsa_exponent", "65537");
		props.put("url", "game.openrsc.com");
		props.put("name", "RSC Preservation");
		props.put("servertype", "1");
		props.put("hiscores_url", "https\\://rsc.vet/player/preservation/%USERNAME%");

		createPropsConfig(config, props);
	}

	public static void createUraniumConfig(File config) {
		Properties props = new Properties();
		props.put("rsa_pub_key", "7112866275597968156550007489163685737528267584779959617759901583041864787078477876689003422509099353805015177703670715380710894892460637136582066351659813");
		props.put("port", "43235");
		props.put("rsa_exponent", "65537");
		props.put("url", "game.openrsc.com");
		props.put("name", "RSC Uranium");
		props.put("servertype", "1");
		props.put("hiscores_url", "https\\://rsc.vet/player/uranium/%USERNAME%");

		createPropsConfig(config, props);
	}

	public static void create2001scapeConfig(File config) {
		Properties props = new Properties();
		props.put("rsa_pub_key", "7112866275597968156550007489163685737528267584779959617759901583041864787078477876689003422509099353805015177703670715380710894892460637136582066351659813");
		props.put("port", "43593");
		props.put("rsa_exponent", "65537");
		props.put("url", "game.openrsc.com");
		props.put("name", "2001scape");
		props.put("servertype", "1");
		props.put("hiscores_url", "https\\://rsc.vet/player/2001scape/%USERNAME%");

		createPropsConfig(config, props);
	}
}
