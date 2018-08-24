package com.openrsc.server.content.clan;


public enum ClanRank {
	NORMAL(0),
	LEADER(1),
	GENERAL(2);
	
	int rankIndex;
	
	ClanRank(int id) {
		this.rankIndex = id;
	}
	
	public int getRankIndex() {
		return rankIndex;
	}
	
	public static ClanRank getRankFor(int rankID) {
		return ClanRank.values()[rankID];
	}
}
