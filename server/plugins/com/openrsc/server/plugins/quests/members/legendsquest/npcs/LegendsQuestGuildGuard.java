package com.openrsc.server.plugins.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestGuildGuard implements TalkToNpcListener, TalkToNpcExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener {

	private static final int MITHRIL_GATES = 1079;

	private void legendsGuardDialogue(Player p, Npc n, int cID) {
		if (n.getID() == NpcId.LEGENDS_GUILD_GUARD.id()) {
			if (cID == -1) {
				switch (p.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 0: /* Not started Legends Quest */
						npcTalk(p, n, "Yes " + (p.isMale() ? "Sir" : "Ma'am") + ", how can I help you?");
						int menu = showMenu(p, n,
							"What is this place?",
							"How do I get in here?",
							"Can I speak to someone in charge?",
							"It's Ok thanks.");
						if (menu == 0) {
							legendsGuardDialogue(p, n, LegendsGuard.WHAT_IS_THIS_PLACE);
						} else if (menu == 1) {
							legendsGuardDialogue(p, n, LegendsGuard.HOW_DO_I_GET_IN_HERE);
						} else if (menu == 2) {
							legendsGuardDialogue(p, n, LegendsGuard.CAN_I_SPEAK_TO_SOMEONE_IN_CHARGE);
						} else if (menu == 3) {
							legendsGuardDialogue(p, n, LegendsGuard.ITS_OK_THANKS);
						}
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
						p.message("A guard nods at you as you walk past.");
						npcTalk(p, n, "Hope the quest is going well " + (p.isMale() ? "Sir" : "Ma'am") + " !");
						break;
					case 11:
					case -1:
						p.message("The guards Salute you as you walk past.");
						npcTalk(p, n, "! ! ! Attention ! ! !",
							"Legends Guild Member Approaching");
						openGates(p);
						break;
				}
			}
			switch (cID) {
				case LegendsGuard.WHAT_IS_THIS_PLACE:
					npcTalk(p, n, "This is the Legends Guild " + (p.isMale() ? "sir" : "Maaam") + " !",
						"Legendary RuneScape citizens are invited on a quest",
						"in order to become members of the guild.");
					int opt = showMenu(p, n,
						"Can I go on the quest?",
						"What kind of quest is it?");
					if (opt == 0) {
						legendsGuardDialogue(p, n, LegendsGuard.CAN_I_GO_ON_THE_QUEST);
					} else if (opt == 1) {
						legendsGuardDialogue(p, n, LegendsGuard.WHAT_KIND_OF_QUEST_IS_IT);
					}
					break;
				case LegendsGuard.HOW_DO_I_GET_IN_HERE:
					npcTalk(p, n, "Well " + (p.isMale() ? "sir" : "Ma'am") + ", ",
						"you'll need to be a legendary citizen of RuneScape.",
						"If you want to use the Legends Hall, ",
						"you'll be invited to complete a quest.",
						"Once you have completed that Quest,",
						"you'll be a fully fledged member of the Guild.");
					int opt2 = showMenu(p, n,
						"What is this place?",
						"Can I speak to someone in charge?",
						"Can I go on the quest?");
					if (opt2 == 0) {
						legendsGuardDialogue(p, n, LegendsGuard.WHAT_IS_THIS_PLACE);
					} else if (opt2 == 1) {
						legendsGuardDialogue(p, n, LegendsGuard.CAN_I_SPEAK_TO_SOMEONE_IN_CHARGE);
					} else if (opt2 == 2) {
						legendsGuardDialogue(p, n, LegendsGuard.CAN_I_GO_ON_THE_QUEST);
					}
					break;
				case LegendsGuard.CAN_I_SPEAK_TO_SOMEONE_IN_CHARGE:
					npcTalk(p, n, "Well, " + (p.isMale() ? "Sir" : "Ma'am") + ",",
						"Radimus Erkle is the Grand Vizier of the Legends Guild.",
						"He's a very busy man.",
						"And he'll only talk to those people eligible for the quest.");
					int opt3 = showMenu(p, n,
						"Can I go on the quest?",
						"What kind of quest is it?");
					if (opt3 == 0) {
						legendsGuardDialogue(p, n, LegendsGuard.CAN_I_GO_ON_THE_QUEST);
					} else if (opt3 == 1) {
						legendsGuardDialogue(p, n, LegendsGuard.WHAT_KIND_OF_QUEST_IS_IT);
					}
					break;
				case LegendsGuard.ITS_OK_THANKS:
					npcTalk(p, n, "Very well " + (p.isMale() ? "Sir" : "Ma'am") + " !");
					break;
				case LegendsGuard.CAN_I_GO_ON_THE_QUEST:
					message(p, "The guard gets out a scroll of paper and starts looking through it.");
					if (p.getQuestPoints() >= 107
						&& p.getQuestStage(Quests.HEROS_QUEST) == -1
						&& p.getQuestStage(Quests.FAMILY_CREST) == -1
						&& p.getQuestStage(Quests.SHILO_VILLAGE) == -1
						&& p.getQuestStage(Quests.UNDERGROUND_PASS) == -1
						&& p.getQuestStage(Quests.WATERFALL_QUEST) == -1) {
						npcTalk(p, n, "Well, it looks as if you are eligable for the quest.",
							"Grand Vizier Erkle will give you the details about the quest.",
							"You can go and talk to him about it if you like?");
						int opt4 = showMenu(p, n,
							"Who is Grand Vizier Erkle?",
							"Yes, I'd like to talk to Grand Vizier Erkle.",
							"Some other time perhaps.");
						if (opt4 == 0) {
							legendsGuardDialogue(p, n, LegendsGuard.WHO_IS_GRAND_VIZIER_ERKLE);
						} else if (opt4 == 1) {
							legendsGuardDialogue(p, n, LegendsGuard.LIKE_TO_TALK_TO_GVE);
						}
					} else {
						npcTalk(p, n, "I'm very sorry,",
							"But you need to complete more quests before you qualify.",
							"You also need to have 107 quest points.");
						int denyMenu = showMenu(p, n,
							"Which quests do I need to complete?",
							"Ok thanks.");
						if (denyMenu == 0) {
							npcTalk(p, n, "You need to complete the...");
							if (p.getQuestStage(Quests.HEROS_QUEST) != -1) {
								npcTalk(p, n, "Hero's Quest.");
							}
							if (p.getQuestStage(Quests.FAMILY_CREST) != -1) {
								npcTalk(p, n, "Family Crest Quest.");
							}
							if (p.getQuestStage(Quests.SHILO_VILLAGE) != -1) {
								npcTalk(p, n, "Shilo Village Quest.");
							}
							if (p.getQuestStage(Quests.UNDERGROUND_PASS) != -1) {
								npcTalk(p, n, "Underground Pass Quest.");
							}
							if (p.getQuestStage(Quests.WATERFALL_QUEST) != -1) {
								npcTalk(p, n, "Waterfall Quest.");
							}
							if (p.getQuestPoints() < 107) {
								npcTalk(p, n, "You also need to have 107 Quest Points as well!");
							}
							npcTalk(p, n, "They don't call it the Legends Guild for nothing you know!",
								"Best of luck if you intend to become a member!");
						} else if (denyMenu == 1) {
							npcTalk(p, n, "That's no problem...",
								"Best of luck if you intend to become a member!");
						}
					}
					break;
				case LegendsGuard.WHAT_KIND_OF_QUEST_IS_IT:
					npcTalk(p, n, "Well, to be honest " + (p.isMale() ? "Sir" : "Ma'am") + ", I'm not really sure.",
						"You'll need to talk to Grand Vizier Erkle to find that out.");
					int opt4 = showMenu(p, n, false, //do not send over
						"Can I go on the quest?",
						"Thanks for your help.");
					if (opt4 == 0) {
						playerTalk(p, n, "Can I go on the quest?");
						legendsGuardDialogue(p, n, LegendsGuard.CAN_I_GO_ON_THE_QUEST);
					} else if (opt4 == 1) {
						playerTalk(p, n, "Thanks for your help");
						npcTalk(p, n, "You're welcome..");
						p.message("The Guard marches off on patrol again.");
					}
					break;
				case LegendsGuard.WHO_IS_GRAND_VIZIER_ERKLE:
					npcTalk(p, n, "He is the head of the Legends Guild.",
						"His full name is Radimus Erkle.",
						"Would you like to talk to him about the quest?");
					int opt5 = showMenu(p, n, false, //do not send over
						"Yes, I'd like to talk to Grand Vizier Erkle.",
						"Some other time perhaps.");
					if (opt5 == 0) {
						playerTalk(p, n, "Yes, I'd like to talk to Grand Vizier Erkle.");
						legendsGuardDialogue(p, n, LegendsGuard.LIKE_TO_TALK_TO_GVE);
					} else if (opt5 == 1) {
						playerTalk(p, n, "Some other time perhaps");
					}
					break;
				case LegendsGuard.LIKE_TO_TALK_TO_GVE:
					npcTalk(p, n, "Ok, very well...",
						"You need  to go into the building on the left, he's in his study.");
					p.message("The guard unlocks the gate and opens it for you.");
					npcTalk(p, n, "Good Luck!");
					openGates(p);
					break;
			}
		}
	}

	private void openGates(Player p) {
		GameObject the_gate = p.getWorld().getRegionManager().getRegion(Point.location(512, 550)).getGameObject(Point.location(512, 550));
		replaceObjectDelayed(the_gate, 2500, 181);
		p.teleport(513, 549);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.LEGENDS_GUILD_GUARD.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.LEGENDS_GUILD_GUARD.id()) {
			if (p.getQuestStage(Quests.LEGENDS_QUEST) == 0) {
				message(p, 1200, "You approach a nearby guard...");
			}
			legendsGuardDialogue(p, n, -1);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == MITHRIL_GATES;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == MITHRIL_GATES) {
			if (command.equals("open")) {
				if (p.getY() <= 550) {
					replaceObjectDelayed(obj, 2500, 181);
					p.teleport(513, 552);
					return;
				}
				Npc legends_guard = getNearestNpc(p, NpcId.LEGENDS_GUILD_GUARD.id(), 5);
				switch (p.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 0:
						if (legends_guard != null) {
							message(p, 1200, "A nearby guard approaches you...");
							legends_guard.initializeTalkScript(p);
						} else {
							p.message("The guards is currently busy.");
						}
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
						if (legends_guard != null) {
							p.message("A guard nods at you as you walk past.");
							npcTalk(p, legends_guard, "Hope the quest is going well " + (p.isMale() ? "Sir" : "Ma'am") + " !");
						}
						openGates(p);
						break;
					case 11:
					case -1:
						if (legends_guard != null) {
							p.message("The guards Salute you as you walk past.");
							npcTalk(p, legends_guard, "! ! ! Attention ! ! !",
								"Legends Guild Member Approaching");
						}
						openGates(p);
						break;
				}

			} else if (command.equals("search")) {
				message(p, 1200, "The gates to the Legends Guild are made from wrought Mithril.");
				message(p, 1200, "A small path leads away up to a very grandiose building.");
				message(p, 1200, "To the left is a smaller out building, but it is no less impressive.");
				message(p, 1200, "All the buildings are set in wonderfully landscaped gardens.");
				p.message("Two well dressed guards seem to be guarding the gate.");
			}
		}
	}

	class LegendsGuard {
		static final int WHAT_IS_THIS_PLACE = 0;
		static final int HOW_DO_I_GET_IN_HERE = 1;
		static final int CAN_I_SPEAK_TO_SOMEONE_IN_CHARGE = 2;
		static final int ITS_OK_THANKS = 3;
		static final int CAN_I_GO_ON_THE_QUEST = 4;
		static final int WHAT_KIND_OF_QUEST_IS_IT = 5;
		static final int WHO_IS_GRAND_VIZIER_ERKLE = 6;
		static final int LIKE_TO_TALK_TO_GVE = 7;
	}
}
