package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Bartender implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 12;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "What can I do yer for?");
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
		int reply = showMenu(p, n, options);
		if (reply == 0) {
			npcTalk(p, n, "No problemo", "That'll be 2 coins");
			if (hasItem(p, 10, 2)) {
				p.message("You buy a pint of beer");
				addItem(p, 193, 1);
				p.getInventory().remove(10, 2);
			} else
				playerTalk(p, n, "Oh dear. I don't seem to have enough money");
		} else if (reply == 1) {
			npcTalk(p, n,
				"Ooh I don't know if I should be giving away information",
				"Makes the computer game too easy");
			reply = showMenu(p, n, false, //do not send over
					"Oh ah well",
				"Computer game? What are you talking about?",
				"Just a small clue?");
			if (reply == 0) {
				playerTalk(p, n, "Oh ah well");
			} else if (reply == 1) {
				playerTalk(p, n, "Computer game?",
					"What are you talking about?");
				npcTalk(p, n, "This world around us..",
					"is all a computer game..", "called Runescape");
				playerTalk(
					p,
					n,
					"Nope, still don't understand what you are talking about",
					"What's a computer?");
				npcTalk(p, n, "It's a sort of magic box thing,",
					"which can do all sorts of different things");
				playerTalk(p, n, "I give up",
					"You're obviously completely mad!");

			} else if (reply == 2) {
				playerTalk(p, n, "Just a small clue?");
				npcTalk(p, n,
					"Go and talk to the bartender at the Jolly Boar Inn",
					"He doesn't seem to mind giving away clues");
			}
		} else if (reply == 2) {
			npcTalk(p, n, "Well, there's the sword shop across the road,",
				"or there's also all sorts of shops up around the market");
		} else if (reply == 3) {
			npcTalk(p,
				n,
				"Oh no not another of you guys",
				"These barbarian barcrawls cause too much damage to my bar",
				"You're going to have to pay 50 gold for the Uncle Humphrey's gutrot");
			if (hasItem(p, 10, 50)) {
				p.getInventory().remove(10, 50);
				p.message("You buy some gutrot");
				sleep(800);
				p.message("You drink the gutrot");
				sleep(800);
				p.message("your insides feel terrible");
				drinkAle(p);
				p.damage(p.getRandom().nextInt(2) + 1);
				sleep(800);
				p.message("The bartender signs your card");
				p.getCache().store("bartwo", true);
				playerTalk(p, n, "Blearrgh");
			} else
				playerTalk(p, n, "I don't have 50 coins");
		}
	}
	
	private void drinkAle(Player p) {
		int[] skillIDs = {SKILLS.ATTACK.id(), SKILLS.DEFENSE.id(), SKILLS.STRENGTH.id(), SKILLS.SMITHING.id()};
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
