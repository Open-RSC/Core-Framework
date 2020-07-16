package com.openrsc.server.plugins.authentic.npcs.draynor;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class Ned implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.NED.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Why hello there, me friends call me Ned",
			"I was a man of the sea, but its past me now",
			"Could I be making or selling you some Rope?"
		);
		String[] menu = new String[]{ // Default Menu
			"Yes, I would like some Rope",
			"No thanks Ned, I don't need any"
		};
		if (player.getQuestStage(Quests.DRAGON_SLAYER) == 2 && !player.getCache().hasKey("ned_hired")) {
			if (player.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 2) {
				menu = new String[]{ // Dragon Slayer + Prince Ali Rescue
					"You're a sailor? Could you take me to the Isle of Crandor",
					"Yes, I would like some Rope",
					"Ned, could you make other things from wool?",
					"No thanks Ned, I don't need any"
				};
				int choice = multi(player, n, menu);
				makeChoice(player, n, choice);
			} else {
				menu = new String[]{ // Dragon Slayer
					"You're a sailor? Could you take me to the Isle of Crandor",
					"Yes, I would like some Rope",
					"No thanks Ned, I don't need any"
				};
				int choice = multi(player, n, menu);
				if (choice >= 2)
					makeChoice(player, n, 3);
				else
					makeChoice(player, n, choice);
			}
		} else if (player.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 2) {
			menu = new String[]{ // Prince Ali Rescue
				"Yes, I would like some Rope",
				"Ned, could you make other things from wool?",
				"No thanks Ned, I don't need any"
			};
			int choice = multi(player, n, menu);
			if (choice >= 0) {
				makeChoice(player, n, choice + 1);
			}
		} else {
			int choice = multi(player, n, menu);
			if (choice == 0)
				makeChoice(player, n, 1);
			else if (choice == 1)
				makeChoice(player, n, 3);
		}
	}

	public void makeChoice(Player player, Npc n, int option) {
		if (option == 0) { // Dragon Slayer
			npcsay(player, n, "Well I was a sailor",
				"I've not been able to get work at sea these days though",
				"They say I am too old"
			);
			mes("There is a wistfull look in Ned's eyes");
			delay(3);
			npcsay(player, n, "I miss those days",
				"If you could get me a ship I would take you anywhere"
			);
			if (player.getCache().hasKey("ship_fixed")) {
				say(player, n, "As it happens I do have a ship ready to sail");
				npcsay(player, n, "That'd be grand, where is it");
				say(player, n, "It's called the Lumbridge Lady and it's docked in Port Sarim");
				npcsay(player, n, "I'll go right over there and check her out then",
					"See you over there"
				);
				player.getCache().store("ned_hired", true);
			} else {
				say(player, n, "I will work on finding a sea worthy ship then");
			}
		} else if (option == 1) { // Buy Rope
			npcsay(player, n, "Well, I can sell you some rope for 15 coins",
				"Or I can be making you some if you gets me 4 balls of wool",
				"I strands them together I does, makes em strong"
			);
			int choice;
			if (!ifheld(player, ItemId.BALL_OF_WOOL.id(), 4)) {
				choice = multi(player, n, false, //do not send over
					"Okay, please sell me some Rope",
					"Thats a little more than I want to pay",
					"I will go and get some wool"
				);
			} else {
				choice = multi(player, n, false, //do not send over
					"Okay, please sell me some Rope",
					"Thats a little more than I want to pay",
					"I have some balls of wool. could you make me some Rope?"
				);
			}
			if (choice == 0) {
				if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < 15) {
					player.message("You Don't have enough coins to buy any rope!");
				} else {
					say(player, n, "Okay, please sell me some Rope");
					player.message("You hand Ned 15 coins");
					npcsay(player, n, "There you go, finest rope in Runescape");
					player.getCarriedItems().getInventory().add(new Item(ItemId.ROPE.id()));
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 15));
					player.message("Ned gives you a coil of rope");
				}
			} else if (choice == 1) {
				say(player, n, "Thats a little more than I want to pay");
				npcsay(player, n, "Well, if you ever need rope. thats the price. sorry",
					"An old sailor needs money for a little drop o rum."
				);
			} else if (choice == 2) {
				if (!ifheld(player, ItemId.BALL_OF_WOOL.id(), 4)) {
					say(player, n, "I will go and get some wool");
					npcsay(player, n, "Aye, you do that",
						"Remember, it takes 4 balls of wool to make strong rope");
				} else {
					say(player, n, "I have some balls of wool. could you make me some Rope?");
					npcsay(player, n, "Sure I can.");
					for (int i = 0; i < 4; i++) {
						player.getCarriedItems().remove(new Item(ItemId.BALL_OF_WOOL.id()));
					}
					player.getCarriedItems().getInventory().add(new Item(ItemId.ROPE.id(), 1));
				}
			}
		} else if (option == 2) { // Prince Ali's Rescue
			npcsay(player, n, "I am sure I can. What are you thinking of?");
			int wool_menu = multi(player, n, "Could you knit me a sweater?",
				"How about some sort of a wig?",
				"Could you repair the arrow holes in the back of my shirt?");
			if (wool_menu == 0) {
				npcsay(player, n, "Do I look like a member of a sewing circle?",
					"Be off wi' you, I have fought monsters that would turn your hair blue",
					"I don't need to be laughed at just 'cos I am getting a bit old");
			} else if (wool_menu == 1) {
				npcsay(player, n, "Well... Thats an interesting thought",
					"yes, I think I could do something",
					"Give me 3 balls of wool and I might be able to do it"
				);
				if (player.getCarriedItems().getInventory().countId(ItemId.BALL_OF_WOOL.id()) >= 3) {
					int choice = multi(player, n,
						"I have that now. Please, make me a wig",
						"I will come back when I need you to make me one"
					);
					if (choice == 0) {
						npcsay(player, n, "Okay, I will have a go.");
						mes("You hand Ned 3 balls of wool");
						delay(3);
						mes("Ned works with the wool. His hands move with a speed you couldn't imagine");
						delay(3);
						for (int i = 0; i < 3; i++) {
							player.getCarriedItems().remove(new Item(ItemId.BALL_OF_WOOL.id()));
						}
						npcsay(player, n, "Here you go, hows that for a quick effort? Not bad I think!");
						player.message("Ned gives you a pretty good wig");
						give(player, ItemId.WOOL_WIG.id(), 1);
						say(player, n, "Thanks Ned, theres more to you than meets the eye");
					} else if (choice == 1) {
						npcsay(player, n, "Well, it sounds like a challenge",
							"come to me if you need one"
						);
					}
				} else {
					say(player, n, "great, I will get some. I think a wig would be useful");
				}
			} else if (wool_menu == 2) {
				npcsay(player, n, "Ah yes, its a tough world these days",
					"Theres a few brave enough to attack from 10 metres away");
				player.message("Ned pulls out a needle and attacks your shirt");
				npcsay(player, n, "There you go, good as new");
				say(player, n, "Thanks Ned, maybe next time they will attack me face to face");
			}
		} else if (option == 3) { // No thanks
			npcsay(player, n, "Well, old Neddy is always here if you do",
				"Tell your friends, I can always be using the business"
			);
		}
	}
}
