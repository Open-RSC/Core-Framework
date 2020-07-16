package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class InvItemPoisoning implements UseInvTrigger {

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return item1.getCatalogId() == ItemId.WEAPON_POISON.id() || item2.getCatalogId() == ItemId.WEAPON_POISON.id();
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (item1.getCatalogId() == ItemId.WEAPON_POISON.id()) {
			applyPoison(player, item2);
		} else if (item2.getCatalogId() == ItemId.WEAPON_POISON.id()) {
			applyPoison(player, item1);
		}
	}


	private void applyPoison(Player player, Item item) {
		int makeAmount = 1, maxAmount;
		String rawItemName = item.getDef(player.getWorld()).getName().toLowerCase();
		String procItemName;

		if (item.getDef(player.getWorld()).isStackable()) {
			//6 darts or 5 bolts/arrows
			maxAmount = rawItemName.contains("dart") ? 6 : 5;
			makeAmount = ifheld(player, item.getCatalogId(), maxAmount) ? maxAmount : player.getCarriedItems().getInventory().countId(item.getCatalogId());

			procItemName = "some ";
			if (rawItemName.contains("dart")) {
				procItemName += "darts";
			} else if (rawItemName.contains("bolt")) {
				procItemName += "bolts";
			} else if (rawItemName.contains("arrow")) {
				procItemName += "arrows";
			} else {
				procItemName += (rawItemName + (!rawItemName.endsWith("s") ? "s" : ""));
			}
			procItemName += "!";
		}
		else {
			procItemName = "a " + rawItemName + ".";
		}
		Item poisonedItem = getPoisonedItem(player.getWorld(), item.getDef(player.getWorld()).getName());
		if (poisonedItem != null) {
			if (player.getCarriedItems().remove(new Item(ItemId.WEAPON_POISON.id())) != -1
				&& player.getCarriedItems().remove(new Item(item.getCatalogId(), makeAmount)) != -1) {
				player.message("You poison " + procItemName);
				give(player, poisonedItem.getCatalogId(), makeAmount);
			}
		} else {
			player.message("Nothing interesting happens");
		}
	}

	private Item getPoisonedItem(World world, String name) {
		String poisonedVersion = "Poisoned " + name;
		String poisonedVersion2 = "Poison " + name;
		for (int i = 0; i < world.getServer().getEntityHandler().items.size(); i++) {
			ItemDefinition def = world.getServer().getEntityHandler().getItemDef(i);
			if (def.getName().equalsIgnoreCase(poisonedVersion) || def.getName().equalsIgnoreCase(poisonedVersion2)) {
				return new Item(i);
			}
		}
		return null;
	}
}
