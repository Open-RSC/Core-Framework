package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ClanActionStruct extends AbstractStruct<OpcodeOut> {

	public int actionId;
	public String targetUsername;
	public String clanName;
}
