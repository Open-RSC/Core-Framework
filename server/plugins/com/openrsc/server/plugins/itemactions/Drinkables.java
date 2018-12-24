package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.Bubble;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

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
		player.setConsumeTimer(1200);
		switch (item.getID()) {
		case 2106:
			if(player.getCache().hasKey("elixir_time") && player.getElixir() > 0) {
				player.message("You can't drink more of this elixir, you need to wait till your active time has ended");
				return;
			}
			player.message("You drink the mysterious elixir");
			showBubble(player, item);
			player.getInventory().remove(item.getID(), 1);
			player.addElixir(7200);
			ActionSender.sendElixirTimer(player, player.getElixir());
			sleep(1200);
			player.message("it has a strange taste");
			break;
		case 1253:
			player.message("Are you sure you want to drink this?");
			int drink = showMenu(player,
					"Yes, I'm sure...",
					"No, I've had second thoughts...");
			if(drink == 0) {
				player.message("You drink the potion...");
				player.getInventory().replace(1253, 465);
				if(!player.getCache().hasKey("gujuo_potion")) {
					player.getCache().store("gujuo_potion", true);
				}
				playerTalk(player, null, "Mmmm.....");
				sleep(1100);
				player.message("It tastes sort of strange...like fried oranges...");
				playerTalk(player, null, ".....!.....");
				sleep(1100);
				message(player, 1300, "You feel somehow different...");
				playerTalk(player, null, "Let's just hope that this isn't a placibo!");
			} else if(drink == 1) {
				player.message("You decide against drinking the potion...");
			}
			break;
		case 876: // alcohol - brandy
		case 869: // alcohol - vodka
		case 870: // alcohol - gin
		case 868: // alcohol - whisky
			player.playerServerMessage(MessageType.QUEST, "You drink the " + item.getDef().getName().toLowerCase());
			player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
			player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
			if(item.getID() == 868) 
				player.getSkills().setLevel(0, player.getSkills().getLevel(0) - 6);
			else 
				player.getSkills().setLevel(0, player.getSkills().getLevel(0) - 3);
			if (player.getSkills().getLevel(2) <= player.getSkills().getMaxStat(2)) {
				player.getSkills().setLevel(2, player.getSkills().getLevel(2) + 5);
			}
			final boolean heals = player.getSkills().getLevel(3) < player.getSkills().getMaxStat(3);
			if (heals) {
				int newHp = player.getSkills().getLevel(3) + 4;
				if (newHp > player.getSkills().getMaxStat(3)) {
					newHp = player.getSkills().getMaxStat(3);
				}
				player.getSkills().setLevel(3, newHp);
			}
			player.getInventory().remove(item);
			break;
		case 853:// half cocktail glass
		case 854:// full cocktail glass
		case 867:// odd looking cocktail
			player.getSkills().setLevel(0, player.getSkills().getLevel(0) - 3);
			player.getSkills().setLevel(1, player.getSkills().getLevel(1) - 1);
			player.getSkills().setLevel(2, player.getSkills().getLevel(2) - 4);
			player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
			player.playerServerMessage(MessageType.QUEST, "It tastes awful..yuck");
			player.getInventory().remove(item);
			player.getInventory().add(new Item(833));
			checkAndRemoveBlurberry(player, true);
			break;
		case 937: // fruit blast
		case 866: // fruit blast
		case 940: // pineapple punch
		case 879: // pineapple punch
			if (player.getSkills().getLevel(3) < player.getSkills().getMaxStat(3)) {
				int newHp = player.getSkills().getLevel(3) + 8;
				if (newHp > player.getSkills().getMaxStat(3)) {
					newHp = player.getSkills().getMaxStat(3);
				}
				player.getSkills().setLevel(3, newHp);
			}
			player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
			player.playerServerMessage(MessageType.QUEST, "yum ..it tastes great");
			player.playerServerMessage(MessageType.QUEST, "You feel reinvigorated");
			player.getInventory().remove(item);
			player.getInventory().add(new Item(833));
			break;
		case 938: // blurberry special
		case 877: // blurberry special
		case 939: // wizard blizzard
		case 878: // wizard blizzard
		case 941: // SGG
		case 874: // SGG
		case 942: // chocolate saturday
		case 875: // chocolate saturday
		case 943: // drunk dragon
		case 872: // drunk dragon
			if (player.getSkills().getLevel(3) < player.getSkills().getMaxStat(3)) {
				int newHp = player.getSkills().getLevel(3) + 5;
				if (newHp > player.getSkills().getMaxStat(3)) {
					newHp = player.getSkills().getMaxStat(3);
				}
				player.getSkills().setLevel(3, newHp);
			}
			player.getSkills().setLevel(0, player.getSkills().getLevel(0) - 4);
			if (player.getSkills().getLevel(2) <= player.getSkills().getMaxStat(2)) {
				player.getSkills().setLevel(2, player.getSkills().getLevel(2) + 6);
			}
			player.playerServerMessage(MessageType.QUEST, "You drink the cocktail");
			player.playerServerMessage(MessageType.QUEST, "yum ..it tastes great");
			player.playerServerMessage(MessageType.QUEST, "although you feel slightly dizzy");
			player.getInventory().remove(item);
			player.getInventory().add(new Item(833));
			break;
		case 180: // bad wine
			player.message("You drink the bad wine");
			showBubble(player, item);

			player.getInventory().remove(item);
			player.getInventory().add(new Item(140));

			player.getSkills().setLevel(0, player.getSkills().getLevel(0) - 3);
			sleep(1200);
			player.message("You start to feel sick");
			break;
		case 246: // Half Wine
		case 142: // Wine
			showBubble(player, item);
			player.playerServerMessage(MessageType.QUEST, "You drink the wine");
			player.playerServerMessage(MessageType.QUEST, "It makes you feel a bit dizzy");
			player.getInventory().remove(item);
			//half-wine set to 1/25k chance
			int rand = DataConversions.random(0, 25000);
			if(item.getID() == 142 && rand == 0) {
				player.getInventory().add(new Item(246));
			}
			else {
				player.getInventory().add(new Item(140));
			}
			if (player.getSkills().getLevel(3) < player.getSkills().getMaxStat(3)) {
				int newStat = player.getSkills().getLevel(3) + 11;
				if (newStat > player.getSkills().getMaxStat(3)) {
					player.getSkills().setLevel(3, player.getSkills().getMaxStat(3));
				} else {
					player.getSkills().setLevel(3, newStat);
				}
			}
			player.getSkills().setLevel(0, player.getSkills().getLevel(0) - 3);
			break;
		case 770: //chocolaty milk
			showBubble(player, item);
			player.message("You drink the " + item.getDef().getName().toLowerCase());
			player.getInventory().remove(item);
			player.getInventory().add(new Item(21));
			if (player.getSkills().getLevel(3) < player.getSkills().getMaxStat(3)) {
				int newHp = player.getSkills().getLevel(3) + 4;
				if (newHp > player.getSkills().getMaxStat(3)) {
					newHp = player.getSkills().getMaxStat(3);
				}
				player.getSkills().setLevel(3, newHp);
			}
			break;
		case 739: // Tea
			showBubble(player, item);
			player.message("You drink the " + item.getDef().getName().toLowerCase());
			player.getInventory().remove(item);
			int changeHp = (player.getSkills().getMaxStat(3) > 55 ? 3 : 2);
			if (player.getSkills().getLevel(3) < player.getSkills().getMaxStat(3)) {
				int newHp = player.getSkills().getLevel(3) + changeHp;
				if (newHp > player.getSkills().getMaxStat(3)) {
					newHp = player.getSkills().getMaxStat(3);
				}
				player.getSkills().setLevel(3, newHp);
			}
			int changeAtt = (player.getSkills().getMaxStat(0) > 55 ? 3 : 2);
			int maxWithTea = (player.getSkills().getMaxStat(0) + changeAtt);
			if (maxWithTea - player.getSkills().getLevel(0) < changeAtt) {
				changeAtt = maxWithTea - player.getSkills().getLevel(0);
			}
			if (player.getSkills().getLevel(
					0) <= (player.getSkills().getMaxStat(0) + (player.getSkills().getMaxStat(0) > 55 ? 3 : 2))) {
				player.getSkills().setLevel(0, player.getSkills().getLevel(0) + changeAtt);
			}
			break;
		case 193: // Beer
			showBubble(player, item);
			player.playerServerMessage(MessageType.QUEST, "You drink the beer");
			player.playerServerMessage(MessageType.QUEST, "You feel slightly reinvigorated");
			player.playerServerMessage(MessageType.QUEST, "And slightly dizzy too");
			player.getInventory().remove(item);
			player.getInventory().add(new Item(620));
			player.getSkills().setLevel(0, player.getSkills().getLevel(0) - 4);
			if (player.getSkills().getLevel(2) <= player.getSkills().getMaxStat(2)) {
				player.getSkills().setLevel(2, player.getSkills().getLevel(2) + 2);
			}
			break;
		case 830: // Greenmans Ale
			showBubble(player, item);
			player.message("You drink the " + item.getDef().getName() + ".");
			player.getInventory().remove(item);
			player.getInventory().add(new Item(620));
			sleep(1200);
			player.message("It has a strange taste.");
			for (int stat = 0; stat < 3; stat++) {
				player.getSkills().setLevel(stat, player.getSkills().getLevel(stat) - 4);
			}
			if (player.getSkills().getLevel(15) <= player.getSkills().getMaxStat(15)) {
				player.getSkills().setLevel(15, player.getSkills().getLevel(15) + 1);
			}
			break;
		case 268: // Mind Bomb
			showBubble(player, item);
			player.message("You drink the " + item.getDef().getName() + ".");
			player.getInventory().remove(item);
			player.getInventory().add(new Item(620));
			sleep(1200);
			player.message("You feel very strange.");
			for (int stat = 0; stat < 3; stat++) {
				player.getSkills().setLevel(stat, player.getSkills().getLevel(stat) - 4);
			}
			int change = (player.getSkills().getMaxStat(6) > 55 ? 3 : 2);
			int maxWithBomb = (player.getSkills().getMaxStat(6) + change);
			if (maxWithBomb - player.getSkills().getLevel(6) < change) {
				change = maxWithBomb - player.getSkills().getLevel(6);
			}
			if (player.getSkills().getLevel(
					6) <= (player.getSkills().getMaxStat(6) + (player.getSkills().getMaxStat(6) > 55 ? 3 : 2))) {
				player.getSkills().setLevel(6, player.getSkills().getLevel(6) + change);
			}
			break;
		case 269: // Dwarven Stout
			showBubble(player, item);
			player.message("You drink the " + item.getDef().getName() + ".");
			player.message("It tastes foul.");
			player.getInventory().remove(item);
			player.getInventory().add(new Item(620));
			sleep(1600);
			player.message("It tastes pretty strong too");
			for (int stat = 0; stat < 3; stat++) {
				player.getSkills().setLevel(stat, player.getSkills().getLevel(stat) - 4);
			}
			if (player.getSkills().getLevel(13) <= player.getSkills().getMaxStat(13)) {
				player.getSkills().setLevel(13, player.getSkills().getLevel(13) + 1);
			}
			if (player.getSkills().getLevel(14) <= player.getSkills().getMaxStat(14)) {
				player.getSkills().setLevel(14, player.getSkills().getLevel(14) + 1);
			}
			break;
		case 267: // Asgarnian Ale
			player.message("You drink the " + item.getDef().getName() + ".");
			showBubble(player, item);
			player.getInventory().remove(item);
			player.getInventory().add(new Item(620));
			sleep(1200);
			player.message("You feel slightly reinvigorated");
			player.message("And slightly dizzy too.");
			player.getSkills().setLevel(0, player.getSkills().getLevel(0) - 4);
			if (player.getSkills().getLevel(2) <= player.getSkills().getMaxStat(2)) {
				player.getSkills().setLevel(2, player.getSkills().getLevel(2) + 2);
			}
			break;
		case 829: // Dragon Bitter
			player.message("You drink the " + item.getDef().getName() + ".");
			player.getInventory().remove(item);
			player.getInventory().add(new Item(620));
			showBubble(player, item);
			sleep(1200);
			player.message("You feel slightly dizzy.");
			player.getSkills().setLevel(0, player.getSkills().getLevel(0) - 4);
			if (player.getSkills().getLevel(2) <= player.getSkills().getMaxStat(2)) {
				player.getSkills().setLevel(2, player.getSkills().getLevel(2) + 2);
			}
			break;

		case 737: // Poison chalice
			player.getInventory().remove(item);
			int chance = DataConversions.random(0, 5);
			int needs;
			switch (chance) {
				case 0: // Hits -1 or -3
					int c = DataConversions.random(0, 1);
					int hp = player.getSkills().getLevel(player.getSkills().HITPOINTS);
					player.getSkills().setLevel(player.getSkills().HITPOINTS, c == 0 ? hp - 1 : hp - 3);
					player.message("That tasted a bit dodgy. You feel a bit ill");
					break;
				case 1: // Hits + 7
					needs = (
						player.getSkills().getMaxStat(player.getSkills().HITPOINTS)
						- player.getSkills().getLevel(player.getSkills().HITPOINTS));
					needs = needs < 7 ? needs : 7;
					player.getSkills().setLevel(player.getSkills().HITPOINTS,
						player.getSkills().getLevel(player.getSkills().HITPOINTS) + needs);
					player.message("It heals some health");
					break;
				case 2: // Crafting +1 Attack & Defence -1
					needs = (
						player.getSkills().getMaxStat(player.getSkills().CRAFTING) + 1
						- player.getSkills().getLevel(player.getSkills().CRAFTING));
					needs = needs < 4 ? needs : 4;
					player.getSkills().setLevel(player.getSkills().CRAFTING,
						player.getSkills().getLevel(player.getSkills().CRAFTING) + needs);
					player.getSkills().setLevel(player.getSkills().ATTACK,
						player.getSkills().getLevel(player.getSkills().ATTACK) - 1);
					player.getSkills().setLevel(player.getSkills().DEFENCE,
						player.getSkills().getLevel(player.getSkills().DEFENCE) - 1);
					player.message("You feel a little strange");
					break;
				case 3: // Hits +? Thieving + 1
					needs = (
						player.getSkills().getMaxStat(player.getSkills().HITPOINTS)
						- player.getSkills().getLevel(player.getSkills().HITPOINTS));
					needs = needs < 30 ? needs : 30;
					player.getSkills().setLevel(player.getSkills().HITPOINTS,
						player.getSkills().getLevel(player.getSkills().HITPOINTS) + needs);
					needs = (
						player.getSkills().getMaxStat(player.getSkills().THIEVING) + 1
						- player.getSkills().getLevel(player.getSkills().THIEVING));
					needs = needs < 1 ? needs : 1;
					player.getSkills().setLevel(player.getSkills().THIEVING,
						player.getSkills().getLevel(player.getSkills().THIEVING) + needs);
					player.message("You feel a lot better");
					break;
				case 4: // Hits +? Attack, Defence, Strength +4
					needs = (
						player.getSkills().getMaxStat(player.getSkills().HITPOINTS)
						- player.getSkills().getLevel(player.getSkills().HITPOINTS));
					needs = needs < 30 ? needs : 30;
					player.getSkills().setLevel(player.getSkills().HITPOINTS,
						player.getSkills().getLevel(player.getSkills().HITPOINTS) + needs);
					needs = (
						player.getSkills().getMaxStat(player.getSkills().ATTACK) + 4
						- player.getSkills().getLevel(player.getSkills().ATTACK));
					needs = needs < 4 ? needs : 4;
					player.getSkills().setLevel(player.getSkills().ATTACK,
						player.getSkills().getLevel(player.getSkills().ATTACK) + needs);
					needs = (
						player.getSkills().getMaxStat(player.getSkills().STRENGTH) + 4
						- player.getSkills().getLevel(player.getSkills().STRENGTH));
					needs = needs < 4 ? needs : 4;
					player.getSkills().setLevel(player.getSkills().STRENGTH,
						player.getSkills().getLevel(player.getSkills().STRENGTH) + needs);
					needs = (
						player.getSkills().getMaxStat(player.getSkills().DEFENCE) + 4
						- player.getSkills().getLevel(player.getSkills().DEFENCE));
					needs = needs < 4 ? needs : 4;
					player.getSkills().setLevel(player.getSkills().DEFENCE,
						player.getSkills().getLevel(player.getSkills().DEFENCE) + needs);
					player.message("Wow that was an amazing!! You feel really invigorated");
					break;
				case 5: // No effect
					player.message("It has a slight taste of apricot");
					break;
			}
			break;
		case 221: // Strength Potion - 4 dose
			useNormalPotion(player, item, 2, 10, 2, 222, 3);
			break;
		case 222: // Strength Potion - 3 dose
			useNormalPotion(player, item, 2, 10, 2, 223, 2);
			break;
		case 223: // Strength Potion - 2 dose
			useNormalPotion(player, item, 2, 10, 2, 224, 1);
			break;
		case 224: // Strength Potion - 1 dose
			useNormalPotion(player, item, 2, 10, 2, 465, 0);
			break;
		case 474: // attack Potion - 3 dose
			useNormalPotion(player, item, 0, 10, 2, 475, 2);
			break;
		case 475: // attack Potion - 2 dose
			useNormalPotion(player, item, 0, 10, 2, 476, 1);
			break;
		case 476: // attack Potion - 1 dose
			useNormalPotion(player, item, 0, 10, 2, 465, 0);
			break;
		case 477: // stat restoration Potion - 3 dose
			useStatRestorePotion(player, item, 478, 2);
			break;
		case 478: // stat restoration Potion - 2 dose
			useStatRestorePotion(player, item, 479, 1);
			break;
		case 479: // stat restoration Potion - 1 dose
			useStatRestorePotion(player, item, 465, 0);
			break;
		case 480: // defense Potion - 3 dose
			useNormalPotion(player, item, 1, 10, 2, 481, 2);
			break;
		case 481: // defense Potion - 2 dose
			useNormalPotion(player, item, 1, 10, 2, 482, 1);
			break;
		case 482: // defense Potion - 1 dose
			useNormalPotion(player, item, 1, 10, 2, 465, 0);
			break;
		case 483: // restore prayer Potion - 3 dose
			usePrayerPotion(player, item, 484, 2);
			break;
		case 484: // restore prayer Potion - 2 dose
			usePrayerPotion(player, item, 485, 1);
			break;
		case 485: // restore prayer Potion - 1 dose
			usePrayerPotion(player, item, 465, 0);
			break;
		case 486: // Super attack Potion - 3 dose
			useNormalPotion(player, item, 0, 15, 4, 487, 2);
			break;
		case 487: // Super attack Potion - 2 dose
			useNormalPotion(player, item, 0, 15, 4, 488, 1);
			break;
		case 488: // Super attack Potion - 1 dose
			useNormalPotion(player, item, 0, 15, 4, 465, 0);
			break;
		case 489: // fishing Potion - 3 dose
			useFishingPotion(player, item, 490, 2);
			break;
		case 490: // fishing Potion - 2 dose
			useFishingPotion(player, item, 491, 1);
			break;
		case 491: // fishing Potion - 1 dose
			useFishingPotion(player, item, 465, 0);
			break;
		case 492: // Super strength Potion - 3 dose
			useNormalPotion(player, item, 2, 15, 4, 493, 2);
			break;
		case 493: // Super strength Potion - 2 dose
			useNormalPotion(player, item, 2, 15, 4, 494, 1);
			break;
		case 494: // Super strength Potion - 1 dose
			useNormalPotion(player, item, 2, 15, 4, 465, 0);
			break;
		case 495: // Super defense Potion - 3 dose
			useNormalPotion(player, item, 1, 15, 4, 496, 2);
			break;
		case 496: // Super defense Potion - 2 dose
			useNormalPotion(player, item, 1, 15, 4, 497, 1);
			break;
		case 497: // Super defense Potion - 1 dose
			useNormalPotion(player, item, 1, 15, 4, 465, 0);
			break;
		case 498: // ranging Potion - 3 dose
			useNormalPotion(player, item, 4, 10, 2, 499, 2);
			break;
		case 499: // ranging Potion - 2 dose
			useNormalPotion(player, item, 4, 10, 2, 500, 1);
			break;
		case 500: // ranging Potion - 1 dose
			useNormalPotion(player, item, 4, 10, 2, 465, 0);
			break;
		case 566: // cure poison potion - 3 dose
			useCurePotion(player, item, 567, 2);
			break;
		case 567: // rcure poison potion - 2 dose
			useCurePotion(player, item, 568, 1);
			break;
		case 568: // cure poison potion - 1 dose
			useCurePotion(player, item, 465, 0);
			break;
		case 569: // poison antidote potion - 3 dose
			usePoisonAntidotePotion(player, item, 570, 2);
			break;
		case 570: // poison antidote potion - 2 dose
			usePoisonAntidotePotion(player, item, 571, 1);
			break;
		case 571: // poison antidote potion - 1 dose
			usePoisonAntidotePotion(player, item, 465, 0);
			break;
		case 963: // Zamorak potion - 3 dose
			useZamorakPotion(player, item, 964, 2);
			break;
		case 964: // Zamorak potion - 2 dose
			useZamorakPotion(player, item, 965, 1);
			break;
		case 965: // Zamorak potion - 1 dose
			useZamorakPotion(player, item, 465, 0);
			break;
		case 2116: // EASTER - Super attack Potion - 3 dose
			useNormalPotion(player, item, 0, 15, 4, 2117, 2);
			break;
		case 2117: // EASTER - Super attack Potion - 2 dose
			useNormalPotion(player, item, 0, 15, 4, 2118, 1);
			break;
		case 2118: // EASTER - Super attack Potion - 1 dose
			useNormalPotion(player, item, 0, 15, 4, 465, 0);
			break;
		case 2119: // EASTER - Super strength Potion - 3 dose
			useNormalPotion(player, item, 2, 15, 4, 2120, 2);
			break;
		case 2120: // EASTER - Super strength Potion - 2 dose
			useNormalPotion(player, item, 2, 15, 4, 2121, 1);
			break;
		case 2121: // EASTER - Super strength Potion - 1 dose
			useNormalPotion(player, item, 2, 15, 4, 465, 0);
			break;	
		case 2122: // EASTER - Super defense Potion - 3 dose
			useNormalPotion(player, item, 1, 15, 4, 2123, 2);
			break;
		case 2123: // EASTER - Super defense Potion - 2 dose
			useNormalPotion(player, item, 1, 15, 4, 2124, 1);
			break;
		case 2124: // EASTER - Super defense Potion - 1 dose
			useNormalPotion(player, item, 1, 15, 4, 465, 0);
			break;
		default:
			player.message("Nothing interesting happens");
			return;
		}
	}

	private void useFishingPotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef().getName().toLowerCase());
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		player.getSkills().setLevel(10, player.getSkills().getMaxStat(10) + 3);
		sleep(1200);
		if(left <= 0) {
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
		if(dosesLeft <= 0) {
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
		if(dosesLeft <= 0) {
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
		if(left <= 0) {
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
		int strengthBoost = (int) 15;
		int defenceDecrease = (int) 12.5;
		int hitsDecrease = (int) 10;
		// ugly but right formula.
		if (player.getSkills().getLevel(0) > player.getSkills().getMaxStat(0)) {
			int baseStat = player.getSkills().getMaxStat(0);
			int newStat = baseStat + DataConversions.roundUp(player.getSkills().getMaxStat(0) / 100D * attackBoost);
			if (newStat > player.getSkills().getLevel(0)) {
				player.getSkills().setLevel(0, newStat);
			}
		} else {
			int baseStat = player.getSkills().getLevel(0);
			int newStat = baseStat + DataConversions.roundUp(player.getSkills().getMaxStat(0) / 100D * attackBoost);
			if (newStat > player.getSkills().getLevel(0)) {
				player.getSkills().setLevel(0, newStat);
			}
		}
		if (player.getSkills().getLevel(2) > player.getSkills().getMaxStat(2)) {
			int baseStat = player.getSkills().getMaxStat(2);
			int newStat = baseStat + DataConversions.roundUp(player.getSkills().getMaxStat(2) / 100D * strengthBoost);
			if (newStat > player.getSkills().getLevel(2)) {
				player.getSkills().setLevel(2, newStat);
			}
		} else {
			int baseStat = player.getSkills().getLevel(2);
			int newStat = baseStat + DataConversions.roundUp(player.getSkills().getMaxStat(2) / 100D * strengthBoost);
			if (newStat > player.getSkills().getLevel(2)) {
				player.getSkills().setLevel(2, newStat);
			}
		}
		if (player.getSkills().getLevel(1) < player.getSkills().getMaxStat(1)) {
			int baseStat = player.getSkills().getMaxStat(1);
			int newStat = baseStat - DataConversions.roundUp(player.getSkills().getMaxStat(1) / 100D * defenceDecrease);
			if (newStat < player.getSkills().getLevel(1)) {
				player.getSkills().setLevel(1, newStat);
			}
		} else {
			int baseStat = player.getSkills().getLevel(1);
			int newStat = baseStat - DataConversions.roundUp(player.getSkills().getMaxStat(1) / 100D * defenceDecrease);
			if (newStat < player.getSkills().getLevel(1)) {
				player.getSkills().setLevel(1, newStat);
			}
		}
		if (player.getSkills().getLevel(3) < player.getSkills().getMaxStat(3)) {
			int baseStat = player.getSkills().getMaxStat(3);
			int newStat = baseStat - DataConversions.roundUp(player.getSkills().getMaxStat(3) / 100D * hitsDecrease);
			if (newStat < player.getSkills().getLevel(3)) {
				player.getSkills().setLevel(3, newStat);
			}
		} else {
			int baseStat = player.getSkills().getLevel(3);
			int newStat = baseStat - DataConversions.roundUp(player.getSkills().getMaxStat(3) / 100D * hitsDecrease);
			if (newStat < player.getSkills().getLevel(3)) {
				player.getSkills().setLevel(3, newStat);
			}
		}
		sleep(1200);
		if(left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void usePrayerPotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef().getName().toLowerCase());
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		int newPrayer = player.getSkills().getLevel(5) + (int) ((player.getSkills().getMaxStat(5) * 0.25) + 7);
		if (newPrayer > player.getSkills().getMaxStat(5)) {
			newPrayer = player.getSkills().getMaxStat(5);
		}
		player.getSkills().setLevel(5, newPrayer);
		sleep(1200);
		if(left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	private void useStatRestorePotion(Player player, final Item item, final int newItem, final int left) {
		player.message("You drink some of your " + item.getDef().getName().toLowerCase());
		player.getInventory().remove(item);
		player.getInventory().add(new Item(newItem));
		for (int i = 0; i < 7; i++) {
			if (i == 3 || i == 5) {
				continue;
			}
			if(player.getSkills().getLevel(i) > player.getSkills().getMaxStat(i)) {
				continue;
			}
			int newStat = player.getSkills().getLevel(i) + (int) ((player.getSkills().getMaxStat(i) * 0.3) + 10);
			if (newStat > player.getSkills().getMaxStat(i)) {
				newStat = player.getSkills().getMaxStat(i);
			}
			if(newStat < 14) {
				player.getSkills().setLevel(i, player.getSkills().getMaxStat(i));
			} else {
				player.getSkills().setLevel(i, newStat);
			}
		}
		sleep(1200);
		if(left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}
}
