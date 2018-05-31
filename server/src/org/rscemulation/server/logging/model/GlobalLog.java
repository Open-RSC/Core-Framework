package org.rscemulation.server.logging.model;

public class GlobalLog extends Log {
	private String message;
	private int time;

	public GlobalLog(long user, int account, String IP, String message, int time) {
		super(user, account, IP);
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