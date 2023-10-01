package com.openrsc.server.content.minigame.combatodyssey;

public class Task {
	private final int taskId;
	private final String description;
	private final int[] npcIds;
	private final int kills;
	private final String[] monsterInfoDialog;

	public Task(int taskId, String description, int[] npcIds, int kills, String[] monsterInfoDialog) {
		this.taskId = taskId;
		this.description = description;
		this.npcIds = npcIds;
		this.kills = kills;
		this.monsterInfoDialog = monsterInfoDialog;
	}

	public int getTaskId() {
		return taskId;
	}

	public String getDescription() {
		return description;
	}

	public int[] getNpcIds() {
		return npcIds;
	}

	public int getKills() {
		return kills;
	}

	public String[] getMonsterInfoDialog() {
		return monsterInfoDialog;
	}
}
