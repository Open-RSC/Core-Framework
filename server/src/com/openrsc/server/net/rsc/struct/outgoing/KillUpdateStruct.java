package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class KillUpdateStruct extends AbstractStruct<OpcodeOut> {

	public String victim;
	public String attacker;
	public int killType;
}
