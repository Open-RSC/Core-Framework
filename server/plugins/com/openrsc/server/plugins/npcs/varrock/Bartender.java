package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Bartender implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BARTENDER_VARROCK.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "What can I do yer for?");
		String[] options = {};
		if (p.getCache().hasKey("barcrawl") && !p.getCache().hasKey("bartwo")) {
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
		int reply = multi(p, n, options);
		if (reply == 0) {
			npcsay(p, n, "No problemo", "That'll be 2 coins");
			if (ifheld(p, ItemId.COINS.id(), 2)) {
				p.message("You buy a pint of beer");
				give(p, ItemId.BEER.id(), 1);
				p.getCarriedItems().remove(ItemId.COINS.id(), 2);
			} else
				say(p, n, "Oh dear. I don't seem to have enough money");
		} else if (reply == 1) {
			npcsay(p, n,
				"Ooh I don't know if I should be giving away information",
				"Makes the computer game too easy");
			reply = multi(p, n, false, //do not send over
					"Oh ah well",
				"Computer game? What are you talking about?",
				"Just a small clue?");
			if (reply == 0) {
				say(p, n, "Oh ah well");
			} else if (reply == 1) {
				say(p, n, "Computer game?",
					"What are you talking about?");
				npcsay(p, n, "This world around us..",
					"is all a computer game..", "called Runescape");
				say(
					p,
					n,
					"Nope, still don't understand what you are talking about",
					"What's a computer?");
				npcsay(p, n, "It's a sort of magic box thing,",
					"which can do all sorts of different things");
				say(p, n, "I give up",
					"You're obviously completely mad!");

			} else if (reply == 2) {
				say(p, n, "Just a small clue?");
				npcsay(p, n,
					"Go and talk to the bartender at the Jolly Boar Inn",
					"He doesn't seem to mind giving away clues");
			}
		} else if (reply == 2) {
			npcsay(p, n, "Well, there's the sword shop across the road,",
				"or there's also all sorts of shops up around the market");
		} else if (reply == 3) {
			npcsay(p,
				n,
				"Oh no not another of you guys",
				"These barbarian barcrawls cause too much damage to my bar",
				"You're going to have to pay 50 gold for the Uncle Humphrey's gutrot");
			if (ifheld(p, ItemId.COINS.id(), 50)) {
				p.getCarriedItems().remove(ItemId.COINS.id(), 50);
				p.message("You buy some gutrot");
				delay(800);
				p.message("You drink the gutrot");
				delay(800);
				p.message("your insides feel terrible");
				drinkAle(p);
				p.damage(DataConversions.getRandom().nextInt(2) + 1);
				delay(800);
				p.message("The bartender signs your card");
				p.getCache().store("bartwo", true);
				say(p, n, "Blearrgh");
			} else
				say(p, n, "I don't have 50 coins");
		}
	}

	private void drinkAle(Player p) {
		int[] skillIDs = {Skills.ATTACK, Skills.DEFENSE, Skills.STRENGTH, Skills.SMITHING};
		for (int i = 0; i < skillIDs.length; i++) {
			setAleEffect(p, skillIDs[i]);
		}
	}

	private void setAleEffect(Player p, int skillId) {
		int reduction, currentStat, maxStat;
		maxStat = p.getSkills().getMaxStat(skillId);
		//estimated
		reduction = maxStat < 15 ? 5 :
			maxStat < 40 ? 6 :
			maxStat < 70 ? 7 :
			maxStat < 85 ? 8 : 9;
		currentStat = p.getSkills().getLevel(skillId);
		if (currentStat <= 9) {
			p.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
		}
		else {
			p.getSkills().setLevel(skillId, currentStat - reduction);
		}
	}

}
