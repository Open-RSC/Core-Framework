package com.openrsc.server.plugins.authentic.minigames.gnomerestaurant;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeCooking implements OpInvTrigger, UseLocTrigger {

	private boolean canCook(Item item, GameObject object) {
		for (GnomeCook c : GnomeCook.values()) {
			if (item.getCatalogId() == c.uncookedID && inArray(object.getID(), 119)) {
				return true;
			}
		}
		return false;
	}

	protected int GNOMECRUNCHIE = 0;
	protected int CHOC_CRUNCHIE = 1;
	protected int WORM_CRUNCHIE = 2;
	protected int TOAD_CRUNCHIE = 3;
	protected int SPICY_CRUNCHIE = 4;
	protected int CHEESE_AND_TOMATO_BATTA = 5;
	private int TOAD_BATTA = 6;
	protected int WORM_BATTA = 7;
	protected int FRUIT_BATTA = 8;
	protected int VEG_BATTA = 9;
	protected int CHOC_BOMB = 10;
	protected int VEGBALL = 11;
	protected int WORM_HOLE = 12;
	private int TANGLED_TOADS_LEGS = 13;

	protected String[] recipeStrings = {

		// 0 Gnomecrunchie
		String.format("%d!", ItemId.GNOMECRUNCHIE_DOUGH.id()),

		// 1 Choc crunchies
		String.format("%d%d-%d%d-%d%d-%d-%d-%d%d!",
			ItemId.CHOCOLATE_BAR.id(), ItemId.GIANNE_DOUGH.id(), // Step One: Chocolate bar on Gianne Dough
			ItemId.CHOCOLATE_BAR.id(), ItemId.GIANNE_DOUGH.id(), // Step Two: Chocolate bar on Gianne Dough
			ItemId.GNOME_SPICE.id(), ItemId.GIANNE_DOUGH.id(), // Step Three: Gnome Spice on Gianne Dough
			ItemId.GIANNE_DOUGH.id(), // Step Four: Mould Gianne Dough
			ItemId.GNOMECRUNCHIE_DOUGH.id(), // Step Five: Bake Gnomecrunchie Dough
			ItemId.CHOCOLATE_DUST.id(), ItemId.GNOMECRUNCHIE.id()), // Step Six: Chocolate Dust on Gnomecrunchie

		// 2 Worm crunchies
		String.format("%d%d-%d%d-%d%d-%d%d-%d-%d-%d%d!",
			ItemId.GNOME_SPICE.id(), ItemId.GIANNE_DOUGH.id(), // Step One: Gnome Spice on Gianne Dough
			ItemId.KING_WORM.id(), ItemId.GIANNE_DOUGH.id(), // Step Two: King Worm on Gianne Dough
			ItemId.KING_WORM.id(), ItemId.GIANNE_DOUGH.id(), // Step Three: King Worm on Gianne Dough
			ItemId.EQUA_LEAVES.id(), ItemId.GIANNE_DOUGH.id(), // Step Four: Equa Leaves on Gianne Dough
			ItemId.GIANNE_DOUGH.id(), // Step Five: Mould Gianne Dough
			ItemId.GNOMECRUNCHIE_DOUGH.id(), // Step Six: Bake Gnomecrunchie Dough
			ItemId.GNOME_SPICE.id(), ItemId.GNOMECRUNCHIE.id()), // Step Seven: Gnome Spice on Gnomecrunchie

		// 3 Toad crunchies
		String.format("%d%d-%d%d-%d%d-%d-%d-%d%d!",
			ItemId.GNOME_SPICE.id(), ItemId.GIANNE_DOUGH.id(), // Step One: Gnome Spice on Gianne Dough
			ItemId.TOAD_LEGS.id(), ItemId.GIANNE_DOUGH.id(), // Step Two: Toad Legs on Gianne Dough
			ItemId.TOAD_LEGS.id(), ItemId.GIANNE_DOUGH.id(), // Step Three: Toad Legs on Gianne Dough
			ItemId.GIANNE_DOUGH.id(), // Step Four: Mould Gianne Dough
			ItemId.GNOMECRUNCHIE_DOUGH.id(), // Step Five: Bake Gnomecrunchie Dough
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMECRUNCHIE.id()), // Step Six: Equa Leaves on Gnomecrunchie

		// 4 Spicy crunchies
		String.format("%d%d-%d%d-%d%d-%d%d-%d%d-%d-%d-%d%d!",
			ItemId.GNOME_SPICE.id(), ItemId.GIANNE_DOUGH.id(), // Step One: Gnome Spice on Gianne Dough
			ItemId.GNOME_SPICE.id(), ItemId.GIANNE_DOUGH.id(), // Step Two: Gnome Spice on Gianne Dough
			ItemId.GNOME_SPICE.id(), ItemId.GIANNE_DOUGH.id(), // Step Three: Gnome Spice on Gianne Dough
			ItemId.EQUA_LEAVES.id(), ItemId.GIANNE_DOUGH.id(), // Step Four: Equa Leaves on Gianne Dough
			ItemId.EQUA_LEAVES.id(), ItemId.GIANNE_DOUGH.id(), // Step Five: Equa Leaves on Gianne Dough
			ItemId.GIANNE_DOUGH.id(), // Step Six: Mould Gianne Dough
			ItemId.GNOMECRUNCHIE_DOUGH.id(), // Step Seven: Bake Gnomecrunchie Dough
			ItemId.GNOME_SPICE.id(), ItemId.GNOMECRUNCHIE.id()), // Step Eight: Gnome Spice on Gnomecrunchie

		// 5 Cheese and tomato batta
		String.format("%d%d-%d%d-%d-%d%d!",
			ItemId.CHEESE.id(), ItemId.GNOMEBATTA.id(), // Step One: Cheese on Gnomebatta
			ItemId.TOMATO.id(), ItemId.GNOMEBATTA.id(), // Step Two: Tomato on Gnomebatta
			ItemId.GNOMEBATTA.id(), // Step Three: Bake Gnomebatta
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBATTA.id()), // Step Four: Equa Leaves on Gnomebatta

		// 6 Toad batta (use equa leaves and gnome spice on toad legs)
		String.format("%d%d-%d%d-%d%d-%d%d-%d!",
			ItemId.EQUA_LEAVES.id(), ItemId.TOAD_LEGS.id(), // Step One: Equa Leaves on Toad Legs
			ItemId.GNOME_SPICE.id(), ItemId.TOAD_LEGS.id(), // Step Two: Gnome Spice on Toad Legs
			ItemId.TOAD_LEGS.id(), ItemId.GNOMEBATTA.id(), // Step Three: Toad Legs on Gnomebatta
			ItemId.CHEESE.id(), ItemId.GNOMEBATTA.id(), // Step Four: Cheese on Gnomebatta
			ItemId.GNOMEBATTA.id()), // Step Five: Bake Gnomebatta

		// 7 Worm batta (use gnome spice on king worm)
		String.format("%d%d-%d%d-%d%d-%d-%d%d!",
			ItemId.GNOME_SPICE.id(), ItemId.KING_WORM.id(), // Step One: Gnome Spice on King Worm
			ItemId.KING_WORM.id(), ItemId.GNOMEBATTA.id(), // Step Two: King Worm on Gnomebatta
			ItemId.CHEESE.id(), ItemId.GNOMEBATTA.id(), // Step Three: Cheese on Gnomebatta
			ItemId.GNOMEBATTA.id(), // Step Four: Bake Gnomebatta
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBATTA.id()), // Step Five: Equa Leaves on Gnomebatta

		// 8 Fruit batta
		String.format("%d%d-%d%d-%d%d-%d%d-%d-%d%d-%d%d-%d%d-%d%d!",
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBATTA.id(), // Step One: Equa Leaves on Gnomebatta
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBATTA.id(), // Step Two: Equa Leaves on Gnomebatta
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBATTA.id(), // Step Three: Equa Leaves on Gnomebatta
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBATTA.id(), // Step Four: Equa Leaves on Gnomebatta
			ItemId.GNOMEBATTA.id(), // Step Five: Bake Gnomebatta
			ItemId.PINEAPPLE_CHUNKS.id(), ItemId.GNOMEBATTA.id(), // Step Six: Pineapple Chunks on Gnomebatta
			ItemId.DICED_ORANGE.id(), ItemId.GNOMEBATTA.id(), // Step Seven: Diced Orange on Gnomebatta
			ItemId.LIME_CHUNKS.id(), ItemId.GNOMEBATTA.id(), // Step Eight: Lime Chunks on Gnomebatta
			ItemId.GNOME_SPICE.id(), ItemId.GNOMEBATTA.id()), // Step Nine: Gnome Spice on Gnomebatta

		// 9 Veg batta
		String.format("%d%d-%d%d-%d%d-%d%d-%d%d-%d-%d%d-%d-%d%d!",
			ItemId.ONION.id(), ItemId.GNOMEBATTA.id(), // Step One: Onion on Gnomebatta
			ItemId.TOMATO.id(), ItemId.GNOMEBATTA.id(), // Step Two: Tomato on Gnomebatta
			ItemId.TOMATO.id(), ItemId.GNOMEBATTA.id(), // Step Three: Tomato on Gnomebatta
			ItemId.CABBAGE.id(), ItemId.GNOMEBATTA.id(), // Step Four: Cabbage on Gnomebatta
			ItemId.DWELLBERRIES.id(), ItemId.GNOMEBATTA.id(), // Step Five: Dwellberries on Gnomebatta
			ItemId.GNOMEBATTA.id(), // Step Six: Bake Gnomebatta
			ItemId.CHEESE.id(), ItemId.GNOMEBATTA.id(), // Step Seven: Cheese on Gnomebatta
			ItemId.GNOMEBATTA.id(), // Step Eight: Bake Gnomebatta
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBATTA.id()), // Step Nine: Equa Leaves on Gnomebatta

		// 10 Choc bomb
		String.format("%d%d-%d%d-%d%d-%d%d-%d%d-%d-%d%d-%d%d-%d%d!",
			ItemId.CHOCOLATE_BAR.id(), ItemId.GNOMEBOWL.id(), // Step One: Chocolate Bar on Gnomebowl
			ItemId.CHOCOLATE_BAR.id(), ItemId.GNOMEBOWL.id(), // Step Two: Chocolate Bar on Gnomebowl
			ItemId.CHOCOLATE_BAR.id(), ItemId.GNOMEBOWL.id(), // Step Three: Chocolate Bar on Gnomebowl
			ItemId.CHOCOLATE_BAR.id(), ItemId.GNOMEBOWL.id(), // Step Four: Chocolate Bar on Gnomebowl
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBOWL.id(), // Step Five: Equa Leaves on Gnomebowl
			ItemId.GNOMEBOWL.id(), // Step Six: Bake Gnomebowl
			ItemId.CREAM.id(), ItemId.GNOMEBOWL.id(), // Step Seven: Cream on Gnomebowl
			ItemId.CREAM.id(), ItemId.GNOMEBOWL.id(), // Step Eight: Cream on Gnomebowl
			ItemId.CHOCOLATE_DUST.id(), ItemId.GNOMEBOWL.id()), // Step Nine: Chocolate Dust on Gnomebowl

		// 11 VegBall
		String.format("%d%d-%d%d-%d%d-%d%d-%d%d-%d-%d%d!",
			ItemId.ONION.id(), ItemId.GNOMEBOWL.id(), // Step One: Onion on Gnomebowl
			ItemId.ONION.id(), ItemId.GNOMEBOWL.id(), // Step Two: Onion on Gnomebowl
			ItemId.POTATO.id(), ItemId.GNOMEBOWL.id(), // Step Three: Potato on Gnomebowl
			ItemId.POTATO.id(), ItemId.GNOMEBOWL.id(), // Step Four: Potato on Gnomebowl
			ItemId.GNOME_SPICE.id(), ItemId.GNOMEBOWL.id(), // Step Five: Gnome Spice on Gnomebowl
			ItemId.GNOMEBOWL.id(), // Step Six: Bake Gnomebowl
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBOWL.id()), // Step Seven: Equa Leaves on Gnomebowl

		// 12 Worm hole
		String.format("%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d-%d%d!",
			ItemId.KING_WORM.id(), ItemId.GNOMEBOWL.id(), // Step One: King Worm on Gnomebowl
			ItemId.KING_WORM.id(), ItemId.GNOMEBOWL.id(), // Step Two: King Worm on Gnomebowl
			ItemId.KING_WORM.id(), ItemId.GNOMEBOWL.id(), // Step Three: King Worm on Gnomebowl
			ItemId.KING_WORM.id(), ItemId.GNOMEBOWL.id(), // Step Four: King Worm on Gnomebowl
			ItemId.KING_WORM.id(), ItemId.GNOMEBOWL.id(), // Step Five: King Worm on Gnomebowl
			ItemId.KING_WORM.id(), ItemId.GNOMEBOWL.id(), // Step Six: King Worm on Gnomebowl
			ItemId.ONION.id(), ItemId.GNOMEBOWL.id(), // Step Seven: Onion on Gnomebowl
			ItemId.ONION.id(), ItemId.GNOMEBOWL.id(), // Step Eight: Onion on Gnomebowl
			ItemId.GNOME_SPICE.id(), ItemId.GNOMEBOWL.id(), // Step Nine: Gnome Spice on Gnomebowl
			ItemId.GNOMEBOWL.id(), // Step Ten: Bake Gnomebowl
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBOWL.id()), // Step Eleven: Equa Leaves on Gnomebowl

		// 13 Tangled toads legs (Twisted toads legs)
		String.format("%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d%d-%d!",
			ItemId.CHEESE.id(), ItemId.GNOMEBOWL.id(), // Step One: Cheese on Gnomebowl
			ItemId.CHEESE.id(), ItemId.GNOMEBOWL.id(), // Step Two: Cheese on Gnomebowl
			ItemId.TOAD_LEGS.id(), ItemId.GNOMEBOWL.id(), // Step Three: Toad Legs on Gnomebowl
			ItemId.TOAD_LEGS.id(), ItemId.GNOMEBOWL.id(), // Step Four: Toad Legs on Gnomebowl
			ItemId.TOAD_LEGS.id(), ItemId.GNOMEBOWL.id(), // Step Five: Toad Legs on Gnomebowl
			ItemId.TOAD_LEGS.id(), ItemId.GNOMEBOWL.id(), // Step Six: Toad Legs on Gnomebowl
			ItemId.TOAD_LEGS.id(), ItemId.GNOMEBOWL.id(), // Step Seven: Toad Legs on Gnomebowl
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBOWL.id(), // Step Eight: Equa Leaves on Gnomebowl
			ItemId.EQUA_LEAVES.id(), ItemId.GNOMEBOWL.id(), // Step Nine: Equa Leaves on Gnomebowl
			ItemId.DWELLBERRIES.id(), ItemId.GNOMEBOWL.id(), // Step Ten: Dwellberries on Gnomebowl
			ItemId.GNOME_SPICE.id(), ItemId.GNOMEBOWL.id(), // Step Eleven: Gnome Spice on Gnomebowl
			ItemId.GNOME_SPICE.id(), ItemId.GNOMEBOWL.id(), // Step Twelve: Gnome Spice on Gnomebowl
			ItemId.GNOMEBOWL.id()), // Step Thirteen: Bake Gnomebowl
	};

	private void handleGnomeCooking(final Item item, Player player, final GameObject object) {
		GnomeCook gc = null;
		for (GnomeCook c : GnomeCook.values()) {
			if (item.getCatalogId() == c.uncookedID && inArray(object.getID(), 119)) {
				gc = c;
			}
		}
		// NOTE: THERE ARE NO REQUIREMENT TO COOK THE DOUGH ONLY TO MOULD IT.
		thinkbubble(item);
		player.playSound("cooking");
		if (player.getCarriedItems().remove(item) > -1) {
			mes(gc.messages[0]);
			delay(5);
			if (!burnFood(player, item.getCatalogId(), player.getSkills().getLevel(Skill.COOKING.id()))) {
				player.message(gc.messages[1]);

				// Cooking Gnomebatta and Gnomebowl base
				if (item.getCatalogId() == ItemId.GNOMEBATTA_DOUGH.id() || item.getCatalogId() == ItemId.GNOMEBOWL_DOUGH.id()) {
					give(player, gc.cookedID, 1);
					return;
				}

				// Successful recipe
				boolean recipeSuccess = addGnomeRecipeCache(player, -1, item.getCatalogId());
				if (recipeSuccess) {
					player.incExp(Skill.COOKING.id(), gc.experience, true);

					// Toad Batta
					if (player.getCache().getString("gnome_recipe").equals(recipeStrings[TOAD_BATTA])) {
						give(player, ItemId.TOAD_BATTA.id(), 1);
						resetGnomeCooking(player);
					}

					// Tangled Toads Legs
					else if (player.getCache().getString("gnome_recipe").equals(recipeStrings[TANGLED_TOADS_LEGS])) {
						give(player, ItemId.TANGLED_TOADS_LEGS.id(), 1);
						resetGnomeCooking(player);
					}

					// Basic baking
					else if (gc.cookedID == ItemId.GNOMEBATTA.id() || gc.cookedID == ItemId.GNOMEBOWL.id()
						|| gc.cookedID == ItemId.GNOMECRUNCHIE.id()) {
						give(player, gc.cookedID, 1);
					}
				}
			}
			else {
				give(player, gc.burntID, 1);
				player.message(gc.messages[2]);
				resetGnomeCooking(player);
			}
		}
	}

	private boolean mouldDough(Item item, Player player) {
		if (player.getCarriedItems().hasCatalogID(ItemId.GNOMEBATTA_DOUGH.id(), Optional.of(false))
			|| player.getCarriedItems().hasCatalogID(ItemId.GNOMEBOWL_DOUGH.id(), Optional.of(false))
			|| player.getCarriedItems().hasCatalogID(ItemId.GNOMECRUNCHIE_DOUGH.id(), Optional.of(false))
			|| player.getCarriedItems().hasCatalogID(ItemId.GNOMEBATTA.id(), Optional.of(false))
			|| player.getCarriedItems().hasCatalogID(ItemId.GNOMEBOWL.id(), Optional.of(false))
			|| player.getCarriedItems().hasCatalogID(ItemId.GNOMECRUNCHIE.id(), Optional.of(false))) {
			mes("you need to finish, eat or drop the unfinished dish you hold");
			delay(3);
			player.message("before you can make another - giannes rules");
			return false;
		}
		player.message("which shape would you like to mould");
		int menu = multi(player,
			"gnomebatta",
			"gnomebowl",
			"gnomecrunchie");
		if (menu != -1) {
			player.setOption(-1);
			if (menu == 0) {
				if (player.getSkills().getLevel(Skill.COOKING.id()) < 25) {
					player.message("you need a cooking level of 25 to mould dough batta's");
					return false;
				}

				thinkbubble(item);
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				mes("you attempt to mould the dough into a gnomebatta");
				delay(5);
				player.message("You manage to make some gnome batta dough");
				give(player, ItemId.GNOMEBATTA_DOUGH.id(), 1);

				// Add Gianne Dough to our current recipe
				addGnomeRecipeCache(player, -1, ItemId.GIANNE_DOUGH.id());

			} else if (menu == 1) {
				if (player.getSkills().getLevel(Skill.COOKING.id()) < 30) {
					player.message("you need a cooking level of 30 to mould dough bowls");
					return false;
				}

				thinkbubble(item);
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				mes("you attempt to mould the dough into a gnome bowl");
				delay(5);
				player.message("You manage to make some gnome bowl dough");
				give(player, ItemId.GNOMEBOWL_DOUGH.id(), 1);

				// Add Gianne Dough to our current recipe
				addGnomeRecipeCache(player, -1, ItemId.GIANNE_DOUGH.id());

			} else if (menu == 2) {
				if (player.getSkills().getLevel(Skill.COOKING.id()) < 15) {
					player.message("you need a cooking level of 15 to mould crunchies");
					return false;
				}

				thinkbubble(item);
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				mes("you attempt to mould the dough into gnome crunchies");
				delay(5);
				player.message("You manage to make some gnome crunchies dough");
				give(player, ItemId.GNOMECRUNCHIE_DOUGH.id(), 1);

				// Add Gianne Dough to our current recipe
				addGnomeRecipeCache(player, -1, ItemId.GIANNE_DOUGH.id());
			}
			player.incExp(Skill.COOKING.id(), 100, true);
		}
		return true;

	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.GIANNE_DOUGH.id()) {
			mouldDough(item, player);
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.GIANNE_DOUGH.id();
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return canCook(item, obj);
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		handleGnomeCooking(item, player, obj);
	}

	private boolean burnFood(Player player, int itemId, int myCookingLvl) {
		return Formulae.burnFood(player, itemId, myCookingLvl);
	}

	protected boolean addGnomeRecipeCache(final Player player, int baseId, int actionId) {
		String recipeString = "";

		// Get a stored recipe if one exists
		if (player.getCache().hasKey("gnome_recipe")) {
			recipeString = player.getCache().getString("gnome_recipe") + "-";
		}

		// Base ID is -1 for moulding and baking AKA an empty string.
		String baseIdString = "";
		if (baseId == -1)
			baseIdString = actionId + "";
		else
			baseIdString = actionId + "" + baseId;

		recipeString += baseIdString;

		// If we are just moulding or cooking the dough,
		// we do not need to begin a recipe string.
		// Gnomecrunchie is allowed because the mould happens later in the recipes.
		if (recipeString.length() == 3 && !recipeString.equals(ItemId.GNOMECRUNCHIE_DOUGH.id() + ""))
			return false;

		// Check recipe against cookbook to ensure we are still following a recipe and not starting a new one.
		String alternateRecipestring = "";
		for (String recipe : recipeStrings) {

			// Completed
			if (recipe.equals(recipeString + "!")) {
				player.getCache().store("gnome_recipe", recipeString + "!");
				return true;
			}

			// Partially complete
			if (recipe.startsWith(recipeString)) {
				player.getCache().store("gnome_recipe", recipeString);
				return true;
			}

			if (alternateRecipestring.equals("") && recipe.startsWith(baseIdString)) {
				alternateRecipestring = baseIdString;
			}

		}

		player.getCache().store("gnome_recipe", baseIdString);
		return false;
	}

	enum GnomeCook {
		GNOME_BATTA_DOUGH(ItemId.GNOMEBATTA_DOUGH.id(), ItemId.GNOMEBATTA.id(), ItemId.BURNT_GNOMEBATTA.id(), 120, 1,
			"You cook the gnome batta in the oven...",
			"You remove the gnome batta from the oven",
			"You accidentally burn the gnome batta"),

		GNOME_BOWL_DOUGH(ItemId.GNOMEBOWL_DOUGH.id(), ItemId.GNOMEBOWL.id(), ItemId.BURNT_GNOMEBOWL.id(), 120, 1,
			"You cook the gnome bowl in the oven...",
			"You remove the gnome bowl from the oven",
			"You accidentally burn the gnome bbowl"),

		GNOME_CRUNCHIE_DOUGH(ItemId.GNOMECRUNCHIE_DOUGH.id(), ItemId.GNOMECRUNCHIE.id(), ItemId.BURNT_GNOMECRUNCHIE.id(), 120, 1,
			"You cook the gnome crunchie in the oven...",
			"You remove the gnome crunchie from the oven",
			"You accidentally burn the gnome crunchie"),

		GNOME_BATTA_ALREADY_COOKED(ItemId.GNOMEBATTA.id(), ItemId.GNOMEBATTA.id(), ItemId.BURNT_GNOMEBATTA.id(), 120, 1,
			"You cook the gnome batta in the oven...",
			"You remove the gnome batta from the oven",
			"You accidentally burn the gnome batta"),

		GNOME_BOWL_ALREADY_COOKED(ItemId.GNOMEBOWL.id(), ItemId.GNOMEBOWL.id(), ItemId.BURNT_GNOMEBOWL.id(), 120, 1,
			"You cook the gnome bowl in the oven...",
			"You remove the gnome bowl from the oven",
			"You accidentally burn the gnome bbowl");

		private int uncookedID;
		private int cookedID;
		private int burntID;
		private int experience;
		private int requiredLevel;
		private String[] messages;

		GnomeCook(int uncookedID, int cookedID, int burntID, int experience, int reqlevel, String... cookingMessages) {
			this.uncookedID = uncookedID;
			this.cookedID = cookedID;
			this.burntID = burntID;
			this.experience = experience;
			this.requiredLevel = reqlevel;
			this.messages = cookingMessages;
		}
	}
}
