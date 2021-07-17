package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.constants.Spells;
import com.openrsc.server.model.Point;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class SpellStruct extends AbstractStruct<OpcodeIn> {

	public Spells spell;
	public int targetIndex;
	public Point targetCoord;
	public int direction;

}
