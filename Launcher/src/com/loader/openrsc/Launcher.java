package com.loader.openrsc;

import com.loader.openrsc.frame.AppFrame;
import com.loader.openrsc.frame.popup.PopupFrame;
import com.loader.openrsc.frame.threads.StatusChecker;
import com.loader.openrsc.frame.threads.Statistics;
import com.loader.openrsc.net.Downloader;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Launcher {
	private static PopupFrame popup;

	public static void main(final String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			Launcher.getPopup().setMessage("" + e);
		}
		Downloader updater = new Downloader();
		updater.updateJar();

		final AppFrame frame = new AppFrame();
		frame.build();
		Launcher.popup = new PopupFrame();
		String orsc = "orsc";
		String rscc = "rscc";
		String local = "local";

		// ORSC server status checker
		new Thread(new StatusChecker(Constants.ORSC_SERVER_DOMAIN, orsc, Constants.ORSC_SERVER_PORT)).start();

		// RSCC server status checker
		new Thread(new StatusChecker(Constants.RSCC_SERVER_DOMAIN, rscc, Constants.RSCC_SERVER_PORT)).start();

		// Localhost server status checker
		new Thread(new StatusChecker(Constants.LOCALHOST_SERVER_DOMAIN, local, Constants.LOCALHOST_SERVER_PORT)).start();

		// All game statistics via jsoup web scraper
		new Thread(new Statistics()).start();

		updater.init();
		updater.doneLoading();
	}

	public static PopupFrame getPopup() {
		return Launcher.popup;
	}

}
