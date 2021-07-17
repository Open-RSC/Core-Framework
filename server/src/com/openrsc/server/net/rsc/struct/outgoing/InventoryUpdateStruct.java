package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class InventoryUpdateStruct extends AbstractStruct<OpcodeOut> {

	public int slot;
	public int wielded;
	public int catalogID;
	public int noted;
	public int amount;
}
