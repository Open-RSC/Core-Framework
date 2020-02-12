package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnPlayerExecutiveListener {
	boolean blockInvUseOnPlayer(Player player, Player otherPlayer, Item item);
}
