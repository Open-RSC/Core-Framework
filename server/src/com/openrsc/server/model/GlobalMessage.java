package com.openrsc.server.model;

import com.openrsc.server.model.entity.player.Player;

public class GlobalMessage {

	private Player player;
	private String message;

	public GlobalMessage(Player player, String message) {
		this.setPlayer(player);
		this.setMessage(message);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
