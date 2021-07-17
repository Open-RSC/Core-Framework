package com.openrsc.server.constants.custom;

import java.util.HashMap;
import java.util.Map;

public enum PointsOptions {

	REDUCE_DEFENSE(0),
	INCREASE_DEFENSE(1),
	INCREASE_ATTACK(2),
	INCREASE_STRENGTH(3),
	INCREASE_RANGED(4),
	INCREASE_PRAYER(5),
	INCREASE_MAGIC(6),
	REDUCE_ATTACK(7),
	REDUCE_STRENGTH(8),
	REDUCE_RANGED(9),
	REDUCE_PRAYER(10),
	REDUCE_MAGIC(11),
	POINTS_TO_GP(12),
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
