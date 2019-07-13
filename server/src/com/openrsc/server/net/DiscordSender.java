package com.openrsc.server.net;

import com.openrsc.server.Constants;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.external.EntityHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class DiscordSender {

	private static final Logger LOGGER = LogManager.getLogger();

	public static void auctionAdd(MarketItem addItem) {
		String pluralHandlerMessage = addItem.getAmount() > 1
				? "%d x %s, priced at %d coins each, auctioned by %s.  %d hours left."
				: "%d x %s, priced at %d coins, auctioned by %s.  %d hours left.";

		String addMessage = String.format(pluralHandlerMessage,
				addItem.getAmount(),
				EntityHandler.getItemDef(addItem.getItemID()).getName(),
				addItem.getPrice() / addItem.getAmount(),
				addItem.getSellerName(),
				addItem.getHoursLeft()
		);
		try {
			sendToDiscord(addMessage);
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	public static void auctionBuy(MarketItem buyItem) {
		String buyMessage = String.format("%s purchased from %s.  %d left in auction.",
				EntityHandler.getItemDef(buyItem.getItemID()).getName(),
				buyItem.getSellerName(),
				buyItem.getAmountLeft()
		);
		try {
			sendToDiscord(buyMessage);
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	public static void auctionCancel(MarketItem cancelItem) {
		String cancelMessage = String.format("%d x %s cancelled from auction by %s.",
				cancelItem.getAmount(),
				EntityHandler.getItemDef(cancelItem.getItemID()).getName(),
				cancelItem.getSellerName()
		);
		try {
			sendToDiscord(cancelMessage);
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	public static void auctionModDelete(MarketItem deleteItem) {
		String cancelMessage = String.format("%d x %s, auctioned by %s, has been deleted by moderator.",
				deleteItem.getAmount(),
				EntityHandler.getItemDef(deleteItem.getItemID()).getName(),
				deleteItem.getSellerName()
		);
		try {
			sendToDiscord(cancelMessage);
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	private static void sendToDiscord(String auctionMessage) throws Exception {

		String jsonPostBody = String.format("{\"content\": \"%s\"}", auctionMessage);

		java.net.URL url = new java.net.URL(Constants.GameServer.DISCORD_WEBHOOK_URL);

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
