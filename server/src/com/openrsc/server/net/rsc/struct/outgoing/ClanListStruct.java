package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ClanListStruct extends AbstractStruct<OpcodeOut> {

	public int actionId = 4;
	public int totalClans;
	public ClanStruct[] clansInfo;
}
