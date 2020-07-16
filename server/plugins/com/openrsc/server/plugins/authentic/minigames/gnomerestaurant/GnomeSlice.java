package com.openrsc.server.plugins.authentic.minigames.gnomerestaurant;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeSlice implements UseInvTrigger {

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.ORANGE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.LIME.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.PINEAPPLE.id()) ||
				compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.FRESH_PINEAPPLE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.LEMON.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.GRAPEFRUIT.id()))
			return true;

		return false;
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.ORANGE.id())) {
			player.message("you can slice or dice the orange");
			int menu = multi(player,
				"slice orange",
				"dice orange");
			if (menu == 0) {
				player.message("you slice the orange");
				player.getCarriedItems().remove(new Item(ItemId.ORANGE.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.ORANGE_SLICES.id()));
			} else if (menu == 1) {
				player.message("you cut the orange into chunks");
				player.getCarriedItems().remove(new Item(ItemId.ORANGE.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.DICED_ORANGE.id()));
			}
		}
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.LIME.id())) {
			player.message("you can slice or dice the lime");
			int menu = multi(player,
				"slice lime",
				"dice lime");
			if (menu == 0) {
				player.message("you slice the lime");
				player.getCarriedItems().remove(new Item(ItemId.LIME.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.LIME_SLICES.id()));
			} else if (menu == 1) {
				player.message("you cut the lime into chunks");
				player.getCarriedItems().remove(new Item(ItemId.LIME.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.LIME_CHUNKS.id()));
			}
		}
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.PINEAPPLE.id()) ||
				compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.FRESH_PINEAPPLE.id())) {
			int pineappleId;
			if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.PINEAPPLE.id())) {
				pineappleId = ItemId.PINEAPPLE.id();
			} else {
				pineappleId = ItemId.FRESH_PINEAPPLE.id();
			}
			player.message("you can slice or dice the pineapple");
			int menu = multi(player,
				"slice pineapple",
				"dice pineapple");
			if (menu == 0) {
				player.message("you slice the pineapple into rings");
				player.getCarriedItems().remove(new Item(pineappleId));
				player.getCarriedItems().getInventory().add(new Item(ItemId.PINEAPPLE_RING.id()));
				give(player, ItemId.PINEAPPLE_RING.id(), 3);
			} else if (menu == 1) {
				player.message("you cut the pineapple into chunks");
				player.getCarriedItems().remove(new Item(pineappleId));
				player.getCarriedItems().getInventory().add(new Item(ItemId.PINEAPPLE_CHUNKS.id()));
			}
		}
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.LEMON.id())) {
			player.message("you can slice or dice the lemon");
			int menu = multi(player,
				"slice lemon",
				"dice lemon");
			if (menu == 0) {
				player.message("you slice the lemon");
				player.getCarriedItems().remove(new Item(ItemId.LEMON.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.LEMON_SLICES.id()));
			} else if (menu == 1) {
				player.message("you cut the lemon into chunks");
				player.getCarriedItems().remove(new Item(ItemId.LEMON.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.DICED_LEMON.id()));
			}
		}
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.GRAPEFRUIT.id())) {
			player.message("you can slice or dice the grapefruit");
			int menu = multi(player,
				"slice grapefruit",
				"dice grapefruit");
			if (menu == 0) {
				player.message("you slice the grapefruit");
				player.getCarriedItems().remove(new Item(ItemId.GRAPEFRUIT.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.GRAPEFRUIT_SLICES.id()));
			} else if (menu == 1) {
				player.message("you cut the grapefruit into chunks");
				player.getCarriedItems().remove(new Item(ItemId.GRAPEFRUIT.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.DICED_GRAPEFRUIT.id()));
			}
		}
	}
}
