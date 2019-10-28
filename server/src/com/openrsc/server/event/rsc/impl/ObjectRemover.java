package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.world.World;


public class ObjectRemover extends GameTickEvent {

	private GameObject object;

	public ObjectRemover(World world, GameObject object, int ticks) {
		super(world,null, ticks, "Object Remover");
		this.object = object;
	}

	public boolean equals(Object o) {
		if (o instanceof ObjectRemover) {
			return ((ObjectRemover) o).getObject().equals(getObject());
		}
		return false;
	}

	public GameObject getObject() {
		return object;
	}

	public void run() {
		getWorld().unregisterGameObject(object);
		stop();
	}

}
