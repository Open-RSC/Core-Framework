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
		String rscc = "rscc";
		String orsc = "orsc";
		String openpk = "openpk";
		String rscp = "rscp";
		String local = "local";
		String dev = "dev";

		// ORSC server status checker
		new Thread(new StatusChecker(Constants.ORSC_SERVER_DOMAIN, orsc, Constants.ORSC_SERVER_PORT)).start();

		// RSCC server status checker
		new Thread(new StatusChecker(Constants.RSCC_SERVER_DOMAIN, rscc, Constants.RSCC_SERVER_PORT)).start();

		// OpenPK server status checker
		new Thread(new StatusChecker(Constants.OPENPK_SERVER_DOMAIN, openpk, Constants.OPENPK_SERVER_PORT)).start();

		// RSCP server status checker
		new Thread(new StatusChecker(Constants.RSCP_SERVER_DOMAIN, rscp, Constants.RSCP_SERVER_PORT)).start();

		// Localhost server status checker
		new Thread(new StatusChecker(Constants.LOCALHOST_SERVER_DOMAIN, local, Constants.LOCALHOST_SERVER_PORT)).start();

		// Dev World server status checker
		new Thread(new StatusChecker(Constants.DEV_SERVER_DOMAIN, dev, Constants.DEV_SERVER_PORT)).start();

		// All game statistics via jsoup web scraper
		new Thread(new Statistics()).start();

		updater.init();
		updater.doneLoading();
	}

	public static PopupFrame getPopup() {
		return Launcher.popup;
	}

}
