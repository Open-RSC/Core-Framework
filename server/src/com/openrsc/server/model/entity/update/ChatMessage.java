package com.openrsc.server.model.entity.update;

import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;

public class ChatMessage {
	/**
	 * The message it self, in byte format
	 */
	private byte[] message;
	/**
	 * Who the message is for
	 */
	private Mob recipient = null;
	/**
	 * Who sent the message
	 */
	private Mob sender;
	private String messageString;

	public ChatMessage(Mob sender, byte[] message) {
		this.sender = sender;
		this.setMessage(message);
	}

	public ChatMessage(Mob sender, String message, Mob recipient) {
		this.sender = sender;
		this.setMessageString(message);
		this.setMessage(message.getBytes());
		this.recipient = recipient;
	}

	public ChatMessage(Player sender2, String readString) {
		this.sender = sender2;
		this.setMessageString(readString);
		this.setMessage(readString.getBytes());
	}

	public int getLength() {
		return getMessage().length;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public Mob getRecipient() {
		return recipient;
	}

	public Mob getSender() {
		return sender;
	}

	public String getMessageString() {
		return messageString;
	}

	public void setMessageString(String messageString) {
		this.messageString = messageString;
	}

}
