package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnGroundItemListener {

	public void onInvUseOnGroundItem(Item myItem, GroundItem item, Player player);

}
