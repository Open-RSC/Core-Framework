package com.openrsc.server.database.struct;

public class AuctionItem {
	public int auctionID;
	public int itemID;
	public int amount;
	public int amount_left;
	public int price;
	public int seller;
	public String seller_username;
	public String buyer_info;
	public int sold_out;
	public long time;
	public Boolean was_cancel;
}
