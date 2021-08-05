package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ItemCommandStruct extends AbstractStruct<OpcodeIn> {

	public int index;
	public int amount = 1;
	public int realIndex;
	public int commandIndex;
}
