package org.rscemulation.server.logging.model;

public class eventLog extends Log {
	private String message;
	private int time;

	public eventLog(long user, int account, String IP, int time, String message) 
	{
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