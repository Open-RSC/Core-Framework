package com.loader.openrsc.frame.threads;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.AppFrame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Statistics implements Runnable {

	@Override
	public void run() {
		// ORSC
		try {
			Document document = Jsoup.connect(Constants.ORSC_WORLD_STATS_URL).get();

			Elements linkOnline = document.select("a[href$=\"online\"]");
			for (Element online : linkOnline) {
				AppFrame.get().getOnline().setText("<html>Players Online: <span style='color:00FF00;'>" + online.text() + "</span></html>");
			}

			Elements linkLogins48 = document.select("a[href$=\"logins48\"]");
			for (Element logins48 : linkLogins48) {
				AppFrame.get().getLogins48().setText("<html>Online Last 48 Hours: <span style='color:00FF00;'>" + logins48.text() + "</span></html>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// RSCC
		try {
			Document document = Jsoup.connect(Constants.RSCC_WORLD_STATS_URL).get();

			Elements linkOnline = document.select("a[href$=\"online\"]");
			for (Element online : linkOnline) {
				AppFrame.get().getOnline().setText("<html>Players Online: <span style='color:00FF00;'>" + online.text() + "</span></html>");
			}

			Elements linkLogins48 = document.select("a[href$=\"logins48\"]");
			for (Element logins48 : linkLogins48) {
				AppFrame.get().getLogins48().setText("<html>Online Last 48 Hours: <span style='color:00FF00;'>" + logins48.text() + "</span></html>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
