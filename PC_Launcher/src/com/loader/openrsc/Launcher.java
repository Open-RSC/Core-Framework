package com.loader.openrsc;

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
		updater.updateJar();

		final AppFrame frame = new AppFrame();
		frame.build();
		Launcher.popup = new PopupFrame();

		// All game statistics via jsoup web scraper
		new Thread(new Statistics()).start();

		updater.init();
		updater.doneLoading();
	}

	public static PopupFrame getPopup() {
		return Launcher.popup;
	}

}
