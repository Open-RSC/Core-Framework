package com.openrsc.interfaces.misc.clan;

import orsc.mudclient;

public class Clan {
	public String[] username = new String[200];
	public int[] clanRank = new int[200];
	public int[] onlineClanMember = new int[200];
	public boolean[] allowed = new boolean[2];
	private boolean inClan;
	private String clanName;
	private String clanTag;
	private String clanLeader;
	private boolean isClanLeader;
	private String[] clanByName = {"None", "Owner", "General"};
	private String[] clanSettingOptions = {"Anyone", "Owner", "General+"};
	private String[] clanSearchSettings = {"Anyone can join", "Invite only", "Closed"};
	private int[] playerKills = new int[25];
	private int[] playerDeaths = new int[25];
	private int[] clanSetting = new int[3];
	private ClanInterface clanInterface;

	public Clan(mudclient mc) {
		clanInterface = new ClanInterface(mc);
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

	public String getClanRankNames(int i) {
		return clanByName[i];
	}

	public void putClan(boolean b) {
		this.inClan = b;
	}

	public boolean inClan() {
		return inClan;
	}

	public String getClanLeaderUsername() {
		return clanLeader;
	}

	public void setClanLeaderUsername(String leader) {
		this.clanLeader = leader;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String name) {
		this.clanName = name;
	}

	public String getClanTag() {
		return clanTag;
	}

	public void setClanTag(String tag) {
		this.clanTag = tag;
	}

	public void showClanSetupInterface(boolean inClan) {
		clanInterface.setVisible(true);
		if (!inClan()) {
			clanInterface.clanSetupPanel.setFocus(clanInterface.clanName_field);
			clanInterface.clanActivePanel = 1;
		} else {
			clanInterface.clanSetupPanel.setFocus(-1);
			if (isClanLeader()) {
				clanInterface.clanActivePanel = 1;
			} else {
				clanInterface.clanActivePanel = 2;
			}
		}
	}

	public ClanInterface getClanInterface() {
		return clanInterface;
	}

	public boolean isClanLeader() {
		return isClanLeader;
	}

	public void setClanLeader(boolean b) {
		this.isClanLeader = b;
	}

	public void update() {
		if (isClanLeader() && !inClan()) {
			setClanLeader(false);
		}
	}

	public String getClanSettingByName(int i) {
		return clanSettingOptions[i];
	}

	public void setClanSetting(int setting, int state) {
		this.clanSetting[setting] = state;
	}

	public int getClanSetting(int setting) {
		return clanSetting[setting];
	}

	public String getClanSearchSettingByName() {
		return clanSearchSettings[clanSetting[2]];
	}
}


