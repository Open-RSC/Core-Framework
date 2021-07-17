package com.openrsc.server.net.rsc.struct;

public abstract class AbstractStruct<Opcode> {
	protected Opcode opcode;

	public Opcode getOpcode() {
		return opcode;
	}

	public void setOpcode(Opcode opcode) { this.opcode = opcode; }
}
