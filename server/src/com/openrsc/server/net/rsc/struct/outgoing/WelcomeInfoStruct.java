package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class WelcomeInfoStruct extends AbstractStruct<OpcodeOut> {

	public String lastIp;
	public int daysSinceLogin;
	public int daysSinceRecoveryChange = -1; // default not set
	public int unreadMessages;
}
