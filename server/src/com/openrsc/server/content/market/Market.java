package com.openrsc.server.content.market;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.content.market.task.*;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
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

	public Market(World world) {
		this.world = world;
		this.auctionItems = new ArrayList<>();
		this.auctionTaskQueue = new LinkedBlockingQueue<>();
		this.refreshRequestTasks = new LinkedBlockingQueue<>();
		this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getWorld().getServer().getName()+" : AuctionHouseThread").build());
		this.marketDatabase = new MarketDatabase(this);
		this.running = false;
	}

	public void addBuyAuctionItemTask(final Player player, int auctionID, int amount) {
		auctionTaskQueue.add(new BuyMarketItemTask(player, auctionID, amount));
	}

	public void addCancelAuctionItemTask(final Player player, final int auctionID) {
		auctionTaskQueue.add(new CancelMarketItemTask(player, auctionID));
	}

	public void addNewAuctionItemTask(final Player player, int itemID, final int amount, final int price) {
		auctionTaskQueue.add(new NewMarketItemTask(player, new MarketItem(-1, itemID, amount, amount, price,
			player.getDatabaseID(), player.getUsername(), "", System.currentTimeMillis() / 1000)));
	}

	public void addRequestOpenAuctionHouseTask(final Player player) {
		refreshRequestTasks.add(new OpenMarketTask(player));
	}

	public void addCollectableItemsNotificationTask(Player player) {
		auctionTaskQueue.add(new CollectableItemsNotificationTask(player));
	}

	public void addPlayerCollectItemsTask(Player player) {
		auctionTaskQueue.add(new PlayerCollectItemsTask(player));
	}

	public void addModeratorDeleteItemTask(Player player, int auctionID) {
		auctionTaskQueue.add(new ModeratorDeleteAuctionTask(player, auctionID));
	}

	private void checkAndRemoveExpiredItems() {
		try {
			LinkedList<MarketItem> expiredItems = new LinkedList<>();

			for (MarketItem auction : auctionItems)
				if (auction.hasExpired()) expiredItems.add(auction);

			if (expiredItems.size() != 0) {
				PreparedStatement expiredItemsStatement = getWorld().getServer().getDatabaseConnection().prepareStatement(
					"INSERT INTO `" + getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
						+ "expired_auctions`(`item_id`, `item_amount`, `time`, `playerID`, `explanation`) VALUES (?,?,?,?,?)");
				for (MarketItem expiredItem : expiredItems) {

					int itemIndex = expiredItem.getItemID();
					int amount = expiredItem.getAmountLeft();

					Player sellerPlayer = getWorld().getPlayerID(expiredItem.getSeller());
					getMarketDatabase().setSoldOut(expiredItem);

					expiredItemsStatement.setInt(1, itemIndex);
					expiredItemsStatement.setInt(2, amount);
					expiredItemsStatement.setLong(3, System.currentTimeMillis() / 1000);
					expiredItemsStatement.setInt(4, expiredItem.getSeller());
					expiredItemsStatement.setString(5, "Expired");
					expiredItemsStatement.addBatch();

					ItemDefinition def = sellerPlayer.getWorld().getServer().getEntityHandler().getItemDef(itemIndex);
					if (sellerPlayer != null) {
						sellerPlayer.message("@gre@[Auction House] @whi@Your auction - @lre@" + def.getName() + " x" + amount
							+ "@whi@ has expired!");
						sellerPlayer.message("You can collect it back from a banker.");
					}
				}
				expiredItemsStatement.executeBatch();
			}
			lastCleanUp = System.currentTimeMillis();
		} catch (Throwable e) {
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
		int activeAuctionCount = getMarketDatabase().getAuctionCount();
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
			} catch (Throwable r) {
				LOGGER.catching(r);
			}
		}
	}

	public void stop() {
		synchronized(running) {
			running = false;
			scheduledExecutor.shutdown();
		}
	}

	public void start() {
		synchronized(running) {
			running = true;
			scheduledExecutor.scheduleAtFixedRate(this, 50, 50, TimeUnit.MILLISECONDS);
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
