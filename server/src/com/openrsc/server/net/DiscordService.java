package com.openrsc.server.net;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.Server;
import com.openrsc.server.content.market.MarketItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.JsonUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordService implements Runnable{

	private final ScheduledExecutorService scheduledExecutor;

	private Queue<String> auctionRequests = new ConcurrentLinkedQueue<String>();
	private Queue<String> monitoringRequests = new ConcurrentLinkedQueue<String>();

	private static final Logger LOGGER	= LogManager.getLogger();
	private long monitoringLastUpdate	= 0;
	private Boolean running				= false;

	private final Server server;

	public DiscordService(Server server) {
		this.server = server;

		scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getServer().getName()+" : DiscordServiceThread").build());
	}

	public final Server getServer() {
		return server;
	}

	public void auctionAdd(MarketItem addItem) {
		String pluralHandlerMessage = addItem.getAmount() > 1
				? "%d x %s, priced at %d coins each, auctioned by %s.  %d hours left."
				: "%d x %s, priced at %d coins, auctioned by %s.  %d hours left.";

		String addMessage = String.format(pluralHandlerMessage,
				addItem.getAmount(),
				getServer().getEntityHandler().getItemDef(addItem.getItemID()).getName(),
				addItem.getPrice() / addItem.getAmount(),
				addItem.getSellerName(),
				addItem.getHoursLeft()
		);

		auctionSendToDiscord(addMessage);
	}

	public void auctionBuy(MarketItem buyItem) {
		String buyMessage = String.format("%s purchased from %s.  %d left in auction.",
			getServer().getEntityHandler().getItemDef(buyItem.getItemID()).getName(),
				buyItem.getSellerName(),
				buyItem.getAmountLeft()
		);

		auctionSendToDiscord(buyMessage);
	}

	public void auctionCancel(MarketItem cancelItem) {
		String cancelMessage = String.format("%d x %s cancelled from auction by %s.",
				cancelItem.getAmount(),
				getServer().getEntityHandler().getItemDef(cancelItem.getItemID()).getName(),
				cancelItem.getSellerName()
		);

		auctionSendToDiscord(cancelMessage);
	}

	public void auctionModDelete(MarketItem deleteItem) {
		String cancelMessage = String.format("%d x %s, auctioned by %s, has been deleted by moderator.",
				deleteItem.getAmount(),
				getServer().getEntityHandler().getItemDef(deleteItem.getItemID()).getName(),
				deleteItem.getSellerName()
		);

		auctionSendToDiscord(cancelMessage);
	}

	public void monitoringSendServerBehind(String message) {
		try {
			monitoringSendToDiscord(message + "\r\n" + getServer().buildProfilingDebugInformation(false));
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

	private void auctionSendToDiscord(String message) {
		if(getServer().getConfig().WANT_DISCORD_AUCTION_UPDATES) {
			auctionRequests.add(message);
		}
	}

	private void monitoringSendToDiscord(String message) {
		final long now = System.currentTimeMillis();

		if(getServer().getConfig().WANT_DISCORD_MONITORING_UPDATES && now >= (monitoringLastUpdate + 3600)) {
			monitoringRequests.add(message);
			monitoringLastUpdate = now;
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

	@Override
	public void run()  {
		synchronized(running) {
			String message = null;

			try {
				while ((message = auctionRequests.poll()) != null) {
					sendToDiscord(getServer().getConfig().DISCORD_AUCTION_WEBHOOK_URL, message);
				}

				while ((message = monitoringRequests.poll()) != null) {
					sendToDiscord(getServer().getConfig().DISCORD_MONITORING_WEBHOOK_URL, message);
				}
			} catch (Exception e) {
				LOGGER.catching(e);
			}
		}
	}

	public void start() {
		synchronized(running) {
			running = true;
			scheduledExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
		}
	}

	public void stop() {
		synchronized(running) {
			running = false;
			scheduledExecutor.shutdown();
		}
	}

	public final boolean isRunning() {
		return running;
	}
}
