package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class PrivacySettingsStruct extends AbstractStruct<OpcodeOut> {

	public int hideStatus;
	public int blockChat;
	public int blockPrivate;
	public int blockTrade;
	public int blockDuel;
}
