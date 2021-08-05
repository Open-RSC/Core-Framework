package com.openrsc.server.plugins.authentic.minigames.gnomebar;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeBartending implements OpInvTrigger, UseLocTrigger {

	private boolean canHeat(Item item, GameObject object) {
		if ((item.getCatalogId() == ItemId.FULL_COCKTAIL_GLASS.id() || item.getCatalogId() == ItemId.HALF_COCKTAIL_GLASS.id()
			|| item.getCatalogId() == ItemId.ODD_LOOKING_COCKTAIL.id()) && inArray(object.getID(), 119)) {
			return true;
		}
		return false;
	}

	protected int FRUIT_BLAST = 0;
	protected int PINEAPPLE_PUNCH = 1;
	protected int DRUNK_DRAGON = 2;
	protected int SHORT_GREEN_GUY = 3;
	protected int CHOC_SATURDAY = 4;
	protected int BLURBERRY_SPECIAL = 5;
	protected int WIZARD_BLIZZARD = 6;

	protected String[] recipeStrings = {
		// 0 Fruit Blast
		String.format("%d%d-%d%d-%d%d-%d-%d%d!",
			ItemId.LEMON.id(), ItemId.COCKTAIL_SHAKER.id(), // Step One: Lemon on Cocktail Shaker
			ItemId.ORANGE.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Two: Orange on Cocktail Shaker
			ItemId.FRESH_PINEAPPLE.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Three: Pineapple on Cocktail Shaker
			ItemId.COCKTAIL_SHAKER.id(), // Step Four: Pour into glass
			ItemId.LEMON_SLICES.id(), ItemId.FULL_COCKTAIL_GLASS.id()), // Step Five: Lemon slices on glass

		// 1 Pineapple Punch
		String.format("%d%d-%d%d-%d%d-%d%d-%d-%d%d-%d%d-%d%d!",
			ItemId.FRESH_PINEAPPLE.id(), ItemId.COCKTAIL_SHAKER.id(), // Step One: Pineapple on Cocktail Shaker
			ItemId.FRESH_PINEAPPLE.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Two: Pineapple on Cocktail Shaker
			ItemId.LEMON.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Three: Lemon on Cocktail Shaker
			ItemId.ORANGE.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Four: Orange on Cocktail Shaker
			ItemId.COCKTAIL_SHAKER.id(), // Step Five: Pour into glass
			ItemId.PINEAPPLE_CHUNKS.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Six: Pineapple chunks on glass
			ItemId.LIME_CHUNKS.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Seven: Lime chunks on glass
			ItemId.LIME_SLICES.id(), ItemId.FULL_COCKTAIL_GLASS.id()), // Step Eight: Lime slices on glass

		// 2 Drunk Dragon
		String.format("%d%d-%d%d-%d%d-%d-%d%d-%d%d-%d!",
			ItemId.VODKA.id(), ItemId.COCKTAIL_SHAKER.id(), // Step One: Vodka on Cocktail Shaker
			ItemId.GIN.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Two: Gin on Cocktail Shaker
			ItemId.DWELLBERRIES.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Three: Dwellberries on Cocktail Shaker
			ItemId.COCKTAIL_SHAKER.id(), // Step Four: Pour into glass
			ItemId.PINEAPPLE_CHUNKS.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Five: Pineapple chunks on glass
			ItemId.CREAM.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Six: Cream on glass
			ItemId.FULL_COCKTAIL_GLASS.id()), // Step Seven: Heat drink

		// 3 Short Green Guy (SGG)
		String.format("%d%d-%d%d-%d%d-%d%d-%d-%d%d-%d%d!",
			ItemId.VODKA.id(), ItemId.COCKTAIL_SHAKER.id(), // Step One: Vodka on Cocktail Shaker
			ItemId.LIME.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Two: Lime on Cocktail Shaker
			ItemId.LIME.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Three: Lime on Cocktail Shaker
			ItemId.LIME.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Four: Lime on Cocktail Shaker
			ItemId.COCKTAIL_SHAKER.id(), // Step Five: Pour into glass
			ItemId.EQUA_LEAVES.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Six: Equa leaves on glass
			ItemId.LIME_SLICES.id(), ItemId.FULL_COCKTAIL_GLASS.id()), // Step Seven: Lime slices on glass

		// 4 Choc Saturday
		String.format("%d%d-%d%d-%d%d-%d-%d%d-%d-%d%d-%d%d!",
			ItemId.WHISKY.id(), ItemId.COCKTAIL_SHAKER.id(), // Step One: Whisky on Cocktail Shaker
			ItemId.MILK.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Two: Milk on Cocktail Shaker
			ItemId.EQUA_LEAVES.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Three: Equa leaves on Cocktail Shaker
			ItemId.COCKTAIL_SHAKER.id(), // Step Four: Pour into glass
			ItemId.CHOCOLATE_BAR.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Five: Chocolate on glass
			ItemId.FULL_COCKTAIL_GLASS.id(),  // Step Six: Heat drink
			ItemId.CREAM.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Seven: Cream on glass
			ItemId.CHOCOLATE_DUST.id(), ItemId.FULL_COCKTAIL_GLASS.id()), // Step Eight: Chocolate dust on glass

		// 5 Blurberry Special
		String.format("%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d-%d%d-%d%d-%d%d-%d%d!",
			ItemId.VODKA.id(), ItemId.COCKTAIL_SHAKER.id(), // Step One: Vodka on Cocktail Shaker
			ItemId.GIN.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Two: Gin on Cocktail Shaker
			ItemId.BRANDY.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Three: Brandy on Cocktail Shaker
			ItemId.LEMON.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Four: Lemon on Cocktail Shaker
			ItemId.LEMON.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Five: Lemon on Cocktail Shaker
			ItemId.ORANGE.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Six: Orange on Cocktail Shaker
			ItemId.COCKTAIL_SHAKER.id(), // Step Seven: Pour into glass
			ItemId.DICED_ORANGE.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Eight: Orange chunks on glass
			ItemId.DICED_LEMON.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Nine: Lemon chunks on glass
			ItemId.LIME_SLICES.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Ten: Lime slices on glass
			ItemId.EQUA_LEAVES.id(), ItemId.FULL_COCKTAIL_GLASS.id()), // Step Eleven: Equa leaves on glass

		// 6 Wizard Blizzard
		String.format("%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d-%d%d-%d%d!",
			ItemId.FRESH_PINEAPPLE.id(), ItemId.COCKTAIL_SHAKER.id(), // Step One: Pineapple on Cocktail Shaker
			ItemId.ORANGE.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Two: Orange on Cocktail Shaker
			ItemId.LEMON.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Three: Lemon on Cocktail Shaker
			ItemId.LIME.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Four: Lime on Cocktail Shaker
			ItemId.VODKA.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Five: Vodka on Cocktail Shaker
			ItemId.VODKA.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Six: Vodka on Cocktail Shaker
			ItemId.GIN.id(), ItemId.COCKTAIL_SHAKER.id(), // Step Seven: Gin on Cocktail Shaker
			ItemId.COCKTAIL_SHAKER.id(), // Step Eight: Pour into glass
			ItemId.PINEAPPLE_CHUNKS.id(), ItemId.FULL_COCKTAIL_GLASS.id(), // Step Nine: Pineapple chunks on glass
			ItemId.LIME_SLICES.id(), ItemId.FULL_COCKTAIL_GLASS.id()), // Step Ten: Lime slices on glass
	};

	private void handleCocktailHeating(final Item item, Player player, final GameObject object) {
		mes("you briefly place the drink in the oven");
		delay(3);
		player.message("you remove the warm drink");
		if (item.getCatalogId() == ItemId.FULL_COCKTAIL_GLASS.id()) {
			boolean recipeSuccess = addCocktailRecipeCache(player, -1, item.getCatalogId()); //heat the drink
			if (recipeSuccess) {
				// Drunk Dragon
				if (player.getCache().getString("cocktail_recipe").equals(recipeStrings[DRUNK_DRAGON])) {
					player.getCarriedItems().remove(new Item(item.getCatalogId()));
					give(player, ItemId.DRUNK_DRAGON.id(), 1);
					resetGnomeBartending(player);
				}
			} else {
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
				resetGnomeBartending(player);
			}
		}
	}

	private void pourGlass(Item item, Player player) {
		if (player.getCarriedItems().hasCatalogID(ItemId.COCKTAIL_GLASS.id(), Optional.of(false))) {
			String recipe = "";
			if (player.getCache().hasKey("cocktail_recipe")) {
				recipe = player.getCache().getString("cocktail_recipe");
			}
			if (!recipe.isEmpty() && !recipe.contains("-" + ItemId.COCKTAIL_SHAKER.id())) {
				boolean full = false;
				for (String chkRecipe : recipeStrings) {
					if (chkRecipe.startsWith(recipe + "-" + ItemId.COCKTAIL_SHAKER.id())) {
						full = true;
						break;
					}
				}
				player.getCarriedItems().remove(new Item(ItemId.COCKTAIL_GLASS.id()));
				if (full) {
					player.getCarriedItems().getInventory().add(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
				} else {
					player.getCarriedItems().getInventory().add(new Item(ItemId.HALF_COCKTAIL_GLASS.id()));
				}
				addCocktailRecipeCache(player, -1, item.getCatalogId());
				mes("you pour the contents into a glass");
			} else {
				mes("you need to put some contents into the shaker");
			}
			delay();
		} else {
			player.message("first you'll need a glass to pour the drink into");
		}
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.COCKTAIL_SHAKER.id()) {
			pourGlass(item, player);
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.COCKTAIL_SHAKER.id();
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return canHeat(item, obj);
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		handleCocktailHeating(item, player, obj);
	}

	protected boolean addCocktailRecipeCache(final Player player, int baseId, int actionId) {
		String recipeString = "";

		// Get a stored recipe if one exists
		if (player.getCache().hasKey("cocktail_recipe")) {
			recipeString = player.getCache().getString("cocktail_recipe") + "-";
		}

		// Base ID is -1 for pouring and heating AKA an empty string.
		String baseIdString = "";
		if (baseId == -1)
			baseIdString = actionId + "";
		else
			baseIdString = actionId + "" + baseId;

		recipeString += baseIdString;

		// Check recipe against cocktail guide to ensure we are still following a recipe and not starting a new one.
		String alternateRecipestring = "";
		for (String recipe : recipeStrings) {

			// Completed
			if (recipe.equals(recipeString + "!")) {
				player.getCache().store("cocktail_recipe", recipeString + "!");
				return true;
			}

			// Partially complete
			if (recipe.startsWith(recipeString)) {
				player.getCache().store("cocktail_recipe", recipeString);
				return true;
			}

			if (alternateRecipestring.equals("") && recipe.startsWith(baseIdString)) {
				alternateRecipestring = baseIdString;
			}

		}

		player.getCache().store("cocktail_recipe", baseIdString);
		return false;
	}
}
