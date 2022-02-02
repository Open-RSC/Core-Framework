package com.openrsc.server.plugins.authentic.skills.cooking;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MathUtil;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class InvCooking implements UseInvTrigger {

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {

		// Cake
		if (item1.getCatalogId() == ItemId.CAKE_TIN.id() || item2.getCatalogId() == ItemId.CAKE_TIN.id()) {
			if (item1.getCatalogId() == ItemId.EGG.id() || item2.getCatalogId() == ItemId.EGG.id() ||
				item1.getCatalogId() == ItemId.MILK.id() || item2.getCatalogId() == ItemId.MILK.id() ||
				item1.getCatalogId() == ItemId.POT_OF_FLOUR.id() || item2.getCatalogId() == ItemId.POT_OF_FLOUR.id()) {
				if (player.getSkills().getLevel(Skill.COOKING.id()) < 40) {
					player.message("You need level 40 cooking to do this");
					return;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.EGG.id()) &&
					player.getCarriedItems().hasCatalogID(ItemId.MILK.id()) &&
					player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id()) &&
					player.getCarriedItems().hasCatalogID(ItemId.CAKE_TIN.id())) {
					if (player.getCarriedItems().remove(new Item(ItemId.EGG.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.MILK.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.CAKE_TIN.id())) > -1) {
						player.getCarriedItems().getInventory().add(new Item(ItemId.POT.id()));
						player.getCarriedItems().getInventory().add(new Item(ItemId.UNCOOKED_CAKE.id()));
						player.playerServerMessage(MessageType.QUEST, "You mix some milk, flour, and egg together into a cake mixture");
					}
				} else {
					if (!player.getCarriedItems().hasCatalogID(ItemId.EGG.id()))  // Egg
						player.playerServerMessage(MessageType.QUEST, "I also need an egg to make a cake");
					else if (!player.getCarriedItems().hasCatalogID(ItemId.MILK.id()))  // Milk
						player.playerServerMessage(MessageType.QUEST, "I also need some milk to make a cake");
					else if (!player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id())) // Flour
						player.playerServerMessage(MessageType.QUEST, "I also need some flour to make a cake");
				}
			}
			return;
		}

		// Wine
		if (item1.getCatalogId() == ItemId.GRAPES.id() && item2.getCatalogId() == ItemId.JUG_OF_WATER.id()
			|| item1.getCatalogId() == ItemId.JUG_OF_WATER.id() && item2.getCatalogId() == ItemId.GRAPES.id()) {
			if (player.getConfig().FERMENTED_WINE ||
				(player.getConfig().RESTRICT_ITEM_ID >= 0 && player.getConfig().RESTRICT_ITEM_ID < ItemId.CHEESE.id())) {
				player.getCarriedItems().remove(new Item(ItemId.JUG_OF_WATER.id()));
				player.getCarriedItems().remove(new Item(ItemId.GRAPES.id()));
				delay(5);
				player.playerServerMessage(MessageType.QUEST, "You add some grapes to the jug"); //unknown message
				player.getCarriedItems().getInventory().add(new Item(ItemId.BAD_OR_UNFERMENTED_WINE.id()));
				return;
			}
			if (player.getSkills().getLevel(Skill.COOKING.id()) < 35) {
				player.message("You need level 35 cooking to do this");
				return;
			}
			if (player.getCarriedItems().getInventory().contains(item1)
				&& player.getCarriedItems().getInventory().contains(item2)) {
				if (player.getSkills().getLevel(Skill.COOKING.id()) < 35) {
					player.playerServerMessage(MessageType.QUEST, "You need level 35 cooking to do this");
					return;
				}
				player.playerServerMessage(MessageType.QUEST, "You squeeze the grapes into the jug");
				player.getCarriedItems().remove(new Item(ItemId.JUG_OF_WATER.id()));
				player.getCarriedItems().remove(new Item(ItemId.GRAPES.id()));
				delay(5);
				if (Formulae.goodWine(player.getSkills().getLevel(Skill.COOKING.id()))) {
					player.playerServerMessage(MessageType.QUEST, "You make some nice wine");
					player.getCarriedItems().getInventory().add(new Item(ItemId.WINE.id()));
					player.incExp(Skill.COOKING.id(), 440, true);
				} else {
					player.playerServerMessage(MessageType.QUEST, "You accidentally make some bad wine");
					player.getCarriedItems().getInventory().add(new Item(ItemId.BAD_OR_UNFERMENTED_WINE.id()));
				}
			}

		// Dough
		} else if (isWaterItem(item1) && item2.getCatalogId() == ItemId.POT_OF_FLOUR.id()
				|| item1.getCatalogId() == ItemId.POT_OF_FLOUR.id() && isWaterItem(item2)) {
			int waterContainer = isWaterItem(item1) ? item1.getCatalogId() : item2.getCatalogId();

			player.message("What would you like to make?");
			ArrayList<String> options = new ArrayList<>();
			int maxItemId = player.getConfig().RESTRICT_ITEM_ID;

			options.add("Bread dough");
			if (MathUtil.maxUnsigned(maxItemId, ItemId.PASTRY_DOUGH.id()) == maxItemId) {
				options.add("Pastry dough");
			}
			if (MathUtil.maxUnsigned(maxItemId, ItemId.PIZZA_BASE.id()) == maxItemId) {
				options.add("Pizza dough");
			}
			if (MathUtil.maxUnsigned(maxItemId, ItemId.UNCOOKED_PITTA_BREAD.id()) == maxItemId) {
				options.add("Pitta dough");
			}

			String[] finalOptions = new String[options.size()];
			int option = multi(player, options.toArray(finalOptions));
			if (option < 0 || option > finalOptions.length) {
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
				if (!config().MEMBER_WORLD) {
					player.message("This feature is members only");
					return;
				}
			}

			if (player.getCarriedItems().remove(new Item(waterContainer)) != -1
				&& player.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id())) != -1 && productID > -1) {
				int emptyContainer = 0;

				if (waterContainer == ItemId.BUCKET_OF_WATER.id())
					emptyContainer = ItemId.BUCKET.id();
				else if (waterContainer == ItemId.JUG_OF_WATER.id())
					emptyContainer = ItemId.JUG.id();

				give(player, ItemId.POT.id(), 1);
				give(player, emptyContainer, 1);
				give(player, productID, 1);

				player.playerServerMessage(MessageType.QUEST, "You mix the water and flour to make some " + new Item(productID, 1).getDef(player.getWorld()).getName().toLowerCase());
			}
		} else if (isValidCooking(item1, item2)) {
			handleCombineCooking(player, item1, item2);
		}
	}

	private void handleCombineCooking(Player player, Item itemOne, Item itemTwo) {
		CombineCooking combine = null;

		// Pizza order matters!
		if ((itemOne.getCatalogId() == ItemId.PIZZA_BASE.id() || itemTwo.getCatalogId() == ItemId.PIZZA_BASE.id())
			&& (itemOne.getCatalogId() == ItemId.CHEESE.id() || itemTwo.getCatalogId() == ItemId.CHEESE.id())) {
			player.playerServerMessage(MessageType.QUEST, "I should add the tomato first");
			return;
		}

		for (CombineCooking c : CombineCooking.values()) {
			if (c.isValid(itemOne.getCatalogId(), itemTwo.getCatalogId())) {
				combine = c;
			}
		}
		if (player.getSkills().getLevel(Skill.COOKING.id()) < combine.requiredLevel) {
			player.playerServerMessage(MessageType.QUEST, "You need level " + combine.requiredLevel + " cooking to do this");
			return;
		}
		if (combine.resultItem == ItemId.TOMATO_MIXTURE.id() || combine.resultItem == ItemId.ONION_MIXTURE.id()
				|| combine.resultItem == ItemId.ONION_AND_TOMATO_MIXTURE.id() || combine.resultItem == ItemId.TASTY_UGTHANKI_KEBAB.id()) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.KNIFE.id())) { // No knife
				player.message("You need a knife in order to cut this");
				return;
			}
		}

		if (player.getCarriedItems().remove(new Item(combine.itemID)) != -1
			&& player.getCarriedItems().remove(new Item(combine.itemIDOther)) != -1) {

			// Check for tasty kebab failure
			if (combine.resultItem == ItemId.TASTY_UGTHANKI_KEBAB.id() && DataConversions.random(0, 31) < 1) {
				give(player, ItemId.UGTHANKI_KEBAB.id(), 1);
				player.playerServerMessage(MessageType.QUEST, "You make a dodgy looking ugthanki kebab");
				return;
			}

			if (combine.messages.length > 1)
				mes(combine.messages[0]);
			else
				player.message(combine.messages[0]);

			give(player, combine.resultItem, 1);
			player.incExp(Skill.COOKING.id(), combine.experience, true);

			if (combine.messages.length > 1)
				player.playerServerMessage(MessageType.QUEST, combine.messages[1]);
			if (combine.messages.length > 2)
				player.playerServerMessage(MessageType.QUEST, combine.messages[2]);
		}
	}

	private boolean isValidCooking(Item itemOne, Item itemTwo) {
		for (CombineCooking c : CombineCooking.values()) {
			if (c.isValid(itemOne.getCatalogId(), itemTwo.getCatalogId())) {
				return true;
			}
		}
		return false;
	}

	private boolean isWaterItem(Item item) {
		return item.getCatalogId() == ItemId.BUCKET_OF_WATER.id() || item.getCatalogId() == ItemId.JUG_OF_WATER.id();
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (item1.getCatalogId() == ItemId.CAKE_TIN.id() || item2.getCatalogId() == ItemId.CAKE_TIN.id())
			return true;
		if (item1.getCatalogId() == ItemId.GRAPES.id() && item2.getCatalogId() == ItemId.JUG_OF_WATER.id()
			|| item1.getCatalogId() == ItemId.JUG_OF_WATER.id() && item2.getCatalogId() == ItemId.GRAPES.id())
			return true;
		if (isWaterItem(item1) && item2.getCatalogId() == ItemId.POT_OF_FLOUR.id() || item1.getCatalogId() == ItemId.POT_OF_FLOUR.id() && isWaterItem(item2))
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
