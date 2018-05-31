package org.rscemulation.server.logging.model;

public class ErrorLog extends Log {
	private String error;
	private int time;
	
	public ErrorLog(long user, int account, String IP, String error, int time) {
		super(user, account, IP);
		this.error = error;
		this.time = time;
	}
	
	public String getMessage() {
		return super.formatMessage(error);
	}
	
	public int getTime() {
		return time;
	}	
}