package com.openrsc.server.model.snapshot;

public class Activity extends Snapshot {

	/**
	 * The messages that was sent
	 */
	private String activity;

	/**
	 * Constructor
	 *
	 * @param player   player that performed the activity
	 * @param activity the activity that was performed
	 */
	public Activity(String sender, String activity) {
		super(sender);
		this.setActivity(activity);
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}
}

