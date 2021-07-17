package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class DuelConfirmStruct extends AbstractStruct<OpcodeOut> {

	public String targetPlayer;
	public int opponentDuelCount;
	public int[] opponentCatalogIDs;
	public int[] opponentAmounts;
	public int[] opponentNoted;
	public int myCount;
	public int[] myCatalogIDs;
	public int[] myAmounts;
	public int[] myNoted;
	public int disallowRetreat;
	public int disallowMagic;
	public int disallowPrayer;
	public int disallowWeapons;
}
