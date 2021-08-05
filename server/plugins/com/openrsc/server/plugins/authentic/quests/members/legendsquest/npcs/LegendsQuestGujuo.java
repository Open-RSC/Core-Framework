package com.openrsc.server.plugins.authentic.quests.members.legendsquest.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestGujuo implements TalkNpcTrigger, UseNpcTrigger {

	private void GujuoDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.GUJUO.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(Quests.LEGENDS_QUEST)) {
					case 1:
						npcsay(player, n, "Grettings Bwana...",
							"Why do you make such strange sounds and disturb the peace of the jungle?");
						int menu = multi(player, n,
							"I was hoping to attract the attention of a native.",
							"Sorry, it was a mistake?");
						if (menu == 0) {
							npcsay(player, n, "Well, it had the desired effect...",
								"I am Gujuo, proud member of the Kharazi tribe.",
								"What did you want to talk about Bwana ?");
							int opt4 = multi(player, n,
								"I want to develop friendly relations with your people.",
								"Sorry, it was a mistake?");
							if (opt4 == 0) {
								GujuoDialogue(player, n, Gujuo.I_WANT_TO_DEVELOP_FRIENDLY_RELATIONS);
							} else if (opt4 == 1) {
								GujuoDialogue(player, n, Gujuo.SORRY_IT_WAS_A_MISTAKE);
							}
						} else if (menu == 1) {
							GujuoDialogue(player, n, Gujuo.SORRY_IT_WAS_A_MISTAKE);
						}
						break;
					case 2: // DISCOVER CAVES
						npcsay(player, n, "How goes your quest to release Ungadulu Bwana?");
						int menuCave = multi(player, n, "I've found the caves, but I don't know what to do.",
								"Ok thanks for your help.");
						if (menuCave == 0) {
							npcsay(player, n, "Search the caves and try to talk to Ungadulu, there may be some",
									"clues to be had by searching all the items in the cave...");
							gujuoBye(player, n);
						} else if (menuCave == 1) {
							GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
						}
						break;
					case 3:
						if (player.getCarriedItems().hasCatalogID(ItemId.GOLDEN_BOWL.id(), Optional.of(false))
							|| player.getCarriedItems().hasCatalogID(ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id(), Optional.of(false))
							|| player.getCarriedItems().hasCatalogID(ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id(), Optional.of(false))) { // GOLDEN BOWL
							npcsay(player, n, "Greetings Bwana.",
								"Ah I see you have the golden bowl !",
								"Would like me to show you how to bless it?");
							int bowl = multi(player,
								"Yes, I'd like you to bless my gold bowl.",
								"No thanks, I need help with something else.");
							if (bowl == 0) {
								GujuoDialogue(player, n, Gujuo.BLESS_THE_BOWL);
							} else if (bowl == 1) {
								GujuoDialogue(player, n, Gujuo.HOW_GOES_YOUR_QUEST_TO_RELEASE_UNGADULU);
							}
						} else if (player.getCarriedItems().hasCatalogID(ItemId.BLESSED_GOLDEN_BOWL.id(), Optional.of(false))
							|| player.getCarriedItems().hasCatalogID(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(), Optional.of(false))
							|| player.getCarriedItems().hasCatalogID(ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id(), Optional.of(false))) {
							npcsay(player, n, "How goes your quest to release Ungadulu Bwana?");
							int releaseopt = multi(player, n, "Ungadulu looks strange.",
								"I need to douse some flames with pure water.",
								"I'm not sure what to do?",
								"Can you help me?");
							if (releaseopt == 0) {
								GujuoDialogue(player, n, Gujuo.UNGADULU_LOOKS_STRANGE);
							} else if (releaseopt == 1) {
								GujuoDialogue(player, n, Gujuo.I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER);
							} else if (releaseopt == 2) {
								GujuoDialogue(player, n, Gujuo.IM_NOT_SURE_WHAT_TO_DO);
							} else if (releaseopt == 3) {
								GujuoDialogue(player, n, Gujuo.CAN_YOU_HELP_ME);
							}
						} else {
							GujuoDialogue(player, n, Gujuo.HOW_GOES_YOUR_QUEST_TO_RELEASE_UNGADULU);
						}
						break;
					case 4:
						npcsay(player, n, "How goes your Quest to release Ungadulu?");
						int opt16 = multi(player, n,
							"Ungadulu is free, he was possesed by a demon and I killed it.",
							"I have the Yommi tree seeds.",
							"What do I do now?");
						if (opt16 == 0) {
							GujuoDialogue(player, n, Gujuo.UNGADULU_IS_FREE);
						} else if (opt16 == 1) {
							GujuoDialogue(player, n, Gujuo.I_HAVE_THE_YOMMI_TREE_SEEDS);
						} else if (opt16 == 2) {
							GujuoDialogue(player, n, Gujuo.WHAT_DO_I_DO_NOW);
						}
						break;
					case 5:
						npcsay(player, n, "Congratulations on releasing Ungadulu! My people are very pleased...",
							"How goes the growing of the Yommi tree?");
						int newMenu1 = multi(player, n,
							"I have germinated the Yommi tree seeds.",
							"Where is the fertile soil.",
							"Ok thanks for your help.");
						if (newMenu1 == 0) {
							GujuoDialogue(player, n, Gujuo.I_HAVE_GERMINATED_THE_YOMMI_TREE_SEEDS);
						} else if (newMenu1 == 1) {
							GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_FETILE_SOIL);
						} else if (newMenu1 == 2) {
							GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
						}
						break;
					case 6:
						npcsay(player, n, "I have visited Ungadulu in the caves, he is hard at work studying.",
							"He looks well!",
							"Have you grown the Yommi tree yet?");
						int opt20 = multi(player, n, false, //do not send over
							"The water pool has dried up and I need more water.",
							"The Yommi tree died");
						if (opt20 == 0) {
							say(player, n, "The water pool has dried up and I need more pure water.");
							GujuoDialogue(player, n, Gujuo.THE_WATER_POOL_HAS_DRIED_UP_AND_I_NEED_MORE_WATER);
						} else if (opt20 == 1) {
							say(player, n, "The Yommi tree died");
							GujuoDialogue(player, n, Gujuo.THE_YOMMI_TREE_DIED);
						}
						break;
					case 7:
						npcsay(player, n, "I have visited Ungadulu in the caves, he is hard at work studying..",
							"He looks well!",
							"How is your quest Bwana ?");
						// TODO: verify if is the deepest part of or just by going down the rope
						if (player.getCache().hasKey("cavernous_opening")) {
							int opt25a = multi(player, n,
								"I have found a way into the caves !",
								"Where is the source of the spring of pure water ?",
								"I searched the catacombs thoroughly but found nothing else.",
								"If I went in search of the source, could you help me?",
								"Ok thanks for your help.");
							if (opt25a == 0) {
								GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_GET_MORE_WATER_FOR_THE_YOMMI_TREE);
							} else if (opt25a == 1) {
								GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
							} else if (opt25a == 2) {
								GujuoDialogue(player, n, Gujuo.I_SEARCHED_THE_CATACOMBS_THOROUGHLY_BUT_FOUND_NADA_NIET);
							} else if (opt25a == 3) {
								GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
							} else if (opt25a == 4) {
								GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
							}
						} else {
							int opt25 = multi(player, n,
								"Where can I get more water for the Yommi tree?",
								"Where is the source of the spring of pure water ?",
								"I searched the catacombs thoroughly but found nothing else.",
								"If I went in search of the source, could you help me?",
								"Ok thanks for your help.");
							if (opt25 == 0) {
								GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_GET_MORE_WATER_FOR_THE_YOMMI_TREE);
							} else if (opt25 == 1) {
								GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
							} else if (opt25 == 2) {
								GujuoDialogue(player, n, Gujuo.I_SEARCHED_THE_CATACOMBS_THOROUGHLY_BUT_FOUND_NADA_NIET);
							} else if (opt25 == 3) {
								GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
							} else if (opt25 == 4) {
								GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
							}
						}
						break;
					case 8:
					case 9:
						npcsay(player, n, "Hello Bwana, I am very pleased to see you again.",
							"Things seem much happier now in the Kharazi Jungle.",
							"I suspect that it is down to your good doings !");
						int a_menu = multi(player, n,
							"I found the source of the spring and I got the water.",
							"I killed the demon again.",
							"How do I make the totem pole?",
							"Ok thanks for your help.");
						if (a_menu == 0) {
							GujuoDialogue(player, n, Gujuo.I_FOUND_THE_SOURCE_OF_THE_SPRING_AND_I_GOT_THE_WATER);
						} else if (a_menu == 1) {
							GujuoDialogue(player, n, Gujuo.I_KILLED_THE_DEMON_AGAIN);
						} else if (a_menu == 2) {
							GujuoDialogue(player, n, Gujuo.HOW_DO_I_MAKE_THE_TOTEM_POLE);
						} else if (a_menu == 3) {
							GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
						}
						break;
					case 10:
					case 11:
					case -1:
						if (!player.getCache().hasKey("rewarded_totem") && player.getQuestStage(Quests.LEGENDS_QUEST) >= 10) {
							n.resetPath();
							delay();
							npcWalkFromPlayer(player, n);
							npcsay(player, n, "Greetins Bwana,",
								"We witnessed your fight with the Demon from some distance away.",
								"My people are so pleased with your heroic efforts.",
								"Your strength and ability as a warrior are Legendary.");
							mes(n, "Gujuo offers you an awe inspiring jungle crafted Totem Pole.");
							delay(2);
							give(player, ItemId.GILDED_TOTEM_POLE.id(), 1);
							player.getCache().store("rewarded_totem", true);
							npcsay(player, n, "Please accept this as a token of our appreciation.",
								"Please, now consider yourself a friend of my people.",
								"And visit us anytime.");
							if (player.getCarriedItems().hasCatalogID(ItemId.GERMINATED_YOMMI_TREE_SEED.id(), Optional.of(false))) {
								player.getCarriedItems().remove(
									new Item(
										ItemId.GERMINATED_YOMMI_TREE_SEED.id(),
										player.getCarriedItems().getInventory().countId(ItemId.GERMINATED_YOMMI_TREE_SEED.id())
									));
								npcsay(player, n, "I'll take those Germinated Yommi tree seeds to Ungadulu,",
									"I'm sure he'll apreciate them.");
							}
							gujuoBye(player, n);
							return;
						}
						npcsay(player, n, "Good day Bwana.");
						npcsay(player, n, DataConversions.getRandom().nextBoolean() ? "The Kharazi jungle is especially beautifull today isn't it?"
							: "The jungle is especially beatifull today isn't it?");
						npcsay(player, n, "My village people pass on their thanks to you.");
						String[] menuOpts;
						if (player.getCarriedItems().hasCatalogID(ItemId.GILDED_TOTEM_POLE.id(), Optional.of(false)) || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
							menuOpts = new String[]{ "Do you have any news?",
									"Where are all your people.",
									"Ok thanks for your help."};
						} else {
							menuOpts = new String[]{ "Do you have any news?",
									"Where are all your people.",
									"I've lost the tribal gift you gave me.",
									"Ok thanks for your help."};
						}

						int last = multi(player, n, menuOpts);
						if (last == 0) {
							npcsay(player, n, "Just that everything is fine in the jungle with us.",
									"And that we are gratefull to you for your help.");
						} else if (last == 1) {
							npcsay(player, n, "My people are all happy living in the jungle.",
									"They are still afraid of strangers and will not approach",
									"But they are around, none the less.",
									"Your story has been woven into the fabric of our society.",
									"And we all sing your many praises Bwana.");
						} else if (last == 2 || last == 3) {
							if (last == 2 && !player.getCarriedItems().hasCatalogID(ItemId.GILDED_TOTEM_POLE.id(), Optional.of(false)) && menuOpts.length == 4) {
								npcsay(player, n, "Well, that wasn't very nice of you.",
										"It took us a long time to make that Totem pole.",
										"Luckily, I made another one at the same time.");
								player.message("Gujuo hands over another totem pole.");
								give(player, ItemId.GILDED_TOTEM_POLE.id(), 1);
							} else {
								GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
							}
						}
						break;
				}
			}
			switch (cID) {
				case Gujuo.WHO_IS_VIYELDI:
					// TODO: check message times
					mes(n, "Gujuo scratches his head for a moment.");
					delay(2);
					npcsay(player, n, "Well, I have heard that name before, perhaps from the eldars.");
					mes(n, "Gujuo suddenly has an inspiration.");
					delay(2);
					npcsay(player, n, "Ah, yes, I think that is the name of the wizard who first",
						"went in search of the source.",
						"Be wary of him Bwana, he may try to trick you.");
					int viymenu = multi(player, n,
						"I have found a way into the caves !",
						"Do you know anything more about the caves?",
						"Where is the source of the spring of pure water ?",
						"Ok thanks for your help.");
					if (viymenu == 0) {
						GujuoDialogue(player, n, Gujuo.I_FOUND_WAY_INTO_CAVES);
					} else if (viymenu == 1) {
						GujuoDialogue(player, n, Gujuo.DO_YOU_KNOW_MORE_ABOUT_CAVES);
					} else if (viymenu == 2) {
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
					} else if (viymenu == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.DO_YOU_KNOW_MORE_ABOUT_CAVES:
					npcsay(player, n, "I am sorry to say that I don't Bwana.",
						"You will need to explore that area,",
						"but use your wits, and you may be lucky.");
					int submenu = multi(player, n,
						"I have found a way into the caves !",
						"Who is Viyeldi?",
						"Where is the source of the spring of pure water ?",
						"Ok thanks for your help.");
					if (submenu == 0) {
						GujuoDialogue(player, n, Gujuo.I_FOUND_WAY_INTO_CAVES);
					} else if (submenu == 1) {
						GujuoDialogue(player, n, Gujuo.WHO_IS_VIYELDI);
					} else if (submenu == 2) {
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
					} else if (submenu == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.I_FOUND_WAY_INTO_CAVES:
					npcsay(player, n, "That's great Bwana, good luck with your quest...",
						"and take care!");
					int aa_menu = multi(player, n,
						"Do you know anything more about the caves?",
						"Who is Viyeldi?",
						"Where is the source of the spring of pure water ?",
						"Ok thanks for your help.");
					if (aa_menu == 0) {
						GujuoDialogue(player, n, Gujuo.DO_YOU_KNOW_MORE_ABOUT_CAVES);
					} else if (aa_menu == 1) {
						GujuoDialogue(player, n, Gujuo.WHO_IS_VIYELDI);
					} else if (aa_menu == 2) {
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
					} else if (aa_menu == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.I_FOUND_THE_SOURCE_OF_THE_SPRING_AND_I_GOT_THE_WATER:
					npcsay(player, n, "Great Bwana, you are truly a brave warrior.",
						"Now you can try to grow the Yommi tree in earnest and make the totem pole.");
					int b_menu = multi(player, n,
						"I found the source of the spring and I got the water.",
						"I killed the demon again.",
						"How do I make the totem pole?",
						"Ok thanks for your help.");
					if (b_menu == 0) {
						GujuoDialogue(player, n, Gujuo.I_FOUND_THE_SOURCE_OF_THE_SPRING_AND_I_GOT_THE_WATER);
					} else if (b_menu == 1) {
						GujuoDialogue(player, n, Gujuo.I_KILLED_THE_DEMON_AGAIN);
					} else if (b_menu == 2) {
						GujuoDialogue(player, n, Gujuo.HOW_DO_I_MAKE_THE_TOTEM_POLE);
					} else if (b_menu == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.I_KILLED_THE_DEMON_AGAIN:
					npcsay(player, n, "You are indeed very brave Bwana,",
						"We have  noticed a difference in the Kharazi jungle,",
						"The tree's seem to sing again.",
						"And we have you to thank for it.");
					int c_menu = multi(player, n,
						"I found the source of the spring and I got the water.",
						"I killed the demon again.",
						"How do I make the totem pole?",
						"Ok thanks for your help.");
					if (c_menu == 0) {
						GujuoDialogue(player, n, Gujuo.I_FOUND_THE_SOURCE_OF_THE_SPRING_AND_I_GOT_THE_WATER);
					} else if (c_menu == 1) {
						GujuoDialogue(player, n, Gujuo.I_KILLED_THE_DEMON_AGAIN);
					} else if (c_menu == 2) {
						GujuoDialogue(player, n, Gujuo.HOW_DO_I_MAKE_THE_TOTEM_POLE);
					} else if (c_menu == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.HOW_DO_I_MAKE_THE_TOTEM_POLE:
					npcsay(player, n, "You will need to grow the Yommi tree to full height.",
						"And then, before it rots. you must chop it down.",
						"Once you have felled the tree, you need to trim the branches.",
						"And finally, you need to craft the totem pole out of the trunk.",
						"You'll need a very sharp, very tough axe to do all this.",
						"But once you have completed the totem pole.",
						"You will need to use it to replace a totem pole that already exists.",
						"As they're all placed on sacred areas to my people.");
					int a_menu = multi(player, n,
						"I found the source of the spring and I got the water.",
						"I killed the demon again.",
						"How do I make the totem pole?",
						"Ok thanks for your help.");
					if (a_menu == 0) {
						GujuoDialogue(player, n, Gujuo.I_FOUND_THE_SOURCE_OF_THE_SPRING_AND_I_GOT_THE_WATER);
					} else if (a_menu == 1) {
						GujuoDialogue(player, n, Gujuo.I_KILLED_THE_DEMON_AGAIN);
					} else if (a_menu == 2) {
						GujuoDialogue(player, n, Gujuo.HOW_DO_I_MAKE_THE_TOTEM_POLE);
					} else if (a_menu == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.WILL_I_NEED_THIS_POTION_I_FEEL_BRAVE_AS_I_AM:
					npcsay(player, n, "I would urge you to take it, Bwana, ");
					if (player.getCache().hasKey("gujuo_potion")) {
						npcsay(player, n, "I have heard that the caves are protected by supernatural ");
					} else {
						npcsay(player, n, "I have heard that the caves are protected by a supernatural ");
					}
					npcsay(player, n, "fear that renders even the bravest man to a trembling wreck.",
						"You will need all your wits about you when dealing with",
						"the terrors that exist down there. ");
					if (!player.getCache().hasKey("gujuo_potion")) {
						player.getCache().store("gujuo_potion", true);
					}
					int opt33 = multi(player, n,
						"Where can I find Snake weed?",
						"Where is the source of the spring of pure water ?",
						"Where can I find ardrigal.",
						"Ok thanks for your help.");
					if (opt33 == 0) {
						GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_FIND_SNAKE_WEED);
					} else if (opt33 == 1) {
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
					} else if (opt33 == 2) {
						GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_FIND_ARDRIGAL);
					} else if (opt33 == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.WHERE_CAN_I_FIND_ARDRIGAL:
					int opt32;
					npcsay(player, n, "Ardrigal is often found growing near to large groups of palms.",
						"Such a collection exists in the North. If you head east out of",
						"Tai Bwo Wannai village you should come across them.",
						"The herb grows in the shade of the palm so check carefully.");
					if (player.getCache().hasKey("gujuo_potion")) {
						opt32 = multi(player, n,
							"Where can I find Snake weed ?",
							"Where is the source of the spring of pure water ?",
							"If I went, could you help me ?",
							"Will I need this potion? I feel brave enough as I am.",
							"Ok thanks for your help.");
						if (opt32 == 0) {
							GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_FIND_SNAKE_WEED);
						} else if (opt32 == 1) {
							GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
						} else if (opt32 == 2) {
							GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
						} else if (opt32 == 3) {
							GujuoDialogue(player, n, Gujuo.WILL_I_NEED_THIS_POTION_I_FEEL_BRAVE_AS_I_AM);
						} else if (opt32 == 4) {
							GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
						}
					} else {
						opt32 = multi(player, n, false, //do not send over
							"Where is the source of the spring of pure water ?",
							"Where can I find Snake weed?",
							"If I went in search of the source, could you help me?",
							"Ok thanks for your help.");
						if (opt32 == 0) {
							say(player, n, "Where is the source of the spring of pure water ?");
							GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
						} else if (opt32 == 1) {
							say(player, n, "Where can I find Snake weed ?");
							GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_FIND_SNAKE_WEED);
						} else if (opt32 == 2) {
							say(player, n, "If I went in search of the source, could you help me?");
							GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
						} else if (opt32 == 3) {
							say(player, n, "Ok thanks for your help.");
							GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
						}
					}
					break;
				case Gujuo.WHERE_CAN_I_FIND_SNAKE_WEED:
					int opt31;
					if (player.getCache().hasKey("gujuo_potion")) {
						npcsay(player, n, "Snake weed is usually found by swampy marshy areas.",
							"It is not very common and it may be quite difficult to find.",
							"There is some marsh to the south of Tai Bwo Wannai village,",
							"The herb grows near Jungle Vines, so check all around very carefully.");
						opt31 = multi(player, n,
							"Where can I find ardrigal.",
							"Where is the source of the spring of pure water ?",
							"If I went, could you help me ?",
							"Will I need this potion? I feel brave enough as I am.",
							"Ok thanks for your help.");
						if (opt31 == 0) {
							GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_FIND_ARDRIGAL);
						} else if (opt31 == 1) {
							GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
						} else if (opt31 == 2) {
							GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
						} else if (opt31 == 3) {
							GujuoDialogue(player, n, Gujuo.WILL_I_NEED_THIS_POTION_I_FEEL_BRAVE_AS_I_AM);
						} else if (opt31 == 4) {
							GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
						}
					} else {
						npcsay(player, n, "Snake weed is usually found in swampy marshy areas.",
							"It is not very common and it may be quite difficult to find.",
							"There is some marsh to the South of Tai Bwo Wannai village.",
							"Near to where the river becomes the sea.",
							"The herb grows near Jungle Vines, so check all around very carefully.");
						opt31 = multi(player, n,
							"Where is the source of the spring of pure water ?",
							"Where can I find ardrigal.",
							"If I went in search of the source, could you help me?",
							"Ok thanks for your help.");
						if (opt31 == 0) {
							GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
						} else if (opt31 == 1) {
							GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_FIND_ARDRIGAL);
						} else if (opt31 == 2) {
							GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
						} else if (opt31 == 3) {
							GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
						}
					}
					break;
				case Gujuo.I_SEARCHED_THE_CATACOMBS_THOROUGHLY_BUT_FOUND_NADA_NIET:
					npcsay(player, n, "Perhaps the location has been hidden or buried under a rubble?",
						"These stories were told to me as a child by the village elders.",
						"They were probably meant to frighten us away from the caves.",
						"It could all just be a myth !",
						"You should perhaps talk to Ungadulu, he may know something ?",
						"Perhaps there is another way to get to the source of the stream?",
						"But I am not sure where it is...");
					ArrayList<String> options = new ArrayList<>();
					options.add("Where is the source of the spring of pure water ?");
					if (player.getCache().hasKey("gujuo_potion")) {
						options.add("If I went in search of the source, could you help me?");
					} else {
						options.add("If I went, could you help me?");
					}
					options.add("Ok thanks for your help.");
					String[] finalOptions = new String[options.size()];
					int opt30 = multi(player, n, false, //do not send over
						options.toArray(finalOptions));
					if (opt30 == 0) {
						say(player, n, "Where is the source of the spring of pure water ?");
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
					} else if (opt30 == 1) {
						say(player, n, "If I went, could you help me?");
						GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
					} else if (opt30 == 2) {
						say(player, n, "Ok thanks for your help.");
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME:
					npcsay(player, n, "Well, if you are sure you want to go.",
						"I will assist as much as I can.",
						"You will need the bravery of the Jungle lion,",
						"if you are to go into that forbidden place.",
						"I can give you the recipe for a potion to help with that.");
					if (player.getCache().hasKey("gujuo_potion")) {
						npcsay(player, n, "You will need to find two herbs, Snake weed and ardrigal.",
							"Add them both to a vial of water,",
							"and you will walk with the bravery of the lion.");
					} else {
						npcsay(player, n, "You will need to find two herbs, Snake weed and Ardrigal.",
							"Add them both to a vial of water,",
							"and you will walk with the bravery of the Kharazi lion.");
					}
					int opt29 = multi(player, n, false, //do not send over
						"Where can I find Snake weed?",
						"Where is the source of the spring of pure water ?",
						"Where can I find ardrigal.",
						"Will I need this potion? I feel brave enough as I am.",
						"Ok thanks for your help.");
					if (opt29 == 0) {
						say(player, n, "Where can I find Snake weed ?");
						GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_FIND_SNAKE_WEED);
					} else if (opt29 == 1) {
						say(player, n, "Where is the source of the spring of pure water ?");
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
					} else if (opt29 == 2) {
						say(player, n, "Where can I find ardrigal.");
						GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_FIND_ARDRIGAL);
					} else if (opt29 == 3) {
						say(player, n, "Will I need this potion? I feel brave enough as I am.");
						GujuoDialogue(player, n, Gujuo.WILL_I_NEED_THIS_POTION_I_FEEL_BRAVE_AS_I_AM);
					} else if (opt29 == 4) {
						say(player, n, "Ok thanks for your help.");
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.OK_I_WONT_GO:
					npcsay(player, n, "I understand Bwana,",
							"It would be a waste of a perfectly good life.",
							"We will try to defeat the evil spirits in other ways ?",
							"But I am not sure how we will do that.");
					int optNotGo = multi(player, n, "If I went, could you help me?",
							"Ok thanks for your help.");
					if (optNotGo == 0) {
						GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
					} else if (optNotGo == 1) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.WHERE_CAN_I_GET_MORE_WATER_FOR_THE_YOMMI_TREE:
					npcsay(player, n, "If the pool of sacred water has dried up,",
						"there may be a way to get to the source of the spring.",
						"But it is said to be very, very dangerous.");
					int opt27 = multi(player, n,
						"Where is the source of the spring of pure water ?",
						"If I went in search of the source, could you help me?",
						"Ok thanks for your help.");
					if (opt27 == 0) {
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2);
					} else if (opt27 == 1) {
						GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
					} else if (opt27 == 2) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.THE_WATER_POOL_HAS_DRIED_UP_AND_I_NEED_MORE_WATER:
					npcsay(player, n, "This is indeed a bad omen Bwana, that pool is sacred to us...",
						"I have seen it and it is full of filth, it is not natural...",
						"I suspect that some evil is at work here.");
					int opt24 = multi(player, n, "Does the Yommi tree have to have pure water?",
						"Where is the source of the spring of pure water ?");
					if (opt24 == 0) {
						GujuoDialogue(player, n, Gujuo.DOES_THE_YOMMI_TREE_HAVE_TO_HAVE_PURE_WATER);
					} else if (opt24 == 1) {
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER);
					}
					break;
				case Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER:
					mes(n, "Gujuo looks very uncomfortable...");
					delay(2);
					npcsay(player, n, "I am not sure...",
						"But I have heard that deeper in the Catacombs where you found Ungadulu,",
						"deep underground,",
						"There is a terrible place guarded by the spirits of the undead.",
						"Since they died trying to find the source of the stream,",
						"They are cursed to guard it for all eternity.",
						"The first to seek the source was said to be a high level sorcerer.",
						"He created a powerfull spell in the caves,",
						"Now, all those who venture near are overcome by a supernatural fear...",
						"With all my heart Bwana, I would never go near such a place.");
					if (player.getQuestStage(Quests.LEGENDS_QUEST) == 6) {
						player.updateQuestStage(Quests.LEGENDS_QUEST, 7);
					}
					int opt23 = multi(player, n,
						"Ok, I won't go...",
						"If I went, could you help me?",
						"I searched the catacombs thoroughly but found nothing else..");
					if (opt23 == 0) {
						GujuoDialogue(player, n, Gujuo.OK_I_WONT_GO);
					} else if (opt23 == 1) {
						GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
					} else if (opt23 == 2) {
						GujuoDialogue(player, n, Gujuo.I_SEARCHED_THE_CATACOMBS_THOROUGHLY_BUT_FOUND_NADA_NIET);
					}
					break;
				case Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2:
					mes(n, "Gujuo looks very uncomfortable...");
					delay(2);
					npcsay(player, n, "I am not sure...",
						"But I have heard that deeper in the Catacombs where you found Ungadulu,",
						"deep underground,",
						"There is a terrible place guarded by the spirits of the undead.",
						"Since they died trying to find the source of the stream,",
						"They are cursed to guard it for all eternity.",
						"The first to seek the source was said to be a high level sorcerer.",
						"He created a powerfull spell in the caves,",
						"Now, all those who venture near are overcome by a supernatural fear...",
						"With all my heart Bwana, I would never go near such a place.");
					int opt26 = multi(player, n,
						"If I went in search of the source, could you help me?",
						"I searched the catacombs thoroughly but found nothing else..",
						"Ok thanks for your help.");
					if (opt26 == 0) {
						GujuoDialogue(player, n, Gujuo.IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME);
					} else if (opt26 == 1) {
						GujuoDialogue(player, n, Gujuo.I_SEARCHED_THE_CATACOMBS_THOROUGHLY_BUT_FOUND_NADA_NIET);
					} else if (opt26 == 2) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.DOES_THE_YOMMI_TREE_HAVE_TO_HAVE_PURE_WATER:
					npcsay(player, n, "Yes, it is a magical tree and can only survive on the water ",
						"from the sacred pool. This is indeed a tragedy...");
					int opt22 = multi(player, n,
						"Where is the source of the spring of pure water ?",
						"Ok thanks for your help.");
					if (opt22 == 0) {
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER);
					} else if (opt22 == 1) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.THE_YOMMI_TREE_DIED:
					npcsay(player, n, "Well, it requires pure sacred water for it to grow.",
						"It is a very special tree...");
					int opt21 = multi(player, n,
						"The sacred water pool has dried up and I need more water.",
						"Does the Yommi tree have to have pure water?");
					if (opt21 == 0) {
						GujuoDialogue(player, n, Gujuo.THE_WATER_POOL_HAS_DRIED_UP_AND_I_NEED_MORE_WATER);
					} else if (opt21 == 1) {
						GujuoDialogue(player, n, Gujuo.DOES_THE_YOMMI_TREE_HAVE_TO_HAVE_PURE_WATER);
					}
					break;
				case Gujuo.I_HAVE_GERMINATED_THE_YOMMI_TREE_SEEDS:
					npcsay(player, n, "Well done Bwana,",
						"With the blessings of the gods we will soon have our Totem Pole.",
						"Bwana, you now need to plant the seed in the fertile earth.");
					int newMenu3 = multi(player, n,
						"Where is the fertile soil.",
						"Ok thanks for your help.");
					if (newMenu3 == 0) {
						GujuoDialogue(player, n, Gujuo.WHERE_IS_THE_FETILE_SOIL);
					} else if (newMenu3 == 1) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.WHERE_IS_THE_FETILE_SOIL:
					npcsay(player, n, "You should be able to find many places where the ",
						"ground is fertile in the Kharazi Jungle.",
						"Planting the Yommi tree seeds in fertile soil gives it a good ",
						"chance to grow. My people are trying to grow the Yommi tree as well.",
						"But so far we have not met with any success.",
						"If you find a rotten tree or what looks like a rotten totem pole.",
						"You'll need to remove it yourself to get to the fertile soil.",
						"It will take a very sharp, robust axe to do it.");
					int newMenu2 = multi(player, n,
						"I have germinated the Yommi tree seeds.",
						"Ok thanks for your help.");
					if (newMenu2 == 0) {
						GujuoDialogue(player, n, Gujuo.I_HAVE_GERMINATED_THE_YOMMI_TREE_SEEDS);
					} else if (newMenu2 == 1) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.UNGADULU_IS_FREE:
					npcsay(player, n, "You are indeed brave Bwana, a truly fearsome warrior to take on ",
						"Such an enemy! Well Done!");
					int opt19 = multi(player, n,
						"I have the Yommi tree seeds.",
						"What do I do now?",
						"Ok thanks for your help.");
					if (opt19 == 0) {
						GujuoDialogue(player, n, Gujuo.I_HAVE_THE_YOMMI_TREE_SEEDS);
					} else if (opt19 == 1) {
						GujuoDialogue(player, n, Gujuo.WHAT_DO_I_DO_NOW);
					} else if (opt19 == 2) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.I_HAVE_THE_YOMMI_TREE_SEEDS:
					if (player.getCarriedItems().hasCatalogID(ItemId.YOMMI_TREE_SEED.id(), Optional.of(false))) {
						npcsay(player, n, "That's great Bwana. Now you just need to germinate ",
							"the seeds and then plant them in some fertile soil.",
							"I'm sure that Ungadulu has explained all this to you already.");
						int opt18 = multi(player, n,
							"Ungadulu is free, he was possesed by a demon and I killed it.",
							"What do I do now?",
							"Ok thanks for your help.");
						if (opt18 == 0) {
							GujuoDialogue(player, n, Gujuo.UNGADULU_IS_FREE);
						} else if (opt18 == 1) {
							GujuoDialogue(player, n, Gujuo.WHAT_DO_I_DO_NOW);
						} else if (opt18 == 2) {
							GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
						}
					} else {
						npcsay(player, n, "Hmmm, well I don't see them...",
							"Why not go and see Ungadulu and see if you can get some more.");
					}
					break;
				case Gujuo.WHAT_DO_I_DO_NOW:
					npcsay(player, n, "If you have the Yommi tree seeds, you will need to germinate them.",
						"You need to place the seeds into pure water.",
						"And they will begin to sprout tiny shoots...",
						"You can then plant them in fertile soil.");
					int opt17 = multi(player, n,
						"Ungadulu is free, he was possesed by a demon and I killed it.",
						"I have the Yommi tree seeds.",
						"Ok thanks for your help.");
					if (opt17 == 0) {
						GujuoDialogue(player, n, Gujuo.UNGADULU_IS_FREE);
					} else if (opt17 == 1) {
						GujuoDialogue(player, n, Gujuo.I_HAVE_THE_YOMMI_TREE_SEEDS);
					} else if (opt17 == 2) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.SORRY_IT_WAS_A_MISTAKE:
					npcsay(player, n, "Very good Bwana...however, it begs the question...",
						"What are you doing in the Kharazi jungle");
					int opt = multi(player, n,
						"I want to develop friendly relations with your people.",
						"I'm lost, can you show me the way out?");
					if (opt == 0) {
						GujuoDialogue(player, n, Gujuo.I_WANT_TO_DEVELOP_FRIENDLY_RELATIONS);
					} else if (opt == 1) {
						GujuoDialogue(player, n, Gujuo.IM_LOST);
					}
					break;
				case Gujuo.IM_LOST:
					npcsay(player, n, "Yes Bwana...",
						"I can take you to the edge of the Kharazi jungle.",
						"Would you like me to take you?");
					int opt2 = multi(player, n,
						"Yes Please...",
						"No thanks...");
					if (opt2 == 0) {
						npcsay(player, n, "Follow me...");
						mes("Gujuo takes you out of the jungle...");
						delay(2);
						player.teleport(397, 865);
						if (n != null) {
							n.teleport(398, 865);
							delay();
							npcsay(player, n, "");
						}
						mes("Gujuo disapears into the Kharazi jungle as swiftly as he appeared...");
						delay(3);
						if (n != null)
							n.remove();
					} else if (opt2 == 1) {
						GujuoDialogue(player, n, Gujuo.NO_THANKS);
					}
					break;
				case Gujuo.NO_THANKS:
					npcsay(player, n, "As you wish...",
						"Again, Bwana, What is it that brings you to the Kharazi jungle?");
					int opt3 = multi(player, n,
						"I want to develop friendly relations with your people.",
						"I'm lost, can you show me the way out?");
					if (opt3 == 0) {
						GujuoDialogue(player, n, Gujuo.I_WANT_TO_DEVELOP_FRIENDLY_RELATIONS);
					} else if (opt3 == 1) {
						GujuoDialogue(player, n, Gujuo.IM_LOST);
					}
					break;
				case Gujuo.I_WILL_RELEASE_UNGADULU:
					npcsay(player, n, "You make me very happy Bwana...",
						"In the North western part of this Kharazi jungle area, near some great cliffs.",
						"You will find three rocks that form a triangle shape.",
						"They are flanked by the palm which also forms the divine geometry",
						"You will find that they cover a small entrance...",
						"That is where Ungadulu is being kept,",
						"If you can free him, he will entrust to you some of the sacred Yommi tree seeds.");
					if (!player.getCache().hasKey("legends_cavern")) {
						player.getCache().store("legends_cavern", true);
					}
					gujuoBye(player, n);
					break;
				case Gujuo.I_WANT_TO_DEVELOP_FRIENDLY_RELATIONS:
					mes(n, "Gujuo smiles and shakes your hand warmly...");
					delay(2);
					npcsay(player, n, "Very good Bwana...this is indeed a very pleasant gesture.",
						"However, my people are very distributed throughout the Kharazi jungle.");
					int opt5 = multi(player, n,
						"Can you get your people together ?",
						"I'm lost, can you show me the way out?");
					if (opt5 == 0) {
						npcsay(player, n, "All of my people normally congregate around a totem pole, ",
							"But ours has been polluted by an evil spirit.",
							"It has been transformed, ",
							"and now our people are afraid to approach it...",
							"We tried to drive the evil spirit out of the totem pole,",
							"but it does not seem to work.");
						int opt6 = multi(player, n,
							"What can we do instead then?",
							"I'm lost, can you show me the way out?");
						if (opt6 == 0) {
							npcsay(player, n, "We could try to make a new totem pole.",
								"However, we need to make it from the trunk of the ",
								"sacred Yommi tree. ");
							int opt7 = multi(player, n, false, //do not send over
								"How do we make the totem pole?",
								"I'm lost, can you show me the way out?");
							if (opt7 == 0) {
								say(player, n, "How do we make a totem pole?");
								npcsay(player, n, "First we need to plant a sacred Yommi tree..",
									"It is a magical tree of great power, however, our Shaman..",
									"Ungadulu is the only person with the seeds for this tree.");
								mes("Gujuo's expression changes to sadness...");
								delay(2);
								npcsay(player, n, "And I fear that it is impossible to get some seeds.",
									"He is being held against his will in some caves in ",
									"north western part of the Kharazi jungle.");
								int opt8 = multi(player, n,
									"I will release Ungadulu...",
									"Oh well, sorry to hear about that ?");
								if (opt8 == 0) {
									GujuoDialogue(player, n, Gujuo.I_WILL_RELEASE_UNGADULU);
								} else if (opt8 == 1) {
									mes("Gujuo's expression of sadness deepens...");
									delay(2);
									npcsay(player, n, "Yes Bwana, perhaps we will become friends sometime in the future...",
										"But not today...",
										"Ungadulu has problably lost his mind anyway... ",
										"it is most likely a lost cause...");
									int opt9 = multi(player, n,
										"I will release Ungadulu...",
										"Ok thanks for your help.");
									if (opt9 == 0) {
										GujuoDialogue(player, n, Gujuo.I_WILL_RELEASE_UNGADULU);
									} else if (opt9 == 1) {
										GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
									}
								}
							} else if (opt7 == 1) {
								say(player, n, "I'm lost, can you show me the way out?");
								GujuoDialogue(player, n, Gujuo.IM_LOST);
							}
						} else if (opt6 == 1) {
							GujuoDialogue(player, n, Gujuo.IM_LOST);
						}
					} else if (opt5 == 1) {
						GujuoDialogue(player, n, Gujuo.IM_LOST);
					}
					break;
				case Gujuo.OK_THANKS_FOR_YOUR_HELP:
					npcsay(player, n, "You are more than welcome bwana...");
					gujuoBye(player, n);
					break;
				case Gujuo.UNGADULU_LOOKS_STRANGE:
					npcsay(player, n, "Be wary Bwana.",
						"There are many unknown spirits that reside in these dark areas.",
						"You may be tricked by an unknown force...");
					int newMenu = multi(player, n,
						"I need to douse some flames with pure water.",
						"What kind of unknown forces...",
						"Ok thanks for your help.");
					if (newMenu == 0) {
						GujuoDialogue(player, n, Gujuo.I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER);
					} else if (newMenu == 1) {
						GujuoDialogue(player, n, Gujuo.UNKNOWN_FORCES);
					} else if (newMenu == 2) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.UNGADULU_CALLED_ME_VACU:
					mes("Gujuo shakes his head slightly in sadness.");
					delay(2);
					npcsay(player, n, "It seems that Ungadulu has started to lose his senses.",
						"In our native and ancient history, ",
						"the Vacu were the servants of the evil spirits from the underworld.",
						"Originally they were priests who had summoned spirits of our ancestors,",
						"but they were enslaved...along with the rest of the village.",
						"But this is ancient history and is most likely a myth,",
						"a story told to frighten poorly behaved children...");
					int m = multi(player, n,
						"Ungadulu looks strange.",
						"I need to douse some flames with pure water.",
						"Ok thanks for your help.");
					if (m == 0) {
						GujuoDialogue(player, n, Gujuo.UNGADULU_LOOKS_STRANGE);
					} else if (m == 1) {
						GujuoDialogue(player, n, Gujuo.I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER);
					} else if (m == 2) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.UNKNOWN_FORCES:
					npcsay(player, n, "Strange spirits that our forefathers summoned for visions.",
						"They haunt the underworld and caves that exist in this area.",
						"Take not anything as it might first appear.");
					int check = multi(player, n,
						"I need to douse some flames with pure water.",
						"How did they summon the spirits?",
						"Ok thanks for your help.");
					if (check == 0) {
						GujuoDialogue(player, n, Gujuo.I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER);
					} else if (check == 1) {
						npcsay(player, n, "I am unlearned in such matters.",
							"But I am told of sacred patterns that are scored on the ground",
							"to bind the spirit and confine it...",
							"But that is all I know.");
						int nextMenu = multi(player, n,
							"Ungadulu looks strange.",
							"I need to douse some flames with pure water.",
							"Ok thanks for your help.");
						if (nextMenu == 0) {
							GujuoDialogue(player, n, Gujuo.UNGADULU_LOOKS_STRANGE);
						} else if (nextMenu == 1) {
							GujuoDialogue(player, n, Gujuo.I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER);
						} else if (nextMenu == 2) {
							GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
						}
					} else if (check == 2) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER:
					npcsay(player, n, "This sounds very strange Bwana...but maybe I can help.",
						"There is a pool of water that is sacred to us...",
						"It is located in the middle of the Kharazi jungle ",
						"The water contains special properties but it can only",
						"be contained in a blessed vessel made from metal of the sun.",
						"The water is difficult to get to, ",
						"but I am sure you will manage to claim some.");
					int opt11 = multi(player, n,
						"Metal of the sun, what is that?",
						"Ungadulu looks strange.",
						"What kind of a vessel?",
						"Ok thanks for your help.");
					if (opt11 == 0) {
						GujuoDialogue(player, n, Gujuo.METAL_OF_SUN_WHAT_IS_THAT);
					} else if (opt11 == 1) {
						GujuoDialogue(player, n, Gujuo.UNGADULU_LOOKS_STRANGE);
					} else if (opt11 == 2) {
						GujuoDialogue(player, n, Gujuo.WHAT_KIND_OF_A_VESSEL);
					} else if (opt11 == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.WHAT_KIND_OF_A_VESSEL:
					npcsay(player, n, "A vessel made of sun metal, but it can be of any shape.",
						"However, it must be blessed.");
					if (!player.getCarriedItems().hasCatalogID(ItemId.ROUGH_SKETCH_OF_A_BOWL.id(), Optional.of(false))) {
						mes("Gujuo takes out a small scroll and some charcoal and draws a rough sketch.");
						delay(2);
						mes("When he has finished, he gives the sketch to you.");
						delay(2);
						give(player, ItemId.ROUGH_SKETCH_OF_A_BOWL.id(), 1);
						npcsay(player, n, "Here, have this as an example...I pray that it will help you.");
					} else {
						npcsay(player, n, "Similar to the picture I have already given you.");
					}
					int opt12 = multi(player, n,
						"Ungadulu looks strange.",
						"I need to douse some flames with pure water.",
						"How do I bless the bowl.",
						"Ok thanks for your help.");
					if (opt12 == 0) {
						GujuoDialogue(player, n, Gujuo.UNGADULU_LOOKS_STRANGE);
					} else if (opt12 == 1) {
						GujuoDialogue(player, n, Gujuo.I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER);
					} else if (opt12 == 2) {
						GujuoDialogue(player, n, Gujuo.HOW_DO_I_BLESS_THE_BOWL);
					} else if (opt12 == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.METAL_OF_SUN_WHAT_IS_THAT:
					npcsay(player, n, "It is a bright and precious metal that is very rare.",
						"It is the same glorious colour as the sun and it never loses",
						"it's wonderous lustre...",
						"A blessed vessel made of this metal protects the purity of the water.");
					int opt13 = multi(player, n,
						"Where can I find this metal?",
						"What kind of a vessel?",
						"Ok thanks for your help.");
					if (opt13 == 0) {
						GujuoDialogue(player, n, Gujuo.WHERE_CAN_I_FIND_THIS_METAL);
					} else if (opt13 == 1) {
						GujuoDialogue(player, n, Gujuo.WHAT_KIND_OF_A_VESSEL);
					} else if (opt13 == 2) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.HOW_DO_I_BLESS_THE_BOWL:
					npcsay(player, n, "When you have made a bowl, bring it to me and I will help.",
						"But you need to ensure that you are devout and have faith.",
						"Your ability in prayer will be thoroughly tested.");
					int opt14 = multi(player, n,
						"Ungadulu looks strange.",
						"I need to douse some flames with pure water.",
						"What kind of a vessel?",
						"Ok thanks for your help.");
					if (opt14 == 0) {
						GujuoDialogue(player, n, Gujuo.UNGADULU_LOOKS_STRANGE);
					} else if (opt14 == 1) {
						GujuoDialogue(player, n, Gujuo.I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER);
					} else if (opt14 == 2) {
						GujuoDialogue(player, n, Gujuo.WHAT_KIND_OF_A_VESSEL);
					} else if (opt14 == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.WHERE_CAN_I_FIND_THIS_METAL:
					npcsay(player, n, "It is found in some rocks and must be extracted.",
						"It has a magical ability over some men and women, it can posess them.",
						"They fall within it's power and seek to gain more and more of this",
						"precious metal for themselves.",
						"A blessed vessel made of this metal protects the purity of the water.");
					int opt15 = multi(player, n,
						"Metal of the sun, what is that?",
						"Ungadulu looks strange.",
						"What kind of a vessel?",
						"Ok thanks for your help.");
					if (opt15 == 0) {
						GujuoDialogue(player, n, Gujuo.METAL_OF_SUN_WHAT_IS_THAT);
					} else if (opt15 == 1) {
						GujuoDialogue(player, n, Gujuo.UNGADULU_LOOKS_STRANGE);
					} else if (opt15 == 2) {
						GujuoDialogue(player, n, Gujuo.WHAT_KIND_OF_A_VESSEL);
					} else if (opt15 == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.HOW_GOES_YOUR_QUEST_TO_RELEASE_UNGADULU:
					npcsay(player, n, "How goes your quest to release Ungadulu Bwana?");
					int opt10 = multi(player, n, "Ungadulu looks strange.",
						"I need to douse some flames with pure water.",
						"Ungadulu called me 'Vacu', what does that mean?",
						"Ok thanks for your help.");
					if (opt10 == 0) {
						GujuoDialogue(player, n, Gujuo.UNGADULU_LOOKS_STRANGE);
					} else if (opt10 == 1) {
						GujuoDialogue(player, n, Gujuo.I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER);
					} else if (opt10 == 2) {
						GujuoDialogue(player, n, Gujuo.UNGADULU_CALLED_ME_VACU);
					} else if (opt10 == 3) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.CAN_YOU_HELP_ME:
					npcsay(player, n, "I am sorry Bwana, but I have no experience of these things.",
						"I am sure that I would get in your way...");
					int helpopts = multi(player, n, "I'm not sure what to do?", "Ok thanks for your help.");
					if (helpopts == 0) {
						GujuoDialogue(player, n, Gujuo.IM_NOT_SURE_WHAT_TO_DO);
					} else if (helpopts == 1) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.IM_NOT_SURE_WHAT_TO_DO:
					npcsay(player, n, "Don't you need to take some pure water down to the caves?",
						"If you haven't got the water yet, try getting some from the pool.");
					int sureopts = multi(player, n, "Can you help me?", "Ok thanks for your help.");
					if (sureopts == 0) {
						GujuoDialogue(player, n, Gujuo.CAN_YOU_HELP_ME);
					} else if (sureopts == 1) {
						GujuoDialogue(player, n, Gujuo.OK_THANKS_FOR_YOUR_HELP);
					}
					break;
				case Gujuo.BLESS_THE_BOWL:
					gujuoBlessBowl(player, n);
					break;
			}
		}
	}

	public void gujuoBlessBowl(Player player, Npc npc) {
		if (getCurrentLevel(player, Skill.PRAYER.id()) < 42) {
			npcsay(player, npc, "Bwana, I am very sorry,",
				"But you are too inexperienced to bless this bowl.");
			player.message("You need a prayer ability of 42 to complete this task.");
		} else {
			npcsay(player, npc, "Very well Bwana...");
			mes("Gujuo places the bowl on the floor in front of you,");
			delay(2);
			mes("and leads you into a deep meditation...");
			delay(3);
			npcsay(player, npc, "Ohhhhhmmmmmm");
			say(player, npc, "Oooooommmmmmmmmm");
			npcsay(player, npc, "Ohhhhhmmmmmm");
			say(player, npc, "Oooooohhhhmmmmmmmmmm");
			npcsay(player, npc, "Ohhhhhmmmmmm");
			if (Formulae.failCalculation(player, Skill.PRAYER.id(), 42)) {
				mes("A totally peacefull aura surrounds you and you ");
				delay(2);
				mes("bring down the blessings of your god on the bowl.");
				delay(2);
				if (player.getCarriedItems().hasCatalogID(ItemId.GOLDEN_BOWL.id(), Optional.of(false))) {
					player.getCarriedItems().remove(new Item(ItemId.GOLDEN_BOWL.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL.id()));
				} else if (player.getCarriedItems().hasCatalogID(ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id(), Optional.of(false))) {
					player.getCarriedItems().remove(new Item(ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()));
				} else if (player.getCarriedItems().hasCatalogID(ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id(), Optional.of(false))) {
					player.getCarriedItems().remove(new Item(ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id()));
				}
				GujuoDialogue(player, npc, Gujuo.HOW_GOES_YOUR_QUEST_TO_RELEASE_UNGADULU);
			} else {
				mes("You were not able to go into a deep enough trance.");
				delay(2);
				mes("You lose some prayer...");
				delay(2);
				player.getSkills().setLevel(Skill.PRAYER.id(), player.getSkills().getLevel(Skill.PRAYER.id()) - 5);
				npcsay(player, npc, "Would you like to try again.");
				int failMenu = multi(player, npc, false, //do not send over
					"Yes, I'd like to bless my golden bowl.",
					"No thanks, I'll wait.");
				if (failMenu == 0) {
					GujuoDialogue(player, npc, Gujuo.BLESS_THE_BOWL);
				} else if (failMenu == 1) {
					say(player, npc, "No thanks, I'll wait.");
					npcsay(player, npc, "Very well, let me know when you want to try?");
					GujuoDialogue(player, npc, Gujuo.HOW_GOES_YOUR_QUEST_TO_RELEASE_UNGADULU);
				}
			}
		}
	}

	public void gujuoBye(Player player, Npc n) {
		int yell = DataConversions.random(0, 3);
		if (yell == 0) {
			npcsay(player, n, "I am tired Bwana, I must go and rest...");
		}
		if (yell == 1) {
			npcsay(player, n, "I must visit my people now...");
		} else if (yell == 2) {
			npcsay(player, n, "I must go and hunt now Bwana..");
		} else if (yell == 3) {
			npcsay(player, n, "I have to collect herbs now Bwana...");
		} else {
			npcsay(player, n, "I have work to do Bwana, I may see you again...");
		}
		npcsay(player, n, "");
		player.getWorld().getServer().getGameEventHandler().add(new SingleEvent(player.getWorld(), null, config().GAME_TICK * 3, "Legends Quest Gujuo Disappears") {
			public void action() {
				if (player != null)
					player.message("Gujuo disapears into the Kharazi jungle as swiftly as he appeared...");
				if(n != null)
					n.remove();
			}
		});
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GUJUO.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GUJUO.id()) {
			GujuoDialogue(player, n, -1);
		}
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.GUJUO.id() && !item.getNoted()) {
			if (item.getCatalogId() == ItemId.GOLDEN_BOWL.id()
				|| item.getCatalogId() == ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id() || item.getCatalogId() == ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id()) {
				npcsay(player, npc, "Aha Bwana, well done, you have made the golden bowl.",
					"Would you like me to show you how to bless it.");
				int menu = multi(player, npc, false, //do not send over
					"Yes, I'd like to bless my golden bowl.", "No thanks, I'll wait.");
				if (menu == 0) {
					gujuoBlessBowl(player, npc);
				} else if (menu == 1) {
					say(player, npc, "No thanks, I'll wait.");
					npcsay(player, npc, "Very well, let me know when you want to try?");
					GujuoDialogue(player, npc, Gujuo.HOW_GOES_YOUR_QUEST_TO_RELEASE_UNGADULU);
				}
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return (npc.getID() == NpcId.GUJUO.id() && !item.getNoted() && (item.getCatalogId() == ItemId.GOLDEN_BOWL.id()
			|| item.getCatalogId() == ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id() || item.getCatalogId() == ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id()));
	}

	class Gujuo {
		static final int SORRY_IT_WAS_A_MISTAKE = 0;
		static final int IM_LOST = 1;
		static final int NO_THANKS = 2;
		static final int I_WILL_RELEASE_UNGADULU = 3;
		static final int I_WANT_TO_DEVELOP_FRIENDLY_RELATIONS = 4;
		static final int OK_THANKS_FOR_YOUR_HELP = 5;
		static final int UNGADULU_LOOKS_STRANGE = 6;
		static final int UNGADULU_CALLED_ME_VACU = 7;
		static final int UNKNOWN_FORCES = 8;
		static final int I_NEED_TO_DOUSE_SOME_FLAMES_WITH_PURE_WATER = 9;
		static final int WHAT_KIND_OF_A_VESSEL = 10;
		static final int METAL_OF_SUN_WHAT_IS_THAT = 11;
		static final int HOW_DO_I_BLESS_THE_BOWL = 12;
		static final int WHERE_CAN_I_FIND_THIS_METAL = 13;
		static final int HOW_GOES_YOUR_QUEST_TO_RELEASE_UNGADULU = 14;
		static final int BLESS_THE_BOWL = 15;
		static final int IM_NOT_SURE_WHAT_TO_DO = 16;
		static final int CAN_YOU_HELP_ME = 17;
		static final int WHAT_DO_I_DO_NOW = 18;
		static final int I_HAVE_THE_YOMMI_TREE_SEEDS = 19;
		static final int UNGADULU_IS_FREE = 20;
		static final int WHERE_IS_THE_FETILE_SOIL = 21;
		static final int I_HAVE_GERMINATED_THE_YOMMI_TREE_SEEDS = 22;
		static final int THE_YOMMI_TREE_DIED = 23;
		static final int DOES_THE_YOMMI_TREE_HAVE_TO_HAVE_PURE_WATER = 24;
		static final int WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER = 25;
		static final int THE_WATER_POOL_HAS_DRIED_UP_AND_I_NEED_MORE_WATER = 26;
		static final int WHERE_CAN_I_GET_MORE_WATER_FOR_THE_YOMMI_TREE = 27;
		static final int I_SEARCHED_THE_CATACOMBS_THOROUGHLY_BUT_FOUND_NADA_NIET = 28;
		static final int IF_I_WENT_IN_SEARCH_OF_THE_SOURCE_COULD_U_HELP_ME = 29;
		static final int WHERE_IS_THE_SOURCE_OF_THE_SPRING_OF_PURE_WATER2 = 30;
		static final int WHERE_CAN_I_FIND_SNAKE_WEED = 31;
		static final int WHERE_CAN_I_FIND_ARDRIGAL = 32;
		static final int WILL_I_NEED_THIS_POTION_I_FEEL_BRAVE_AS_I_AM = 33;

		static final int I_FOUND_WAY_INTO_CAVES = 34;
		static final int DO_YOU_KNOW_MORE_ABOUT_CAVES = 35;
		static final int WHO_IS_VIYELDI = 36;
		static final int I_FOUND_THE_SOURCE_OF_THE_SPRING_AND_I_GOT_THE_WATER = 37;
		static final int I_KILLED_THE_DEMON_AGAIN = 38;
		static final int HOW_DO_I_MAKE_THE_TOTEM_POLE = 39;
		static final int OK_I_WONT_GO = 40;
	}
}
