package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class TrawlerUpdateStruct extends AbstractStruct<OpcodeOut> {

	public int interfaceId = 6; // custom fishing trawler interface id
	public int actionId; // 0 = open, // 1 = update, // 2 = close
	public int waterLevel;
	public int fishCaught;
	public int minutesLeft;
	public int isNetBroken;
}
