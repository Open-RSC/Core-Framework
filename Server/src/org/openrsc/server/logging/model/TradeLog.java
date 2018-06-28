package org.openrsc.server.logging.model;

public class TradeLog extends Log {
	private long reciever, sender;
	private int time, reciever_account, sender_account;
	private String recieverIP, senderIP;

	private java.util.ArrayList<org.openrsc.server.model.InvItem> itemsTraded;

	private java.util.ArrayList<org.openrsc.server.model.InvItem> itemsRecieved;

	public TradeLog(long sender, int sender_account, String senderIP, long reciever, int reciever_account, String recieverIP, int time) {
		super(sender, sender_account, senderIP);
		this.itemsTraded = new java.util.ArrayList<org.openrsc.server.model.InvItem>(12);
		this.itemsRecieved = new java.util.ArrayList<org.openrsc.server.model.InvItem>(12);
		this.reciever = reciever;
		this.reciever_account = reciever_account;
		this.recieverIP = recieverIP;
		this.sender = sender;
		this.sender_account = sender_account;
		this.senderIP = senderIP;		
		this.time = time;
	}

	public void addItemToTradeList(org.openrsc.server.model.InvItem item) {
		itemsTraded.add(item);
	}

	public void addItemToRecievedList(org.openrsc.server.model.InvItem item) {
		itemsRecieved.add(item);
	}

	public long getReciever() {
		return reciever;
	}
	
	public int getRecieverAccount() {
		return reciever_account;
	}
	
	public long getSender() {
		return sender;
	}
	
	public int getSenderAccount() {
		return sender_account;
	}		
	
	public int getTime() {
		return time;
	}

	public String getRecieverIP() {
		return recieverIP;
	}
	
	public String getSenderIP() {
		return senderIP;
	}	

	public java.util.ArrayList<org.openrsc.server.model.InvItem> getTradedItems() {
		return itemsTraded;
	}

	public java.util.ArrayList<org.openrsc.server.model.InvItem> getRecievedItems() {
		return itemsRecieved;
	}
}