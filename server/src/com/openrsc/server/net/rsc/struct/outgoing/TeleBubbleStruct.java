package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.model.Point;
import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class TeleBubbleStruct extends AbstractStruct<OpcodeOut> {

	public int isGrab;
	public Point localPoint;
}
