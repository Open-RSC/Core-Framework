package com.openrsc.server.external;

import com.openrsc.server.constants.ItemId;

import java.util.HashMap;
import java.util.Map;

public enum Gauntlets {
	STEEL(0, ItemId.STEEL_GAUNTLETS.id()),
	GOLDSMITHING(1, ItemId.GAUNTLETS_OF_GOLDSMITHING.id()),
	COOKING(2, ItemId.GAUNTLETS_OF_COOKING.id()),
	CHAOS(3, ItemId.GAUNTLETS_OF_CHAOS.id());
	private int id;
	private int catalogId;

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

	Gauntlets(int id, int catalogId) {
		this.id = id;
		this.catalogId = catalogId;
	}

	public int id() {
		return this.id;
	}

	public int catalogId() {
		return catalogId;
	}
}
