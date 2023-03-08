package com.openrsc.server.event.rsc.impl.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum PVPCombatFormulaType {
	RSCD(0),
	STORMY(1);

	private static final Map<Integer, PVPCombatFormulaType> byType = new HashMap<Integer, PVPCombatFormulaType>();
	public static final PVPCombatFormulaType DEFAULT = RSCD;

	static {
		for (PVPCombatFormulaType type : PVPCombatFormulaType.values()) {
			if (byType.put(type.getType(), type) != null) {
				throw new IllegalArgumentException("duplicate id: " + type.getType());
			}
		}
	}

	public static PVPCombatFormulaType getByType(Integer type) {
		return byType.getOrDefault(type, PVPCombatFormulaType.RSCD);
	}

	PVPCombatFormulaType(int type) {
		this.type = type;
	}

	private final int type;

	public static PVPCombatFormulaType resolveType(String type) {
		try {
			return PVPCombatFormulaType.valueOf(type.toUpperCase());
		} catch (Exception e) {
			try {
				return PVPCombatFormulaType.getByType(Integer.parseInt(type));
			} catch (Exception ex) {
				return PVPCombatFormulaType.DEFAULT;
			}
		}
	}

	public int getType() {
		return this.type;
	}
}
