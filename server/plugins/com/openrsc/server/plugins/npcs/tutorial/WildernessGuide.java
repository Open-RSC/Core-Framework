package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;
import static com.openrsc.server.plugins.Functions.multi;

import com.openrsc.server.constants.NpcId;

public class WildernessGuide implements TalkNpcTrigger {
	/**
	 * @author Davve
	 * Tutorial island wilderness guide
	 */

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Hi are you someone who likes to fight other players?",
			"Granted it has big risks",
			"but it can be very rewarding too");
		int menu = Functions.multi(p, n, "Yes I'm up for a bit of a fight", "I'd prefer to avoid that");
		if (menu == 0) {
			npcsay(p, n, "Then the wilderness is the place for you",
				"That is the area of the game where you can attack other players",
				"Be careful though",
				"Other players can be a lot more dangerous than monsters",
				"they will be much more persistant in chasing after you",
				"Especially when they hunt in groups");
			optionsDialogue(p, n);
		} else if (menu == 1) {
			npcsay(p, n, "Then don't stray into the wilderness",
				"That is the area of the game where you can attack other players");
			optionsDialogue(p, n);
		}
	}

	private void optionsDialogue_where(Player p, Npc n) {
		Functions.say(p, n, "Where is this wilderness?");
		npcsay(p, n, "Once you get into the main playing area head north",
				"then you will eventually reach the wilderness",
				"The deeper you venture into the wilderness",
				"The greater the level range of players who can attack you",
				"So if you go in really deep",
				"Players much stronger than you can attack you");
	}

	private void optionsDialogue_die(Player p, Npc n) {
		Functions.say(p, n, "What happens when I die?");
		npcsay(p, n, "normally when you die",
				"you will lose all of the items in your inventory",
				"Except the three most valuable",
				"You never keep stackable items like coins and runes",
				"which is why it is a good idea to leave things in the bank",
				"However if you attack another player",
				"You get a skull above your head for twenty minutes",
				"If you die with a skull above your head you lose your entire inventory");
	}

	private void optionsDialogue(Player p, Npc n) {
		int menu = multi(p, n, false, "Where is this wilderness?", "What happens when I die?");
		if (menu == 0) {
			optionsDialogue_where(p, n);
			optionsDialogue_die(p, n);
		} else if (menu == 1) {
			optionsDialogue_die(p, n);
			optionsDialogue_where(p, n);
		}
		if (menu != -1) {
			npcsay(p, n, "Now proceed through the next door");
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") < 70) {
				p.getCache().set("tutorial", 70);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.WILDERNESS_GUIDE.id();
	}

}
