package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnItemListener {

	public void onInvUseOnItem(Player player, Item item1, Item item2);
}
