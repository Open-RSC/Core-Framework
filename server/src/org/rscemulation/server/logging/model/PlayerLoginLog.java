package org.rscemulation.server.logging.model;

public class PlayerLoginLog extends Log {
	private int time;
	public PlayerLoginLog(long user, int account, String IP, int time) {
		super(user, account, IP);
		this.time = time;
	}
	
	public int getTime() {
		return time;
	}
}