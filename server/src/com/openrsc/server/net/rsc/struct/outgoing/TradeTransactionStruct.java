package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class TradeTransactionStruct extends AbstractStruct<OpcodeOut> {

	public String targetPlayer;
	public int opponentTradeCount;
	public int[] opponentCatalogIDs;
	public int[] opponentAmounts;
	public int[] opponentNoted;
	public int myCount;
	public int[] myCatalogIDs;
	public int[] myAmounts;
	public int[] myNoted;
}
