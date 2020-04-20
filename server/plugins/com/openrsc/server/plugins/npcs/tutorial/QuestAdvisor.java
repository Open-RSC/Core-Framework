package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;
import static com.openrsc.server.plugins.Functions.multi;

import com.openrsc.server.constants.NpcId;

public class QuestAdvisor implements TalkNpcTrigger {
	/**
	 * Tutorial island quest advisor
	 */

	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Greetings traveller",
			"If you're interested in a bit of adventure",
			"I can recommend going on a good quest",
			"There are many secrets to be unconvered",
			"And wrongs to be set right",
			"If you talk to the various characters in the game",
			"Some of them will give you quests");
		Functions.say(p, n, "What sort of quests are there to do?");
		npcsay(p, n, "If you select the bar graph in the menu bar",
			"And then select the quests tabs",
			"You will see a list of quests",
			"quests you have completed will show up in green",
			"You can only do each quest once");
		int menu = multi(p, n, false, "Thank you for the advice", "Can you recommend any quests?");
		if (menu == 0) {
			Functions.say(p, n, "thank you for the advice");
			npcsay(p, n, "good questing traveller");
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") < 65) {
				p.getCache().set("tutorial", 65);
			}
		} else if (menu == 1) {
			Functions.say(p, n, "Can you recommend any quests?");
			npcsay(p, n, "Well I hear the cook in Lumbridge castle is having some problems",
				"When you get to Lumbridge, go into the castle there",
				"Find the cook and have a chat with him");
			Functions.say(p, n, "Okay thanks for the advice");
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") < 65) {
				p.getCache().set("tutorial", 65);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.QUEST_ADVISOR.id();
	}

}
