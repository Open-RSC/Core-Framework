package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class PartyStruct extends AbstractStruct<OpcodeOut> {

	public int actionId;
	public int partyId;
	public String leaderName;
	public int isLeader;
	public String partyName;
	public String nameInviter;
	public int partySize;
	public int allowsSearchedJoin;
	public int partyPoints;
	public String[] partyMembers;
	public int[] memberRanks;
	public int[] isMemberOnline;
	public int[] currentHitsMembers;
	public int[] maximumHitsMembers;
	public int[] combatLevelsMembers;
	public int[] isMemberSkulled;
	public int[] isMemberDead;
	public int[] isShareLoot;
	public int[] partyMemberTotal; // total level
	public int[] isInCombat;
	public int[] shareExp;
	public long[] shareExp2; // why two?
}
