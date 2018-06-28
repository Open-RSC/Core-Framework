package org.openrsc.server.logging.model;

public final class ScriptLog extends Log {

	private final int time, status;
	
	private final String script;
	
	private final long target;
	
	public ScriptLog(long user, int account, String ip, int time, String script, long target, int status) {
		super(user, account, ip);
		this.time = time;
		this.script = script;
		this.target = target;
		this.status = status;
	}
	
	public int getTime() {
		return time;
	}
	
	public String getScript() {
		return script;
	}
	
	public long getTarget() {
		return target;
	}
	
	public int getStatus() {
		return status;
	}

}
