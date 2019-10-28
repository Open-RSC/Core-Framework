package com.openrsc.server.plugins.listeners.executive;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnItemExecutiveListener {

	public boolean blockInvUseOnItem(Player player, Item item1, Item item2);

}
