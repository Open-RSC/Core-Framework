package com.openrsc.server.plugins.minigames.gnomerestaurant;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.message;

import com.openrsc.server.constants.ItemId;

public class SwampToads implements PickupListener, PickupExecutiveListener, InvActionListener, InvActionExecutiveListener {

	@Override
	public boolean blockInvAction(Item item, Player p, String command) {
		return item.getID() == ItemId.SWAMP_TOAD.id();
	}

	@Override
	public void onInvAction(Item item, Player p, String command) {
		if (item.getID() == ItemId.SWAMP_TOAD.id()) {
			message(p, 1900, "you pull the legs off the toad");
			p.message("poor toad..at least they'll grow back");
			p.getInventory().replace(item.getID(), ItemId.TOAD_LEGS.id());
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		return i.getID() == ItemId.SWAMP_TOAD.id();
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == ItemId.SWAMP_TOAD.id()) {
			p.message("you pick up the swamp toad");
			if (DataConversions.random(0, 10) >= 3) {
				message(p, 1900, "but it jumps out of your hands..");
				p.message("..slippery little blighters");
			} else {
				i.remove();
				addItem(p, ItemId.SWAMP_TOAD.id(), 1);
				p.message("you just manage to hold onto it");
			}
		}
	}
}
