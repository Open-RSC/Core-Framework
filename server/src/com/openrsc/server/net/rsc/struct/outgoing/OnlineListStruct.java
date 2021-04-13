package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class OnlineListStruct extends AbstractStruct<OpcodeOut> {

	public int numberOnline;
	public int playerCount;
	public String[] name;
	public int[] icon;
	public String[] location;
}
