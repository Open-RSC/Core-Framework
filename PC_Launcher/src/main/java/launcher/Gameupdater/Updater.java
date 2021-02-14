package launcher.Gameupdater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import launcher.Gameupdater.UpdaterGui.MainUpdaterGui;
import launcher.Utils.ClientLauncher;
import launcher.Utils.Defaults;

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

	public static void updateAPOS() throws SecurityException {
		try {
			File gamePath = new File(_CACHE_DIR + "/extras/apos");
			if (!gamePath.exists() || !gamePath.isDirectory()) {
				gamePath.mkdir();
				try {
					String URL = Defaults._APOS_REPOSITORY_DL;
					String filename = "apos-master.zip";
					URLConnection connection = new URL(URL).openConnection();
					String description = getDescription(new File(filename));
					int fileSize = connection.getContentLength();
					try (BufferedInputStream inputStream = new BufferedInputStream(new URL(URL).openStream());
						 FileOutputStream fileOS = new FileOutputStream(gamePath + File.separator + filename)) {
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
			}

			ClientLauncher.launchAPOS();
		} catch (IllegalArgumentException e) {
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
}
