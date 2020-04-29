package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface UseInvTrigger {
	void onUseInv(Player player, Integer invIndex, Item item1, Item item2);
	boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2);
}
