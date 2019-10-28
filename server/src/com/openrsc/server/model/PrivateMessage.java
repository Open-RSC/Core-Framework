package com.openrsc.server.model;

import com.openrsc.server.model.entity.player.Player;

public class PrivateMessage {

	private Player player;
	private String message;
	private long friend;

	public PrivateMessage(Player player, String message, long friend) {
		this.setPlayer(player);
		this.setMessage(message);
		this.setFriend(friend);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public long getFriend() {
		return friend;
	}

	public void setFriend(long friend) {
		this.friend = friend;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
