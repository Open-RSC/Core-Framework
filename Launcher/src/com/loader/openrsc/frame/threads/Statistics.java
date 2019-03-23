package com.loader.openrsc.frame.threads;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.AppFrame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Statistics implements Runnable {

	@Override
	public void run() {
		// ORSC
		try {
			Document document = Jsoup.connect(Constants.ORSC_WORLD_STATS_URL).get();

			for (Element getorscOnline : document.select("a[href$=\"online\"]")) {
				AppFrame.get().getorscOnline().setText("<html>Players Online: <span style='color:00FF00;'>" + getorscOnline.text() + "</span></html>");
			}

			for (Element getorscLogins48 : document.select("a[href$=\"logins48\"]")) {
				AppFrame.get().getorscLogins48().setText("<html>Online Last 48 Hours: <span style='color:00FF00;'>" + getorscLogins48.text() + "</span></html>");
			}
		} catch (Exception ignored) {
		}

		// RSCC
		try {
			Document document = Jsoup.connect(Constants.RSCC_WORLD_STATS_URL).get();

			for (Element getrsccOnline : document.select("a[href$=\"online\"]")) {
				AppFrame.get().getrsccOnline().setText("<html>Players Online: <span style='color:00FF00;'>" + getrsccOnline.text() + "</span></html>");
			}

			for (Element getrsccLogins48 : document.select("a[href$=\"logins48\"]")) {
				AppFrame.get().getrsccLogins48().setText("<html>Online Last 48 Hours: <span style='color:00FF00;'>" + getrsccLogins48.text() + "</span></html>");
			}
		} catch (Exception ignored) {
		}
	}
}
