package com.openrsc.server.model.entity;

public enum GameObjectType {
	GAME_OBJECT("GameObject", 0),
	WALL_OBJECT("WallObject", 1);

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
		return value == GAME_OBJECT.getId() ? GAME_OBJECT : WALL_OBJECT;
	}

	@Override
	public String toString() {
		return this.getLabel();
	}
}
