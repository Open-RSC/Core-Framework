package com.openrsc.server.net.rsc;

import com.openrsc.server.util.rsc.ISAACCipher;

/**
 * @author n0m
 */
public class ISAACContainer {

	private ISAACCipher inCipher;
	private ISAACCipher outCipher;

	public ISAACContainer(ISAACCipher in, ISAACCipher out) {
		inCipher = in;
		outCipher = out;
	}

	public int encodeOpcode(int opcode) {
		return opcode /*+ outCipher.nextInt() & 0xFF*/;
	}

	public int decodeOpcode(int opcode) {
		return opcode /*- inCipher.nextInt() & 0xFF*/;
	}
}
