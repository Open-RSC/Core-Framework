package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class IgnoreListStruct extends AbstractStruct<OpcodeOut> {

	public int listSize;
	public String[] name;
	public String[] formerName;
	public boolean updateExisting;
}
