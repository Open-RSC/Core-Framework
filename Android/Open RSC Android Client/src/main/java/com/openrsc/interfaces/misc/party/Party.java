package com.openrsc.interfaces.misc.party;

import orsc.mudclient;

public class Party {
	public String[] username = new String[25];
	public int[] partyRank = new int[25];
	public int[] onlinePartyMember = new int[25];
	public int[] curHp = new int[99];
	public int[] pMemD = new int[99];
	public int[] shareLoot = new int[99];
	public int[] cbLvl = new int[99];
	public int[] skull = new int[99];
	public int[] maxHp = new int[99];
	public int[] pMemDTimeout = new int[99];
	public boolean[] allowed = new boolean[2];
	private boolean inParty;
	private String partyName;
	private String partyTag;
	private String partyLeader;
	private boolean isPartyLeader;
	private String[] partyByName = {"None", "Owner", "General"};
	private String[] partySettingOptions = {"Anyone", "Owner", "General+"};
	private String[] partySearchSettings = {"Anyone can join", "Invite only", "Closed"};
	private int[] playerKills = new int[25];
	private int[] playerDeaths = new int[25];
	private int[] partySetting = new int[3];
	private PartyInterface partyInterface;

	public Party(mudclient mc) {
		partyInterface = new PartyInterface(mc);
	}

	public boolean isAllowed(int setting) {
		if (allowed[setting] == true) {
			return true;
		} else if (allowed[setting] == true) {
			return true;
		}
		return false;
	}

	public int getPlayerKills(int user) {
		return playerKills[user];
	}

	public void setPlayerKills(int user, int kills) {
		this.playerKills[user] = kills;
	}

	public int getPlayerDeaths(int user) {
		return playerDeaths[user];
	}

	public void setPlayerDeaths(int user, int deaths) {
		this.playerDeaths[user] = deaths;
	}

	public double getKDR(int user) {
		double kdRatio = 0.0;
		if (playerDeaths[user] != 0)
			kdRatio = (double) playerKills[user] / playerDeaths[user];
		else
			kdRatio = (double) playerKills[user];

		return kdRatio;
	}

	public String getPartyRankNames(int i) {
		return partyByName[i];
	}

	public void putParty(boolean b) {
		this.inParty = b;
	}

	public boolean inParty() {
		return inParty;
	}

	public String getPartyLeaderUsername() {
		return partyLeader;
	}

	public void setPartyLeaderUsername(String leader) {
		this.partyLeader = leader;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String name) {
		this.partyName = name;
	}

	public String getPartyTag() {
		return partyTag;
	}

	public void setPartyTag(String tag) {
		this.partyTag = tag;
	}

	public void showPartySetupInterface(boolean inParty) {
		partyInterface.setVisible(true);
		if (!inParty()) {
			partyInterface.partySetupPanel.setFocus(partyInterface.partyName_field);
			partyInterface.partyActivePanel = 1;
		} else {
			partyInterface.partySetupPanel.setFocus(-1);
			if (isPartyLeader()) {
				partyInterface.partyActivePanel = 1;
			} else {
				partyInterface.partyActivePanel = 2;
			}
		}
	}

	public PartyInterface getPartyInterface() {
		return partyInterface;
	}

	public boolean isPartyLeader() {
		return isPartyLeader;
	}

	public void setPartyLeader(boolean b) {
		this.isPartyLeader = b;
	}

	public void update() {
		if (isPartyLeader() && !inParty()) {
			setPartyLeader(false);
		}
	}

	public String getPartySettingByName(int i) {
		return partySettingOptions[i];
	}

	public void setPartySetting(int setting, int state) {
		this.partySetting[setting] = state;
	}

	public int getPartySetting(int setting) {
		return partySetting[setting];
	}

	public String getPartySearchSettingByName() {
		return partySearchSettings[partySetting[2]];
	}
}
