package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteWorkman implements TalkNpcTrigger, UseNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.WORKMAN.id() || n.getID() == NpcId.WORKMAN_UNDERGROUND.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.WORKMAN.id()) {
			switch (p.getQuestStage(Quests.DIGSITE)) {
				case -1:
					say(p, n, "Hello there");
					npcsay(p, n, "Ah it's the great archaeologist!",
						"Congratulations on your discovery");
					break;
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
					say(p, n, "Hello there");
					npcsay(p, n, "Good day, what can I do for you ?");
					int menu = multi(p, n,
						"What do you do here ?",
						"I'm not sure...",
						"Can I dig around here ?");
					if (menu == 0) {
						npcsay(p, n, "I am involved in various stages of the dig",
							"From the initial investigation to the installation of the mine shafts");
						say(p, n, "Oh okay, thanks");
					} else if (menu == 1) {
						npcsay(p, n, "Well, let me know when you are");
					} else if (menu == 2) {
						npcsay(p, n, "You can only use the site you have the appropriate exam level for");
						int sub_menu = multi(p, n,
							"Appropriate exam level ?",
							"I am already skilled in digging");
						if (sub_menu == 0) {
							npcsay(p, n, "Yes, only persons with the correct certificate of earth sciences can dig here",
								"A level 1 certificate will let you dig in a level 1 site and so on...");
							say(p, n, "Oh, okay I understand");
						} else if (sub_menu == 1) {
							npcsay(p, n, "Well that's nice for you...",
								"You can't dig around here without a certificate though");
						}
					}
					break;
			}
		}
		else if (n.getID() == NpcId.WORKMAN_UNDERGROUND.id()) {
			say(p, n, "Hello");
			npcsay(p, n, "Well well...",
				"I have a visitor",
				"What are you doing here ?");
			int menu = multi(p, n,
				"I have been invited to research here",
				"I am not sure really",
				"I'm here to get rich rich rich!");
			if (menu == 0) {
				npcsay(p, n, "Indeed you must be someone special to be allowed down here...");
				int opt1 = multi(p, n,
					"Do you know where to find a specimen jar ?",
					"Do you know where to find a chest key");
				if (opt1 == 0) {
					npcsay(p, n, "Hmmm, let me think...",
						"Nope, can't help you there i'm afraid");
				} else if (opt1 == 1) {
					npcsay(p, n, "Yes I might have one...");
					int opt2 = multi(p, n,
						"I don't suppose I could use it ?",
						"Can I buy it from you ?",
						"Hey that's my key!");
					if (opt2 == 0) {
						npcsay(p, n, "Aww, but I need it...");
						int opt3 = multi(p, n,
							"Please",
							"Can I buy it from you ?",
							"Hey that's my key!");
						if (opt3 == 0) {
							npcsay(p, n, "I am not sure about this...");
							int opt4 = multi(p, n,
								"Aww...go on",
								"Can I buy it from you ?",
								"Hey that's my key!");
							if (opt4 == 0) {
								npcsay(p, n, "Hmmm...well I don't know");
								int opt5 = multi(p, n,
									"Pretty please!",
									"Can I buy it from you ?",
									"Hey that's my key!");
								if (opt5 == 0) {
									npcsay(p, n, "You are trying to change my mind");
									say(p, n, "Of course!");
									int opt6 = multi(p, n,
										"Pretty please with sugar on top!",
										"Can I buy it from you ?",
										"Hey that's my key!");
									if (opt6 == 0) {
										give(p, ItemId.DIGSITE_CHEST_KEY.id(), 1);
										npcsay(p, n, "All right, all right!",
											"Stop begging I can't stand it.",
											"Here's the key...take care of it");
										say(p, n, "Thanks");
									} else if (opt6 == 1) {
										canIBuyIt(p, n);
									} else if (opt6 == 2) {
										myKey(p, n);
									}
								} else if (opt5 == 1) {
									canIBuyIt(p, n);
								} else if (opt5 == 2) {
									myKey(p, n);
								}
							} else if (opt4 == 1) {
								canIBuyIt(p, n);
							} else if (opt4 == 2) {
								myKey(p, n);
							}
						} else if (opt3 == 1) {
							canIBuyIt(p, n);
						} else if (opt3 == 2) {
							myKey(p, n);
						}
					} else if (opt2 == 1) {
						canIBuyIt(p, n);
					} else if (opt2 == 2) {
						myKey(p, n);
					}
				}
			} else if (menu == 1) {
				npcsay(p, n, "A miner without a clue - how funny");
			} else if (menu == 2) {
				npcsay(p, n, "Oh, well don't forget that wealth and riches isn't everything...");
			}
		}
	}

	private void canIBuyIt(Player p, Npc n) {
		npcsay(p, n, "Ooo no, I need it!");
	}

	private void myKey(Player p, Npc n) {
		npcsay(p, n, "You don't think im going to fall for that do you ?",
			"Get lost!");
	}

	@Override
	public boolean blockUseNpc(Player p, Npc n, Item item) {
		return n.getID() == NpcId.WORKMAN.id() && item.getCatalogId() == ItemId.DIGSITE_SCROLL.id();
	}

	@Override
	public void onUseNpc(Player p, Npc n, Item item) {
		if (n.getID() == NpcId.WORKMAN.id() && item.getCatalogId() == ItemId.DIGSITE_SCROLL.id()) {
			say(p, n, "Here, have a look at this...");
			npcsay(p, n, "I give permission...blah de blah etc....",
				"Okay that's all in order, you may use the mineshafts now",
				"I'll hang onto this scroll shall I ?");
			say(p, n, "Thanks");
			remove(p, ItemId.DIGSITE_SCROLL.id(), 1);
			if (!p.getCache().hasKey("digsite_winshaft") && p.getQuestStage(Quests.DIGSITE) == 5) {
				p.getCache().store("digsite_winshaft", true);
			}
		}
	}
}
