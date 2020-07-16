package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class CoalTrucks implements OpLocTrigger, UseLocTrigger {

	private static int COAL_TRUCK = 383;

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == COAL_TRUCK) {
			if (player.getCache().hasKey("coal_truck") && player.getCache().getInt("coal_truck") > 0) {
				int coalLeft = player.getCache().getInt("coal_truck");
				player.playerServerMessage(MessageType.QUEST, "You remove a piece of coal from the truck");
				give(player, ItemId.COAL.id(), 1);
				player.getCache().set("coal_truck", coalLeft - 1);
			} else {
				player.playerServerMessage(MessageType.QUEST, "there is no coal left in the truck\"");
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == COAL_TRUCK;
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == COAL_TRUCK && item.getCatalogId() == ItemId.COAL.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == COAL_TRUCK && item.getCatalogId() == ItemId.COAL.id()) {
			int coalAmount = player.getCarriedItems().getInventory().countId(ItemId.COAL.id());
			for (int i = 0; i < coalAmount; i++) {
				if (player.getCache().hasKey("coal_truck")) {
					if (player.getCache().getInt("coal_truck") >= 120) {
						player.message("The coal truck is full");
						break;
					}
					int coalDeposited = player.getCache().getInt("coal_truck");
					player.getCache().set("coal_truck", coalDeposited + 1);
				} else {
					player.getCache().set("coal_truck", coalAmount);
				}
				player.playerServerMessage(MessageType.QUEST, "You put a piece of coal in the truck");
				player.getCarriedItems().remove(new Item(ItemId.COAL.id()));
				delay();
			}
		}
	}
}
