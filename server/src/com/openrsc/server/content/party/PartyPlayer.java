package com.openrsc.server.content.party;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.player.Player;

import static com.openrsc.server.plugins.Functions.getMaxLevel;

public class PartyPlayer {
	private String username;
	private Player playerReference;
	private PartyRank rank;
	private int kills;
	private int curHp;
	private int cbLvl;
	private int MaxHp;
	private int deaths;
	private int partyMemberDead = 0;
	private int shareLoot;
	private int shareExp;
	private int partyMembersTotal;
	private int inCombat;

	PartyPlayer(String player) {
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

	void setPlayerReference(Player playerReference) {
		this.playerReference = playerReference;
	}

	public boolean isOnline() {
		return playerReference != null && !playerReference.isUnregistering();
	}

	public PartyRank getRank() {
		return rank;
	}

	public void setRank(PartyRank rank) {
		this.rank = rank;
	}

	public int getKills() {
		return kills;
	}

	public int getCurHp() {
		return playerReference.getSkills().getLevel(3);
	}

	public int getShareLoot() {
		return shareLoot;
	}
	public int getShareExp() {
		return shareExp;
	}

	public int getInCombat() {
		if (playerReference.inCombat()) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setShareLoot(int shareLoot) {
		this.shareLoot = shareLoot;
	}
	public void setShareExp(int shareExp) {
		this.shareExp = shareExp;
	}

	public int getPartyMembersTotal() {
		int o = playerReference.getParty().getPlayers().size();
		return o;
	}
	public int getPartyMembersNotTired() {
		int o = playerReference.getParty().getPlayers().size();
		for (PartyPlayer p : playerReference.getParty().getPlayers()) {
			if(p.getPlayerReference().getFatigue() >= p.getPlayerReference().MAX_FATIGUE){
				o--;
			}
		}
		return o;
	}

	public void setPartyMembersTotal(int partyMembersTotal) {
		int o = playerReference.getParty().getPlayers().size();
		o = partyMembersTotal;
	}

	public int getCbLvl() {
		return playerReference.getCombatLevel();
	}

	public int getSkull() {
		return playerReference.getSkullType();
	}

	public int getPartyMemberDead() {
		if (getCurHp() < 1) {
			return 1;
		} else {
			return 0;
		}
	}

	public int getMaxHp() {
		return getMaxLevel(playerReference, Skills.HITPOINTS);
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
