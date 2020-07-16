package com.openrsc.server.plugins.authentic.quests.members.digsite;

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
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.WORKMAN.id() || n.getID() == NpcId.WORKMAN_UNDERGROUND.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.WORKMAN.id()) {
			switch (player.getQuestStage(Quests.DIGSITE)) {
				case -1:
					say(player, n, "Hello there");
					npcsay(player, n, "Ah it's the great archaeologist!",
						"Congratulations on your discovery");
					break;
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
					say(player, n, "Hello there");
					npcsay(player, n, "Good day, what can I do for you ?");
					int menu = multi(player, n,
						"What do you do here ?",
						"I'm not sure...",
						"Can I dig around here ?");
					if (menu == 0) {
						npcsay(player, n, "I am involved in various stages of the dig",
							"From the initial investigation to the installation of the mine shafts");
						say(player, n, "Oh okay, thanks");
					} else if (menu == 1) {
						npcsay(player, n, "Well, let me know when you are");
					} else if (menu == 2) {
						npcsay(player, n, "You can only use the site you have the appropriate exam level for");
						int sub_menu = multi(player, n,
							"Appropriate exam level ?",
							"I am already skilled in digging");
						if (sub_menu == 0) {
							npcsay(player, n, "Yes, only persons with the correct certificate of earth sciences can dig here",
								"A level 1 certificate will let you dig in a level 1 site and so on...");
							say(player, n, "Oh, okay I understand");
						} else if (sub_menu == 1) {
							npcsay(player, n, "Well that's nice for you...",
								"You can't dig around here without a certificate though");
						}
					}
					break;
			}
		}
		else if (n.getID() == NpcId.WORKMAN_UNDERGROUND.id()) {
			say(player, n, "Hello");
			npcsay(player, n, "Well well...",
				"I have a visitor",
				"What are you doing here ?");
			int menu = multi(player, n,
				"I have been invited to research here",
				"I am not sure really",
				"I'm here to get rich rich rich!");
			if (menu == 0) {
				npcsay(player, n, "Indeed you must be someone special to be allowed down here...");
				int opt1 = multi(player, n,
					"Do you know where to find a specimen jar ?",
					"Do you know where to find a chest key");
				if (opt1 == 0) {
					npcsay(player, n, "Hmmm, let me think...",
						"Nope, can't help you there i'm afraid");
				} else if (opt1 == 1) {
					npcsay(player, n, "Yes I might have one...");
					int opt2 = multi(player, n,
						"I don't suppose I could use it ?",
						"Can I buy it from you ?",
						"Hey that's my key!");
					if (opt2 == 0) {
						npcsay(player, n, "Aww, but I need it...");
						int opt3 = multi(player, n,
							"Please",
							"Can I buy it from you ?",
							"Hey that's my key!");
						if (opt3 == 0) {
							npcsay(player, n, "I am not sure about this...");
							int opt4 = multi(player, n,
								"Aww...go on",
								"Can I buy it from you ?",
								"Hey that's my key!");
							if (opt4 == 0) {
								npcsay(player, n, "Hmmm...well I don't know");
								int opt5 = multi(player, n,
									"Pretty please!",
									"Can I buy it from you ?",
									"Hey that's my key!");
								if (opt5 == 0) {
									npcsay(player, n, "You are trying to change my mind");
									say(player, n, "Of course!");
									int opt6 = multi(player, n,
										"Pretty please with sugar on top!",
										"Can I buy it from you ?",
										"Hey that's my key!");
									if (opt6 == 0) {
										give(player, ItemId.DIGSITE_CHEST_KEY.id(), 1);
										npcsay(player, n, "All right, all right!",
											"Stop begging I can't stand it.",
											"Here's the key...take care of it");
										say(player, n, "Thanks");
									} else if (opt6 == 1) {
										canIBuyIt(player, n);
									} else if (opt6 == 2) {
										myKey(player, n);
									}
								} else if (opt5 == 1) {
									canIBuyIt(player, n);
								} else if (opt5 == 2) {
									myKey(player, n);
								}
							} else if (opt4 == 1) {
								canIBuyIt(player, n);
							} else if (opt4 == 2) {
								myKey(player, n);
							}
						} else if (opt3 == 1) {
							canIBuyIt(player, n);
						} else if (opt3 == 2) {
							myKey(player, n);
						}
					} else if (opt2 == 1) {
						canIBuyIt(player, n);
					} else if (opt2 == 2) {
						myKey(player, n);
					}
				}
			} else if (menu == 1) {
				npcsay(player, n, "A miner without a clue - how funny");
			} else if (menu == 2) {
				npcsay(player, n, "Oh, well don't forget that wealth and riches isn't everything...");
			}
		}
	}

	private void canIBuyIt(Player player, Npc n) {
		npcsay(player, n, "Ooo no, I need it!");
	}

	private void myKey(Player player, Npc n) {
		npcsay(player, n, "You don't think im going to fall for that do you ?",
			"Get lost!");
	}

	@Override
	public boolean blockUseNpc(Player player, Npc n, Item item) {
		return n.getID() == NpcId.WORKMAN.id() && item.getCatalogId() == ItemId.DIGSITE_SCROLL.id();
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item item) {
		if (n.getID() == NpcId.WORKMAN.id() && item.getCatalogId() == ItemId.DIGSITE_SCROLL.id()) {
			say(player, n, "Here, have a look at this...");
			npcsay(player, n, "I give permission...blah de blah etc....",
				"Okay that's all in order, you may use the mineshafts now",
				"I'll hang onto this scroll shall I ?");
			say(player, n, "Thanks");
			player.getCarriedItems().remove(new Item(ItemId.DIGSITE_SCROLL.id()));
			if (!player.getCache().hasKey("digsite_winshaft") && player.getQuestStage(Quests.DIGSITE) == 5) {
				player.getCache().store("digsite_winshaft", true);
			}
		}
	}
}
