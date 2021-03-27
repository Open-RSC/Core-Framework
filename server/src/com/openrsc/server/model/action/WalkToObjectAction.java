package com.openrsc.server.model.action;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public abstract class WalkToObjectAction extends WalkToAction {

	private GameObject object;

	public WalkToObjectAction(final Player owner, final GameObject object) {
		super(owner, object.getLocation());
		this.object = object;
	}

	@Override
	public boolean shouldExecuteInternal() {
		return getPlayer().atObject(object);
	}

}
