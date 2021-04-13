package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class InventoryStruct extends AbstractStruct<OpcodeOut> {

	public int inventorySize;
	public int[] wielded;
	public int[] catalogIDs;
	public int[] noted;
	public int[] amount;
}
