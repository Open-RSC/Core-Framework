package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class PrivacySettingsStruct extends AbstractStruct<OpcodeIn> {

	public byte[] newSettings = new byte[4];
}
