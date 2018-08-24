package com.openrsc.server.plugins.misc;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.showBubble;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

public class CrystalChest implements InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener {

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		// TODO Auto-generated method stub
		return item.getID() == 525 && obj.getID() == 248;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		showBubble(player, item);
		message(player, "You use the key to unlock the chest");
		if (player.getInventory().remove(item) > -1) {
			player.getInventory().add(new Item(542, 1));
			Item[] loot = null;
			int percent = DataConversions.random(0, 100);
			if (percent <= 100) {
				loot = new Item[] { new Item(179, 1), new Item(10, 2000) };
			}
			if (percent < 80) {
				loot = new Item[] { new Item(179, 1), new Item(10, 2000) };
			}
			if (percent < 60) {
				loot = new Item[] { new Item(179, 1), new Item(536, 1),
						new Item(10, 1000) };
			}
			if (percent < 40) {
				loot = new Item[] { new Item(33, 50), new Item(32, 50),
						new Item(34, 50), new Item(31, 50), new Item(35, 50),
						new Item(36, 50), new Item(41, 10), new Item(46, 10),
						new Item(40, 10), new Item(42, 10), new Item(38, 10) };
			}
			if (percent < 18) {
				loot = new Item[] { new Item(518, 20) };
			}
			if (percent < 15) {
				loot = new Item[] { new Item(162, 2), new Item(161, 2) };
			}
			if (percent < 12) {
				loot = new Item[] { new Item(526, 1), new Item(10, 750) };
			}
			if (percent < 10) {
				if (DataConversions.random(0, 1) == 1) {
					loot = new Item[] { new Item(526, 1), new Item(10, 750) };
				} else
					loot = new Item[] { new Item(527, 1), new Item(10, 750) };
			}
			if (percent < 5) {
				loot = new Item[] { new Item(408, 3) };
			}
			if (percent < 5) {
				loot = new Item[] { new Item(517, 30) };
			}
			if (percent < 2) {
				loot = new Item[] { new Item(127, 1) };
			}
			if (percent < 1) {
				loot = new Item[] { new Item(402, 1) };
			}
			for (Item i : loot) {
				if (i.getAmount() > 1 && !i.getDef().isStackable()) {
					for (int x = 0; x < i.getAmount(); x++) {
						player.getInventory().add(new Item(i.getID(), 1));
					}
				} else {
					player.getInventory().add(i);
				}
			}
		}
	}

}
