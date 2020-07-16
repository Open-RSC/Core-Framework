package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class CommunityInstructor implements TalkNpcTrigger {
	/**
	 * Tutorial island community instructor
	 */
	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "You're almost ready to go out into the main game area",
			"When you get out there",
			"You will be able to interact with thousands of other players");
		int menu = multi(player, n, "How can I communicate with other players?", "Are there rules on ingame behaviour?");
		if (menu == 0) {
			communicateDialogue(player, n);
		} else if (menu == 1) {
			behaviourDialogue(player, n);
		}
	}

	private void communicateDialogue(Player player, Npc n) {
		npcsay(player, n, "typing in the game window will bring up chat",
			"Which players in the nearby area will be able to see",
			"If you want to speak to a particular friend anywhere in the game",
			"You will be able to select the smiley face icon",
			"then click to add a friend, and type in your friend's name",
			"If that player is logged in on the same world as you",
			"their name will go green",
			"If they are logged in on a different world their name will go yellow",
			"clicking on their name will allow you to send a message");
		int menu2 = multi(player, n, "Are there rules on ingame behaviour?", "goodbye then");
		if (menu2 == 0) {
			behaviourDialogue(player, n);
		} else if (menu2 == 1) {
			npcsay(player, n, "Good luck");
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 100) {
				player.getCache().set("tutorial", 100);
			}
		}
	}

	private void behaviourDialogue(Player player, Npc n) {
		npcsay(player, n, "Yes you should read the rules of conduct on our front page",
			"To make sure you do nothing to get yourself banned",
			"but as general guide always try to be courteous to people in game",
			"Remember the people in the game are real people somewhere",
			"With real feelings",
			"If you go round being abusive or causing trouble",
			"your character could quickly be the one in trouble");
		int menu3 = multi(player, n, "How can I communicate with other players?", "goodbye then");
		if (menu3 == 0) {
			communicateDialogue(player, n);
		} else if (menu3 == 1) {
			npcsay(player, n, "Good luck");
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 100) {
				player.getCache().set("tutorial", 100);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.COMMUNITY_INSTRUCTOR.id();
	}

}
