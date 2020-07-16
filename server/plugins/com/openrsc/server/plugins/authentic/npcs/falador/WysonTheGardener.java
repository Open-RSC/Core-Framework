package com.openrsc.server.plugins.authentic.npcs.falador;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class WysonTheGardener implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.WYSON_THE_GARDENER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		int option = 0;
		int op = 0;
		if (player.getQuestStage(Quests.GOBLIN_DIPLOMACY) == -1) {
			npcsay(player, n, "Hey i have heard you are looking for woad leaves");
			op = multi(player, n, "Well yes I am. Can you get some?",
				"Who told you that?");
			if (op == 1) {
				npcsay(player, n, "I can't remember now. Someone who visits this park",
					"I happen to have some woad leaves lying around",
					"Would you like to buy some?");
				int op2 = multi(player, n, "Oh yes please", "No thanks not right now");
				if (op2 == 1) return;
			}
		} else {
			npcsay(player, n, "I am the gardener round here",
				"Do you have any gardening that needs doing?");
			option = multi(player, n, "I'm looking for woad leaves", "Not right now thanks");
		}
		if (option == 0) {
			// from "Well yes I am. Can you get some?"
			if (player.getQuestStage(Quests.GOBLIN_DIPLOMACY) == -1 && op == 0) {
				npcsay(player, n, "Yes I have some somewhere");
				say(player, n, "Can I buy one please?");
				say(player, n, "Can I buy one please?");
			}
			// from "I'm looking for woad leaves"
			else if (player.getQuestStage(Quests.GOBLIN_DIPLOMACY) > 0) {
				npcsay(player, n, "Well luckily for you I may have some around here somewhere");
				say(player, n, "Can I buy one please?");
			}
			npcsay(player, n, "How much are you willing to pay?");
			int sub_option = multi(player, n, "How about 5 coins?", "How about 10 coins?",
				"How about 15 coins?", "How about 20 coins?");
			if (sub_option == 0) {
				npcsay(player, n, "No No thats far too little. Woad leaves are hard to get you know",
					"I used to have plenty but someone kept stealing them off me");
			} else if (sub_option == 1) {
				npcsay(player, n, "No No thats far to little. Woad leaves are hard to get you know",
					"I used to have plenty but someone kept stealing them off me");
			} else if (sub_option == 2) {
				npcsay(player, n, "Mmmm Ok that sounds fair.");
				if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 15)) != -1) {
					give(player, ItemId.WOAD_LEAF.id(), 1);
					player.message("You give wyson 15 coins");
					player.message("Wyson the gardener gives you some woad leaves");
				} else
					say(player, n, "I dont have enough coins to buy the leaves. I'll come back later");
			} else if (sub_option == 3) {
				npcsay(player, n, "Ok that's more than fair.");
				if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20)) != -1) {
					player.message("You give wyson 20 coins");
					player.message("Wyson the gardener gives you some woad leaves");
					give(player, ItemId.WOAD_LEAF.id(), 2);
					npcsay(player, n, "Here have some more you're a generous person");
					player.message("Wyson the gardener gives you some more leaves");
				} else
					say(player, n, "I dont have enough coins to buy the leaves. I'll come back later");
			}
		}
	}

}
