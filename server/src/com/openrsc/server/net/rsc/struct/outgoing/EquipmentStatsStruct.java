package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class EquipmentStatsStruct extends AbstractStruct<OpcodeOut> {

	public int armourPoints;
	public int weaponAimPoints;
	public int weaponPowerPoints;
	public int magicPoints;
	public int prayerPoints;
	public int hidingPoints; // retro rsc equipment stat
	public int rangedPoints; // not sent in original rsc but possibly virtual
}
