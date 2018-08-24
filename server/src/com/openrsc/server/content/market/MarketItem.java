package com.openrsc.server.content.market;

public class MarketItem {
	private static final int TIME_LIMIT = (60 * 60 * 24) * 1;
	
	private int auctionID, itemID, amount, amount_left, price;
	private String buyers;
	private int seller;
	private long time;
	private String sellerName;
	
	public MarketItem(int auctionID, int itemID, int amount, int amount_left, int price, int seller, String sellerName, String buyers, long t) {
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
	public int getAmountLeft() {
		return amount_left;
	}
	public int getAuctionID() {
		return auctionID;
	}
	public String getSellerName() {
		return sellerName;
	}
	public String getBuyers() {
		return buyers;
	}
	public int getHoursLeft() {
		int expireDate = (int) (time + TIME_LIMIT);
		int curTime = (int) (System.currentTimeMillis() / 1000);
		int timeDiff = (int) (expireDate - curTime);
		
		if(timeDiff < 0) 
			return 0;
		
		int hoursLeft = timeDiff / 60 / 60;
		return hoursLeft;
	}
	public int getItemID() {
		return itemID;
	}
	public int getPrice() {
		return price;
	}
	public int getSeller() {
		return seller;
	}
	public long getTime() {
		return time;
	}
	public boolean hasExpired() {
		return getHoursLeft() <= 0;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setAmountLeft(int amount_left) {
		this.amount_left = amount_left;
	}

	public void setAuctionID(int auctionID) {
		this.auctionID = auctionID;
	}

	public void setBuyers(String buyers) {
		this.buyers = buyers;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setSeller(int seller) {
		this.seller = seller;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
