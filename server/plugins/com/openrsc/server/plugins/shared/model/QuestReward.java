package com.openrsc.server.plugins.shared.model;

public class QuestReward {

	private int questPoints;
	private XPReward[] xpRewards;

	public static final QuestReward NONE = new QuestReward(0, new XPReward[0]);

	public QuestReward(int questPoints, XPReward[] xpRewards) {
		this.questPoints = questPoints;
		this.xpRewards = xpRewards.clone();
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public XPReward[] getXpRewards() {
		return xpRewards;
	}
}
