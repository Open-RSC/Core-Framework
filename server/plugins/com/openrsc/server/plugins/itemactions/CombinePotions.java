package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;

public class CombinePotions implements InvUseOnItemListener, InvUseOnItemExecutiveListener {

	int[][] combinePotions = { 
			{ 475, 476, 474 }, // Attack potions.
			{ 478, 479, 477 }, // Stat restore potions
			{ 481, 482, 480 }, // Defense potions
			{ 484, 485, 483 }, // Prayer potion
			{ 487, 488, 486 }, // SAP
			{ 490, 491, 489 }, // Fishing potion
			{ 493, 494, 492 }, // SSP
			{ 496, 497, 495 }, // SDP
			{ 499, 500, 498 }, // Range pot
			{ 567, 568, 566 }, // Cure pot
			{ 964, 965, 963 } // Zammy pot
	};

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {

		// No Decanting without the config set to true!
		if (!Constants.GameServer.WANT_DECANTING) {
			p.message("Nothing interesting happens");
			return;
		}

		/** Regular Strength Potions **/
		// 1 dose on 2 dose str = 3 dose
		if (item1.getID() == 224 && item2.getID() == 223 || item1.getID() == 223 && item2.getID() == 224) {
			if (p.getInventory().remove(new Item(224)) > -1 && p.getInventory().remove(new Item(223)) > -1) {
				addItem(p, 222, 1);
				p.message("You combine 2 doses of " + item1.getDef().getName().toLowerCase() + " with 1 dose of " + item2.getDef().getName().toLowerCase());
				addItem(p, 465, 1); // give 1 empty vial.
				return;
			}
		}
		// 1 dose on 3 dose = 4 dose
		else if (item1.getID() == 224 && item2.getID() == 222 || item1.getID() == 222 && item2.getID() == 224) {
			if (p.getInventory().remove(new Item(224)) > -1 && p.getInventory().remove(new Item(222)) > -1) {
				addItem(p, 221, 1);
				p.message("You combine 3 doses of " + item1.getDef().getName().toLowerCase() + " with 1 dose of " + item2.getDef().getName().toLowerCase());
				addItem(p, 465, 1); // give 1 empty vial.
				return;
			}
		}
		// 2 dose on 2 dose = 4 dose
		else if (item1.getID() == 223 && item2.getID() == 223) {
			if (p.getInventory().remove(new Item(223)) > -1 && p.getInventory().remove(new Item(223)) > -1) {
				addItem(p, 221, 1);
				p.message("You combine two 2 doses of " + item1.getDef().getName().toLowerCase());
				addItem(p, 465, 1); // give 1 empty vial.
				return;
			}
		}
		// 1 dose on 1 dose = 2 dose
		else if (item1.getID() == 224 && item2.getID() == 224) {
			if (p.getInventory().remove(new Item(224)) > -1 && p.getInventory().remove(new Item(224)) > -1) {
				addItem(p, 223, 1);
				p.message("You combine 1 dose of " + item1.getDef().getName().toLowerCase() + " with 1 dose of " + item2.getDef().getName().toLowerCase());
				addItem(p, 465, 1); // give 1 empty vial.
				return;
			}
		} 
		// 3 dose on 3 dose = 6 dose (one 4 dose full pot, one 2 dose pot)
		else if (item1.getID() == 222 && item2.getID() == 222) {
			if (p.getInventory().remove(new Item(222)) > -1 && p.getInventory().remove(new Item(222)) > -1) {
				addItem(p, 221, 1); // 4 dose
				addItem(p, 223, 1); // 2 dose
				p.message("You combine two 3 doses of " + item1.getDef().getName().toLowerCase());
				return;
			}
		} 
		/** Rest of the potions in the game **/
		else {
			for (int i = 0; i < combinePotions.length; i++) {
				/** 1 dose with 2 dose. **/
				if ((item1.getID() == combinePotions[i][0] && item2.getID() == combinePotions[i][1]) || (item2.getID() == combinePotions[i][0] && item1.getID() == combinePotions[i][1])) {
					if (p.getInventory().remove(new Item(combinePotions[i][0])) > -1 && p.getInventory().remove(new Item(combinePotions[i][1])) > -1) {
						p.message("You combine 2 doses of " + item1.getDef().getName().toLowerCase() + " with 1 dose of " + item2.getDef().getName().toLowerCase());
						p.getInventory().add(new Item(combinePotions[i][2])); // 1 full pot
						p.message("to a full 3 doses of " + item1.getDef().getName().toLowerCase());
						addItem(p, 465, 1); // give 1 empty vial.
						p.message("you get an empty vial over");
						return;
					}
				}
				/** 1 dose with 1 dose. **/
				else if (item1.getID() == combinePotions[i][1] && item2.getID() == combinePotions[i][1]) {
					if (p.getInventory().remove(new Item(combinePotions[i][1])) > -1 && p.getInventory().remove(new Item(combinePotions[i][1])) > -1) {
						p.message("You combine two 1 dose of " + item1.getDef().getName().toLowerCase());
						p.getInventory().add(new Item(combinePotions[i][0])); // 2 dose pot
						p.message("to 2 doses of " + item1.getDef().getName().toLowerCase());
						addItem(p, 465, 1); // give 1 empty vial.
						p.message("you get an empty vial over");
						return;
					} 
				}
				/** 2 dose with 2 dose. **/
				else if (item1.getID() == combinePotions[i][0] && item2.getID() == combinePotions[i][0]) {
					if (p.getInventory().remove(new Item(combinePotions[i][0])) > -1 && p.getInventory().remove(new Item(combinePotions[i][0])) > -1) {
						p.message("You combine two 2 doses of " + item1.getDef().getName().toLowerCase());
						p.getInventory().add(new Item(combinePotions[i][2])); // 1 full pot
						p.getInventory().add(new Item(combinePotions[i][1])); // 1 dose pot
						p.message("to a full 3 doses of " + item1.getDef().getName().toLowerCase() + " and 1 dose of " + item1.getDef().getName().toLowerCase());
						return;
					}
				}
			}
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		// 1 dose on 2 dose str = 3 dose
		if (item1.getID() == 224 && item2.getID() == 223 || item1.getID() == 223 && item2.getID() == 224) {
			return true;
		}
		// 1 dose on 3 dose = 4 dose
		if (item1.getID() == 224 && item2.getID() == 222 || item1.getID() == 222 && item2.getID() == 224) {
			return true;
		}
		// 2 dose on 2 dose = 4 dose
		if (item1.getID() == 223 && item2.getID() == 223) {
			return true;
		}
		// 1 dose on 1 dose = 2 dose
		if (item1.getID() == 224 && item2.getID() == 224) {
			return true;
		} 
		if (item1.getID() == 222 && item2.getID() == 222) {
			return true;
		}
		for (int i = 0; i < combinePotions.length; i++) {
			if ((item1.getID() == combinePotions[i][0] && item2.getID() == combinePotions[i][1]) || (item2.getID() == combinePotions[i][0] && item1.getID() == combinePotions[i][1])) {
				return true;
			}
			if (item1.getID() == combinePotions[i][1] && item2.getID() == combinePotions[i][1]) {
				return true;
			}
			if (item1.getID() == combinePotions[i][0] && item2.getID() == combinePotions[i][0]) {
				return true;
			}
		}
		return false;
	}
}
