package com.openrsc.server.content.achievement;

import com.openrsc.server.content.achievement.Achievement.TaskReward;

public class AchievementReward {

	private TaskReward rewardType;
	private int id;
	private int amount;
	private boolean guaranteed;

	public AchievementReward(TaskReward rewardType, int id, int amount, boolean choice) {
		this.setRewardType(rewardType);
		this.setId(id);
		this.amount = amount;
		this.setChoice(choice);
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TaskReward getRewardType() {
		return rewardType;
	}

	public void setRewardType(TaskReward rewardType) {
		this.rewardType = rewardType;
	}

	public boolean isGuaranteed() {
		return guaranteed;
	}

	public void setChoice(boolean choice) {
		this.guaranteed = choice;
	}
}
