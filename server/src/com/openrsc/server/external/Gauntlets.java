package com.openrsc.server.external;

import java.util.HashMap;
import java.util.Map;

public enum Gauntlets {
	STEEL(0),
	GOLDSMITHING(1),
	COOKING(2),
	CHAOS(3);
	private int id;
	
	private static final Map<Integer, Gauntlets> byId = new HashMap<Integer, Gauntlets>();
	static {
		for (Gauntlets enchantment : Gauntlets.values()) {
			if (byId.put(enchantment.id(), enchantment) != null) {
				throw new IllegalArgumentException("duplicate id: " + enchantment.id());
			}
		}
	}
	
	public static Gauntlets getById(Integer id) {
		return byId.getOrDefault(id, Gauntlets.STEEL);
	}
	
	Gauntlets(int id) {
		this.id = id;
	}
	
	public int id() {
		return this.id;
	}
}
