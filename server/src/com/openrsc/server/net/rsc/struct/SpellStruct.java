package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.model.Point;
import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class SpellStruct extends AbstractStruct<OpcodeIn> {

	public int spellIdx;
	public int targetIndex;
	public Point targetCoord;
	public int direction;

}
