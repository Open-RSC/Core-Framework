package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class WorldInfoStruct extends AbstractStruct<OpcodeOut> {

	public int serverIndex;
	public int planeWidth;
	public int planeHeight;
	public int planeFloor;
	public int distanceBetweenFloors;
}
