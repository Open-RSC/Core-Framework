package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class SecuritySettingsStruct extends AbstractStruct<OpcodeIn> {

	public String[] passwords;
	public String[] questions;
	public String[] answers;
	public String[] details;
}
