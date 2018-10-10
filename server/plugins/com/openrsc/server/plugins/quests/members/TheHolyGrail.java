package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;

import static com.openrsc.server.plugins.Functions.*;

public class TheHolyGrail implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener, WallObjectActionListener,
		WallObjectActionExecutiveListener, InvActionListener,
		InvActionExecutiveListener, PlayerKilledNpcExecutiveListener,
		PlayerKilledNpcListener, PickupListener, PickupExecutiveListener,
		ObjectActionListener, ObjectActionExecutiveListener {
	/**
	 * @author Davve
	 * 
	 */
	@Override
	public int getQuestId() {
		return Quests.THE_HOLY_GRAIL;
	}

	@Override
	public String getQuestName() {
		return "The Holy Grail (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the holy grail quest");
		player.incQuestPoints(2);
		player.message("@gre@You haved gained 2 quest points!");
		player.incQuestExp(5, (player.getSkills().getMaxStat(5) + 1) * 1000);
		player.incQuestExp(1, (player.getSkills().getMaxStat(1) + 1) * 1200);
	}

	/**
	 * NPCS: #275 King Arthur - NPC HANDLED IN MERLINS CRYSTAL QUEST FILE. #287
	 * Merlin
	 * 
	 */
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 287) {
			return true;
		}
		if (n.getID() == 401) {
			return true;
		}
		if (n.getID() == 416) {
			return true;
		}
		if (n.getID() == 414) {
			return true;
		}
		if (n.getID() == 412) {
			return true;
		}
		if (n.getID() == 415) {
			return true;
		}
		if (n.getID() == 417) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 287) {
			switch (p.getQuestStage(Constants.Quests.THE_HOLY_GRAIL)) {
			case 1:
			case 2:
			case 3:
				playerTalk(
						p,
						n,
						"Hello King Arthur has sent me on a quest for the holy grail",
						"He thought you could offer some assistance");
				npcTalk(p, n, "Ah yes the holy grail",
						"That is a powerful artifact indeed",
						"Returning it here would help Camelot a lot",
						"Due to its nature the holy grail is likely to reside in a holy place");
				playerTalk(p, n, "Any suggestions?");
				npcTalk(p,
						n,
						"I believe there is a holy island somewhere not far away",
						"I'm not entirely sure",
						"I spent too long inside that crystal",
						"Anyway go and talk to someone over there",
						"I suppose you could also try speaking to Sir Galahad",
						"He returned from the quest many years after everyone else",
						"He seems to know something about it",
						"but he can only speak about those experiences cryptically");
				if (p.getQuestStage(this) == 1) {
					setQuestStage(p, this, 2);
				}
				int menu = showMenu(p, n, "Thankyou for the advice",
						"Where can I find Sir Galahad?");
				if (menu == 1) {
					npcTalk(p,
							n,
							"Galahad now lives a life of religious contemplation",
							"He lives somewhere west of McGrubors Wood");
				}
				break;
			case -1:
				npcTalk(p, n, "hello I'm working on a new spell",
						"To turn people into hedgehogs");
				break;
			}
		}
		if (n.getID() == 401) {
			npcTalk(p, n, "I am the black knight titan",
					"You must pass through me before you can continue in this realm");
			int menu = showMenu(p, n, "Ok, have at ye oh evil knight",
					"Actually I think I'll run away");
			if (menu == 0) {
				sleep(800);
				n.setChasing(p);
			}
		}
		if (n.getID() == 416) {
			npcTalk(p, n, "Woe is me", "Our crops are all failing",
					"How shall I feed myself this winter?");
		}
		if (n.getID() == 414) {
			npcTalk(p, n, "Hi - I don't get many visitors here");
			int menu = showMenu(p, n, "How's the fishing?",
					"Any idea how to get into the castle?",
					"Yes well this place is a dump");
			if (menu == 0) {
				npcTalk(p, n, "Not amazing",
						"Not many fish can live in this gungey stuff",
						"I remember when this was a pleasant river",
						"Teaming with every sort of fish");
			} else if (menu == 1) {
				npcTalk(p, n, "why thats easy",
						"just ring one of the bells outside");
				playerTalk(p, n, "I didn't see any bells");
				npcTalk(p, n, "You must be blind then",
						"There's always bells there when I go to the castle");
				createGroundItem(743, 1, 421, 30, p);
			} else if (menu == 2) {
				npcTalk(p, n, "This place used to be very beautiful",
						"However as our king grows old and weak",
						"the land seems to be dying too");
			}
		}
		if (n.getID() == 412) {
			npcTalk(p, n, "Ah you got inside at last",
					"You spent all that time fumbling around outside",
					"I thought you'd never make it here");
			if (p.getQuestStage(this) == 3) {
				p.updateQuestStage(this, 4);
			}
			int menu = showMenu(p, n,
					"How did you know what I have been doing?",
					"I seek the holy grail", "You don't look too well");
			if (menu == 0) {
				npcTalk(p, n, "Oh I can see what is happening in my realm",
						"I have sent clues to help you get here",
						"Such as the fisherman", "And the crone");
				int mm = showMenu(p, n, "I seek the holy grail",
						"You don't look too well",
						"Do you mind if I have a look around?");
				if (mm == 0) {
					npcTalk(p,
							n,
							"Ah excellent, a knight come to seek the holy grail",
							"Maybe now our land can be restored to it's former glory",
							"At the moment the grail cannot be removed from the castle",
							"legend has it a questing knight will one day",
							"Work out how to restore our land",
							"then he will claim the grail as his prize");
					playerTalk(p, n, "Any ideas how I can restore the land?");
					npcTalk(p, n, "None at all");
					int m = showMenu(p, n, "You don't look to well",
							"Do you mind if I have a look around?");
					if (m == 0) {
						npcTalk(p,
								n,
								"Nope I don't feel so good either",
								"I fear my life is running short",
								"Alas my son and heir is not here",
								"I am waiting for my son to return to this castle",
								"If you could find my son that would be a great weight off my shoulders");
						playerTalk(p, n, "Who is your son?");
						npcTalk(p, n, "He is known as Percival",
								"I believe he is a knight of the round table");
						playerTalk(p, n, "I shall go and see if I can find him");
					} else if (m == 1) {
						npcTalk(p, n, " No not at all, be my guest");
					}
				} else if (mm == 1) {
					npcTalk(p, n, "Nope I don't feel so good either",
							"I fear my life is running short",
							"Alas my son and heir is not here",
							"I am waiting for my son to return to this castle",
							"If you could find my son that would be a great weight off my shoulders");
					playerTalk(p, n, "Who is your son?");
					npcTalk(p, n, "He is known as Percival",
							"I believe he is a knight of the round table");
					playerTalk(p, n, "I shall go and see if I can find him");
				} else if (mm == 2) {
					npcTalk(p, n, " No not at all, be my guest");
				}
			} else if (menu == 1) {
				npcTalk(p,
						n,
						"Ah excellent, a knight come to seek the holy grail",
						"Maybe now our land can be restored to it's former glory",
						"At the moment the grail cannot be removed from the castle",
						"legend has it a questing knight will one day",
						"Work out how to restore our land",
						"then he will claim the grail as his prize");
				playerTalk(p, n, "Any ideas how I can restore the land?");
				npcTalk(p, n, "None at all");
				int m = showMenu(p, n, "You don't look to well",
						"Do you mind if I have a look around?");
				if (m == 0) {
					npcTalk(p, n, "Nope I don't feel so good either",
							"I fear my life is running short",
							"Alas my son and heir is not here",
							"I am waiting for my son to return to this castle",
							"If you could find my son that would be a great weight off my shoulders");
					playerTalk(p, n, "Who is your son?");
					npcTalk(p, n, "He is known as Percival",
							"I believe he is a knight of the round table");
					playerTalk(p, n, "I shall go and see if I can find him");
				} else if (m == 1) {
					npcTalk(p, n, " No not at all, be my guest");
				}
			} else if (menu == 2) {
				npcTalk(p, n, "Nope I don't feel so good either",
						"I fear my life is running short",
						"Alas my son and heir is not here",
						"I am waiting for my son to return to this castle",
						"If you could find my son that would be a great weight off my shoulders");
				playerTalk(p, n, "Who is your son?");
				npcTalk(p, n, "He is known as Percival",
						"I believe he is a knight of the round table");
				playerTalk(p, n, "I shall go and see if I can find him");
			}
		}
		if (n.getID() == 415) {
			npcTalk(p,
					n,
					"You missed all the excitement",
					"I got here and agreed to take over duties as king here",
					"Then before my eyes the most miraculous changes occured here",
					"Grass and trees were growing outside before our very eyes",
					"Thankyou very much for showing me the way home");
		}
		if (n.getID() == 417) {
			npcTalk(p, n, "Oh happy day",
					"suddenly our crops are growing again",
					"It'll be a bumper harvest this year");
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 117) {
			return true;
		}
		if (obj.getID() == 116) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 117) {
			if (p.getQuestStage(this) >= 1
					&& atQuestStage(p, Constants.Quests.MERLINS_CRYSTAL, -1)
					|| p.getQuestStage(this) == -1) {
				doDoor(obj, p);
			}
		}
		if (obj.getID() == 116) {
			p.message("You go through the door");
			doDoor(obj, p);
			if (p.getInventory().countId(738) != 2
					&& (p.getQuestStage(Quests.THE_HOLY_GRAIL) >= 3 || p
							.getQuestStage(Quests.THE_HOLY_GRAIL) == -1)) {
				createGroundItem(738, 1, 204, 2440, p);
				createGroundItem(738, 1, 204, 2440, p);
			}
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		if (item.getID() == 738) {
			return true;
		}
		if (item.getID() == 743) {
			return true;
		}
		if (item.getID() == 745) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if (item.getID() == 738) {
			if (p.getLocation().inBounds(490, 652, 491, 653)) { // SQUARE PLOT
				if (p.getQuestStage(this) == 5 || p.getQuestStage(this) == -1) {
					p.teleport(492, 18, false);
				} else {
					p.teleport(396, 18, false);
				}
			} else if (p.getLocation().inBounds(388, 4, 427, 40)) { // 1st
																	// ISLAND
				p.teleport(490, 651, false);
			} else if (p.getLocation().inBounds(484, 4, 523, 40)
					|| p.getLocation().inBounds(511, 976, 519, 984)
					|| p.getLocation().inBounds(511, 1920, 518, 1925)) { // 2nd
																			// ISLAND
																			// -
																			// 2nd
																			// floor
																			// -
																			// top
																			// floor
																			// castle.
				p.teleport(490, 651, false);
			} else {
				message(p, "The whistle makes no noise",
						"It will not work in this location");
			}
		}
		if (item.getID() == 743) {
			p.message("Ting a ling a ling");
			if (p.getLocation().inBounds(411, 27, 425, 40)) {
				p.message("Somehow you are now inside the castle");
				p.teleport(420, 35, false);
			}
		}
		if (item.getID() == 745) { // GOLDEN FEATHER SHOULD SAY SOUTH, NORTH,
									// WEST, EAST ALL THE WAY FROM CAMELOT TO
									// GOBLIN VILLAGE PROD SACK
			if (p.getQuestStage(this) == -1) {
				p.message("nothing intersting happens");
			} else {
				// TODO gotta do this one later.
				p.message("The feather points north");
			}
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == 401) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (n.getID() == 401) {
			if (p.getInventory().wielding(606)) {
				n.killedBy(p);
				n.resetCombatEvent();
				p.message("Well done you have defeated the black knight titan");
				p.teleport(414, 11, false);
			} else {
				n.resetCombatEvent();
				n.getSkills().setLevel(3, n.getDef().hits);
				n.teleport(n.getLoc().startX, n.getLoc().startY);
				p.message("Maybe you need something more to beat the titan");
			}
		}
	}

	/**
	 * playerTalk(p,n, "You feel that the grail shouldn't be moved");
	 * playerTalk(p,n,
	 * "You must complete some task here before you are worthy");
	 */
	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == 746 && i.getX() == 418 && i.getY() == 1924) {
			message(p, "You feel that the grail shouldn't be moved",
					"You must complete some task here before you are worthy");
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == 746 && i.getX() == 418 && i.getY() == 1924) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 408) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 408) {
			if (p.getQuestStage(this) == 4) {
				message(p, "You hear muffled noises from the sack");
				p.message("You open the sack");
				Npc percival = spawnNpc(411, 328, 446, 120000);
				npcTalk(p, percival, "Wow thankyou",
						"I could hardly breathe in there");
				int menu = showMenu(p, percival,
						"How did you end up in a sack?",
						"Come with me, I shall make you a king",
						"Your father wishes to speak to you");
				if (menu == 0) {
					npcTalk(p,
							percival,
							"It's a little embarrassing really",
							"After going on a long and challenging quest",
							"to retrieve the boots of arkaneeses",
							"defeating many powerful enemies on the way",
							"I fell into a goblin trap",
							"I've been kept as a slave here for the last 3 months",
							"a day or so ago, they decided it was a fun game",
							"To put me in this sack",
							"Then they forgot about me",
							"I'm now very hungry and my bones feel very stiff");
					int menu2 = showMenu(p, percival,
							"Come with me, I shall make you a king",
							"Your father wishes to speak to you");
					if (menu2 == 0) {
						npcTalk(p, percival, "What are you talking about?",
								"The king of where?");
						playerTalk(
								p,
								percival,
								"Your father is apparently someone called the fisher king",
								"He is dying and wishes you to be his heir");
						npcTalk(p, percival, "I have been told that before",
								"I have not been able to find that castle again though");
						playerTalk(p, percival,
								"Well I do have the means to get us there - a magic whistle");
						if (hasItem(p, 738)) {
							message(p, "You give a whistle to Sir Percival",
									"You tell sir Percival what to do with the whistle");
							removeItem(p, 738, 1);
							npcTalk(p, percival, "Ok I will see you there then");
							p.updateQuestStage(this, 5);
						} else {
							playerTalk(p, percival,
									"Oh dear seems like I have forgot the whistle with me");
						}
					} else if (menu2 == 1) {
						npcTalk(p, percival,
								"My father? you have spoken to him recently?");
						playerTalk(p, percival,
								"He is dying and wishes you to be his heir");
						npcTalk(p, percival, "I have been told that before",
								"I have not been able to find that castle again though");
						playerTalk(p, percival,
								"Well I do have the means to get us there - a magic whistle");
						if (hasItem(p, 738)) {
							message(p, "You give a whistle to Sir Percival",
									"You tell sir Percival what to do with the whistle");
							removeItem(p, 738, 1);
							npcTalk(p, percival, "Ok I will see you there then");
							p.updateQuestStage(this, 5);
						} else {
							playerTalk(p, percival,
									"Oh dear seems like I have forgot the whistle with me");
						}
					}
				} else if (menu == 1) {
					npcTalk(p, percival, "What are you talking about?",
							"The king of where?");
					playerTalk(
							p,
							percival,
							"Your father is apparently someone called the fisher king",
							"He is dying and wishes you to be his heir");
					npcTalk(p, percival, "I have been told that before",
							"I have not been able to find that castle again though");
					playerTalk(p, percival,
							"Well I do have the means to get us there - a magic whistle");
					if (hasItem(p, 738)) {
						message(p, "You give a whistle to Sir Percival",
								"You tell sir Percival what to do with the whistle");
						removeItem(p, 738, 1);
						npcTalk(p, percival, "Ok I will see you there then");
						p.updateQuestStage(this, 5);
					} else {
						playerTalk(p, percival,
								"Oh dear seems like I have forgot the whistle with me");
					}
				} else if (menu == 2) {
					npcTalk(p, percival,
							"My father? you have spoken to him recently?");
					playerTalk(p, percival,
							"He is dying and wishes you to be his heir");
					npcTalk(p, percival, "I have been told that before",
							"I have not been able to find that castle again though");
					playerTalk(p, percival,
							"Well I do have the means to get us there - a magic whistle");
					if (hasItem(p, 738)) {
						message(p, "You give a whistle to Sir Percival",
								"You tell sir Percival what to do with the whistle");
						removeItem(p, 738, 1);
						npcTalk(p, percival, "Ok I will see you there then");
						p.updateQuestStage(this, 5);
					} else {
						playerTalk(p, percival,
								"Oh dear seems like I have forgot the whistle with me");
					}
				}
			} else {
				p.message("nothing interesting happens");
			}
		}
	}
}
