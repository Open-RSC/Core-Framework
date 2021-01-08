package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Drinkables implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return command.equalsIgnoreCase("drink");
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getItemStatus().getNoted()) {
			return;
		}
		int id = item.getCatalogId();
		if (id == ItemId.GUJUO_POTION.id())
			handleGujouPotion(player);

		else if (id == ItemId.BRANDY.id() || id == ItemId.VODKA.id()
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

		} else if (id == ItemId.BAD_WINE.id())
			handleBadWine(player, item);

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
			useNormalPotion(player, item, Skills.STRENGTH, 10, 2, ItemId.THREE_STRENGTH_POTION.id(), 3);

		else if (id == ItemId.THREE_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skills.STRENGTH, 10, 2, ItemId.TWO_STRENGTH_POTION.id(), 2);

		else if (id == ItemId.TWO_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skills.STRENGTH, 10, 2, ItemId.ONE_STRENGTH_POTION.id(), 1);

		else if (id == ItemId.ONE_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skills.STRENGTH, 10, 2, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_ATTACK_POTION.id())
			useNormalPotion(player, item, Skills.ATTACK, 10, 2, ItemId.TWO_ATTACK_POTION.id(), 2);

		else if (id == ItemId.TWO_ATTACK_POTION.id())
			useNormalPotion(player, item, Skills.ATTACK, 10, 2, ItemId.ONE_ATTACK_POTION.id(), 1);

		else if (id == ItemId.ONE_ATTACK_POTION.id())
			useNormalPotion(player, item, Skills.ATTACK, 10, 2, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.TWO_STAT_RESTORATION_POTION.id(), 2);

		else if (id == ItemId.TWO_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.ONE_STAT_RESTORATION_POTION.id(), 1);

		else if (id == ItemId.ONE_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skills.DEFENSE, 10, 2, ItemId.TWO_DEFENSE_POTION.id(), 2);

		else if (id == ItemId.TWO_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skills.DEFENSE, 10, 2, ItemId.ONE_DEFENSE_POTION.id(), 1);

		else if (id == ItemId.ONE_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skills.DEFENSE, 10, 2, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.TWO_RESTORE_PRAYER_POTION.id(), 2);

		else if (id == ItemId.TWO_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.ONE_RESTORE_PRAYER_POTION.id(), 1);

		else if (id == ItemId.ONE_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, Skills.ATTACK, 15, 4, ItemId.TWO_SUPER_ATTACK_POTION.id(), 2);

		else if (id == ItemId.TWO_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, Skills.ATTACK, 15, 4, ItemId.ONE_SUPER_ATTACK_POTION.id(), 1);

		else if (id == ItemId.ONE_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, Skills.ATTACK, 15, 4, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.TWO_FISHING_POTION.id(), 2);

		else if (id == ItemId.TWO_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.ONE_FISHING_POTION.id(), 1);

		else if (id == ItemId.ONE_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skills.STRENGTH, 15, 4, ItemId.TWO_SUPER_STRENGTH_POTION.id(), 2);

		else if (id == ItemId.TWO_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skills.STRENGTH, 15, 4, ItemId.ONE_SUPER_STRENGTH_POTION.id(), 1);

		else if (id == ItemId.ONE_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skills.STRENGTH, 15, 4, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skills.DEFENSE, 15, 4, ItemId.TWO_SUPER_DEFENSE_POTION.id(), 2);

		else if (id == ItemId.TWO_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skills.DEFENSE, 15, 4, ItemId.ONE_SUPER_DEFENSE_POTION.id(), 1);

		else if (id == ItemId.ONE_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skills.DEFENSE, 15, 4, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_RANGING_POTION.id())
			useNormalPotion(player, item, Skills.RANGED, 10, 3, ItemId.TWO_RANGING_POTION.id(), 2);

		else if (id == ItemId.TWO_RANGING_POTION.id())
			useNormalPotion(player, item, Skills.RANGED, 10, 3, ItemId.ONE_RANGING_POTION.id(), 1);

		else if (id == ItemId.ONE_RANGING_POTION.id())
			useNormalPotion(player, item, Skills.RANGED, 10, 3, ItemId.EMPTY_VIAL.id(), 0);

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

		else if (id == ItemId.GLASS_MILK.id())
			handleGlassMilk(player, item);

		else {
			if (config().WANT_RUNECRAFT) {
				if (id == ItemId.FULL_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.TWO_RUNECRAFT_POTION.id(), false, 2);

				else if (id == ItemId.TWO_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.ONE_RUNECRAFT_POTION.id(), false, 1);

				else if (id == ItemId.ONE_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.EMPTY_VIAL.id(), false, 0);

				else if (id == ItemId.FULL_SUPER_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.TWO_SUPER_RUNECRAFT_POTION.id(), true, 2);

				else if (id == ItemId.TWO_SUPER_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.ONE_SUPER_RUNECRAFT_POTION.id(), true, 1);

				else if (id == ItemId.ONE_SUPER_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.EMPTY_VIAL.id(), true, 0);
			}

			if (id == ItemId.FULL_MAGIC_POTION.id())
				useNormalPotion(player, item, Skills.MAGIC, 10, 3, ItemId.TWO_MAGIC_POTION.id(), 2);

			else if (id == ItemId.TWO_MAGIC_POTION.id())
				useNormalPotion(player, item, Skills.MAGIC, 10, 3, ItemId.ONE_MAGIC_POTION.id(), 1);

			else if (id == ItemId.ONE_MAGIC_POTION.id())
				useNormalPotion(player, item, Skills.MAGIC, 10, 3, ItemId.EMPTY_VIAL.id(), 0);

			else if (id == ItemId.FULL_SUPER_RANGING_POTION.id())
				useNormalPotion(player, item, Skills.RANGED, 15, 4, ItemId.TWO_SUPER_RANGING_POTION.id(), 2);

			else if (id == ItemId.TWO_SUPER_RANGING_POTION.id())
				useNormalPotion(player, item, Skills.RANGED, 15, 4, ItemId.ONE_SUPER_RANGING_POTION.id(), 1);

			else if (id == ItemId.ONE_SUPER_RANGING_POTION.id())
				useNormalPotion(player, item, Skills.RANGED, 15, 4, ItemId.EMPTY_VIAL.id(), 0);

			else if (id == ItemId.FULL_SUPER_MAGIC_POTION.id())
				useNormalPotion(player, item, Skills.MAGIC, 15, 4, ItemId.TWO_SUPER_MAGIC_POTION.id(), 2);

			else if (id == ItemId.TWO_SUPER_MAGIC_POTION.id())
				useNormalPotion(player, item, Skills.MAGIC, 15, 4, ItemId.ONE_SUPER_MAGIC_POTION.id(), 1);

			else if (id == ItemId.ONE_SUPER_MAGIC_POTION.id())
				useNormalPotion(player, item, Skills.MAGIC, 15, 4, ItemId.EMPTY_VIAL.id(), 0);

			else if (id == ItemId.FULL_POTION_OF_SARADOMIN.id())
				useSaradominPotion(player, item, ItemId.TWO_POTION_OF_SARADOMIN.id(), 2);

			else if (id == ItemId.TWO_POTION_OF_SARADOMIN.id())
				useSaradominPotion(player, item, ItemId.ONE_POTION_OF_SARADOMIN.id(), 1);

			else if (id == ItemId.ONE_POTION_OF_SARADOMIN.id())
				useSaradominPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);
			else
				player.message("Nothing interesting happens");
		}
	}

	private void useFishingPotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		player.getSkills().setLevel(Skills.FISHING, player.getSkills().getMaxStat(Skills.FISHING) + 3);
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " doses of potion left");
		}
	}

	private void useCurePotion(Player player, final Item item, final int newItem, final int dosesLeft) {
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		player.cure();
		delay(2);
		if (dosesLeft <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + dosesLeft + " doses of potion left");
		}
	}

	private void usePoisonAntidotePotion(Player player, final Item item, final int newItem, final int dosesLeft) {
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase() + " potion");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		player.cure();
		player.setAntidoteProtection(); // 90 seconds.
		delay(2);
		if (dosesLeft <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + dosesLeft + " doses of potion left");
		}
	}

	private void useNormalPotion(Player player, final Item item, final int affectedStat, final int percentageIncrease, final int modifier, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		int baseStat = player.getSkills().getLevel(affectedStat) > player.getSkills().getMaxStat(affectedStat) ? player.getSkills().getMaxStat(affectedStat) : player.getSkills().getLevel(affectedStat);
		int newStat = baseStat
			+ DataConversions.roundUp((player.getSkills().getMaxStat(affectedStat) / 100D) * percentageIncrease)
			+ modifier;
		if (newStat > player.getSkills().getLevel(affectedStat)) {
			player.getSkills().setLevel(affectedStat, newStat);
		}
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useZamorakPotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of the foul liquid");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		boolean isLastDose = item.getCatalogId() == ItemId.ONE_POTION_OF_ZAMORAK.id();
		int[] affectedStats = {Skills.ATTACK, Skills.DEFENSE, Skills.STRENGTH, Skills.HITS, Skills.PRAYER};
		int[] percentageIncrease = {20, -10, 12, -10, 10};
		int[] modifier = {1, -1, 1, 0, 0};
		if (isLastDose) {
			for (int i=0; i<5; i++) modifier[i] *= 3;
		}

		for (int i=0; i<affectedStats.length; i++) {
			boolean isBoost = percentageIncrease[i] >= 0;
			if (isBoost) {
				int baseStat = player.getSkills().getLevel(affectedStats[i]) > player.getSkills().getMaxStat(affectedStats[i]) ? player.getSkills().getMaxStat(affectedStats[i]) : player.getSkills().getLevel(affectedStats[i]);
				int newStat = baseStat
					+ DataConversions.roundUp((player.getSkills().getMaxStat(affectedStats[i]) / 100D) * percentageIncrease[i])
					+ modifier[i];
				newStat = affectedStats[i] != Skills.PRAYER ? newStat : Math.min(newStat, player.getSkills().getMaxStat(Skills.PRAYER));
				if (newStat > player.getSkills().getLevel(affectedStats[i])) {
					player.getSkills().setLevel(affectedStats[i], newStat);
				}
			} else {
				int baseStat = player.getSkills().getLevel(affectedStats[i]) < player.getSkills().getMaxStat(affectedStats[i]) ? player.getSkills().getMaxStat(affectedStats[i]) : player.getSkills().getLevel(affectedStats[i]);
				int newStat = baseStat
					- DataConversions.roundUp((player.getSkills().getMaxStat(affectedStats[i]) / 100D) * -1 * percentageIncrease[i])
					- (-1 * modifier[i]);
				if (newStat < player.getSkills().getLevel(affectedStats[i])) {
					player.getSkills().setLevel(affectedStats[i], newStat);
				}
			}
		}

		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useSaradominPotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of the cleansed liquid");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		boolean isLastDose = item.getCatalogId() == ItemId.ONE_POTION_OF_SARADOMIN.id();
		int[] affectedStats = {Skills.ATTACK, Skills.DEFENSE, Skills.STRENGTH, Skills.HITS, Skills.RANGED, Skills.MAGIC};
		int[] percentageIncrease = {-10, 20, -10, 15, -10, -10};
		int[] modifier = {-1, 1, -1, 1, -1, -1};
		if (isLastDose) {
			for (int i=0; i<6; i++) modifier[i] *= 3;
		}

		for (int i=0; i<affectedStats.length; i++) {
			boolean isBoost = percentageIncrease[i] >= 0;
			if (isBoost) {
				int baseStat = player.getSkills().getLevel(affectedStats[i]) > player.getSkills().getMaxStat(affectedStats[i]) ? player.getSkills().getMaxStat(affectedStats[i]) : player.getSkills().getLevel(affectedStats[i]);
				int newStat = baseStat
					+ DataConversions.roundUp((player.getSkills().getMaxStat(affectedStats[i]) / 100D) * percentageIncrease[i])
					+ modifier[i];
				newStat = affectedStats[i] != Skills.PRAYER ? newStat : Math.min(newStat, player.getSkills().getMaxStat(Skills.PRAYER));
				if (newStat > player.getSkills().getLevel(affectedStats[i])) {
					player.getSkills().setLevel(affectedStats[i], newStat);
				}
			} else {
				int baseStat = player.getSkills().getLevel(affectedStats[i]) < player.getSkills().getMaxStat(affectedStats[i]) ? player.getSkills().getMaxStat(affectedStats[i]) : player.getSkills().getLevel(affectedStats[i]);
				int newStat = baseStat
					- DataConversions.roundUp((player.getSkills().getMaxStat(affectedStats[i]) / 100D) * -1 * percentageIncrease[i])
					- (-1 * modifier[i]);
				if (newStat < player.getSkills().getLevel(affectedStats[i])) {
					player.getSkills().setLevel(affectedStats[i], newStat);
				}
			}
		}

		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void usePrayerPotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		int newPrayer = player.getSkills().getLevel(Skills.PRAYER) + (int) ((player.getSkills().getMaxStat(Skills.PRAYER) * 0.25) + 7);
		if (newPrayer > player.getSkills().getMaxStat(Skills.PRAYER)) {
			newPrayer = player.getSkills().getMaxStat(Skills.PRAYER);
		}
		player.getSkills().setLevel(Skills.PRAYER, newPrayer);
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useStatRestorePotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		for (int i = Skills.ATTACK; i < Skills.COOKING; i++) {
			if (i == Skills.HITS || i == Skills.PRAYER) {
				continue;
			}
			if (player.getSkills().getLevel(i) > player.getSkills().getMaxStat(i)) {
				continue;
			}
			int newStat = player.getSkills().getLevel(i) + (int) ((player.getSkills().getMaxStat(i) * 0.3) + 10);
			if (newStat > player.getSkills().getMaxStat(i)) {
				newStat = player.getSkills().getMaxStat(i);
			}
			if (newStat < 14) {
				player.getSkills().setLevel(i, player.getSkills().getMaxStat(i));
			} else {
				player.getSkills().setLevel(i, newStat);
			}
		}
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useRunecraftPotion(Player player, final Item item, final int newItem, final boolean superPot, final int left) {
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		int newStat = Math.min(player.getSkills().getLevel(Skills.RUNECRAFT), player.getSkills().getMaxStat(Skills.RUNECRAFT)) + (superPot ? 6 : 3);
		player.getSkills().setLevel(Skills.RUNECRAFT, newStat);
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " doses of potion left");
		}
	}

	private void handleGujouPotion(Player player) {
		player.message("Are you sure you want to drink this?");
		int drink = multi(player,
			"Yes, I'm sure...",
			"No, I've had second thoughts...");
		if (drink == 0) {
			player.message("You drink the potion...");
			player.getCarriedItems().remove(new Item(ItemId.GUJUO_POTION.id()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.EMPTY_VIAL.id()));
			if (!player.getCache().hasKey("gujuo_potion")) {
				player.getCache().store("gujuo_potion", true);
			}
			say(player, null, "Mmmm.....");
			delay(2);
			player.message("It tastes sort of strange...like fried oranges...");
			say(player, null, ".....!.....");
			delay(2);
			mes("You feel somehow different...");
			delay(2);
			say(player, null, "Let's just hope that this isn't a placibo!");
		} else if (drink == 1) {
			player.message("You decide against drinking the potion...");
		}
	}

	private void handleSpirits(Player player, Item item) {
		player.playerServerMessage(MessageType.QUEST, "You drink the " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		if (item.getCatalogId() == ItemId.WHISKY.id())
			player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 6);
		else
			player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 3);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 5);
		}
		final boolean heals = player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS);
		if (heals) {
			int newHp = player.getSkills().getLevel(Skills.HITS) + 4;
			if (newHp > player.getSkills().getMaxStat(Skills.HITS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newHp);
		}
		player.getCarriedItems().remove(item);
	}

	private void handleCocktail(Player player, Item item) {
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 3);
		player.getSkills().setLevel(Skills.DEFENSE, player.getSkills().getLevel(Skills.DEFENSE) - 1);
		player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) - 4);
		player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
		player.playerServerMessage(MessageType.QUEST, "It tastes awful..yuck");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
		resetGnomeBartending(player);
	}

	private void handleFruitCocktail(Player player, Item item) {
		if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newHp = player.getSkills().getLevel(Skills.HITS) + 8
					+ (item.getCatalogId() == ItemId.PINEAPPLE_PUNCH.id() ? 1 : 0);
			if (newHp > player.getSkills().getMaxStat(Skills.HITS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newHp);
		}
		player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
		player.playerServerMessage(MessageType.QUEST, "yum ..it tastes great");
		player.playerServerMessage(MessageType.QUEST, "You feel reinvigorated");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
	}

	private void handleSpecialCocktail(Player player, Item item) {
		if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newHp = player.getSkills().getLevel(Skills.HITS) + 5;
			if (newHp > player.getSkills().getMaxStat(Skills.HITS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newHp);
		}
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 4);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 6);
		}
		player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
		player.playerServerMessage(MessageType.QUEST, "yum ..it tastes great");
		player.playerServerMessage(MessageType.QUEST, "although you feel slightly dizzy");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
	}

	private void handleBadWine(Player player, Item item) {
		player.message("You drink the bad wine");
		thinkbubble(item);

		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.JUG.id()));

		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 3);
		delay(2);
		player.message("You start to feel sick");
	}

	private void handleWine(Player player, Item item) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "You drink the wine");
		player.playerServerMessage(MessageType.QUEST, "It makes you feel a bit dizzy");
		player.getCarriedItems().remove(item);
		//half-wine set to 1/25k chance
		int rand = DataConversions.random(0, 25000);
		if (item.getCatalogId() == ItemId.WINE.id() && rand == 0) {
			player.getCarriedItems().getInventory().add(new Item(ItemId.HALF_FULL_WINE_JUG.id()));
		} else {
			player.getCarriedItems().getInventory().add(new Item(ItemId.JUG.id()));
		}
		if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newStat = player.getSkills().getLevel(Skills.HITS) + 11;
			if (newStat > player.getSkills().getMaxStat(Skills.HITS)) {
				newStat = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newStat);
		}
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 3);
	}

	private void handleChocolatyMilk(Player player, Item item) {
		thinkbubble(item);
		player.message("You drink the chocolaty milk");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BUCKET.id()));
		if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newHp = player.getSkills().getLevel(Skills.HITS) + 4;
			if (newHp > player.getSkills().getMaxStat(Skills.HITS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newHp);
		}
	}

	private void handleGlassMilk(Player player, Item item) {
		thinkbubble(item);
		player.message("You drink the cold milk");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEVERAGE_GLASS.id()));
		if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newHp = player.getSkills().getLevel(Skills.HITS) + 2;
			if (newHp > player.getSkills().getMaxStat(Skills.HITS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newHp);
		}
	}

	private void handleTea(Player player, Item item) {
		thinkbubble(item);
		// authentic does not send to quest tab
		player.message("You drink the cup of tea");
		player.getCarriedItems().remove(item);
		int changeHp = (player.getSkills().getMaxStat(Skills.HITS) > 55 ? 3 : 2);
		if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newHp = player.getSkills().getLevel(Skills.HITS) + changeHp;
			if (newHp > player.getSkills().getMaxStat(Skills.HITS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newHp);
		}
		int changeAtt = (player.getSkills().getMaxStat(Skills.ATTACK) > 55 ? 3 : 2);
		int maxWithTea = (player.getSkills().getMaxStat(Skills.ATTACK) + changeAtt);
		if (maxWithTea - player.getSkills().getLevel(Skills.ATTACK) < changeAtt) {
			changeAtt = maxWithTea - player.getSkills().getLevel(Skills.ATTACK);
		}
		if (player.getSkills().getLevel(Skills.ATTACK) <=
				(player.getSkills().getMaxStat(Skills.ATTACK) + (player.getSkills().getMaxStat(Skills.ATTACK) > 55 ? 3 : 2))) {
			player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) + changeAtt);
		}
	}

	private void handleBeer(Player player, Item item) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "You drink the beer");
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 4);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 2);
		}
		if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newStat = player.getSkills().getLevel(Skills.HITS) + 1;
			if (newStat > player.getSkills().getMaxStat(Skills.HITS)) {
				newStat = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newStat);
		}
	}

	private void handleGreenmansAle(Player player, Item item) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "You drink the greenmans ale");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "It has a strange taste");
		for (int stat = Skills.ATTACK; stat < Skills.HITS; stat++) {
			player.getSkills().setLevel(stat, player.getSkills().getLevel(stat) - 4);
		}
		if (player.getSkills().getLevel(Skills.HERBLAW) <= player.getSkills().getMaxStat(Skills.HERBLAW)) {
			player.getSkills().setLevel(Skills.HERBLAW, player.getSkills().getLevel(Skills.HERBLAW) + 1);
		}
	}

	private void handleWizardsMindBomb(Player player, Item item) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "you drink the Wizard's Mind Bomb");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "You feel very strange");
		for (int stat = Skills.ATTACK; stat < Skills.HITS; stat++) {
			player.getSkills().setLevel(stat, player.getSkills().getLevel(stat) - 4);
		}
		int change = (player.getSkills().getMaxStat(Skills.MAGIC) > 55 ? 3 : 2);
		int maxWithBomb = (player.getSkills().getMaxStat(Skills.MAGIC) + change);
		if (maxWithBomb - player.getSkills().getLevel(Skills.MAGIC) < change) {
			change = maxWithBomb - player.getSkills().getLevel(Skills.MAGIC);
		}
		if (player.getSkills().getLevel(Skills.MAGIC)
				<= (player.getSkills().getMaxStat(Skills.MAGIC) + (player.getSkills().getMaxStat(Skills.MAGIC)
				> 55 ? 3 : 2))) {
			player.getSkills().setLevel(Skills.MAGIC, player.getSkills().getLevel(Skills.MAGIC) + change);
		}
	}

	private void handleDwarvenStout(Player player, Item item) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "You drink the Dwarven Stout");
		player.playerServerMessage(MessageType.QUEST, "It tastes foul");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		delay(3);
		player.playerServerMessage(MessageType.QUEST, "It tastes pretty strong too");
		for (int stat = Skills.ATTACK; stat < Skills.HITS; stat++) {
			player.getSkills().setLevel(stat, player.getSkills().getLevel(stat) - 4);
		}
		if (player.getSkills().getLevel(Skills.SMITHING) <= player.getSkills().getMaxStat(Skills.SMITHING)) {
			player.getSkills().setLevel(Skills.SMITHING, player.getSkills().getLevel(Skills.SMITHING) + 1);
		}
		if (player.getSkills().getLevel(Skills.MINING) <= player.getSkills().getMaxStat(Skills.MINING)) {
			player.getSkills().setLevel(Skills.MINING, player.getSkills().getLevel(Skills.MINING) + 1);
		}
	}

	private void handleAsgarnianAle(Player player, Item item) {
		player.playerServerMessage(MessageType.QUEST, "You drink the Ale");
		thinkbubble(item);
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 4);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 2);
		}
		if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newStat = player.getSkills().getLevel(Skills.HITS) + 2;
			if (newStat > player.getSkills().getMaxStat(Skills.HITS)) {
				newStat = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newStat);
		}
	}

	private void handleDragonBitter(Player player, Item item) {
		player.playerServerMessage(MessageType.QUEST, "You drink the Dragon bitter");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		thinkbubble(item);
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 4);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 2);
		}
	}

	private void handleGrog(Player player, Item item) {
		player.playerServerMessage(MessageType.QUEST, "You drink the Grog");
		thinkbubble(item);
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 6);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 3);
		}
		if (player.getSkills().getLevel(Skills.HITS) < player.getSkills().getMaxStat(Skills.HITS)) {
			int newStat = player.getSkills().getLevel(Skills.HITS) + 3;
			if (newStat > player.getSkills().getMaxStat(Skills.HITS)) {
				newStat = player.getSkills().getMaxStat(Skills.HITS);
			}
			player.getSkills().setLevel(Skills.HITS, newStat);
		}
	}

	private void handlePoisonChalice(Player player, Item item) {
		player.getCarriedItems().remove(item);
		int chance = DataConversions.random(0, 5);
		int needs;
		switch (chance) {
			case 0: // Hits -1 or -3
				int c = DataConversions.random(0, 1);
				int hp = player.getSkills().getLevel(Skills.HITS);
				player.getSkills().setLevel(Skills.HITS, c == 0 ? hp - 1 : hp - 3);
				player.message("That tasted a bit dodgy. You feel a bit ill");
				break;
			case 1: // Hits + 7
				needs = (
					player.getSkills().getMaxStat(Skills.HITS)
						- player.getSkills().getLevel(Skills.HITS));
				needs = needs < 7 ? needs : 7;
				player.getSkills().setLevel(Skills.HITS,
					player.getSkills().getLevel(Skills.HITS) + needs);
				player.message("It heals some health");
				break;
			case 2: // Crafting +1 Attack & Defence -1
				needs = (
					player.getSkills().getMaxStat(Skills.CRAFTING) + 1
						- player.getSkills().getLevel(Skills.CRAFTING));
				needs = needs < 4 ? needs : 4;
				player.getSkills().setLevel(Skills.CRAFTING,
					player.getSkills().getLevel(Skills.CRAFTING) + needs);
				player.getSkills().setLevel(Skills.ATTACK,
					player.getSkills().getLevel(Skills.ATTACK) - 1);
				player.getSkills().setLevel(Skills.DEFENSE,
					player.getSkills().getLevel(Skills.DEFENSE) - 1);
				player.message("You feel a little strange");
				break;
			case 3: // Hits +? Thieving + 1
				needs = (
					player.getSkills().getMaxStat(Skills.HITS)
						- player.getSkills().getLevel(Skills.HITS));
				needs = needs < 30 ? needs : 30;
				player.getSkills().setLevel(Skills.HITS,
					player.getSkills().getLevel(Skills.HITS) + needs);
				needs = (
					player.getSkills().getMaxStat(Skills.THIEVING) + 1
						- player.getSkills().getLevel(Skills.THIEVING));
				needs = needs < 1 ? needs : 1;
				player.getSkills().setLevel(Skills.THIEVING,
					player.getSkills().getLevel(Skills.THIEVING) + needs);
				player.message("You feel a lot better");
				break;
			case 4: // Hits +? Attack, Defence, Strength +4
				needs = (
					player.getSkills().getMaxStat(Skills.HITS)
						- player.getSkills().getLevel(Skills.HITS));
				needs = needs < 30 ? needs : 30;
				player.getSkills().setLevel(Skills.HITS,
					player.getSkills().getLevel(Skills.HITS) + needs);
				needs = (
					player.getSkills().getMaxStat(Skills.ATTACK) + 4
						- player.getSkills().getLevel(Skills.ATTACK));
				needs = needs < 4 ? needs : 4;
				player.getSkills().setLevel(Skills.ATTACK,
					player.getSkills().getLevel(Skills.ATTACK) + needs);
				needs = (
					player.getSkills().getMaxStat(Skills.STRENGTH) + 4
						- player.getSkills().getLevel(Skills.STRENGTH));
				needs = needs < 4 ? needs : 4;
				player.getSkills().setLevel(Skills.STRENGTH,
					player.getSkills().getLevel(Skills.STRENGTH) + needs);
				needs = (
					player.getSkills().getMaxStat(Skills.DEFENSE) + 4
						- player.getSkills().getLevel(Skills.DEFENSE));
				needs = needs < 4 ? needs : 4;
				player.getSkills().setLevel(Skills.DEFENSE,
					player.getSkills().getLevel(Skills.DEFENSE) + needs);
				player.message("Wow that was an amazing!! You feel really invigorated");
				break;
			case 5: // No effect
				player.message("It has a slight taste of apricot");
				break;
		}
	}
}
