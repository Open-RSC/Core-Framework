package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.external.ItemLoc;
import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

import java.util.List;

public class GroundItemsUpdateStruct extends AbstractStruct<OpcodeOut> {

	public List<ItemLoc> objects;
}
