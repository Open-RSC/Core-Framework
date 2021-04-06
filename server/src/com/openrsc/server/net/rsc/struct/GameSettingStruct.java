package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class GameSettingStruct extends AbstractStruct<OpcodeIn> {

	public int index;
	public int value;
	public int cameraModeAuto;
	public int playerKiller;
	public int mouseButtonOne;
	public int soundDisabled;
}
