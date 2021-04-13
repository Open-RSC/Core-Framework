package com.openrsc.server.net.rsc.struct.incoming;

import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.AbstractStruct;

public class OptionsStruct extends AbstractStruct<OpcodeIn> {

	public int index;
	public byte value;
	public byte value2;
	public byte value3;
	public int slot;
	public int to;
	public String pin;
	public int id;
	public int amount;
	public int price;
	public String name;
	public String tag;
	public String player;
}
