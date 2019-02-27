package com.openrsc.client.entityhandling.defs;

public abstract class EntityDef {
	public String name;
	public String description;
	public int id;

	EntityDef(String name, String description, int id) {
		this.name = name;
		this.description = description;
		this.id = id;
	}

	EntityDef(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
