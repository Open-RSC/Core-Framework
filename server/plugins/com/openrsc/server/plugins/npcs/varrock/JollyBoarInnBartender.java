package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class JollyBoarInnBartender implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 44;
	}
	
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Yes please?");
		String[] options = {};
		if (p.getCache().hasKey("barcrawl") && !p.getCache().hasKey("barone")) {
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
		int reply = showMenu(p, n, options);
		if (reply == 0) {
			npcTalk(p, n, "Ok, that'll be two coins");

			if (hasItem(p, 10, 2)) {
				p.message("You buy a pint of beer");
				p.getInventory().remove(10, 2);
				addItem(p, 193, 1);
			} else {
				playerTalk(p, n, "Oh dear. I don't seem to have enough money");
			}
		} else if (reply == 1) {
			npcTalk(p, n,
				"It's funny you should say that",
				"An adventurer passed through here, the other day,",
				"claiming to have found a dungeon full of treasure,",
				"guarded by vicious skeletal warriors",
				"He said he found the entrance in a ruined town",
				"deep in the woods to the west of here, behind the palace",
				"Now how much faith you put in that story is up to you,",
				"but it probably wouldn't do any harm to have a look"
			);
			playerTalk(p, n, "Thanks", "I may try that at some point");
		} else if (reply == 2) {
			npcTalk(p, n,
				"I'm not that well up on the gossip out here",
				"I've heard that the bartender in the Blue Moon Inn has gone a little crazy",
				"He keeps claiming he is part of something called a computer game",
				"What that means, I don't know",
				"That's probably old news by now though"
			);
		} else if (reply == 3) {
			npcTalk(p, n, "Ah, there seems to be a fair few doing that one these days",
				"My supply of Olde Suspiciouse is starting to run low",
				"It'll cost you 10 coins");
			if (hasItem(p, 10, 10)) {
				p.getInventory().remove(10, 10);
				message(p, "You buy a pint of Olde Suspiciouse",
					"You gulp it down",
					"Your head is spinning");
				drinkAle(p);
				message(p, "The bartender signs your card");
				p.getCache().store("barone", true);
				playerTalk(p, n, "Thanksh very mush");
			} else {
				playerTalk(p, n, "I don't have 10 coins right now");
			}
		}
	}
	
	private void drinkAle(Player p) {
		int[] skillIDs = {SKILLS.ATTACK.id(), SKILLS.DEFENSE.id(), SKILLS.MAGIC.id(), SKILLS.CRAFTING.id(), SKILLS.MINING.id()};
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
			maxStat < 75 ? 7 : 8;
		currentStat = p.getSkills().getLevel(skillId);
		if (currentStat <= 8) {
			p.getSkills().setLevel(skillId, Math.max(currentStat - reduction, 0));
		}
		else {
			p.getSkills().setLevel(skillId, currentStat - reduction);
		}
	}
}
