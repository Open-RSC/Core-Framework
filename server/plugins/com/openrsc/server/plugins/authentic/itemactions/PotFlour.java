package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.content.EnchantedCrowns;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.UseObjTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

public class PotFlour implements UseObjTrigger, TakeObjTrigger {

	@Override
	public boolean blockUseObj(Player player, GroundItem item, Item myItem) {
		return item.getID() == ItemId.FLOUR.id() && myItem.getCatalogId() == ItemId.POT.id();
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem item) {
		return item.getID() == ItemId.FLOUR.id();
	}

	@Override
	public void onUseObj(Player player, GroundItem item, Item myItem) {
		if (myItem.getCatalogId() == ItemId.POT.id()) {
			if (player.getCarriedItems().remove(myItem) < 0)
				return;
			takeFlour(player, item);
			return;
		}
	}

	@Override
	public void onTakeObj(Player player, GroundItem item) {
		if (item.getID() == ItemId.FLOUR.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.POT.id())) {
				if (player.getCarriedItems().remove(new Item(ItemId.POT.id())) == -1) return;
				takeFlour(player, item);
			} else {
				player.message("I can't pick it up!");
				player.message("I need a pot to hold it in");
			}
		}
	}

	private void takeFlour(Player player, GroundItem item) {
		if (item.getLocation().fromHopper()
			&& EnchantedCrowns.shouldActivate(player, ItemId.CROWN_OF_DEW)) {
			int doughOpt = player.getCache().hasKey("dough_conf") ? player.getCache().getInt("dough_conf") : DataConversions.random(0, 3);
			player.playerServerMessage(MessageType.QUEST, "Your crown shines and the flour humidifies");
			player.playerServerMessage(MessageType.QUEST, "into some usable dough");
			player.getWorld().unregisterItem(item);
			int doughId;
			switch (doughOpt) {
				case 0:
				default:
					doughId = ItemId.BREAD_DOUGH.id();
					break;
				case 1:
					doughId = ItemId.PASTRY_DOUGH.id();
					break;
				case 2:
					doughId = ItemId.PIZZA_BASE.id();
					break;
				case 3:
					doughId = ItemId.UNCOOKED_PITTA_BREAD.id();
					break;
			}
			player.getCarriedItems().getInventory().add(new Item(doughId));
			player.getCarriedItems().getInventory().add(new Item(ItemId.POT.id()));
			EnchantedCrowns.useCharge(player, ItemId.CROWN_OF_DEW);
		} else {
			player.message("You put the flour in the pot");
			player.getWorld().unregisterItem(item);
			player.getCarriedItems().getInventory().add(new Item(ItemId.POT_OF_FLOUR.id()));
		}
	}

}
