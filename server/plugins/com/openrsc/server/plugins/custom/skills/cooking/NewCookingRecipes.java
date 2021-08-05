package com.openrsc.server.plugins.custom.skills.cooking;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class NewCookingRecipes implements OpInvTrigger, UseInvTrigger {

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
		HarvestingMix hm = null;
		for (HarvestingMix mix : HarvestingMix.values()) {
			if (mix.isValid(item1.getCatalogId(), item2.getCatalogId())) {
				hm = mix;
			}
		}

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
			String toks[] = recipe.split("-");
			// counts of ingredients for recipe
			for (int i = 0; i < toks.length; i++)
			{
				chkRecipeMap.put(toks[i], chkRecipeMap.getOrDefault(toks[i], 0) + 1);
			}

			toks = recipeString.split("-");
			// counts of current ingredients for recipe
			for (int i = 0; i < toks.length; i++)
			{
				currRecipeMap.put(toks[i], currRecipeMap.getOrDefault(toks[i], 0) + 1);
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

		private int itemID;
		private int itemIDOther;
		private String[] messages;

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
