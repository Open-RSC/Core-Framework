package com.openrsc.server.plugins.misc;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.showBubble;

import java.util.ArrayList;

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
			ArrayList<Item> loot = new ArrayList<Item>();
			loot.add(new Item(542, 1)); // Dragonstone
			int percent = DataConversions.random(0, 10000);
			if (percent < 26) {
				loot.add(new Item(402, 1));
			}
			else if (percent < 132) {
				loot.add(new Item(127, 1));
			}
			else if (percent < 407) {
				loot.add(new Item(517, 30));
			}
			else if (percent < 733) {
					loot.add(new Item(526, 1));
					loot.add(new Item(10, 750));
			}
			else if (percent < 1084) {
				loot.add(new Item(408, 3));
			}
			else if (percent < 1451) {
				loot.add(new Item(527, 1));
				loot.add(new Item(10, 750));
			}
			else if (percent < 1874) {
				loot.add(new Item(162, 2));
				loot.add(new Item(161, 2));
			}
			else if (percent < 2529) {
				loot.add(new Item(518, 20));
			}
			else if (percent < 3302) {
				loot.add(new Item(33, 50));
				loot.add(new Item(32, 50));
				loot.add(new Item(34, 50));
				loot.add(new Item(31, 50));
				loot.add(new Item(35, 50));
				loot.add(new Item(36, 50));
				loot.add(new Item(41, 10));
				loot.add(new Item(46, 10));
				loot.add(new Item(40, 10));
				loot.add(new Item(42, 10));
				loot.add(new Item(38, 10));
			}
			else if (percent < 4359) {
				loot.add(new Item(536, 1));
				loot.add(new Item(10, 1000));
			}
			else if (percent < 8328) {
				loot.add(new Item(179, 1));
				loot.add(new Item(10, 2000));
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
