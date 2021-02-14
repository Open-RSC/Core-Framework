package launcher.Gameupdater;

import java.io.File;

import launcher.Utils.ClientLauncher;

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
			File gamePath = new File(_CACHE_DIR + "/apos");
			if (!gamePath.exists() || !gamePath.isDirectory()) {
				Downloader gameUpdater = new Downloader(_CACHE_DIR + "/apos");
				gameUpdater.initUpdate();
				System.out.println("print");
			} else {
				System.out.println("print2");
				ClientLauncher.launchAPOS();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

}
