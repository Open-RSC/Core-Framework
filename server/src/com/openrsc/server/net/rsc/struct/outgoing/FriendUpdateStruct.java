package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class FriendUpdateStruct extends AbstractStruct<OpcodeOut> {

	public String name;
	public String formerName = "";
	public int onlineStatus;
	public String worldName = "";
	public int worldNumber;
}
