package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class BankStruct extends AbstractStruct<OpcodeIn> {

	public int catalogID;
	public int amount;
	public boolean noted;
	public int presetSlot;
}
