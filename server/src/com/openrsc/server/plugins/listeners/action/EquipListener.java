package com.openrsc.server.plugins.listeners.action;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;

public interface EquipListener {

	public void onEquip(Player player, Item item, Boolean sound, Boolean fromBank);
}
