
package org.rscemulation.server.logging.model;

public class DuelLog extends Log {
	private int[] duelOptions;
	private int time, opponent_account;
	private long opponent;

	private String opponentIP;

	private java.util.ArrayList<org.rscemulation.server.model.InvItem> itemsStaked;

	private java.util.ArrayList<org.rscemulation.server.model.InvItem> itemsStakedAgainst;

	public DuelLog(long user, int account, String IP, long opponent, int opponent_account, String opponentIP, int[] duelOptions, int time) {
		super(user, account, IP);
		this.opponent = opponent;
		this.opponent_account = opponent_account;
		this.opponentIP = opponentIP;
		this.duelOptions = duelOptions;
		itemsStaked = new java.util.ArrayList<org.rscemulation.server.model.InvItem>();
		itemsStakedAgainst = new java.util.ArrayList<org.rscemulation.server.model.InvItem>();
		this.time = time;
	}

	public void addStakedItem(org.rscemulation.server.model.InvItem item) {
		itemsStaked.add(item);
	}

	public void addStakedAgainstItem(org.rscemulation.server.model.InvItem item) {
		itemsStakedAgainst.add(item);
	}

	public java.util.ArrayList<org.rscemulation.server.model.InvItem> getStakedItems() {
		return itemsStaked;
	}

	public java.util.ArrayList<org.rscemulation.server.model.InvItem> getStakedAgainstItems() {
		return itemsStakedAgainst;
	}

	public long getOpponent() {
		return opponent;
	}

	public int getOpponentAccount() {
		return opponent_account;
	}	
	
	public String getOpponentIP() {
		return opponentIP;
	}

	public int getDuelOption(int option) {
		return duelOptions[option];
	}
	
	public int getTime() {
		return time;
	}
}