package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class DuelSettingsStruct extends AbstractStruct<OpcodeOut> {

	public int disallowRetreat;
	public int disallowMagic;
	public int disallowPrayer;
	public int disallowWeapons;
}
