package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class WildernessGuide implements TalkNpcTrigger {
	/**
	 * Tutorial island wilderness guide
	 */

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Hi are you someone who likes to fight other players?",
			"Granted it has big risks",
			"but it can be very rewarding too");
		int menu = multi(player, n, "Yes I'm up for a bit of a fight", "I'd prefer to avoid that");
		if (menu == 0) {
			npcsay(player, n, "Then the wilderness is the place for you",
				"That is the area of the game where you can attack other players",
				"Be careful though",
				"Other players can be a lot more dangerous than monsters",
				"they will be much more persistant in chasing after you",
				"Especially when they hunt in groups");
			optionsDialogue(player, n);
		} else if (menu == 1) {
			npcsay(player, n, "Then don't stray into the wilderness",
				"That is the area of the game where you can attack other players");
			optionsDialogue(player, n);
		}
	}

	private void optionsDialogue_where(Player player, Npc n) {
		say(player, n, "Where is this wilderness?");
		npcsay(player, n, "Once you get into the main playing area head north",
				"then you will eventually reach the wilderness",
				"The deeper you venture into the wilderness",
				"The greater the level range of players who can attack you",
				"So if you go in really deep",
				"Players much stronger than you can attack you");
	}

	private void optionsDialogue_die(Player player, Npc n) {
		say(player, n, "What happens when I die?");
		npcsay(player, n, "normally when you die",
				"you will lose all of the items in your inventory",
				"Except the three most valuable",
				"You never keep stackable items like coins and runes",
				"which is why it is a good idea to leave things in the bank",
				"However if you attack another player",
				"You get a skull above your head for twenty minutes",
				"If you die with a skull above your head you lose your entire inventory");
	}

	private void optionsDialogue(Player player, Npc n) {
		int menu = multi(player, n, false, "Where is this wilderness?", "What happens when I die?");
		if (menu == 0) {
			optionsDialogue_where(player, n);
			optionsDialogue_die(player, n);
		} else if (menu == 1) {
			optionsDialogue_die(player, n);
			optionsDialogue_where(player, n);
		}
		if (menu != -1) {
			npcsay(player, n, "Now proceed through the next door");
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 70) {
				player.getCache().set("tutorial", 70);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.WILDERNESS_GUIDE.id();
	}

}
