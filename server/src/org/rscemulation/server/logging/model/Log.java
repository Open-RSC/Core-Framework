package org.rscemulation.server.logging.model;

import org.apache.commons.lang.StringEscapeUtils;

public class Log {
	private long user = -1;
	private String ip = "";
	private int account = -1;

	public Log(long user, int account, String ip) {
		this.user = user;
		this.account = account;
		this.ip = ip;
	}

	public long getHash() {
		return user;
	}
	
	public int getAccount() {
		return account;
	}	

	public String getIP() {
		return ip;
	}

	public String formatMessage(String message) {
		return StringEscapeUtils.escapeJava(message).replaceAll("'", "");
	}
}