package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class ShopStruct extends AbstractStruct<OpcodeOut> {

	public int itemsStockSize;
	public int isGeneralStore;
	public int sellModifier;
	public int buyModifier;
	public int stockSensitivity;
	public int[] catalogIDs;
	public int[] amount;
	public int[] baseAmount;
	public int[] price;
}
