package com.openrsc.server.content.market;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.struct.AuctionItem;
import com.openrsc.server.database.struct.ExpiredAuction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			PreparedStatement statement = getMarket().getWorld().getServer().getDatabase().getConnection()
				.prepareStatement("SELECT `auctionID`, `itemID`, `amount`, `amount_left`, `price`, `seller`, `seller_username`, `buyer_info`, `time` FROM `" + getMarket().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
					+ "auctions` WHERE `auctionID`= ? AND `sold-out` = '0'");
			statement.setInt(1, auctionID);

			ResultSet result = statement.executeQuery();
			MarketItem retVal = null;
			try {
				if (result.next()) {
					retVal = new MarketItem(result.getInt("auctionID"), result.getInt("itemID"),
						result.getInt("amount"), result.getInt("amount_left"), result.getInt("price"),
						result.getInt("seller"), result.getString("seller_username"), result.getString("buyer_info"), result.getLong("time"));
				}
			} finally {
				statement.close();
				result.close();
			}

			return retVal;
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		return null;
	}

	public ArrayList<MarketItem> getAuctionItemsOnSale() {
		ArrayList<MarketItem> auctionItems = new ArrayList<>();
		try {
			PreparedStatement statement = getMarket().getWorld().getServer().getDatabase().getConnection()
				.prepareStatement("SELECT `auctionID`, `itemID`, `amount`, `amount_left`, `price`, `seller`, `seller_username`, `buyer_info`, `time` FROM `" + getMarket().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
					+ "auctions` WHERE `sold-out`='0'");
			ResultSet result = statement.executeQuery();
			try {
				while (result.next()) {
					MarketItem auctionItem = new MarketItem(result.getInt("auctionID"), result.getInt("itemID"),
						result.getInt("amount"), result.getInt("amount_left"), result.getInt("price"),
						result.getInt("seller"), result.getString("seller_username"), result.getString("buyer_info"), result.getLong("time"));
					auctionItems.add(auctionItem);
				}
			} finally {
				statement.close();
				result.close();
			}
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return auctionItems;
	}

	public boolean setSoldOut(MarketItem item) {
		try {
			PreparedStatement statement = getMarket().getWorld().getServer().getDatabase().getConnection()
				.prepareStatement("UPDATE `" + getMarket().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
					+ "auctions` SET `amount_left`=?, `sold-out`=?, `buyer_info`=? WHERE `auctionID`=?");
			statement.setInt(1, item.getAmountLeft());
			statement.setInt(2, 1);
			statement.setString(3, item.getBuyers());
			statement.setInt(4, item.getAuctionID());
			statement.executeUpdate();
			return true;
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public boolean update(MarketItem item) {
		try {
			PreparedStatement statement = getMarket().getWorld().getServer().getDatabase().getConnection().prepareStatement(
				"UPDATE `" + getMarket().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
					+ "auctions` SET `amount_left`=?, `price` = ?, `buyer_info`=? WHERE `auctionID`= ?");
			statement.setInt(1, item.getAmountLeft());
			statement.setInt(2, item.getPrice());
			statement.setString(3, item.getBuyers());
			statement.setInt(4, item.getAuctionID());
			try{statement.executeUpdate();}
			finally{statement.close();}
			return true;
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public ArrayList<CollectableItem> getCollectableItemsFor(int player) {
		ArrayList<CollectableItem> list = new ArrayList<>();
		try {
			PreparedStatement statement = getMarket().getWorld().getServer().getDatabase().getConnection().prepareStatement("SELECT `claim_id`, `item_id`, `item_amount`, `playerID`, `explanation` FROM `" + getMarket().getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX
				+ "expired_auctions` WHERE `playerID` = ?  AND `claimed`= '0'");
			statement.setInt(1, player);
			ResultSet result = statement.executeQuery();
			try {
				while (result.next()) {
					CollectableItem item = new CollectableItem();
					item.claim_id = result.getInt("claim_id");
					item.item_id = result.getInt("item_id");
					item.item_amount = result.getInt("item_amount");
					item.playerID = result.getInt("playerID");
					item.explanation = result.getString("explanation");
					list.add(item);
				}
			} finally {
				statement.close();
				result.close();
			}
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		return list;
	}

	public Market getMarket() {
		return market;
	}
}
