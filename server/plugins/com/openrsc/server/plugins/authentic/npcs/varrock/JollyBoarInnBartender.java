package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class JollyBoarInnBartender implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BARTENDER_OUTSIDE_VARROCK.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Yes please?");
		String[] options = {};
		if (player.getCache().hasKey("barcrawl") && !player.getCache().hasKey("barone")
			&& player.getCarriedItems().hasCatalogID(ItemId.BARCRAWL_CARD.id(), Optional.of(false))) {
			options = new String[]{
				"I'll have a beer please",
				"Any hints where I can go adventuring?",
				"Heard any good gossip?",
				"I'm doing Alfred Grimhand's barcrawl"
			};
		} else {
			options = new String[]{
				"I'll have a beer please",
				"Any hints where I can go adventuring?",
				"Heard any good gossip?"
			};
		}
		int reply = multi(player, n, false, //do not send over
			options);
		if (reply == 0) {
			say(player, n, "I'll have a pint of beer please");
			npcsay(player, n, "Ok, that'll be two coins");

			if (ifheld(player, ItemId.COINS.id(), 2)) {
				player.message("You buy a pint of beer");
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
				give(player, ItemId.BEER.id(), 1);
			} else {
				say(player, n, "Oh dear. I don't seem to have enough money");
			}
		} else if (reply == 1) {
			say(player, n, "Any hints on where I can go adventuring?");
			npcsay(player, n,
				"It's funny you should say that",
				"An adventurer passed through here, the other day,",
				"claiming to have found a dungeon full of treasure,",
				"guarded by vicious skeletal warriors",
				"He said he found the entrance in a ruined town",
				"deep in the woods to the west of here, behind the palace",
				"Now how much faith you put in that story is up to you,",
				"but it probably wouldn't do any harm to have a look"
			);
			say(player, n, "Thanks", "I may try that at some point");
		} else if (reply == 2) {
			say(player, n, "Heard any good gossip?");
			npcsay(player, n,
				"I'm not that well up on the gossip out here",
				"I've heard that the bartender in the Blue Moon Inn has gone a little crazy",
				"He keeps claiming he is part of something called a computer game",
				"What that means, I don't know",
				"That's probably old news by now though"
			);
		} else if (reply == 3) {
			say(player, n, "I'm doing Alfred Grimhand's barcrawl");
			npcsay(player, n, "Ah, there seems to be a fair few doing that one these days",
				"My supply of Olde Suspiciouse is starting to run low",
				"It'll cost you 10 coins");
			if (ifheld(player, ItemId.COINS.id(), 10)) {
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 10));
				mes("You buy a pint of Olde Suspiciouse");
				delay(3);
				mes("You gulp it down");
				delay(3);
				mes("Your head is spinning");
				delay(3);
				drinkAle(player);
				mes("The bartender signs your card");
				delay(3);
				player.getCache().store("barone", true);
				say(player, n, "Thanksh very mush");
			} else {
				say(player, n, "I don't have 10 coins right now");
			}
		}
	}

	private void drinkAle(Player player) {
		int[] skillIDs = {Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.MAGIC.id(), Skill.CRAFTING.id(), Skill.MINING.id()};
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
