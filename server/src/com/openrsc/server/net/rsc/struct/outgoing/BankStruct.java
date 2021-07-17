package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class BankStruct extends AbstractStruct<OpcodeOut> {

	public int itemsStoredSize;
	public int maxBankSize;
	public int[] catalogIDs;
	public int[] amount;
}
