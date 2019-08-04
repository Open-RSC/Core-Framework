package com.openrsc.server.content.party;


public enum PartyRank {
	NORMAL(0),
	LEADER(1),
	GENERAL(2);

	int rankIndex;

	PartyRank(int id) {
		this.rankIndex = id;
	}

	public static PartyRank getRankFor(int rankID) {
		return PartyRank.values()[rankID];
	}

	public int getRankIndex() {
		return rankIndex;
	}
}
