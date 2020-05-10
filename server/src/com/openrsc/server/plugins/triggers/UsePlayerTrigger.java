package com.openrsc.server.plugins.triggers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface UsePlayerTrigger {
	void onUsePlayer(Player player, Player otherPlayer, Item item);
	boolean blockUsePlayer(Player player, Player otherPlayer, Item item);
}
