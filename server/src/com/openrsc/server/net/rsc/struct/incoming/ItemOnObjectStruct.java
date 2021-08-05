package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.model.Point;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ItemOnObjectStruct extends AbstractStruct<OpcodeIn> {

	public Point coordObject;
	public int direction = 0;
	public int slotID;
	public int itemID;
}
