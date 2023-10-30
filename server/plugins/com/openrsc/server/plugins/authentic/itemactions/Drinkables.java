package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.itemactions.RunecraftPotion;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.openrsc.server.plugins.Functions.*;

public class Drinkables implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return command.equalsIgnoreCase("drink") && !inArray(item.getCatalogId(), RunecraftPotion.runecraftPotions);
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getItemStatus().getNoted()) {
			return;
		}

		// act on the last item from inventory
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false)));
		int id = item.getCatalogId();

		if (id == ItemId.BRANDY.id() || id == ItemId.VODKA.id()
			|| id == ItemId.GIN.id() || id == ItemId.WHISKY.id()) {
			handleSpirits(player, item);

		} else if (id == ItemId.HALF_COCKTAIL_GLASS.id() || id == ItemId.FULL_COCKTAIL_GLASS.id()
			|| id == ItemId.ODD_LOOKING_COCKTAIL.id()) {
			handleCocktail(player, item);

		} else if (id == ItemId.FRUIT_BLAST.id() || id == ItemId.BLURBERRY_BARMAN_FRUIT_BLAST.id()
			|| id == ItemId.PINEAPPLE_PUNCH.id() || id == ItemId.BLURBERRY_BARMAN_PINEAPPLE_PUNCH.id()) {
			handleFruitCocktail(player, item);

		} else if (id == ItemId.BLURBERRY_SPECIAL.id() || id == ItemId.BLURBERRY_BARMAN_BLURBERRY_SPECIAL.id()
			|| id == ItemId.WIZARD_BLIZZARD.id() || id == ItemId.BLURBERRY_BARMAN_WIZARD_BLIZZARD.id()
			|| id == ItemId.SGG.id() || id == ItemId.BLURBERRY_BARMAN_SGG.id()
			|| id == ItemId.CHOCOLATE_SATURDAY.id() || id == ItemId.BLURBERRY_BARMAN_CHOCOLATE_SATURDAY.id()
			|| id == ItemId.DRUNK_DRAGON.id() || id == ItemId.BLURBERRY_BARMAN_DRUNK_DRAGON.id()) {
			handleSpecialCocktail(player, item);

		} else if (id == ItemId.BAD_OR_UNFERMENTED_WINE.id()) {
			if (player.getConfig().FERMENTED_WINE ||
				(player.getConfig().RESTRICT_ITEM_ID >= 0 && player.getConfig().RESTRICT_ITEM_ID < ItemId.CHEESE.id())) {
				// item can't be drunk since is treated unfermented wine
				return;
			}
			handleBadWine(player, item);
		}

		else if (id == ItemId.HALF_FULL_WINE_JUG.id() || id == ItemId.WINE.id())
			handleWine(player, item);

		else if (id == ItemId.CHOCOLATY_MILK.id())
			handleChocolatyMilk(player, item);

		else if (id == ItemId.CUP_OF_TEA.id())
			handleTea(player, item);

		else if (id == ItemId.BEER.id())
			handleBeer(player, item);

		else if (id == ItemId.GREENMANS_ALE.id())
			handleGreenmansAle(player, item);

		else if (id == ItemId.WIZARDS_MIND_BOMB.id())
			handleWizardsMindBomb(player, item);

		else if (id == ItemId.DWARVEN_STOUT.id())
			handleDwarvenStout(player, item);

		else if (id == ItemId.ASGARNIAN_ALE.id())
			handleAsgarnianAle(player, item);

		else if (id == ItemId.DRAGON_BITTER.id())
			handleDragonBitter(player, item);

		else if (id == ItemId.GROG.id())
			handleGrog(player, item);

		else if (id == ItemId.POISON_CHALICE.id())
			handlePoisonChalice(player, item);

		else if (id == ItemId.FULL_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 10, 3, ItemId.THREE_STRENGTH_POTION.id(), 3);

		else if (id == ItemId.THREE_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 10, 3, ItemId.TWO_STRENGTH_POTION.id(), 2);

		else if (id == ItemId.TWO_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 10, 3, ItemId.ONE_STRENGTH_POTION.id(), 1);

		else if (id == ItemId.ONE_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 10, 3, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 10, 3, ItemId.TWO_ATTACK_POTION.id(), 2);

		else if (id == ItemId.TWO_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 10, 3, ItemId.ONE_ATTACK_POTION.id(), 1);

		else if (id == ItemId.ONE_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 10, 3, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.TWO_STAT_RESTORATION_POTION.id(), 2);

		else if (id == ItemId.TWO_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.ONE_STAT_RESTORATION_POTION.id(), 1);

		else if (id == ItemId.ONE_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 10, 3, ItemId.TWO_DEFENSE_POTION.id(), 2);

		else if (id == ItemId.TWO_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 10, 3, ItemId.ONE_DEFENSE_POTION.id(), 1);

		else if (id == ItemId.ONE_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 10, 3, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.TWO_RESTORE_PRAYER_POTION.id(), 2);

		else if (id == ItemId.TWO_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.ONE_RESTORE_PRAYER_POTION.id(), 1);

		else if (id == ItemId.ONE_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 15, 5, ItemId.TWO_SUPER_ATTACK_POTION.id(), 2);

		else if (id == ItemId.TWO_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 15, 5, ItemId.ONE_SUPER_ATTACK_POTION.id(), 1);

		else if (id == ItemId.ONE_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 15, 5, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.TWO_FISHING_POTION.id(), 2);

		else if (id == ItemId.TWO_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.ONE_FISHING_POTION.id(), 1);

		else if (id == ItemId.ONE_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 15, 5, ItemId.TWO_SUPER_STRENGTH_POTION.id(), 2);

		else if (id == ItemId.TWO_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 15, 5, ItemId.ONE_SUPER_STRENGTH_POTION.id(), 1);

		else if (id == ItemId.ONE_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 15, 5, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 15, 5, ItemId.TWO_SUPER_DEFENSE_POTION.id(), 2);

		else if (id == ItemId.TWO_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 15, 5, ItemId.ONE_SUPER_DEFENSE_POTION.id(), 1);

		else if (id == ItemId.ONE_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 15, 5, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_RANGING_POTION.id())
			useNormalPotion(player, item, Skill.RANGED.id(), 10, 3, ItemId.TWO_RANGING_POTION.id(), 2);

		else if (id == ItemId.TWO_RANGING_POTION.id())
			useNormalPotion(player, item, Skill.RANGED.id(), 10, 3, ItemId.ONE_RANGING_POTION.id(), 1);

		else if (id == ItemId.ONE_RANGING_POTION.id())
			useNormalPotion(player, item, Skill.RANGED.id(), 10, 3, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_CURE_POISON_POTION.id())
			useCurePotion(player, item, ItemId.TWO_CURE_POISON_POTION.id(), 2);

		else if (id == ItemId.TWO_CURE_POISON_POTION.id())
			useCurePotion(player, item, ItemId.ONE_CURE_POISON_POTION.id(), 1);

		else if (id == ItemId.ONE_CURE_POISON_POTION.id())
			useCurePotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_POISON_ANTIDOTE.id())
			usePoisonAntidotePotion(player, item, ItemId.TWO_POISON_ANTIDOTE.id(), 2);

		else if (id == ItemId.TWO_POISON_ANTIDOTE.id())
			usePoisonAntidotePotion(player, item, ItemId.ONE_POISON_ANTIDOTE.id(), 1);

		else if (id == ItemId.ONE_POISON_ANTIDOTE.id())
			usePoisonAntidotePotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_POTION_OF_ZAMORAK.id())
			useZamorakPotion(player, item, ItemId.TWO_POTION_OF_ZAMORAK.id(), 2);

		else if (id == ItemId.TWO_POTION_OF_ZAMORAK.id())
			useZamorakPotion(player, item, ItemId.ONE_POTION_OF_ZAMORAK.id(), 1);

		else if (id == ItemId.ONE_POTION_OF_ZAMORAK.id())
			useZamorakPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else {
			Skill[] magicSkills = player.getSkills().getMagicSkills();
			int[] magicStat = new int[magicSkills.length];

			for (int i = 0; i < magicSkills.length; i++) {
				magicStat[i] = magicSkills[i].id();
			}
			// XXX should consider moving these to inauthentic plugin class
			if (id == ItemId.FULL_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 10).toArray(), IntStream.of(magicStat).map(x -> 3).toArray(), ItemId.TWO_MAGIC_POTION.id(), 2);

			else if (id == ItemId.TWO_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 10).toArray(), IntStream.of(magicStat).map(x -> 3).toArray(), ItemId.ONE_MAGIC_POTION.id(), 1);

			else if (id == ItemId.ONE_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 10).toArray(), IntStream.of(magicStat).map(x -> 3).toArray(), ItemId.EMPTY_VIAL.id(), 0);

			else if (id == ItemId.FULL_SUPER_RANGING_POTION.id())
				useNormalPotion(player, item, Skill.RANGED.id(), 15, 5, ItemId.TWO_SUPER_RANGING_POTION.id(), 2);

			else if (id == ItemId.TWO_SUPER_RANGING_POTION.id())
				useNormalPotion(player, item, Skill.RANGED.id(), 15, 5, ItemId.ONE_SUPER_RANGING_POTION.id(), 1);

			else if (id == ItemId.ONE_SUPER_RANGING_POTION.id())
				useNormalPotion(player, item, Skill.RANGED.id(), 15, 5, ItemId.EMPTY_VIAL.id(), 0);

			else if (id == ItemId.FULL_SUPER_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 15).toArray(), IntStream.of(magicStat).map(x -> 5).toArray(), ItemId.TWO_SUPER_MAGIC_POTION.id(), 2);

			else if (id == ItemId.TWO_SUPER_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 15).toArray(), IntStream.of(magicStat).map(x -> 5).toArray(), ItemId.ONE_SUPER_MAGIC_POTION.id(), 1);

			else if (id == ItemId.ONE_SUPER_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 15).toArray(), IntStream.of(magicStat).map(x -> 5).toArray(), ItemId.EMPTY_VIAL.id(), 0);

			else
				player.message("Nothing interesting happens");
		}
	}

	private static void tryGiveBeerGlass(Player player) {
		if ((player.getConfig().RESTRICT_ITEM_ID >= 0 && player.getConfig().RESTRICT_ITEM_ID < ItemId.BEER_GLASS.id())
			|| player.getClientLimitations().maxItemId < ItemId.BEER_GLASS.id())
			return;
		give(player, ItemId.BEER_GLASS.id(), 1);
	}

	private void useFishingPotion(Player player, final Item item, final int newItem, final int left) {
		int affectedStat = Skill.FISHING.id();
		if (player.getConfig().WAIT_TO_REBOOST && isstatup(player, affectedStat)) {
			player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
			return;
		}

		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().getInventory().add(new Item(newItem));
		// Constant increase by 3 Fishing
		addstat(player, Skill.FISHING.id(), 3, 0);
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " doses of potion left");
		}
	}

	private void useCurePotion(Player player, final Item item, final int newItem, final int dosesLeft) {
		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().getInventory().add(new Item(newItem));
		player.cure();
		player.setCurePoisonProtection(); // 3 minutes.
		delay(2);
		if (dosesLeft <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + dosesLeft + " doses of potion left");
		}
	}

	private void usePoisonAntidotePotion(Player player, final Item item, final int newItem, final int dosesLeft) {
		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase() + " potion");
		player.getCarriedItems().getInventory().add(new Item(newItem));
		player.cure();
		player.setAntidoteProtection(); // 6 minutes.
		delay(2);
		if (dosesLeft <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + dosesLeft + " doses of potion left");
		}
	}

	private void useNormalPotion(Player player, final Item item, final int[] affectedStats, final int[] percentageIncreases, final int[] modifiers, final int newItem, final int left) {
		for (int affectedStat : affectedStats) {
			if (player.getConfig().WAIT_TO_REBOOST && isstatup(player, affectedStat)) {
				player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
				return;
			}
		}

		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());

		for (int i=0; i < affectedStats.length; i++) {
			addstat(player, affectedStats[i], modifiers[i], percentageIncreases[i]);
		}

		player.getCarriedItems().getInventory().add(new Item(newItem));
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useNormalPotion(Player player, final Item item, final int affectedStat, final int percentageIncrease, final int modifier, final int newItem, final int left) {

		final String skillName;

		if (affectedStat == Skill.RANGED.id()) {
			skillName = "ranging";
		} else {
			skillName = player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat).toLowerCase();
		}

		if (player.getConfig().WAIT_TO_REBOOST && isstatup(player, affectedStat)) {
			player.playerServerMessage(MessageType.QUEST, "You already have boosted " + skillName);
			return;
		}

		if (player.getCarriedItems().remove(item) == -1) return;
		player.message(String.format("You drink some of your %s potion", skillName));

		addstat(player, affectedStat, modifier, percentageIncrease);

		player.getCarriedItems().getInventory().add(new Item(newItem));
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useZamorakPotion(Player player, final Item item, final int newItem, final int left) {
		int[] boostStats = {Skill.ATTACK.id(), Skill.STRENGTH.id()};
		for (int affectedStat : boostStats) {
			if (player.getConfig().WAIT_TO_REBOOST && isstatup(player, affectedStat)) {
				player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
				return;
			}
		}

		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink some of the foul liquid");
		player.getCarriedItems().getInventory().add(new Item(newItem));
		boolean isLastDose = item.getCatalogId() == ItemId.ONE_POTION_OF_ZAMORAK.id();
		int[] commonAffectedStats = {Skill.ATTACK.id(),
			Skill.DEFENSE.id(),
			Skill.STRENGTH.id(),
			Skill.HITS.id()};
		Skill[] prayerSkills = player.getSkills().getPrayerSkills();
		int[] prayerStats = new int[prayerSkills.length];
		for (int i = 0; i < prayerSkills.length; i++) {
			prayerStats[i] = prayerSkills[i].id();
		}
		int[] affectedStats = concat(commonAffectedStats, prayerStats);
		int[] percentageIncrease = concat(new int[]{20, -10, 12, -10}, IntStream.of(prayerStats).map(x -> 10).toArray());
		final int[] modifier;
		if (isLastDose) {
			modifier = concat(new int[]{2, -2, 2, 0}, IntStream.of(prayerStats).map(x -> 0).toArray());
		} else {
			modifier = concat(new int[]{4, -4, 2, 0}, IntStream.of(prayerStats).map(x -> 0).toArray());
		}

		for (int i=0; i<affectedStats.length; i++) {
			boolean isBoost = percentageIncrease[i] >= 0;
			if (isBoost) {
				addstat(player, affectedStats[i], modifier[i], percentageIncrease[i]);
			} else {
				substat(player, affectedStats[i], -modifier[i], -percentageIncrease[i]);
			}
		}

		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private static void usePrayerPotion(Player player, final Item item, final int newItem, final int left) {
		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().getInventory().add(new Item(newItem));

		Skill[] prayerSkills = player.getSkills().getPrayerSkills();
		for (Skill prayerStat : prayerSkills) {
			// Restore prayer by 25% + 7
			healstat(player, prayerStat.id() , 7, 25);
		}

		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private static void useStatRestorePotion(Player player, final Item item, final int newItem, final int left) {
		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().getInventory().add(new Item(newItem));
		// In RSC stat restore potion is only applicable for Attack, Strength, and Defense
		int[] affectedStats = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id()};
		for (int i = 0; i < affectedStats.length; i++) {
			// Restore by 30% + 10
			healstat(player, affectedStats[i], 10, 30);
		}
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private static void handleSpirits(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;

		mes("You drink the " + item.getDef(player.getWorld()).getName().toLowerCase());
		mes("You feel slightly reinvigorated");
		mes("And slightly dizzy too");

		// Need more data. Likely would have scaled with level.
		if (item.getCatalogId() == ItemId.WHISKY.id())
			substat(player, Skill.ATTACK.id(), 6, 0);
		else
			substat(player, Skill.ATTACK.id(), 3, 0);
		addstat(player, Skill.STRENGTH.id(), 5, 0);
		healstat(player, Skill.HITS.id(), 4, 0);
	}

	private static void handleCocktail(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;

		// Need more data. Likely would have scaled with level.
		substat(player, Skill.ATTACK.id(), 3, 0);
		substat(player, Skill.DEFENSE.id(), 1, 0);
		substat(player, Skill.STRENGTH.id(), 4, 0);

		mes("You drink the cocktail");
		mes("It tastes awful..yuck");
		player.getCarriedItems().getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
		resetGnomeBartending(player);
	}

	private static void handleFruitCocktail(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		if (item.getCatalogId() == ItemId.PINEAPPLE_PUNCH.id()) {
			healstat(player, Skill.HITS.id(), 9, 0);
		} else {
			healstat(player, Skill.HITS.id(), 8, 0);
		}
		mes("You drink the cocktail");
		mes("yum ..it tastes great");
		mes("You feel reinvigorated");
		give(player, ItemId.COCKTAIL_GLASS.id(), 1);
	}

	private static void handleSpecialCocktail(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;

		// heal constant 5 - needs checking
		healstat(player, Skill.HITS.id(), 5, 0);
		// removes 3% + 1 from attack
		substat(player, Skill.ATTACK.id(), 1, 3);
		// adds 6% + 1 to strength
		addstat(player, Skill.STRENGTH.id(), 1, 6);

		mes("You drink the cocktail");
		mes("yum ..it tastes great");
		mes("although you feel slightly dizzy");
		give(player, ItemId.COCKTAIL_GLASS.id(), 1);
	}

	private static void handleBadWine(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink the bad wine");
		thinkbubble(item);

		give(player, ItemId.JUG.id(), 1);

		// removes constant 3
		substat(player, Skill.ATTACK.id(), 3, 0);
		delay(2);
		player.message("You start to feel sick");
	}

	private static void handleWine(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		thinkbubble(item);
		mes("You drink the wine");
		mes("It makes you feel a bit dizzy");
		// wine used to be two dose likely before the cooking update of 11 June 2001
		boolean twoDoseWine = player.getConfig().RESTRICT_ITEM_ID >= 0 && player.getConfig().RESTRICT_ITEM_ID < ItemId.CHEESE.id();
		//half-wine set to 1/25k chance
		int rand = DataConversions.random(0, 25000);
		boolean isFullWine = item.getCatalogId() == ItemId.WINE.id();
		if (isFullWine && (twoDoseWine || rand == 0)) {
			give(player, ItemId.HALF_FULL_WINE_JUG.id(), 1);
		} else {
			give(player, ItemId.JUG.id(), 1);
		}
		int healAmount = !isFullWine || twoDoseWine ? 5 : 11;
		int lowerAmount = !isFullWine || twoDoseWine ? 1 : 3;
		healstat(player, Skill.HITS.id(), healAmount, 0);
		substat(player, Skill.ATTACK.id(), lowerAmount, 0);
	}

	private static void handleChocolatyMilk(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		thinkbubble(item);
		player.message("You drink the chocolaty milk");
		give(player, ItemId.BUCKET.id(), 1);
		// heal constant 4
		healstat(player, Skill.HITS.id(), 4, 0);
	}

	private static void handleTea(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		thinkbubble(item);
		// authentic does not send to quest tab
		player.message("You drink the cup of tea");

		// heal 2% plus 2 to hp
		// if below 50 hits add 3, otherwise add 2
		healstat(player, Skill.HITS.id(), 2, 2);

		// add 2% plus 2 levels to attack
		// if below 50 attack add 2, otherwise add 3
		addstat(player, Skill.ATTACK.id(), 2, 2);
	}

	private static void handleBeer(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		thinkbubble(item);
		mes("You drink the beer");
		mes("You feel slightly reinvigorated");
		mes("And slightly dizzy too");
		tryGiveBeerGlass(player);

		// drain 5% + 1 from attack
		substat(player, Skill.ATTACK.id(), 1, 5);

		// add 2 (constant - needs checking) to strength
		addstat(player, Skill.STRENGTH.id(), 2, 0);

		// heal 1
		healstat(player, Skill.HITS.id(), 1, 0);
	}

	private static void handleGreenmansAle(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		thinkbubble(item);
		mes("You drink the greenmans ale");
		tryGiveBeerGlass(player);
		delay(2);
		mes("It has a strange taste");

		// remove 5% from all melee stats
		substat(player, Skill.ATTACK.id(), 0, 5);
		substat(player, Skill.DEFENSE.id(), 0, 5);

		// only add 1 (constant) to herblaw
		addstat(player, Skill.HERBLAW.id(), 1, 0);

		// heal 1
		healstat(player, Skill.HITS.id(), 1, 0);
	}

	private static void handleWizardsMindBomb(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		thinkbubble(item);
		mes("you drink the Wizard's Mind Bomb");
		tryGiveBeerGlass(player);
		delay(2);
		mes("You feel very strange");

		// remove 5% from all melee stats
		substat(player, Skill.ATTACK.id(), 0, 5);
		substat(player, Skill.DEFENSE.id(), 0, 5);
		substat(player, Skill.STRENGTH.id(), 0, 5);

		Skill[] magicSkills = player.getSkills().getMagicSkills();
		for (Skill magicStat: magicSkills) {
			// add 2% plus 2 levels
			// if below 50 magic add 2, otherwise add 3
			addstat(player, magicStat.id(), 2, 2);
		}

		// heal 1
		healstat(player, Skill.HITS.id(), 1, 0);
	}

	private static void handleDwarvenStout(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		thinkbubble(item);
		mes("You drink the Dwarven Stout");
		mes("It tastes foul");
		tryGiveBeerGlass(player);
		delay(3);
		mes("It tastes pretty strong too");

		// remove 5% from all melee stats
		substat(player, Skill.ATTACK.id(), 0, 5);
		substat(player, Skill.DEFENSE.id(), 0, 5);
		substat(player, Skill.STRENGTH.id(), 0, 5);

		// add 1 to mining and smithing (constant)
		addstat(player, Skill.SMITHING.id(), 1, 0);
		addstat(player, Skill.MINING.id(), 1, 0);

		// heal 1
		healstat(player, Skill.HITS.id(), 1, 0);
	}

	private static void handleAsgarnianAle(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		mes("You drink the Ale");
		thinkbubble(item);
		tryGiveBeerGlass(player);
		delay(2);
		mes("You feel slightly reinvigorated");
		mes("And slightly dizzy too");

		// add 5% + 1 to attack
		substat(player, Skill.ATTACK.id(), 1, 5);

		// add constant 2 strength (needs checking, might have been 1 at low levels)
		addstat(player, Skill.STRENGTH.id(), 2, 0);

		// heal 1
		healstat(player, Skill.HITS.id(), 1, 0);
	}

	private static void handleDragonBitter(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		mes("You drink the Dragon bitter");
		tryGiveBeerGlass(player);
		thinkbubble(item);
		delay(2);
		mes("You feel slightly reinvigorated");
		mes("And slightly dizzy too");

		// add 5% + 1 to attack
		substat(player, Skill.ATTACK.id(), 1, 5);

		// add constant 2 strength (needs checking, might have been 1 at low levels)
		addstat(player, Skill.STRENGTH.id(), 2, 0);

		// heal 1
		healstat(player, Skill.HITS.id(), 1, 0);
	}

	private static void handleGrog(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		mes("You drink the Grog");
		thinkbubble(item);
		tryGiveBeerGlass(player);
		delay(2);
		mes("You feel slightly reinvigorated");
		mes("And slightly dizzy too");

		// XXX: all needs checking - probably should be the same as other regional ales

		// remove constant 6 strength
		substat(player, Skill.ATTACK.id(), 6, 0);

		// add constant 3 strength
		addstat(player, Skill.STRENGTH.id(), 3, 0);

		// heal 3
		healstat(player, Skill.HITS.id(), 3, 0);
	}

	private static void handlePoisonChalice(Player player, Item item) {
		if (player.getCarriedItems().remove(item) == -1) return;
		int chance = DataConversions.random(0, 5);
		int needs;
		switch (chance) {
			case 0: // Hits -1 or -3
				substat(player, Skill.HITS.id(), DataConversions.random(0, 1) == 0 ? 1 : 3, 0);
				player.message("That tasted a bit dodgy. You feel a bit ill");
				break;
			case 1: // Hits +5%
				healstat(player, Skill.HITS.id(), 0, 5);
				player.message("It heals some health");
				break;
			case 2: // Crafting +1 Attack & Defence -1
				addstat(player, Skill.CRAFTING.id(), 1, 0);
				substat(player, Skill.ATTACK.id(), 1, 0);
				substat(player, Skill.DEFENSE.id(), 1, 0);
				player.message("You feel a little strange");
				break;
			case 3: // Hits +15% Thieving + 1
				healstat(player, Skill.HITS.id(), 0, 15);
				addstat(player, Skill.THIEVING.id(), 1, 0);
				player.message("You feel a lot better");
				break;
			case 4: // Hits +30% Attack, Defence, Strength +4
				healstat(player, Skill.HITS.id(), 0, 30);
				addstat(player, Skill.ATTACK.id(), 4, 0);
				addstat(player, Skill.STRENGTH.id(), 4, 0);
				addstat(player, Skill.DEFENSE.id(), 4, 0);
				player.message("Wow that was an amazing!! You feel really invigorated");
				break;
			case 5: // No effect
				player.message("It has a slight taste of apricot");
				break;
		}
	}
}
