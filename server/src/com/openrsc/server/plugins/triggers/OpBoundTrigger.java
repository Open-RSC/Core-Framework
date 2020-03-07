package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;

public interface OpBoundTrigger {
	void onOpBound(GameObject obj, Integer click, Player p);
	boolean blockOpBound(GameObject obj, Integer click, Player player);
}
