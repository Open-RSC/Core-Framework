package com.openrsc.server.plugins.custom.skills.cooking;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class NewCookingRecipes implements OpInvTrigger, UseInvTrigger {

	@SuppressWarnings("DefaultLocale")
	protected String[] recipeStrings = {

		// Seaweed Soup Mixture
		String.format("%d-%d-%d-%d-%d",
			ItemId.GIANT_CARP.id(), // Giant Carp
			ItemId.EDIBLE_SEAWEED.id(), // Some edible Seaweed
			ItemId.GARLIC.id(), // Some Garlic
			ItemId.POTATO.id(), // A potato
			ItemId.SPICE.id()), // Some Spice
	};

	private boolean canMix(Item itemOne, Item itemTwo) {
		if (itemOne.getCatalogId() == ItemId.PIE_SHELL.id() || itemTwo.getCatalogId() == ItemId.PIE_SHELL.id()) {
			if (itemOne.getCatalogId() == ItemId.LILYS_PUMPKIN.id() || itemTwo.getCatalogId() == ItemId.LILYS_PUMPKIN.id()) {
				return true;
			}
			if (itemOne.getCatalogId() == ItemId.EGG.id() || itemTwo.getCatalogId() == ItemId.EGG.id() ||
				itemOne.getCatalogId() == ItemId.MILK.id() || itemTwo.getCatalogId() == ItemId.MILK.id() ||
				itemOne.getCatalogId() == ItemId.PUMPKIN.id() || itemTwo.getCatalogId() == ItemId.PUMPKIN.id() ||
				itemOne.getCatalogId() == ItemId.WHITE_PUMPKIN.id() || itemTwo.getCatalogId() == ItemId.WHITE_PUMPKIN.id()) {
				return true;
			}
		}
		for (HarvestingMix hm : HarvestingMix.values()) {
			if (hm.isValid(itemOne.getCatalogId(), itemTwo.getCatalogId())) {
				return true;
			}
		}
		return false;
	}

	private void pourMixture(Item item, Player player) {
		if (player.getCarriedItems().hasCatalogID(ItemId.BOWL_OF_WATER.id(), Optional.of(false))) {
			String recipe = "";
			if (player.getCache().hasKey("harvesting_recipe")) {
				recipe = player.getCache().getString("harvesting_recipe");
			}
			if (recipe.contains("!")) {
				player.getCarriedItems().remove(new Item(ItemId.BOWL_OF_WATER.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.UNCOOKED_SEAWEED_SOUP.id()));
				player.getCache().remove("harvesting_recipe");
				mes("you pour the contents and get some uncooked soup");
				// the heating of uncooked soup handled with the cookingdef
			} else if (!recipe.isEmpty()) {
				mes("your mixture is still missing some contents");
				int option = multi(player, "Empty it", "Cancel");
				if (option == 0) {
					player.message("but you decide to empty it");
					player.getCache().remove("harvesting_recipe");
				}
			} else {
				mes("you need to put some contents into the mixing bowl");
			}
		} else {
			player.message("first you'll need a bowl with water to pour the mixture into");
		}
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.MIXING_BOWL.id()) {
			pourMixture(item, player);
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.MIXING_BOWL.id();
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return canMix(item1, item2);
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {

		if (item1.getCatalogId() == ItemId.PIE_SHELL.id() || item2.getCatalogId() == ItemId.PIE_SHELL.id()) {
			if (item1.getCatalogId() == ItemId.LILYS_PUMPKIN.id() || item2.getCatalogId() == ItemId.LILYS_PUMPKIN.id()) {
				if (player.getSkills().getLevel(Skill.COOKING.id()) < 40) {
					player.message("You need level 40 cooking to do this");
					return;
				}
				if (ifheld(player, ItemId.PIE_SHELL.id())
					&& ifheld(player, ItemId.LILYS_PUMPKIN.id())) {
					if (player.getCarriedItems().remove(new Item(ItemId.LILYS_PUMPKIN.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.PIE_SHELL.id())) > -1) {
						give(player, ItemId.UNCOOKED_LILYS_PUMPKIN_PIE.id(), 1);
						mes("You add the pumpkin to the pie shell");
					}
				}
			}
			// Pumpkin Pie & White Pumpkin Pie
			if (item1.getCatalogId() == ItemId.WHITE_PUMPKIN.id() || item2.getCatalogId() == ItemId.WHITE_PUMPKIN.id()) {
				if (player.getSkills().getLevel(Skill.COOKING.id()) < 80) {
					player.message("You need level 80 cooking to do this");
					return;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.EGG.id()) &&
					player.getCarriedItems().hasCatalogID(ItemId.MILK.id()) &&
					player.getCarriedItems().hasCatalogID(ItemId.WHITE_PUMPKIN.id()) &&
					player.getCarriedItems().hasCatalogID(ItemId.PIE_SHELL.id())) {
					if (player.getCarriedItems().remove(new Item(ItemId.EGG.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.MILK.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.WHITE_PUMPKIN.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.PIE_SHELL.id())) > -1) {
						player.getCarriedItems().getInventory().add(new Item(ItemId.UNCOOKED_WHITE_PUMPKIN_PIE.id()));
						player.playerServerMessage(MessageType.QUEST, "You mix the milk, egg, and white pumpkin together into your pie shell");
					}
				} else {
					if (!player.getCarriedItems().hasCatalogID(ItemId.EGG.id()))  // Egg
						player.playerServerMessage(MessageType.QUEST, "I also need an egg to make a white pumpkin pie");
					else if (!player.getCarriedItems().hasCatalogID(ItemId.MILK.id()))  // Milk
						player.playerServerMessage(MessageType.QUEST, "I also need some milk to make a white pumpkin pie");
				}
				return;
			}
			if (item1.getCatalogId() == ItemId.EGG.id() || item2.getCatalogId() == ItemId.EGG.id() ||
				item1.getCatalogId() == ItemId.MILK.id() || item2.getCatalogId() == ItemId.MILK.id() ||
				item1.getCatalogId() == ItemId.PUMPKIN.id() || item2.getCatalogId() == ItemId.PUMPKIN.id()) {
				if (player.getSkills().getLevel(Skill.COOKING.id()) < 80) {
					player.message("You need level 80 cooking to do this");
					return;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.EGG.id()) &&
					player.getCarriedItems().hasCatalogID(ItemId.MILK.id()) &&
					player.getCarriedItems().hasCatalogID(ItemId.PUMPKIN.id()) &&
					player.getCarriedItems().hasCatalogID(ItemId.PIE_SHELL.id())) {
					if (player.getCarriedItems().remove(new Item(ItemId.EGG.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.MILK.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.PUMPKIN.id())) > -1
						&& player.getCarriedItems().remove(new Item(ItemId.PIE_SHELL.id())) > -1) {
						player.getCarriedItems().getInventory().add(new Item(ItemId.UNCOOKED_PUMPKIN_PIE.id()));
						player.playerServerMessage(MessageType.QUEST, "You mix the milk, egg, and pumpkin together into your pie shell");
					}
				} else {
					if (!player.getCarriedItems().hasCatalogID(ItemId.EGG.id()))  // Egg
						player.playerServerMessage(MessageType.QUEST, "I also need an egg to make a pumpkin pie");
					else if (!player.getCarriedItems().hasCatalogID(ItemId.MILK.id()))  // Milk
						player.playerServerMessage(MessageType.QUEST, "I also need some milk to make a pumpkin pie");
					else if (!player.getCarriedItems().hasCatalogID(ItemId.PUMPKIN.id())) { // Pumpkin
						if (player.getCarriedItems().hasCatalogID(ItemId.WHITE_PUMPKIN.id())) {
							if (player.getCarriedItems().hasCatalogID(ItemId.EGG.id()) &&
								player.getCarriedItems().hasCatalogID(ItemId.MILK.id()) &&
								player.getCarriedItems().hasCatalogID(ItemId.WHITE_PUMPKIN.id()) &&
								player.getCarriedItems().hasCatalogID(ItemId.PIE_SHELL.id())) {
								if (player.getCarriedItems().remove(new Item(ItemId.EGG.id())) > -1
									&& player.getCarriedItems().remove(new Item(ItemId.MILK.id())) > -1
									&& player.getCarriedItems().remove(new Item(ItemId.WHITE_PUMPKIN.id())) > -1
									&& player.getCarriedItems().remove(new Item(ItemId.PIE_SHELL.id())) > -1) {
									player.getCarriedItems().getInventory().add(new Item(ItemId.UNCOOKED_WHITE_PUMPKIN_PIE.id()));
									player.playerServerMessage(MessageType.QUEST, "You mix the milk, egg, and white pumpkin together into your pie shell");
								}
							}
						} else {
							player.playerServerMessage(MessageType.QUEST, "I also need a pumpkin to make a pumpkin pie");
						}
					}
				}
			}
			return;
		}

		HarvestingMix hm = null;
		for (HarvestingMix mix : HarvestingMix.values()) {
			if (mix.isValid(item1.getCatalogId(), item2.getCatalogId())) {
				hm = mix;
			}
		}

		assert hm != null;
		if (player.getCarriedItems().hasCatalogID(hm.itemIDOther, Optional.of(false))) {
			if (addHarvestingRecipeCache(player, hm.itemIDOther)) {
				// element added
				player.message(hm.messages[0]);

				// Remove secondary ingredient
				player.getCarriedItems().remove(new Item(hm.itemIDOther));

			} else {
				// mixture already has enough of that ingredient
				player.message("Nothing interesting happens");
			}
		}
	}

	protected boolean addHarvestingRecipeCache(final Player player, int actionId) {
		String recipeString = "";

		// Get a stored recipe if one exists
		if (player.getCache().hasKey("harvesting_recipe")) {
			recipeString = player.getCache().getString("harvesting_recipe") + "-";
		}

		String actionString = actionId + "";

		recipeString += actionString;

		for (String recipe : recipeStrings) {

			if (!recipe.contains(actionId + "")) {
				continue;
			}

			Map<String, Integer> chkRecipeMap = new HashMap<>();
			Map<String, Integer> currRecipeMap = new HashMap<>();
			String[] toks = recipe.split("-");
			// counts of ingredients for recipe
			for (String s : toks) {
				chkRecipeMap.put(s, chkRecipeMap.getOrDefault(s, 0) + 1);
			}

			toks = recipeString.split("-");
			// counts of current ingredients for recipe
			for (String tok : toks) {
				currRecipeMap.put(tok, currRecipeMap.getOrDefault(tok, 0) + 1);
			}

			// Completed Mixture
			if (currRecipeMap.equals(chkRecipeMap)) {
				player.getCache().store("harvesting_recipe", recipeString + "!");
				return true;
			}
			// In Progress
			else if (currRecipeMap.get(actionString) <= chkRecipeMap.get(actionString)) {
				player.getCache().store("harvesting_recipe", recipeString);
				return true;
			}
			// Will exceed, prevent
			else {
				return false;
			}
		}
		return false;
	}

	enum HarvestingMix {
		CARP_ON_BOWL(ItemId.MIXING_BOWL.id(), ItemId.GIANT_CARP.id(),
			"you place a tasty carp over the mixing bowl"),
		SEAWEED_ON_BOWL(ItemId.MIXING_BOWL.id(), ItemId.EDIBLE_SEAWEED.id(),
			"you put some good seaweed to the mixing bowl"),
		GARLIC_ON_BOWL(ItemId.MIXING_BOWL.id(), ItemId.GARLIC.id(),
			"you add some garlic to the mixture"),
		POTATO_ON_BOWL(ItemId.MIXING_BOWL.id(), ItemId.POTATO.id(),
			"you put a potato on the mixture"),
		SPICE_ON_BOWL(ItemId.MIXING_BOWL.id(), ItemId.SPICE.id(),
			"you spice the mixture");

		private final int itemID;
		private final int itemIDOther;
		private final String[] messages;

		HarvestingMix(int itemOne, int itemTwo, String... messages) {
			this.itemID = itemOne;
			this.itemIDOther = itemTwo;
			this.messages = messages;
		}

		public boolean isValid(int i, int is) {
			return compareItemsIds(new Item(itemID), new Item(itemIDOther), i, is);
		}
	}
}
