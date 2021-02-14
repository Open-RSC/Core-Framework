package launcher.Gameupdater;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import launcher.Gameupdater.UpdaterGui.MainUpdaterGui;
import launcher.Utils.ClientLauncher;
import launcher.Utils.Defaults;

import javax.swing.*;

import static launcher.Launcher.fetchLatestExtrasVersionNumber;

public class Updater {
	private static String _CACHE_DIR = null;

	public Updater(String cacheDir, String gameVersion) {
		_CACHE_DIR = cacheDir;
	}

	public void updateGame() {
		File gamePath = new File(_CACHE_DIR);
		if (!gamePath.exists() || !gamePath.isDirectory())
			gamePath.mkdir();

		Downloader gameUpdater = new Downloader(_CACHE_DIR);
		gameUpdater.initUpdate();
	}

	public static void updateRSCPlus() throws SecurityException, IOException {
		try {
			// Set variables
			File _GAME_PATH = new File(_CACHE_DIR + "/extras/rscplus");
			String _FILE_NAME = "rscplus-master.zip";
			String _URL = Defaults._RSCPLUS_REPOSITORY_DL;
			String _EXTRA_VERSION = String.valueOf(Defaults._RSCPLUS_VERSION);
			File _PRESERVATION_CONFIG = new File(_CACHE_DIR + "/extras/rscplus/worlds/01_RSC Preservation.ini");
			File _URANIUM_CONFIG = new File(_CACHE_DIR + "/extras/rscplus/worlds/02_RSC Uranium.ini");
			File _DEFAULT_CONFIG = new File(_CACHE_DIR + "/extras/rscplus/worlds/01_World 1.ini");

			// Download new or update existing, then execute
			downloadOrUpdate(_GAME_PATH, _FILE_NAME, _URL, _EXTRA_VERSION);

			if (!_PRESERVATION_CONFIG.exists()) {
				createPreservationConfig(_PRESERVATION_CONFIG);
				return;
			}
			if (!_URANIUM_CONFIG.exists()) {
				createUraniumConfig(_URANIUM_CONFIG);
				return;
			}

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
			File _GAME_PATH = new File(_CACHE_DIR + "/extras/apos");
			String _FILE_NAME = "apos-master.zip";
			String _URL = Defaults._APOS_REPOSITORY_DL;
			String _EXTRA_VERSION = String.valueOf(Defaults._APOS_VERSION);

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
			File _GAME_PATH = new File(_CACHE_DIR + "/extras/idlersc");
			String _FILE_NAME = "idlersc-master.zip";
			String _URL = Defaults._IDLERSC_REPOSITORY_DL;
			String _EXTRA_VERSION = String.valueOf(Defaults._IDLERSC_VERSION);

			// Download new or update existing, then execute
			downloadOrUpdate(_GAME_PATH, _FILE_NAME, _URL, _EXTRA_VERSION);
			ClientLauncher.launchIdleRSC();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private static void downloadOrUpdate(File _GAME_PATH, String _FILE_NAME, String _URL, String _EXTRA_VERSION) {
		// If the folder does not exist, download, extract, and launch.
		if (!_GAME_PATH.exists() || !_GAME_PATH.isDirectory()) {
			_GAME_PATH.mkdir();
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
						MainUpdaterGui.get().setDownloadProgress(description, (float) (totalRead * 100 / fileSize));
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
							MainUpdaterGui.get().setDownloadProgress(description, (float) (totalRead * 100 / fileSize));
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
	}

	private static void unZipUpdate(String pathToUpdateZip, String destinationPath) {
		byte[] byteBuffer = new byte[1024];

		try {
			ZipInputStream inZip = new ZipInputStream(new FileInputStream(pathToUpdateZip));
			ZipEntry inZipEntry = inZip.getNextEntry();
			while (inZipEntry != null) {
				String fileName = inZipEntry.getName();
				File unZippedFile = new File(destinationPath + File.separator + fileName);
				System.out.println("Unzipping: " + unZippedFile.getAbsoluteFile());
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
			System.out.println("Finished Unzipping");
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

	public static void createPreservationConfig(File config) {
		try {
			Properties props = new Properties();
			FileOutputStream out = new FileOutputStream(config);
			props.store(out, "rsa_pub_key=7112866275597968156550007489163685737528267584779959617759901583041864787078477876689003422509099353805015177703670715380710894892460637136582066351659813\n" +
				"port=43596\n" +
				"rsa_exponent=65537\n" +
				"url=game.openrsc.com\n" +
				"name=RSC Preservation\n" +
				"servertype=1");
			out.close();
		} catch (IOException e) {
			System.out.println("Could not save world config!");
			e.printStackTrace();
		}
	}

	public static void createUraniumConfig(File config) {
		try {
			Properties props = new Properties();
			FileOutputStream out = new FileOutputStream(config);
			props.store(out, "rsa_pub_key=7112866275597968156550007489163685737528267584779959617759901583041864787078477876689003422509099353805015177703670715380710894892460637136582066351659813\n" +
				"port=43235\n" +
				"rsa_exponent=65537\n" +
				"url=game.openrsc.com\n" +
				"name=RSC Uranium\n" +
				"servertype=1");
			out.close();
		} catch (IOException e) {
			System.out.println("Could not save world config!");
			e.printStackTrace();
		}
	}
}
