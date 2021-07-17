package com.openrsc.server.plugins.custom.skills.crafting;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.external.ItemCraftingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.authentic.skills.crafting.Crafting;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class CustomJewelryCrafting implements UseLocTrigger {

	// Gets the gold moulds the player is holding.
	// The return array stores the index of the mould in the Crafting.gold_moulds array.
	private int[] getGoldMoulds(final Player player) {
		ArrayList<Integer> moulds = new ArrayList<>();
		for (int i = 0; i < Crafting.gold_moulds.length; i++) {
			if (player.getCarriedItems().getInventory().hasCatalogID(Crafting.gold_moulds[i])) {
				moulds.add(i);
			}
		}
		final int finalMoulds[] = moulds.stream().mapToInt(i->i).toArray();
		return finalMoulds;
	}

	// Gets the silver moulds the player is holding.
	// The return array stores the index of the mould in the Crafting.silver_moulds array.
	private int[] getSilverMoulds(final Player player) {
		ArrayList<Integer> moulds = new ArrayList<>();
		for (int i = 0; i < Crafting.silver_moulds.length; i++) {
			if (player.getCarriedItems().getInventory().hasCatalogID(Crafting.silver_moulds[i])) {
				moulds.add(i);
			}
		}
		final int finalMoulds[] = moulds.stream().mapToInt(i->i).toArray();
		return finalMoulds;
	}

	// Gets the gems the player is holding.
	// The return array stores the index of the mould in the Crafting.gems array.
	private int[] getGems(final Player player) {
		ArrayList<Integer> gemIds = new ArrayList<>();
		// Add the nothing ID for gold
		gemIds.add(0);
		for (int i = 1; i < Crafting.gems.length; i++) {
			if (player.getCarriedItems().getInventory().hasCatalogID(Crafting.gems[i])) {
				gemIds.add(i);
			}
		}
		final int finalGems[] = gemIds.stream().mapToInt(i->i).toArray();
		return finalGems;
	}

	private void doGoldCrafting(final Player player, final Item item) {
		// Get the moulds the player is holding
		int moulds[] = getGoldMoulds(player);
		// This will be the index of the mould in the Crafting.gold_moulds array.
		int mould = -1;
		if (moulds.length < 1) {
			player.message("You need a mould to craft jewelry");
			return;
		} else if (moulds.length == 1) { // Use the only mould the player is holding
			mould = moulds[0];
		} else { // Create a list so the player can select which mould to use.
			ArrayList<String> options = new ArrayList<>();
			for (int i = 0; i < moulds.length; i++) {
				options.add(player.getWorld().getServer().getEntityHandler().getItemDef(
					Crafting.gold_moulds[moulds[i]]).getName());
			}
			final String finalOptions[] = new String[options.size()];
			player.message("Please select a mould to use");
			int choice = multi(player, options.toArray(finalOptions));
			if (choice == -1) return;
			mould = moulds[choice];
		}

		if (mould == -1) return;

		// Get the name of the item they're trying to make
		String mouldType = player.getWorld().getServer().getEntityHandler().getItemDef(
			Crafting.gold_moulds[mould]).getName().replace(" mould", "").toLowerCase();

		// Get the gem (if any) the player is trying to use
		int gems[] = getGems(player);
		// This will be the index of the gem in the Crafting.gems array.
		int gem = -1;
		if (gems.length <= 1) { // If they don't have a gem, we do gold crafting
			gem = 0;
		} else if (gems.length == 2) { // If they have only one gem, default to that.
			gem = gems[1];
		} else { // If they have more than one gem, ask which one they'd like to use.
			ArrayList<String> options = new ArrayList<>();
			// Gold will always be on the list, so we don't have to check the first slot of array
			options.add("Gold");
			for (int i = 1; i < gems.length; i++) {
				options.add(player.getWorld().getServer().getEntityHandler().getItemDef(
					Crafting.gems[gems[i]]).getName());
			}
			final String finalOptions[] = new String[options.size()];
			player.message("Please select what type of " + mouldType + " you'd like to make");
			int choice = multi(player, options.toArray(finalOptions));
			if (choice == -1) return;
			gem = gems[choice];
		}

		if (gem == -1) return;

		ItemCraftingDef def = player.getWorld().getServer().getEntityHandler().getCraftingDef((gem * 3) + mould);
		if (def == null) {
			player.message("Nothing interesting happens");
			return;
		}

		int repeat = 1;

		if (config().BATCH_PROGRESSION) {
			if (gem > 0) {
				repeat = Math.min(
					player.getCarriedItems().getInventory().countId(Crafting.gems[gem], Optional.of(false)),
					player.getCarriedItems().getInventory().countId(ItemId.GOLD_BAR.id(), Optional.of(false))
				);
			} else {
				repeat = player.getCarriedItems().getInventory().countId(ItemId.GOLD_BAR.id(), Optional.of(false));
			}
		}

		startbatch(repeat);
		batchGoldCrafting(player, item, def, gem);

	}

	private void batchGoldCrafting(Player player, Item item, ItemCraftingDef def, int gem) {
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < def.getReqLevel()) {
			player.playerServerMessage(MessageType.QUEST, "You need a crafting skill of level " + def.getReqLevel() + " to make this");
			return;
		}

		if (checkFatigue(player)) return;

		// Get last gem in inventory
		Item gemItem;
		if (gem != 0) {
			gemItem = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(Crafting.gems[gem], Optional.of(false))
			);
			if (gemItem == null) {
				player.message("You don't have a "
					+ player.getWorld().getServer().getEntityHandler().getItemDef(Crafting.gems[gem]).getName() + ".");
				return;
			}
		}

		// Get last gold bar in inventory
		Item goldBar = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (goldBar == null) {
			player.message("You don't have a "
				+ player.getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId()).getName() + ".");
			return;
		}

		// Remove items
		thinkbubble(goldBar);
		player.getCarriedItems().remove(goldBar);
		if (gem > 0) {
			player.getCarriedItems().remove(new Item(Crafting.gems[gem]));
		}
		delay(2);

		Item result = new Item(def.getItemID(), 1);

		player.playerServerMessage(MessageType.QUEST, "You make a " + result.getDef(player.getWorld()).getName());
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), def.getExp(), true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchGoldCrafting(player, item, def, gem);
		}
	}

	private void doSilverCrafting(Player player, Item item) {
		// Get the moulds the player is holding
		int moulds[] = getSilverMoulds(player);
		// This will be the index of the mould in the Crafting.silver_moulds array.
		int mould = -1;
		if (moulds.length < 1) {
			player.message("You need a mould to craft jewelry");
			return;
		} else if (moulds.length == 1) { // Use the only mould the player is holding
			mould = moulds[0];
		} else { // Create a list so the player can select which mould to use.
			ArrayList<String> options = new ArrayList<>();
			for (int i = 0; i < moulds.length; i++) {
				options.add(player.getWorld().getServer().getEntityHandler().getItemDef(
					Crafting.silver_moulds[moulds[i]]).getName());
			}
			final String finalOptions[] = new String[options.size()];
			player.message("Please select a mould to use");
			int choice = multi(player, options.toArray(finalOptions));
			if (choice == -1) return;
			mould = moulds[choice];
		}

		if (mould == -1) return;

		final int[] results = {
			ItemId.UNSTRUNG_HOLY_SYMBOL_OF_SARADOMIN.id(),
			ItemId.UNSTRUNG_UNHOLY_SYMBOL_OF_ZAMORAK.id()
		};

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchSilverCrafting(player, item, results[mould]);
	}

	private void batchSilverCrafting(Player player, Item item, int resultId) {
		if (player.getSkills().getLevel(Skill.CRAFTING.id()) < 16) {
			player.playerServerMessage(MessageType.QUEST, "You need a crafting skill of level 16 to make this");
			return;
		}
		if (checkFatigue(player)) return;

		// Get last silver bar in inventory
		Item silver = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (silver == null) return;

		// Remove silver bar
		thinkbubble(silver);
		player.getCarriedItems().remove(silver);
		delay(2);

		Item result = new Item(resultId);
		player.playerServerMessage(MessageType.QUEST, "You make a " + result.getDef(player.getWorld()).getName());
		player.getCarriedItems().getInventory().add(result);
		player.incExp(Skill.CRAFTING.id(), 200, true);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchSilverCrafting(player, item, resultId);
		}
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.GOLD_BAR.id()) {
			doGoldCrafting(player, item);
		} else if (item.getCatalogId() == ItemId.SILVER_BAR.id()) {
			doSilverCrafting(player, item);
		}
	}

	private boolean checkFatigue(Player player) {
		if (config().WANT_FATIGUE
			&& config().STOP_SKILLING_FATIGUED >= 2
			&& player.getFatigue() >= player.MAX_FATIGUE) {
			player.message("You are too tired to craft");
			return true;
		}
		return false;
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		final boolean furnace = obj.getID() == 118 || obj.getID() == 813;
		final boolean jewelryBar = item.getCatalogId() == ItemId.SILVER_BAR.id() || item.getCatalogId() == ItemId.GOLD_BAR.id();
		final boolean wantBetterJewelryCrafting = player.getConfig().WANT_BETTER_JEWELRY_CRAFTING && !player.getQolOptOut();

		// Checks to make sure that we're using a jewelry bar on a furnace
		// And that we have WANT_BETTER_JEWELRY_CRAFTING enabled.
		// Otherwise it will be handled in the main Crafting class.
		return furnace && jewelryBar && wantBetterJewelryCrafting;
	}
}
