package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface InvUseOnPlayerListener {

	public void onInvUseOnPlayer(Player player, Player otherPlayer, Item item);

}
