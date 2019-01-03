package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnGroundItemExecutiveListener {

	public boolean blockInvUseOnGroundItem(Item myItem, GroundItem item, Player player);

}
