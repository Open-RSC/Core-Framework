package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.model.Point;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class TargetPositionStruct extends AbstractStruct<OpcodeIn> {

	public Point coordinate;
	public int itemId;
}
