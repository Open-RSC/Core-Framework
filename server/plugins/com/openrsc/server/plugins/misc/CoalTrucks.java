package com.openrsc.server.plugins.misc;

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
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == COAL_TRUCK) {
			if (p.getCache().hasKey("coal_truck") && p.getCache().getInt("coal_truck") > 0) {
				p.setBusyTimer(500);
				int coalLeft = p.getCache().getInt("coal_truck");
				p.playerServerMessage(MessageType.QUEST, "You remove a piece of coal from the truck");
				give(p, ItemId.COAL.id(), 1);
				p.getCache().set("coal_truck", coalLeft - 1);
			} else {
				p.playerServerMessage(MessageType.QUEST, "there is no coal left in the truck\"");
			}
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == COAL_TRUCK;
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return obj.getID() == COAL_TRUCK && item.getCatalogId() == ItemId.COAL.id();
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == COAL_TRUCK && item.getCatalogId() == ItemId.COAL.id()) {
			p.setBusy(true);
			int coalAmount = p.getCarriedItems().getInventory().countId(ItemId.COAL.id());
			for (int i = 0; i < coalAmount; i++) {
				if (p.getCache().hasKey("coal_truck")) {
					if (p.getCache().getInt("coal_truck") >= 120) {
						p.message("The coal truck is full");
						break;
					}
					int coalDeposited = p.getCache().getInt("coal_truck");
					p.getCache().set("coal_truck", coalDeposited + 1);
				} else {
					p.getCache().set("coal_truck", coalAmount);
				}
				p.playerServerMessage(MessageType.QUEST, "You put a piece of coal in the truck");
				remove(p, ItemId.COAL.id(), 1);
				delay(50);
			}
			p.setBusy(false);
		}
	}
}
