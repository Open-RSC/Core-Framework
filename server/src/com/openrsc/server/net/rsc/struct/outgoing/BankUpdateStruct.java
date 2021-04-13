package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class BankUpdateStruct extends AbstractStruct<OpcodeOut> {

	public int slot;
	public int catalogID;
	public int amount;
}
