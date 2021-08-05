package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class AuctionProgressStruct extends AbstractStruct<OpcodeOut> {

	public int interfaceId;
	public int delay;
	public int timesRepeat;
}
