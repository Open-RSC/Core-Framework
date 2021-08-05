package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class MenuOptionStruct extends AbstractStruct<OpcodeOut> {

	public int numOptions;
	public String[] optionTexts;
}
