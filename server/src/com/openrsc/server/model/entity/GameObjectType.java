package com.openrsc.server.model.entity;

public enum GameObjectType {
	SCENERY("Scenery", 0),
	BOUNDARY("Boundary", 1);

	private final String label;
	private final int id;

	GameObjectType(String label, int type) {
		this.label = label;
		this.id = type;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public static GameObjectType fromInt(int value) {
		return value == SCENERY.getId() ? SCENERY : BOUNDARY;
	}

	@Override
	public String toString() {
		return this.getLabel();
	}
}
