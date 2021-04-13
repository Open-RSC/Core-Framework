package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.model.Point;
import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

import java.util.List;

public class ClearLocationsStruct extends AbstractStruct<OpcodeOut> {

	public List<Point> points;
}
