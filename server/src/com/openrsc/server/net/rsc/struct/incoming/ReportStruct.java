package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ReportStruct extends AbstractStruct<OpcodeIn> {

	public String targetPlayerName;
	public byte reason;
	public byte suggestsOrMutes;
}
