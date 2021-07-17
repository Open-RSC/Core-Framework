package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.model.Point;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ItemOnGroundItemStruct extends AbstractStruct<OpcodeIn> {

	public Point groundItemCoord;
	public int slotIndex;
	public int groundItemId;
}
