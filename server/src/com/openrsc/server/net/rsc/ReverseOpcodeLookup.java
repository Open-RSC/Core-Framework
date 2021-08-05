package com.openrsc.server.net.rsc;

import com.openrsc.server.net.rsc.enums.OpcodeIn;

import java.util.HashMap;
import java.util.Map;

/**
 * Reverse Lookup to map a numeric input opcode to its OpcodeIn, when the
 * client version isn't know
 */
public class ReverseOpcodeLookup {
	private static final Map<Integer, OpcodeIn> reverseMap = new HashMap<Integer, OpcodeIn>() {{
		put(0, OpcodeIn.LOGIN);
		put(2, OpcodeIn.REGISTER_ACCOUNT);
		put(4, OpcodeIn.FORGOT_PASSWORD);
		put(8, OpcodeIn.RECOVERY_ATTEMPT);
		put(19, OpcodeIn.RELOGIN);
	}};

	public static OpcodeIn getOpcode(Integer numericOpcode) {
		return reverseMap.getOrDefault(numericOpcode, null);
	}
}
