package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.external.NpcId;

public class QuestAdvisor implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island quest advisor
	 */

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Greetings traveller",
			"If you're interested in a bit of adventure",
			"I can recommend going on a good quest",
			"There are many secrets to be unconvered",
			"And wrongs to be set right",
			"If you talk to the various characters in the game",
			"Some of them will give you quests");
		playerTalk(p, n, "What sort of quests are there to do?");
		npcTalk(p, n, "If you select the bar graph in the menu bar",
			"And then select the quests tabs",
			"You will see a list of quests",
			"quests you have completed will show up in green",
			"You can only do each quest once");
		int menu = showMenu(p, n, false, "Thank you for the advice", "Can you recommend any quests?");
		if (menu == 0) {
			playerTalk(p, n, "thank you for the advice");
			npcTalk(p, n, "good questing traveller");
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") < 65) {
				p.getCache().set("tutorial", 65);
			}
		} else if (menu == 1) {
			playerTalk(p, n, "Can you recommend any quests?");
			npcTalk(p, n, "Well I hear the cook in Lumbridge castle is having some problems",
				"When you get to Lumbridge, go into the castle there",
				"Find the cook and have a chat with him");
			playerTalk(p, n, "Okay thanks for the advice");
			if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") < 65) {
				p.getCache().set("tutorial", 65);
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.QUEST_ADVISOR.id();
	}

}
