package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Bartender implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BARTENDER_VARROCK.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "What can I do yer for?");
		String[] options = {};
		if (player.getCache().hasKey("barcrawl") && !player.getCache().hasKey("bartwo")
			&& player.getCarriedItems().hasCatalogID(ItemId.BARCRAWL_CARD.id(), Optional.of(false))) {
			options = new String[]{
				"A glass of your finest ale please",
				"Can you recommend anywhere an adventurer might make his fortune?",
				"Do you know where I can get some good equipment?",
				"I'm doing Alfred Grimhand's barcrawl"};
		} else {
			options = new String[]{
				"A glass of your finest ale please",
				"Can you recommend anywhere an adventurer might make his fortune?",
				"Do you know where I can get some good equipment?"};
		}
		int reply = multi(player, n, options);
		if (reply == 0) {
			npcsay(player, n, "No problemo", "That'll be 2 coins");
			if (ifheld(player, ItemId.COINS.id(), 2)) {
				player.message("You buy a pint of beer");
				give(player, ItemId.BEER.id(), 1);
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
			} else
				say(player, n, "Oh dear. I don't seem to have enough money");
		} else if (reply == 1) {
			npcsay(player, n,
				"Ooh I don't know if I should be giving away information",
				"Makes the computer game too easy");
			reply = multi(player, n, false, //do not send over
					"Oh ah well",
				"Computer game? What are you talking about?",
				"Just a small clue?");
			if (reply == 0) {
				say(player, n, "Oh ah well");
			} else if (reply == 1) {
				say(player, n, "Computer game?",
					"What are you talking about?");
				npcsay(player, n, "This world around us..",
					"is all a computer game..", "called Runescape");
				say(
					player,
					n,
					"Nope, still don't understand what you are talking about",
					"What's a computer?");
				npcsay(player, n, "It's a sort of magic box thing,",
					"which can do all sorts of different things");
				say(player, n, "I give up",
					"You're obviously completely mad!");

			} else if (reply == 2) {
				say(player, n, "Just a small clue?");
				npcsay(player, n,
					"Go and talk to the bartender at the Jolly Boar Inn",
					"He doesn't seem to mind giving away clues");
			}
		} else if (reply == 2) {
			npcsay(player, n, "Well, there's the sword shop across the road,",
				"or there's also all sorts of shops up around the market");
		} else if (reply == 3) {
			npcsay(player,
				n,
				"Oh no not another of you guys",
				"These barbarian barcrawls cause too much damage to my bar",
				"You're going to have to pay 50 gold for the Uncle Humphrey's gutrot");
			if (ifheld(player, ItemId.COINS.id(), 50)) {
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 50));
				player.message("You buy some gutrot");
				delay(2);
				player.message("You drink the gutrot");
				delay(2);
				player.message("your insides feel terrible");
				drinkAle(player);
				player.damage(DataConversions.getRandom().nextInt(2) + 1);
				delay(2);
				player.message("The bartender signs your card");
				player.getCache().store("bartwo", true);
				say(player, n, "Blearrgh");
			} else
				say(player, n, "I don't have 50 coins");
		}
	}

	private void drinkAle(Player player) {
		int[] skillIDs = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id(), Skill.SMITHING.id()};
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
			maxStat < 70 ? 7 :
			maxStat < 85 ? 8 : 9;
		currentStat = player.getSkills().getLevel(skillId);
		if (currentStat <= 9) {
			player.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
		}
		else {
			player.getSkills().setLevel(skillId, currentStat - reduction);
		}
	}

}
