package org.openrsc.server.logging.model;

public class DeathLog extends Log {
	private int x, y, time;

	private java.util.ArrayList<org.openrsc.server.model.InvItem> itemsLost;

	public DeathLog(long user, int account, String IP, int x, int y, int time) {
		super(user, account, IP);
		this.x = x;
		this.y = y;
		itemsLost = new java.util.ArrayList<org.openrsc.server.model.InvItem>();
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

	public void addLostItem(org.openrsc.server.model.InvItem item) {
		itemsLost.add(item);
	}

	public java.util.ArrayList<org.openrsc.server.model.InvItem> getItemsLost() {
		return itemsLost;
	}
}