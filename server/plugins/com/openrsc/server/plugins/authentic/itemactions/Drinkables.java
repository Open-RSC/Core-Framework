package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;
import java.util.stream.IntStream;

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

		boolean sendUpdate = player.getClientLimitations().supportsSkillUpdate;

		// act on the last item from inventory
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false)));
		int id = item.getCatalogId();
		if (id == ItemId.GUJUO_POTION.id())
			handleGujouPotion(player, sendUpdate);

		else if (id == ItemId.BRANDY.id() || id == ItemId.VODKA.id()
			|| id == ItemId.GIN.id() || id == ItemId.WHISKY.id()) {
			handleSpirits(player, item, sendUpdate);

		} else if (id == ItemId.HALF_COCKTAIL_GLASS.id() || id == ItemId.FULL_COCKTAIL_GLASS.id()
			|| id == ItemId.ODD_LOOKING_COCKTAIL.id()) {
			handleCocktail(player, item, sendUpdate);

		} else if (id == ItemId.FRUIT_BLAST.id() || id == ItemId.BLURBERRY_BARMAN_FRUIT_BLAST.id()
			|| id == ItemId.PINEAPPLE_PUNCH.id() || id == ItemId.BLURBERRY_BARMAN_PINEAPPLE_PUNCH.id()) {
			handleFruitCocktail(player, item, sendUpdate);

		} else if (id == ItemId.BLURBERRY_SPECIAL.id() || id == ItemId.BLURBERRY_BARMAN_BLURBERRY_SPECIAL.id()
			|| id == ItemId.WIZARD_BLIZZARD.id() || id == ItemId.BLURBERRY_BARMAN_WIZARD_BLIZZARD.id()
			|| id == ItemId.SGG.id() || id == ItemId.BLURBERRY_BARMAN_SGG.id()
			|| id == ItemId.CHOCOLATE_SATURDAY.id() || id == ItemId.BLURBERRY_BARMAN_CHOCOLATE_SATURDAY.id()
			|| id == ItemId.DRUNK_DRAGON.id() || id == ItemId.BLURBERRY_BARMAN_DRUNK_DRAGON.id()) {
			handleSpecialCocktail(player, item, sendUpdate);

		} else if (id == ItemId.BAD_WINE.id())
			handleBadWine(player, item, sendUpdate);

		else if (id == ItemId.HALF_FULL_WINE_JUG.id() || id == ItemId.WINE.id())
			handleWine(player, item, sendUpdate);

		else if (id == ItemId.CHOCOLATY_MILK.id())
			handleChocolatyMilk(player, item, sendUpdate);

		else if (id == ItemId.CUP_OF_TEA.id())
			handleTea(player, item, sendUpdate);

		else if (id == ItemId.BEER.id())
			handleBeer(player, item, sendUpdate);

		else if (id == ItemId.GREENMANS_ALE.id())
			handleGreenmansAle(player, item, sendUpdate);

		else if (id == ItemId.WIZARDS_MIND_BOMB.id())
			handleWizardsMindBomb(player, item, sendUpdate);

		else if (id == ItemId.DWARVEN_STOUT.id())
			handleDwarvenStout(player, item, sendUpdate);

		else if (id == ItemId.ASGARNIAN_ALE.id())
			handleAsgarnianAle(player, item, sendUpdate);

		else if (id == ItemId.DRAGON_BITTER.id())
			handleDragonBitter(player, item, sendUpdate);

		else if (id == ItemId.GROG.id())
			handleGrog(player, item, sendUpdate);

		else if (id == ItemId.POISON_CHALICE.id())
			handlePoisonChalice(player, item, sendUpdate);

		else if (id == ItemId.FULL_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 10, 2, ItemId.THREE_STRENGTH_POTION.id(), 3, sendUpdate);

		else if (id == ItemId.THREE_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 10, 2, ItemId.TWO_STRENGTH_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 10, 2, ItemId.ONE_STRENGTH_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 10, 2, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 10, 2, ItemId.TWO_ATTACK_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 10, 2, ItemId.ONE_ATTACK_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 10, 2, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.TWO_STAT_RESTORATION_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.ONE_STAT_RESTORATION_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 10, 2, ItemId.TWO_DEFENSE_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 10, 2, ItemId.ONE_DEFENSE_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 10, 2, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.TWO_RESTORE_PRAYER_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.ONE_RESTORE_PRAYER_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 15, 4, ItemId.TWO_SUPER_ATTACK_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 15, 4, ItemId.ONE_SUPER_ATTACK_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, Skill.ATTACK.id(), 15, 4, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.TWO_FISHING_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.ONE_FISHING_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 15, 4, ItemId.TWO_SUPER_STRENGTH_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 15, 4, ItemId.ONE_SUPER_STRENGTH_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, Skill.STRENGTH.id(), 15, 4, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 15, 4, ItemId.TWO_SUPER_DEFENSE_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 15, 4, ItemId.ONE_SUPER_DEFENSE_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, Skill.DEFENSE.id(), 15, 4, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_RANGING_POTION.id())
			useNormalPotion(player, item, Skill.RANGED.id(), 10, 3, ItemId.TWO_RANGING_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_RANGING_POTION.id())
			useNormalPotion(player, item, Skill.RANGED.id(), 10, 3, ItemId.ONE_RANGING_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_RANGING_POTION.id())
			useNormalPotion(player, item, Skill.RANGED.id(), 10, 3, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_CURE_POISON_POTION.id())
			useCurePotion(player, item, ItemId.TWO_CURE_POISON_POTION.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_CURE_POISON_POTION.id())
			useCurePotion(player, item, ItemId.ONE_CURE_POISON_POTION.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_CURE_POISON_POTION.id())
			useCurePotion(player, item, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_POISON_ANTIDOTE.id())
			usePoisonAntidotePotion(player, item, ItemId.TWO_POISON_ANTIDOTE.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_POISON_ANTIDOTE.id())
			usePoisonAntidotePotion(player, item, ItemId.ONE_POISON_ANTIDOTE.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_POISON_ANTIDOTE.id())
			usePoisonAntidotePotion(player, item, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.FULL_POTION_OF_ZAMORAK.id())
			useZamorakPotion(player, item, ItemId.TWO_POTION_OF_ZAMORAK.id(), 2, sendUpdate);

		else if (id == ItemId.TWO_POTION_OF_ZAMORAK.id())
			useZamorakPotion(player, item, ItemId.ONE_POTION_OF_ZAMORAK.id(), 1, sendUpdate);

		else if (id == ItemId.ONE_POTION_OF_ZAMORAK.id())
			useZamorakPotion(player, item, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

		else if (id == ItemId.GLASS_MILK.id())
			handleGlassMilk(player, item, sendUpdate);

		else {
			if (config().WANT_RUNECRAFT) {
				if (id == ItemId.FULL_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.TWO_RUNECRAFT_POTION.id(), false, 2, sendUpdate);

				else if (id == ItemId.TWO_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.ONE_RUNECRAFT_POTION.id(), false, 1, sendUpdate);

				else if (id == ItemId.ONE_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.EMPTY_VIAL.id(), false, 0, sendUpdate);

				else if (id == ItemId.FULL_SUPER_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.TWO_SUPER_RUNECRAFT_POTION.id(), true, 2, sendUpdate);

				else if (id == ItemId.TWO_SUPER_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.ONE_SUPER_RUNECRAFT_POTION.id(), true, 1, sendUpdate);

				else if (id == ItemId.ONE_SUPER_RUNECRAFT_POTION.id())
					useRunecraftPotion(player, item, ItemId.EMPTY_VIAL.id(), true, 0, sendUpdate);
			}

			int[] magicStat = getMagicSkillId(player);

			if (id == ItemId.FULL_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 10).toArray(), IntStream.of(magicStat).map(x -> 3).toArray(), ItemId.TWO_MAGIC_POTION.id(), 2, sendUpdate);

			else if (id == ItemId.TWO_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 10).toArray(), IntStream.of(magicStat).map(x -> 3).toArray(), ItemId.ONE_MAGIC_POTION.id(), 1, sendUpdate);

			else if (id == ItemId.ONE_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 10).toArray(), IntStream.of(magicStat).map(x -> 3).toArray(), ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

			else if (id == ItemId.FULL_SUPER_RANGING_POTION.id())
				useNormalPotion(player, item, Skill.RANGED.id(), 15, 4, ItemId.TWO_SUPER_RANGING_POTION.id(), 2, sendUpdate);

			else if (id == ItemId.TWO_SUPER_RANGING_POTION.id())
				useNormalPotion(player, item, Skill.RANGED.id(), 15, 4, ItemId.ONE_SUPER_RANGING_POTION.id(), 1, sendUpdate);

			else if (id == ItemId.ONE_SUPER_RANGING_POTION.id())
				useNormalPotion(player, item, Skill.RANGED.id(), 15, 4, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

			else if (id == ItemId.FULL_SUPER_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 15).toArray(), IntStream.of(magicStat).map(x -> 4).toArray(), ItemId.TWO_SUPER_MAGIC_POTION.id(), 2, sendUpdate);

			else if (id == ItemId.TWO_SUPER_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 15).toArray(), IntStream.of(magicStat).map(x -> 4).toArray(), ItemId.ONE_SUPER_MAGIC_POTION.id(), 1, sendUpdate);

			else if (id == ItemId.ONE_SUPER_MAGIC_POTION.id())
				useNormalPotion(player, item, magicStat, IntStream.of(magicStat).map(x -> 15).toArray(), IntStream.of(magicStat).map(x -> 4).toArray(), ItemId.EMPTY_VIAL.id(), 0, sendUpdate);

			else if (id == ItemId.FULL_POTION_OF_SARADOMIN.id())
				useSaradominPotion(player, item, ItemId.TWO_POTION_OF_SARADOMIN.id(), 2, sendUpdate);

			else if (id == ItemId.TWO_POTION_OF_SARADOMIN.id())
				useSaradominPotion(player, item, ItemId.ONE_POTION_OF_SARADOMIN.id(), 1, sendUpdate);

			else if (id == ItemId.ONE_POTION_OF_SARADOMIN.id())
				useSaradominPotion(player, item, ItemId.EMPTY_VIAL.id(), 0, sendUpdate);
			else
				player.message("Nothing interesting happens");
		}

		if (!sendUpdate) {
			player.getSkills().sendUpdateAll();
		}
	}

	private int[] getMagicSkillId(Player player) {
		return player.getConfig().DIVIDED_GOOD_EVIL ? new int[]{Skill.GOODMAGIC.id(), Skill.EVILMAGIC.id()} : new int[]{Skill.MAGIC.id()};
	}

	private int[] getPrayerSkillId(Player player) {
		return player.getConfig().DIVIDED_GOOD_EVIL ? new int[]{Skill.PRAYGOOD.id(), Skill.PRAYEVIL.id()} : new int[]{Skill.PRAYER.id()};
	}

	private void tryGiveBeerGlass(Player player) {
		if (player.getConfig().RESTRICT_ITEM_ID < ItemId.BEER_GLASS.id() || player.getClientLimitations().maxItemId < ItemId.BEER_GLASS.id())
			return;
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEER_GLASS.id()));
	}

	private void useFishingPotion(Player player, final Item item, final int newItem, final int left, final boolean sendUpdate) {
		int affectedStat = Skill.FISHING.id();
		if (player.getConfig().WAIT_TO_REBOOST && !isNormalLevel(player, affectedStat)) {
			player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
			return;
		}

		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		player.getSkills().setLevel(Skill.FISHING.id(),
			player.getSkills().getMaxStat(Skill.FISHING.id()) + 3, sendUpdate);
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " doses of potion left");
		}
	}

	private void useCurePotion(Player player, final Item item, final int newItem, final int dosesLeft, final boolean sendUpdate) {
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

	private void usePoisonAntidotePotion(Player player, final Item item, final int newItem, final int dosesLeft, final boolean sendUpdate) {
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

	private void useNormalPotion(Player player, final Item item, final int[] affectedStats, final int[] percentageIncreases, final int[] modifiers, final int newItem, final int left, final boolean sendUpdate) {
		for (int affectedStat : affectedStats) {
			if (player.getConfig().WAIT_TO_REBOOST && !isNormalLevel(player, affectedStat)) {
				player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
				return;
			}
		}

		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());

		for (int i=0; i < affectedStats.length; i++) {
			applyPotionEffect(player, item, affectedStats[i], percentageIncreases[i], modifiers[i], newItem, left, sendUpdate);
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

	private void useNormalPotion(Player player, final Item item, final int affectedStat, final int percentageIncrease, final int modifier, final int newItem, final int left, final boolean sendUpdate) {
		if (player.getConfig().WAIT_TO_REBOOST && !isNormalLevel(player, affectedStat)) {
			player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
			return;
		}

		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());

		applyPotionEffect(player, item, affectedStat, percentageIncrease, modifier, newItem, left, sendUpdate);

		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void applyPotionEffect(Player player, final Item item, final int affectedStat, final int percentageIncrease, final int modifier, final int newItem, final int left, final boolean sendUpdate) {
		int baseStat = player.getSkills().getLevel(affectedStat) > player.getSkills().getMaxStat(affectedStat) ? player.getSkills().getMaxStat(affectedStat) : player.getSkills().getLevel(affectedStat);
		int newStat = baseStat
			+ DataConversions.roundUp((player.getSkills().getMaxStat(affectedStat) / 100D) * percentageIncrease)
			+ modifier;
		if (newStat > player.getSkills().getLevel(affectedStat)) {
			player.getSkills().setLevel(affectedStat, newStat, sendUpdate);
		}
	}

	private void useZamorakPotion(Player player, final Item item, final int newItem, final int left, final boolean sendUpdate) {
		int[] boostStats = {Skill.ATTACK.id(), Skill.STRENGTH.id()};
		for (int affectedStat : boostStats) {
			if (player.getConfig().WAIT_TO_REBOOST && !isNormalLevel(player, affectedStat)) {
				player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
				return;
			}
		}

		player.message("You drink some of the foul liquid");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		boolean isLastDose = item.getCatalogId() == ItemId.ONE_POTION_OF_ZAMORAK.id();
		int[] commonAffectedStats = {Skill.ATTACK.id(),
			Skill.DEFENSE.id(),
			Skill.STRENGTH.id(),
			Skill.HITS.id()};
		int[] prayerStats = getPrayerSkillId(player);
		int[] affectedStats = concat(commonAffectedStats, prayerStats);
		int[] percentageIncrease = concat(new int[]{20, -10, 12, -10}, IntStream.of(prayerStats).map(x -> 10).toArray());
		int[] modifier = concat(new int[]{1, -1, 1, 0}, IntStream.of(prayerStats).map(x -> 0).toArray());
		if (isLastDose) {
			for (int i=0; i<affectedStats.length; i++) modifier[i] *= 3;
		}

		for (int i=0; i<affectedStats.length; i++) {
			boolean isBoost = percentageIncrease[i] >= 0;
			if (isBoost) {
				int baseStat = player.getSkills().getLevel(affectedStats[i]) > player.getSkills().getMaxStat(affectedStats[i]) ? player.getSkills().getMaxStat(affectedStats[i]) : player.getSkills().getLevel(affectedStats[i]);
				int newStat = baseStat
					+ DataConversions.roundUp((player.getSkills().getMaxStat(affectedStats[i]) / 100D) * percentageIncrease[i])
					+ modifier[i];
				newStat = !inArray(affectedStats[i], prayerStats) ? newStat : Math.min(newStat, player.getSkills().getMaxStat(affectedStats[i]));
				if (newStat > player.getSkills().getLevel(affectedStats[i])) {
					player.getSkills().setLevel(affectedStats[i], newStat, sendUpdate);
				}
			} else {
				int baseStat = player.getSkills().getLevel(affectedStats[i]) < player.getSkills().getMaxStat(affectedStats[i]) ? player.getSkills().getMaxStat(affectedStats[i]) : player.getSkills().getLevel(affectedStats[i]);
				int newStat = baseStat
					- DataConversions.roundUp((player.getSkills().getMaxStat(affectedStats[i]) / 100D) * -1 * percentageIncrease[i])
					- (-1 * modifier[i]);
				if (newStat < player.getSkills().getLevel(affectedStats[i])) {
					player.getSkills().setLevel(affectedStats[i], newStat, sendUpdate);
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

	private void useSaradominPotion(Player player, final Item item, final int newItem, final int left, final boolean sendUpdate) {
		int affectedStat = Skill.DEFENSE.id();
		if (player.getConfig().WAIT_TO_REBOOST && !isNormalLevel(player, affectedStat)) {
			player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
			return;
		}

		player.message("You drink some of the cleansed liquid");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		boolean isLastDose = item.getCatalogId() == ItemId.ONE_POTION_OF_SARADOMIN.id();
		int[] commonAffectedStats = {Skill.ATTACK.id(),
			Skill.DEFENSE.id(),
			Skill.STRENGTH.id(),
			Skill.HITS.id(),
			Skill.RANGED.id()};
		int[] magicStats = getMagicSkillId(player);
		int[] affectedStats = concat(commonAffectedStats, magicStats);
		int[] percentageIncrease = concat(new int[]{-10, 20, -10, 15, -10}, IntStream.of(magicStats).map(x -> -10).toArray());
		int[] modifier = concat(new int[]{-1, 1, -1, 1, -1}, IntStream.of(magicStats).map(x -> -1).toArray());
		if (isLastDose) {
			for (int i=0; i<affectedStats.length; i++) modifier[i] *= 3;
		}

		for (int i=0; i<affectedStats.length; i++) {
			boolean isBoost = percentageIncrease[i] >= 0;
			if (isBoost) {
				int baseStat = player.getSkills().getLevel(affectedStats[i]) > player.getSkills().getMaxStat(affectedStats[i]) ? player.getSkills().getMaxStat(affectedStats[i]) : player.getSkills().getLevel(affectedStats[i]);
				int newStat = baseStat
					+ DataConversions.roundUp((player.getSkills().getMaxStat(affectedStats[i]) / 100D) * percentageIncrease[i])
					+ modifier[i];
				if (newStat > player.getSkills().getLevel(affectedStats[i])) {
					player.getSkills().setLevel(affectedStats[i], newStat, sendUpdate);
				}
			} else {
				int baseStat = player.getSkills().getLevel(affectedStats[i]) < player.getSkills().getMaxStat(affectedStats[i]) ? player.getSkills().getMaxStat(affectedStats[i]) : player.getSkills().getLevel(affectedStats[i]);
				int newStat = baseStat
					- DataConversions.roundUp((player.getSkills().getMaxStat(affectedStats[i]) / 100D) * -1 * percentageIncrease[i])
					- (-1 * modifier[i]);
				if (newStat < player.getSkills().getLevel(affectedStats[i])) {
					player.getSkills().setLevel(affectedStats[i], newStat, sendUpdate);
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

	private void usePrayerPotion(Player player, final Item item, final int newItem, final int left, final boolean sendUpdate) {
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));

		int[] prayerIds = getPrayerSkillId(player);
		for (int prayerId : prayerIds) {
			int newPrayer = player.getSkills().getLevel(prayerId) + (int) ((player.getSkills().getMaxStat(prayerId) * 0.25) + 7);
			if (newPrayer > player.getSkills().getMaxStat(prayerId)) {
				newPrayer = player.getSkills().getMaxStat(prayerId);
			}
			player.getSkills().setLevel(prayerId, newPrayer, sendUpdate);
		}

		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useStatRestorePotion(Player player, final Item item, final int newItem, final int left, final boolean sendUpdate) {
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		// In RSC stat restore potion is only applicable for Attack, Strength, and Defense
		int[] affectedStats = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id()};
		for (int i = 0; i < affectedStats.length; i++) {
			if (player.getSkills().getLevel(affectedStats[i]) > player.getSkills().getMaxStat(affectedStats[i])) {
				continue;
			}
			int newStat = player.getSkills().getLevel(affectedStats[i]) + (int) ((player.getSkills().getMaxStat(affectedStats[i]) * 0.3) + 10);
			if (newStat > player.getSkills().getMaxStat(affectedStats[i])) {
				newStat = player.getSkills().getMaxStat(affectedStats[i]);
			}
			if (newStat < 14) {
				player.getSkills().setLevel(affectedStats[i], player.getSkills().getMaxStat(affectedStats[i]), sendUpdate);
			} else {
				player.getSkills().setLevel(affectedStats[i], newStat, sendUpdate);
			}
		}
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useRunecraftPotion(Player player, final Item item, final int newItem, final boolean superPot, final int left, final boolean sendUpdate) {
		int affectedStat = Skill.RUNECRAFT.id();
		if (player.getConfig().WAIT_TO_REBOOST && !isNormalLevel(player, affectedStat)) {
			player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
			return;
		}

		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(newItem));
		int newStat;
		// TODO Should probably put the boost values in some kind of configuration or definition at some point.
		// Restore stat
		if (player.getSkills().getLevel(Skill.RUNECRAFT.id()) <= player.getSkills().getMaxStat(Skill.RUNECRAFT.id())) {
			newStat = player.getSkills().getLevel(Skill.RUNECRAFT.id()) + (superPot ? 6 : 3);
		}

		// Boost stat
		else {
			final int boostedStat = player.getSkills().getMaxStat(Skill.RUNECRAFT.id()) + (superPot ? 6 : 3);
			newStat = Math.max(boostedStat, player.getSkills().getLevel(Skill.RUNECRAFT.id()));
		}

		player.getSkills().setLevel(Skill.RUNECRAFT.id(), newStat, sendUpdate);
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " doses of potion left");
		}
	}

	private void handleGujouPotion(Player player, final boolean sendUpdate) {
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

	private void handleSpirits(Player player, Item item, final boolean sendUpdate) {
		player.playerServerMessage(MessageType.QUEST, "You drink the " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		if (item.getCatalogId() == ItemId.WHISKY.id())
			player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 6, sendUpdate);
		else
			player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 3, sendUpdate);
		if (player.getSkills().getLevel(Skill.STRENGTH.id()) <= player.getSkills().getMaxStat(Skill.STRENGTH.id())) {
			player.getSkills().setLevel(Skill.STRENGTH.id(), player.getSkills().getLevel(Skill.STRENGTH.id()) + 5, sendUpdate);
		}
		final boolean heals = player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id());
		if (heals) {
			int newHp = player.getSkills().getLevel(Skill.HITS.id()) + 4;
			if (newHp > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newHp = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newHp, sendUpdate);
		}
		player.getCarriedItems().remove(item);
	}

	private void handleCocktail(Player player, Item item, final boolean sendUpdate) {
		player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 3, sendUpdate);
		player.getSkills().setLevel(Skill.DEFENSE.id(), player.getSkills().getLevel(Skill.DEFENSE.id()) - 1, sendUpdate);
		player.getSkills().setLevel(Skill.STRENGTH.id(), player.getSkills().getLevel(Skill.STRENGTH.id()) - 4, sendUpdate);
		player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
		player.playerServerMessage(MessageType.QUEST, "It tastes awful..yuck");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
		resetGnomeBartending(player);
	}

	private void handleFruitCocktail(Player player, Item item, final boolean sendUpdate) {
		if (player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id())) {
			int newHp = player.getSkills().getLevel(Skill.HITS.id()) + 8
					+ (item.getCatalogId() == ItemId.PINEAPPLE_PUNCH.id() ? 1 : 0);
			if (newHp > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newHp = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newHp, sendUpdate);
		}
		player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
		player.playerServerMessage(MessageType.QUEST, "yum ..it tastes great");
		player.playerServerMessage(MessageType.QUEST, "You feel reinvigorated");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
	}

	private void handleSpecialCocktail(Player player, Item item, final boolean sendUpdate) {
		if (player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id())) {
			int newHp = player.getSkills().getLevel(Skill.HITS.id()) + 5;
			if (newHp > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newHp = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newHp, sendUpdate);
		}
		player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 4, sendUpdate);
		if (player.getSkills().getLevel(Skill.STRENGTH.id()) <= player.getSkills().getMaxStat(Skill.STRENGTH.id())) {
			player.getSkills().setLevel(Skill.STRENGTH.id(), player.getSkills().getLevel(Skill.STRENGTH.id()) + 6, sendUpdate);
		}
		player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
		player.playerServerMessage(MessageType.QUEST, "yum ..it tastes great");
		player.playerServerMessage(MessageType.QUEST, "although you feel slightly dizzy");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
	}

	private void handleBadWine(Player player, Item item, final boolean sendUpdate) {
		player.message("You drink the bad wine");
		thinkbubble(item);

		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.JUG.id()));

		player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 3, sendUpdate);
		delay(2);
		player.message("You start to feel sick");
	}

	private void handleWine(Player player, Item item, final boolean sendUpdate) {
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
		if (player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id())) {
			int newStat = player.getSkills().getLevel(Skill.HITS.id()) + 11;
			if (newStat > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newStat = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newStat, sendUpdate);
		}
		player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 3, sendUpdate);
	}

	private void handleChocolatyMilk(Player player, Item item, final boolean sendUpdate) {
		thinkbubble(item);
		player.message("You drink the chocolaty milk");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BUCKET.id()));
		if (player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id())) {
			int newHp = player.getSkills().getLevel(Skill.HITS.id()) + 4;
			if (newHp > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newHp = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newHp, sendUpdate);
		}
	}

	private void handleGlassMilk(Player player, Item item, final boolean sendUpdate) {
		thinkbubble(item);
		player.message("You drink the cold milk");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.BEVERAGE_GLASS.id()));
		if (player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id())) {
			int newHp = player.getSkills().getLevel(Skill.HITS.id()) + 2;
			if (newHp > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newHp = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newHp, sendUpdate);
		}
	}

	private void handleTea(Player player, Item item, final boolean sendUpdate) {
		thinkbubble(item);
		// authentic does not send to quest tab
		player.message("You drink the cup of tea");
		player.getCarriedItems().remove(item);
		int changeHp = (player.getSkills().getMaxStat(Skill.HITS.id()) > 55 ? 3 : 2);
		if (player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id())) {
			int newHp = player.getSkills().getLevel(Skill.HITS.id()) + changeHp;
			if (newHp > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newHp = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newHp, sendUpdate);
		}
		int changeAtt = (player.getSkills().getMaxStat(Skill.ATTACK.id()) > 55 ? 3 : 2);
		int maxWithTea = (player.getSkills().getMaxStat(Skill.ATTACK.id()) + changeAtt);
		if (maxWithTea - player.getSkills().getLevel(Skill.ATTACK.id()) < changeAtt) {
			changeAtt = maxWithTea - player.getSkills().getLevel(Skill.ATTACK.id());
		}
		if (player.getSkills().getLevel(Skill.ATTACK.id()) <=
				(player.getSkills().getMaxStat(Skill.ATTACK.id()) + (player.getSkills().getMaxStat(Skill.ATTACK.id()) > 55 ? 3 : 2))) {
			player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) + changeAtt, sendUpdate);
		}
	}

	private void handleBeer(Player player, Item item, final boolean sendUpdate) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "You drink the beer");
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		player.getCarriedItems().remove(item);
		tryGiveBeerGlass(player);
		player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 4, sendUpdate);
		if (player.getSkills().getLevel(Skill.STRENGTH.id()) <= player.getSkills().getMaxStat(Skill.STRENGTH.id())) {
			player.getSkills().setLevel(Skill.STRENGTH.id(), player.getSkills().getLevel(Skill.STRENGTH.id()) + 2, sendUpdate);
		}
		if (player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id())) {
			int newStat = player.getSkills().getLevel(Skill.HITS.id()) + 1;
			if (newStat > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newStat = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newStat, sendUpdate);
		}
	}

	private void handleGreenmansAle(Player player, Item item, final boolean sendUpdate) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "You drink the greenmans ale");
		player.getCarriedItems().remove(item);
		tryGiveBeerGlass(player);
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "It has a strange taste");
		int[] meleeIds = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id()};
		for (int statId : meleeIds) {
			player.getSkills().setLevel(statId, player.getSkills().getLevel(statId) - 4, sendUpdate);
		}
		if (player.getSkills().getLevel(Skill.HERBLAW.id()) <= player.getSkills().getMaxStat(Skill.HERBLAW.id())) {
			player.getSkills().setLevel(Skill.HERBLAW.id(), player.getSkills().getLevel(Skill.HERBLAW.id()) + 1, sendUpdate);
		}
	}

	private void handleWizardsMindBomb(Player player, Item item, final boolean sendUpdate) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "you drink the Wizard's Mind Bomb");
		player.getCarriedItems().remove(item);
		tryGiveBeerGlass(player);
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "You feel very strange");
		int[] meleeIds = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id()};
		for (int statId : meleeIds) {
			player.getSkills().setLevel(statId, player.getSkills().getLevel(statId) - 4, sendUpdate);
		}
		int[] magicIds = getMagicSkillId(player);
		for (int magicId : magicIds) {
			int change = (player.getSkills().getMaxStat(magicId) > 55 ? 3 : 2);
			int maxWithBomb = (player.getSkills().getMaxStat(magicId) + change);
			if (maxWithBomb - player.getSkills().getLevel(magicId) < change) {
				change = maxWithBomb - player.getSkills().getLevel(magicId);
			}
			if (player.getSkills().getLevel(magicId)
				<= (player.getSkills().getMaxStat(magicId) + (player.getSkills().getMaxStat(magicId)
				> 55 ? 3 : 2))) {
				player.getSkills().setLevel(magicId, player.getSkills().getLevel(magicId) + change, sendUpdate);
			}
		}
	}

	private void handleDwarvenStout(Player player, Item item, final boolean sendUpdate) {
		thinkbubble(item);
		player.playerServerMessage(MessageType.QUEST, "You drink the Dwarven Stout");
		player.playerServerMessage(MessageType.QUEST, "It tastes foul");
		player.getCarriedItems().remove(item);
		tryGiveBeerGlass(player);
		delay(3);
		player.playerServerMessage(MessageType.QUEST, "It tastes pretty strong too");
		int[] meleeIds = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id()};
		for (int statId : meleeIds) {
			player.getSkills().setLevel(statId, player.getSkills().getLevel(statId) - 4, sendUpdate);
		}
		if (player.getSkills().getLevel(Skill.SMITHING.id()) <= player.getSkills().getMaxStat(Skill.SMITHING.id())) {
			player.getSkills().setLevel(Skill.SMITHING.id(), player.getSkills().getLevel(Skill.SMITHING.id()) + 1, sendUpdate);
		}
		if (player.getSkills().getLevel(Skill.MINING.id()) <= player.getSkills().getMaxStat(Skill.MINING.id())) {
			player.getSkills().setLevel(Skill.MINING.id(), player.getSkills().getLevel(Skill.MINING.id()) + 1, sendUpdate);
		}
	}

	private void handleAsgarnianAle(Player player, Item item, final boolean sendUpdate) {
		player.playerServerMessage(MessageType.QUEST, "You drink the Ale");
		thinkbubble(item);
		player.getCarriedItems().remove(item);
		tryGiveBeerGlass(player);
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 4, sendUpdate);
		if (player.getSkills().getLevel(Skill.STRENGTH.id()) <= player.getSkills().getMaxStat(Skill.STRENGTH.id())) {
			player.getSkills().setLevel(Skill.STRENGTH.id(), player.getSkills().getLevel(Skill.STRENGTH.id()) + 2, sendUpdate);
		}
		if (player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id())) {
			int newStat = player.getSkills().getLevel(Skill.HITS.id()) + 2;
			if (newStat > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newStat = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newStat, sendUpdate);
		}
	}

	private void handleDragonBitter(Player player, Item item, final boolean sendUpdate) {
		player.playerServerMessage(MessageType.QUEST, "You drink the Dragon bitter");
		player.getCarriedItems().remove(item);
		tryGiveBeerGlass(player);
		thinkbubble(item);
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 4, sendUpdate);
		if (player.getSkills().getLevel(Skill.STRENGTH.id()) <= player.getSkills().getMaxStat(Skill.STRENGTH.id())) {
			player.getSkills().setLevel(Skill.STRENGTH.id(), player.getSkills().getLevel(Skill.STRENGTH.id()) + 2, sendUpdate);
		}
	}

	private void handleGrog(Player player, Item item, final boolean sendUpdate) {
		player.playerServerMessage(MessageType.QUEST, "You drink the Grog");
		thinkbubble(item);
		player.getCarriedItems().remove(item);
		tryGiveBeerGlass(player);
		delay(2);
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 6, sendUpdate);
		if (player.getSkills().getLevel(Skill.STRENGTH.id()) <= player.getSkills().getMaxStat(Skill.STRENGTH.id())) {
			player.getSkills().setLevel(Skill.STRENGTH.id(), player.getSkills().getLevel(Skill.STRENGTH.id()) + 3, sendUpdate);
		}
		if (player.getSkills().getLevel(Skill.HITS.id()) < player.getSkills().getMaxStat(Skill.HITS.id())) {
			int newStat = player.getSkills().getLevel(Skill.HITS.id()) + 3;
			if (newStat > player.getSkills().getMaxStat(Skill.HITS.id())) {
				newStat = player.getSkills().getMaxStat(Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newStat, sendUpdate);
		}
	}

	private void handlePoisonChalice(Player player, Item item, final boolean sendUpdate) {
		player.getCarriedItems().remove(item);
		int chance = DataConversions.random(0, 5);
		int needs;
		switch (chance) {
			case 0: // Hits -1 or -3
				int c = DataConversions.random(0, 1);
				int hp = player.getSkills().getLevel(Skill.HITS.id());
				player.getSkills().setLevel(Skill.HITS.id(), c == 0 ? hp - 1 : hp - 3, sendUpdate);
				player.message("That tasted a bit dodgy. You feel a bit ill");
				break;
			case 1: // Hits + 7
				needs = (
					player.getSkills().getMaxStat(Skill.HITS.id())
						- player.getSkills().getLevel(Skill.HITS.id()));
				needs = needs < 7 ? needs : 7;
				player.getSkills().setLevel(Skill.HITS.id(),
					player.getSkills().getLevel(Skill.HITS.id()) + needs, sendUpdate);
				player.message("It heals some health");
				break;
			case 2: // Crafting +1 Attack & Defence -1
				needs = (
					player.getSkills().getMaxStat(Skill.CRAFTING.id()) + 1
						- player.getSkills().getLevel(Skill.CRAFTING.id()));
				needs = needs < 4 ? needs : 4;
				player.getSkills().setLevel(Skill.CRAFTING.id(),
					player.getSkills().getLevel(Skill.CRAFTING.id()) + needs, sendUpdate);
				player.getSkills().setLevel(Skill.ATTACK.id(),
					player.getSkills().getLevel(Skill.ATTACK.id()) - 1, sendUpdate);
				player.getSkills().setLevel(Skill.DEFENSE.id(),
					player.getSkills().getLevel(Skill.DEFENSE.id()) - 1, sendUpdate);
				player.message("You feel a little strange");
				break;
			case 3: // Hits +? Thieving + 1
				needs = (
					player.getSkills().getMaxStat(Skill.HITS.id())
						- player.getSkills().getLevel(Skill.HITS.id()));
				needs = needs < 30 ? needs : 30;
				player.getSkills().setLevel(Skill.HITS.id(),
					player.getSkills().getLevel(Skill.HITS.id()) + needs, sendUpdate);
				needs = (
					player.getSkills().getMaxStat(Skill.THIEVING.id()) + 1
						- player.getSkills().getLevel(Skill.THIEVING.id()));
				needs = needs < 1 ? needs : 1;
				player.getSkills().setLevel(Skill.THIEVING.id(),
					player.getSkills().getLevel(Skill.THIEVING.id()) + needs, sendUpdate);
				player.message("You feel a lot better");
				break;
			case 4: // Hits +? Attack, Defence, Strength +4
				needs = (
					player.getSkills().getMaxStat(Skill.HITS.id())
						- player.getSkills().getLevel(Skill.HITS.id()));
				needs = needs < 30 ? needs : 30;
				player.getSkills().setLevel(Skill.HITS.id(),
					player.getSkills().getLevel(Skill.HITS.id()) + needs, sendUpdate);
				needs = (
					player.getSkills().getMaxStat(Skill.ATTACK.id()) + 4
						- player.getSkills().getLevel(Skill.ATTACK.id()));
				needs = needs < 4 ? needs : 4;
				player.getSkills().setLevel(Skill.ATTACK.id(),
					player.getSkills().getLevel(Skill.ATTACK.id()) + needs, sendUpdate);
				needs = (
					player.getSkills().getMaxStat(Skill.STRENGTH.id()) + 4
						- player.getSkills().getLevel(Skill.STRENGTH.id()));
				needs = needs < 4 ? needs : 4;
				player.getSkills().setLevel(Skill.STRENGTH.id(),
					player.getSkills().getLevel(Skill.STRENGTH.id()) + needs, sendUpdate);
				needs = (
					player.getSkills().getMaxStat(Skill.DEFENSE.id()) + 4
						- player.getSkills().getLevel(Skill.DEFENSE.id()));
				needs = needs < 4 ? needs : 4;
				player.getSkills().setLevel(Skill.DEFENSE.id(),
					player.getSkills().getLevel(Skill.DEFENSE.id()) + needs, sendUpdate);
				player.message("Wow that was an amazing!! You feel really invigorated");
				break;
			case 5: // No effect
				player.message("It has a slight taste of apricot");
				break;
		}
	}
}
