package com.openrsc.server.model.entity;

import com.openrsc.server.model.entity.player.Player;

public abstract class VisibleCondition {
	public abstract boolean isVisibleTo(Entity entity, Player observer);
}
