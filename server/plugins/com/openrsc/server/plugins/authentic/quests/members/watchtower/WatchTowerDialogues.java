package com.openrsc.server.plugins.authentic.quests.members.watchtower;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.custom.minigames.CombatOdyssey;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class WatchTowerDialogues implements QuestInterface, TalkNpcTrigger, UseNpcTrigger {

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
	public int getQuestPoints() {
		return Quest.WATCHTOWER.reward().getQuestPoints();
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
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(), NpcId.WATCHTOWER_WIZARD.id(), NpcId.GREW.id(), NpcId.OG.id(), NpcId.TOBAN.id(), NpcId.OGRE_CITIZEN.id(),
				NpcId.OGRE_TRADER_FOOD.id(), NpcId.OGRE_GUARD_CAVE_ENTRANCE.id(), NpcId.OGRE_TRADER_ROCKCAKE.id(), NpcId.CITY_GUARD.id(),
				NpcId.SKAVID_FINALQUIZ.id(), NpcId.SKAVID_IG.id(), NpcId.SKAVID_AR.id(), NpcId.SKAVID_CUR.id(), NpcId.SKAVID_NOD.id(), NpcId.SKAVID_INITIAL.id());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SKAVID_FINALQUIZ.id()) {
			if (player.getCache().hasKey("skavid_completed_language") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
				npcsay(player, n, "What, you gots the crystal...");
				int lastMenu = multi(player, n, "But I've lost it!", "Oh okay then");
				if (lastMenu == 0) {
					if (player.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL2.id(), Optional.empty()) || player.getQuestStage(Quests.WATCHTOWER) == -1) {
						npcsay(player, n, "I have no more for you!");
					} else {
						npcsay(player, n, "All right, take this one then...");
						player.message("The skavid gives you a crystal");
						give(player, ItemId.POWERING_CRYSTAL2.id(), 1);
					}
				} else if (lastMenu == 1) {
					npcsay(player, n, "I'll be on my way then");
				}
			} else if (player.getCache().hasKey("language_cur")
				&& player.getCache().hasKey("language_ar")
				&& player.getCache().hasKey("language_ig")
				&& player.getCache().hasKey("language_nod")) {
				String[] sayChat = {"Cur tanath...", "Ar cur...", "Bidith Ig...", "Gor nod..."};
				int randomizeChat = DataConversions.random(0, sayChat.length - 1);
				npcsay(player, n, sayChat[randomizeChat]);
				int menu = multi(player, n, "Cur", "Ar", "Bidith", "Tanath", "Gor");
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
						npcsay(player, n, "Heh-heh! So you speak a little skavid eh?",
							"I'm impressed, here take this prize...");
						player.message("The skavid gives you a large crystal");
						give(player, ItemId.POWERING_CRYSTAL2.id(), 1);
						if (player.getCache().hasKey("language_cur")
							&& player.getCache().hasKey("language_ar")
							&& player.getCache().hasKey("language_ig")
							&& player.getCache().hasKey("language_nod")) {
							player.getCache().remove("language_cur");
							player.getCache().remove("language_ar");
							player.getCache().remove("language_ig");
							player.getCache().remove("language_nod");
							player.getCache().remove("skavid_started_language");
							player.getCache().store("skavid_completed_language", true);
						}
					} else if (menu == 1) {
						npcsay(player, n, "Grrr!");
						player.message("It seems your response has upset the skavid");
					} else {
						npcsay(player, n, "???");
						player.message("The response was wrong");
					}
				}
			} else {
				npcsay(player, n, "Tanath Gor Ar Bidith ?");
				say(player, n, "???");
				player.message("You cannot communicate with the skavid");
				player.message("It seems you haven't learned enough of thier language yet...");
			}
		}
		else if (inArray(n.getID(), NpcId.SKAVID_IG.id(), NpcId.SKAVID_AR.id(), NpcId.SKAVID_CUR.id(), NpcId.SKAVID_NOD.id())) {
			// replays not showing what happens immediately afte completing language but very likely them saying Ar cur! up to and incl. after quest
			if (n.getID() == NpcId.SKAVID_IG.id()) {
				if (player.getCache().hasKey("skavid_completed_language") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
					npcsay(player, n, "Ar cur!");
					player.message("You have already learned the skavid language");
					return;
				} else if (player.getCache().hasKey("language_ig")) {
					npcsay(player, n, "Bidith Ig...");
					player.message("You have already talked to this skavid");
					return;
				}
				npcsay(player, n, "Cur bidith...");
			} else if (n.getID() == NpcId.SKAVID_AR.id()) {
				if (player.getCache().hasKey("skavid_completed_language") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
					npcsay(player, n, "Ar cur!");
					player.message("You have already learned the skavid language");
					return;
				} else if (player.getCache().hasKey("language_ar")) {
					npcsay(player, n, "Ar cur...");
					player.message("You have already talked to this skavid");
					return;
				}
				npcsay(player, n, "Gor cur...");
			} else if (n.getID() == NpcId.SKAVID_CUR.id()) {
				if (player.getCache().hasKey("skavid_completed_language") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
					npcsay(player, n, "Ar cur!");
					player.message("You have already learned the skavid language");
					return;
				} else if (player.getCache().hasKey("language_cur")) {
					npcsay(player, n, "Cur tanath...");
					player.message("You have already talked to this skavid");
					return;
				}
				npcsay(player, n, "Bidith tanath...");
			} else if (n.getID() == NpcId.SKAVID_NOD.id()) {
				if (player.getCache().hasKey("skavid_completed_language") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
					npcsay(player, n, "Ar cur!");
					player.message("You have already learned the skavid language");
					return;
				} else if (player.getCache().hasKey("language_nod")) {
					npcsay(player, n, "Gor nod...");
					player.message("You have already talked to this skavid");
					return;
				}
				npcsay(player, n, "Tanath gor...");
			}
			if (player.getCache().hasKey("skavid_started_language")) {
				player.message("The skavid is trying to communicate...");
				boolean correctWord = false;
				int learnMenu = multi(player, n, "Cur", "Ar", "Ig", "Nod", "Gor");
				if (learnMenu == 0) {
					if (n.getID() == NpcId.SKAVID_CUR.id()) {
						npcsay(player, n, "Cur",
							"Cur tanath");
						player.getCache().store("language_cur", true);
						correctWord = true;
					}
				} else if (learnMenu == 1) {
					if (n.getID() == NpcId.SKAVID_AR.id()) {
						npcsay(player, n, "Ar",
							"Ar cur");
						player.getCache().store("language_ar", true);
						correctWord = true;
					}
				} else if (learnMenu == 2) {
					if (n.getID() == NpcId.SKAVID_IG.id()) {
						npcsay(player, n, "Ig",
							"Bidith Ig");
						player.getCache().store("language_ig", true);
						correctWord = true;
					}
				} else if (learnMenu == 3) {
					if (n.getID() == NpcId.SKAVID_NOD.id()) {
						npcsay(player, n, "Nod",
							"Gor nod");
						player.getCache().store("language_nod", true);
						correctWord = true;
					}
				}

				if (learnMenu != -1) {
					if (correctWord) {
						player.message("It seems the skavid understood you");
					} else {
						npcsay(player, n, "???");
						mes("It seems that was the wrong reply");
						delay(3);
					}
				}
			} else {
				say(player, n, "???");
				player.message("The skavid is trying to communicate...");
				player.message("You don't know any skavid words yet!");
			}
		}

		else if (n.getID() == NpcId.SKAVID_INITIAL.id()) {
			if (player.getQuestStage(Quests.WATCHTOWER) == -1) {
				npcsay(player, n, "Ah master...",
					"You did well to master our language...");
				return;
			}
			if ((player.getCache().hasKey("language_cur")
				&& player.getCache().hasKey("language_ar")
				&& player.getCache().hasKey("language_ig")
				&& player.getCache().hasKey("language_nod")) || player.getCache().hasKey("skavid_completed_language")) {
				npcsay(player, n, "Master, my kinsmen tell me you have learned skavid",
					"You should speak to the mad ones in their cave...");
				return;
			} else if (player.getCache().hasKey("skavid_started_language")) {
				npcsay(player, n, "Master, how are you doing learning our language ?");
				say(player, n, "I am studying the speech of your kind...");
			} else {
				npcsay(player, n, "Tanath cur, tanath cur");
				say(player, n, "???");
				npcsay(player, n, "Don't hurt me, don't hurt me!");
				say(player, n, "Stop moaning creature",
					"I know about you skavids",
					"You serve those monsters the ogres");
				npcsay(player, n, "Please dont touch me!");
				say(player, n, "You have something that belongs to me...");
				npcsay(player, n, "I don't have anything, please believe me!");
				say(player, n, "Somehow I find your words hard to believe");
				npcsay(player, n, "I'm begging your kindness, I don't have it!");
				int menu = multi(player, n, false, //do not send over
					"I don't believe you hand it over!",
					"Okay okay i'm not going to hurt you");
				if (menu == 0) {
					say(player, n, "I don't believe you, hand it over!");
					npcsay(player, n, "Ahhhhh, help!");
					player.message("The skavid runs away...");
					delnpc(n, true);
					say(player, n, "Oh great...I've scared it off!");
				} else if (menu == 1) {
					say(player, n, "Okay, okay i'm not going to hurt you");
					npcsay(player, n, player.getText("WatchTowerSkavidThankYouKindHuman"),
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
					player.getCache().store("skavid_started_language", true);
					player.updateQuestStage(Quests.WATCHTOWER, 5);
				}
			}
		}

		else if (n.getID() == NpcId.WATCHTOWER_WIZARD.id()) {
			watchtowerWizardDialogue(player, n, -1);
		}
		else if (n.getID() == NpcId.OGRE_CITIZEN.id()) {
			npcsay(player, n, "Uh ? what are you doing here ?");
		}
		else if (n.getID() == NpcId.OGRE_TRADER_FOOD.id()) {
			npcsay(player, n, "Grrr, little animal.. I shall destroy you!");
			n.startCombat(player);
		}
		else if (n.getID() == NpcId.OGRE_GUARD_CAVE_ENTRANCE.id()) {
			if (player.getQuestStage(Quests.WATCHTOWER) >= 0 && player.getQuestStage(Quests.WATCHTOWER) < 5) {
				npcsay(player, n, "Stop bothering me minion!");
			} else if (player.getQuestStage(Quests.WATCHTOWER) != -1) {
				npcsay(player, n, "What do you want ?");
				int menu = multi(player, n,
					"I want to go in there",
					"I want to rid the world of ogres");
				if (menu == 0) {
					npcsay(player, n, "Oh you do, do you ?",
						"How about no ?");
					n.startCombat(player);
				} else if (menu == 1) {
					npcsay(player, n, "You dare mock me creature!!!");
					n.startCombat(player);
				}
			} else {
				player.message("The guard is occupied at the moment");
			}
		}
		else if (n.getID() == NpcId.OGRE_TRADER_ROCKCAKE.id()) {
			npcsay(player, n, "Arr, small thing wants my food does it ?",
				"I'll teach you to deal with ogres!");
			n.startCombat(player);
		}
		else if (n.getID() == NpcId.CITY_GUARD.id()) {
			if (player.getCache().hasKey("city_guard_riddle") && player.getCache().getBoolean("city_guard_riddle")) {
				npcsay(player, n, "What is it ?");
				int menu = multi(player, n,
					"Do you have any other riddles for me ?",
					"I have lost the map you gave me");
				if (menu == 0) {
					npcsay(player, n, "Yes, what looks good on a plate with salad ?");
					int subMenu = multi(player, n,
						"I don't know...",
						"A nice pizza ?");
					if (subMenu == 0) {
						npcsay(player, n, "You!!!",
							"Now go and bother me no more...");
					} else if (subMenu == 1) {
						npcsay(player, n, "Grr.. think you are a comedian eh ?",
							"Get lost!");
					}
				} else if (menu == 1) {
					if (player.getCarriedItems().hasCatalogID(ItemId.SKAVID_MAP.id(), Optional.of(false))) {
						npcsay(player, n, "Are you blind ? what is that you are carrying ?");
						say(player, n, "Oh, that map....");
					} else {
						npcsay(player, n, "What's the point ? take this copy and bother me no more!");
						give(player, ItemId.SKAVID_MAP.id(), 1);
					}
				}
			} else {
				npcsay(player, n, "Grrrr, what business have you here ?");
				say(player, n, "I am on an errand...");
				npcsay(player, n, "So what do you want with me ?");
				int menu = multi(player, n, "I am an ogre killer come to destroy you!",
					"I seek passage into the skavid caves");
				if (menu == 0) {
					npcsay(player, n, "I would like to see you try!");
					n.startCombat(player);
				} else if (menu == 1) {
					npcsay(player, n, "Is that so...",
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
					// player got the riddle
					player.getCache().store("city_guard_riddle", false);
				}
			}
		}
		else if (n.getID() == NpcId.GREW.id()) {
			switch (player.getQuestStage(this)) {
				case -1:
					if (config().WANT_COMBAT_ODYSSEY
						&& CombatOdyssey.getCurrentTier(player) == 2
						&& CombatOdyssey.isTierCompleted(player)) {
						if (CombatOdyssey.biggumMissing()) return;
						int newTier = 3;
						CombatOdyssey.assignNewTier(player, newTier);
						npcsay(player, n, "So the morsel returns",
							"The sorceror asked me to give you this if you made it this far");
						CombatOdyssey.giveRewards(player, n);
						npcsay(player, n, "The morsel is meant to kill these things");
						npcsay(player, n, player.getWorld().getCombatOdyssey().getTier(newTier).getTasksAndCounts());
						npcsay(player, n, "If the morsel manages this without being eaten",
							"then the morsel should see the dark mage in the city north");
						return;
					}
					player.message("The ogre is not interested in you anymore");
					break;
				case 0:
				case 1:
					player.message("The ogre has nothing to say at the moment...");
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					if (player.getCache().hasKey("ogre_relic_part_2")) {
						npcsay(player, n, "What are you doing here morsel ?");
						int menu = multi(player, n,
							"Can I do anything else for you ?",
							"I've lost the relic part you gave me",
							"I've lost the crystal you gave me");
						if (menu == 0) {
							npcsay(player, n, "I have nothing left for you but the cooking pot!");
						} else if (menu == 1) {
							if (!player.getCarriedItems().hasCatalogID(ItemId.OGRE_RELIC_PART_BASE.id(), Optional.empty())) {
								npcsay(player, n, "Stupid morsel, I have another",
									"Take it and go now before I lose my temper");
								give(player, ItemId.OGRE_RELIC_PART_BASE.id(), 1);
							} else {
								npcsay(player, n, "You lie to me morsel!");
							}
						} else if (menu == 2) {
							if (!player.getCarriedItems().hasCatalogID(ItemId.POWERING_CRYSTAL1.id(), Optional.empty())) {
								npcsay(player, n, "I suppose you want another ?",
									"I suppose just this once I could give you my copy...");
								give(player, ItemId.POWERING_CRYSTAL1.id(), 1);
							} else {
								npcsay(player, n, "How dare you lie to me Morsel!",
									"I will finish you now!");
							}
						}
					} else {
						if (player.getCache().hasKey("ogre_grew")) {
							toothDialogue(player, n);
						} else {
							npcsay(player, n, "What do you want tiny morsel ?",
								"You would look good on my plate");
							say(player, n, "I want to enter the city of ogres");
							npcsay(player, n, "Perhaps I should eat you instead ?");
							int menu = multi(player, n,
								"Don't eat me, I can help you",
								"You will have to kill me first");
							if (menu == 0) {
								npcsay(player, n, "What can a morsel like you do for me ?");
								say(player, n, "I am a mighty adventurer",
									"Slayer of monsters and user of magic powers");
								npcsay(player, n, "Well well, perhaps the morsel can help after all...",
									"If you think you're tough",
									"Find Gorad my enemy in the south east settlement",
									"And knock one of his teeth out!",
									"Heheheheh!");
								player.getCache().store("ogre_grew", true);
							} else if (menu == 1) {
								npcsay(player, n, "That can be arranged - guards!!");
								ogreSpawnAndAttack(player, n);
							}
						}
					}
					break;
			}
		}
		else if (n.getID() == NpcId.OG.id()) {
			switch (player.getQuestStage(this)) {
				case -1:
					player.message("The ogre is not interested in you anymore");
					break;
				case 0:
				case 1:
					player.message("He's busy, try him later");
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					if (player.getCache().hasKey("ogre_relic_part_3")) {
						npcsay(player, n, "It's the little rat again");
						int menu = multi(player, n,
							"Do you have any other tasks for me ?",
							"I have lost the relic part you gave me");
						if (menu == 0) {
							npcsay(player, n, "No, I have no more tasks for you, now go away");
						} else if (menu == 1) {
							if (!player.getCarriedItems().hasCatalogID(ItemId.OGRE_RELIC_PART_HEAD.id(), Optional.empty())) {
								npcsay(player, n, "Grrr, why do I bother ?",
									"It's a good job I have another part!");
								give(player, ItemId.OGRE_RELIC_PART_HEAD.id(), 1);
							} else {
								npcsay(player, n, "Are you blind! I can see you have it even from here!");
							}
						}
					} else {
						if (player.getCache().hasKey("ogre_og")) {
							stolenGoldDialogue(player, n);
						} else {
							npcsay(player, n, "Why are you here little rat ?");
							int menu = multi(player, n,
								"I seek entrance to the city of ogres",
								"I have come to kill you");
							if (menu == 0) {
								npcsay(player, n, "You have no business there!",
									"Just a minute...maybe if you did something for me I might help you get in...");
								say(player, n, "What can I do to help an ogre ?");
								npcsay(player, n, "South East of here there is another settlement",
									"The name of the chieftan is Toban",
									"He stole some gold from me",
									"And I want it back!",
									"Here is a key to the chest it's in",
									"If you bring it here",
									"I may reward you...");
								give(player, ItemId.KEY.id(), 1);
								player.getCache().store("ogre_og", true);
							} else if (menu == 1) {
								npcsay(player, n, "Kill me eh ?",
									"you shall be crushed like the vermin you are!",
									"Guards!!");
								ogreSpawnAndAttack(player, n);
							}
						}
					}
					break;
			}
		}
		else if (n.getID() == NpcId.TOBAN.id()) {
			switch (player.getQuestStage(this)) {
				case -1:
					player.message("The ogre is not interested in you anymore");
					break;
				case 0:
				case 1:
					player.message("He is busy at the moment...");
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					if (player.getCache().hasKey("ogre_relic_part_1")) {
						npcsay(player, n, "The small thing returns, what do you want now ?");
						int subMenu = multi(player, n,
							"I seek another task",
							"I can't find the relic part you gave me");
						if (subMenu == 0) {
							npcsay(player, n, "Have you arrived for dinner ?",
								"Ha ha ha! begone small thing!");
						} else if (subMenu == 1) {
							if (!player.getCarriedItems().hasCatalogID(ItemId.OGRE_RELIC_PART_BODY.id(), Optional.empty())) {
								npcsay(player, n, "Small thing, how could you be so careless ?",
									"Here, take this one");
								give(player, ItemId.OGRE_RELIC_PART_BODY.id(), 1);
							} else {
								npcsay(player, n, "Small thing, you lie to me!",
									"I always says that small things are big trouble...");
							}
						}
					} else {
						if (player.getCache().hasKey("ogre_toban")) {
							dragonBoneDialogue(player, n);
						} else {
							npcsay(player, n, "What do you want small thing ?");
							int menu = multi(player, n,
								"I seek entrance to the city of ogres",
								"Die creature");
							if (menu == 0) {
								npcsay(player, n, "Ha ha ha! you'll never get in there");
								say(player, n, "I fear not for that city");
								npcsay(player, n, "Bold words for a thing so small");
								int subMenu = multi(player, n,
									"I could do something for you...",
									"Die creature");
								if (subMenu == 0) {
									npcsay(player, n, "Ha ha ha! this creature thinks it can help me!",
										"I would eat you now, but for your puny size",
										"Prove to me your might",
										"Bring me the bones of a dragon to chew on",
										"And I may spare you from a painful death");
									player.getCache().store("ogre_toban", true);
								} else if (subMenu == 1) {
									npcsay(player, n, "Ha ha ha! it thinks it's a match for toban does it ?");
									n.startCombat(player);
								}
							} else if (menu == 1) {
								npcsay(player, n, "Ha ha ha! it thinks it's a match for toban does it ?");
								n.startCombat(player);
							}
						}
					}
					break;
			}
		}
	}

	private void fingerNailsDialogue(Player player, Npc n) {
		if (player.getQuestStage(this) == 1) {
			say(player, n, "Have a look at these");
			player.getCarriedItems().remove(new Item(ItemId.FINGERNAILS.id()));
			npcsay(player, n, "Interesting, very interesting",
				"Long nails...grey in colour",
				"Well chewed...",
				"Of course, they belong to a skavid");
			say(player, n, "A skavid ?");
			npcsay(player, n, "A servant race to the ogres",
				"Gray depressed looking creatures",
				"Always loosing nails, teeth and hair",
				"They inhabit the caves in the mendip hills",
				"They normally keep to themselves though",
				"It's unusual for them to venture from their caves");
			int m = multi(player, n,
				"What do you suggest that I do ?",
				"Shall I search the caves ?");
			if (m == 0) {
				watchtowerWizardDialogue(player, n, WatchTowerWizard.SEARCHINGTHECAVES);
			} else if (m == 1) {
				watchtowerWizardDialogue(player, n, WatchTowerWizard.SEARCHINGTHECAVES);
			}
		} else {
			player.message("The wizard has no need for more evidence");
		}
	}

	private void stolenGoldDialogue(Player player, Npc n) {
		npcsay(player, n, "Where is my gold from that traitor toban?");
		int subMenu = multi(player, n,
			"I have your gold",
			"I haven't got it yet",
			"I have lost the key!");
		if (subMenu == 0) {
			if (player.getCarriedItems().hasCatalogID(ItemId.STOLEN_GOLD.id(), Optional.of(false))) {
				npcsay(player, n, "Well well, the little rat has got it!",
					"take this to show the little rat is a friend to the ogres",
					"Hahahahaha!");
				player.getCarriedItems().remove(new Item(ItemId.STOLEN_GOLD.id()));
				player.message("The ogre gives you part of a horrible statue");
				give(player, ItemId.OGRE_RELIC_PART_HEAD.id(), 1);
				if (player.getCache().hasKey("ogre_og")) {
					player.getCache().remove("ogre_og");
				}
				if (!player.getCache().hasKey("ogre_relic_part_3")) {
					/** Very strange setup of quest tbh, but that's what it is **/
					player.getCache().store("ogre_relic_part_3", true);
				}
			} else {
				npcsay(player, n, "That is not what I want rat!",
					"If you want to impress me",
					"Then get the gold I asked for!");
			}
		} else if (subMenu == 1) {
			npcsay(player, n, "Don't come back until you have it",
				"Unless you want to be on tonight's menu!");
		} else if (subMenu == 2) {
			if (player.getCarriedItems().hasCatalogID(ItemId.KEY.id(), Optional.of(false))) {
				npcsay(player, n, "Oh yeah! what's that then ?");
				player.message("It seems you still have the key...");
			} else {
				npcsay(player, n, "Idiot! take another and don't lose it!");
				give(player, ItemId.KEY.id(), 1);
			}
		}
	}

	private void toothDialogue(Player player, Npc n) {
		npcsay(player, n, "The morsel is back",
			"Does it have our tooth for us ?");
		if (player.getCarriedItems().hasCatalogID(ItemId.OGRE_TOOTH.id(), Optional.of(false))) {
			say(player, n, "I have it");
			npcsay(player, n, "It's got it, good good",
				"That should annoy gorad wonderfully",
				"Heheheheh!");
			player.getCarriedItems().remove(new Item(ItemId.OGRE_TOOTH.id()));
			npcsay(player, n, "Heres a token of my gratitude");
			give(player, ItemId.OGRE_RELIC_PART_BASE.id(), 1);
			npcsay(player, n, "Some old gem I stole from Gorad...",
				"And an old part of a statue",
				"Heheheheh!");
			player.message("The ogre hands you a large crystal");
			player.message("The ogre gives you part of a statue");
			give(player, ItemId.POWERING_CRYSTAL1.id(), 1);
			if (player.getCache().hasKey("ogre_grew")) {
				player.getCache().remove("ogre_grew");
			}
			if (!player.getCache().hasKey("ogre_relic_part_2")) {
				player.getCache().store("ogre_relic_part_2", true);
			}
		} else {
			say(player, n, "Err, I don't have it");
			npcsay(player, n, "Morsel, you dare to return without the tooth!",
				"Either you are a fool, or want to be eaten!");
		}
	}

	private void dragonBoneDialogue(Player player, Npc n) {
		npcsay(player, n, "Ha ha ha! small thing returns",
			"Did you bring the dragon bone ?");
		if (player.getCarriedItems().hasCatalogID(ItemId.DRAGON_BONES.id(), Optional.of(false))) {
			say(player, n, "When I say I will get something I get it!");
			player.getCarriedItems().remove(new Item(ItemId.DRAGON_BONES.id()));
			npcsay(player, n, "Ha ha ha! small thing has done it",
				"Toban is glad, take this...");
			player.message("The ogre gives you part of a statue");
			give(player, ItemId.OGRE_RELIC_PART_BODY.id(), 1);
			if (player.getCache().hasKey("ogre_toban")) {
				player.getCache().remove("ogre_toban");
			}
			if (!player.getCache().hasKey("ogre_relic_part_1")) {
				player.getCache().store("ogre_relic_part_1", true);
			}
		} else {
			say(player, n, "I have nothing for you");
			npcsay(player, n, "Then you shall get nothing from me!");
		}
	}

	private void watchtowerWizardDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.WATCHTOWER_WIZARD.id()) {
			if (cID == -1) {
				switch (player.getQuestStage(this)) {
					case -1:
						if (player.getCache().hasKey("watchtower_scroll")) {
							npcsay(player, n, "Greetings friend",
								"I trust all is well with you ?",
								"Yanilee is safe at last!");
						} else {
							npcsay(player, n, "Hello again adventurer",
								"Thanks again for your help in keeping us safe");
							int finish = multi(player, n,
								"I lost the scroll you gave me",
								"That's okay");
							if (finish == 0) {
								if (!player.getBank().hasItemId(ItemId.SPELL_SCROLL.id()) && !player.getCarriedItems().hasCatalogID(ItemId.SPELL_SCROLL.id())) {
									npcsay(player, n, "Never mind, have another...");
									give(player, ItemId.SPELL_SCROLL.id(), 1);
								} else if (player.getBank().hasItemId(ItemId.SPELL_SCROLL.id())) {
									//maybe non-kosher message though it was also bank restricted
									npcsay(player, n, "Ho ho ho! a comedian to the finish!",
										"There it is, in your bank!");
								} else {
									npcsay(player, n, "Ho ho ho! a comedian to the finish!",
										"There it is, in your backpack!");
								}
							} else if (finish == 1) {
								npcsay(player, n, "We are always in your debt...");
							}
						}
						break;
					case 0:
						npcsay(player, n, "Oh my Oh my!");
						int menu = multi(player, n,
							"What's the matter ?",
							"You wizards are always complaining");
						if (menu == 0) {
							npcsay(player, n, "Oh dear oh dear",
								"Darn and drat",
								"We try hard to keep this town protected",
								"But how can we do that when the watchtower isn't working ?");
							say(player, n, "What do you mean it isn't working ?");
							npcsay(player, n, "The watchtower here works by the power of a magical device",
								"An ancient spell designed to ward off ogres",
								"That has been in place here for many moons",
								"The exact knowledge of the spell is lost to us now",
								"But the essence of the spell",
								"Has been infused into 4 powering crystals",
								"To keep the tower protected from the hordes in the mendips...");
							int menu2 = multi(player, n,
								"So how come the spell dosen't work ?",
								"I'm not interested in the rantings of an old wizard");
							if (menu2 == 0) {
								npcsay(player, n, "The crystals! the crystals!",
									"They have been taken!");
								say(player, n, "Taken...");
								npcsay(player, n, "Stolen!");
								say(player, n, "Stolen...");
								npcsay(player, n, "Yes, yes! do I have to repeat myself ?");
								player.message("The wizard seems very stressed...");
								int menu3 = multi(player, n,
									"Can I be of help ?",
									"I'm not sure I can help",
									"I'm not interested");
								if (menu3 == 0) {
									npcsay(player, n, "Help ?",
										"Oh wonderful dear traveller",
										"Yes I could do with an extra pair of eyes here");
									say(player, n, "???");
									npcsay(player, n, "There must be some evidence of what has happened somewhere",
										"Perhaps you could assist me in searching for clues");
									say(player, n, "I would be happy to");
									npcsay(player, n, "Try searching the surrounding area");
									/** QUEST START - STAGE 1 **/
									player.updateQuestStage(this, 1);
								} else if (menu3 == 1) {
									npcsay(player, n, "Oh dear what am I to do ?",
										"The safety of this whole area is in jeopardy!");
								} else if (menu3 == 2) {
									npcsay(player, n, "That's typical nowadays",
										"Its left to us wizards to do all the work...");
									player.message("The wizard is not impressed");
								}
							} else if (menu2 == 1) {
								player.message("The wizard gives you a suspicious look");
							}
						} else if (menu == 1) {
							npcsay(player, n, "Complaining ?.... complaining !",
								"What folks these days don't realize",
								"Is that if it wasn't for us wizards",
								"This entire world would be overrun",
								"With every creature that walks this world!");
							player.message("The wizard angrily walks away");
						}
						break;
					case 1:
						npcsay(player, n, "Hello again",
							"Did you find anything of interest ?");
						if (player.getCarriedItems().hasCatalogID(ItemId.FINGERNAILS.id(), Optional.of(false))) {
							fingerNailsDialogue(player, n);
						}
						/** EASTER EGG? IN OFFICIAL RSC THE RELATED QUEST ITEMS WERE NOT CHECKED
						 * BUT INSTEAD THE REGULAR ONES (see wiki)
						 * **/
						else if (player.getCarriedItems().hasCatalogID(ItemId.EYE_PATCH.id(), Optional.of(false))
							|| player.getCarriedItems().hasCatalogID(ItemId.GOBLIN_ARMOUR.id(), Optional.of(false))
							|| player.getCarriedItems().hasCatalogID(ItemId.IRON_DAGGER.id(), Optional.of(false))
							|| player.getCarriedItems().hasCatalogID(ItemId.WIZARDS_ROBE.id(), Optional.of(false))) {
							if (player.getCarriedItems().hasCatalogID(ItemId.EYE_PATCH.id(), Optional.of(false))) {
								say(player, n, "I found this eye patch");
							} else if (player.getCarriedItems().hasCatalogID(ItemId.GOBLIN_ARMOUR.id(), Optional.of(false))) {
								say(player, n, "Have a look at this goblin armour");
							} else if (player.getCarriedItems().hasCatalogID(ItemId.IRON_DAGGER.id(), Optional.of(false))) {
								say(player, n, "I found a dagger");
							} else if (player.getCarriedItems().hasCatalogID(ItemId.WIZARDS_ROBE.id(), Optional.of(false))) {
								say(player, n, "I have this robe");
							}
							npcsay(player, n, "Let me see...",
								"No, sorry this is not evidence",
								"You need to keep searching im afraid");
						} else {
							say(player, n, "No nothing yet");
							npcsay(player, n, "Oh dear oh dear",
								"There must be something somewhere");
						}
						break;
					case 2:
						npcsay(player, n, "How's it going ?");
						int newM = multi(player, n,
							"I am having difficulty with the tribes",
							"I have everything under control",
							"I have lost something the ogres gave to me");
						if (newM == 0) {
							npcsay(player, n, "Talk to them face to face",
								"And don't show any fear",
								"Make sure you are rested and well-fed",
								"And fight the good fight!");
						} else if (newM == 1) {
							npcsay(player, n, "Good, good! I will expect the crystals back shortly then...");
						} else if (newM == 2) {
							npcsay(player, n, "Oh deary me!",
								"Well there's nothing I can do about it",
								"You will have to go back to them i'm afraid");
						}
						break;
					case 3:
						if (!player.getCache().hasKey("has_ogre_companionship") &&
							!player.getCache().hasKey("city_guard_riddle")) {
							npcsay(player, n, "Ah the warrior returns",
								"Have you found a way into Gu'Tanoth yet ?");
							say(player, n, "I can't get past the guards");
							npcsay(player, n, "Well, ogres dislike others apart from their kind",
								"What you need is some form of proof of friendship",
								"Something to trick them into believing you are their friend",
								"...Which shouldn't be too hard considering their intelligence!");
							if (!player.getCarriedItems().hasCatalogID(ItemId.OGRE_RELIC.id(), Optional.empty())) {
								int lostRelicMenu = multi(player, n,
									"I have lost the relic you gave me",
									"I will find my way in, no problem");
								if (lostRelicMenu == 0) {
									npcsay(player, n, "What! lost the relic ? How careless!",
										"It's a good job I copied that design then...",
										"You can take this copy instead, its just as good");
									give(player, ItemId.OGRE_RELIC.id(), 1);
								} else if (lostRelicMenu == 1) {
									npcsay(player, n, "Yes, I'm sure you will...good luck");
								}
							}
						} else if(player.getCache().hasKey("has_ogre_companionship") &&
							!player.getCache().hasKey("city_guard_riddle")) {
							npcsay(player, n, "How are you doing with the ogres ?");
							say(player, n, "I have gained entry to the city");
							npcsay(player, n, "Already ? excellent!");
							say(player, n, "I still can't navigate the skavid caves");
							npcsay(player, n, "You need a map of some kind...",
								"I bet one of the ogres has one");
							say(player, n, "Okay thanks, I'll go and find out");
						} else {
							npcsay(player, n, "How is the quest going ?");
							int puzzleMenu = multi(player, n,
								"Some of the city guards have set me a puzzle",
								"Can you tell me more about the city ?");
							if (puzzleMenu == 0) {
								npcsay(player, n, "Ummm is that so ?",
									"I can't help you there, I never was much good at puzzles...");
							} else if (puzzleMenu == 1) {
								npcsay(player, n, "Yes indeed, this city is very ancient",
									"It's not clear whether the ogres actually constructed it",
									"Or whether they took it over from another race",
									"What I can tell you is that the whole city is controlled",
									"By a group of ogre shaman");
								say(player, n, "Ogre shaman ?");
								npcsay(player, n, "Indeed, these ogres have harnessed the black arts...",
									"They wield great power");
								say(player, n, "They sound nasty!");
								npcsay(player, n, "Indeed they are, but you must confront them",
									"To break the power of the ogres they must be beaten!");
								int sMenu = multi(player, n, false, //do not send over
									"But I'm scared of those shaman!", "Leave it to me, I fear no ogre");
								if (sMenu == 0) {
									say(player, n, "But i'm scared of those shaman!");
									npcsay(player, n, "Scared ? to get this far and to falter now...",
										"Perchance you are not ready for the final challenge ?");
								} else if (sMenu == 1) {
									say(player, n, "Leave it to me, I fear no ogre");
									npcsay(player, n, "That's the spirit!",
										"May your search prove fruitful!");
								}
							}
						}
						break;
					case 4:
						npcsay(player, n, "How is the quest going ?");
						say(player, n, "I have worked out the guard's puzzle");
						npcsay(player, n, "My my! a wordsmith as well as a hero!");
						int mymyMenu = multi(player, n,
							"I am still trying to navigate the skavid caves",
							"I am trying to get into the shaman's cave",
							"It is going well");
						if (mymyMenu == 0) {
							npcsay(player, n, "Take some illumination with you or else it will be dark!");
						} else if (mymyMenu == 1) {
							npcsay(player, n, "Yes it will be well-guarded",
								"Hmmm, let me see...",
								"Ah yes, I gather some ogres are allergic to certain herbs...",
								"Now what was it ?",
								"It had white berries and blue leaves.... I remember that!",
								"You should try looking through some of the caves...");
						} else if (mymyMenu == 2) {
							npcsay(player, n, "Thats good to hear",
								"We are much closer to fixing the tower now");
						}
						break;
					case 5:
						npcsay(player, n, "Hello again, how do you fare?");
						int questMenu5 = multi(player, n, false, //do not send over
							"It goes well, I can now navigate the skavid caves",
							"I had a crystal but I lost it",
							"I am now ready for the shaman");
						if (questMenu5 == 0) {
							say(player, n, "It goes well, I can now navigate the skavid caves");
							npcsay(player, n, "That is good news",
								"Let me know if you find anything of interest...");
						} else if (questMenu5 == 1) {
							say(player, n, "I had a crystal, but I lost it");
							npcsay(player, n, "Oh no, well you had better go back there again then!");
						} else if (questMenu5 == 2) {
							say(player, n, "I am now ready for the shaman");
							npcsay(player, n, "Remember all I told you, you must distract the guard somehow",
								"The herbs with blue leaves and berries is what you are looking for",
								"This herb is very poisonous however, handle it carefully",
								"Also, be on your guard in that cave",
								"Who know what monsters may be present in that awful place");
						}
						break;
					case 6:
						say(player, n, "I have found the cave of ogre shaman",
							"But I cannot touch them!");
						npcsay(player, n, "That is because of their magical powers",
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
						player.updateQuestStage(this, 7);
						break;
					case 7:
						npcsay(player, n, "Any more news ?");
						if (player.getCarriedItems().hasCatalogID(ItemId.OGRE_POTION.id(), Optional.of(false))) {
							say(player, n, "Yes I have made the potion");
							npcsay(player, n, "That's great news, let me infuse it with magic...");
							player.message("The wizard mutters strange words over the liquid");
							player.getCarriedItems().remove(new Item(ItemId.OGRE_POTION.id()));
							give(player, ItemId.MAGIC_OGRE_POTION.id(), 1);
							if (player.getQuestStage(Quests.WATCHTOWER) == 7) {
								player.updateQuestStage(Quests.WATCHTOWER, 8);
							}
							npcsay(player, n, "Here it is, a dangerous substance",
								"I must remind you that this potion can only be used",
								"If your magic ability is high enough");
						} else {
							say(player, n, "Can you tell me again what I need for the potion ?");
							npcsay(player, n, "Yes indeed, you need some guam leaves,",
								"Jangerberries and ground bat bones",
								"Then the potion can be powered with magic",
								"And the ogre shaman can be destroyed");
						}
						break;
					case 8:
						npcsay(player, n, "Hello again",
							"Did the potion work ?");
						say(player, n, "I am still working to rid us of these shaman...");
						npcsay(player, n, "May you have sucess in your task");
						int qMenu = multi(player, n,
							"I had another crystal but I lost it",
							"I am looking for another crystal",
							"I have found another crystal!");
						if (qMenu == 0) {
							npcsay(player, n, "Oh really ?",
								"It's probably been dropped in the shaman cave",
								"Go and have a good search that area again");
						} else if (qMenu == 1) {
							npcsay(player, n, "I am sure the cave holds the final one",
								"Look for the source of the shaman power...");
							say(player, n, "Okay I will go and have a look");
						} else if (qMenu == 2) {
							npcsay(player, n, "Good, let's have it here...");
						}
						break;
					case 9:
						if (!player.getCache().hasKey("crystal_rock")) {
							npcsay(player, n, "Hello again",
								"Did the potion work ?");
							say(player, n, "Indeed it did!",
								"I wiped out those ogre shaman!",
								"I am looking for another crystal");
							npcsay(player, n, "I am sure the cave holds the final one",
								"Look for the source of the shaman power...");
							say(player, n, "Okay I will go and have a look");
						} else {
							npcsay(player, n, "Well, how did it go ?",
								"Have you found any more crystals ?");
							int rMenu = multi(player, n, false, //do not send over
								"I did have the crystal but I lost it",
								"I can't find any more crystals yet...",
								"Yes, here it is");
							if (rMenu == 0) {
								say(player, n, "I did have the crystal but I lost it");
								npcsay(player, n, "Dissappointing, dissappointing...",
									"Well there's not much I can do...",
									"You had better go back and search the area again");
							} else if (rMenu == 1) {
								say(player, n, "I can't find any more crystals yet...");
								npcsay(player, n, "The rock of the shaman is the key",
									"I understand their power is linked to it in some way",
									"You may need something heavy to crack this boulder...");
							} else if (rMenu == 2) {
								say(player, n, "Yes, here it is!");
								npcsay(player, n, "Wonderful!",
									"Show it to me so I can confirm it's the real thing...");
							}
						}
						break;
					case 10:
						npcsay(player, n, "The system is not activated yet",
							"Throw the switch to start it...");
						break;
				}
			}
			switch (cID) {
				case WatchTowerWizard.SEARCHINGTHECAVES:
					npcsay(player, n, "It's no good searching the caves",
						"Well, not yet anyway");
					say(player, n, "Why not ?");
					npcsay(player, n, "They are deep and complex",
						"The only way you will navigate the caves is to have a map or something",
						"It may be that the ogres have one");
					say(player, n, "And how do you know that ?");
					npcsay(player, n, "Well... I don't");
					int m2 = multi(player, n, false, //do not send over
						"So what do I do ?",
						"I wont bother then");
					if (m2 == 0) {
						say(player, n, "So what do I do ?");
						npcsay(player, n, "You need to be fearless",
							"And gain entrance to Gu'Tanoth the city of ogres",
							"And find out how to navigate the caves");
						say(player, n, "That sounds scary");
						npcsay(player, n, "Ogres are nasty creatures yes",
							"Only a strong warrior, and a clever one at that",
							"Can get the better of the ogres...");
						say(player, n, "What do I need to do to get into the city");
						npcsay(player, n, "Well the guards need to be dealt with",
							"You could start by checking out the ogre settlements around here",
							"Tribal ogres often hate their neighbours...");
						player.updateQuestStage(this, 2);
					} else if (m2 == 1) {
						say(player, n, "I won't bother then");
						npcsay(player, n, "Won't bother, won't bother ?",
							"...Perhaps this quest is too hard for you");
						player.message("The wizard walks away");
					}
					break;
			}
		}
	}

	private void ogreSpawnAndAttack(Player player, Npc n) {
		addnpc(player.getWorld(), NpcId.OGRE_GENERAL.id(), player.getX(), player.getY(), 60000 * 3);
		delay(3);
		Npc ogre = ifnearvisnpc(player, NpcId.OGRE_GENERAL.id(), 4);
		if (ogre != null) {
			ogre.startCombat(player);
		}
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.WATCHTOWER_WIZARD.id() && item.getCatalogId() == ItemId.FINGERNAILS.id()) {
			fingerNailsDialogue(player, npc);
		} else if (npc.getID() == NpcId.OG.id() && item.getCatalogId() == ItemId.STOLEN_GOLD.id()) {
			if (player.getCache().hasKey("ogre_og") || player.getCache().hasKey("ogre_relic_part_3")) {
				stolenGoldDialogue(player, npc);
			} else {
				// needs checking
				player.message("Nothing interesting happens");
			}
		} else if (npc.getID() == NpcId.GREW.id() && item.getCatalogId() == ItemId.OGRE_TOOTH.id()) {
			if (player.getCache().hasKey("ogre_relic_part_2")) {
				say(player, null, "I am not sure giving him another tooth will have any purpose");
			} else if (player.getCache().hasKey("ogre_grew")) {
				toothDialogue(player, npc);
			} else {
				// needs checking
				player.message("Nothing interesting happens");
			}
		} else if (npc.getID() == NpcId.TOBAN.id() && item.getCatalogId() == ItemId.DRAGON_BONES.id()) {
			if (player.getCache().hasKey("ogre_toban") || player.getCache().hasKey("ogre_relic_part_1")) {
				dragonBoneDialogue(player, npc);
			} else {
				// needs checking
				player.message("Nothing interesting happens");
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return !item.getNoted() && (npc.getID() == NpcId.WATCHTOWER_WIZARD.id() && item.getCatalogId() == ItemId.FINGERNAILS.id()
			|| npc.getID() == NpcId.OG.id() && item.getCatalogId() == ItemId.STOLEN_GOLD.id()
			|| npc.getID() == NpcId.GREW.id() && item.getCatalogId() == ItemId.OGRE_TOOTH.id()
			|| npc.getID() == NpcId.TOBAN.id() && item.getCatalogId() == ItemId.DRAGON_BONES.id());
	}

	class WatchTowerWizard {
		static final int SEARCHINGTHECAVES = 0;
	}
}
