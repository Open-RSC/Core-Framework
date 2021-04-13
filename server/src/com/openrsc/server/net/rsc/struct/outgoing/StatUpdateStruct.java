package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class StatUpdateStruct extends AbstractStruct<OpcodeOut> {

	public int statId;
	public int currentLevel;
	public int maxLevel;
	public int experience;
}
