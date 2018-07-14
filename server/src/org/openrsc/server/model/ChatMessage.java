package org.openrsc.server.model;

import org.openrsc.server.util.DataConversions;

public class ChatMessage {
	private Mob sender;
	private byte[] message;
	private Mob recipient = null;

	public ChatMessage(Mob sender, byte[] message) {
		this.sender = sender;
		this.message = message;
	}
	
	public ChatMessage(Mob sender, String message, Mob recipient) {
		this.sender = sender;
		this.message = DataConversions.stringToByteArray(message);
		this.recipient = recipient;
	}
	
	public Mob getRecipient() {
		return recipient;
	}

	public Mob getSender() {
		return sender;
	}
	
	public byte[] getMessage() {
		return message;
	}
	
	public int getLength() {
		return message.length;
	}
}