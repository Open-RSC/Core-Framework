package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class PlayerDuelStruct extends AbstractStruct<OpcodeIn> {

	public int targetPlayerID;
	public int duelCount;
	public int[] duelCatalogIDs;
	public int[] duelAmounts;
	public boolean[] duelNoted;
	public byte[] newSettings = new byte[4];

}
