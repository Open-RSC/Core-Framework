package com.openrsc.server.plugins.quests.members.watchtower;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

/**
 * @author Imposter/Fate
 */
public class WatchTowerDialogues implements QuestInterface, TalkNpcTrigger {

	/**
	 * REMEMBER:
	 * Ogre Og = Want coins.
	 * Ogre Grew = Want Tooth.
	 * **/

	@Override
	public int getQuestId() {
		return Quests.WATCHTOWER;
	}

	@Override
	public String getQuestName() {
		return "Watchtower (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		//NOT USED NO NEED.
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.WATCHTOWER_WIZARD.id(), NpcId.GREW.id(), NpcId.OG.id(), NpcId.TOBAN.id(), NpcId.OGRE_CITIZEN.id(),
				NpcId.OGRE_TRADER_FOOD.id(), NpcId.OGRE_GUARD_CAVE_ENTRANCE.id(), NpcId.OGRE_TRADER_ROCKCAKE.id(), NpcId.CITY_GUARD.id(),
				NpcId.SKAVID_FINALQUIZ.id(), NpcId.SKAVID_IG.id(), NpcId.SKAVID_AR.id(), NpcId.SKAVID_CUR.id(), NpcId.SKAVID_NOD.id(), NpcId.SKAVID_INITIAL.id());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SKAVID_FINALQUIZ.id()) {
			if (p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Quests.WATCHTOWER) == -1) {
				npcsay(p, n, "What, you gots the crystal...");
				int lastMenu = multi(p, n, "But I've lost it!", "Oh okay then");
				if (lastMenu == 0) {
					if (p.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL2.id(), Optional.empty()) || p.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(p, n, "I have no more for you!");
					} else {
						npcsay(p, n, "All right, take this one then...");
						p.message("The skavid gives you a crystal");
						give(p, ItemId.POWERING_CRYSTAL2.id(), 1);
					}
				} else if (lastMenu == 1) {
					npcsay(p, n, "I'll be on my way then");
				}
			} else if (p.getCache().hasKey("language_cur")
				&& p.getCache().hasKey("language_ar")
				&& p.getCache().hasKey("language_ig")
				&& p.getCache().hasKey("language_nod")) {
				String[] sayChat = {"Cur tanath...", "Ar cur...", "Bidith Ig...", "Gor nod..."};
				int randomizeChat = DataConversions.random(0, sayChat.length - 1);
				npcsay(p, n, sayChat[randomizeChat]);
				int menu = multi(p, n, "Cur", "Ar", "Bidith", "Tanath", "Gor");
				boolean correctAnswer = false;
				if (menu == 0) {
					if (randomizeChat == 2)
						correctAnswer = true;
				} else if (menu == 2) {
					if (randomizeChat == 0)
						correctAnswer = true;
				} else if (menu == 3) {
					if (randomizeChat == 3)
						correctAnswer = true;
				} else if (menu == 4) {
					if (randomizeChat == 1)
						correctAnswer = true;
				}
				if (menu != -1) {
					if (correctAnswer) {
						npcsay(p, n, "Heh-heh! So you speak a little skavid eh?",
							"I'm impressed, here take this prize...");
						p.message("The skavid gives you a large crystal");
						give(p, ItemId.POWERING_CRYSTAL2.id(), 1);
						if (p.getCache().hasKey("language_cur")
							&& p.getCache().hasKey("language_ar")
							&& p.getCache().hasKey("language_ig")
							&& p.getCache().hasKey("language_nod")) {
							p.getCache().remove("language_cur");
							p.getCache().remove("language_ar");
							p.getCache().remove("language_ig");
							p.getCache().remove("language_nod");
							p.getCache().remove("skavid_started_language");
							p.getCache().store("skavid_completed_language", true);
						}
					} else if (menu == 1) {
						npcsay(p, n, "Grrr!");
						p.message("It seems your response has upset the skavid");
					} else {
						npcsay(p, n, "???");
						p.message("The response was wrong");
					}
				}
			} else {
				npcsay(p, n, "Tanath Gor Ar Bidith ?");
				say(p, n, "???");
				p.message("You cannot communicate with the skavid");
				p.message("It seems you haven't learned enough of thier language yet...");
			}
		}
		else if (inArray(n.getID(), NpcId.SKAVID_IG.id(), NpcId.SKAVID_AR.id(), NpcId.SKAVID_CUR.id(), NpcId.SKAVID_NOD.id())) {
			if (n.getID() == NpcId.SKAVID_IG.id()) {
				if (p.getCache().hasKey("language_ig") || p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Quests.WATCHTOWER) == -1) {
					npcsay(p, n, "Bidith Ig...");
					p.message("You have already talked to this skavid");
					return;
				}
				npcsay(p, n, "Cur bidith...");
			} else if (n.getID() == NpcId.SKAVID_AR.id()) {
				if (p.getCache().hasKey("language_ar") || p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Quests.WATCHTOWER) == -1) {
					npcsay(p, n, "Ar cur...");
					p.message("You have already talked to this skavid");
					return;
				}
				npcsay(p, n, "Gor cur...");
			} else if (n.getID() == NpcId.SKAVID_CUR.id()) {
				if (p.getCache().hasKey("language_cur") || p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Quests.WATCHTOWER) == -1) {
					npcsay(p, n, "Cur tanath...");
					p.message("You have already talked to this skavid");
					return;
				}
				npcsay(p, n, "Bidith tanath...");
			} else if (n.getID() == NpcId.SKAVID_NOD.id()) {
				if (p.getCache().hasKey("language_nod") || p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Quests.WATCHTOWER) == -1) {
					npcsay(p, n, "Gor nod...");
					p.message("You have already talked to this skavid");
					return;
				}
				npcsay(p, n, "Tanath gor...");
			}
			if (p.getCache().hasKey("skavid_started_language")) {
				p.message("The skavid is trying to communicate...");
				boolean correctWord = false;
				int learnMenu = multi(p, n, "Cur", "Ar", "Ig", "Nod", "Gor");
				if (learnMenu == 0) {
					if (n.getID() == NpcId.SKAVID_CUR.id()) {
						npcsay(p, n, "Cur",
							"Cur tanath");
						p.getCache().store("language_cur", true);
						correctWord = true;
					}
				} else if (learnMenu == 1) {
					if (n.getID() == NpcId.SKAVID_AR.id()) {
						npcsay(p, n, "Ar",
							"Ar cur");
						p.getCache().store("language_ar", true);
						correctWord = true;
					}
				} else if (learnMenu == 2) {
					if (n.getID() == NpcId.SKAVID_IG.id()) {
						npcsay(p, n, "Ig",
							"Bidith Ig");
						p.getCache().store("language_ig", true);
						correctWord = true;
					}
				} else if (learnMenu == 3) {
					if (n.getID() == NpcId.SKAVID_NOD.id()) {
						npcsay(p, n, "Nod",
							"Gor nod");
						p.getCache().store("language_nod", true);
						correctWord = true;
					}
				}

				if (learnMenu != -1) {
					if (correctWord) {
						p.message("It seems the skavid understood you");
					} else {
						npcsay(p, n, "???");
						Functions.mes(p, "It seems that was the wrong reply");
					}
				}
			} else {
				say(p, n, "???");
				p.message("The skavid is trying to communicate...");
				p.message("You don't know any skavid words yet!");
			}
		}

		else if (n.getID() == NpcId.SKAVID_INITIAL.id()) {
			if (p.getQuestStage(Quests.WATCHTOWER) == -1) {
				npcsay(p, n, "Ah master...",
					"You did well to master our language...");
				return;
			}
			if ((p.getCache().hasKey("language_cur")
				&& p.getCache().hasKey("language_ar")
				&& p.getCache().hasKey("language_ig")
				&& p.getCache().hasKey("language_nod")) || p.getCache().hasKey("skavid_completed_language")) {
				npcsay(p, n, "Master, my kinsmen tell me you have learned skavid",
					"You should speak to the mad ones in their cave...");
				return;
			} else if (p.getCache().hasKey("skavid_started_language")) {
				npcsay(p, n, "Master, how are you doing learning our language ?");
				say(p, n, "I am studying the speech of your kind...");
			} else {
				npcsay(p, n, "Tanath cur, tanath cur");
				say(p, n, "???");
				npcsay(p, n, "Don't hurt me, don't hurt me!");
				say(p, n, "Stop moaning creature",
					"I know about you skavids",
					"You serve those monsters the ogres");
				npcsay(p, n, "Please dont touch me!");
				say(p, n, "You have something that belongs to me...");
				npcsay(p, n, "I don't have anything, please believe me!");
				say(p, n, "Somehow I find your words hard to believe");
				npcsay(p, n, "I'm begging your kindness, I don't have it!");
				int menu = multi(p, n, false, //do not send over
					"I don't believe you hand it over!",
					"Okay okay i'm not going to hurt you");
				if (menu == 0) {
					say(p, n, "I don't believe you, hand it over!");
					npcsay(p, n, "Ahhhhh, help!");
					p.message("The skavid runs away...");
					delnpc(n, true);
					say(p, n, "Oh great...I've scared it off!");
				} else if (menu == 1) {
					say(p, n, "Okay, okay i'm not going to hurt you");
					npcsay(p, n, "Thank you kind " + (p.isMale() ? "sir" : "madam"),
						"I'll tells you where that things you wants is...",
						"The mad skavids have it in their cave in the city",
						"You will have to learn skavid",
						"Otherwise they will not talks to you",
						"Make sure you remembers all that you hear",
						"Let me tells you the most common skavid words...",
						"Ar",
						"Nod",
						"Gor",
						"Ig",
						"Cur",
						"That will gets you started...");
					p.getCache().store("skavid_started_language", true);
					p.updateQuestStage(Quests.WATCHTOWER, 5);
				}
			}
		}

		else if (n.getID() == NpcId.WATCHTOWER_WIZARD.id()) {
			watchtowerWizardDialogue(p, n, -1);
		}
		else if (n.getID() == NpcId.OGRE_CITIZEN.id()) {
			npcsay(p, n, "Uh ? what are you doing here ?");
		}
		else if (n.getID() == NpcId.OGRE_TRADER_FOOD.id()) {
			npcsay(p, n, "Grrr, little animal.. I shall destroy you!");
			n.startCombat(p);
		}
		else if (n.getID() == NpcId.OGRE_GUARD_CAVE_ENTRANCE.id()) {
			if (p.getQuestStage(Quests.WATCHTOWER) != -1) {
				npcsay(p, n, "What do you want ?");
				int menu = multi(p, n,
					"I want to go in there",
					"I want to rid the world of ogres");
				if (menu == 0) {
					npcsay(p, n, "Oh you do, do you ?",
						"How about no ?");
					n.startCombat(p);
				} else if (menu == 1) {
					npcsay(p, n, "You dare mock me creature!!!");
					n.startCombat(p);
				}
			} else {
				p.message("The guard is occupied at the moment");
			}
		}
		else if (n.getID() == NpcId.OGRE_TRADER_ROCKCAKE.id()) {
			npcsay(p, n, "Arr, small thing wants my food does it ?",
				"I'll teach you to deal with ogres!");
			n.startCombat(p);
		}
		else if (n.getID() == NpcId.CITY_GUARD.id()) {
			if (p.getCache().hasKey("city_guard_riddle")) {
				npcsay(p, n, "What is it ?");
				int menu = multi(p, n,
					"Do you have any other riddles for me ?",
					"I have lost the map you gave me");
				if (menu == 0) {
					npcsay(p, n, "Yes, what looks good on a plate with salad ?");
					int subMenu = multi(p, n,
						"I don't know...",
						"A nice pizza ?");
					if (subMenu == 0) {
						npcsay(p, n, "You!!!",
							"Now go and bother me no more...");
					} else if (subMenu == 1) {
						npcsay(p, n, "Grr.. think you are a comedian eh ?",
							"Get lost!");
					}
				} else if (menu == 1) {
					if (p.getCarriedItems().hasCatalogID(ItemId.SKAVID_MAP.id(), Optional.of(false))) {
						npcsay(p, n, "Are you blind ? what is that you are carrying ?");
						say(p, n, "Oh, that map....");
					} else {
						npcsay(p, n, "What's the point ? take this copy and bother me no more!");
						give(p, ItemId.SKAVID_MAP.id(), 1);
					}
				}
			} else {
				npcsay(p, n, "Grrrr, what business have you here ?");
				say(p, n, "I am on an errand...");
				npcsay(p, n, "So what do you want with me ?");
				int menu = multi(p, n, "I am an ogre killer come to destroy you!",
					"I seek passage into the skavid caves");
				if (menu == 0) {
					npcsay(p, n, "I would like to see you try!");
					n.startCombat(p);
				} else if (menu == 1) {
					npcsay(p, n, "Is that so...",
						"You humour me small thing, answer this riddle and I will help you...",
						"I want you to bring me an item",
						"I will give you all the letters of this item, you work out what it is...",
						"My first is in days, but not in years",
						"My second is in evil, and also in tears",
						"My third is in all, but not in none",
						"My fourth is in hot, but not in sun",
						"My fifth is in heaven, and also in hate",
						"My sixth is in fearing, but not in fate",
						"My seventh is in plush, but not in place",
						"My eighth is in nine, but not in eight",
						"My last is in earth, and also in in great",
						"My whole is an object, that magic will make",
						"It brings wrack and ruin to all in it's wake...",
						"Now how long I wonder, will this riddle take ?");
				}
			}
		}
		else if (n.getID() == NpcId.GREW.id()) {
			switch (p.getQuestStage(this)) {
				case -1:
					p.message("The ogre is not interested in you anymore");
					break;
				case 0:
				case 1:
					p.message("The ogre has nothing to say at the moment...");
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
					if (p.getCache().hasKey("ogre_grew_p1")) {
						npcsay(p, n, "What are you doing here morsel ?");
						int menu = multi(p, n,
							"Can I do anything else for you ?",
							"I've lost the relic part you gave me",
							"I've lost the crystal you gave me");
						if (menu == 0) {
							npcsay(p, n, "I have nothing left for you but the cooking pot!");
						} else if (menu == 1) {
							if (!p.getCarriedItems().hasCatalogID(ItemId.OGRE_RELIC_PART_BASE.id(), Optional.empty())) {
								npcsay(p, n, "Stupid morsel, I have another",
									"Take it and go now before I lose my temper");
								give(p, ItemId.OGRE_RELIC_PART_BASE.id(), 1);
							} else {
								npcsay(p, n, "You lie to me morsel!");
							}
						} else if (menu == 2) {
							if (p.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL1.id(), Optional.empty())) {
								npcsay(p, n, "I suppose you want another ?",
									"I suppose just this once I could give you my copy...");
								give(p, ItemId.POWERING_CRYSTAL1.id(), 1);
							} else {
								npcsay(p, n, "How dare you lie to me Morsel!",
									"I will finish you now!");
							}
						}
					} else {
						if (p.getCache().hasKey("ogre_grew")) {
							npcsay(p, n, "The morsel is back",
								"Does it have our tooth for us ?");
							if (p.getCarriedItems().hasCatalogID(ItemId.OGRE_TOOTH.id(), Optional.of(false))) {
								say(p, n, "I have it");
								npcsay(p, n, "It's got it, good good",
									"That should annoy gorad wonderfully",
									"Heheheheh!");
								p.getCarriedItems().remove(new Item(ItemId.OGRE_TOOTH.id()));
								npcsay(p, n, "Heres a token of my gratitude");
								give(p, ItemId.OGRE_RELIC_PART_BASE.id(), 1);
								npcsay(p, n, "Some old gem I stole from Gorad...",
									"And an old part of a statue",
									"Heheheheh!");
								p.message("The ogre hands you a large crystal");
								p.message("The ogre gives you part of a statue");
								give(p, ItemId.POWERING_CRYSTAL1.id(), 1);
								p.getCache().remove("ogre_grew");
								p.getCache().store("ogre_grew_p1", true);
							} else {
								say(p, n, "Err, I don't have it");
								npcsay(p, n, "Morsel, you dare to return without the tooth!",
									"Either you are a fool, or want to be eaten!");
							}
						} else {
							npcsay(p, n, "What do you want tiny morsel ?",
								"You would look good on my plate");
							say(p, n, "I want to enter the city of ogres");
							npcsay(p, n, "Perhaps I should eat you instead ?");
							int menu = multi(p, n,
								"Don't eat me, I can help you",
								"You will have to kill me first");
							if (menu == 0) {
								npcsay(p, n, "What can a morsel like you do for me ?");
								say(p, n, "I am a mighty adventurer",
									"Slayer of monsters and user of magic powers");
								npcsay(p, n, "Well well, perhaps the morsel can help after all...",
									"If you think you're tough",
									"Find Gorad my enemy in the south east settlement",
									"And knock one of his teeth out!",
									"Heheheheh!");
								p.getCache().store("ogre_grew", true);
							} else if (menu == 1) {
								npcsay(p, n, "That can be arranged - guards!!");
								ogreSpawnAndAttack(p, n);
							}
						}
					}
					break;
			}
		}
		else if (n.getID() == NpcId.OG.id()) {
			switch (p.getQuestStage(this)) {
				case -1:
					p.message("The ogre is not interested in you anymore");
					break;
				case 0:
				case 1:
					p.message("He's busy, try him later");
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
					if (p.getCache().hasKey("ogre_relic_part_3")) {
						npcsay(p, n, "It's the little rat again");
						int menu = multi(p, n,
							"Do you have any other tasks for me ?",
							"I have lost the relic part you gave me");
						if (menu == 0) {
							npcsay(p, n, "No, I have no more tasks for you, now go away");
						} else if (menu == 1) {
							if (!p.getCarriedItems().hasCatalogID(ItemId.OGRE_RELIC_PART_HEAD.id(), Optional.empty())) {
								npcsay(p, n, "Grrr, why do I bother ?",
									"It's a good job I have another part!");
								give(p, ItemId.OGRE_RELIC_PART_HEAD.id(), 1);
							} else {
								npcsay(p, n, "Are you blind! I can see you have it even from here!");
							}
						}
					} else {
						if (p.getCache().hasKey("ogre_og")) {
							npcsay(p, n, "Where is my gold from that traitor toban?");
							int subMenu = multi(p, n,
								"I have your gold",
								"I haven't got it yet",
								"I have lost the key!");
							if (subMenu == 0) {
								if (p.getCarriedItems().hasCatalogID(ItemId.STOLEN_GOLD.id(), Optional.of(false))) {
									npcsay(p, n, "Well well, the little rat has got it!",
										"take this to show the little rat is a friend to the ogres",
										"Hahahahaha!");
									p.getCarriedItems().remove(new Item(ItemId.STOLEN_GOLD.id()));
									p.message("The ogre gives you part of a horrible statue");
									give(p, ItemId.OGRE_RELIC_PART_HEAD.id(), 1);
									p.getCache().remove("ogre_og");
									/** Very strange setup of quest tbh, but that's what it is **/
									p.getCache().store("ogre_relic_part_3", true);
								} else {
									npcsay(p, n, "That is not what I want rat!",
										"If you want to impress me",
										"Then get the gold I asked for!");
								}
							} else if (subMenu == 1) {
								npcsay(p, n, "Don't come back until you have it",
									"Unless you want to be on tonight's menu!");
							} else if (subMenu == 2) {
								if (p.getCarriedItems().hasCatalogID(ItemId.KEY.id(), Optional.of(false))) {
									npcsay(p, n, "Oh yeah! what's that then ?");
									p.message("It seems you still have the key...");
								} else {
									npcsay(p, n, "Idiot! take another and don't lose it!");
									give(p, ItemId.KEY.id(), 1);
								}
							}
						} else {
							npcsay(p, n, "Why are you here little rat ?");
							int menu = multi(p, n,
								"I seek entrance to the city of ogres",
								"I have come to kill you");
							if (menu == 0) {
								npcsay(p, n, "You have no business there!",
									"Just a minute...maybe if you did something for me I might help you get in...");
								say(p, n, "What can I do to help an ogre ?");
								npcsay(p, n, "South East of here there is another settlement",
									"The name of the chieftan is Toban",
									"He stole some gold from me",
									"And I want it back!",
									"Here is a key to the chest it's in",
									"If you bring it here",
									"I may reward you...");
								give(p, ItemId.KEY.id(), 1);
								p.getCache().store("ogre_og", true);
							} else if (menu == 1) {
								npcsay(p, n, "Kill me eh ?",
									"you shall be crushed like the vermin you are!",
									"Guards!!");
								ogreSpawnAndAttack(p, n);
							}
						}
					}
					break;
			}
		}
		else if (n.getID() == NpcId.TOBAN.id()) {
			switch (p.getQuestStage(this)) {
				case -1:
					p.message("The ogre is not interested in you anymore");
					break;
				case 0:
				case 1:
					p.message("He is busy at the moment...");
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
					if (p.getCache().hasKey("ogre_relic_part_1")) {
						npcsay(p, n, "The small thing returns, what do you want now ?");
						int subMenu = multi(p, n,
							"I seek another task",
							"I can't find the relic part you gave me");
						if (subMenu == 0) {
							npcsay(p, n, "Have you arrived for dinner ?",
								"Ha ha ha! begone small thing!");
						} else if (subMenu == 1) {
							if (!p.getCarriedItems().hasCatalogID(ItemId.OGRE_RELIC_PART_BODY.id(), Optional.empty())) {
								npcsay(p, n, "Small thing, how could you be so careless ?",
									"Here, take this one");
								give(p, ItemId.OGRE_RELIC_PART_BODY.id(), 1);
							} else {
								npcsay(p, n, "Small thing, you lie to me!",
									"I always says that small things are big trouble...");
							}
						}
					} else {
						if (p.getCache().hasKey("ogre_toban")) {
							npcsay(p, n, "Ha ha ha! small thing returns",
								"Did you bring the dragon bone ?");
							if (p.getCarriedItems().hasCatalogID(ItemId.DRAGON_BONES.id(), Optional.of(false))) {
								say(p, n, "When I say I will get something I get it!");
								p.getCarriedItems().remove(new Item(ItemId.DRAGON_BONES.id()));
								npcsay(p, n, "Ha ha ha! small thing has done it",
									"Toban is glad, take this...");
								p.message("The ogre gives you part of a statue");
								give(p, ItemId.OGRE_RELIC_PART_BODY.id(), 1);
								p.getCache().remove("ogre_toban");
								p.getCache().store("ogre_relic_part_1", true);
							} else {
								say(p, n, "I have nothing for you");
								npcsay(p, n, "Then you shall get nothing from me!");
							}
						} else {
							npcsay(p, n, "What do you want small thing ?");
							int menu = multi(p, n,
								"I seek entrance to the city of ogres",
								"Die creature");
							if (menu == 0) {
								npcsay(p, n, "Ha ha ha! you'll never get in there");
								say(p, n, "I fear not for that city");
								npcsay(p, n, "Bold words for a thing so small");
								int subMenu = multi(p, n,
									"I could do something for you...",
									"Die creature");
								if (subMenu == 0) {
									npcsay(p, n, "Ha ha ha! this creature thinks it can help me!",
										"I would eat you now, but for your puny size",
										"Prove to me your might",
										"Bring me the bones of a dragon to chew on",
										"And I may spare you from a painful death");
									p.getCache().store("ogre_toban", true);
								} else if (subMenu == 1) {
									npcsay(p, n, "Ha ha ha! it thinks it's a match for toban does it ?");
									n.startCombat(p);
								}
							} else if (menu == 1) {
								npcsay(p, n, "Ha ha ha! it thinks it's a match for toban does it ?");
								n.startCombat(p);
							}
						}
					}
					break;
			}
		}
	}

	private void watchtowerWizardDialogue(Player p, Npc n, int cID) {
		if (n.getID() == NpcId.WATCHTOWER_WIZARD.id()) {
			if (cID == -1) {
				switch (p.getQuestStage(this)) {
					case -1:
						if (p.getCache().hasKey("watchtower_scroll")) {
							npcsay(p, n, "Greetings friend",
								"I trust all is well with you ?",
								"Yanilee is safe at last!");
						} else {
							npcsay(p, n, "Hello again adventurer",
								"Thanks again for your help in keeping us safe");
							int finish = multi(p, n,
								"I lost the scroll you gave me",
								"That's okay");
							if (finish == 0) {
								if (!p.getBank().hasItemId(ItemId.SPELL_SCROLL.id()) && !p.getCarriedItems().hasCatalogID(ItemId.SPELL_SCROLL.id())) {
									npcsay(p, n, "Never mind, have another...");
									give(p, ItemId.SPELL_SCROLL.id(), 1);
								} else if (p.getBank().hasItemId(ItemId.SPELL_SCROLL.id())) {
									//maybe non-kosher message though it was also bank restricted
									npcsay(p, n, "Ho ho ho! a comedian to the finish!",
										"There it is, in your bank!");
								} else {
									npcsay(p, n, "Ho ho ho! a comedian to the finish!",
										"There it is, in your backpack!");
								}
							} else if (finish == 1) {
								npcsay(p, n, "We are always in your debt...");
							}
						}
						break;
					case 0:
						npcsay(p, n, "Oh my Oh my!");
						int menu = multi(p, n,
							"What's the matter ?",
							"You wizards are always complaining");
						if (menu == 0) {
							npcsay(p, n, "Oh dear oh dear",
								"Darn and drat",
								"We try hard to keep this town protected",
								"But how can we do that when the watchtower isn't working ?");
							say(p, n, "What do you mean it isn't working ?");
							npcsay(p, n, "The watchtower here works by the power of a magical device",
								"An ancient spell designed to ward off ogres",
								"That has been in place here for many moons",
								"The exact knowledge of the spell is lost to us now",
								"But the essence of the spell",
								"Has been infused into 4 powering crystals",
								"To keep the tower protected from the hordes in the mendips...");
							int menu2 = multi(p, n,
								"So how come the spell dosen't work ?",
								"I'm not interested in the rantings of an old wizard");
							if (menu2 == 0) {
								npcsay(p, n, "The crystals! the crystals!",
									"They have been taken!");
								say(p, n, "Taken...");
								npcsay(p, n, "Stolen!");
								say(p, n, "Stolen...");
								npcsay(p, n, "Yes, yes! do I have to repeat myself ?");
								p.message("The wizard seems very stressed...");
								int menu3 = multi(p, n,
									"Can I be of help ?",
									"I'm not sure I can help",
									"I'm not interested");
								if (menu3 == 0) {
									npcsay(p, n, "Help ?",
										"Oh wonderful dear traveller",
										"Yes I could do with an extra pair of eyes here");
									say(p, n, "???");
									npcsay(p, n, "There must be some evidence of what has happened somewhere",
										"Perhaps you could assist me in searching for clues");
									say(p, n, "I would be happy to");
									npcsay(p, n, "Try searching the surrounding area");
									/** QUEST START - STAGE 1 **/
									p.updateQuestStage(this, 1);
								} else if (menu3 == 1) {
									npcsay(p, n, "Oh dear what am I to do ?",
										"The safety of this whole area is in jeopardy!");
								} else if (menu3 == 2) {
									npcsay(p, n, "That's typical nowadays",
										"Its left to us wizards to do all the work...");
									p.message("The wizard is not impressed");
								}
							} else if (menu2 == 1) {
								p.message("The wizard gives you a suspicious look");
							}
						} else if (menu == 1) {
							npcsay(p, n, "Complaining ?.... complaining !",
								"What folks these days don't realize",
								"Is that if it wasn't for us wizards",
								"This entire world would be overrun",
								"With every creature that walks this world!");
							p.message("The wizard angrily walks away");
						}
						break;
					case 1:
						npcsay(p, n, "Hello again",
							"Did you find anything of interest ?");
						if (p.getCarriedItems().hasCatalogID(ItemId.FINGERNAILS.id(), Optional.of(false))) {
							say(p, n, "Have a look at these");
							p.getCarriedItems().remove(new Item(ItemId.FINGERNAILS.id()));
							npcsay(p, n, "Interesting, very interesting",
								"Long nails...grey in colour",
								"Well chewed...",
								"Of course, they belong to a skavid");
							say(p, n, "A skavid ?");
							npcsay(p, n, "A servant race to the ogres",
								"Gray depressed looking creatures",
								"Always loosing nails, teeth and hair",
								"They inhabit the caves in the mendip hills",
								"They normally keep to themselves though",
								"It's unusual for them to venture from their caves");
							int m = multi(p, n,
								"What do you suggest that I do ?",
								"Shall I search the caves ?");
							if (m == 0) {
								watchtowerWizardDialogue(p, n, WatchTowerWizard.SEARCHINGTHECAVES);
							} else if (m == 1) {
								watchtowerWizardDialogue(p, n, WatchTowerWizard.SEARCHINGTHECAVES);
							}
						}
						/** EASTER EGG? IN OFFICIAL RSC THE RELATED QUEST ITEMS WERE NOT CHECKED
						 * BUT INSTEAD THE REGULAR ONES (see wiki)
						 * **/
						else if (p.getCarriedItems().hasCatalogID(ItemId.EYE_PATCH.id(), Optional.of(false))
							|| p.getCarriedItems().hasCatalogID(ItemId.GOBLIN_ARMOUR.id(), Optional.of(false))
							|| p.getCarriedItems().hasCatalogID(ItemId.IRON_DAGGER.id(), Optional.of(false))
							|| p.getCarriedItems().hasCatalogID(ItemId.WIZARDS_ROBE.id(), Optional.of(false))) {
							if (p.getCarriedItems().hasCatalogID(ItemId.EYE_PATCH.id(), Optional.of(false))) {
								say(p, n, "I found this eye patch");
							} else if (p.getCarriedItems().hasCatalogID(ItemId.GOBLIN_ARMOUR.id(), Optional.of(false))) {
								say(p, n, "Have a look at this goblin armour");
							} else if (p.getCarriedItems().hasCatalogID(ItemId.IRON_DAGGER.id(), Optional.of(false))) {
								say(p, n, "I found a dagger");
							} else if (p.getCarriedItems().hasCatalogID(ItemId.WIZARDS_ROBE.id(), Optional.of(false))) {
								say(p, n, "I have this robe");
							}
							npcsay(p, n, "Let me see...",
								"No, sorry this is not evidence",
								"You need to keep searching im afraid");
						} else {
							say(p, n, "No nothing yet");
							npcsay(p, n, "Oh dear oh dear",
								"There must be something somewhere");
						}
						break;
					case 2:
						npcsay(p, n, "How's it going ?");
						int newM = multi(p, n,
							"I am having difficulty with the tribes",
							"I have everything under control",
							"I have lost something the ogres gave to me");
						if (newM == 0) {
							npcsay(p, n, "Talk to them face to face",
								"And don't show any fear",
								"Make sure you are rested and well-fed",
								"And fight the good fight!");
						} else if (newM == 1) {
							npcsay(p, n, "Good, good! I will expect the crystals back shortly then...");
						} else if (newM == 2) {
							npcsay(p, n, "Oh deary me!",
								"Well there's nothing I can do about it",
								"You will have to go back to them i'm afraid");
						}
						break;
					case 3:
						npcsay(p, n, "Ah the warrior returns",
							"Have you found a way into Gu'Tanoth yet ?");
						say(p, n, "I can't get past the guards");
						npcsay(p, n, "Well, ogres dislike others apart from their kind",
							"What you need is some form of proof of friendship",
							"Something to trick them into believing you are their friend",
							"...Which shouldn't be too hard considering their intelligence!");
						if (!p.getCarriedItems().hasCatalogID(ItemId.OGRE_RELIC.id(), Optional.empty())) {
							int lostRelicMenu = multi(p, n,
								"I have lost the relic you gave me",
								"I will find my way in, no problem");
							if (lostRelicMenu == 0) {
								npcsay(p, n, "What! lost the relic ? How careless!",
									"It's a good job I copied that design then...",
									"You can take this copy instead, its just as good");
								give(p, ItemId.OGRE_RELIC.id(), 1);
							} else if (lostRelicMenu == 1) {
								npcsay(p, n, "Yes, I'm sure you will...good luck");
							}
						}
						break;
					case 4:
						npcsay(p, n, "How is the quest going ?");
						say(p, n, "I have worked out the guard's puzzle");
						npcsay(p, n, "My my! a wordsmith as well as a hero!");
						int mymyMenu = multi(p, n,
							"I am still trying to navigate the skavid caves",
							"I am trying to get into the shaman's cave",
							"It is going well");
						if (mymyMenu == 0) {
							npcsay(p, n, "Take some illumination with you or else it will be dark!");
						} else if (mymyMenu == 1) {
							npcsay(p, n, "Yes it will be well-guarded",
								"Hmmm, let me see...",
								"Ah yes, I gather some ogres are allergic to certain herbs...",
								"Now what was it ?",
								"It had white berries and blue leaves.... I remember that!",
								"You should try looking through some of the caves...");
						} else if (mymyMenu == 2) {
							npcsay(p, n, "Thats good to hear",
								"We are much closer to fixing the tower now");
						}
						break;
					case 5:
						npcsay(p, n, "Hello again, how do you fare?");
						int questMenu5 = multi(p, n, false, //do not send over
							"It goes well, I can now navigate the skavid caves",
							"I had a crystal but I lost it",
							"I am now ready for the shaman");
						if (questMenu5 == 0) {
							say(p, n, "It goes well, I can now navigate the skavid caves");
							npcsay(p, n, "That is good news",
								"Let me know if you find anything of interest...");
						} else if (questMenu5 == 1) {
							say(p, n, "I had a crystal, but I lost it");
							npcsay(p, n, "Oh no, well you had better go back there again then!");
						} else if (questMenu5 == 2) {
							say(p, n, "I am now ready for the shaman");
							npcsay(p, n, "Remember all I told you, you must distract the guard somehow",
								"The herbs with blue leaves and berries is what you are looking for",
								"This herb is very poisonous however, handle it carefully",
								"Also, be on your guard in that cave",
								"Who know what monsters may be present in that awful place");
						}
						break;
					case 6:
						say(p, n, "I have found the cave of ogre shaman",
							"But I cannot touch them!");
						npcsay(p, n, "That is because of their magical powers",
							"We must fight them with their own methods",
							"Do not speak to them!",
							"I suggest a potion...",
							"Collect some guam leaves",
							"and some jangerberries",
							"And mix in some ground bat bones",
							"It is essential to return it to me before you use it",
							"So I can empower it with my magic",
							"Be very careful how you mix it, its extremely volatile",
							"Mixing ingredients of this type in the wrong order can cause explosions!",
							"I hope you've been brushing up in herblaw and magic ?",
							"I must warn you that only experienced magicians can use this potion",
							"It is too dangerous in the hands of the unskilled...");
						p.updateQuestStage(this, 7);
						break;
					case 7:
						npcsay(p, n, "Any more news ?");
						if (p.getCarriedItems().hasCatalogID(ItemId.OGRE_POTION.id(), Optional.of(false))) {
							say(p, n, "Yes I have made the potion");
							npcsay(p, n, "That's great news, let me infuse it with magic...");
							p.message("The wizard mutters strange words over the liquid");
							p.getCarriedItems().remove(new Item(ItemId.OGRE_POTION.id()));
							give(p, ItemId.MAGIC_OGRE_POTION.id(), 1);
							if (p.getQuestStage(Quests.WATCHTOWER) == 7) {
								p.updateQuestStage(Quests.WATCHTOWER, 8);
							}
							npcsay(p, n, "Here it is, a dangerous substance",
								"I must remind you that this potion can only be used",
								"If your magic ability is high enough");
						} else {
							say(p, n, "Can you tell me again what I need for the potion ?");
							npcsay(p, n, "Yes indeed, you need some guam leaves,",
								"Jangerberries and ground bat bones",
								"Then the potion can be powered with magic",
								"And the ogre shaman can be destroyed");
						}
						break;
					case 8:
						npcsay(p, n, "Hello again",
							"Did the potion work ?");
						say(p, n, "I am still working to rid us of these shaman...");
						npcsay(p, n, "May you have sucess in your task");
						int qMenu = multi(p, n,
							"I had another crystal but I lost it",
							"I am looking for another crystal",
							"I have found another crystal!");
						if (qMenu == 0) {
							npcsay(p, n, "Oh really ?",
								"It's probably been dropped in the shaman cave",
								"Go and have a good search that area again");
						} else if (qMenu == 1) {
							npcsay(p, n, "I am sure the cave holds the final one",
								"Look for the source of the shaman power...");
							say(p, n, "Okay I will go and have a look");
						} else if (qMenu == 2) {
							npcsay(p, n, "Good, let's have it here...");
						}
						break;
					case 9:
						npcsay(p, n, "Hello again",
							"Did the potion work ?");
						say(p, n, "Indeed it did!",
							"I wiped out those ogre shaman!",
							"I am looking for another crystal");
						npcsay(p, n, "I am sure the cave holds the final one",
							"Look for the source of the shaman power...");
						say(p, n, "Okay I will go and have a look");
						break;
				}
			}
			switch (cID) {
				case WatchTowerWizard.SEARCHINGTHECAVES:
					npcsay(p, n, "It's no good searching the caves",
						"Well, not yet anyway");
					say(p, n, "Why not ?");
					npcsay(p, n, "They are deep and complex",
						"The only way you will navigate the caves is to have a map or something",
						"It may be that the ogres have one");
					say(p, n, "And how do you know that ?");
					npcsay(p, n, "Well... I don't");
					int m2 = multi(p, n, false, //do not send over
						"So what do I do ?",
						"I wont bother then");
					if (m2 == 0) {
						say(p, n, "So what do I do ?");
						npcsay(p, n, "You need to be fearless",
							"And gain entrance to Gu'Tanoth the city of ogres",
							"And find out how to navigate the caves");
						say(p, n, "That sounds scary");
						npcsay(p, n, "Ogres are nasty creatures yes",
							"Only a strong warrior, and a clever one at that",
							"Can get the better of the ogres...");
						say(p, n, "What do I need to do to get into the city");
						npcsay(p, n, "Well the guards need to be dealt with",
							"You could start by checking out the ogre settlements around here",
							"Tribal ogres often hate their neighbours...");
						p.updateQuestStage(this, 2);
					} else if (m2 == 1) {
						say(p, n, "I won't bother then");
						npcsay(p, n, "Won't bother, won't bother ?",
							"...Perhaps this quest is too hard for you");
						p.message("The wizard walks away");
					}
					break;
			}
		}
	}

	private void ogreSpawnAndAttack(Player p, Npc n) {
		addnpc(p.getWorld(), NpcId.OGRE_GENERAL.id(), p.getX(), p.getY(), 60000 * 3);
		delay(1600);
		Npc ogre = ifnearvisnpc(p, NpcId.OGRE_GENERAL.id(), 4);
		if (ogre != null) {
			ogre.startCombat(p);
		}
	}

	class WatchTowerWizard {
		static final int SEARCHINGTHECAVES = 0;
	}
}
