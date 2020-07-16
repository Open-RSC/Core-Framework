package com.openrsc.server.plugins.authentic.npcs.shilo;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class JungleForester implements TalkNpcTrigger, UseNpcTrigger {

	/*
	 * JungleForesterNPC class is for not started Legends quest.
	 */

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.JUNGLE_FORESTER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.JUNGLE_FORESTER.id()) {
			switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
				case 0:
					defaultJungleForesterDialogue(player, n, -1);
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
					LegendsQuest_jungleForesterDialogue(player, n, -1);
					break;
			}
		}
	}

	private void LegendsQuest_jungleForesterDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.JUNGLE_FORESTER.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
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
						npcsay(player, n, "Hello friend, you're a long way from civilisation!");
						int opt = multi(player, n,
							"How do I get into the Kharazi jungle?",
							"What do you do here?",
							"Have you seen any natives in the jungle?");
						if (opt == 0) {
							LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.KHARAZI_JUNGLE);
						} else if (opt == 1) {
							LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.WHAT_DO_YOU_DO_HERE);
						} else if (opt == 2) {
							LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE);
						}
						break;
				}
			}
			switch (cID) {
				case JungleForesterNPC_LegendsQuest.WHAT_DO_YOU_DO_HERE:
					npcsay(player, n, "I'm a forester, and I specialise in exotic woods. ",
						"I've not managed to penetrate the Kharazi jungle very far,",
						"but I have found some interesting specimens of trees.",
						"If you do happen to get into the Kharazi jungle, do come and let me know.",
						"I'd love to be able to safely navigate my own way in and out.");
					int menu = multi(player, n,
						"How do I get into the Kharazi jungle?",
						"Have you seen any natives in the jungle?",
						"Ok thanks");
					if (menu == 0) {
						LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.KHARAZI_JUNGLE);
					} else if (menu == 1) {
						LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE);
					} else if (menu == 2) {
						LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
					}
					break;
				case JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE:
					npcsay(player, n, "Well, I've heard some funny sounds...",
						"And I think I've seen a native...but I'm not sure",
						"They generally don't like to be seen I guess...",
						"But I found an item that you might be interested in.",
						"You swing it above your head and it makes a strange sound,",
						"it seems to attract their attention.");
					int opt = multi(player, n,
						"Can I have the item please?",
						"How do I get into the jungle?",
						"Ok thanks");
					if (opt == 0) {
						npcsay(player, n, "Well, I wish I could give it to you.",
							"However, I have grown fond of it.",
							"And it may help me incase I get lost in the jungle.");
						int opt3 = multi(player, n,
							"Will you trade something for it?",
							"Ok thanks");
						if (opt3 == 0) {
							npcsay(player, n, "Well, if you have something interesting, let me have a look at it",
								"and I'll offer you something in return...",
								"OK, I have to go now, but it's been nice talking with you.");
						} else if (opt3 == 1) {
							LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
						}
					} else if (opt == 1) {
						LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.KHARAZI_JUNGLE);
					} else if (opt == 2) {
						LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
					}
					break;
				case JungleForesterNPC_LegendsQuest.KHARAZI_JUNGLE:
					npcsay(player, n, "Well, I've not managed it yet, ",
						"But I heard that someone managed to find a way in..",
						"But they only just managed to to escape the jungle with their lives.",
						"Apparently he was on a mission to map the area.",
						"How foolish is that?");
					int option = multi(player, n,
						"Well, in fact I plan to map that area myself.",
						"Are you calling me foolish?",
						"What do you do here?",
						"Have you seen any natives in the jungle?",
						"Ok thanks");
					if (option == 0) {
						mes("The forester looks very interested..");
						delay(2);
						npcsay(player, n, "Oh, well, that sounds quite good actually...",
							"Sorry if I sounded rude before, it just didn't seem like a good idea to me.",
							"I guess I just wouldn't want to do it myself.",
							"But a map of that area would certainly be a big task.",
							"And it would certainly be very useful...");
						mes("The forester looks very thoughtfull");
						delay();
						npcsay(player, n, "Hey, if you manage to complete it, be sure to let me take a look!",
							"Well, best of luck with it, I'm sure you're going to need it.");
						int opt2 = multi(player, n,
							"Do you have any other tips about the Kharazi jungle?",
							"Have you seen any natives in the jungle?",
							"Ok thanks");
						if (opt2 == 0) {
							npcsay(player, n, "Not really, but I would say be careful, it's a dangerous place.",
								"And good luck.");
						} else if (opt2 == 1) {
							LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE);
						} else if (opt2 == 2) {
							LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
						}
					} else if (option == 1) {
						npcsay(player, n, "No, of course not...",
							"Sorry, I have to be on myway...");
					} else if (option == 2) {
						LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.WHAT_DO_YOU_DO_HERE);
					} else if (option == 3) {
						LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.NATIVES_IN_THE_JUNGLE);
					} else if (option == 4) {
						LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.OK_THANKS);
					}
					break;
				case JungleForesterNPC_LegendsQuest.MAKE_A_COPY:
					npcsay(player, n, "Many thanks friend.");
					mes("The Jungle Forester takes out some parchment and some charcoal.");
					delay(2);
					mes("He studiously renders another copy of your map.");
					delay(2);
					npcsay(player, n, "Many thanks friend.");
					mes("He takes out a strange looking object and hands it to you.");
					delay(2);
					npcsay(player, n, "Here, I won't be needing this any longer, and it may help you.",
						"Whenever I've used it before, it attracted the attention of jungle natives.");
					give(player, ItemId.BULL_ROARER.id(), 1);
					break;
				case JungleForesterNPC_LegendsQuest.OK_THANKS:
					npcsay(player, n, "You're welcome!",
							"See you around...");
					break;
			}
		}
	}

	private void defaultJungleForesterDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.JUNGLE_FORESTER.id()) {
			if (cID == -1) {
				npcsay(player, n, "Hello friend, you're a long way from civilisation!");
				int menu = multi(player, n,
					"What do you do here?",
					"How do I get into the jungle?",
					"Who are you?");
				if (menu == 0) {
					defaultJungleForesterDialogue(player, n, JungleForesterNPC.WHAT_DO_YOU_DO_HERE);
				} else if (menu == 1) {
					defaultJungleForesterDialogue(player, n, JungleForesterNPC.KHARAZI_JUNGLE);
				} else if (menu == 2) {
					defaultJungleForesterDialogue(player, n, JungleForesterNPC.WHO_ARE_YOU);
				}
			}
			switch (cID) {
				case JungleForesterNPC.WHAT_DO_YOU_DO_HERE:
					npcsay(player, n, "I'm a forester, and I specialise in exotic woods. ",
						"I've not managed to penetrate the Kharazi jungle very far,",
						"but I have found some interesting specimens of trees.",
						"If you do happen to get into the Kharazi jungle, do come and let me know.",
						"I'd love to be able to safely navigate my own way in and out.");
					int menu = multi(player, n,
						"Who are you?",
						"How do I get into the Kharazi jungle?",
						"Ok thanks");
					if (menu == 0) {
						defaultJungleForesterDialogue(player, n, JungleForesterNPC.WHO_ARE_YOU);
					} else if (menu == 1) {
						defaultJungleForesterDialogue(player, n, JungleForesterNPC.KHARAZI_JUNGLE);
					} else if (menu == 2) {
						defaultJungleForesterDialogue(player, n, JungleForesterNPC.OK_THANKS);
					}
					break;
				case JungleForesterNPC.WHO_ARE_YOU:
					npcsay(player, n, "I'm a jungle forester,",
						"Names mean little in this part of the world.");
					int sub_menu = multi(player, n,
						"What do you do here?",
						"How do I get into the Kharazi jungle?");
					if (sub_menu == 0) {
						defaultJungleForesterDialogue(player, n, JungleForesterNPC.WHAT_DO_YOU_DO_HERE);
					} else if (sub_menu == 1) {
						defaultJungleForesterDialogue(player, n, JungleForesterNPC.KHARAZI_JUNGLE);
					}
					break;
				case JungleForesterNPC.KHARAZI_JUNGLE:
					npcsay(player, n, "Well, I've not managed it yet, ",
						"But I heard that someone managed to find a way in..",
						"But they only just managed to to escape the jungle with their lives.",
						"Apparently he was on a mission to map the area.",
						"How foolish is that?");
					int sub_menu2 = multi(player, n, false, //do not send over
						"So someone managed to get into the Kharazi Jungle?",
						"What do you do here?",
						"Ok thanks");
					if (sub_menu2 == 0) {
						say(player, n, "So someone managed to get into the Jungle?");
						npcsay(player, n, "Yes, he said he was from some place...near the Barbarian outpost.",
							"Mentioned something about a legend ?",
							"It meant nothing to me though.");
						int sub_menu3 = multi(player, n, false, //do not send over
							"How do I get into the jungle?",
							"What do you do here?",
							"Ok thanks");
						if (sub_menu3 == 0) {
							say(player, n, "How do I get into the Kharazi jungle?");
							defaultJungleForesterDialogue(player, n, JungleForesterNPC.KHARAZI_JUNGLE);
						} else if (sub_menu3 == 1) {
							say(player, n, "What do you do here?");
							defaultJungleForesterDialogue(player, n, JungleForesterNPC.WHAT_DO_YOU_DO_HERE);
						} else if (sub_menu3 == 2) {
							say(player, n, "Ok thanks");
							defaultJungleForesterDialogue(player, n, JungleForesterNPC.OK_THANKS);
						}
					} else if (sub_menu2 == 1) {
						say(player, n, "What do you do here?");
						defaultJungleForesterDialogue(player, n, JungleForesterNPC.WHAT_DO_YOU_DO_HERE);
					} else if (sub_menu2 == 2) {
						say(player, n, "Ok thanks");
						defaultJungleForesterDialogue(player, n, JungleForesterNPC.OK_THANKS);
					}
					break;
				case JungleForesterNPC.OK_THANKS:
					npcsay(player, n, "You're welcome!",
							"See you around...");
					break;
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc n, Item item) {
		return n.getID() == NpcId.JUNGLE_FORESTER.id() && item.getCatalogId() == ItemId.RADIMUS_SCROLLS_COMPLETE.id(); // the complete map.
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item item) {
		if (n.getID() == NpcId.JUNGLE_FORESTER.id() && item.getCatalogId() == ItemId.RADIMUS_SCROLLS_COMPLETE.id()) { // the complete map.
			player.message("You show the completed map of Kharazi Jungle to the Forester.");
			if (player.getCarriedItems().hasCatalogID(ItemId.BULL_ROARER.id(), Optional.empty())) { // if already have the bull roarer
				npcsay(player, n, "It's a great map, thanks for letting me take a copy!",
					"It has helped me out a number of times now.");
				return;
			}
			npcsay(player, n, "*Gasp*");
			player.message("The jungle forester looks speechless.");
			npcsay(player, n, "This is very impressive!",
				"I'm amazed, it's just great!",
				"Do you mind if I make a copy of it, and I'll give you an item in return.");
			int menu = multi(player, n,
				"Yes, go ahead make a copy!",
				"What will you give me in return?",
				"Sorry, I must complete my quest.");
			if (menu == 0) {
				LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.MAKE_A_COPY);
			} else if (menu == 1) {
				npcsay(player, n, "Well, I can offer you this?");
				mes("The Jungle Forester takes out a strange looking object.");
				delay(2);
				mes("It looks like a wooden pole, with string attached to one end.");
				delay(2);
				player.message("And at the other end of the string is shaped piece of wood.");
				npcsay(player, n, "If you swing this above your head, it makes a strange sound.",
					"I noticed that it attracts the attention of the natives.",
					"Is it a deal? Can I make a copy of your map?");
				int opt = multi(player, n,
					"Yes, go ahead make a copy!",
					"Sorry, I must complete my quest.");
				if (opt == 0) {
					LegendsQuest_jungleForesterDialogue(player, n, JungleForesterNPC_LegendsQuest.MAKE_A_COPY);
				} else if (opt == 1) {
					npcsay(player, n, "Very well friend, I understand, I must be on my way as well.");
					player.message("The Jungle Forester seems a bit annoyed...and wanders off.");
				}
			} else if (menu == 2) {
				npcsay(player, n, "Very well friend, I understand, I must be on my way as well.");
				player.message("The Jungle Forester seems a bit annoyed...and wanders off.");
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
