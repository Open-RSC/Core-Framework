package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class KnownPlayersStruct extends AbstractStruct<OpcodeIn> {

	public int playerCount;
	public int[] playerServerIndex;
	public int[] playerServerId;
}
