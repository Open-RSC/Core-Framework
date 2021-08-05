package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

import java.util.List;

public class ServerConfigsStruct extends AbstractStruct<OpcodeOut> {

	public List<Object> configs; //custom protocol, too many to lay out and varying type
}
