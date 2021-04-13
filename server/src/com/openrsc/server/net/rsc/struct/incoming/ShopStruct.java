package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ShopStruct extends AbstractStruct<OpcodeIn> {

	public int catalogID;
	public int stockAmount;
	public int price;
	public int amount;
}
