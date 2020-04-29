package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface OpBoundTrigger {
	void onOpBound(Player player, GameObject obj, Integer click);
	boolean blockOpBound(Player player, GameObject obj, Integer click);
}
