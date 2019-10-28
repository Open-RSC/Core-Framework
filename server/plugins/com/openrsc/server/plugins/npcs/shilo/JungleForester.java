package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class JungleForester implements TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	/*
	 * JungleForesterNPC class is for not started Legends quest.
	 */

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.JUNGLE_FORESTER.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.JUNGLE_FORESTER.id()) {
			switch (p.getQuestStage(Quests.LEGENDS_QUEST)) {
				case 0:
					defaultJungleForesterDialogue(p, n, -1);
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case -1:
					LegendsQuest_jungleForesterDialogue(p, n, -1);
					break;
			}
		}
	}

	private void LegendsQuest_jungleForesterDialogue(Player p, Npc n, int cID) {
		if (n.getID() == NpcId.JUNGLE_FORESTER.id()) {
			if (cID == -1) {
				switch (p.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
					case -1:
						npcTalk(p, n, "Hello friend, you're a long way from civilisation!");
						int opt = showMenu(p, n,
							"How do I get into the Kharazi jungle?",
							"What do you do here?",
							"Have you seen any natives in the jungle?");
						if (opt == 0) {
							LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.KHARAZI_JUNGLE);
						} else if (opt == 1) {
							LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.WHAT_DO_YOU_DO_HERE);
						} else if (opt == 2) {
							LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE);
						}
						break;
				}
			}
			switch (cID) {
				case JungleForesterNPC_LegendsQuest.WHAT_DO_YOU_DO_HERE:
					npcTalk(p, n, "I'm a forester, and I specialise in exotic woods. ",
						"I've not managed to penetrate the Kharazi jungle very far,",
						"but I have found some interesting specimens of trees.",
						"If you do happen to get into the Kharazi jungle, do come and let me know.",
						"I'd love to be able to safely navigate my own way in and out.");
					int menu = showMenu(p, n,
						"How do I get into the Kharazi jungle?",
						"Have you seen any natives in the jungle?",
						"Ok thanks");
					if (menu == 0) {
						LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.KHARAZI_JUNGLE);
					} else if (menu == 1) {
						LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE);
					} else if (menu == 2) {
						LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
					}
					break;
				case JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE:
					npcTalk(p, n, "Well, I've heard some funny sounds...",
						"And I think I've seen a native...but I'm not sure",
						"They generally don't like to be seen I guess...",
						"But I found an item that you might be interested in.",
						"You swing it above your head and it makes a strange sound,",
						"it seems to attract their attention.");
					int opt = showMenu(p, n,
						"Can I have the item please?",
						"How do I get into the jungle?",
						"Ok thanks");
					if (opt == 0) {
						npcTalk(p, n, "Well, I wish I could give it to you.",
							"However, I have grown fond of it.",
							"And it may help me incase I get lost in the jungle.");
						int opt3 = showMenu(p, n,
							"Will you trade something for it?",
							"Ok thanks");
						if (opt3 == 0) {
							npcTalk(p, n, "Well, if you have something interesting, let me have a look at it",
								"and I'll offer you something in return...",
								"OK, I have to go now, but it's been nice talking with you.");
						} else if (opt3 == 1) {
							LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
						}
					} else if (opt == 1) {
						LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.KHARAZI_JUNGLE);
					} else if (opt == 2) {
						LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
					}
					break;
				case JungleForesterNPC_LegendsQuest.KHARAZI_JUNGLE:
					npcTalk(p, n, "Well, I've not managed it yet, ",
						"But I heard that someone managed to find a way in..",
						"But they only just managed to to escape the jungle with their lives.",
						"Apparently he was on a mission to map the area.",
						"How foolish is that?");
					int option = showMenu(p, n,
						"Well, in fact I plan to map that area myself.",
						"Are you calling me foolish?",
						"What do you do here?",
						"Have you seen any natives in the jungle?",
						"Ok thanks");
					if (option == 0) {
						message(p, 1200, "The forester looks very interested..");
						npcTalk(p, n, "Oh, well, that sounds quite good actually...",
							"Sorry if I sounded rude before, it just didn't seem like a good idea to me.",
							"I guess I just wouldn't want to do it myself.",
							"But a map of that area would certainly be a big task.",
							"And it would certainly be very useful...");
						message(p, 300, "The forester looks very thoughtfull");
						npcTalk(p, n, "Hey, if you manage to complete it, be sure to let me take a look!",
							"Well, best of luck with it, I'm sure you're going to need it.");
						int opt2 = showMenu(p, n,
							"Do you have any other tips about the Kharazi jungle?",
							"Have you seen any natives in the jungle?",
							"Ok thanks");
						if (opt2 == 0) {
							npcTalk(p, n, "Not really, but I would say be careful, it's a dangerous place.",
								"And good luck.");
						} else if (opt2 == 1) {
							LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE);
						} else if (opt2 == 2) {
							LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
						}
					} else if (option == 1) {
						npcTalk(p, n, "No, of course not...",
							"Sorry, I have to be on myway...");
					} else if (option == 2) {
						LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.WHAT_DO_YOU_DO_HERE);
					} else if (option == 3) {
						LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE);
					} else if (option == 4) {
						LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
					}
					break;
				case JungleForesterNPC_LegendsQuest.MAKE_A_COPY:
					npcTalk(p, n, "Many thanks friend.");
					message(p, 1200, "The Jungle Forester takes out some parchment and some charcoal.",
						"He studiously renders another copy of your map.");
					npcTalk(p, n, "Many thanks friend.");
					message(p, 1200, "He takes out a strange looking object and hands it to you.");
					npcTalk(p, n, "Here, I won't be needing this any longer, and it may help you.",
						"Whenever I've used it before, it attracted the attention of jungle natives.");
					addItem(p, ItemId.BULL_ROARER.id(), 1);
					break;
				case JungleForesterNPC_LegendsQuest.OK_THANKS:
					npcTalk(p, n, "You're welcome!",
							"See you around...");
					break;
			}
		}
	}

	private void defaultJungleForesterDialogue(Player p, Npc n, int cID) {
		if (n.getID() == NpcId.JUNGLE_FORESTER.id()) {
			if (cID == -1) {
				npcTalk(p, n, "Hello friend, you're a long way from civilisation!");
				int menu = showMenu(p, n,
					"What do you do here?",
					"How do I get into the jungle?",
					"Who are you?");
				if (menu == 0) {
					defaultJungleForesterDialogue(p, n, JungleForesterNPC.WHAT_DO_YOU_DO_HERE);
				} else if (menu == 1) {
					defaultJungleForesterDialogue(p, n, JungleForesterNPC.KHARAZI_JUNGLE);
				} else if (menu == 2) {
					defaultJungleForesterDialogue(p, n, JungleForesterNPC.WHO_ARE_YOU);
				}
			}
			switch (cID) {
				case JungleForesterNPC.WHAT_DO_YOU_DO_HERE:
					npcTalk(p, n, "I'm a forester, and I specialise in exotic woods. ",
						"I've not managed to penetrate the Kharazi jungle very far,",
						"but I have found some interesting specimens of trees.",
						"If you do happen to get into the Kharazi jungle, do come and let me know.",
						"I'd love to be able to safely navigate my own way in and out.");
					int menu = showMenu(p, n,
						"Who are you?",
						"How do I get into the Kharazi jungle?",
						"Ok thanks");
					if (menu == 0) {
						defaultJungleForesterDialogue(p, n, JungleForesterNPC.WHO_ARE_YOU);
					} else if (menu == 1) {
						defaultJungleForesterDialogue(p, n, JungleForesterNPC.KHARAZI_JUNGLE);
					} else if (menu == 2) {
						defaultJungleForesterDialogue(p, n, JungleForesterNPC.OK_THANKS);
					}
					break;
				case JungleForesterNPC.WHO_ARE_YOU:
					npcTalk(p, n, "I'm a jungle forester,",
						"Names mean little in this part of the world.");
					int sub_menu = showMenu(p, n,
						"What do you do here?",
						"How do I get into the Kharazi jungle?");
					if (sub_menu == 0) {
						defaultJungleForesterDialogue(p, n, JungleForesterNPC.WHAT_DO_YOU_DO_HERE);
					} else if (sub_menu == 1) {
						defaultJungleForesterDialogue(p, n, JungleForesterNPC.KHARAZI_JUNGLE);
					}
					break;
				case JungleForesterNPC.KHARAZI_JUNGLE:
					npcTalk(p, n, "Well, I've not managed it yet, ",
						"But I heard that someone managed to find a way in..",
						"But they only just managed to to escape the jungle with their lives.",
						"Apparently he was on a mission to map the area.",
						"How foolish is that?");
					int sub_menu2 = showMenu(p, n, false, //do not send over
						"So someone managed to get into the Kharazi Jungle?",
						"What do you do here?",
						"Ok thanks");
					if (sub_menu2 == 0) {
						playerTalk(p, n, "So someone managed to get into the Jungle?");
						npcTalk(p, n, "Yes, he said he was from some place...near the Barbarian outpost.",
							"Mentioned something about a legend ?",
							"It meant nothing to me though.");
						int sub_menu3 = showMenu(p, n, false, //do not send over
							"How do I get into the jungle?",
							"What do you do here?",
							"Ok thanks");
						if (sub_menu3 == 0) {
							playerTalk(p, n, "How do I get into the Kharazi jungle?");
							defaultJungleForesterDialogue(p, n, JungleForesterNPC.KHARAZI_JUNGLE);
						} else if (sub_menu3 == 1) {
							playerTalk(p, n, "What do you do here?");
							defaultJungleForesterDialogue(p, n, JungleForesterNPC.WHAT_DO_YOU_DO_HERE);
						} else if (sub_menu3 == 2) {
							playerTalk(p, n, "Ok thanks");
							defaultJungleForesterDialogue(p, n, JungleForesterNPC.OK_THANKS);
						}
					} else if (sub_menu2 == 1) {
						playerTalk(p, n, "What do you do here?");
						defaultJungleForesterDialogue(p, n, JungleForesterNPC.WHAT_DO_YOU_DO_HERE);
					} else if (sub_menu2 == 2) {
						playerTalk(p, n, "Ok thanks");
						defaultJungleForesterDialogue(p, n, JungleForesterNPC.OK_THANKS);
					}
					break;
				case JungleForesterNPC.OK_THANKS:
					npcTalk(p, n, "You're welcome!",
							"See you around...");
					break;
			}
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc n, Item item) {
		return n.getID() == NpcId.JUNGLE_FORESTER.id() && item.getID() == ItemId.RADIMUS_SCROLLS_COMPLETE.id(); // the complete map.
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc n, Item item) {
		if (n.getID() == NpcId.JUNGLE_FORESTER.id() && item.getID() == ItemId.RADIMUS_SCROLLS_COMPLETE.id()) { // the complete map.
			p.message("You show the completed map of Kharazi Jungle to the Forester.");
			if (hasItem(p, ItemId.BULL_ROARER.id())) { // if already have the bull roarer
				npcTalk(p, n, "It's a great map, thanks for letting me take a copy!",
					"It has helped me out a number of times now.");
				return;
			}
			npcTalk(p, n, "*Gasp*");
			p.message("The jungle forester looks speechless.");
			npcTalk(p, n, "This is very impressive!",
				"I'm amazed, it's just great!",
				"Do you mind if I make a copy of it, and I'll give you an item in return.");
			int menu = showMenu(p, n,
				"Yes, go ahead make a copy!",
				"What will you give me in return?",
				"Sorry, I must complete my quest.");
			if (menu == 0) {
				LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.MAKE_A_COPY);
			} else if (menu == 1) {
				npcTalk(p, n, "Well, I can offer you this?");
				message(p, 1200, "The Jungle Forester takes out a strange looking object.",
					"It looks like a wooden pole, with string attached to one end.");
				p.message("And at the other end of the string is shaped piece of wood.");
				npcTalk(p, n, "If you swing this above your head, it makes a strange sound.",
					"I noticed that it attracts the attention of the natives.",
					"Is it a deal? Can I make a copy of your map?");
				int opt = showMenu(p, n,
					"Yes, go ahead make a copy!",
					"Sorry, I must complete my quest.");
				if (opt == 0) {
					LegendsQuest_jungleForesterDialogue(p, n, JungleForesterNPC_LegendsQuest.MAKE_A_COPY);
				} else if (opt == 1) {
					npcTalk(p, n, "Very well friend, I understand, I must be on my way as well.");
					p.message("The Jungle Forester seems a bit annoyed...and wanders off.");
				}
			} else if (menu == 2) {
				npcTalk(p, n, "Very well friend, I understand, I must be on my way as well.");
				p.message("The Jungle Forester seems a bit annoyed...and wanders off.");
			}
		}
	}

	class JungleForesterNPC {
		public static final int WHAT_DO_YOU_DO_HERE = 0;
		public static final int WHO_ARE_YOU = 1;
		public static final int KHARAZI_JUNGLE = 2;
		public static final int OK_THANKS = 3;
	}

	class JungleForesterNPC_LegendsQuest {
		public static final int WHAT_DO_YOU_DO_HERE = 0;
		public static final int WHO_ARE_YOU = 1;
		public static final int KHARAZI_JUNGLE = 2;
		public static final int NATIVES_IN_THE_JUNGLE = 3;
		public static final int MAKE_A_COPY = 4;
		public static final int OK_THANKS = 5;
	}
}
