package com.openrsc.server.content.market;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.content.market.task.*;
import com.openrsc.server.database.struct.ExpiredAuction;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Market implements Runnable {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final World world;

	private final MarketDatabase marketDatabase;

	private Boolean running;

	private ArrayList<MarketItem> auctionItems;
	private LinkedBlockingQueue<MarketTask> auctionTaskQueue;
	private long lastCleanUp = 0;
	private LinkedBlockingQueue<OpenMarketTask> refreshRequestTasks;
	private ScheduledExecutorService scheduledExecutor;

	public Market(final World world) {
		this.world = world;
		this.auctionItems = new ArrayList<>();
		this.auctionTaskQueue = new LinkedBlockingQueue<>();
		this.refreshRequestTasks = new LinkedBlockingQueue<>();
		this.marketDatabase = new MarketDatabase(this);
		this.running = false;
	}

	public void addBuyAuctionItemTask(final Player player, final int auctionID, final int amount) {
		auctionTaskQueue.add(new BuyMarketItemTask(player, auctionID, amount));
	}

	public void addCancelAuctionItemTask(final Player player, final int auctionID) {
		auctionTaskQueue.add(new CancelMarketItemTask(player, auctionID));
	}

	public void addNewAuctionItemTask(final Player player, final int itemID, final int amount, final int price) {
		auctionTaskQueue.add(new NewMarketItemTask(player, new MarketItem(-1, itemID, amount, amount, price,
			player.getDatabaseID(), player.getUsername(), "", System.currentTimeMillis() / 1000)));
	}

	public void addRequestOpenAuctionHouseTask(final Player player) {
		refreshRequestTasks.add(new OpenMarketTask(player));
	}

	public void addCollectableItemsNotificationTask(final Player player) {
		auctionTaskQueue.add(new CollectibleItemsNotificationTask(player));
	}

	public void addPlayerCollectItemsTask(final Player player) {
		auctionTaskQueue.add(new PlayerCollectItemsTask(player));
	}

	public void addModeratorDeleteItemTask(final Player player, final int auctionID) {
		auctionTaskQueue.add(new ModeratorDeleteAuctionTask(player, auctionID));
	}

	private void checkAndRemoveExpiredItems() {
		try {
			// This is used to handle game logic
			final LinkedList<MarketItem> expiredItems = new LinkedList<>();

			// This is used to save to the database.
			final ArrayList<ExpiredAuction> expiredAuctions = new ArrayList<ExpiredAuction>();

			for (final MarketItem auction : auctionItems) {
				if (auction.hasExpired()) {
					expiredItems.add(auction);

					// Create an entry to be saved to the database
					final ExpiredAuction expiredAuction = new ExpiredAuction();
					expiredAuction.item_id = auction.getCatalogID();
					expiredAuction.item_amount = auction.getAmountLeft();
					expiredAuction.time = System.currentTimeMillis() / 1000;
					expiredAuction.playerID = auction.getSeller();
					expiredAuction.explanation = "Expired";
					expiredAuctions.add(expiredAuction);
				}
			}

			if (expiredItems.size() != 0) {
				// Save to the database
				getWorld().getServer().getDatabase()
					.addExpiredAuction(expiredAuctions.toArray(new ExpiredAuction[expiredAuctions.size()]));

				// Preform game logic
				for (final MarketItem expiredItem : expiredItems) {
					final int itemIndex = expiredItem.getCatalogID();
					final int amount = expiredItem.getAmountLeft();

					final Player sellerPlayer = getWorld().getPlayerID(expiredItem.getSeller());
					getMarketDatabase().setSoldOut(expiredItem);

					if (sellerPlayer != null) {
						ItemDefinition def = sellerPlayer.getWorld().getServer().getEntityHandler().getItemDef(itemIndex);
						sellerPlayer.message("@gre@[Auction House] @whi@Your auction - @lre@" + def.getName() + " x" + amount
							+ "@whi@ has expired!");
						sellerPlayer.message("You can collect it back from a banker.");
					}
				}
			}
			lastCleanUp = System.currentTimeMillis();
		} catch (final Throwable e) {
			LOGGER.catching(e);
		}
	}

	public ArrayList<MarketItem> getAuctionItems() {
		return auctionItems;
	}

	private void processAuctionTasks() {
		MarketTask nextTask;
		while ((nextTask = auctionTaskQueue.poll()) != null) try {
			nextTask.doTask();
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	private void processRefreshRequests() {
		MarketTask refreshTask;
		while ((refreshTask = refreshRequestTasks.poll()) != null) try {
			refreshTask.doTask();
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	private void processUpdateAuctionItemCache() {
		final int activeAuctionCount = getMarketDatabase().getAuctionCount();
		if (activeAuctionCount == auctionItems.size()) return;
		auctionItems.clear();
		auctionItems = getMarketDatabase().getAuctionItemsOnSale();
	}

	@Override
	public void run() {
		synchronized (running) {
			try {
				if (System.currentTimeMillis() - lastCleanUp > 60000) checkAndRemoveExpiredItems();
				processAuctionTasks();
				processUpdateAuctionItemCache();
				processRefreshRequests();
			} catch (final Throwable r) {
				LOGGER.catching(r);
			}
		}
	}

	public void stop() {
		synchronized(running) {
			// Process the rest of the Market tasks.
			auctionItems.clear();
			scheduledExecutor.shutdown();
			try {
				scheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
			} catch (final InterruptedException e) {
				LOGGER.catching(e);
			}
			scheduledExecutor = null;
			running = false;
		}
	}

	public void start() {
		synchronized(running) {
			this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getWorld().getServer().getName()+" : AuctionHouseThread").build());
			scheduledExecutor.scheduleAtFixedRate(this, 50, 50, TimeUnit.MILLISECONDS);
			running = true;
			LOGGER.info("Market executor running");
		}
	}

	public World getWorld() {
		return world;
	}

	public MarketDatabase getMarketDatabase() {
		return marketDatabase;
	}
}
