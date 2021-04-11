package com.openrsc.server.net.rsc.struct;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

public class SecuritySettingsStruct extends AbstractStruct<OpcodeIn> {

	public String[] passwords;
	public String[] questions;
	public String[] answers;
	public String[] details;
}
