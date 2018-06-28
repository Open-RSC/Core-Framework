package org.openrsc.server.packethandler;

import java.util.ArrayList;

public class PacketHandlerDef {
	public ArrayList<Integer> ids;
	public String className;

	public PacketHandlerDef(String className) {
		this.ids = new ArrayList<Integer>();
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public ArrayList<Integer> getAssociatedPackets() {
		return ids;
	}
}