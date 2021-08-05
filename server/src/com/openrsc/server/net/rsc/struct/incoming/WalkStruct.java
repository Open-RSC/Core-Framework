package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.model.Point;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

import java.util.ArrayList;
import java.util.List;

public class WalkStruct extends AbstractStruct<OpcodeIn> {

	public Point firstStep;
	public List<Point> steps = new ArrayList<>();

}
