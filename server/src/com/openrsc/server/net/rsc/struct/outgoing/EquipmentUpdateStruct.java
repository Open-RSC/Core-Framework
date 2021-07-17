package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class EquipmentUpdateStruct extends AbstractStruct<OpcodeOut> {

	public int slotIndex;
	public int catalogID; // 0xFFFF if no item
	public int amount = 0; // only sent if amount present, i.e stackable item
}
