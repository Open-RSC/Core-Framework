package com.openrsc.server.plugins.authentic.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestGuildGuard implements TalkNpcTrigger, OpLocTrigger {

	private static final int MITHRIL_GATES = 1079;

	private void legendsGuardDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.LEGENDS_GUILD_GUARD.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 0: /* Not started Legends Quest */
						npcsay(player, n, player.getText("LegendsQuestGuildGuardYesHowCanIHelpYou"));
						int menu = multi(player, n,
							"What is this place?",
							"How do I get in here?",
							"Can I speak to someone in charge?",
							"It's Ok thanks.");
						if (menu == 0) {
							legendsGuardDialogue(player, n, LegendsGuard.WHAT_IS_THIS_PLACE);
						} else if (menu == 1) {
							legendsGuardDialogue(player, n, LegendsGuard.HOW_DO_I_GET_IN_HERE);
						} else if (menu == 2) {
							legendsGuardDialogue(player, n, LegendsGuard.CAN_I_SPEAK_TO_SOMEONE_IN_CHARGE);
						} else if (menu == 3) {
							legendsGuardDialogue(player, n, LegendsGuard.ITS_OK_THANKS);
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
						player.message("A guard nods at you as you walk past.");
						npcsay(player, n, player.getText("LegendsQuestGuildGuardHopeTheQuestIsGoingWell"));
						break;
					case 11:
					case -1:
						player.message("The guards Salute you as you walk past.");
						npcsay(player, n, "! ! ! Attention ! ! !",
							"Legends Guild Member Approaching");
						openGates(player);
						break;
				}
			}
			switch (cID) {
				case LegendsGuard.WHAT_IS_THIS_PLACE:
					npcsay(player, n, player.getText("LegendsQuestGuildGuardThisIsTheLegendsGuild"),
						"Legendary RuneScape citizens are invited on a quest",
						"in order to become members of the guild.");
					int opt = multi(player, n,
						"Can I go on the quest?",
						"What kind of quest is it?");
					if (opt == 0) {
						legendsGuardDialogue(player, n, LegendsGuard.CAN_I_GO_ON_THE_QUEST);
					} else if (opt == 1) {
						legendsGuardDialogue(player, n, LegendsGuard.WHAT_KIND_OF_QUEST_IS_IT);
					}
					break;
				case LegendsGuard.HOW_DO_I_GET_IN_HERE:
					npcsay(player, n, player.getText("LegendsQuestGuildGuardHowDoIGetInHereWell"),
						"you'll need to be a legendary citizen of RuneScape.",
						"If you want to use the Legends Hall, ",
						"you'll be invited to complete a quest.",
						"Once you have completed that Quest,",
						"you'll be a fully fledged member of the Guild.");
					int opt2 = multi(player, n,
						"What is this place?",
						"Can I speak to someone in charge?",
						"Can I go on the quest?");
					if (opt2 == 0) {
						legendsGuardDialogue(player, n, LegendsGuard.WHAT_IS_THIS_PLACE);
					} else if (opt2 == 1) {
						legendsGuardDialogue(player, n, LegendsGuard.CAN_I_SPEAK_TO_SOMEONE_IN_CHARGE);
					} else if (opt2 == 2) {
						legendsGuardDialogue(player, n, LegendsGuard.CAN_I_GO_ON_THE_QUEST);
					}
					break;
				case LegendsGuard.CAN_I_SPEAK_TO_SOMEONE_IN_CHARGE:
					npcsay(player, n, player.getText("LegendsQuestGuildGuardCanISpeakToSomeoneInChargeWell"),
						"Radimus Erkle is the Grand Vizier of the Legends Guild.",
						"He's a very busy man.",
						"And he'll only talk to those people eligible for the quest.");
					int opt3 = multi(player, n,
						"Can I go on the quest?",
						"What kind of quest is it?");
					if (opt3 == 0) {
						legendsGuardDialogue(player, n, LegendsGuard.CAN_I_GO_ON_THE_QUEST);
					} else if (opt3 == 1) {
						legendsGuardDialogue(player, n, LegendsGuard.WHAT_KIND_OF_QUEST_IS_IT);
					}
					break;
				case LegendsGuard.ITS_OK_THANKS:
					npcsay(player, n, player.getText("LegendsQuestGuildGuardVeryWell"));
					break;
				case LegendsGuard.CAN_I_GO_ON_THE_QUEST:
					mes("The guard gets out a scroll of paper and starts looking through it.");
					delay(3);
					if (player.getQuestPoints() >= 107
						&& player.getQuestStage(Quests.HEROS_QUEST) == -1
						&& player.getQuestStage(Quests.FAMILY_CREST) == -1
						&& player.getQuestStage(Quests.SHILO_VILLAGE) == -1
						&& player.getQuestStage(Quests.UNDERGROUND_PASS) == -1
						&& player.getQuestStage(Quests.WATERFALL_QUEST) == -1) {
						npcsay(player, n, "Well, it looks as if you are eligable for the quest.",
							"Grand Vizier Erkle will give you the details about the quest.",
							"You can go and talk to him about it if you like?");
						int opt4 = multi(player, n,
							"Who is Grand Vizier Erkle?",
							"Yes, I'd like to talk to Grand Vizier Erkle.",
							"Some other time perhaps.");
						if (opt4 == 0) {
							legendsGuardDialogue(player, n, LegendsGuard.WHO_IS_GRAND_VIZIER_ERKLE);
						} else if (opt4 == 1) {
							legendsGuardDialogue(player, n, LegendsGuard.LIKE_TO_TALK_TO_GVE);
						}
					} else {
						npcsay(player, n, "I'm very sorry,",
							"But you need to complete more quests before you qualify.",
							"You also need to have 107 quest points.");
						int denyMenu = multi(player, n,
							"Which quests do I need to complete?",
							"Ok thanks.");
						if (denyMenu == 0) {
							npcsay(player, n, "You need to complete the...");
							if (player.getQuestStage(Quests.HEROS_QUEST) != -1) {
								npcsay(player, n, "Hero's Quest.");
							}
							if (player.getQuestStage(Quests.FAMILY_CREST) != -1) {
								npcsay(player, n, "Family Crest Quest.");
							}
							if (player.getQuestStage(Quests.SHILO_VILLAGE) != -1) {
								npcsay(player, n, "Shilo Village Quest.");
							}
							if (player.getQuestStage(Quests.UNDERGROUND_PASS) != -1) {
								npcsay(player, n, "Underground Pass Quest.");
							}
							if (player.getQuestStage(Quests.WATERFALL_QUEST) != -1) {
								npcsay(player, n, "Waterfall Quest.");
							}
							if (!player.getConfig().INFLUENCE_INSTEAD_QP && player.getQuestPoints() < 107) {
								npcsay(player, n, "You also need to have 107 Quest Points as well!");
							}
							npcsay(player, n, "They don't call it the Legends Guild for nothing you know!",
								"Best of luck if you intend to become a member!");
						} else if (denyMenu == 1) {
							npcsay(player, n, "That's no problem...",
								"Best of luck if you intend to become a member!");
						}
					}
					break;
				case LegendsGuard.WHAT_KIND_OF_QUEST_IS_IT:
					npcsay(player, n, player.getText("LegendsQuestGuildGuardWellToBeHonestImNotReallySure"),
						"You'll need to talk to Grand Vizier Erkle to find that out.");
					int opt4 = multi(player, n, false, //do not send over
						"Can I go on the quest?",
						"Thanks for your help.");
					if (opt4 == 0) {
						say(player, n, "Can I go on the quest?");
						legendsGuardDialogue(player, n, LegendsGuard.CAN_I_GO_ON_THE_QUEST);
					} else if (opt4 == 1) {
						say(player, n, "Thanks for your help");
						npcsay(player, n, "You're welcome..");
						player.message("The Guard marches off on patrol again.");
					}
					break;
				case LegendsGuard.WHO_IS_GRAND_VIZIER_ERKLE:
					npcsay(player, n, "He is the head of the Legends Guild.",
						"His full name is Radimus Erkle.",
						"Would you like to talk to him about the quest?");
					int opt5 = multi(player, n, false, //do not send over
						"Yes, I'd like to talk to Grand Vizier Erkle.",
						"Some other time perhaps.");
					if (opt5 == 0) {
						say(player, n, "Yes, I'd like to talk to Grand Vizier Erkle.");
						legendsGuardDialogue(player, n, LegendsGuard.LIKE_TO_TALK_TO_GVE);
					} else if (opt5 == 1) {
						say(player, n, "Some other time perhaps");
					}
					break;
				case LegendsGuard.LIKE_TO_TALK_TO_GVE:
					npcsay(player, n, "Ok, very well...",
						"You need  to go into the building on the left, he's in his study.");
					player.message("The guard unlocks the gate and opens it for you.");
					npcsay(player, n, "Good Luck!");
					openGates(player);
					break;
			}
		}
	}

	private void openGates(Player player) {
		GameObject the_gate = player.getWorld().getRegionManager().getRegion(Point.location(512, 550)).getGameObject(Point.location(512, 550), player);
		// If the gate is already open, we don't want to do anything.
		if (the_gate.getID() == MITHRIL_GATES) {
			changeloc(the_gate, config().GAME_TICK * 4, 181);
		}
		player.teleport(513, 549);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.LEGENDS_GUILD_GUARD.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.LEGENDS_GUILD_GUARD.id()) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 0) {
				mes("You approach a nearby guard...");
				delay(2);
			}
			legendsGuardDialogue(player, n, -1);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == MITHRIL_GATES;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == MITHRIL_GATES) {
			if (command.equals("open")) {
				if (player.getY() <= 550) {
					changeloc(obj, config().GAME_TICK * 4, 181);
					player.teleport(513, 552);
					return;
				}
				Npc legends_guard = ifnearvisnpc(player, NpcId.LEGENDS_GUILD_GUARD.id(), 5);
				switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 0:
						if (legends_guard != null) {
							mes("A nearby guard approaches you...");
							delay(2);
							legends_guard.initializeTalkScript(player);
						} else {
							player.message("The guards is currently busy.");
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
							player.message("A guard nods at you as you walk past.");
							npcsay(player, legends_guard, player.getText("LegendsQuestGuildGuardHopeTheQuestIsGoingWell"));
						}
						openGates(player);
						break;
					case 11:
					case -1:
						if (legends_guard != null) {
							player.message("The guards Salute you as you walk past.");
							npcsay(player, legends_guard, "! ! ! Attention ! ! !",
								"Legends Guild Member Approaching");
						}
						openGates(player);
						break;
				}

			} else if (command.equals("search")) {
				mes("The gates to the Legends Guild are made from wrought Mithril.");
				delay(2);
				mes("A small path leads away up to a very grandiose building.");
				delay(2);
				mes("To the left is a smaller out building, but it is no less impressive.");
				delay(2);
				mes("All the buildings are set in wonderfully landscaped gardens.");
				delay(2);
				player.message("Two well dressed guards seem to be guarding the gate.");
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
