package com.openrsc.server.plugins.authentic.npcs.dwarvenmine;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class Thordur implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		say(player, n, "Hello");
		npcsay(player, n, "Hello adventurer",
				"What brings you to this place?");
		int opts = multi(player, n, "I just wanted to come by and say Hi",
				"Who are you?", "Do you like it here?");
		if (opts == 0) {
			thordurDialogue(player, n, WANTED_SAY_HI);
		}
		else if (opts == 1) {
			thordurDialogue(player, n, WHO_ARE_YOU);
		}
		else if (opts == 2) {
			thordurDialogue(player, n, LIKE_IT_HERE);
		}
	}

	private void thordurDialogue(Player player, Npc n, int cID) {
		switch (cID) {
		case WANTED_SAY_HI:
			npcsay(player, n, "Well hello there");
			break;
		case WHO_ARE_YOU:
			npcsay(player, n, "I am Thordur though names don't mean much");
			int opts = multi(player, n, "So what do you do",
					"Do you like it here?",
					"Nice to meet you");
			if (opts == 0) {
				thordurDialogue(player, n, WHAT_DO_YOU_DO);
			} else if (opts == 1) {
				thordurDialogue(player, n, LIKE_IT_HERE);
			} else if (opts == 2) {
				thordurDialogue(player, n, NICE_TO_MEET_YOU);
			}
			break;
		case LIKE_IT_HERE:
			npcsay(player, n, "Yes, its nice and quiet",
					"I get visitors once in a while",
					"this place is home to me");
			int opts1 = multi(player, n, "Visitors? what do you do?",
					"Nice to meet you");
			if (opts1 == 0) {
				thordurDialogue(player, n, WHAT_DO_YOU_DO);
			} else if (opts1 == 1) {
				thordurDialogue(player, n, NICE_TO_MEET_YOU);
			}
			break;
		case WHAT_DO_YOU_DO:
			npcsay(player, n, "I run a tourist attraction called the Black Hole");
			int opts2 = multi(player, n, "Oooh fancy, tell me more",
					"You mean like the prison where bad players are sent?",
					"Sounds good, how can I visit it?");
			if (opts2 == 0) {
				npcsay(player, n, "Well back in the day players tried",
						"to play unfairly and get advantage over other",
						"players, so moderators would trap them in the void space,",
						"often refered as the Black Hole");
				thordurDialogue(player, n, PRISON_BAD_PLAYERS);
			} else if (opts2 == 1) {
				thordurDialogue(player, n, PRISON_BAD_PLAYERS);
			} else if (opts2 == 2) {
				thordurDialogue(player, n, CAN_I_VISIT);
			}
			break;
		case NICE_TO_MEET_YOU:
			npcsay(player, n, "Nice to meet you too");
			break;
		case PRISON_BAD_PLAYERS:
			npcsay(player, n, "The prison used to be an old style of prisoning players",
					"who abused the rules",
					"Nowadays they use other methods",
					"My place allows players experience the void, allowing them",
					"to make it back here");
			int opts3 = multi(player, n, "I see, so can I visit the Black Hole",
					"I have to go");
			if (opts3 == 0) {
				thordurDialogue(player, n, CAN_I_VISIT);
			} else if (opts3 == 1) {
				thordurDialogue(player, n, HAVE_TO_GO);
			}
			break;
		case CAN_I_VISIT:
			npcsay(player, n, "You will need a special disk that allows you to get there",
					"and when you want to come back just spin it");
			int opts4 = multi(player, n, "So about this disk, can I buy it?",
					"I'll be right back");
			if (opts4 == 0) {
				thordurDialogue(player, n, CAN_I_BUY_IT);
			} else if (opts4 == 1) {
				thordurDialogue(player, n, BE_RIGHT_BACK);
			}
			break;
		case HAVE_TO_GO:
			npcsay(player, n, "Ok, have a nice day");
			break;
		case CAN_I_BUY_IT:
			npcsay(player, n, "I sell the disks for 10 coins",
					"Would you like to buy one?");
			int opts5 = multi(player, n, "Yes please",
					"No thankyou");
			if (opts5 == 0) {
				if (!ifheld(player, ItemId.COINS.id(), 10)) {
					say(player, n,
						"Oh dear I don't actually seem to have enough money");
				} else {
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 10));
					give(player, ItemId.DISK_OF_RETURNING.id(), 1);
					player.message("Thordur hands you a special disk");
					delay(2);
					say(player, n, "Thank you");
					npcsay(player, n, "If you ever happen to lose the disk whilst being in the Black Hole,",
							"the magical pool on the other side will allow you",
							"to safely return here");
				}
			} else if (opts5 == 1) {
				// NOTHING
			}
			break;
		case BE_RIGHT_BACK:
			npcsay(player, n, "Ok");
			break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.THORDUR.id();
	}

	static final int WANTED_SAY_HI = 0;
	static final int WHO_ARE_YOU = 1;
	static final int LIKE_IT_HERE = 2;
	static final int WHAT_DO_YOU_DO = 3;
	static final int NICE_TO_MEET_YOU = 4;
	static final int PRISON_BAD_PLAYERS = 5;
	static final int CAN_I_VISIT = 6;
	static final int HAVE_TO_GO = 7;
	static final int CAN_I_BUY_IT = 8;
	static final int BE_RIGHT_BACK = 9;

}
