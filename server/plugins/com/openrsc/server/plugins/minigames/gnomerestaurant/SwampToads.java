package com.openrsc.server.plugins.minigames.gnomerestaurant;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.Functions.mes;

import com.openrsc.server.constants.ItemId;

public class SwampToads implements TakeObjTrigger, OpInvTrigger {

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return item.getCatalogId() == ItemId.SWAMP_TOAD.id();
	}

	@Override
	public void onOpInv(Item item, Player p, String command) {
		if (item.getCatalogId() == ItemId.SWAMP_TOAD.id()) {
			Functions.mes(p, 1900, "you pull the legs off the toad");
			p.message("poor toad..at least they'll grow back");
			p.getCarriedItems().getInventory().replace(item.getCatalogId(), ItemId.TOAD_LEGS.id());
		}
	}

	@Override
	public boolean blockTakeObj(Player p, GroundItem i) {
		return i.getID() == ItemId.SWAMP_TOAD.id();
	}

	@Override
	public void onTakeObj(Player p, GroundItem i) {
		if (i.getID() == ItemId.SWAMP_TOAD.id()) {
			p.message("you pick up the swamp toad");
			if (DataConversions.random(0, 10) >= 3) {
				Functions.mes(p, 1900, "but it jumps out of your hands..");
				p.message("..slippery little blighters");
			} else {
				i.remove();
				give(p, ItemId.SWAMP_TOAD.id(), 1);
				p.message("you just manage to hold onto it");
			}
		}
	}
}
