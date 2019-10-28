package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface UnWieldListener {

	public void onUnWield(Player player, Item item);

}
