package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class StatInfoStruct  extends AbstractStruct<OpcodeOut> {

	public int[] currentLevels;
	public int[] maxLevels;
	public int[] experiences;
	public int questPoints;
}
