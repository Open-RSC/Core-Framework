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
		INCOMPLETE_STEW(348, 342, 343, 0, 25, "You start to create a stew"),
		UNCOOKED_STEW(132, 343, 345, 0, 25, "Your stew is now ready, but uncooked"),
		PIE_SHELL(250, 251, 253, 0, 1, "You add the pastry dough in the dish"),
		UNCOOKED_APPLEPIE(253, 252, 254, 0, 30,  "You create an uncoooked pie"),
		UNCOOKED_MEATPIE(253, 132, 255, 0, 20, "You create an uncoooked pie"),
		UNCOOKED_REDBERRYPIE(253, 236, 256, 0, 10,  "You create an uncoooked pie"),
		CHOCOLATE_CAKE(337, 330, 332, 0, 50, "You add chocolate to the cake"),
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
				player.message("You create an uncooked cake");
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
					player.incExp(7, 55, true);
					player.getInventory().add(new Item(180));
					player.message("You mix the grapes, and accidentally create Bad wine!");
				} else {
					player.incExp(7, 110, true);
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
