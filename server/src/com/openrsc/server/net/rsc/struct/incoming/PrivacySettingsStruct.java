package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class PrivacySettingsStruct extends AbstractStruct<OpcodeIn> {

	public int hideStatus;
	public int blockChat;
	public int blockPrivate;
	public int blockTrade;
	public int blockDuel;
}
