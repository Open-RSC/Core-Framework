package com.loader.openrsc.frame.threads;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.AppFrame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Statistics implements Runnable {

	@Override
	public void run() {
		// RSCC
		try {
			Document document = Jsoup.connect(Constants.RSCC_WORLD_STATS_URL).get();

			for (Element getrsccOnline : document.select("a[href$=\"online\"]")) {
				AppFrame.get().getrsccOnline().setText("<html>Players Online: <span style='color:90c040;'>" + getrsccOnline.text() + "</span></html>");
			}
		} catch (Exception ignored) {
		}

		// ORSC
		try {
			Document document = Jsoup.connect(Constants.ORSC_WORLD_STATS_URL).get();

			for (Element getorscOnline : document.select("a[href$=\"online\"]")) {
				AppFrame.get().getorscOnline().setText("<html>Players Online: <span style='color:90c040;'>" + getorscOnline.text() + "</span></html>");
			}
		} catch (Exception ignored) {
		}

		// RSCP
		try {
			Document document = Jsoup.connect(Constants.RSCP_WORLD_STATS_URL).get();

			for (Element getrscpOnline : document.select("a[href$=\"NULLonline\"]")) { // Broken on purpose until a website is operating
				AppFrame.get().getrscpOnline().setText("<html>Players Online: <span style='color:90c040;'>" + getrscpOnline.text() + "</span></html>");
			}
		} catch (Exception ignored) {
		}

		// DEV
		try {
			Document document = Jsoup.connect(Constants.DEV_WORLD_STATS_URL).get();

			for (Element getdevOnline : document.select("a[href$=\"NULLonline\"]")) { // Broken on purpose until a website is operating
				AppFrame.get().getdevOnline().setText("<html>Players Online: <span style='color:90c040;'>" + getdevOnline.text() + "</span></html>");
			}
		} catch (Exception ignored) {
		}
	}
}
