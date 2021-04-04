package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class ItemCommandStruct extends AbstractStruct<OpcodeIn> {

	public int index;
	public int amount = 1;
	public int realIndex;
	public int commandIndex;
}
