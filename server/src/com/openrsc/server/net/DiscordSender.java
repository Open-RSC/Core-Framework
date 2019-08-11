package com.openrsc.server.net;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.content.market.MarketItem;
import com.openrsc.server.external.EntityHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.JsonUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class DiscordSender {

	private static final Logger LOGGER	= LogManager.getLogger();
	private long monitoringLastUpdate	= 0;

	public void auctionAdd(MarketItem addItem) {
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
			auctionSendToDiscord(addMessage);
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	public void auctionBuy(MarketItem buyItem) {
		String buyMessage = String.format("%s purchased from %s.  %d left in auction.",
				EntityHandler.getItemDef(buyItem.getItemID()).getName(),
				buyItem.getSellerName(),
				buyItem.getAmountLeft()
		);
		try {
			auctionSendToDiscord(buyMessage);
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	public void auctionCancel(MarketItem cancelItem) {
		String cancelMessage = String.format("%d x %s cancelled from auction by %s.",
				cancelItem.getAmount(),
				EntityHandler.getItemDef(cancelItem.getItemID()).getName(),
				cancelItem.getSellerName()
		);
		try {
			auctionSendToDiscord(cancelMessage);
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	public void auctionModDelete(MarketItem deleteItem) {
		String cancelMessage = String.format("%d x %s, auctioned by %s, has been deleted by moderator.",
				deleteItem.getAmount(),
				EntityHandler.getItemDef(deleteItem.getItemID()).getName(),
				deleteItem.getSellerName()
		);
		try {
			auctionSendToDiscord(cancelMessage);
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	public void monitoringSendServerBehind(String message) {
		try {
			monitoringSendToDiscord(message + "\r\n" + Server.getServer().buildProfilingDebugInformation(false));
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	private void auctionSendToDiscord(String message) throws Exception {
		if(Constants.GameServer.WANT_DISCORD_AUCTION_UPDATES) {
			sendToDiscord(Constants.GameServer.DISCORD_MONITORING_WEBHOOK_URL, message);
		}
	}

	private void monitoringSendToDiscord(String message) throws Exception {
		final long now 				= System.currentTimeMillis();

		if(now >= (monitoringLastUpdate + 3600) && Constants.GameServer.WANT_DISCORD_MONITORING_UPDATES) {
			sendToDiscord(Constants.GameServer.DISCORD_MONITORING_WEBHOOK_URL, message);
			monitoringLastUpdate	= now;
		}
	}

	private static void sendToDiscord(String webhookUrl, String message) throws Exception {
		StringBuilder sb = new StringBuilder();
		JsonUtils.quoteAsString(message, sb);

		String jsonPostBody = String.format("{\"content\": \"%s\"}", sb);

		java.net.URL url = new java.net.URL(webhookUrl);

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
