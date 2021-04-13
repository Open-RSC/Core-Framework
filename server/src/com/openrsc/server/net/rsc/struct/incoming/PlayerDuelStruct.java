package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class PlayerDuelStruct extends AbstractStruct<OpcodeIn> {

	public int targetPlayerID;
	public int duelCount;
	public int[] duelCatalogIDs;
	public int[] duelAmounts;
	public boolean[] duelNoted;
	public int disallowRetreat;
	public int disallowMagic;
	public int disallowPrayer;
	public int disallowWeapons;

}
