package com.openrsc.server.plugins.quests.members.shilovillage;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

/**
 * @author Imposter/Fate
 * Shilo village Quest: Start template.
 * <p>
 * TODO:
 * the messages inside UndeadOnes a mist (possible to damage?)
 */
public class ShiloVillageMosolRei implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.MOSOL.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.MOSOL.id()) {
			moselReiDialogue(p, n, -1);
		}
	}

	private void moselReiDialogue(Player p, Npc n, int cID) {
		if (n.getID() == NpcId.MOSOL.id()) {
			if (cID == -1) {
				switch (p.getQuestStage(Quests.SHILO_VILLAGE)) {
					case -1:
						playerTalk(p, n, "Greetings!");
						npcTalk(p, n, "Hello Effendi,",
							"We have removed the threat of Rashiliyia and even though",
							"there are still some random outbreaks of undead activity,",
							"we are more than able to deal with it.",
							"You can now enter Shilo village.",
							"Please follow me...");
						int myMenu = showMenu(p,
							"Yes, OK, I'll go into the village!",
							"I think I'll see it some other time.");
						if (myMenu == 0) {
							p.message("Mosol leads you into the village.");
							p.teleport(395, 851);
							p.message("@yel@Mosol: Have a nice time!");
							p.message("Mosol leaves you by the gate and walks back out into the jungle.");
						} else if (myMenu == 1) {
							p.message("You decide to stay where you are.");
						}
						break;
					case 0:
						p.message("Mosol seems to be looking around very cautiously.");
						p.message("He jumps a little when you approach and talk to him.");
						npcTalk(p, n, "Run! Run for your life!",
							"Save yourself!",
							"I'll keep them back as long as I can...");
						int menu = showMenu(p, n,
							"Why do I need to run?",
							"Yeah..Ok, I'm running!",
							"Who are you?");
						if (menu == 0) {
							npcTalk(p, n, "Your very life is in danger!",
								"Rashiliyia has returned and we are all doomed!");
							int menu3 = showMenu(p, n,
								"Rashiliyia? Who is she?",
								"What danger is there around here?");
							if (menu3 == 0) {
								npcTalk(p, n, "Rashiliyia? She is the Queen of the dead!",
									"She has returned and has bought a plague of undead with her.",
									"They now occupy our village and we have them trapped.",
									"We warn people like yourself to stay away!");
								int menu4 = showMenu(p, n,
									"What can we do?",
									"Uh, it sounds nasty, just the kind of thing I want to avoid!");
								if (menu4 == 0) {
									moselReiDialogue(p, n, MoselRei.WHAT_CAN_WE_DO);
									/**
									 * STARTED SHILO VILLAGE QUEST!
									 */
									p.updateQuestStage(Quests.SHILO_VILLAGE, 1);
								} else if (menu4 == 1) {
									message(p, "Mosol casts a disaproving glance at you");
									npcTalk(p, n, "Quite right, bwana, please make all haste!",
										"Before your spine turns to water as we speak.");
								}
							} else if (menu3 == 1) {
								moselReiDialogue(p, n, MoselRei.WHAT_DANGER_IS_THERE);
							}
						} else if (menu == 1) {
							npcTalk(p, n, "God speed to you my friend!");
						} else if (menu == 2) {
							npcTalk(p, n, "I am Mosol Rei, a jungle warrior. ",
								"I used to live in this village.",
								"But it is too dangerous for you to stay around here!");
							int menu2 = showMenu(p, n,
								"Mosol Rei, that's a nice name.",
								"What danger is there around here?");
							if (menu2 == 0) {
								p.message("Mosol looks at you and shakes his head in bewilderment.");
								npcTalk(p, n, "Thanks! But you really should leave!");
							} else if (menu2 == 1) {
								moselReiDialogue(p, n, MoselRei.WHAT_DANGER_IS_THERE);
							}
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
						npcTalk(p, n, "Oh are you still here?",
							"The undead seem to be getting stronger!");
						int option = showMenu(p, n,
							"Why are the undead here?",
							"What can we do?");
						if (option == 0) {
							npcTalk(p, n, "Rashiliyia! The Queen of the dead has risen!",
								"She is the mother of the undead creatures that roam this land.",
								"But I know nothing of the legend that surounds her");
							int sub_opt = showMenu(p, n,
								"Legend you say?",
								"I don't think this is something I can help with at the moment!");
							if (sub_opt == 0) {
								npcTalk(p, n, "Yes. I said it was a legend that I know nothing about.");
								int sub_opt2 = showMenu(p, n,
									"Oh, Ok, sorry for bothering you",
									"Oh come on, you must know something!",
									"Maybe you know someone who does know something?");
								if (sub_opt2 == 0) {
									npcTalk(p, n, "Ok, perhaps you'd like to be on your way now?");
								} else if (sub_opt2 == 1) {
									message(p, "Mosol lowers his brows in deep concentration");
									npcTalk(p, n, "Well, let me have a think?");
									message(p, "He scratches his head.");
									npcTalk(p, n, "Hmmm, there was something I think that might help...",
										"No, sorry, it's gone.");
									int sub_opt3 = showMenu(p, n,
										"Maybe you know someone who does know something?",
										"Oh, Ok, sorry for bothering you");
									if (sub_opt3 == 0) {
										moselReiDialogue(p, n, MoselRei.SOMEONE_WHO_DOES_KNOW);
									} else if (sub_opt3 == 1) {
										npcTalk(p, n, "Ok, perhaps you'd like to be on your way now?");
									}
								} else if (sub_opt2 == 2) {
									moselReiDialogue(p, n, MoselRei.SOMEONE_WHO_DOES_KNOW);
								}
							} else if (sub_opt == 1) {
								npcTalk(p, n, "Ok, I understand, you may as well be on your way then.");
							}
						} else if (option == 1) {
							moselReiDialogue(p, n, MoselRei.WHAT_CAN_WE_DO);
						}
						break;
				}
			}
			switch (cID) {
				case MoselRei.WHAT_DANGER_IS_THERE:
					npcTalk(p, n, "Can you not see Bwana?",
						"This whole area is infested with the Living dead.");
					break;
				case MoselRei.WHAT_CAN_WE_DO:
					npcTalk(p, n, "We are doing all that we can just to keep the undead at bay!",
						"The village is covered in a deadly green mist.",
						"If you go into the village, a terrible sickness will befall you.",
						"And the undead creatures are even stonger beyond the gates.",
						"My guess is that it has something to do with the legend of Rashiliyia.",
						"But you would need to speak to the Witch Doctor in the Tai Bwo Wannai village.",
						"To get more details about that.",
						"I really have to go now and fight these undead!");
					break;
				case MoselRei.SOMEONE_WHO_DOES_KNOW:
					npcTalk(p, n, "My guess is that this has something to do with the legend of Rashiliyia.",
						"But you need to speak to the Witch Doctor in 'Tai Bwo Wannai' village.",
						"To get more details about that.",
						"I really have to go now and fight these undead",
						"Before they take over the world!");
					break;
			}
		}
	}

	class MoselRei {
		static final int WHAT_DANGER_IS_THERE = 0;
		static final int WHAT_CAN_WE_DO = 1;
		static final int SOMEONE_WHO_DOES_KNOW = 2;
	}
}
