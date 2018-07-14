package org.openrsc.server.logging.model;

public class PrivateMessageLog extends Log {
	private long reciever, sender;
	private int time, sender_account, reciever_account;
	private String recieverIP, message;

	public PrivateMessageLog(long sender, int sender_account, String senderIP, long reciever, int reciever_account, String recieverIP, String message, int time) {
		super(sender, sender_account, senderIP);
		this.reciever = reciever;
		this.recieverIP = recieverIP;
		this.message = message;
		this.time = time;
		this.reciever_account = reciever_account;
		this.sender_account = sender_account;
		this.sender = sender;
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

	public String getRecieverIP() {
		return recieverIP;
	}

	public String getMessage() {
		return super.formatMessage(message);
	}
	
	public int getTime() {
		return time;
	}
}