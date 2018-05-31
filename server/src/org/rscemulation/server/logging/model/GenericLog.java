package org.rscemulation.server.logging.model;

public class GenericLog extends Log {
	private String message;
	private int time;
	
	public GenericLog(String message, int time) {
		super(-1, -1, "null");
		this.message = message;
		this.time = time;
	}
	
	public String getMessage() {
		return super.formatMessage(message);
	}
	
	public int getTime() {
		return time;
	}
}