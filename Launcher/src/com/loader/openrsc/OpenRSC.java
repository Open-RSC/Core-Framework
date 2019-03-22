package com.loader.openrsc;

import com.loader.openrsc.frame.AppFrame;
import com.loader.openrsc.frame.popup.PopupFrame;
import com.loader.openrsc.frame.threads.StatusChecker;
import com.loader.openrsc.frame.threads.Statistics;
import com.loader.openrsc.net.Downloader;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class OpenRSC {
	private static PopupFrame popup;

	public static void main(final String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			OpenRSC.getPopup().setMessage("" + e);
		}
		Downloader updater = new Downloader();
		updater.updateJar();

		final AppFrame frame = new AppFrame();
		frame.build();
		OpenRSC.popup = new PopupFrame();

		// ORSC server status checker
		new Thread(new StatusChecker(Constants.ORSC_SERVER_DOMAIN, Constants.ORSC_SERVER_PORT)).start();

		// RSCC server status checker
		new Thread(new StatusChecker(Constants.RSCC_SERVER_DOMAIN, Constants.RSCC_SERVER_PORT)).start();

		// All game statistics via jsoup web scraper
		new Thread(new Statistics()).start();

		updater.init();
		updater.doneLoading();
	}

	public static PopupFrame getPopup() {
		return OpenRSC.popup;
	}

}
