package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class Baraek implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		int menu;
		boolean bargained = false;
		boolean hasFur = player.getCarriedItems().hasCatalogID(ItemId.FUR.id());
		if (canGetInfoGang(player) && hasFur) {
			menu = multi(player, n, false, //do not send over
				"Can you tell me where I can find the phoenix gang?",
				"Can you sell me some furs?",
				"Hello. I am in search of a quest",
				"Would you like to buy my fur?");
		} else if (canGetInfoGang(player) && !hasFur) {
			menu = multi(player, n, false, //do not send over
				"Can you tell me where I can find the phoenix gang?",
				"Can you sell me some furs?",
				"Hello. I am in search of a quest");
		} else if (hasFur) {
			menu = multi(player, n, false, //do not send over
				"Can you sell me some furs?",
				"Hello. I am in search of a quest",
				"Would you like to buy my fur?");
			if (menu >= 0) {
				menu += 1;
			}
		} else {
			menu = multi(player, n, false, //do not send over
				"Can you sell me some furs?",
				"Hello. I am in search of a quest");
			if (menu >= 0) {
				menu += 1;
			}
		}
		if (menu == 0) {
			say(player, n, "Can you tell me where I can find the phoenix gang?");
			npcsay(player, n, "Sh Sh, not so loud",
				"You don't want to get me in trouble");
			say(player, n, "So do you know where they are?");
			npcsay(player, n, "I may do",
				"Though I don't want to get into trouble for revealing their hideout",
				"Now if I was say 20 gold coins richer",
				"I may happen to be more inclined to take that sort of risk");
			int sub_menu = multi(player, n, "Okay have 20 gold coins",
				"No I don't like things like bribery",
				"Yes I'd like to be 20 gold coins richer too");
			if (sub_menu == 0) {
				if (!ifheld(player, ItemId.COINS.id(), 20)) {
					say(player, n, "Oops. I don't have 20 coins. Silly me.");
				} else {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
					npcsay(player, n,
						"Cheers",
						"Ok to get to the gang hideout",
						"After entering Varrock through the south gate",
						"If you take the first turning east",
						"Somewhere along there is an alleyway to the south",
						"The door at the end of there is the entrance to the phoenix gang",
						"They're operating there under the name of the VTAM corporation",
						"Be careful",
						"The phoenix gang ain't the types to be messed with");
					say(player, n, "Thanks");
					if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 2) {
						player.updateQuestStage(Quests.SHIELD_OF_ARRAV, 3);
					}
				}
			} else if (sub_menu == 1) {
				npcsay(player, n, "Heh, if you wanna deal with the phoenix gang",
					"They're involved in much worse than a bit of bribery");
			} else if (sub_menu == 2) {
				//nothing
			}
		} else if (menu == 1) {
			say(player, n, "Can you sell me some furs?");
			npcsay(player, n, "Yeah sure they're 20 gold coins a piece");
			int opts = multi(player, n, false, //do not send over
				"Yeah, okay here you go",
				"20 gold coins thats an outrage");
			if (opts == 0) {
				if (!ifheld(player, ItemId.COINS.id(), 20)) {
					say(player, n, "Oh dear I don't seem to have enough money");
					npcsay(player, n, "Well, okay I'll go down to 18 coins");
					bargained = true;
				} else {
					say(player, n, "Yeah okay here you go");
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
					player.message("You buy a fur from Baraek");
					player.getCarriedItems().getInventory().add(new Item(ItemId.FUR.id()));
				}
			} else if (opts == 1) {
				say(player, n, "20 gold coins that's an outrage");
				npcsay(player, n, "Well, okay I'll go down to 18");
				bargained = true;
			}
		} else if (menu == 2) {
			say(player, n, "Hello I am in search of a quest");
			npcsay(player, n,
				"Sorry kiddo, I'm a fur trader not a damsel in distress");
		} else if (menu == 3) {
			say(player, n, "Would you like to buy my fur?");
			npcsay(player, n, "Lets have a look at it");
			player.message("Baraek examines a fur");
			npcsay(player, n, "It's not in the best of condition",
				"I guess I could give 12 coins to take it off your hands");
			int opts = multi(player, n, "Yeah that'll do", "I think I'll keep hold of it actually");
			if (opts == 0) {
				mes(player, "You give Baraek a fur",
					"And he gives you twelve coins");
				player.getCarriedItems().remove(new Item(ItemId.FUR.id()));
				give(player, ItemId.COINS.id(), 12);
			} else if (opts == 1) {
				npcsay(player, n, "Oh ok", "Didn't want it anyway");
			}
		}

		if (bargained) {
			int sub_opts = multi(player, n, false, //do not send over
				"Okay here you go", "No thanks I'll leave it");
			if (sub_opts == 0) {
				if (!ifheld(player, ItemId.COINS.id(), 18)) {
					say(player, n, "Oh dear I don't seem to have enough money");
					npcsay(player, n, "Well I can't go any cheaper than that mate",
						"I have a family to feed");
				} else {
					say(player, n, "Okay here you go");
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 18));
					player.message("You buy a fur from Baraek");
					player.getCarriedItems().getInventory().add(new Item(ItemId.FUR.id()));
				}
			} else if (sub_opts == 1) {
				say(player, n, "No thanks, I'll leave it");
				npcsay(player, n, "It's your loss mate");
			}
		}
	}

	private boolean canGetInfoGang(Player player) {
		return player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 2
				|| (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 3 && !player.getCache().hasKey("arrav_mission"));
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BARAEK.id();
	}

}
