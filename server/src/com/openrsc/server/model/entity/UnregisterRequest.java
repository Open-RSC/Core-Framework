package com.openrsc.server.model.entity;

public class UnregisterRequest {
	boolean force;
	String reason;

	public UnregisterRequest(boolean force, String reason) {
		this.force = force;
		this.reason = reason;
	}

	public boolean isForced() {
		return force;
	}

	public String getReason() {
		return reason;
	}
}
