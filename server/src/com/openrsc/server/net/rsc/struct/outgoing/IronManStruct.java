package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class IronManStruct extends AbstractStruct<OpcodeOut> {

	public int interfaceId = 2; // custom ironman interface id
	public int actionId; // 0 = update, // 1 = open, // 2 = close
	public int ironmanType;
	public int ironmanRestriction;
}
