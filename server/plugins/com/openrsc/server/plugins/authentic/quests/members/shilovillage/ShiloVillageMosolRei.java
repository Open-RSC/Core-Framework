package com.openrsc.server.plugins.authentic.quests.members.shilovillage;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

/**
 * TODO:
 * the messages inside UndeadOnes a mist (possible to damage?)
 */
public class ShiloVillageMosolRei implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MOSOL.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.MOSOL.id()) {
			moselReiDialogue(player, n, -1);
		}
	}

	private void moselReiDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.MOSOL.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.SHILO_VILLAGE)) {
					case -1:
						say(player, n, "Greetings!");
						npcsay(player, n, "Hello Effendi,",
							"We have removed the threat of Rashiliyia and even though",
							"there are still some random outbreaks of undead activity,",
							"we are more than able to deal with it.",
							"You can now enter Shilo village.",
							"Please follow me...");
						int myMenu = multi(player,
							"Yes, OK, I'll go into the village!",
							"I think I'll see it some other time.");
						if (myMenu == 0) {
							player.message("Mosol leads you into the village.");
							player.teleport(395, 851);
							player.message("@yel@Mosol: Have a nice time!");
							player.message("Mosol leaves you by the gate and walks back out into the jungle.");
						} else if (myMenu == 1) {
							player.message("You decide to stay where you are.");
						}
						break;
					case 0:
						player.message("Mosol seems to be looking around very cautiously.");
						player.message("He jumps a little when you approach and talk to him.");
						npcsay(player, n, "Run! Run for your life!",
							"Save yourself!",
							"I'll keep them back as long as I can...");
						int menu = multi(player, n,
							"Why do I need to run?",
							"Yeah..Ok, I'm running!",
							"Who are you?");
						if (menu == 0) {
							npcsay(player, n, "Your very life is in danger!",
								"Rashiliyia has returned and we are all doomed!");
							int menu3 = multi(player, n,
								"Rashiliyia? Who is she?",
								"What danger is there around here?");
							if (menu3 == 0) {
								npcsay(player, n, "Rashiliyia? She is the Queen of the dead!",
									"She has returned and has bought a plague of undead with her.",
									"They now occupy our village and we have them trapped.",
									"We warn people like yourself to stay away!");
								int menu4 = multi(player, n,
									"What can we do?",
									"Uh, it sounds nasty, just the kind of thing I want to avoid!");
								if (menu4 == 0) {
									moselReiDialogue(player, n, MoselRei.WHAT_CAN_WE_DO);
									/**
									 * STARTED SHILO VILLAGE QUEST!
									 */
									player.updateQuestStage(Quests.SHILO_VILLAGE, 1);
								} else if (menu4 == 1) {
									mes("Mosol casts a disaproving glance at you");
									delay(3);
									npcsay(player, n, "Quite right, bwana, please make all haste!",
										"Before your spine turns to water as we speak.");
								}
							} else if (menu3 == 1) {
								moselReiDialogue(player, n, MoselRei.WHAT_DANGER_IS_THERE);
							}
						} else if (menu == 1) {
							npcsay(player, n, "God speed to you my friend!");
						} else if (menu == 2) {
							npcsay(player, n, "I am Mosol Rei, a jungle warrior. ",
								"I used to live in this village.",
								"But it is too dangerous for you to stay around here!");
							int menu2 = multi(player, n,
								"Mosol Rei, that's a nice name.",
								"What danger is there around here?");
							if (menu2 == 0) {
								player.message("Mosol looks at you and shakes his head in bewilderment.");
								npcsay(player, n, "Thanks! But you really should leave!");
							} else if (menu2 == 1) {
								moselReiDialogue(player, n, MoselRei.WHAT_DANGER_IS_THERE);
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
						npcsay(player, n, "Oh are you still here?",
							"The undead seem to be getting stronger!");
						int option = multi(player, n,
							"Why are the undead here?",
							"What can we do?");
						if (option == 0) {
							npcsay(player, n, "Rashiliyia! The Queen of the dead has risen!",
								"She is the mother of the undead creatures that roam this land.",
								"But I know nothing of the legend that surounds her");
							int sub_opt = multi(player, n,
								"Legend you say?",
								"I don't think this is something I can help with at the moment!");
							if (sub_opt == 0) {
								npcsay(player, n, "Yes. I said it was a legend that I know nothing about.");
								int sub_opt2 = multi(player, n,
									"Oh, Ok, sorry for bothering you",
									"Oh come on, you must know something!",
									"Maybe you know someone who does know something?");
								if (sub_opt2 == 0) {
									npcsay(player, n, "Ok, perhaps you'd like to be on your way now?");
								} else if (sub_opt2 == 1) {
									mes("Mosol lowers his brows in deep concentration");
									delay(3);
									npcsay(player, n, "Well, let me have a think?");
									mes("He scratches his head.");
									delay(3);
									npcsay(player, n, "Hmmm, there was something I think that might help...",
										"No, sorry, it's gone.");
									int sub_opt3 = multi(player, n,
										"Maybe you know someone who does know something?",
										"Oh, Ok, sorry for bothering you");
									if (sub_opt3 == 0) {
										moselReiDialogue(player, n, MoselRei.SOMEONE_WHO_DOES_KNOW);
									} else if (sub_opt3 == 1) {
										npcsay(player, n, "Ok, perhaps you'd like to be on your way now?");
									}
								} else if (sub_opt2 == 2) {
									moselReiDialogue(player, n, MoselRei.SOMEONE_WHO_DOES_KNOW);
								}
							} else if (sub_opt == 1) {
								npcsay(player, n, "Ok, I understand, you may as well be on your way then.");
							}
						} else if (option == 1) {
							moselReiDialogue(player, n, MoselRei.WHAT_CAN_WE_DO);
						}
						break;
				}
			}
			switch (cID) {
				case MoselRei.WHAT_DANGER_IS_THERE:
					npcsay(player, n, "Can you not see Bwana?",
						"This whole area is infested with the Living dead.");
					break;
				case MoselRei.WHAT_CAN_WE_DO:
					npcsay(player, n, "We are doing all that we can just to keep the undead at bay!",
						"The village is covered in a deadly green mist.",
						"If you go into the village, a terrible sickness will befall you.",
						"And the undead creatures are even stonger beyond the gates.",
						"My guess is that it has something to do with the legend of Rashiliyia.",
						"But you would need to speak to the Witch Doctor in the Tai Bwo Wannai village.",
						"To get more details about that.",
						"I really have to go now and fight these undead!");
					break;
				case MoselRei.SOMEONE_WHO_DOES_KNOW:
					npcsay(player, n, "My guess is that this has something to do with the legend of Rashiliyia.",
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
