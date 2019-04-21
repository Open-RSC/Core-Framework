package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvUseOnGroundItemListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnGroundItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;

public class PotFlour implements InvUseOnGroundItemListener, InvUseOnGroundItemExecutiveListener, PickupListener, PickupExecutiveListener {
	
	@Override
	public boolean blockInvUseOnGroundItem(Item myItem, GroundItem item, Player player) {
		return item.getID() == ItemId.FLOUR.id() && myItem.getID() == ItemId.POT.id();
	}
	
	@Override
	public boolean blockPickup(Player p, GroundItem item) {
		return item.getID() == ItemId.FLOUR.id();
	}

	@Override
	public void onInvUseOnGroundItem(Item myItem, GroundItem item, Player player) {
		if (myItem.getID() == ItemId.POT.id()) {
			if (player.getInventory().remove(myItem) < 0)
				return;
			player.message("You put the flour in the pot");
			World.getWorld().unregisterItem(item);
			player.getInventory().add(new Item(ItemId.POT_OF_FLOUR.id()));
			return;
		}
	}

	@Override
	public void onPickup(Player player, GroundItem item) {
		if (item.getID() == ItemId.FLOUR.id()) {
			if (player.getInventory().hasItemId(ItemId.POT.id())) {
				player.message("You put the flour in the pot");
				World.getWorld().unregisterItem(item);
				player.getInventory().replace(ItemId.POT.id(), ItemId.POT_OF_FLOUR.id());
			} else {
				player.message("I can't pick it up!");
				player.message("I need a pot to hold it in");
			}
			return;
		}
	}
	
	
}
