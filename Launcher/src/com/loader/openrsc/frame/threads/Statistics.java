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

			for (Element getrsccOnline : document.select("a[href$=\"cabbageonline\"]")) {
				AppFrame.get().getrsccOnline().setText("<html>Players Online: <span style='color:00FF00;'>" + getrsccOnline.text() + "</span></html>");
			}

			for (Element getrsccLogins48 : document.select("a[href$=\"cabbagelogins48\"]")) {
				AppFrame.get().getrsccLogins48().setText("<html>Online Last 48 Hours: <span style='color:00FF00;'>" + getrsccLogins48.text() + "</span></html>");
			}
		} catch (Exception ignored) {
		}

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

		/*// OpenPK
		try {
			Document document = Jsoup.connect(Constants.OPENPK_WORLD_STATS_URL).get();

			for (Element getopenpkOnline : document.select("a[href$=\"online\"]")) {
				AppFrame.get().getrsccOnline().setText("<html>Players Online: <span style='color:00FF00;'>" + getopenpkOnline.text() + "</span></html>");
			}

			for (Element getopenpkLogins48 : document.select("a[href$=\"logins48\"]")) {
				AppFrame.get().getrsccLogins48().setText("<html>Online Last 48 Hours: <span style='color:00FF00;'>" + getopenpkLogins48.text() + "</span></html>");
			}
		} catch (Exception ignored) {
		}

		// RSCP
		try {
			Document document = Jsoup.connect(Constants.RSCP_WORLD_STATS_URL).get();

			for (Element getrscpOnline : document.select("a[href$=\"online\"]")) {
				AppFrame.get().getrsccOnline().setText("<html>Players Online: <span style='color:00FF00;'>" + getrscpOnline.text() + "</span></html>");
			}

			for (Element getrscpLogins48 : document.select("a[href$=\"logins48\"]")) {
				AppFrame.get().getrsccLogins48().setText("<html>Online Last 48 Hours: <span style='color:00FF00;'>" + getrscpLogins48.text() + "</span></html>");
			}
		} catch (Exception ignored) {
		}*/
	}
}
