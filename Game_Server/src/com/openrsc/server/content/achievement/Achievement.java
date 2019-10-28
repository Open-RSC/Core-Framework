package com.openrsc.server.content.achievement;

import java.util.ArrayList;

public class Achievement {

	private ArrayList<AchievementTask> tasks = new ArrayList<AchievementTask>();
	private ArrayList<AchievementReward> rewards = new ArrayList<AchievementReward>();
	private int id;
	private String name;
	private String title;
	private String desc;
	private AchievementType achievementType;
	private int startID;

	public Achievement(ArrayList<AchievementTask> tasks,
					   ArrayList<AchievementReward> rewards,
					   int id, String name, String title, String desc) {
		this.tasks = tasks;
		this.rewards = rewards;
		this.id = id;
		this.name = name;
		this.title = title;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getDesc() {
		return desc;
	}

	public int getId() {
		return id;
	}

	public ArrayList<AchievementTask> getTasks() {
		return tasks;
	}

	public ArrayList<AchievementReward> getRewards() {
		return rewards;
	}

	public AchievementType getAchievementType() {
		return achievementType;
	}

	public void setStartType(AchievementType achievementType, int startID) {
		this.achievementType = achievementType;
		this.setStartID(startID);
	}

	public int getStartID() {
		return startID;
	}

	public void setStartID(int startID) {
		this.startID = startID;
	}

	public enum AchievementType {
		TALK_TO_NPC,
		PICK_UP_ITEM,
		USE_OBJECT
	}

	public enum TaskType {
		KILL_NPC, GATHER_ITEM, TALK_TO_NPC, DO_QUEST,
	}

	public enum TaskReward {
		ITEM, EXPERIENCE,
	}
}
