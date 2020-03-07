package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;

public interface UseObjTrigger {
	void onUseObj(Item myItem, GroundItem item, Player player);
	boolean blockUseObj(Item myItem, GroundItem item, Player player);
}
