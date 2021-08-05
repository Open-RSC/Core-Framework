package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ClanSettingsStruct extends AbstractStruct<OpcodeOut> {

	public int magicNumber = 3;
	public int kickSetting;
	public int inviteSetting;
	public int allowSearchJoin;
	public int allowSetting0;
	public int allowSetting1;
}
