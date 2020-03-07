package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.InvUseOnItemListener;

import static com.openrsc.server.plugins.Functions.*;

public class InvItemPoisoning implements InvUseOnItemListener {

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		return item1.getCatalogId() == ItemId.WEAPON_POISON.id() || item2.getCatalogId() == ItemId.WEAPON_POISON.id();
	}

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
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
			makeAmount = hasItem(player, item.getCatalogId(), maxAmount) ? maxAmount : player.getCarriedItems().getInventory().countId(item.getCatalogId());

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
			if (removeItem(player, ItemId.WEAPON_POISON.id(), 1) && removeItem(player, item.getCatalogId(), makeAmount)) {
				player.message("You poison " + procItemName);
				addItem(player, poisonedItem.getCatalogId(), makeAmount);
			}
		} else {
			player.message("Nothing interesting happens");
		}
	}

	private Item getPoisonedItem(World world, String name) {
		String poisonedVersion = "Poisoned " + name;
		String poisonedVersion2 = "Poison " + name;
		for (int i = 0; i < world.getServer().getEntityHandler().items.length; i++) {
			ItemDefinition def = world.getServer().getEntityHandler().getItemDef(i);
			if (def.getName().equalsIgnoreCase(poisonedVersion) || def.getName().equalsIgnoreCase(poisonedVersion2)) {
				return new Item(i);
			}
		}
		return null;
	}
}
