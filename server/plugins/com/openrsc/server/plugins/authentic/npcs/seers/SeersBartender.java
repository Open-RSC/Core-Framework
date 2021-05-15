package com.openrsc.server.plugins.authentic.npcs.seers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class SeersBartender implements
	TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BARTENDER_SEERS.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Good morning, what would you like?");

		ArrayList<String> options = new ArrayList<>();
		options.add("What do you have?");
		options.add("Beer please");
		if (player.getCache().hasKey("barcrawl")
			&& !player.getCache().hasKey("barfive")
			&& player.getCarriedItems().hasCatalogID(ItemId.BARCRAWL_CARD.id(), Optional.of(false))) {
			options.add("I'm doing Alfred Grimhand's barcrawl");
		}
		options.add("I don't really want anything thanks");
		String[] finalOptions = new String[options.size()];
		int option = multi(player, n, options.toArray(finalOptions));

		if (option == 2) {
			if (player.getCache().hasKey("barcrawl")
				&& !player.getCache().hasKey("barfive")
				&& player.getCarriedItems().hasCatalogID(ItemId.BARCRAWL_CARD.id(), Optional.of(false))) {
				npcsay(player,
					n,
					"Oh you're a barbarian then",
					"Now which of these was the barrels contained the liverbane ale?",
					"That'll be 18 coins please");
				if (ifheld(player, ItemId.COINS.id(), 18)) {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 18));
					mes("The bartender gives you a glass of liverbane ale");
					delay(3);
					mes("You gulp it down");
					delay(3);
					mes("The room seems to be swaying");
					delay(3);
					drinkAle(player);
					mes("The bartender scrawls his signiture on your card");
					delay(3);
					player.getCache().store("barfive", true);
				} else {
					say(player, n, "Sorry I don't have 18 coins");
				}
			}
		}
		else if (option == 1) {
			beerOrderDialog(player, n);
		}
		else if (option == 0) {
			barMenuDialog(player, n);
		}
	}

	private void barMenuDialog(Player player, Npc n) {
		npcsay(player, n, "Well we have beer",
			"Or if you want some food, we have our home made stew and meat pies");

		int option = multi(player, n,
			"Beer please",
			"I'll try the meat pie",
			"Could I have some stew please",
			"I don't really want anything thanks"
		);

		if (option == 0) {
			beerOrderDialog(player, n);
		}
		else if (option == 1) {
			npcsay(player, n, "Ok, that'll be 16 gold");
			if (ifheld(player, ItemId.COINS.id(), 16)) {
				player.message("You buy a nice hot meat pie");
				give(player, ItemId.MEAT_PIE.id(), 1);
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 16));
			} else {
				say(player, n,
					"Oh dear. I don't seem to have enough money");
			}
		}
		else if (option == 2) {
			npcsay(player, n,
				"A bowl of stew, that'll be 20 gold please");
			if (ifheld(player, ItemId.COINS.id(), 20)) {
				player.message("You buy a bowl of home made stew");
				give(player, ItemId.STEW.id(), 1);
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
			} else {
				say(player, n,
					"Oh dear. I don't seem to have enough money");
			}
		}
	}

	private void beerOrderDialog(Player player, Npc n) {
		npcsay(player, n, "one beer coming up",
			"Ok, that'll be two coins");
		if (ifheld(player, ItemId.COINS.id(), 2)) {
			player.message("You buy a pint of beer");
			give(player, ItemId.BEER.id(), 1);
			player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
		} else {
			say(player, n,
				"Oh dear. I don't seem to have enough money");
		}
	}

	private void drinkAle(Player player) {
		int[] skillIDs = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.WOODCUTTING.id(), Skill.FLETCHING.id(), Skill.FIREMAKING.id()};
		for (int i = 0; i < skillIDs.length; i++) {
			setAleEffect(player, skillIDs[i]);
		}
	}

	private void setAleEffect(Player player, int skillId) {
		int reduction, currentStat, maxStat;
		maxStat = player.getSkills().getMaxStat(skillId);
		//estimated
		reduction = maxStat < 15 ? 5 :
			maxStat < 40 ? 6 :
			maxStat < 75 ? 7 : 8;
		currentStat = player.getSkills().getLevel(skillId);
		if (currentStat <= 8) {
			player.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
		}
		else {
			player.getSkills().setLevel(skillId, currentStat - reduction);
		}
	}

}
