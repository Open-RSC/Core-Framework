package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.checkAndRemoveBlurberry;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

public class Drinkables implements InvActionListener, InvActionExecutiveListener {

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getDef().getCommand().equalsIgnoreCase("drink");
	}

	@Override
	public void onInvAction(Item item, Player player) {
		if (player.cantConsume()) {
			return;
		}
		int id = item.getID();
		player.setConsumeTimer(Constants.GameServer.GAME_TICK); // drink speed is same as tick speed setting
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
			useNormalPotion(player, item, 2, 10, 2, ItemId.THREE_STRENGTH_POTION.id(), 3);

		else if (id == ItemId.THREE_STRENGTH_POTION.id())
			useNormalPotion(player, item, 2, 10, 2, ItemId.TWO_STRENGTH_POTION.id(), 2);

		else if (id == ItemId.TWO_STRENGTH_POTION.id())
			useNormalPotion(player, item, 2, 10, 2, ItemId.ONE_STRENGTH_POTION.id(), 1);

		else if (id == ItemId.ONE_STRENGTH_POTION.id())
			useNormalPotion(player, item, 2, 10, 2, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_ATTACK_POTION.id())
			useNormalPotion(player, item, 0, 10, 2, 475, 2);

		else if (id == ItemId.TWO_ATTACK_POTION.id())
			useNormalPotion(player, item, 0, 10, 2, 476, 1);

		else if (id == ItemId.ONE_ATTACK_POTION.id())
			useNormalPotion(player, item, 0, 10, 2, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, 478, 2);

		else if (id == ItemId.TWO_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, 479, 1);

		else if (id == ItemId.ONE_STAT_RESTORATION_POTION.id())
			useStatRestorePotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_DEFENSE_POTION.id())
			useNormalPotion(player, item, 1, 10, 2, 481, 2);

		else if (id == ItemId.TWO_DEFENSE_POTION.id())
			useNormalPotion(player, item, 1, 10, 2, 482, 1);

		else if (id == ItemId.ONE_DEFENSE_POTION.id())
			useNormalPotion(player, item, 1, 10, 2, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, 484, 2);

		else if (id == ItemId.TWO_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, 485, 1);

		else if (id == ItemId.ONE_RESTORE_PRAYER_POTION.id())
			usePrayerPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, 0, 15, 4, 487, 2);

		else if (id == ItemId.TWO_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, 0, 15, 4, 488, 1);

		else if (id == ItemId.ONE_SUPER_ATTACK_POTION.id())
			useNormalPotion(player, item, 0, 15, 4, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_FISHING_POTION.id())
			useFishingPotion(player, item, 490, 2);

		else if (id == ItemId.TWO_FISHING_POTION.id())
			useFishingPotion(player, item, 491, 1);

		else if (id == ItemId.ONE_FISHING_POTION.id())
			useFishingPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, 2, 15, 4, 493, 2);

		else if (id == ItemId.TWO_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, 2, 15, 4, 494, 1);

		else if (id == ItemId.ONE_SUPER_STRENGTH_POTION.id())
			useNormalPotion(player, item, 2, 15, 4, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, 1, 15, 4, 496, 2);

		else if (id == ItemId.TWO_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, 1, 15, 4, 497, 1);

		else if (id == ItemId.ONE_SUPER_DEFENSE_POTION.id())
			useNormalPotion(player, item, 1, 15, 4, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_RANGING_POTION.id())
			useNormalPotion(player, item, 4, 10, 2, 499, 2);

		else if (id == ItemId.TWO_RANGING_POTION.id())
			useNormalPotion(player, item, 4, 10, 2, 500, 1);

		else if (id == ItemId.ONE_RANGING_POTION.id())
			useNormalPotion(player, item, 4, 10, 2, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_CURE_POISON_POTION.id())
			useCurePotion(player, item, 567, 2);

		else if (id == ItemId.TWO_CURE_POISON_POTION.id())
			useCurePotion(player, item, 568, 1);

		else if (id == ItemId.ONE_CURE_POISON_POTION.id())
			useCurePotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_POISON_ANTIDOTE.id())
			usePoisonAntidotePotion(player, item, 570, 2);

		else if (id == ItemId.TWO_POISON_ANTIDOTE.id())
			usePoisonAntidotePotion(player, item, 571, 1);

		else if (id == ItemId.ONE_POISON_ANTIDOTE.id())
			usePoisonAntidotePotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else if (id == ItemId.FULL_POTION_OF_ZAMORAK.id())
			useZamorakPotion(player, item, 964, 2);

		else if (id == ItemId.TWO_POTION_OF_ZAMORAK.id())
			useZamorakPotion(player, item, 965, 1);

		else if (id == ItemId.ONE_POTION_OF_ZAMORAK.id())
			useZamorakPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);

		else
			player.message("Nothing interesting happens");
	}

	private void useFishingPotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef().getName().toLowerCase());
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		player.getSkills().setLevel(Skills.FISHING, player.getSkills().getMaxStat(Skills.FISHING) + 3);
		sleep(1200);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " doses of potion left");
		}
	}

	private void useCurePotion(Player player, final Item item, final int newItem, final int dosesLeft) {
		player.message("You drink some of your " + item.getDef().getName().toLowerCase());
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		player.cure();
		sleep(1200);
		if (dosesLeft <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + dosesLeft + " doses of potion left");
		}
	}

	private void usePoisonAntidotePotion(Player player, final Item item, final int newItem, final int dosesLeft) {
		player.message("You drink some of your " + item.getDef().getName().toLowerCase() + " potion");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		player.cure();
		player.setAntidoteProtection(); // 90 seconds.
		sleep(1200);
		if (dosesLeft <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + dosesLeft + " doses of potion left");
		}
	}

	private void useNormalPotion(Player player, final Item item, final int affectedStat, final int percentageIncrease, final int modifier, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef().getName().toLowerCase());
		int baseStat = player.getSkills().getLevel(affectedStat) > player.getSkills().getMaxStat(affectedStat) ? player.getSkills().getMaxStat(affectedStat) : player.getSkills().getLevel(affectedStat);
		int newStat = baseStat
			+ DataConversions.roundUp((player.getSkills().getMaxStat(affectedStat) / 100D) * percentageIncrease)
			+ modifier;
		if (newStat > player.getSkills().getLevel(affectedStat)) {
			player.getSkills().setLevel(affectedStat, newStat);
		}
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		sleep(1200);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useZamorakPotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your zamorak potion");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		int attackBoost = (int) 22.5;
		int strengthBoost = 15;
		int defenceDecrease = (int) 12.5;
		int hitsDecrease = 10;

		if (player.getSkills().getLevel(Skills.ATTACK) > player.getSkills().getMaxStat(Skills.ATTACK)) {
			int baseStat = player.getSkills().getMaxStat(Skills.ATTACK);
			int newStat = baseStat + DataConversions.roundUp(player.getSkills().getMaxStat(Skills.ATTACK) / 100D * attackBoost);
			if (newStat > player.getSkills().getLevel(Skills.ATTACK)) {
				player.getSkills().setLevel(Skills.ATTACK, newStat);
			}
		} else {
			int baseStat = player.getSkills().getLevel(Skills.ATTACK);
			int newStat = baseStat + DataConversions.roundUp(player.getSkills().getMaxStat(Skills.ATTACK) / 100D * attackBoost);
			if (newStat > player.getSkills().getLevel(Skills.ATTACK)) {
				player.getSkills().setLevel(Skills.ATTACK, newStat);
			}
		}
		if (player.getSkills().getLevel(Skills.STRENGTH) > player.getSkills().getMaxStat(Skills.STRENGTH)) {
			int baseStat = player.getSkills().getMaxStat(Skills.STRENGTH);
			int newStat = baseStat + DataConversions.roundUp(player.getSkills().getMaxStat(Skills.STRENGTH) / 100D * strengthBoost);
			if (newStat > player.getSkills().getLevel(Skills.STRENGTH)) {
				player.getSkills().setLevel(Skills.STRENGTH, newStat);
			}
		} else {
			int baseStat = player.getSkills().getLevel(Skills.STRENGTH);
			int newStat = baseStat + DataConversions.roundUp(player.getSkills().getMaxStat(Skills.STRENGTH) / 100D * strengthBoost);
			if (newStat > player.getSkills().getLevel(Skills.STRENGTH)) {
				player.getSkills().setLevel(Skills.STRENGTH, newStat);
			}
		}
		if (player.getSkills().getLevel(Skills.DEFENSE) < player.getSkills().getMaxStat(Skills.DEFENSE)) {
			int baseStat = player.getSkills().getMaxStat(Skills.DEFENSE);
			int newStat = baseStat - DataConversions.roundUp(player.getSkills().getMaxStat(Skills.DEFENSE) / 100D * defenceDecrease);
			if (newStat < player.getSkills().getLevel(Skills.DEFENSE)) {
				player.getSkills().setLevel(Skills.DEFENSE, newStat);
			}
		} else {
			int baseStat = player.getSkills().getLevel(Skills.DEFENSE);
			int newStat = baseStat - DataConversions.roundUp(player.getSkills().getMaxStat(Skills.DEFENSE) / 100D * defenceDecrease);
			if (newStat < player.getSkills().getLevel(Skills.DEFENSE)) {
				player.getSkills().setLevel(Skills.DEFENSE, newStat);
			}
		}
		if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS)) {
			int baseStat = player.getSkills().getMaxStat(Skills.HITPOINTS);
			int newStat = baseStat - DataConversions.roundUp(player.getSkills().getMaxStat(Skills.HITPOINTS) / 100D * hitsDecrease);
			if (newStat < player.getSkills().getLevel(Skills.HITPOINTS)) {
				player.getSkills().setLevel(Skills.HITPOINTS, newStat);
			}
		} else {
			int baseStat = player.getSkills().getLevel(Skills.HITPOINTS);
			int newStat = baseStat - DataConversions.roundUp(player.getSkills().getMaxStat(Skills.HITPOINTS) / 100D * hitsDecrease);
			if (newStat < player.getSkills().getLevel(Skills.HITPOINTS)) {
				player.getSkills().setLevel(Skills.HITPOINTS, newStat);
			}
		}
		sleep(1200);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void usePrayerPotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef().getName().toLowerCase());
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		int newPrayer = player.getSkills().getLevel(Skills.PRAYER) + (int) ((player.getSkills().getMaxStat(Skills.PRAYER) * 0.25) + 7);
		if (newPrayer > player.getSkills().getMaxStat(Skills.PRAYER)) {
			newPrayer = player.getSkills().getMaxStat(Skills.PRAYER);
		}
		player.getSkills().setLevel(Skills.PRAYER, newPrayer);
		sleep(1200);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useStatRestorePotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef().getName().toLowerCase());
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		for (int i = Skills.ATTACK; i < Skills.COOKING; i++) {
			if (i == Skills.HITPOINTS || i == Skills.PRAYER) {
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
		sleep(1200);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void handleGujouPotion(Player player) {
		player.message("Are you sure you want to drink this?");
		int drink = showMenu(player,
			"Yes, I'm sure...",
			"No, I've had second thoughts...");
		if (drink == 0) {
			player.message("You drink the potion...");
			player.getInventory().replace(ItemId.GUJUO_POTION.id(), ItemId.EMPTY_VIAL.id());
			if (!player.getCache().hasKey("gujuo_potion")) {
				player.getCache().store("gujuo_potion", true);
			}
			playerTalk(player, null, "Mmmm.....");
			sleep(1100);
			player.message("It tastes sort of strange...like fried oranges...");
			playerTalk(player, null, ".....!.....");
			sleep(1100);
			message(player, 1300, "You feel somehow different...");
			playerTalk(player, null, "Let's just hope that this isn't a placibo!");
		} else if (drink == 1) {
			player.message("You decide against drinking the potion...");
		}
	}

	private void handleSpirits(Player player, Item item) {
		player.playerServerMessage(MessageType.QUEST, "You drink the " + item.getDef().getName().toLowerCase());
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		if (item.getID() == ItemId.WHISKY.id())
			player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 6);
		else
			player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 3);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 5);
		}
		final boolean heals = player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS);
		if (heals) {
			int newHp = player.getSkills().getLevel(Skills.HITPOINTS) + 4;
			if (newHp > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITPOINTS);
			}
			player.getSkills().setLevel(Skills.HITPOINTS, newHp);
		}
		player.getInventory().remove(item);
	}

	private void handleCocktail(Player player, Item item) {
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 3);
		player.getSkills().setLevel(Skills.DEFENSE, player.getSkills().getLevel(Skills.DEFENSE) - 1);
		player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) - 4);
		player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
		player.playerServerMessage(MessageType.QUEST, "It tastes awful..yuck");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
		checkAndRemoveBlurberry(player, true);
	}

	private void handleFruitCocktail(Player player, Item item) {
		if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS)) {
			int newHp = player.getSkills().getLevel(Skills.HITPOINTS) + 8;
			if (newHp > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITPOINTS);
			}
			player.getSkills().setLevel(Skills.HITPOINTS, newHp);
		}
		player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
		player.playerServerMessage(MessageType.QUEST, "yum ..it tastes great");
		player.playerServerMessage(MessageType.QUEST, "You feel reinvigorated");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
	}

	private void handleSpecialCocktail(Player player, Item item) {
		if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS)) {
			int newHp = player.getSkills().getLevel(Skills.HITPOINTS) + 5;
			if (newHp > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITPOINTS);
			}
			player.getSkills().setLevel(Skills.HITPOINTS, newHp);
		}
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 4);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 6);
		}
		player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
		player.playerServerMessage(MessageType.QUEST, "yum ..it tastes great");
		player.playerServerMessage(MessageType.QUEST, "although you feel slightly dizzy");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.COCKTAIL_GLASS.id()));
	}

	private void handleBadWine(Player player, Item item) {
		player.message("You drink the bad wine");
		showBubble(player, item);

		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.JUG.id()));

		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 3);
		sleep(1200);
		player.message("You start to feel sick");
	}

	private void handleWine(Player player, Item item) {
		showBubble(player, item);
		player.playerServerMessage(MessageType.QUEST, "You drink the wine");
		player.playerServerMessage(MessageType.QUEST, "It makes you feel a bit dizzy");
		player.getInventory().remove(item);
		//half-wine set to 1/25k chance
		int rand = DataConversions.random(0, 25000);
		if (item.getID() == ItemId.WINE.id()/* && rand == 0*/) {
			player.getInventory().add(new Item(ItemId.HALF_FULL_WINE_JUG.id()));
		} else {
			player.getInventory().add(new Item(ItemId.JUG.id()));
		}
		if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS)) {
			int newStat = player.getSkills().getLevel(Skills.HITPOINTS) + 11;
			if (newStat > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
				newStat = player.getSkills().getMaxStat(Skills.HITPOINTS);
			}
			player.getSkills().setLevel(Skills.HITPOINTS, newStat);
		}
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 3);
	}

	private void handleChocolatyMilk(Player player, Item item) {
		showBubble(player, item);
		player.message("You drink the " + item.getDef().getName().toLowerCase());
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.BUCKET.id()));
		if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS)) {
			int newHp = player.getSkills().getLevel(Skills.HITPOINTS) + 4;
			if (newHp > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITPOINTS);
			}
			player.getSkills().setLevel(Skills.HITPOINTS, newHp);
		}
	}

	private void handleTea(Player player, Item item) {
		showBubble(player, item);
		player.message("You drink the " + item.getDef().getName().toLowerCase());
		player.getInventory().remove(item);
		int changeHp = (player.getSkills().getMaxStat(Skills.HITPOINTS) > 55 ? 3 : 2);
		if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS)) {
			int newHp = player.getSkills().getLevel(Skills.HITPOINTS) + changeHp;
			if (newHp > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
				newHp = player.getSkills().getMaxStat(Skills.HITPOINTS);
			}
			player.getSkills().setLevel(Skills.HITPOINTS, newHp);
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
		showBubble(player, item);
		player.playerServerMessage(MessageType.QUEST, "You drink the beer");
		player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
		player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 4);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 2);
		}
		if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS)) {
			int newStat = player.getSkills().getLevel(Skills.HITPOINTS) + 1;
			if (newStat > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
				newStat = player.getSkills().getMaxStat(Skills.HITPOINTS);
			}
			player.getSkills().setLevel(Skills.HITPOINTS, newStat);
		}
	}

	private void handleGreenmansAle(Player player, Item item) {
		showBubble(player, item);
		player.message("You drink the " + item.getDef().getName() + ".");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		sleep(1200);
		player.message("It has a strange taste.");
		for (int stat = Skills.ATTACK; stat < Skills.HITPOINTS; stat++) {
			player.getSkills().setLevel(stat, player.getSkills().getLevel(stat) - 4);
		}
		if (player.getSkills().getLevel(Skills.HERBLAW) <= player.getSkills().getMaxStat(Skills.HERBLAW)) {
			player.getSkills().setLevel(Skills.HERBLAW, player.getSkills().getLevel(Skills.HERBLAW) + 1);
		}
	}

	private void handleWizardsMindBomb(Player player, Item item) {
		showBubble(player, item);
		player.message("You drink the " + item.getDef().getName() + ".");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		sleep(1200);
		player.message("You feel very strange.");
		for (int stat = Skills.ATTACK; stat < Skills.HITPOINTS; stat++) {
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
		showBubble(player, item);
		player.message("You drink the " + item.getDef().getName() + ".");
		player.message("It tastes foul.");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		sleep(1600);
		player.message("It tastes pretty strong too");
		for (int stat = Skills.ATTACK; stat < Skills.HITPOINTS; stat++) {
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
		player.message("You drink the " + item.getDef().getName() + ".");
		showBubble(player, item);
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		sleep(1200);
		player.message("You feel slightly reinvigorated");
		player.message("And slightly dizzy too.");
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 4);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 2);
		}
		if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS)) {
			int newStat = player.getSkills().getLevel(Skills.HITPOINTS) + 2;
			if (newStat > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
				newStat = player.getSkills().getMaxStat(Skills.HITPOINTS);
			}
			player.getSkills().setLevel(Skills.HITPOINTS, newStat);
		}
	}

	private void handleDragonBitter(Player player, Item item) {
		player.message("You drink the " + item.getDef().getName() + ".");
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		showBubble(player, item);
		sleep(1200);
		player.message("You feel slightly dizzy.");
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 4);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 2);
		}
	}
	
	private void handleGrog(Player player, Item item) {
		player.message("You drink the " + item.getDef().getName() + ".");
		showBubble(player, item);
		player.getInventory().remove(item);
		player.getInventory().add(new Item(ItemId.BEER_GLASS.id()));
		sleep(1200);
		player.message("You feel slightly reinvigorated");
		player.message("And slightly dizzy too.");
		player.getSkills().setLevel(Skills.ATTACK, player.getSkills().getLevel(Skills.ATTACK) - 6);
		if (player.getSkills().getLevel(Skills.STRENGTH) <= player.getSkills().getMaxStat(Skills.STRENGTH)) {
			player.getSkills().setLevel(Skills.STRENGTH, player.getSkills().getLevel(Skills.STRENGTH) + 3);
		}
		if (player.getSkills().getLevel(Skills.HITPOINTS) < player.getSkills().getMaxStat(Skills.HITPOINTS)) {
			int newStat = player.getSkills().getLevel(Skills.HITPOINTS) + 3;
			if (newStat > player.getSkills().getMaxStat(Skills.HITPOINTS)) {
				newStat = player.getSkills().getMaxStat(Skills.HITPOINTS);
			}
			player.getSkills().setLevel(Skills.HITPOINTS, newStat);
		}
	}

	private void handlePoisonChalice(Player player, Item item) {
		player.getInventory().remove(item);
		int chance = DataConversions.random(0, 5);
		int needs;
		switch (chance) {
			case 0: // Hits -1 or -3
				int c = DataConversions.random(0, 1);
				int hp = player.getSkills().getLevel(Skills.HITPOINTS);
				player.getSkills().setLevel(Skills.HITPOINTS, c == 0 ? hp - 1 : hp - 3);
				player.message("That tasted a bit dodgy. You feel a bit ill");
				break;
			case 1: // Hits + 7
				needs = (
					player.getSkills().getMaxStat(Skills.HITPOINTS)
						- player.getSkills().getLevel(Skills.HITPOINTS));
				needs = needs < 7 ? needs : 7;
				player.getSkills().setLevel(Skills.HITPOINTS,
					player.getSkills().getLevel(Skills.HITPOINTS) + needs);
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
					player.getSkills().getMaxStat(Skills.HITPOINTS)
						- player.getSkills().getLevel(Skills.HITPOINTS));
				needs = needs < 30 ? needs : 30;
				player.getSkills().setLevel(Skills.HITPOINTS,
					player.getSkills().getLevel(Skills.HITPOINTS) + needs);
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
					player.getSkills().getMaxStat(Skills.HITPOINTS)
						- player.getSkills().getLevel(Skills.HITPOINTS));
				needs = needs < 30 ? needs : 30;
				player.getSkills().setLevel(Skills.HITPOINTS,
					player.getSkills().getLevel(Skills.HITPOINTS) + needs);
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
