package com.openrsc.server.plugins.authentic.skills.crafting;

public class Mould {
	String menuString;
	int itemId;
	String failString;

	Mould(String menuString, int itemId, String failString) {
		this.menuString = menuString;
		this.itemId = itemId;
		this.failString = failString;
	}
}
