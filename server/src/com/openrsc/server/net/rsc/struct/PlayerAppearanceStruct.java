package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class PlayerAppearanceStruct extends AbstractStruct<OpcodeIn> {

	public byte headRestrictions;
	public byte headType;
	public byte bodyType;
	public byte mustEqual2;
	public int hairColour;
	public int topColour;
	public int trouserColour;
	public int skinColour;
	public int chosenClass;
	public int pkMode;
	public int ironmanMode = -1;
	public int isOneXp = -1;
}
