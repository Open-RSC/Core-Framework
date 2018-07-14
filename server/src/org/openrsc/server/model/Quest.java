package org.openrsc.server.model;

public class Quest {
	private int questID, questStage, questPoints;
	private boolean finished;
	
	public Quest(int id, int stage, boolean finished, int questPoints) {
		this.questID = id;
		this.questStage = stage;
		this.finished = finished;
		this.questPoints = questPoints;
	}

	public int getQuestPoints() {
		return questPoints;
	}
	
	public void finish() {
		finished = true;
	}
	
	public boolean finished() {
		return finished;
	}
	
	public int getID() {
		return questID;
	}
	
	public int getStage() {
		return questStage;
	}
	
	public void setStage(int stage) {
		questStage = stage;
	}
	
	public void incStage() {
		questStage++;
	}
	
	public void decStage() {
		questStage--;
	}
}