package com.openrsc.server.net;

import com.openrsc.server.external.EntityHandler;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class DiscordSender {

	private static String discordUrl = "https://discordapp.com/api/webhooks/597525640895397900/MVZYoeHXhju1_h74NwT4Kffv3g1wpNsNLeWl7YhhECSOXNuwwTXrR3_YJcGRGFpAOcFu";

	public static void auctionAdd(int itemID, int price, int amount, String player) {
		String addMessage = String.format("{\"content\": \"%s has auctioned %d x %s, priced at %d.\"}", player, amount, EntityHandler.getItemDef(itemID).getName(), price);
		try {
			sendToDiscord(addMessage);
		} catch(Exception e) {
			// swallow for now
		}
	}

	private static void sendToDiscord(String auctionMessage) throws Exception {

		String jsonPostBody = String.format("{\"content\": \"%s\"}", auctionMessage);

		java.net.URL url = new java.net.URL(discordUrl);

		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestMethod("POST");
		http.setDoOutput(true);

		byte[] out = jsonPostBody.getBytes(StandardCharsets.UTF_8);
		int length = out.length;

		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		http.connect();
		try (OutputStream os = http.getOutputStream()) {
			os.write(out);
		}
	}
}
