package com.openrsc.server.model.entity.update;

import com.openrsc.server.model.entity.player.Player;

public class Bubble {
	/**
	 * What to draw in it
	 */
	private int itemID;
	/**
	 * Who the bubble belongs to
	 */
	private Player owner;

	public Bubble(Player owner, int itemID) {
		this.owner = owner;
		this.itemID = itemID;
	}

	public int getID() {
		return itemID;
	}

	public Player getOwner() {
		return owner;
	}

}
