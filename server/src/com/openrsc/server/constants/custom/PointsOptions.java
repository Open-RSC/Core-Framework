package com.openrsc.server.constants.custom;

import java.util.HashMap;
import java.util.Map;

public enum PointsOptions {

	REDUCE_DEF(0),
	INC_DEF(1),
	INC_ATK(2),
	INC_STR(3),
	INC_RNG(4),
	INC_PRAY(5),
	INC_MAGE(6),
	REDUCE_ATK(7),
	REDUCE_STR(8),
	REDUCE_RNG(9),
	REDUCE_PRAY(10),
	REDUCE_MAGE(11),
	POINTS2GP(12),
	SAVE_PRESET(13),
	LOAD_PRESET1(14),
	LOAD_PRESET2(15),
	LOAD_PRESET3(16),
	LOAD_PRESET4(17),
	LOAD_PRESET5(18),
	;

	private int option;

	private static final Map<Integer, PointsOptions> byId = new HashMap<Integer, PointsOptions>();

	static {
		for (PointsOptions option : PointsOptions.values()) {
			if (byId.put(option.id(), option) != null) {
				throw new IllegalArgumentException("duplicate id: " + option.id());
			}
		}
	}

	public static PointsOptions getById(Integer id) {
		return byId.getOrDefault(id, null);
	}

	PointsOptions(int option) {
		this.option = option;
	}

	public int id() {
		return this.option;
	}
}
