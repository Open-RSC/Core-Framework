package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class PartyListStruct extends AbstractStruct<OpcodeOut> {

	public int actionId;
	public int totalParties;
	public PartyStruct[] partyInfo;
}
