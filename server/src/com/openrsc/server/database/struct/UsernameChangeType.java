package com.openrsc.server.database.struct;

import java.util.HashMap;
import java.util.Map;

public enum UsernameChangeType {
	NOT_RENAMED(-1),
	VOLUNTARY(0),
	INAPPROPRIATE(1),
	CAPITALIZATION(2),
	RELEASED(3),
	INVALID(4),
	REMOVE_FORMER_NAME(5);

	private final int changeType;

	UsernameChangeType(int changeType) {
		this.changeType = changeType;
	}

	public int id() {
		return changeType;
	}


	private static final Map<Integer, UsernameChangeType> byId = new HashMap<Integer, UsernameChangeType>();

	static {
		for (UsernameChangeType changeType : UsernameChangeType.values()) {
			if (byId.put(changeType.id(), changeType) != null) {
				throw new IllegalArgumentException("duplicate id: " + changeType.id());
			}
		}
	}

	public static UsernameChangeType getById(Integer id) {
		return byId.getOrDefault(id, NOT_RENAMED);
	}


}
