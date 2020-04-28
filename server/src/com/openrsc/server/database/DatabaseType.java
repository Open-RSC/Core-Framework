package com.openrsc.server.database;

import java.util.HashMap;
import java.util.Map;

public enum DatabaseType {
	MYSQL(0);

	private static final Map<Integer, DatabaseType> byType = new HashMap<Integer, DatabaseType>();

	static {
		for (DatabaseType type : DatabaseType.values()) {
			if (byType.put(type.getType(), type) != null) {
				throw new IllegalArgumentException("duplicate id: " + type.getType());
			}
		}
	}

	public static DatabaseType getByType(Integer type) {
		return byType.getOrDefault(type, DatabaseType.MYSQL);
	}

	DatabaseType(int type) {
		this.type = type;
	}

	private final int type;

	public int getType() {
		return this.type;
	}
}
