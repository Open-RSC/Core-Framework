package com.openrsc.server.plugins.npcs.falador;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class WysonTheGardener implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.WYSON_THE_GARDENER.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		int option = 0;
		int op = 0;
		if (p.getQuestStage(Quests.GOBLIN_DIPLOMACY) == -1) {
			npcTalk(p, n, "Hey i have heard you are looking for woad leaves");
			op = showMenu(p, n, "Well yes I am. Can you get some?",
				"Who told you that?");
			if (op == 1) {
				npcTalk(p, n, "I can't remember now. Someone who visits this park",
					"I happen to have some woad leaves lying around",
					"Would you like to buy some?");
				int op2 = showMenu(p, n, "Oh yes please", "No thanks not right now");
				if (op2 == 1) return;
			}
		} else {
			npcTalk(p, n, "I am the gardener round here",
				"Do you have any gardening that needs doing?");
			option = showMenu(p, n, "I'm looking for woad leaves", "Not right now thanks");
		}
		if (option == 0) {
			// from "Well yes I am. Can you get some?"
			if (p.getQuestStage(Quests.GOBLIN_DIPLOMACY) == -1 && op == 0) {
				npcTalk(p, n, "Yes I have some somewhere");
				playerTalk(p, n, "Can I buy one please?");
				playerTalk(p, n, "Can I buy one please?");
			}
			// from "I'm looking for woad leaves"
			else if (p.getQuestStage(Quests.GOBLIN_DIPLOMACY) > 0) {
				npcTalk(p, n, "Well luckily for you I may have some around here somewhere");
				playerTalk(p, n, "Can I buy one please?");
			}
			npcTalk(p, n, "How much are you willing to pay?");
			int sub_option = showMenu(p, n, "How about 5 coins?", "How about 10 coins?",
				"How about 15 coins?", "How about 20 coins?");
			if (sub_option == 0) {
				npcTalk(p, n, "No No thats far too little. Woad leaves are hard to get you know",
					"I used to have plenty but someone kept stealing them off me");
			} else if (sub_option == 1) {
				npcTalk(p, n, "No No thats far to little. Woad leaves are hard to get you know",
					"I used to have plenty but someone kept stealing them off me");
			} else if (sub_option == 2) {
				npcTalk(p, n, "Mmmm Ok that sounds fair.");
				if (removeItem(p, ItemId.COINS.id(), 15)) {
					addItem(p, ItemId.WOAD_LEAF.id(), 1);
					p.message("You give wyson 15 coins");
					p.message("Wyson the gardener gives you some woad leaves");
				} else
					playerTalk(p, n, "I dont have enough coins to buy the leaves. I'll come back later");
			} else if (sub_option == 3) {
				npcTalk(p, n, "Ok that's more than fair.");
				if (removeItem(p, ItemId.COINS.id(), 20)) {
					p.message("You give wyson 20 coins");
					p.message("Wyson the gardener gives you some woad leaves");
					addItem(p, ItemId.WOAD_LEAF.id(), 2);
					npcTalk(p, n, "Here have some more you're a generous person");
					p.message("Wyson the gardener gives you some more leaves");
				} else
					playerTalk(p, n, "I dont have enough coins to buy the leaves. I'll come back later");
			}
		}
	}

}
