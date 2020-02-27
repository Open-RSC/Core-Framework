package com.openrsc.server.plugins.minigames.gnomerestaurant;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeSlice implements InvUseOnItemListener, InvUseOnItemExecutiveListener {

	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
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
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.ORANGE.id())) {
			p.message("you can slice or dice the orange");
			int menu = showMenu(p,
				"slice orange",
				"dice orange");
			if (menu == 0) {
				p.message("you slice the orange");
				p.getCarriedItems().getInventory().replace(ItemId.ORANGE.id(), ItemId.ORANGE_SLICES.id());
			} else if (menu == 1) {
				p.message("you cut the orange into chunks");
				p.getCarriedItems().getInventory().replace(ItemId.ORANGE.id(), ItemId.DICED_ORANGE.id());
			}
		}
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.LIME.id())) {
			p.message("you can slice or dice the lime");
			int menu = showMenu(p,
				"slice lime",
				"dice lime");
			if (menu == 0) {
				p.message("you slice the lime");
				p.getCarriedItems().getInventory().replace(ItemId.LIME.id(), ItemId.LIME_SLICES.id());
			} else if (menu == 1) {
				p.message("you cut the lime into chunks");
				p.getCarriedItems().getInventory().replace(ItemId.LIME.id(), ItemId.LIME_CHUNKS.id());
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
			p.message("you can slice or dice the pineapple");
			int menu = showMenu(p,
				"slice pineapple",
				"dice pineapple");
			if (menu == 0) {
				p.message("you slice the pineapple into rings");
				p.getCarriedItems().getInventory().replace(pineappleId, ItemId.PINEAPPLE_RING.id());
				addItem(p, ItemId.PINEAPPLE_RING.id(), 3);
			} else if (menu == 1) {
				p.message("you cut the pineapple into chunks");
				p.getCarriedItems().getInventory().replace(pineappleId, ItemId.PINEAPPLE_CHUNKS.id());
			}
		}
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.LEMON.id())) {
			p.message("you can slice or dice the lemon");
			int menu = showMenu(p,
				"slice lemon",
				"dice lemon");
			if (menu == 0) {
				p.message("you slice the lemon");
				p.getCarriedItems().getInventory().replace(ItemId.LEMON.id(), ItemId.LEMON_SLICES.id());
			} else if (menu == 1) {
				p.message("you cut the lemon into chunks");
				p.getCarriedItems().getInventory().replace(ItemId.LEMON.id(), ItemId.DICED_LEMON.id());
			}
		}
		else if (compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.GRAPEFRUIT.id())) {
			p.message("you can slice or dice the grapefruit");
			int menu = showMenu(p,
				"slice grapefruit",
				"dice grapefruit");
			if (menu == 0) {
				p.message("you slice the grapefruit");
				p.getCarriedItems().getInventory().replace(ItemId.GRAPEFRUIT.id(), ItemId.GRAPEFRUIT_SLICES.id());
			} else if (menu == 1) {
				p.message("you cut the grapefruit into chunks");
				p.getCarriedItems().getInventory().replace(ItemId.GRAPEFRUIT.id(), ItemId.DICED_GRAPEFRUIT.id());
			}
		}
	}
}
