package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class BankStruct extends AbstractStruct<OpcodeIn> {

	public int catalogID;
	public int amount;
	public boolean noted;
	public int magicNumber;
	public int presetSlot;
}
