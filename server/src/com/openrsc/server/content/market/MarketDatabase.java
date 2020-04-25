package com.openrsc.server.content.market;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.AuctionItem;
import com.openrsc.server.database.struct.ExpiredAuction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class MarketDatabase {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Market market;

	public MarketDatabase(Market market) {
		this.market = market;
	}

	public boolean add(MarketItem item) {
		try {
			AuctionItem auctionItem = new AuctionItem();
			auctionItem.itemID = item.getCatalogID();
			auctionItem.amount = item.getAmount();
			auctionItem.amount_left = item.getAmountLeft();
			auctionItem.price = item.getPrice();
			auctionItem.seller = item.getSeller();
			auctionItem.seller_username = item.getSellerName();
			auctionItem.buyer_info = item.getBuyers();
			auctionItem.time = item.getTime();

			getMarket().getWorld().getServer().getDatabase().newAuction(auctionItem);

			return true;
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
			return false;
		}
	}

	public boolean addCollectableItem(String explanation, final int itemIndex, final int amount,
											 final int playerID) {

		final String finalExplanation = explanation.replaceAll("'", "");
		try {
			// Need to store in an array to pass to the database function;
			ExpiredAuction[] expiredAuctions = new ExpiredAuction[1];

			ExpiredAuction expiredAuction = new ExpiredAuction();
			expiredAuction.item_id = itemIndex;
			expiredAuction.item_amount = amount;
			expiredAuction.time = System.currentTimeMillis() / 1000;
			expiredAuction.playerID = playerID;
			expiredAuction.explanation = finalExplanation;

			expiredAuctions[0] = expiredAuction;
			getMarket().getWorld().getServer().getDatabase().addExpiredAuction(expiredAuctions);
			return true;
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
			return false;
		}
	}

	public boolean cancel(MarketItem item) {
		try {
			getMarket().getWorld().getServer().getDatabase().cancelAuction(item.getAuctionID());
			return true;
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
			return false;
		}
	}

	public int getAuctionCount() {
		try {
			return getMarket().getWorld().getServer().getDatabase().auctionCount();
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
		}
		return 0;
	}

	public int getMyAuctionsCount(int ownerID) {
		try {
			return getMarket().getWorld().getServer().getDatabase().playerAuctionCount(ownerID);
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
		}
		return 0;
	}

	public MarketItem getAuctionItem(int auctionID) {
		try {
			MarketItem retVal = null;
			AuctionItem auctionItem = getMarket().getWorld().getServer().getDatabase().getAuctionItem(auctionID);
			if (auctionItem != null) {
				retVal = new MarketItem(auctionItem.auctionID, auctionItem.itemID, auctionItem.amount,
					auctionItem.amount_left, auctionItem.price, auctionItem.seller, auctionItem.seller_username,
					auctionItem.buyer_info, auctionItem.time);
			}
			return retVal;
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
			return null;
		}
	}

	public ArrayList<MarketItem> getAuctionItemsOnSale() {
		ArrayList<MarketItem> marketItems = new ArrayList<>();
		try {
			AuctionItem auctionItems[] = getMarket().getWorld().getServer().getDatabase().getAuctionItems();
			for (AuctionItem item : auctionItems) {
				MarketItem marketItem = new MarketItem(item.auctionID, item.itemID, item.amount, item.amount_left,
					item.price, item.seller,item.seller_username,item.buyer_info,item.time);
				marketItems.add(marketItem);
			}
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
		}
		return marketItems;
	}

	public boolean setSoldOut(MarketItem item) {
		try {
			AuctionItem auctionItem = new AuctionItem();
			auctionItem.amount_left = item.getAmountLeft();
			auctionItem.sold_out = 1;
			auctionItem.buyer_info = item.getBuyers();
			auctionItem.auctionID = item.getAuctionID();

			getMarket().getWorld().getServer().getDatabase().setSoldOut(auctionItem);

			return true;
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
			return false;
		}
	}

	public boolean update(MarketItem item) {
		try {
			AuctionItem auctionItem = new AuctionItem();
			auctionItem.amount_left = item.getAmountLeft();
			auctionItem.price = item.getPrice();
			auctionItem.buyer_info = item.getBuyers();
			auctionItem.auctionID = item.getAuctionID();

			getMarket().getWorld().getServer().getDatabase().updateAuction(auctionItem);
			return true;
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
			return false;
		}
	}

	public ArrayList<CollectibleItem> getCollectibleItemsFor(int player) {
		ArrayList<CollectibleItem> list = new ArrayList<>();
		try {
			ExpiredAuction expiredAuctions[] = getMarket().getWorld().getServer().getDatabase().getCollectibleItems(player);
			for (ExpiredAuction collectible : expiredAuctions) {
				CollectibleItem item = new CollectibleItem();
				item.claim_id = collectible.claim_id;
				item.item_id = collectible.item_id;
				item.item_amount = collectible.item_amount;
				item.playerID = collectible.playerID;
				item.explanation = collectible.explanation;
				list.add(item);
			}
		} catch (GameDatabaseException e) {
			LOGGER.catching(e);
		}
		return list;
	}

	public Market getMarket() {
		return market;
	}
}
