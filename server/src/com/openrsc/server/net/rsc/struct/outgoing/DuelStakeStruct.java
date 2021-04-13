package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class DuelStakeStruct extends AbstractStruct<OpcodeOut> {

	public int count;
	public int[] catalogIDs;
	public int[] amounts;
	public int[] noted;
}
