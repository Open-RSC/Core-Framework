package com.openrsc.server.plugins.authentic.minigames.gnomerestaurant;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;

public class SwampToads implements TakeObjTrigger, OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.SWAMP_TOAD.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.SWAMP_TOAD.id()) {
			mes("you pull the legs off the toad");
			delay(3);
			player.message("poor toad..at least they'll grow back");
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.TOAD_LEGS.id()));
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.SWAMP_TOAD.id();
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.SWAMP_TOAD.id()) {
			player.message("you pick up the swamp toad");
			if (DataConversions.random(0, 10) >= 3) {
				mes("but it jumps out of your hands..");
				delay(3);
				player.message("..slippery little blighters");
			} else {
				i.remove();
				give(player, ItemId.SWAMP_TOAD.id(), 1);
				player.message("you just manage to hold onto it");
			}
		}
	}
}
