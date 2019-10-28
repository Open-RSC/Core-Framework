package com.openrsc.server.content.market;

public class MarketItem {
	private static final int TIME_LIMIT = (60 * 60 * 24 * 5); // Number of hours an auction lasts until it expires - 5 days

	private int auctionID, itemID, amount, amount_left, price;
	private String buyers;
	private int seller;
	private long time;
	private String sellerName;

	MarketItem(int auctionID, int itemID, int amount, int amount_left, int price, int seller, String sellerName, String buyers, long t) {
		this.auctionID = auctionID;
		this.itemID = itemID;
		this.amount = amount;
		this.setAmountLeft(amount_left);
		this.price = price;
		this.seller = seller;
		this.sellerName = sellerName;
		this.setTime(t);
		this.setBuyers(buyers);
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmountLeft() {
		return amount_left;
	}

	public void setAmountLeft(int amount_left) {
		this.amount_left = amount_left;
	}

	public int getAuctionID() {
		return auctionID;
	}

	public void setAuctionID(int auctionID) {
		this.auctionID = auctionID;
	}

	public String getSellerName() {
		return sellerName;
	}

	public String getBuyers() {
		return buyers;
	}

	public void setBuyers(String buyers) {
		this.buyers = buyers;
	}

	public int getHoursLeft() {
		long expireDate = (time + TIME_LIMIT);
		long curTime = (System.currentTimeMillis() / 1000);
		long timeDiff = (expireDate - curTime);

		if (timeDiff < 0)
			return 0;

		return (int) (timeDiff / 60 / 60); // Displays in hours the remaining time left on an auction
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getSeller() {
		return seller;
	}

	public void setSeller(int seller) {
		this.seller = seller;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	boolean hasExpired() {
		return getHoursLeft() <= 0;
	}
}
