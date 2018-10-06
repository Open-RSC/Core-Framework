package com.openrsc.server.plugins.skills;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

/**
 * Way better way to handle item on item cooking.
 * @author n0m
 *
 */
public class InvCooking implements InvUseOnItemListener, InvUseOnItemExecutiveListener {
	
	enum CombineCooking {
		TOMATO_MIX(320, 341, 1106, 0, 58, "You create a tomato mixture in the bowl"),
		TOMATO_ONION_MIX(241, 1106, 1108, 0, 58, "You add the onion to the tomato mixture"),
		ONION_MIX(241, 341, 1107, 0, 58, "You create an onion mixture in the bowl"),
		ONION_TOMATO_MIX(320, 1107, 1108, 0, 58, "You add the tomato to the onion mixture"),
		UGTHANKI_MIX(1103, 1108, 1109, 0, 58, "You cut up the Cooked Ugthanki Meat and put it into the mix"),
		TASTY_UGTHANKI_KEBAB(1109, 1105, 1102, 480, 58, "You add the mixture to your Pitta Bread to make a tasty kebab"),
		INCOMPLETE_STEW(348, 342, 343, 0, 25, "You cut up the meat and put it into the bowl"),
		UNCOOKED_STEW(132, 343, 345, 0, 25, "You cut up the potato and put it into the stew"),
		UNCOOKED_CURRY(707, 345, 708, 0, 60, "You add spice to the stew and make a curry"),
		PIE_SHELL(250, 251, 253, 0, 1, "You put the dough in the pie dish to make a pie shell"),
		UNCOOKED_APPLEPIE(253, 252, 254, 0, 30,  "You fill your pie with apples"),
		UNCOOKED_MEATPIE(253, 132, 255, 0, 20, "You fill your pie with meat"),
		UNCOOKED_REDBERRYPIE(253, 236, 256, 0, 10,  "You fill your pie with redberries"),
		CHOCOLATE_CAKE(337, 330, 332, 0, 50, "You make a chocolate cake!"),
		INCOMPLETE_PIZZA(321, 320, 323, 0, 35, "You add tomato to the pizza base"),
		UNCOOKED_PIZZA(323, 319, 324, 0, 35, "You add cheese on the incomplete pizza"),
		MEAT_PIZZA(132, 325, 326, 0, 45, "You create a meat pizza."),
		ANCHOVIE_PIZZA(325, 352, 327, 0, 55, "You create a anchovie pizza."),
		PINEAPPLERINGS_PIZZA(325, 749, 750, 0, 65, "You create a pineapple pizza."),
		PINEAPPLECHUNCKS_PIZZA(325, 862, 750, 0, 65, "You create a pineapple pizza."),
		FLOUR_POT(23, 135, 136, 0, 1, "You pour flour to the pot");
		
		private int itemID;
		private int itemIDOther;
		private int resultItem;
		private int experience;
		private int requiredLevel;
		private String[] messages;
		
		CombineCooking(int itemOne, int itemTwo, int resultItem, int experience, int level, String... messages) {
			this.itemID = itemOne;
			this.itemIDOther = itemTwo;
			this.resultItem = resultItem;
			this.experience = experience;
			this.requiredLevel = level;
			this.messages = messages;
		}
		
		public boolean isValid(int i, int is) {
			return itemID == i && itemIDOther == is || itemIDOther == i && itemID == is;
		}
	}
	
	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == 338 || item2.getID() == 338) {
			if (player.getInventory().remove(new Item(19)) > -1
					&& player.getInventory().remove(new Item(22)) > -1
					&& player.getInventory().remove(new Item(136)) > -1
					&& player.getInventory().remove(new Item(338)) > -1) {
				player.getInventory().add(new Item(135));
				player.getInventory().add(new Item(339));
				player.message("You mix some milk, flour, and egg together into a cake mixture");
				return;
			}
			else {
				if (!player.getInventory().hasItemId(19))  // Egg
					player.message("I also need an egg to make a cake");
				else if (!player.getInventory().hasItemId(22))  // Milk
					player.message("I also need some milk to make a cake");
				else if (!player.getInventory().hasItemId(136)) // Flour
					player.message("I also need some flour to make a cake");
				return;
			}
		}
		if (item1.getID() == 143 && item2.getID() == 141
				|| item1.getID() == 141 && item2.getID() == 143) {
			if (player.getSkills().getLevel(7) < 35) {
				player.message("You need level 35 cooking to do this");
				return;
			}
			if (player.getInventory().contains(item1)
					&& player.getInventory().contains(item2)) {
				int rand = DataConversions.random(0, 4);
				if (rand == 2) {
					player.incExp(7, 220, true);
					player.getInventory().add(new Item(180));
					player.message("You mix the grapes, and accidentally create Bad wine!");
				} else {
					player.incExp(7, 440, true);
					player.getInventory().add(new Item(142));
					player.message("You mix the grapes with the water and create wine!");
				}
				player.getInventory().remove(141, 1);
				player.getInventory().remove(143, 1);
			}
		}
		else if (isWaterItem(item1) && item2.getID() == 136 || item1.getID() == 136 && isWaterItem(item2)) {
			int waterContainer = isWaterItem(item1) ? item1.getID() : item2.getID();
			
			player.message("What would you like to make?");
			int option = showMenu(player, "Bread dough", "Pastry dough", "Pizza dough", "Pitta dough");
			if (player.isBusy() || option < 0 || option > 3) {
				return;
			}
			int productID = -1;
			if(option == 0) {
				productID = 137;
			}
			else if(option == 1) {
				productID = 250;
			}
			else if(option == 2) {
				productID = 321; 
			} 
			else if(option == 3) {
				productID = 1104;
			}
			if (removeItem(player, new Item(waterContainer), new Item(136)) && productID > -1) {
				int emptyContainer = 0;
				
				if(waterContainer== 50)
					emptyContainer = 21;
				else if(waterContainer == 141) 
					emptyContainer = 140;
				
				addItem(player, 135, 1);
				addItem(player, emptyContainer, 1);
				addItem(player, productID, 1);
				
				player.message("You mix the water and flour to make some " + new Item(productID, 1).getDef().getName().toLowerCase());
			}
		} else if(isValidCooking(item1, item2)) {
			handleCombineCooking(player, item1, item2);
		}
		return;
	}
	
	public void handleCombineCooking(Player p, Item itemOne, Item itemTwo) {
		CombineCooking combine = null;

		// Pizza order matters!
		if ((itemOne.getID() == 321 || itemTwo.getID() == 321)
			&& (itemOne.getID() == 319 || itemTwo.getID() == 319)) {
			p.message("I should add the tomato first");
			return;
		}

		for(CombineCooking c : CombineCooking.values()) {
			if(c.isValid(itemOne.getID(), itemTwo.getID())) {
				combine = c;
			}
		}
		if (p.getSkills().getLevel(7) < combine.requiredLevel) {
			p.message("You need level " + combine.requiredLevel + " cooking to do this");
			return;
		}
		if(removeItem(p, combine.itemID, 1) && removeItem(p, combine.itemIDOther, 1)) {
			if(combine.messages.length > 1)
				message(p, combine.messages[0]);
			else
				p.message(combine.messages[0]);
			
			addItem(p, combine.resultItem, 1);
			p.incExp(7, combine.experience, true);
			
			if(combine.messages.length > 1)
				p.message(combine.messages[1]);
		}
	}
	
	public boolean isValidCooking(Item itemOne, Item itemTwo) {
		for(CombineCooking c : CombineCooking.values()) {
			if(c.isValid(itemOne.getID(), itemTwo.getID())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isWaterItem(Item item) {
		return item.getID() == 50 || item.getID() == 141;
	}
	
	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == 338 || item2.getID() == 338)
			return true;
		if (item1.getID() == 143 && item2.getID() == 141
				|| item1.getID() == 141 && item2.getID() == 143)
			return true;
		if (isWaterItem(item1) && item2.getID() == 136 || item1.getID() == 136 && isWaterItem(item2))
			return true;
		
		return isValidCooking(item1, item2);
	}
}
