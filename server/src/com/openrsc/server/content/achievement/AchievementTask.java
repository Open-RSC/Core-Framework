package com.openrsc.server.content.achievement;

import com.openrsc.server.content.achievement.Achievement.TaskType;

public class AchievementTask {

	private TaskType task;

	private int id;
	private int amount;

	public AchievementTask(TaskType task, int id, int amount) {
		this.task = task;
		this.id = id;
		this.amount = amount;
	}

	public TaskType getTask() {
		return task;
	}

	public void setTask(TaskType task) {
		this.task = task;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	//public setAchievementStatus(Player p, int status) {

	//}

	@Override
	public String toString() {
		return task.name() + "_id:" + id + "_amount:" + amount;
	}
}
