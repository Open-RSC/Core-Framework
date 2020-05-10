package com.openrsc.server.database.struct;

public class ExpiredAuction {
	public int playerID;
	public int claim_id;
	public int item_id;
	public int item_amount;
	public long time;
	public long claim_time;
	public Boolean claimed;
	public String explanation;
}
