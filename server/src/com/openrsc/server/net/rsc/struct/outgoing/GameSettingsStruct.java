package com.openrsc.server.net.rsc.struct.outgoing;

import com.openrsc.server.net.rsc.enums.OpcodeOut;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

import java.util.List;

public class GameSettingsStruct extends AbstractStruct<OpcodeOut> {

	public int cameraModeAuto;
	public int mouseButtonOne;
	public int soundDisabled;
	public int playerKiller; // retro rsc
	public int pkChangesLeft; // retro rsc started at 2
	public List<Integer> customOptions; // custom options added
}
