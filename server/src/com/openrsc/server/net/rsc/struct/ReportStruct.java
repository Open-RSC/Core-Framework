package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class ReportStruct extends AbstractStruct<OpcodeIn> {

	public String targetPlayerName;
	public byte reason;
	public byte suggestsOrMutes;
}
