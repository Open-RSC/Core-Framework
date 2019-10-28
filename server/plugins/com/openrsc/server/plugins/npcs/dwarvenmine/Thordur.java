package com.openrsc.server.plugins.npcs.dwarvenmine;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class Thordur implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		playerTalk(p, n, "Hello");
		npcTalk(p, n, "Hello adventurer",
				"What brings you to this place?");
		int opts = showMenu(p, n, "I just wanted to come by and say Hi",
				"Who are you?", "Do you like it here?");
		if (opts == 0) {
			thordurDialogue(p, n, WANTED_SAY_HI);
		}
		else if (opts == 1) {
			thordurDialogue(p, n, WHO_ARE_YOU);
		}
		else if (opts == 2) {
			thordurDialogue(p, n, LIKE_IT_HERE);
		}
	}
	
	private void thordurDialogue(Player p, Npc n, int cID) {
		switch (cID) {
		case WANTED_SAY_HI:
			npcTalk(p, n, "Well hello there");
			break;
		case WHO_ARE_YOU:
			npcTalk(p, n, "I am Thordur though names don't mean much");
			int opts = showMenu(p, n, "So what do you do",
					"Do you like it here?",
					"Nice to meet you");
			if (opts == 0) {
				thordurDialogue(p, n, WHAT_DO_YOU_DO);
			} else if (opts == 1) {
				thordurDialogue(p, n, LIKE_IT_HERE);
			} else if (opts == 2) {
				thordurDialogue(p, n, NICE_TO_MEET_YOU);
			}
			break;
		case LIKE_IT_HERE:
			npcTalk(p, n, "Yes, its nice and quiet",
					"I get visitors once in a while",
					"this place is home to me");
			int opts1 = showMenu(p, n, "Visitors? what do you do?",
					"Nice to meet you");
			if (opts1 == 0) {
				thordurDialogue(p, n, WHAT_DO_YOU_DO);
			} else if (opts1 == 1) {
				thordurDialogue(p, n, NICE_TO_MEET_YOU);
			}
			break;
		case WHAT_DO_YOU_DO:
			npcTalk(p, n, "I run a tourist attraction called the Black Hole");
			int opts2 = showMenu(p, n, "Oooh fancy, tell me more",
					"You mean like the prison where bad players are sent?",
					"Sounds good, how can I visit it?");
			if (opts2 == 0) {
				npcTalk(p, n, "Well back in the day players tried",
						"to play unfairly and get advantage over other",
						"players, so moderators would trap them in the void space,",
						"often refered as the Black Hole");
				thordurDialogue(p, n, PRISON_BAD_PLAYERS);
			} else if (opts2 == 1) {
				thordurDialogue(p, n, PRISON_BAD_PLAYERS);
			} else if (opts2 == 2) {
				thordurDialogue(p, n, CAN_I_VISIT);
			}
			break;
		case NICE_TO_MEET_YOU:
			npcTalk(p, n, "Nice to meet you too");
			break;
		case PRISON_BAD_PLAYERS:
			npcTalk(p, n, "The prison used to be an old style of prisoning players",
					"who abused the rules",
					"Nowadays they use other methods",
					"My place allows players experience the void, allowing them",
					"to make it back here");
			int opts3 = showMenu(p, n, "I see, so can I visit the Black Hole",
					"I have to go");
			if (opts3 == 0) {
				thordurDialogue(p, n, CAN_I_VISIT);
			} else if (opts3 == 1) {
				thordurDialogue(p, n, HAVE_TO_GO);
			}
			break;
		case CAN_I_VISIT:
			npcTalk(p, n, "You will need a special disk that allows you to get there",
					"and when you want to come back just spin it");
			int opts4 = showMenu(p, n, "So about this disk, can I buy it?",
					"I'll be right back");
			if (opts4 == 0) {
				thordurDialogue(p, n, CAN_I_BUY_IT);
			} else if (opts4 == 1) {
				thordurDialogue(p, n, BE_RIGHT_BACK);
			}
			break;
		case HAVE_TO_GO:
			npcTalk(p, n, "Ok, have a nice day");
			break;
		case CAN_I_BUY_IT:
			npcTalk(p, n, "I sell the disks for 10 coins",
					"Would you like to buy one?");
			int opts5 = showMenu(p, n, "Yes please",
					"No thankyou");
			if (opts5 == 0) {
				if (!hasItem(p, ItemId.COINS.id(), 10)) {
					playerTalk(p, n,
						"Oh dear I don't actually seem to have enough money");
				} else {
					p.getInventory().remove(ItemId.COINS.id(), 10);
					addItem(p, ItemId.DISK_OF_RETURNING.id(), 1);
					p.message("Thordur hands you a special disk");
					sleep(1200);
					playerTalk(p, n, "Thank you");
					npcTalk(p, n, "If you ever happen to lose the disk whilst being in the Black Hole,",
							"the magical pool on the other side will allow you",
							"to safely return here");
				}
			} else if (opts5 == 1) {
				// NOTHING
			}
			break;
		case BE_RIGHT_BACK:
			npcTalk(p, n, "Ok");
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
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
