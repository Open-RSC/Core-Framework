package com.openrsc.server.content.clan;

import com.openrsc.server.model.entity.player.Player;

public class ClanPlayer {
	private String username;
	private Player playerReference;
	private ClanRank rank;
	private int kills;
	private int deaths;

	public ClanPlayer(String player) {
		this.username = player;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String player) {
		this.username = player;
	}

	public Player getPlayerReference() {
		return playerReference;
	}

	public void setPlayerReference(Player playerReference) {
		this.playerReference = playerReference;
	}

	public boolean isOnline() {
		return playerReference != null && !playerReference.isUnregistering();
	}

	public ClanRank getRank() {
		return rank;
	}

	public void setRank(ClanRank rank) {
		this.rank = rank;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public void incDeaths() {
		deaths++;
	}

	public void incKills() {
		kills++;
	}
}
