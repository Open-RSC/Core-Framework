package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class QuestAdvisor implements TalkNpcTrigger {
	/**
	 * Tutorial island quest advisor
	 */

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Greetings traveller",
			"If you're interested in a bit of adventure",
			"I can recommend going on a good quest",
			"There are many secrets to be unconvered",
			"And wrongs to be set right",
			"If you talk to the various characters in the game",
			"Some of them will give you quests");
		say(player, n, "What sort of quests are there to do?");
		npcsay(player, n, "If you select the bar graph in the menu bar",
			"And then select the quests tabs",
			"You will see a list of quests",
			"quests you have completed will show up in green",
			"You can only do each quest once");
		int menu = multi(player, n, false, "Thank you for the advice", "Can you recommend any quests?");
		if (menu == 0) {
			say(player, n, "thank you for the advice");
			npcsay(player, n, "good questing traveller");
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 65) {
				player.getCache().set("tutorial", 65);
			}
		} else if (menu == 1) {
			say(player, n, "Can you recommend any quests?");
			npcsay(player, n, "Well I hear the cook in Lumbridge castle is having some problems",
				"When you get to Lumbridge, go into the castle there",
				"Find the cook and have a chat with him");
			say(player, n, "Okay thanks for the advice");
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 65) {
				player.getCache().set("tutorial", 65);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.QUEST_ADVISOR.id();
	}

}
