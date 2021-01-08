package com.openrsc.server.plugins.authentic.minigames.gnomebar;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DrinkMixing implements UseInvTrigger {

	private GnomeBartending gb = new GnomeBartending();

	private boolean canMix(Item itemOne, Item itemTwo) {
		for (DrinkMix dm : DrinkMix.values()) {
			if (dm.isValid(itemOne.getCatalogId(), itemTwo.getCatalogId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return canMix(item1, item2);
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		DrinkMix dm = null;
		for (DrinkMix mix : DrinkMix.values()) {
			if (mix.isValid(item1.getCatalogId(), item2.getCatalogId())) {
				dm = mix;
			}
		}

		if ((player.getCarriedItems().hasCatalogID(ItemId.FULL_COCKTAIL_GLASS.id(), Optional.of(false))
			|| player.getCarriedItems().hasCatalogID(ItemId.HALF_COCKTAIL_GLASS.id(), Optional.of(false))
			|| player.getCarriedItems().hasCatalogID(ItemId.ODD_LOOKING_COCKTAIL.id(), Optional.of(false)))
			&& dm.itemID == ItemId.COCKTAIL_SHAKER.id()) {
			player.message("you need to finish, drink or drop your unfished cocktail");
			player.message("before you can start another - blurberry's rules");
			return;
		}

		if (player.getCarriedItems().hasCatalogID(dm.itemIDOther, Optional.of(false))) {

			mes(dm.messages[0]);
			delay(3);

			// Remove secondary ingredient
			if (dm.itemIDOther == ItemId.MILK.id()) {
				player.getCarriedItems().remove(new Item(ItemId.MILK.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.BUCKET.id()));
			} else {
				player.getCarriedItems().remove(new Item(dm.itemIDOther));
			}

			gb.addCocktailRecipeCache(player, dm.itemID, dm.itemIDOther);

			String recipe = "";
			if (player.getCache().hasKey("cocktail_recipe")) {
				recipe = player.getCache().getString("cocktail_recipe");
			}

			// If complete (denoted with a trailing ! on the recipe)
			if (recipe.endsWith("!")) {
				player.getCarriedItems().remove(new Item(dm.itemID)); // Remove base item

				if (gb.recipeStrings[gb.FRUIT_BLAST].equals(recipe)) {
					give(player, ItemId.FRUIT_BLAST.id(), 1);
				}
				else if (gb.recipeStrings[gb.PINEAPPLE_PUNCH].equals(recipe)) {
					give(player, ItemId.PINEAPPLE_PUNCH.id(), 1);
				}
				else if (gb.recipeStrings[gb.SHORT_GREEN_GUY].equals(recipe)) {
					give(player, ItemId.SGG.id(), 1);
				}
				else if (gb.recipeStrings[gb.CHOC_SATURDAY].equals(recipe)) {
					give(player, ItemId.CHOCOLATE_SATURDAY.id(), 1);
				}
				else if (gb.recipeStrings[gb.BLURBERRY_SPECIAL].equals(recipe)) {
					give(player, ItemId.BLURBERRY_SPECIAL.id(), 1);
				}
				else if (gb.recipeStrings[gb.WIZARD_BLIZZARD].equals(recipe)) {
					give(player, ItemId.WIZARD_BLIZZARD.id(), 1);
				}
				resetGnomeBartending(player);
			} else {
				boolean someRecipe = false;
				for (String chkRecipe : gb.recipeStrings) {
					if (chkRecipe.startsWith(recipe)) {
						someRecipe = true;
						break;
					}
				}
				if (dm.itemID == ItemId.FULL_COCKTAIL_GLASS.id() && !someRecipe) {
					player.getCarriedItems().remove(new Item(dm.itemID));
					player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
					resetGnomeBartending(player);
				}
			}

			if (dm.messages.length > 1) {
				player.message(dm.messages[1]);
			}
		}
	}

	enum DrinkMix {
		LEMON_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.LEMON.id(),
			"you squeeze the juice from the lemon...",
			"....into your cocktail shaker and shake well"),
		ORANGE_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.ORANGE.id(),
			"you squeeze the juice from the orange...",
			"....into your cocktail shaker and shake well"),
		PINE_APPLE_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.FRESH_PINEAPPLE.id(),
			"you squeeze the juice from the pineapple...",
			"....into your cocktail shaker and shake well"),
		LEMON_SLICES_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.LEMON_SLICES.id(),
			"you place the lemon slices on the edge of the glass"),
		VODKA_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.VODKA.id(),
			"you pour the vodka into the cocktail shaker",
			"you shake the container"),
		GIN_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.GIN.id(),
			"you pour the gin into the cocktail shaker",
			"you shake the container"),
		DWELLBERRIES_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.DWELLBERRIES.id(),
			"you squeeze the juice from the dwellberries...",
			"....into your cocktail shaker and shake well"),
		DICED_PINE_APPLE_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.PINEAPPLE_CHUNKS.id(),
			"you add the pineapple chunks to the drink"),
		CREAM_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.CREAM.id(),
			"you pour the thick cream into the drink"),
		LIME_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.LIME.id(),
			"you squeeze the juice from the lime...",
			"....into your cocktail shaker and shake well"),
		LEAVES_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.EQUA_LEAVES.id(),
			"you sprinkle the leaves over the drink"),
		LIME_SLICES_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.LIME_SLICES.id(),
			"you place the lime slices on the edge of the glass"),
		WHISKY_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.WHISKY.id(),
			"you pour the whisky into the cocktail shaker",
			"you shake the container"),
		MILK_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.MILK.id(),
			"you pour the milk into the cocktail shaker",
			"and shake thoroughly"),
		LEAVES_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.EQUA_LEAVES.id(),
			"you sprinkle the equa leaves into the shaker",
			"and shake thoroughly"),
		CHOCOLATE_BAR_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.CHOCOLATE_BAR.id(),
			"you crumble the chocolate into the drink"),
		CHOCOLATE_DUST_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.CHOCOLATE_DUST.id(),
			"you sprinkle the chocolate dust over the drink"),
		BRANDY_IN_SHAKER(ItemId.COCKTAIL_SHAKER.id(), ItemId.BRANDY.id(),
			"you pour the brandy into the cocktail shaker",
			"you shake the container"),
		DICED_ORANGE_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.DICED_ORANGE.id(),
			"you add the diced orange to the drink"),
		DICED_LEMON_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.DICED_LEMON.id(),
			"you add the diced lemon to the drink"),
		DICED_LIME_INTO_DRINK(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.LIME_CHUNKS.id(),
			"you add the lime chunks to the drink");


		private int itemID;
		private int itemIDOther;
		private String[] messages;

		DrinkMix(int itemOne, int itemTwo, String... messages) {
			this.itemID = itemOne;
			this.itemIDOther = itemTwo;
			this.messages = messages;
		}

		public boolean isValid(int i, int is) {
			return compareItemsIds(new Item(itemID), new Item(itemIDOther), i, is);
		}
	}
}
