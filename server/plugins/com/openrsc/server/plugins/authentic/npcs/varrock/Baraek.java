package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public final class Baraek implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		int menu;
		boolean hasFur = player.getCarriedItems().hasCatalogID(ItemId.FUR.id());
		boolean hasWolfFur = player.getCarriedItems().hasCatalogID(ItemId.GREY_WOLF_FUR.id());
		ArrayList<String> options = new ArrayList<>();
		int start, skip;
		skip = -1;
		start = 0;
		if (canGetInfoGang(player)) {
			options.add("Can you tell me where I can find the phoenix gang?");
		} else {
			start = 1; // menu start at 1
		}
		options.add("Can you sell me some furs?");
		options.add("Hello. I am in search of a quest");
		if (hasFur) {
			options.add("Would you like to buy my fur?");
		}
		if (hasWolfFur && player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			if (!hasFur) {
				skip = start + 2; // skip option of fur
			}
			options.add("Would you like to buy my grey wolf fur?");
			// also changed "Hello. I am in search of a quest" to "Hello I am in search of a quest"
			options.set(options.indexOf("Hello. I am in search of a quest"), "Hello I am in search of a quest");
		}
		String[] finalOptions = new String[options.size()];
		menu = multi(player, n, false, //do not send over
			options.toArray(finalOptions));

		if (menu >= 0) {
			menu += start;
			if (menu == skip) {
				// wolf fur selected but was shown in position of reg fur, selection+1
				menu++;
			}

			baraekDialogue(player, n, menu);
		}
	}

	private void baraekDialogue(Player player, Npc n, int chosenOption) {
		boolean bargained = false;
		if (chosenOption == 0) {
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
		} else if (chosenOption == 1) {
			say(player, n, "Can you sell me some furs?");
			npcsay(player, n, "Yeah sure they're 20 gold coins a piece");
			boolean canHaggle = true;
			if (player.getConfig().INFLUENCE_INSTEAD_QP && player.getSkills().getLevel(Skill.INFLUENCE.id()) < 5) {
				canHaggle = false;
			}
			ArrayList<String> furOptions = new ArrayList<>();
			furOptions.add("Yeah, okay here you go");
			furOptions.add("20 gold coins thats an outrage");
			String[] finalFurOptions = new String[furOptions.size()];
			int opts = multi(player, n, false, //do not send over
				furOptions.toArray(finalFurOptions));

			if (opts == 0) {
				if (!ifheld(player, ItemId.COINS.id(), 20)) {
					say(player, n, "Oh dear I don't seem to have enough money");
					if (canHaggle) {
						npcsay(player, n, "Well, okay I'll go down to 18 coins");
						bargained = true;
					}
				} else {
					say(player, n, "Yeah okay here you go");
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
					player.message("You buy a fur from Baraek");
					player.getCarriedItems().getInventory().add(new Item(ItemId.FUR.id()));
				}
			} else if (opts == 1) {
				say(player, n, "20 gold coins that's an outrage");
				if (!canHaggle) {
					npcsay(player, n, "Well I can't go any cheaper than that mate",
						"I have a family to feed");
					say(player, n, "Ah well never mind");
				} else {
					npcsay(player, n, "Well, okay I'll go down to 18");
					bargained = true;
				}
			}
		} else if (chosenOption == 2) {
			say(player, n, "Hello I am in search of a quest");
			npcsay(player, n,
				"Sorry kiddo, I'm a fur trader not a damsel in distress");
		} else if (chosenOption == 3) {
			say(player, n, "Would you like to buy my fur?");
			npcsay(player, n, "Lets have a look at it");
			player.message("Baraek examines a fur");
			npcsay(player, n, "It's not in the best of condition",
				"I guess I could give 12 coins to take it off your hands");
			int opts = multi(player, n, "Yeah that'll do", "I think I'll keep hold of it actually");
			if (opts == 0) {
				mes("You give Baraek a fur");
				delay(3);
				mes("And he gives you twelve coins");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.FUR.id()));
				give(player, ItemId.COINS.id(), 12);
			} else if (opts == 1) {
				npcsay(player, n, "Oh ok", "Didn't want it anyway");
			}
		} else if (chosenOption == 4) {
			say(player, n, "Would you like to buy my grey wolf fur?");
			npcsay(player, n, "Grey wolf fur, now you're talking",
				"Hmm I'll give you 120 per fur, does that sound fair?");
			int wolfmenu = multi(player, n, false, //do not send over
				"Yep sounds fine", "No I almost got my throat torn out by a wolf to get this");
			if (wolfmenu == 0) {
				say(player, n, "Yep that sounds fine");
				int count = player.getCarriedItems().getInventory().countId(ItemId.GREY_WOLF_FUR.id());
				for (int i=0; i<count; i++) {
					player.getCarriedItems().remove(new Item(ItemId.GREY_WOLF_FUR.id()));
					give(player, ItemId.COINS.id(), 120);
					delay();
				}
			} else if (wolfmenu == 1) {
				say(player, n, "No I almost got my throat torn out by a wolf to get this");
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
					say(player, n, "Ah well never mind");
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
