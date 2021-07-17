package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

import java.util.List;

public class AppearanceUpdateStruct extends AbstractStruct<OpcodeOut> {

	public List<Object> info; // diverse types, byte, short, string
}
