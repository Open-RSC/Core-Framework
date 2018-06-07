package org.openrsc.server.npchandler;

import java.util.ArrayList;

public class NpcHandlerDef {
	public ArrayList<Integer> ids;
	public String className;

	public NpcHandlerDef(String className) {
		this.ids = new ArrayList<Integer>();
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public ArrayList<Integer> getAssociatedNpcs() {
		return ids;
	}
}