package com.openrsc.server.model.states;

public enum EntityType {
	NONE(0),
    GROUND_ITEM(1),
    INVENTORY_ITEM(2),
    NPC(3),
    LOCATION(4),
    BOUNDARY(5),
    PLAYER(6),
    COORDINATE(7);

    private final int entityType;

    EntityType(final int entityType) {
        this.entityType = entityType;
    }

    public String toString() {
        return entityType + "";
    }
}
