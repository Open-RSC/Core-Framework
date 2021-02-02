package com.loader.openrsc;

import java.io.File;

import com.loader.openrsc.frame.AppFrame;
import com.loader.openrsc.frame.popup.PopupFrame;
import com.loader.openrsc.frame.threads.Statistics;
import com.loader.openrsc.net.Downloader;

import javax.swing.*;

public class Launcher {
	private static PopupFrame popup;

	public static void main(final String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			Launcher.getPopup().setMessage("" + e);
		}

		Downloader updater = new Downloader();

		File configFile = new File(Constants.CONF_DIR + "/launcherSettings.conf");
		if (!configFile.exists()) {
			Settings.loadSettings();
			if (Settings.autoUpdate) {
				System.out.println("Attempting to update the program!"); // Warn user that this is happening
				try {
					updater.updateJar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		final AppFrame frame = new AppFrame();
		frame.build();
		Launcher.popup = new

			PopupFrame();

		// All game statistics via jsoup web scraper
		new

			Thread(new Statistics()).

			start();

		updater.init();
		updater.doneLoading();
	}

	public static PopupFrame getPopup() {
		return Launcher.popup;
	}

}
