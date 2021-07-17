package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class PrivateMessageStruct extends AbstractStruct<OpcodeOut> {

	public String playerName;
	public String formerName;
	public String message;
	public int iconSprite;
	public int worldNumber;
	public int totalSentMessages; // server sent messages so far
}
