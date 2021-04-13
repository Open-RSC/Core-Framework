package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ClanStruct extends AbstractStruct<OpcodeOut> {

	public int actionId;
	public int clanId;
	public String clanName;
	public String clanTag;
	public String leaderName;
	public int isLeader;
	public int clanSize;
	public String nameInviter;
	public int allowsSearchedJoin;
	public int clanPoints;
	public String[] clanMembers;
	public int[] memberRanks;
	public int[] isMemberOnline;
}
