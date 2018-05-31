package org.rscemulation.server.entityhandling.defs.extras;

public class AgilityDef {

	private int courseID, level, experience, successX, successY, failX, failY;
	private float failDamageRate;
	private String attemptMessage;
	
	public AgilityDef(int courseID, int level, int experience, int successX, int successY, int failX, int failY, float failDamageRate, String attemptMessage) {
		this.courseID = courseID;
		this.level = level;
		this.experience = experience;
		this.successX = successX;
		this.successY = successY;
		this.failX = failX;
		this.failY = failY;
		this.failDamageRate = failDamageRate / 100;
		this.attemptMessage = attemptMessage;
	}
	
	public int getCourseID() {
		return courseID;
	}
	
	public String getAttemptMessage() {
		return attemptMessage;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getExperience() {
		return experience;
	}
	
	public int getSuccessX() {
		return successX;
	}

	public int getSuccessY() {
		return successY;
	}
	
	public int getFailX() {
		return failX;
	}
	
	public int getFailY() {
		return failY;
	}
	
	public float getFailDamageRate() {
		return failDamageRate;
	}
}
