package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

import java.util.List;

public class BankPresetStruct extends AbstractStruct<OpcodeOut> {

	public int slotIndex;
	public List<Object> inventoryItems; // either empty or Item
	public List<Object> equipmentItems; // either empty or Item
}
