package com.openrsc.server.content.minigame.combatodyssey;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class Tier {
	private final int tierId;
	private final ArrayList<Task> tasks;
	private final int tierMasterId;
	private final ArrayList<Pair<Integer, Integer>> rewards;

	public Tier(int tierId, int tiermasterId, ArrayList<Pair<Integer, Integer>> rewards) {
		this.tierId = tierId;
		this.tasks = new ArrayList<Task>();
		this.tierMasterId = tiermasterId;
		this.rewards = rewards;
	}

	public Task getTask(int taskId) {
		try {
			return tasks.get(taskId);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	public void addTask(Task task) {
		tasks.add(task);
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public String[] getTasksAndCounts() {
		String[] tasksAndCounts = new String[getTotalTasks()];

		for (int i = 0; i < getTotalTasks(); ++i) {
			tasksAndCounts[i] = getTasks().get(i).getKills() + " " + getTasks().get(i).getDescription();
		}

		return tasksAndCounts;
	}

	public int getTotalTasks() {
		return tasks.size();
	}

	public int getTierMasterId() {
		return tierMasterId;
	}

	public ArrayList<Pair<Integer, Integer>> getRewards() {
		return rewards;
	}
}
