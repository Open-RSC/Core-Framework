package com.openrsc.server.plugins.listeners;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnItemListener {
	void onInvUseOnItem(Player player, Item item1, Item item2);
	boolean blockInvUseOnItem(Player player, Item item1, Item item2);
}
