package com.openrsc.server.plugins.skills;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

/**
 * Way better way to handle item on item cooking.
 *
 * @author n0m
 */
public class InvCooking implements InvUseOnItemListener, InvUseOnItemExecutiveListener {

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == ItemId.CAKE_TIN.id() || item2.getID() == ItemId.CAKE_TIN.id()) {
			if (player.getInventory().remove(new Item(ItemId.EGG.id())) > -1
				&& player.getInventory().remove(new Item(ItemId.MILK.id())) > -1
				&& player.getInventory().remove(new Item(ItemId.POT_OF_FLOUR.id())) > -1
				&& player.getInventory().remove(new Item(ItemId.CAKE_TIN.id())) > -1) {
				player.getInventory().add(new Item(ItemId.POT.id()));
				player.getInventory().add(new Item(ItemId.UNCOOKED_CAKE.id()));
				player.message("You mix some milk, flour, and egg together into a cake mixture");
				return;
			} else {
				if (!player.getInventory().hasItemId(ItemId.EGG.id()))  // Egg
					player.message("I also need an egg to make a cake");
				else if (!player.getInventory().hasItemId(ItemId.MILK.id()))  // Milk
					player.message("I also need some milk to make a cake");
				else if (!player.getInventory().hasItemId(ItemId.POT_OF_FLOUR.id())) // Flour
					player.message("I also need some flour to make a cake");
				return;
			}
		}
		if (item1.getID() == ItemId.GRAPES.id() && item2.getID() == ItemId.JUG_OF_WATER.id()
			|| item1.getID() == ItemId.JUG_OF_WATER.id() && item2.getID() == ItemId.GRAPES.id()) {
			if (player.getSkills().getLevel(Skills.COOKING) < 35) {
				player.message("You need level 35 cooking to do this");
				return;
			}
			if (player.getInventory().contains(item1)
				&& player.getInventory().contains(item2)) {
				player.message("You squeeze the grapes into the jug");
				player.getInventory().remove(ItemId.JUG_OF_WATER.id(), 1);
				player.getInventory().remove(ItemId.GRAPES.id(), 1);

				player.setBatchEvent(new BatchEvent(player, 3000, "Cook Wine", 1, false) {
					@Override
					public void action() {
						if (Formulae.goodWine(owner.getSkills().getLevel(Skills.COOKING))) {
							owner.message("You make some nice wine");
							owner.getInventory().add(new Item(ItemId.WINE.id()));
							owner.incExp(Skills.COOKING, 440, true);
						} else {
							owner.message("You accidentally make some bad wine");
							owner.getInventory().add(new Item(ItemId.BAD_WINE.id()));
						}
					}
				});
			}
		} else if (isWaterItem(item1) && item2.getID() == ItemId.POT_OF_FLOUR.id()
				|| item1.getID() == ItemId.POT_OF_FLOUR.id() && isWaterItem(item2)) {
			int waterContainer = isWaterItem(item1) ? item1.getID() : item2.getID();

			player.message("What would you like to make?");
			int option = showMenu(player, "Bread dough", "Pastry dough", "Pizza dough", "Pitta dough");
			if (player.isBusy() || option < 0 || option > 3) {
				return;
			}
			int productID = -1;
			if (option == 0) {
				productID = ItemId.BREAD_DOUGH.id();
			} else if (option == 1) {
				productID = ItemId.PASTRY_DOUGH.id();
			} else if (option == 2) {
				productID = ItemId.PIZZA_BASE.id();
			} else if (option == 3) {
				productID = ItemId.UNCOOKED_PITTA_BREAD.id();
			}
			if (removeItem(player, new Item(waterContainer), new Item(ItemId.POT_OF_FLOUR.id())) && productID > -1) {
				int emptyContainer = 0;

				if (waterContainer == ItemId.BUCKET_OF_WATER.id())
					emptyContainer = ItemId.BUCKET.id();
				else if (waterContainer == ItemId.JUG_OF_WATER.id())
					emptyContainer = ItemId.JUG.id();

				addItem(player, ItemId.POT.id(), 1);
				addItem(player, emptyContainer, 1);
				addItem(player, productID, 1);

				player.message("You mix the water and flour to make some " + new Item(productID, 1).getDef().getName().toLowerCase());
			}
		} else if (isValidCooking(item1, item2)) {
			handleCombineCooking(player, item1, item2);
		}
	}

	private void handleCombineCooking(Player p, Item itemOne, Item itemTwo) {
		CombineCooking combine = null;

		// Pizza order matters!
		if ((itemOne.getID() == ItemId.PIZZA_BASE.id() || itemTwo.getID() == ItemId.PIZZA_BASE.id())
			&& (itemOne.getID() == ItemId.CHEESE.id() || itemTwo.getID() == ItemId.CHEESE.id())) {
			p.message("I should add the tomato first");
			return;
		}

		for (CombineCooking c : CombineCooking.values()) {
			if (c.isValid(itemOne.getID(), itemTwo.getID())) {
				combine = c;
			}
		}
		if (p.getSkills().getLevel(Skills.COOKING) < combine.requiredLevel) {
			p.message("You need level " + combine.requiredLevel + " cooking to do this");
			return;
		}
		if (combine.resultItem == ItemId.TOMATO_MIXTURE.id() || combine.resultItem == ItemId.ONION_MIXTURE.id()
				|| combine.resultItem == ItemId.ONION_AND_TOMATO_MIXTURE.id() || combine.resultItem == ItemId.TASTY_UGTHANKI_KEBAB.id()) {
			if (!p.getInventory().hasItemId(ItemId.KNIFE.id())) { // No knife
				p.message("You need a knife in order to cut this");
				return;
			}
		}

		if (removeItem(p, combine.itemID, 1) && removeItem(p, combine.itemIDOther, 1)) {

			// Check for tasty kebab failure
			if (combine.resultItem == ItemId.TASTY_UGTHANKI_KEBAB.id() && DataConversions.random(0, 31) < 1) {
				addItem(p, ItemId.UGTHANKI_KEBAB.id(), 1);
				p.message("You make a dodgy looking ugthanki kebab");
				return;
			}

			if (combine.messages.length > 1)
				message(p, combine.messages[0]);
			else
				p.message(combine.messages[0]);

			addItem(p, combine.resultItem, 1);
			p.incExp(Skills.COOKING, combine.experience, true);

			if (combine.messages.length > 1)
				p.message(combine.messages[1]);
			if (combine.messages.length > 2)
				p.message(combine.messages[2]);
		}
	}

	private boolean isValidCooking(Item itemOne, Item itemTwo) {
		for (CombineCooking c : CombineCooking.values()) {
			if (c.isValid(itemOne.getID(), itemTwo.getID())) {
				return true;
			}
		}
		return false;
	}

	private boolean isWaterItem(Item item) {
		return item.getID() == ItemId.BUCKET_OF_WATER.id() || item.getID() == ItemId.JUG_OF_WATER.id();
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		if (item1.getID() == ItemId.CAKE_TIN.id() || item2.getID() == ItemId.CAKE_TIN.id())
			return true;
		if (item1.getID() == ItemId.GRAPES.id() && item2.getID() == ItemId.JUG_OF_WATER.id()
			|| item1.getID() == ItemId.JUG_OF_WATER.id() && item2.getID() == ItemId.GRAPES.id())
			return true;
		if (isWaterItem(item1) && item2.getID() == ItemId.POT_OF_FLOUR.id() || item1.getID() == ItemId.POT_OF_FLOUR.id() && isWaterItem(item2))
			return true;

		return isValidCooking(item1, item2);
	}

	enum CombineCooking {
		OOMLIE_MEAT_PARCEL(ItemId.RAW_OOMLIE_MEAT.id(), ItemId.PALM_TREE_LEAF.id(), ItemId.RAW_OOMLIE_MEAT_PARCEL.id(), 40, 50,
				"You carefully construct a small parcel out of the palm leaf.", "You place the delicate Oomlie meat inside.", "The palm leaf should protect the meat from being burnt."),
		TOMATO_MIX(ItemId.TOMATO.id(), ItemId.BOWL.id(), ItemId.TOMATO_MIXTURE.id(), 0, 58,
				"You cut the tomato into the bowl"),
		TOMATO_ONION_MIX(ItemId.ONION.id(), ItemId.TOMATO_MIXTURE.id(), ItemId.ONION_AND_TOMATO_MIXTURE.id(), 0, 58,
				"You cut the onion into the tomato mixture"),
		ONION_MIX(ItemId.ONION.id(), ItemId.BOWL.id(), ItemId.ONION_MIXTURE.id(), 0, 58,
				"You cut the onion into the bowl"),
		ONION_TOMATO_MIX(ItemId.TOMATO.id(), ItemId.ONION_MIXTURE.id(), ItemId.ONION_AND_TOMATO_MIXTURE.id(), 0, 58,
				"You cut the tomato into the onion mixture"),
		UGTHANKI_MIX(ItemId.COOKED_UGTHANKI_MEAT.id(), ItemId.ONION_AND_TOMATO_MIXTURE.id(), ItemId.ONION_AND_TOMATO_AND_UGTHANKI_MIX.id(), 0, 58,
				"You cut the ugthanki meat into the tomato and onion mixture"),
		TASTY_UGTHANKI_KEBAB(ItemId.ONION_AND_TOMATO_AND_UGTHANKI_MIX.id(), ItemId.PITTA_BREAD.id(), ItemId.TASTY_UGTHANKI_KEBAB.id(), 480, 58,
				"You make a delicious ugthanki kebab"),
		INCOMPLETE_STEW(ItemId.POTATO.id(), ItemId.BOWL_OF_WATER.id(), ItemId.INCOMPLETE_STEW_POTATO.id(), 0, 25,
				"You cut up the potato and put it into the bowl"),
		INCOMPLETE_STEW_MEAT(ItemId.COOKEDMEAT.id(), ItemId.BOWL_OF_WATER.id(), ItemId.INCOMPLETE_STEW_MEAT.id(), 0, 25,
				"You cut up the meat and put it into the bowl"),
		UNCOOKED_STEW_MEAT(ItemId.POTATO.id(), ItemId.INCOMPLETE_STEW_MEAT.id(), ItemId.UNCOOKED_STEW.id(), 0, 25,
				"You cut up the potato and put it into the stew"),
		UNCOOKED_STEW(ItemId.COOKEDMEAT.id(), ItemId.INCOMPLETE_STEW_POTATO.id(), ItemId.UNCOOKED_STEW.id(), 0, 25,
				"You cut up the meat and put it into the stew"),
		UNCOOKED_CURRY(ItemId.SPICE.id(), ItemId.UNCOOKED_STEW.id(), ItemId.UNCOOKED_CURRY.id(), 0, 60,
				"You add spice to the stew and make a curry"),
		PIE_SHELL(ItemId.PASTRY_DOUGH.id(), ItemId.PIE_DISH.id(), ItemId.PIE_SHELL.id(), 0, 1,
				"You put the dough in the pie dish to make a pie shell"),
		UNCOOKED_APPLEPIE(ItemId.PIE_SHELL.id(), ItemId.COOKING_APPLE.id(), ItemId.UNCOOKED_APPLE_PIE.id(), 0, 30,
				"You fill your pie with apples"),
		UNCOOKED_MEATPIE(ItemId.PIE_SHELL.id(), ItemId.COOKEDMEAT.id(), ItemId.UNCOOKED_MEAT_PIE.id(), 0, 20,
				"You fill your pie with meat"),
		UNCOOKED_REDBERRYPIE(ItemId.PIE_SHELL.id(), ItemId.REDBERRIES.id(), ItemId.UNCOOKED_REDBERRY_PIE.id(), 0, 10,
				"You fill your pie with redberries"),
		CHOCOLATE_CAKE(ItemId.CHOCOLATE_BAR.id(), ItemId.CAKE.id(), ItemId.CHOCOLATE_CAKE.id(), 0, 50,
				"You make a chocolate cake!"),
		INCOMPLETE_PIZZA(ItemId.PIZZA_BASE.id(), ItemId.TOMATO.id(), ItemId.INCOMPLETE_PIZZA.id(), 0, 35,
				"You add tomato to the pizza"),
		UNCOOKED_PIZZA(ItemId.INCOMPLETE_PIZZA.id(), ItemId.CHEESE.id(), ItemId.UNCOOKED_PIZZA.id(), 0, 35,
				"You add cheese to the pizza"),
		MEAT_PIZZA(ItemId.COOKEDMEAT.id(), ItemId.PLAIN_PIZZA.id(), ItemId.MEAT_PIZZA.id(), 0, 45,
				"You add the meat to the pizza"),
		ANCHOVIE_PIZZA(ItemId.PLAIN_PIZZA.id(), ItemId.ANCHOVIES.id(), ItemId.ANCHOVIE_PIZZA.id(), 0, 55,
				"You add the anchovies to the pizza"),
		PINEAPPLERINGS_PIZZA(ItemId.PLAIN_PIZZA.id(), ItemId.PINEAPPLE_RING.id(), ItemId.PINEAPPLE_PIZZA.id(), 0, 65,
				"You add the pineapple to the pizza"),
		PINEAPPLECHUNCKS_PIZZA(ItemId.PLAIN_PIZZA.id(), ItemId.PINEAPPLE_CHUNKS.id(), ItemId.PINEAPPLE_PIZZA.id(), 0, 65,
				"You add the pineapple to the pizza"),
		FLOUR_POT(ItemId.FLOUR.id(), ItemId.POT.id(), ItemId.POT_OF_FLOUR.id(), 0, 1, "You put the flour in the pot");

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
}
