package org.rscemulation.server.logging.model;

public class ReportLog extends Log {
	private String IP, reportedIP;
	private long user, reported;
	private int rule, time, account, reported_account;

	public ReportLog(long user, int account, String IP, long reported, int reported_account, String reportedIP, int rule, int time) {
		super(user, account, IP);
		this.user = user;
		this.IP = IP;
		this.reported = reported;
		this.reportedIP = reportedIP;
		this.rule = rule;
		this.time = time;
		this.account = account;
		this.reported_account = reported_account;
	}
	
	public long getUser() {
		return user;
	}
	
	public int getAccount() {
		return account;
	}	
	
	public String getIP() {
		return IP;
	}
	
	public long getReported() {
		return reported;
	}
	
	public long getReportedAccount() {
		return reported_account;
	}	
	
	public String getReportedIP() {
		return reportedIP;
	}
	
	public int getTime() {
		return time;
	}
	
	public int getRule() {
		return rule;
	}
}