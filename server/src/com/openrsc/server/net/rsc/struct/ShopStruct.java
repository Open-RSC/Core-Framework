package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class ShopStruct extends AbstractStruct<OpcodeIn> {

	public int catalogID;
	public int stockAmount;
	public int amount;
}
