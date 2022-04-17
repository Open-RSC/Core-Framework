package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class UnlockedAppearancesStruct extends AbstractStruct<OpcodeOut> {
	public boolean[] unlockedHairColours;
	public boolean[] unlockedTopColours;
	public boolean[] unlockedBottomColours;
	public boolean[] unlockedSkinColours;
	public boolean[] unlockedHairStyles;
	public boolean[] unlockedBodyTypes;
}
