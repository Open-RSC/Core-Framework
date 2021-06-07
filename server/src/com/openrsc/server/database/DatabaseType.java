package com.openrsc.server.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum DatabaseType {
	MYSQL(0),
	SQLITE(1);

	private static final Map<Integer, DatabaseType> byType = new HashMap<Integer, DatabaseType>();
	public static final DatabaseType DEFAULT = SQLITE;

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

	public static DatabaseType resolveType(String type) {
		try {
			return DatabaseType.valueOf(type.toUpperCase());
		} catch (Exception e) {
			try {
				return DatabaseType.getByType(Integer.parseInt(type));
			} catch (Exception ex) {
				return DatabaseType.DEFAULT;
			}
		}
	}

    public int getType() {
		return this.type;
	}
}
