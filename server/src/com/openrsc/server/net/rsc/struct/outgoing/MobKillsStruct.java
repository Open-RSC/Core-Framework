package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class MobKillsStruct extends AbstractStruct<OpcodeOut> {

	public int totalCount;
	public int recentNpcId;
	public int recentNpcKills;
}
