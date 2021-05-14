package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.constants.Classes;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class PlayerAppearanceStruct extends AbstractStruct<OpcodeIn> {

	public byte headRestrictions;
	public byte headType;
	public byte bodyType;
	public byte mustEqual2;
	public int hairColour;
	public int topColour;
	public int trouserColour;
	public int skinColour;
	public Classes chosenClass;
	public int pkMode;
	public int ironmanMode = -1;
	public int isOneXp = -1;
}
