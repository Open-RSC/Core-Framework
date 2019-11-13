package com.openrsc.server.constants;

public enum IronmanMode {
	None(0),
	Ironman(1),
	Ultimate(2),
	Hardcore(3),
	Transfer(4);

	private int mode;

	IronmanMode(int mode) {
		this.mode = mode;
	}

	public int id() {
		return this.mode;
	}
}
