package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.minigames.ALumbridgeCarol;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.isBlackArmGang;
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.isPhoenixGang;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

import java.util.ArrayList;

public class Tramp implements TalkNpcTrigger {
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.TRAMP.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (config().A_LUMBRIDGE_CAROL && ALumbridgeCarol.inPartyRoom(n)) {
			ALumbridgeCarol.partyDialogue(player, n);
			return;
		}
		npcsay(player, n, "Spare some change guv?");

		ArrayList<String> options = new ArrayList<String>();
		options.add("Sorry I haven't got any");
		options.add("Go get a job");
		options.add("Ok here you go");
		options.add("Is there anything down this alleyway?");
		if (config().A_LUMBRIDGE_CAROL) {
			int stage = ALumbridgeCarol.getStage(player);
			if (stage == ALumbridgeCarol.FIND_TRAMP) {
				options.add("I have a job offer for you");
			} else if (stage == ALumbridgeCarol.GET_CLOTHES) {
				options.add("About the clothes...");
			}
		}

		int menu = multi(player, n, options.toArray(new String[0]));

		if (menu == 0) {
			npcsay(player, n, "Thanks anyway");
		} else if (menu == 1) {
			npcsay(player, n, "You startin?");
		} else if (menu == 2) {
			player.getCarriedItems().remove(new Item(ItemId.COINS.id()));
			npcsay(player, n, "Thankyou, thats great");
			int sub_menu = multi(player, n, "No problem",
				"So don't I get some sort of quest hint or something now");
			if (sub_menu == 1) {
				npcsay(player, n, "No that's not why I'm asking for money",
					"I just need to eat");
			}
		} else if (menu == 3) {
			npcsay(player, n, "Yes, there is actually",
				"A notorious gang of thieves and hoodlums",
				"Called the blackarm gang");
			int sub_menu = multi(player, n, "Thanks for the warning",
				"Do you think they would let me join?");
			if (sub_menu == 0) {
				npcsay(player, n, "Don't worry about it");
			} else if (sub_menu == 1) {
				if (isBlackArmGang(player)) {
					npcsay(player, n,
						"I was under the impression you were already a member");
				} else if (isPhoenixGang(player)) {
					npcsay(player, n, "No", "You're a collaborator with the phoenix gang",
						"There's no way they'll let you join");
					int phoen_menu = multi(player, n, "How did you know I was in the phoenix gang?",
						"Any ideas how I could get in there then?");
					if (phoen_menu == 0) {
						npcsay(player, n, "I spend a lot of time on the streets",
							"And you hear those sorta things sometimes");
					} else if (phoen_menu == 1) {
						npcsay(player, n, "Hmm I dunno",
							"Your best bet would probably be to get someone else",
							"Someone who isn't a member of the phoenix gang",
							"To Infiltrate the ranks of the black arm gang",
							"If you find someone",
							"Tell em to come to me first");
						int find_menu = multi(player, n, "Ok good plan", "Like who?");
						if (find_menu == 1) {
							npcsay(player, n, "There's plenty of other adventurers about",
								"Besides yourself",
								"I'm sure if you asked one of them nicely",
								"They would help you");
						}
					}
				} else {
					npcsay(player, n,
						"You never know",
						"You'll find a lady down there called Katrine",
						"Speak to her",
						"But don't upset her, she's pretty dangerous");
					player.getCache().store("spoken_tramp", true);
				}
			}
		} else if (menu == 4 && config().A_LUMBRIDGE_CAROL) {
			ALumbridgeCarol.trampDialogue(player, n);
		}
	}
}
