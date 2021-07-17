package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class MessageStruct extends AbstractStruct<OpcodeOut> {

	public int iconSprite;
	public int messageTypeRsId;
	public int infoContained;
	public String message;
	public String senderName;
	public String colorString;
}
