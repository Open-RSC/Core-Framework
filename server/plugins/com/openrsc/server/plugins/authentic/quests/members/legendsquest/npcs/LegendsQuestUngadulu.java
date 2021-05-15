package com.openrsc.server.plugins.authentic.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestUngadulu implements TalkNpcTrigger, AttackNpcTrigger, SpellNpcTrigger, PlayerRangeNpcTrigger, EscapeNpcTrigger, UseNpcTrigger {

	private static void ungaduluTalkToDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.UNGADULU.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 2:
					case 3:
						npcsay(player, n, "Please run for your life...");
						mes(n, "The Shaman seems to be fighting an inner battle.");
						delay(2);
						npcsay(player, n, "Go...go now...!");
						n = changenpc(n, NpcId.EVIL_UNGADULU.id(), true);
						mes(n, "The Shaman seems to change in front of your eyes...");
						delay(2);
						evilUngadulu(player, n);
						break;
					case 4:
						npcsay(player, n, "Greetings bwana...many thanks for defeating the demon...",
							"and releasing me from this dreadful possesion...",
							"Pray tell me, what can I do to repay this great favour?");
						int menu = multi(player, n,
							"I need to collect some Yommi tree seeds for Gujuo.",
							"How do I get out of here?",
							"Ok, thanks...");
						if (menu == 0) {
							ungaduluTalkToDialogue(player, n, Ungadulu.COLLECT_SOME_YOMMI_SEEDS_FOR_GUJUO);
						} else if (menu == 1) {
							ungaduluTalkToDialogue(player, n, Ungadulu.HOW_DO_I_GET_OUT_OF_HERE);
						} else if (menu == 2) {
							ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
						}
						break;
					case 5:
						npcsay(player, n, "Hello Bwana, how goes your quest with the Yommi tree?");
						int opt = multi(player, n,
							"I have germinated the seeds.",
							"Where do I plant the seeds?",
							"I need more Yommi tree seeds.");
						if (opt == 0) {
							ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_GERMINATED_THE_SEEDS);
						} else if (opt == 1) {
							ungaduluTalkToDialogue(player, n, Ungadulu.WHERE_DO_I_PLANT_THE_SEEDS);
						} else if (opt == 2) {
							ungaduluTalkToDialogue(player, n, Ungadulu.I_NEED_MORE_YOMMI_TREE_SEEDS);
						}
						break;
					case 6:
						npcsay(player, n, "Hello Bwana, how goes your quest with the Yommi tree?");
						int newMenu4 = multi(player, n,
							"The magic pool has dried up and I need some more pure water.",
							"Where can I get more pure water?",
							"I need more Yommi tree seeds.");
						if (newMenu4 == 0) {
							ungaduluTalkToDialogue(player, n, Ungadulu.THE_MAGIC_POOL_HAS_DRIED_UP);
						} else if (newMenu4 == 1) {
							ungaduluTalkToDialogue(player, n, Ungadulu.WHERE_CAN_I_GET_MORE_PURE_WATER);
						} else if (newMenu4 == 2) {
							ungaduluTalkToDialogue(player, n, Ungadulu.I_NEED_MORE_YOMMI_TREE_SEEDS);
						}
						break;
					case 7:
						if (player.getCache().hasKey("met_spirit") && player.getCache().hasKey("killed_viyeldi")) {
							npcsay(player, n, "Hello Bwana, how goes your quest to find the water ?");
							int newMenu9 = multi(player, n,
								"I have killed Viyeldi!",
								"I met a spirit in the Viyeldi Caves.",
								"The spirit told me to kill Viyeldi.",
								"Do you know anything about daggers?",
								"I need more Yommi tree seeds.");
							if (newMenu9 == 0) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_KILLED_VIYELDI);
							} else if (newMenu9 == 1) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_MET_A_SPIRIT_IN_THE_VIYELDI_CAVES);
							} else if (newMenu9 == 2) {
								ungaduluTalkToDialogue(player, n, Ungadulu.THE_SPIRIT_TOLD_ME_TO_KILL_VIYELDI);
							} else if (newMenu9 == 3) {
								ungaduluTalkToDialogue(player, n, Ungadulu.DO_YOU_KNOW_ANYTHING_ABOUT_DAGGERS);
							} else if (newMenu9 == 4) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_NEED_MORE_YOMMI_TREE_SEEDS);
							}
						} else if (player.getCache().hasKey("met_spirit") && !player.getCache().hasKey("killed_viyeldi")) {
							npcsay(player, n, "Hello Bwana, how goes your quest to find the water ?");
							int newMenu9 = multi(player, n,
								"I met a spirit in the Viyeldi Caves.",
								"The spirit told me to kill Viyeldi.",
								"Do you know anything about daggers?",
								"I need more Yommi tree seeds.",
								"Ok, thanks...");
							if (newMenu9 == 0) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_MET_A_SPIRIT_IN_THE_VIYELDI_CAVES);
							} else if (newMenu9 == 1) {
								ungaduluTalkToDialogue(player, n, Ungadulu.THE_SPIRIT_TOLD_ME_TO_KILL_VIYELDI);
							} else if (newMenu9 == 2) {
								ungaduluTalkToDialogue(player, n, Ungadulu.DO_YOU_KNOW_ANYTHING_ABOUT_DAGGERS);
							} else if (newMenu9 == 3) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_NEED_MORE_YOMMI_TREE_SEEDS);
							} else if (newMenu9 == 4) {
								ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
							}
						} else {
							npcsay(player, n, "Hello Bwana, how goes your quest with the Yommi tree?");
							int newMenu9 = multi(player, n,
								"I am on a quest to get more pure water.",
								"What do you know about the source of the sacred water?",
								"I need more Yommi tree seeds.");
							if (newMenu9 == 0) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_AM_ON_A_QUEST_TO_GET_MORE_PURE_WATER);
							} else if (newMenu9 == 1) {
								ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_YOU_KNOW_ABOUT_THE_SOURCE_OF_THE_SACRED_WATER);
							} else if (newMenu9 == 2) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_NEED_MORE_YOMMI_TREE_SEEDS);
							}
						}
						break;
					case 8:
						if (!player.getCache().hasKey("crafted_totem_pole")) {
							mes(n, "You approach Ungadulu...");
							delay(2);
							npcsay(player, n, "Blessings on you Bwana.",
								"Did you use the spell and kill the spirit?",
								"Do you have the sacred water yet?");
							mes(n, "The Shaman looks so excited about seeing you that he is about to burst.");
							delay(2);
							int f_menu = multi(player, n,
								"Yes, I've killed the Spirit.",
								"Yes, I've got the water.",
								"I need more Yommi tree seeds.");
							if (f_menu == 0) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_KILLED_THE_SPIRIT);
							} else if (f_menu == 1) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_GOT_THE_WATER);
							} else if (f_menu == 2) {
								ungaduluTalkToDialogue(player, n, Ungadulu.I_NEED_MORE_YOMMI_TREE_SEEDS);
							}
							break;
						} else {
							//per-wiki
							if (!player.getCarriedItems().hasCatalogID(ItemId.TOTEM_POLE.id(), Optional.of(false))
								&& !player.getCarriedItems().hasCatalogID(ItemId.YOMMI_TREE_SEED.id(), Optional.of(false))) {
								npcsay(player, n, "I see you have no totem pole, or Yommi tree seeds, is everything Ok?");
								int menuopts = multi(player, n, "Yes, everything's fine.", "I need more Yommi tree seeds.");
								if (menuopts == 0) {
									npcsay(player, n, "Your Legendary exploits are travelling the whole jungle.",
											"How goes your quest to grow the sacred Yommi tree ?");
									int submenu = multi(player, n, "I've already made the totem pole.",
											"I'm not sure what to do with the Totem pole.", "Ok, thanks...");
									if (submenu == 0) {
										ungaduluTalkToDialogue(player, n, Ungadulu.MADE_TOTEM_POLE);
									} else if (submenu == 1) {
										ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_TOTEM_POLE);
									} else if (submenu == 2) {
										ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
									}
								} else if (menuopts == 1) {
									ungaduluTalkToDialogue(player, n, Ungadulu.I_NEED_MORE_YOMMI_TREE_SEEDS);
								}
							} else {
								npcsay(player, n, "Your Legendary exploits are travelling the whole jungle.",
										"How goes your quest to grow the sacred Yommi tree ?");
								int submenu = multi(player, n, "I've already made the totem pole.",
										"I'm not sure what to do with the Totem pole.", "Ok, thanks...");
								if (submenu == 0) {
									ungaduluTalkToDialogue(player, n, Ungadulu.MADE_TOTEM_POLE);
								} else if (submenu == 1) {
									ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_TOTEM_POLE);
								} else if (submenu == 2) {
									ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
								}
							}
						}
						break;
					case 9:
						//per wiki
						npcsay(player, n, "Your Legendary exploits are travelling the whole jungle.",
								"How goes your quest to grow the sacred Yommi tree ?");
						int newMenu10 = multi(player, n, "I've killed Nezikchened the Demon again.",
								"I've replaced the evil Totem pole.", "Ok, thanks...");
						if (newMenu10 == 0) {
							ungaduluTalkToDialogue(player, n, Ungadulu.KILLED_DEMON_AGAIN);
						} else if (newMenu10 == 1) {
							ungaduluTalkToDialogue(player, n, Ungadulu.REPLACED_EVIL_TOTEM);
						} else if (newMenu10 == 2) {
							ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
						}
						break;
					case 10:
					case 11:
					case -1:
						npcsay(player, n, "Your Legendary exploits are travelling the whole jungle.",
							"Gujuo has been to see me. ",
							"He told me that you have been given a sacred totem pole.",
							"It was constructed by one of my ancestors many moons ago.",
							"It is a noble prize Bwana, you have earned it,",
							"look after it well.");
						break;
				}
			}
			switch (cID) {
				case Ungadulu.OK_THANKS:
					npcsay(player, n, "My sincerest pleasure Bwana...");
					break;
				case Ungadulu.WHAT_DO_I_DO_NOW:
					npcsay(player, n, "Well, you should be able to plant the Yommi tree.",
						"And then water it with the sacred water.",
						"You should then be able to start making the Totem pole.",
						"So long as you have banished the spirit",
						"And managed to get some of the sacred water.");
					int y_menu = multi(player, n,
						"Yes, I've got the water.",
						"Yes, I've killed the Spirit.",
						"Ok, thanks...");
					if (y_menu == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_GOT_THE_WATER);
					} else if (y_menu == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_KILLED_THE_SPIRIT);
					} else if (y_menu == 2) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.I_HAVE_KILLED_THE_SPIRIT:
					say(player, n, "The spirit actually turned out to be the Demon - Nezikchened.");
					npcsay(player, n, "That's truly a miracle Bwana,",
						"very few come out of Viyeldi's caves alive.",
						"And you managed to defeat Nezikchened a second time?",
						"You are truly a legend bwana.",
						"Do you have the sacred water yet?");
					int f_menu = multi(player, n,
						"Yes, I've got the water.",
						"What do I do now?",
						"Ok, thanks...");
					if (f_menu == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_GOT_THE_WATER);
					} else if (f_menu == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_I_DO_NOW);
					} else if (f_menu == 2) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.I_HAVE_GOT_THE_WATER:
					npcsay(player, n, "That is truly great Bwana...well done!",
						"You have the spirit of the jungle lion",
						"Did you use the spell and kill the spirit?");
					int x_menu = multi(player, n, false, //do not send over
						"Yes, I've killed the Spirit.",
						"What do I do now?",
						"Ok, thanks...");
					if (x_menu == 0) {
						say(player, n, "Yes, I've killed the Spirit.");
						ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_KILLED_THE_SPIRIT);
					} else if (x_menu == 1) {
						say(player, n, "What do I do now ?");
						ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_I_DO_NOW);
					} else if (x_menu == 2) {
						say(player, n, "Ok, thanks...");
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.DO_YOU_KNOW_ANYTHING_ABOUT_DAGGERS:
					npcsay(player, n, "I know something about them, especially magical daggers.",
						"If you have a specific one, show it to me and I'll help",
						"as much as I can.");
					boolean killedViyeldi = player.getCache().hasKey("killed_viyeldi");
					String[] menuOpts;
					if (killedViyeldi) {
						menuOpts = new String[]{"I have killed Viyeldi!",
								"The spirit told me to kill Viyeldi.",
								"Ok, thanks..."};
					} else {
						menuOpts = new String[]{"I met a spirit in the Viyeldi Caves.",
								"The spirit told me to kill Viyeldi.",
								"Ok, thanks..."};
					}
					int reply3 = multi(player, n, menuOpts);
					if (reply3 == 0) {
						if (killedViyeldi) {
							ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_KILLED_VIYELDI);
						}
						else {
							ungaduluTalkToDialogue(player, n, Ungadulu.I_MET_A_SPIRIT_IN_THE_VIYELDI_CAVES);
						}
					} else if (reply3 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.THE_SPIRIT_TOLD_ME_TO_KILL_VIYELDI);
					} else if (reply3 == 2) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.I_HAVE_KILLED_VIYELDI:
					npcsay(player, n, "Why on earth did you do that?");
					mes(n, "The Shaman screams at you...");
					delay(2);
					say(player, n, "A spirit called Echned Zekin said I had to avenge his spirit",
						"by killing Viyeldi if I wanted to get the pure water.");
					mes(n, "The Shaman puts his head in his hands.");
					delay(2);
					npcsay(player, n, "Bwana, you have been tricked by a spirit !",
						"And you have done the worst thing imaginable.",
						"Viyeldi was the sorcerer who controlled the Hero's who protect.",
						"the source.",
						"The spirits of these hero's are now free",
						"to be controlled by other, more powerful forces.",
						"Most likely the spirit that tricked you.");
					int reply4 = multi(player, n,
						"Do you know anything about daggers?",
						"What can we do?",
						"Ok, thanks...");
					if (reply4 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.DO_YOU_KNOW_ANYTHING_ABOUT_DAGGERS);
					} else if (reply4 == 1) {
						if (player.getCarriedItems().hasCatalogID(ItemId.HOLY_FORCE_SPELL.id(), Optional.of(false))) {
							npcsay(player, n, "You can use that Holy Force spell to try and defeat the spirit.",
									"Come back and let me know if I can help in any other way.");
						} else {
							npcsay(player, n, "I am not sure at this time Bwana.",
									"Give me a few moments to think.",
									"Hmmm....");
							mes(n, "The Shaman looks as if he's thinking very deeply.");
							delay(2);
							mes(n, "The wizened old Shaman hands over a piece of paper.");
							delay(2);
							npcsay(player, n, "Take this spell and pray that you can defeat",
									"this evil spirit before it's too late.");
							give(player, ItemId.HOLY_FORCE_SPELL.id(), 1);
							npcsay(player, n, "I'll take that dagger from you now!");
							player.getCarriedItems().remove(new Item(ItemId.GLOWING_DARK_DAGGER.id()));
						}
					} else if (reply4 == 2) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.I_MET_A_SPIRIT_IN_THE_VIYELDI_CAVES:
					npcsay(player, n, "You did well to come to me Bwana...",
						"As I said, I am an expert in spirits of the underworld...",
						"In most circumstances you should just ignore them.",
						"However, beware as many spirits will try to trick you.");
					int reply2 = multi(player, n,
						"The spirit told me to kill Viyeldi.",
						"Ok, thanks...");
					if (reply2 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.THE_SPIRIT_TOLD_ME_TO_KILL_VIYELDI);
					} else if (reply2 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.THE_SPIRIT_TOLD_ME_TO_KILL_VIYELDI:
					npcsay(player, n, "That sounds very strange Bwana,",
						"I'm glad to see that you didn't comit such a foul act.",
						"I can make a spell that would help you to defeat the spirit.",
						"But I need an item that belongs to the spirit to make it work.",
						"If you have something like that, please show it to me.",
						"And I'll give you the spell.",
						"Beware of everyone in these caves,",
						"I was tricked very easily and was enslaved, as you well know.");
					int reply = multi(player, n,
						"I met a spirit in the Viyeldi Caves.",
						"Ok, thanks...");
					if (reply == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.I_MET_A_SPIRIT_IN_THE_VIYELDI_CAVES);
					} else if (reply == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.WHAT_DO_YOU_KNOW_ABOUT_THE_SOURCE_OF_THE_SACRED_WATER:
					npcsay(player, n, "It is said that the caves where the stream is located, ",
						"are littered with strange remains of a past civilisation.",
						"The dwarves are said to have excavated the area in search",
						"of the source of the sacred water.",
						"Something bad must have happened because soon the area was cursed.",
						"Anyone who entered the area looking for the source of the water,",
						"And who died, would be forver cursed to protect the water...",
						"...forever...");
					int newMenu8 = multi(player, n,
						"I am on a quest to get more pure water.",
						"Ok, thanks...");
					if (newMenu8 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.I_AM_ON_A_QUEST_TO_GET_MORE_PURE_WATER);
					} else if (newMenu8 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.I_AM_ON_A_QUEST_TO_GET_MORE_PURE_WATER:
					npcsay(player, n, "Well, good luck with your quest Bwana.",
						"You may well find it worthwhile exploring these catacombs.",
						"There is said to be an entrance to the Viyeldi caves.",
						"Which is where the sacred source of the magic pool exists.",
						"Beware though as it is said that the area is cursed.",
						"Anyone who is killed seeking the sacred water,",
						"will forever be sworn to protect it's secret.");
					int newMenu7 = multi(player, n,
						"What do you know about the source of the sacred water?",
						"Ok, thanks...");
					if (newMenu7 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_YOU_KNOW_ABOUT_THE_SOURCE_OF_THE_SACRED_WATER);
					} else if (newMenu7 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.THE_MAGIC_POOL_HAS_DRIED_UP:
					npcsay(player, n, "Hmmm, that sounds odd..",
						"I'm sure that Gujuo will tell you the same as me though.",
						"Searching for the source of the water pool will be difficult.",
						"However, with some help, it might be possible.");
					int newMenu6 = multi(player, n,
						"Where can I get more pure water?",
						"Ok, thanks...");
					if (newMenu6 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.WHERE_CAN_I_GET_MORE_PURE_WATER);
					} else if (newMenu6 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.WHERE_CAN_I_GET_MORE_PURE_WATER:
					npcsay(player, n, "There is said to be a stream of the sacred water that exists underground.",
						"I'm sure that Gujuo will tell you quite a lot about it.",
						"I'm have not explored outside of this room, but I have heard",
						"that there is a door within these catacombs which challenges any",
						"person with a riddle.",
						"Very few have solved the riddle, ",
						"and fewer have been returned alive if they did solve it.",
						"You can try to explore these caverns, it may help.",
						"You may just be able to find the Viyeldi caves.",
						"That is where the sacred source of the pure water resides...");
					int newMenu5 = multi(player, n, "The magic pool has dried up and I need some more pure water.",
						"Ok, thanks...");
					if (newMenu5 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.THE_MAGIC_POOL_HAS_DRIED_UP);
					} else if (newMenu5 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.I_NEED_MORE_YOMMI_TREE_SEEDS:
					if (player.getCarriedItems().hasCatalogID(ItemId.GERMINATED_YOMMI_TREE_SEED.id(), Optional.of(false))
						|| player.getCarriedItems().hasCatalogID(ItemId.YOMMI_TREE_SEED.id(), Optional.of(false))) {
						npcsay(player, n, "You already have some Yommi tree seeds...",
							"Use those first and then come back to me if you need any more.");
						player.message("Ungadulu goes back to his studies.");
					} else {
						mes(n, "Ungadulu gives you some more seeds..");
						delay(2);
						give(player, ItemId.GERMINATED_YOMMI_TREE_SEED.id(), 3);
						npcsay(player, n, "Take more care of these this time around.");
					}
					break;
				case Ungadulu.I_HAVE_GERMINATED_THE_SEEDS:
					npcsay(player, n, "Great Bwana, now go plant them in the fertile soil.",
						"You should soon have a great Yommi tree worthy of a most marvelous",
						"totem pole.");
					int opt3 = multi(player, n,
						"Where do I plant the seeds?",
						"Ok, thanks...");
					if (opt3 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.WHERE_DO_I_PLANT_THE_SEEDS);
					} else if (opt3 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.WHERE_DO_I_PLANT_THE_SEEDS:
					npcsay(player, n, "Above ground and spaced out througout the whole jungle area",
						"are specially cultivated ferteile soil areas.",
						"Seek one out and plant the Yommi tree in that...",
						"be prepared to water it though...");
					int opt2 = multi(player, n, "I have germinated the seeds.",
						"Ok, thanks...");
					if (opt2 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.I_HAVE_GERMINATED_THE_SEEDS);
					} else if (opt2 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.HOW_DO_I_GET_OUT_OF_HERE:
					if (player.getCarriedItems().hasCatalogID(ItemId.MAGICAL_FIRE_PASS.id(), Optional.of(false))) {
						npcsay(player, n, "Just use the Magical Fire Pass that I gave you to",
							"get past the flames...",
							"Then you should be able to find your way out through",
							"the cave entrance that you came in.");
					} else {
						npcsay(player, n, "Well, the way you came, but here...");
						mes(n, "The Shaman scrawls a some strange markings onto a piece of paper.");
						delay(2);
						give(player, ItemId.MAGICAL_FIRE_PASS.id(), 1);
						player.message("He hands the paper to you...");
						npcsay(player, n, "This will allow you to pass the fire without harm in future.");
					}
					int chapter = multi(player, n, "I need to collect some Yommi tree seeds for Gujuo.",
						"What will you do now?",
						"Ok, thanks...");
					if (chapter == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.COLLECT_SOME_YOMMI_SEEDS_FOR_GUJUO);
					} else if (chapter == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_WILL_YOU_DO_NOW);
					} else if (chapter == 2) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.WHAT_WILL_YOU_DO_NOW:
					npcsay(player, n, "I will remain here in the protection of the flaming Octagram",
						"and continue my research into the spirit world...",
						"I am somewhat of an authority with my recent experience!",
						"But do remember me from time to time and come to visit an old man.",
						"You never know, I may be able to help in you in the future.",
						"And repay you the favour of releasing me from that terrible Demon...");
					int chapter2 = multi(player, n,
						"I need to collect some Yommi tree seeds for Gujuo.",
						"How do I get out of here?",
						"Ok, thanks...");
					if (chapter2 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.COLLECT_SOME_YOMMI_SEEDS_FOR_GUJUO);
					} else if (chapter2 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.HOW_DO_I_GET_OUT_OF_HERE);
					} else if (chapter2 == 2) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.COLLECT_SOME_YOMMI_SEEDS_FOR_GUJUO:
					if (!player.getCarriedItems().hasCatalogID(ItemId.YOMMI_TREE_SEED.id(), Optional.of(false))) {
						npcsay(player, n, "Oh, yes, Bwana...you will be doing a great favour to our people",
							"by doing this..however, you must know that it is a difficult task.",
							"the Yommi tree is difficult to grow. You must have a natural ability",
							"with such things to have a chance...");
						mes(n, "The Shaman holds out his gnarly old hand and reveals three largish green seeds.");
						delay(2);
						npcsay(player, n, "Here you go...",
							"Accept these with my gratitude...",
							"You'll need to soak them in pure water before planting them.",
							"I notice that you are already familiar with it ",
							"to have passed the flaming Octagram.");
						give(player, ItemId.YOMMI_TREE_SEED.id(), 3);
						int newMenu = multi(player, n,
							"How do I grow the Yommi tree.",
							"What do you know about the pure water.",
							"Ok, thanks...");
						if (newMenu == 0) {
							ungaduluTalkToDialogue(player, n, Ungadulu.HOW_DO_I_GROW_THE_YOMMI_TREE);
						} else if (newMenu == 1) {
							ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_YOU_KNOW_ABOUT_THE_PURE_WATER);
						} else if (newMenu == 2) {
							ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
						}
					} else {
						npcsay(player, n, "You already have some Yommi tree seeds, use those first..",
							"and let me know how you get along.");
						int option2 = multi(player, n,
							"How do I grow the Yommi tree.",
							"What do you know about the pure water.");
						if (option2 == 0) {
							ungaduluTalkToDialogue(player, n, Ungadulu.HOW_DO_I_GROW_THE_YOMMI_TREE);
						} else if (option2 == 1) {
							ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_YOU_KNOW_ABOUT_THE_PURE_WATER);
						}
					}
					break;
				case Ungadulu.HOW_DO_I_GROW_THE_YOMMI_TREE:
					npcsay(player, n, "A good question Bwana...but it is essentially quite simple.",
						"First you will need to soak the seeds in some pure water...",
						"This will help to geminate the seed and begin the growing process.",
						"The Yommi tree is sacred and is also slightly magical.",
						"You need to seek out a patch of fertile earth. ",
						"Such places are located around the jungle and should give ",
						"the Yommi tree a good chance of survival.",
						"The tree should show some remarkable growth quite early",
						"But will slow down, you may be able to speed the process up ",
						"by watering the tree with more pure water, although",
						"it can be difficult to find it.");
					int option = multi(player, n,
						"What will you do now?",
						"What do you know about the pure water.",
						"Ok, thanks...");
					if (option == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_WILL_YOU_DO_NOW);
					} else if (option == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_YOU_KNOW_ABOUT_THE_PURE_WATER);
					} else if (option == 2) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.WHAT_DO_YOU_KNOW_ABOUT_THE_PURE_WATER:
					npcsay(player, n, "Hmmm, the pure water is sacred to us.",
						"It is from a sacred spring which is fed from deep underground.",
						"It is said that the spring is protected by spirits of long ",
						"dead adventurers who went in search of the springs source..",
						"But it is likely a myth and the source of the spring is buried",
						"deep in the ground with no chance of access.");
					int next = multi(player, n,
						"What will you do now?",
						"How do I get out of here?",
						"Ok, thanks...");
					if (next == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_WILL_YOU_DO_NOW);
					} else if (next == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.HOW_DO_I_GET_OUT_OF_HERE);
					} else if (next == 2) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.MADE_TOTEM_POLE:
					npcsay(player, n, "This is great news Bwana, you've done really well.",
							"Perhaps we can start to rally our people together now.",
							"And live once again without fear in the jungle.");
					int otheropts = multi(player, n, "I'm not sure what to do with the Totem pole.", "Ok, thanks...");
					if (otheropts == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.WHAT_DO_TOTEM_POLE);
					} else if (otheropts == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.WHAT_DO_TOTEM_POLE:
					npcsay(player, n, "Well, Bwana, you can simply replace the corrupted totem",
							"pole with the good one you have created.",
							"This will make my people very happy.");
					int otheropts2 = multi(player, n, "I've already made the totem pole.", "Ok, thanks...");
					if (otheropts2 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.MADE_TOTEM_POLE);
					} else if (otheropts2 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.KILLED_DEMON_AGAIN:
					npcsay(player, n, "If you have killed him for the third time,",
							"then you have banished him from our world completely.",
							"This is indeed a legendary accomplishment Bwana,",
							"you should feel proud.");
					int other = multi(player, n, "I've replaced the evil Totem pole.", "Ok, thanks...");
					if (other == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.REPLACED_EVIL_TOTEM);
					} else if (other == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
				case Ungadulu.REPLACED_EVIL_TOTEM:
					npcsay(player, n, "Many thanks Bwana, my people are truly grateful.",
							"Have you seen Gujuo, I am sure that he may have something",
							"for you as a token of our appreciation.");
					int other2 = multi(player, n, "I've killed Nezikchened the Demon again.", "Ok, thanks...");
					if (other2 == 0) {
						ungaduluTalkToDialogue(player, n, Ungadulu.KILLED_DEMON_AGAIN);
					} else if (other2 == 1) {
						ungaduluTalkToDialogue(player, n, Ungadulu.OK_THANKS);
					}
					break;
			}
		}
	}

	private static void evilUngadulu(Player player, Npc n) {
		npcsay(player, n, "Ha Ha ha Vacu...now you will be my pawn...");
		mes(n, "The Shaman starts an incantation...");
		delay(2);
		npcsay(player, n, "Iles Resti Yam Darkus Spiritus Possesi Yanai..");
		mes(n, "You feel a strange power coming over you...");
		delay(2);
		player.damage(5);
		player.getSkills().setLevel(Skill.ATTACK.id(), player.getSkills().getLevel(Skill.ATTACK.id()) - 5);
		player.getSkills().setLevel(Skill.DEFENSE.id(), player.getSkills().getLevel(Skill.DEFENSE.id()) - 5);
		player.getSkills().setLevel(Skill.STRENGTH.id(), player.getSkills().getLevel(Skill.STRENGTH.id()) - 5);
		mes(n, "The Shaman seems to get stronger...");
		delay(2);
		mes(n, "The Shaman seems to return to normal...");
		delay(2);
		n = changenpc(n, NpcId.UNGADULU.id(), true);
		npcsay(player, n, "Run, run away...",
			"Run like the leapard bwana...");
	}

	public static void ungaduluWallDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.UNGADULU.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 2:
					case 3:
						player.message("You see a white robed figure gesturing to you.");
						npcsay(player, n, "Please come no closer...the flames will incinerate you.");
						int menu = multi(player, n,
							"How can I extinguish the flames?",
							"Who are you?");
						if (menu == 0) {
							ungaduluWallDialogue(player, n, Ungadulu.EXTINGUISH_THE_FLAMES);
						} else if (menu == 1) {
							ungaduluWallDialogue(player, n, Ungadulu.WHO_ARE_YOU);
						}
						break;
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
					case -1:
						ungaduluTalkToDialogue(player, n, -1);
						break;
				}
			}
			switch (cID) {
				case Ungadulu.EXTINGUISH_THE_FLAMES:
					npcsay(player, n, "Please don't try to extinguish...");
					n = changenpc(n, NpcId.EVIL_UNGADULU.id(), true);
					npcsay(player, n, "Yes, douse the flames with water, pure water...foo...");
					delay();
					n = changenpc(n, NpcId.UNGADULU.id(), true);
					npcsay(player, n, "Please, leave now...don't listen to me...",
						"I beg you,leave now, don't touch the flames...");
					int opt = multi(player, n,
						"Where do I get pure water from ?",
						"Who are you?");
					if (opt == 0) {
						ungaduluWallDialogue(player, n, Ungadulu.WHERE_DO_I_GET_PURE_WATER_FROM);
					} else if (opt == 1) {
						ungaduluWallDialogue(player, n, Ungadulu.WHO_ARE_YOU);
					}
					break;
				case Ungadulu.WHO_ARE_YOU:
					npcsay(player, n, "I am Ungadulu,trapped here many years now...",
						"Leave these caves and save yourself...");
					n = changenpc(n, NpcId.EVIL_UNGADULU.id(), true);
					npcsay(player, n, "Wait...get pure water from the pool...above lands...");
					delay();
					n = changenpc(n, NpcId.UNGADULU.id(), true);
					npcsay(player, n, "Please Bwana, don't listen to me...run, save yourself...");
					int menu = multi(player, n,
						"How can I extinguish the flames?",
						"Where do I get pure water from ?");
					if (menu == 0) {
						ungaduluWallDialogue(player, n, Ungadulu.EXTINGUISH_THE_FLAMES);
					} else if (menu == 1) {
						ungaduluWallDialogue(player, n, Ungadulu.WHERE_DO_I_GET_PURE_WATER_FROM);
					}
					break;
				case Ungadulu.WHERE_DO_I_GET_PURE_WATER_FROM:
					npcsay(player, n, "Please, leave now...");
					n = changenpc(n, NpcId.EVIL_UNGADULU.id(), true);
					npcsay(player, n, "...from the above lands...hurry and release me...");
					n = changenpc(n, NpcId.UNGADULU.id(), true);
					npcsay(player, n, "Leave here, please, go...now...");
					n = changenpc(n, NpcId.EVIL_UNGADULU.id(), true);
					npcsay(player, n, "Hurry, Vacu, the heat kills me...ha ha ha");
					n = changenpc(n, NpcId.UNGADULU.id(), true);
					player.message("The Shaman throws himself down on the floor and starts shaking.");
					if (player.getQuestStage(Quests.LEGENDS_QUEST) == 2) {
						player.updateQuestStage(Quests.LEGENDS_QUEST, 3);
					}
					break;
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.UNGADULU.id() || n.getID() == NpcId.EVIL_UNGADULU.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.UNGADULU.id()) {
			ungaduluTalkToDialogue(player, n, -1);
		}
		else if (n.getID() == NpcId.EVIL_UNGADULU.id()) {
			evilUngadulu(player, n);
		}
	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		return n.getID() == NpcId.UNGADULU.id() || n.getID() == NpcId.EVIL_UNGADULU.id();
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.UNGADULU.id()) {
			player.message("You feel a strange force coming over you...");
			player.message("You feel weakened....");
			player.getSkills().setLevel(Skill.ATTACK.id(), 0);
			player.getSkills().setLevel(Skill.STRENGTH.id(), 0);
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 9 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				mes("The Shaman casts a debilitating spell on you..");
				delay(2);
				mes("You're sent reeling backwards through the flames..");
				delay(2);
				player.teleport(454, 3702);
				player.damage(5);
				npcsay(player, affectedmob, "Think twice in future before attacking me..");
				say(player, affectedmob, "Ughhh!");
				return;
			}
			player.startCombat(affectedmob);
		}
		else if (affectedmob.getID() == NpcId.EVIL_UNGADULU.id()) {
			player.message("A strange power stops you from attacking the Shaman.");
			evilUngadulu(player, affectedmob);
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player player, Npc n) {
		return n.getID() == NpcId.UNGADULU.id() || n.getID() == NpcId.EVIL_UNGADULU.id();
	}

	@Override
	public void onPlayerRangeNpc(Player player, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.UNGADULU.id()) {
			player.message("You feel a strange force coming over you...");
			player.message("You feel weakened....");
			player.getSkills().setLevel(Skill.ATTACK.id(), 0);
			player.getSkills().setLevel(Skill.STRENGTH.id(), 0);
			if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 9 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
				mes("The Shaman casts a debilitating spell on you..");
				delay(2);
				mes("You're sent reeling backwards through the flames..");
				delay(2);
				player.teleport(454, 3702);
				player.damage(5);
				npcsay(player, affectedmob, "Think twice in future before attacking me..");
				say(player, affectedmob, "Ughhh!");
				return;
			}
			player.startCombat(affectedmob);
		}
		else if (affectedmob.getID() == NpcId.EVIL_UNGADULU.id()) {
			player.message("A strange power stops you from attacking the Shaman.");
			evilUngadulu(player, affectedmob);
		}
	}

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.UNGADULU.id() || n.getID() == NpcId.EVIL_UNGADULU.id();
	}

	@Override
	public void onSpellNpc(Player player, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.UNGADULU.id()) {
			player.message("You feel a strange force coming over you...");
			player.message("You feel weakened....");
			player.getSkills().setLevel(Skill.ATTACK.id(), 0);
			player.getSkills().setLevel(Skill.STRENGTH.id(), 0);
			player.message("The spell fizzles and dies...");
			player.message("Some sort of magical effect seems to be protecting the Shaman.");
			return;
		}
		else if (affectedmob.getID() == NpcId.EVIL_UNGADULU.id()) {
			player.message("A strange power stops you from attacking the Shaman.");
			evilUngadulu(player, affectedmob);
		}
	}

	@Override
	public boolean blockEscapeNpc(Player player, Npc n) {
		return n.getID() == NpcId.UNGADULU.id();
	}

	@Override
	public void onEscapeNpc(Player player, Npc n) {
		if (n.getID() == NpcId.UNGADULU.id()) {
			n.resetCombatEvent();
			npcWalkFromPlayer(player, n);
			delay();
			npcsay(player, n, "Run then....run away....",
				"Save yourself....");
			player.getSkills().setLevel(Skill.ATTACK.id(), (player.getSkills().getMaxStat(Skill.ATTACK.id()) - 19) + player.getSkills().getLevel(Skill.ATTACK.id()));
			player.getSkills().setLevel(Skill.STRENGTH.id(), (player.getSkills().getMaxStat(Skill.STRENGTH.id()) - 19) + player.getSkills().getLevel(Skill.STRENGTH.id()));
			player.message("Strangely, you start to feel better.");
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.UNGADULU.id() && (item.getCatalogId() == ItemId.BOOKING_OF_BINDING.id()
				|| item.getCatalogId() == ItemId.GLOWING_DARK_DAGGER.id() || item.getCatalogId() == ItemId.DARK_DAGGER.id());
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.UNGADULU.id() && item.getCatalogId() == ItemId.DARK_DAGGER.id()) { // NOT KILLED VIEYLDY - dark dagger
			mes(npc, "You hand the dagger over to the Shaman.");
			delay(2);
			mes(npc, "The Shaman's face turns pale...");
			delay(2);
			if (player.getCache().hasKey("killed_viyeldi")) {
				npcsay(player, npc, "Oh dear Bwana, I sense something terrible has happened.",
					"This dagger is a portent of some evil action...",
					"Please, reveal to me anything that you have done",
					"so that I might understand this better.");
				int killed = multi(player, npc,
					"I've killed Viyeldi.",
					"Er, I can't think of anything.");
				if (killed == 0) {
					npcsay(player, npc, "Poor Viyeldi',",
						"He was the guardian of the dead hero's that protected the source.",
						"Their tormented spirits will now be at the beck and",
						"call of the one who gave you the dagger.");
					if (player.getCarriedItems().hasCatalogID(ItemId.HOLY_FORCE_SPELL.id(), Optional.of(false))) {
						npcsay(player, npc, "Take the Holy Force spell I gave you and pray that you",
							"can defeat this spirit before it's too late.");
					} else {
						give(player, ItemId.HOLY_FORCE_SPELL.id(), 1);
						mes(npc, "The wizened old Shaman hands over a piece of paper.");
						delay(2);
						npcsay(player, npc, "Take this spell and pray that you can defeat",
							"this evil spirit before it's too late.",
							"The spell will force the spirit to reveal its true self.",
							"And it will also be vulerable to normal attacks.");
					}
				} else if (killed == 1) {
					npcsay(player, npc, "Well, that is strange...",
						"I sense a growing evil power since you visited the caves.");
					player.message("The Wizened old Shaman mutters to himself and wanders off.");
				}
				return;
			}
			npcsay(player, npc, "This dagger has been made for one purpose only...",
				"Praise the gods that you brought it to me.",
				"I can make you a spell with this item which will force the spirit",
				"to reveal its true self.",
				"Once activated, you will be able to attack it like",
				"a normal creature.");
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			give(player, ItemId.HOLY_FORCE_SPELL.id(), 1);
			mes(npc, "The Shaman takes the dagger and gives you a folded piece of paper.");
			delay(2);
			npcsay(player, npc, "Use this spell on the Spirit.",
				"It will force the spirit to show it's true self.",
				"And it will also be vulerable to normal attacks.");
		}
		else if (npc.getID() == NpcId.UNGADULU.id() && item.getCatalogId() == ItemId.GLOWING_DARK_DAGGER.id()) { // KILLED VIYELDI - glowing dark dagger
			mes(npc, "You hand the dagger over to the Shaman.");
			delay(2);
			mes(npc, "The Shaman's face turns pale...");
			delay(2);
			npcsay(player, npc, "Oh dear Bwana.",
				"Poor Viyeldi's spirit is trapped inside this weapon.",
				"No doubt the evil spirit that told you to kill Viyeldi,",
				"is planning to use it for some vile purpose.",
				"I will try to release Viyeldi's spirit from the dagger.",
				"Here, you take this spell...");
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			give(player, ItemId.HOLY_FORCE_SPELL.id(), 1);
			mes(npc, "The Shaman takes the dagger and gives you a folded piece of paper.");
			delay(2);
			npcsay(player, npc, "Use this spell on the Spirit.",
				"It will force the spirit to show it's true self.",
				"And it will also be vulerable to normal attacks.");
		}
		else if (npc.getID() == NpcId.UNGADULU.id() && item.getCatalogId() == ItemId.BOOKING_OF_BINDING.id()) {
			if (player.getQuestStage(Quests.LEGENDS_QUEST) == 3) {
				mes(npc, "You open the book of binding in front of Ungadulu.");
				delay(3);
				mes(npc, "A blinding light fills the room...");
				delay(2);
				mes(npc, "A supernatural light falls on Ungadulu...");
				delay(2);
				mes(npc, "And a mighty demon forms in front of you...");
				delay(2);
				Npc nez = addnpc(NpcId.NEZIKCHENED.id(), npc.getX(), npc.getY(), (int)TimeUnit.SECONDS.toMillis(500), player);
				if (nez != null) {
					npcsay(player, nez, "Curse you foul intruder...your faith will help you little here.");
					nez.startCombat(player);
					player.getSkills().setLevel(Skill.PRAYER.id(), (int) Math.ceil((double) player.getSkills().getLevel(Skill.PRAYER.id()) / 4));
					mes("A sense of hopelessness fills your body...");
					delay(2);
					npcsay(player, nez, "'Ere near to death ye comes now that ye has meddled in my dealings..");
					if (player.getCache().hasKey("holy_water_neiz")) {
						player.message("The holy water starts smoking on the Demons skin...");
						npcsay(player, nez, "Ahhhrhhhhhghhhh...it burns.....");
						// silverlight effect may also be present
						for (int i = 0; i < 3; i++) {
							int currentStat = npc.getSkills().getLevel(i);
							int newStat = currentStat - (int) (currentStat * 0.15);
							npc.getSkills().setLevel(i, newStat);
						}
					}
				}
			} else {
				npcsay(player, npc, "Ha, ha ha! There's no need to use that on me any more...",
					"I'm cured now, remember...");
			}
		}
	}

	class Ungadulu {
		static final int EXTINGUISH_THE_FLAMES = 0;
		static final int WHO_ARE_YOU = 1;
		static final int WHERE_DO_I_GET_PURE_WATER_FROM = 2;
		static final int HOW_DO_I_GET_OUT_OF_HERE = 3;
		static final int WHAT_WILL_YOU_DO_NOW = 4;
		static final int COLLECT_SOME_YOMMI_SEEDS_FOR_GUJUO = 5;
		static final int HOW_DO_I_GROW_THE_YOMMI_TREE = 6;
		static final int WHAT_DO_YOU_KNOW_ABOUT_THE_PURE_WATER = 7;
		static final int WHERE_DO_I_PLANT_THE_SEEDS = 8;
		static final int I_HAVE_GERMINATED_THE_SEEDS = 9;
		static final int I_NEED_MORE_YOMMI_TREE_SEEDS = 10;
		static final int WHERE_CAN_I_GET_MORE_PURE_WATER = 11;
		static final int THE_MAGIC_POOL_HAS_DRIED_UP = 12;
		static final int I_AM_ON_A_QUEST_TO_GET_MORE_PURE_WATER = 13;
		static final int WHAT_DO_YOU_KNOW_ABOUT_THE_SOURCE_OF_THE_SACRED_WATER = 14;

		static final int I_HAVE_KILLED_VIYELDI = 15;
		static final int I_MET_A_SPIRIT_IN_THE_VIYELDI_CAVES = 16;
		static final int THE_SPIRIT_TOLD_ME_TO_KILL_VIYELDI = 17;
		static final int DO_YOU_KNOW_ANYTHING_ABOUT_DAGGERS = 18;
		static final int I_HAVE_KILLED_THE_SPIRIT = 19;
		static final int I_HAVE_GOT_THE_WATER = 20;
		static final int WHAT_DO_I_DO_NOW = 21;
		static final int OK_THANKS = 22;
		static final int MADE_TOTEM_POLE = 23;
		static final int WHAT_DO_TOTEM_POLE = 24;
		static final int KILLED_DEMON_AGAIN = 25;
		static final int REPLACED_EVIL_TOTEM = 26;

	}
}
