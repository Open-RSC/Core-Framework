package com.openrsc.server.content.market;

import com.openrsc.server.Constants;
import com.openrsc.server.sql.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MarketDatabase {

	public final static DatabaseConnection databaseInstance = new DatabaseConnection("MARKET_DB");
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	public static boolean add(MarketItem item) {
		try {
			PreparedStatement statement = databaseInstance.prepareStatement(
				"INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "auctions`(`itemID`, `amount`, `amount_left`, `price`, `seller`, `seller_username`, `buyer_info`, `time`) VALUES (?,?,?,?,?,?,?,?)");
			statement.setInt(1, item.getItemID());
			statement.setInt(2, item.getAmount());
			statement.setInt(3, item.getAmountLeft());
			statement.setInt(4, item.getPrice());
			statement.setInt(5, item.getSeller());
			statement.setString(6, item.getSellerName());
			statement.setString(7, item.getBuyers());
			statement.setLong(8, item.getTime());
			statement.executeUpdate();
			return true;
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public static boolean addCollectableItem(String explanation, final int itemIndex, final int amount,
											 final int playerID) {

		final String finalExplanation = explanation.replaceAll("'", "");
		try {
			PreparedStatement statement = databaseInstance.prepareStatement(
				"INSERT INTO `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "expired_auctions`(`item_id`, `item_amount`, `time`, `playerID`, `explanation`) VALUES (?,?,?,?,?)");
			statement.setInt(1, itemIndex);
			statement.setInt(2, amount);
			statement.setLong(3, System.currentTimeMillis() / 1000);
			statement.setInt(4, playerID);
			statement.setString(5, finalExplanation);
			statement.executeUpdate();
			return true;
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public static boolean cancel(MarketItem item) {
		try {
			PreparedStatement statement = databaseInstance
				.prepareStatement("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "auctions` SET  `sold-out`='1', `was_cancel`='1' WHERE `auctionID`=?");
			statement.setInt(1, item.getAuctionID());
			statement.executeUpdate();
			return true;
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public static void closeDatabase() {
		databaseInstance.close();
	}

	static int getAuctionCount() {
		try {
			PreparedStatement statement = databaseInstance
				.prepareStatement("SELECT count(*) as auction_count FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "auctions` WHERE `sold-out`='0'");
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				int auctionCount = result.getInt("auction_count");
				return auctionCount;
			}
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return 0;
	}

	public static int getMyAuctionsCount(int ownerID) {
		try {
			PreparedStatement statement = databaseInstance
				.prepareStatement("SELECT count(*) as my_slots FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "auctions` WHERE `seller`='" + ownerID + "' AND `sold-out`='0'");
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				int auctionCount = result.getInt("my_slots");
				return auctionCount;
			}
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return 0;
	}

	public static MarketItem getAuctionItem(int auctionID) {
		try {
			PreparedStatement statement = databaseInstance
				.prepareStatement("SELECT `auctionID`, `itemID`, `amount`, `amount_left`, `price`, `seller`, `seller_username`, `buyer_info`, `time` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "auctions` WHERE `auctionID`= ? AND `sold-out` = '0'");
			statement.setInt(1, auctionID);

			ResultSet result = statement.executeQuery();
			if (!result.next()) return null;

			return new MarketItem(result.getInt("auctionID"), result.getInt("itemID"),
				result.getInt("amount"), result.getInt("amount_left"), result.getInt("price"),
				result.getInt("seller"), result.getString("seller_username"), result.getString("buyer_info"), result.getLong("time"));
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		return null;
	}

	static ArrayList<MarketItem> getAuctionItemsOnSale() {
		ArrayList<MarketItem> auctionItems = new ArrayList<>();
		try {
			PreparedStatement statement = databaseInstance
				.prepareStatement("SELECT `auctionID`, `itemID`, `amount`, `amount_left`, `price`, `seller`, `seller_username`, `buyer_info`, `time` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "auctions` WHERE `sold-out`='0'");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				MarketItem auctionItem = new MarketItem(result.getInt("auctionID"), result.getInt("itemID"),
					result.getInt("amount"), result.getInt("amount_left"), result.getInt("price"),
					result.getInt("seller"), result.getString("seller_username"), result.getString("buyer_info"), result.getLong("time"));
				auctionItems.add(auctionItem);
			}
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return auctionItems;
	}

	public static boolean setSoldOut(MarketItem item) {
		try {
			PreparedStatement statement = databaseInstance
				.prepareStatement("UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX
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

	public static boolean update(MarketItem item) {
		try {
			PreparedStatement statement = databaseInstance.prepareStatement(
				"UPDATE `" + Constants.GameServer.MYSQL_TABLE_PREFIX
					+ "auctions` SET `amount_left`=?, `price` = ?, `buyer_info`=? WHERE `auctionID`= ?");
			statement.setInt(1, item.getAmountLeft());
			statement.setInt(2, item.getPrice());
			statement.setString(3, item.getBuyers());
			statement.setInt(4, item.getAuctionID());
			statement.executeUpdate();
			return true;
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
		return false;
	}

	public static ArrayList<CollectableItem> getCollectableItemsFor(int player) {
		ArrayList<CollectableItem> list = new ArrayList<>();
		try {
			PreparedStatement statement = databaseInstance.prepareStatement("SELECT `claim_id`, `item_id`, `item_amount`, `playerID`, `explanation` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX
				+ "expired_auctions` WHERE `playerID` = ?  AND `claimed`= '0'");
			statement.setInt(1, player);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				CollectableItem item = new CollectableItem();
				item.claim_id = result.getInt("claim_id");
				item.item_id = result.getInt("item_id");
				item.item_amount = result.getInt("item_amount");
				item.playerID = result.getInt("playerID");
				item.explanation = result.getString("explanation");
				list.add(item);
			}
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		return list;
	}
}
