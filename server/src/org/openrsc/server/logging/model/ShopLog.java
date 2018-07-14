package org.openrsc.server.logging.model;

public class ShopLog extends Log {
	private int itemID, time, action, account;
	private long itemAmount;
	private long usernameHash;

	public ShopLog(long user, String IP, int account, int itemID, long itemAmount, int time, int action) {
		super(user, account, IP);
		this.usernameHash = user;
		this.itemID = itemID;
		this.account = account;
		this.itemAmount = itemAmount;
		this.time = time;
		this.action = action;
	}

	public int getItemID() {
		return itemID;
	}
	
	public int getAccount() {
		return account;
	}
	
	public int getTime() {
		return time;
	}
	
	public int getAction() {
		return action;
	}	

	public long getItemAmount() {
		return itemAmount;
	}
	
	public long getUser() {
		return usernameHash;
	}
}