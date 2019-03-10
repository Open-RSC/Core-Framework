package com.loader.openrsc;

import com.loader.openrsc.frame.AppFrame;
import com.loader.openrsc.frame.popup.PopupFrame;
import com.loader.openrsc.frame.threads.StatusChecker;
import com.loader.openrsc.frame.threads.WorldPopulation;
import com.loader.openrsc.net.Downloader;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class OpenRSC {
	private static PopupFrame popup;

	public static void main(final String[] args) throws IOException {
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
		new Thread(new StatusChecker(Constants.SERVER_DOMAIN, Constants.SERVER_PORT)).start();
		new Thread(new WorldPopulation()).start();
		updater.init();
		updater.doneLoading();
	}

	public static PopupFrame getPopup() {
		return OpenRSC.popup;
	}

}
