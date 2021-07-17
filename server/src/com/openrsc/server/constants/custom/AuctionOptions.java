package com.openrsc.server.constants.custom;

import java.util.HashMap;
import java.util.Map;

public enum AuctionOptions {

	BUY(0),
	CREATE(1),
	ABORT(2),
	REFRESH(3),
	CLOSE(4),
	DELETE(5);

	private int option;

	private static final Map<Integer, AuctionOptions> byId = new HashMap<Integer, AuctionOptions>();

	static {
		for (AuctionOptions option : AuctionOptions.values()) {
			if (byId.put(option.id(), option) != null) {
				throw new IllegalArgumentException("duplicate id: " + option.id());
			}
		}
	}

	public static AuctionOptions getById(Integer id) {
		return byId.getOrDefault(id, null);
	}

	AuctionOptions(int option) {
		this.option = option;
	}

	public int id() {
		return this.option;
	}
}
