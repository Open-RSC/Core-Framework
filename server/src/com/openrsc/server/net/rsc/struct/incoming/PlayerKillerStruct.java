package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class PlayerKillerStruct extends AbstractStruct<OpcodeIn> {

	public int changedToKiller;
}
