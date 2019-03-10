package com.loader.openrsc.frame.threads;

import com.loader.openrsc.frame.AppFrame;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WorldPopulation implements Runnable {

	@Override
	public void run() {
		try {
			Document document = Jsoup.connect("https://openrsc.com/stats").get();

			Elements linkOnline = document.select("a[href$=\"online\"]");
			for (Element online : linkOnline) {
				AppFrame.get().getOnline().setText("<html>Players Online: <span style='color:00FF00;'>" + online.text() + "</span></html>");
			}

			Elements linkLogins48 = document.select("a[href$=\"logins48\"]");
			for (Element logins48 : linkLogins48) {
				AppFrame.get().getLogins48().setText("<html>Online Last 48 Hours: <span style='color:00FF00;'>" + logins48.text() + "</span></html>");
			}

			Elements linkRegistrationsToday = document.select("a[href$=\"registrationstoday\"]");
			for (Element registrationstoday : linkRegistrationsToday) {
				AppFrame.get().getRegistrationsToday().setText("<html>Registrations Today: <span style='color:00FF00;'>" + registrationstoday.text() + "</span></html>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
