package org.rscemulation.server.logging.model;

public class DropLog extends Log {
	private int x, y, itemID, time;
	private long itemAmount;

	public DropLog(long user, int account, String IP, int x, int y, int itemID, long itemAmount, int time) {
		super(user, account, IP);
		this.x = x;
		this.y = y;
		this.itemID = itemID;
		this.itemAmount = itemAmount;
		this.time = time;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getTime() {
		return time;
	}

	public int getItemID() {
		return itemID;
	}

	public long getItemAmount() {
		return itemAmount;
	}
}