package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.*;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Arrays;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Observatory implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	UseLocTrigger {

	private int selectedNumber = 0;

	@Override
	public int getQuestId() {
		return Quests.OBSERVATORY_QUEST;
	}

	@Override
	public String getQuestName() {
		return "Observatory quest (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.OBSERVATORY_QUEST.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.OBSERVATORY_QUEST.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : Arrays.stream(reward.getXpRewards()).filter(x -> !x.getSkill().equals(Skills.NONE)).toArray(XPReward[]::new)) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.getCache().remove("keep_key_gate");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.OBSERVATORY_ASSISTANT.id() || n.getID() == NpcId.OBSERVATORY_PROFESSOR.id() || n.getID() == NpcId.PROFESSOR.id()
			|| n.getID() == NpcId.GOBLIN_GUARD.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.PROFESSOR.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "Hello friend", "This is my poorly telescope",
						"It's been tampered with and is not working", "If your good at crafting",
						"I would appreciate your help!", "Come to the reception if you can");
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					npcsay(player, n, "Hello friend", "I hope you get all the parts soon",
						"Return to the reception", "When you have the things I need");
					break;
				case 6:
					npcsay(player, n, "Hello friend", "It's time to use the telescope");
					break;
				case -1:
					npcsay(player, n, "Hello friend", "The stars hold many secrets",
						"The moon rises in Scorpio...");
					break;
			}
		}
		else if (n.getID() == NpcId.OBSERVATORY_ASSISTANT.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "Hello wanderer",
						"Do you require any assistance ?");
					int first = multi(player, n, "Yes, what do you two do here ?",
						"No, just looking around thanks",
						"Can I have a look through that telescope ?");
					if (first == 0) {
						npcsay(player, n, "This is the observatory reception",
							"Up on the cliff is the observatory dome",
							"From here we view the heavens",
							"That is before the telescope was damaged",
							"By those monsters outside...");
					} else if (first == 1) {
						npcsay(player, n, "Okay, be my guest",
							"If you need any help let me know...");
					} else if (first == 2) {
						npcsay(player, n, "I'm sorry but it's broken!",
							"The Professor will explain if you speak to him");
					}
					break;
				case 1:
					npcsay(player, n, "How can I help you ?");
					int help = multi(player, n, "I can't find any planks!",
						"I dont need any help thanks");
					if (help == 0) {
						npcsay(player,
							n,
							"I understand planks can be found at the barbarian outpost",
							"To the north east of ardougne",
							"You will probably have to trek over there to find some...");
					} else if (help == 1) {
						npcsay(player, n, "Oh, okay then if you are sure");
						player.message("The assistant continues with his work");
					}
					break;
				case 2:
					npcsay(player, n, "How can I help you ?");
					int bronze = multi(player, n, "I can't see any bronze around",
						"I dont need any help thanks");
					if (bronze == 0) {
						npcsay(player,
							n,
							"You'll need to mix purified copper and tin together",
							"To produce this metal");
					} else if (bronze == 1) {
						npcsay(player, n, "Oh, okay then if you are sure");
						player.message("The assistant continues with his work");
					}
					break;
				case 3:
					npcsay(player, n, "How can I help you ?");
					int molten = multi(player, n,
						"I'm having problems getting glass",
						"I don't need any help thanks");
					if (molten == 0) {
						npcsay(player, n, "Don't you know how to make glass ?",
							"Unfortunately we dont have those skills",
							"I remember reading about that somewhere...");
					} else if (molten == 1) {
						npcsay(player, n, "Oh, okay then if you are sure");
						player.message("The assistant continues with his work");
					}
					break;
				case 4:
					npcsay(player, n, " How can I help you ?");
					int mould = multi(player, n, "I cant find the lens mould",
						"I don't need any help thanks");
					if (mould == 0) {
						npcsay(player,
							n,
							"Can't you find the mould ?",
							"I'm sure I heard one of those goblins talking about it...",
							"I bet they have hidden it somewhere");
					} else if (mould == 1) {
						npcsay(player, n, "Oh, okay then if you are sure");
						player.message("The assistant continues with his work");
					}
					break;
				case 5:
					npcsay(player, n, "How can I help you ?");
					int lens = multi(player, n, "I can't make the lens!",
						"I don't need any help thanks");
					if (lens == 0) {
						npcsay(player, n, "Crafting objects like this requires skill",
							"You may need to practice more first...");
					} else if (lens == 1) {
						npcsay(player, n, "Oh, okay then if you are sure");
						player.message("The assistant continues with his work");
					}
					break;
				case 6:
					npcsay(player, n, "Well hello again",
						"thanks for helping out the professor",
						"You've made my life much easier!",
						"Have a drink on me!");
					player.message("The assistant gives you some wine");
					say(player, n, "Thanks very much");
					give(player, ItemId.WINE.id(), 1);
					break;
				case -1:
					if (!player.getCache().hasKey("observatory_assistant_drink")) {
						npcsay(player, n, "Well hello again",
							"thanks for helping out the professor",
							"You've made my life much easier!",
							"Have a drink on me!");
						player.message("The assistant gives you some wine");
						give(player, ItemId.WINE.id(), 1);
						say(player, n, "Thanks very much");
						player.getCache().store("observatory_assistant_drink", true);
						return;
					}
					npcsay(player, n, "Thanks again");
					break;
			}
		}
		else if (n.getID() == NpcId.OBSERVATORY_PROFESSOR.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "Hello adventurer",
						"What brings you to these parts ?");
					int first = multi(player, n, false, //do not send over
						"I am lost!!!",
						"I'd like to have a look through that telescope",
						"Whats the ladder over there for ?",
						"It is of no concern of yours...");
					if (first == 0) {
						say(player, n, "I am lost!!!");
						npcsay(player,
							n,
							"Lost ? it must have been those gnomes that have lead you astray",
							"Head North-East to find the land Ardougne");
						say(player, n, "I'm sure I'll find the way",
							"Thanks for your help");
						npcsay(player, n, "No problem at all, come and visit again");
					} else if (first == 1) {
						say(player, n, "I'd like to have a look through that telescope");
						npcsay(player, n, "So would I !!",
							"The trouble is, its not working");
						say(player, n, "What do you mean ?");
						npcsay(player, n, "Did you see those houses outside ?");
						say(player, n, "Yes, I've seen them");
						npcsay(player,
							n,
							"Well it's a family of goblins",
							"Since they moved here they cause me nothing but trouble",
							"Last week my telescope was tampered with",
							"And now parts need replacing before it can be used again",
							"Err, I don't suppose you would be willing to help?");
						int second = multi(player, n,
							"Sounds interesting, what can I do for you ?",
							"Oh sorry, I don't have time for that");
						if (second == 0) {
							npcsay(player,
								n,
								"Oh thanks so much!",
								"I need three new parts for the telescope so it can be used again",
								"I need wood to make a new tripod",
								"Bronze to make a new tube",
								"And glass for a replacement lens",
								"My assistant will help you obtaining these",
								"Ask him if you need any help");
							say(player, n, "Okay what do I need to do ?");
							npcsay(player, n,
								"First I need three planks of wood for the tripod");
							player.updateQuestStage(getQuestId(), 1);
						} else if (second == 1) {
							npcsay(player, n, "Oh dear, I really do need some help",
								"If you see anyone who can help please send them my way");
							player.message("The Professor carries on with his duties");
						}

					} else if (first == 2) {
						say(player, n, "What's the ladder there for ?");
						npcsay(player, n,
							"The ladder leads to the entrance of the cavern",
							"That leads from here to the observatory");
					} else if (first == 3) {
						say(player, n, "It is of no concern of yours...");
						npcsay(player, n, "Okay Okay, there's no need to be insulting!");
						player.message("The professor carries on with his studies");
					}
					break;
				case 1:
					npcsay(player, n, "I'ts my helping hand back again!",
						"Do you have the planks yet ?");
					int planks = multi(player, n, "Yes I've got them",
						"No, sorry not yet");
					if (planks == 0) {
						if (player.getCarriedItems().getInventory().countId(ItemId.PLANK.id()) >= 3) {
							npcsay(player,
								n,
								"Well done, I can start the tripod construction now",
								"Now for the bronze");
							for (int i = 0; i < 3; i++) {
								player.getCarriedItems().remove(new Item(ItemId.PLANK.id()));
							}

							player.updateQuestStage(getQuestId(), 2);
						} else {
							npcsay(player, n, "You don't seem to have enough planks!",
								"I need three in total");
						}
					} else if (planks == 1) {
						npcsay(player, n, "Oh dear, well please bring them soon");
					}
					break;
				case 2:
					npcsay(player, n, " Hello again, do you have the bronze yet ?");
					int bronze = multi(player, n, "Yes I have it",
						"I'm still looking");
					if (bronze == 0) {
						if (player.getCarriedItems().getInventory().countId(ItemId.BRONZE_BAR.id()) >= 1) {
							npcsay(player, n, "Great, now all I need is the lens made",
								"Next on the list is molten glass");
							player.getCarriedItems().remove(new Item(ItemId.BRONZE_BAR.id()));

							player.updateQuestStage(getQuestId(), 3);
						} else {
							npcsay(player, n, "That's not bronze!",
								"Please bring me some");
						}
					} else if (bronze == 1) {
						npcsay(player, n, "Please carry on trying to find some");
					}
					break;
				case 3:
					npcsay(player, n, "How are you getting on finding me some glass ?");
					int molten = multi(player, n, "Here it is!",
						"No luck yet I'm afraid");
					if (molten == 0) {
						if (player.getCarriedItems().getInventory().countId(ItemId.MOLTEN_GLASS.id()) >= 1) {
							npcsay(player,
								n,
								"Excellent! now all I need is to make the lens",
								"Oh no, I can't use this glass!",
								"Until I find the lens mould used to cast it");
							say(player, n, "What do you mean, lens mould");
							npcsay(player, n, "I need my lens mould",
								"Without it I'll never get the correct shape",
								"I'll have to ask you to try and find it");
							player.updateQuestStage(getQuestId(), 4);
						} else {
							npcsay(player, n,
								"Sorry, you don't have any glass with you",
								"Please don't tease me, I really need this part!");
						}
					} else if (molten == 1) {
						npcsay(player, n, "I hope you find some soon");
					}
					break;
				case 4:
					npcsay(player, n, "Did you bring me the mould ?");
					int mould = multi(player, n, "Yes, I've managed to find it",
						"I haven't found it yet", "I had it then lost it");
					if (mould == 0) {
						if (player.getCarriedItems().getInventory().countId(ItemId.LENS_MOULD.id()) >= 1) {
							npcsay(player, n,
								"At last you've brought all the items I need",
								"To repair the telescope",
								"Oh no! I can't do this");
							say(player, n, "What do you mean ?");
							npcsay(player, n, "My crafting skill is not good enough",
								"To finish this off",
								"Are you skilled at crafting ?");
							int craft = multi(player, n,
								"Yes I have much experience in crafting",
								"No sorry I'm not good at that");
							if (craft == 0) {
								npcsay(player, n, "Thank goodness for that!",
									"You can use the mould with molten glass",
									"To make a new lens",
									"As long as you have practised your crafting skills");
								player.updateQuestStage(getQuestId(), 5);
							} else if (craft == 1) {
								npcsay(player, n,
									"Oh dear, without the lens its useless",
									"Maybe you'll find someone who can Finish the job for you ?");
								player.updateQuestStage(getQuestId(), 5);
							}
						} else {
							npcsay(player,
								n,
								"Where is the mould! You dont even have it on you",
								"Please try and find it");
						}
					} else if (mould == 1) {
						npcsay(player, n, "Perhaps the goblins have stolen it ?");
					} else if (mould == 2) {
						npcsay(player, n, "Well, I wouldn't worry",
							"No doubt the goblins copied the design",
							"I'm sure if you checked again",
							"You'll find another one");
					}
					break;
				case 5:
					npcsay(player, n, "Is the lens finished ?");
					int finished = multi(player, n, "Yes here it is",
						"I haven't finished it yet");
					if (finished == 0) {
						if (player.getCarriedItems().getInventory().countId(ItemId.LENS.id()) >= 1) {
							player.getCarriedItems().remove(new Item(ItemId.LENS.id()));
							npcsay(player, n,
								"Wonderful, at last I can fix the telescope");
							if (player.getCarriedItems().hasCatalogID(ItemId.LENS_MOULD.id(), Optional.of(false))) {
								npcsay(player, n,
									"I'll take back that mould for use again");
								player.getCarriedItems().remove(new Item(ItemId.LENS_MOULD.id()));
							}
							npcsay(player, n, "Meet me at the Observatory later...");
							player.updateQuestStage(getQuestId(), 6);
						} else {
							npcsay(player, n, "Why do you tell lies ?",
								"Please come back when the lens is made");
						}
					} else if (finished == 1) {
						npcsay(player, n, "Oh, okay please hurry");
					}

					break;
				case 6:
					npcsay(player, n, "The telescope is now repaired",
						"Let's go to the Observatory");
					break;
				case -1:
					npcsay(player, n, "Aha, my friend returns",
						"Thanks for all your help with the telescope",
						"What can I do for you ?");
					int completedQuest = multi(player, n, false, //do not send over
						"Do you have any more quests", "Nothing, thanks");
					if (completedQuest == 0) {
						say(player, n, "Do you have any more quests ?");
						npcsay(player, n, "No I'm all out of quests now",
							"But the stars may hold a secret for you...");
					} else if (completedQuest == 1) {
						say(player, n, "Nothing, thanks");
						npcsay(player, n, "Okay no problem");
					}
					break;
			}
		} else if (n.getID() == NpcId.GOBLIN_GUARD.id()) {
			npcsay(player, n, "What are you doing here ?",
				"This is our domain now",
				"Begone foul human!");
			n.startCombat(player);
		}
	} // DUNGEON SPIDER 656 poison

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return DataConversions.inArray(new int[] {928, 937, 936, 929, 917, 930, 919, 935, 934, 927, 925}, obj.getID())
				|| (obj.getID() == 926 && obj.getX() == 689 && obj.getY() == 3513);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 928) {
			if (player.getQuestStage(getQuestId()) == 0) {
				player.teleport(712, 3512, false);
				player.message("You climb down the ladder");
				return;
			}
			if (player.getQuestStage(getQuestId()) == 6
				|| player.getQuestStage(getQuestId()) == -1) {
				player.teleport(712, 3512, false);
				return;
			}
			if (player.getQuestStage(getQuestId()) >= 1
				|| player.getQuestStage(getQuestId()) <= 5) {
				Npc assistant = ifnearvisnpc(player, NpcId.OBSERVATORY_ASSISTANT.id(), 6);
				if (assistant != null) {
					npcsay(player, assistant, "Take great care down there",
						"Remember the goblins have taken over the cavern");
					say(player, assistant, "Oh, okay thanks for the warning");
					player.teleport(712, 3512, false);
				}
				return;
			}
		}
		else if (obj.getID() == 937) {
			player.message("You open the chest");
			player.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), 936, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 936) {
			player.message("You search the chest");
			player.message("The chest contains nothing");
			player.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), 937, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 929) {
			player.message("You open the chest");
			player.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), 917, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 917) {
			player.message("You search the chest");
			player.message("The chest contains a poisonous spider!");
			Npc spider = addnpc(player.getWorld(), NpcId.DUNGEON_SPIDER.id(), obj.getX(), obj.getY(), 120000);
			spider.setChasing(player);
			player.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 929, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 930) {
			player.message("You open the chest");
			player.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 919, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 919) { // KEY CHEST FOUND!
			player.message("You search the chest");
			player.message("You find a small key inside");
			if (player.getCarriedItems().hasCatalogID(ItemId.KEEP_KEY.id(), Optional.of(false))) {
				mes("You already have a keep key");
				delay(3);
				mes("Another one will have no use");
				delay(3);
			} else {
				give(player, ItemId.KEEP_KEY.id(), 1);
			}
			player.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 930, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 935) {
			player.message("You open the chest");
			player.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 934, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 934) { // POISON CURE FOUND!
			player.message("You search the chest");
			player.message("The chest contains some poison cure");
			give(player, ItemId.ONE_CURE_POISON_POTION.id(), 1);
			player.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 935, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 926 && obj.getX() == 689 && obj.getY() == 3513) { // 690
			// 3514
			if (player.getCache().hasKey("keep_key_gate")
				|| player.getQuestStage(getQuestId()) == -1) {
				if (player.getY() <= 3513) {
					player.teleport(690, 3514, false);
					say(player, null, "I'd better be quick",
						"There may be more guards about");
				} else {
					player.message("you go through the gate");
					player.teleport(690, 3513, false);
				}
			} else {
				player.message("The gate is locked");
			}
		}
		else if (obj.getID() == 927) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.LENS_MOULD.id(), Optional.of(false))) {
				player.message("Underneath you find a peculiar mould");
				give(player, ItemId.LENS_MOULD.id(), 1);
			} else {
				player.message("You already have this lens mould");
				player.message("Another one will be of no use");
			}
		}
		else if (obj.getID() == 925) {
			if (player.getQuestStage(getQuestId()) == -1) {
				Npc professor = ifnearvisnpc(player, NpcId.PROFESSOR.id(), 10);
				if (professor != null) {
					player.message("You look through the telescope");
					constellation(player, player.getQuestStage(getQuestId()));
					int completedQuest = multi(player, professor,
						"I can see a constellation through the telescope",
						"I see something, but I don't know what it is");
					if (completedQuest == 0) {
						npcsay(player, professor,
							"Yes, I feel the stars have a message for you...");
					} else if (completedQuest == 1) {
						npcsay(player, professor, "With time you may come to learn",
							"The secrets of the stars");
					}
				}
				return;
			} else if (player.getQuestStage(getQuestId()) == 6) {
				Npc professor = ifnearvisnpc(player, NpcId.PROFESSOR.id(), 10);
				if (professor != null) {
					npcsay(player, professor, "Well done, well done!!",
						"Let's see what the stars have in store for us today");
					player.message("You look through the telescope");
					constellation(player, player.getQuestStage(getQuestId()));
					int telescop = multi(player, professor,
						"I can see a constellation", "What am I looking at ?");
					if (telescop == 0) {
						npcsay(player, professor, "Yes, with this device",
							"The heavens are opened to us...",
							"The constellation you saw was");
						constellationNameAndReward(player, professor);
						player.sendQuestComplete(Quests.OBSERVATORY_QUEST);
						npcsay(player, professor, "By Saradomin's earlobes!",
							"You must be a friend of the gods indeed");
						player.message("Well done, you have completed the Observatory quest");
						npcsay(player, professor,
							"Look in your backpack for your reward",
							"In payment for your work");
						player.message("After repairing the telescope you feel more knowledgable in the skill of crafting");
						npcsay(player, professor, "Now I have work to do...");
						player.message("The professor goes about his business");
					} else if (telescop == 1) {
						npcsay(player, professor, "This is the revealed sky",
							"The constellation you saw was");
						constellationNameAndReward(player, professor);
						player.sendQuestComplete(Quests.OBSERVATORY_QUEST);
						npcsay(player, professor, "By Saradomin's earlobes!",
							"You must be a friend of the gods indeed");
						player.message("Well done, you have completed the Observatory quest");
						npcsay(player, professor,
							"Look in your backpack for your reward",
							"In payment for your work");
						player.message("After repairing the telescope you feel more knowledgable in the skill of crafting");
						npcsay(player, professor, "Now I have work to do...");
						player.message("The professor goes about his business");
					}
				}
			} else {
				player.message("It seems that the telescope is not operational");
				constellation(player, player.getQuestStage(getQuestId()));
			}
		}
	}

	private void giveOptionalReward(Player player, Npc n, Skill skill) {
		final XPReward origXpReward = Arrays.stream(Quest.OBSERVATORY_QUEST.reward().getXpRewards()).filter(x -> !x.getSkill().equals(Skills.CRAFTING)).toArray(XPReward[]::new)[0];
		XPReward xpReward = origXpReward.copyTo(skill);
		incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
	}

	private void constellationNameAndReward(Player player, Npc n) {
		if (selectedNumber == 0) {
			npcsay(player, n, "Virgo the virtuous",
				"The strong and peaceful nature of virgo boosts your defence");
			giveOptionalReward(player, n, Skill.DEFENSE);
		} else if (selectedNumber == 1) {
			npcsay(player, n, "Libra the scales",
				"The scales of justice award you with Law Runes");
			give(player, ItemId.LAW_RUNE.id(), 3);
		} else if (selectedNumber == 2) {
			npcsay(player, n, "Gemini the twins",
				"The double nature of Gemini awards you a two-handed weapon");
			give(player, ItemId.BLACK_2_HANDED_SWORD.id(), 1);
		} else if (selectedNumber == 3) {
			npcsay(player, n, "Pisces the fish",
				"The gods rain food from the sea on you");
			give(player, ItemId.TUNA.id(), 3);
		} else if (selectedNumber == 4) {
			npcsay(player, n, "Taurus the bull",
				"You are given the strength of a bull");
			give(player, ItemId.FULL_SUPER_STRENGTH_POTION.id(), 1);
		} else if (selectedNumber == 5) {
			npcsay(player, n, "Aquarius the water-bearer",
				"the Gods of water award you with water runes");
			give(player, ItemId.WATER_RUNE.id(), 25);
		} else if (selectedNumber == 6) {
			npcsay(player, n, "Scorpio the scorpion",
				"The scorpion gives you poison from it's sting");
			give(player, ItemId.WEAPON_POISON.id(), 1);
		} else if (selectedNumber == 7) {
			npcsay(player, n, "Aries the ram",
				"The ram's strength improves your attack abilites");
			giveOptionalReward(player, n, Skill.ATTACK);
		} else if (selectedNumber == 8) {
			npcsay(player, n, "Sagittarius the Centaur",
				"The Gods award you a maple longbow");
			give(player, ItemId.MAPLE_LONGBOW.id(), 1);
		} else if (selectedNumber == 9) {
			npcsay(player, n, "Leo the lion",
				"The power of the lion has increased your hitpoints");
			giveOptionalReward(player, n, Skill.HITS);
		} else if (selectedNumber == 10) {
			npcsay(player, n, "Capricorn the goat",
				"you are granted an increase in strength");
			giveOptionalReward(player, n, Skill.STRENGTH);
		} else if (selectedNumber == 11) {
			npcsay(player, n, "Cancer the crab",
				"The armoured crab gives you an amulet of protection");
			give(player, ItemId.EMERALD_AMULET_OF_PROTECTION.id(), 1);
		}
		// all constellations give uncut sapphire
		give(player, ItemId.UNCUT_SAPPHIRE.id(), 1);
	}

	private void constellation(Player player, int stage) {
		selectedNumber = 0;

		// quest completed, always show scorpion
		if (stage == -1) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                                 *                                                                                                                               *                                                                                                      *                                                                                                                     *                    *                                                                                                                                                                                                                                                     *                                                                                         *                                                                                                                                                                                                                                *              *                                                                                                    *                    *                                                                                                   *        *                                                                           ", true);
			return;
		}
		// show no image on telescope at this stage
		else if (stage < 6) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      ", true);
			return;
		}

		int random = DataConversions.random(0, 11);
		if (random == 0) { //
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                    *                                                                                               *                                                                                                                             *                                   *                                                                                                               *                  *                                                                     *         *                                                                                                                                                 *     *                                                                                  *                          *                                                                                                                    *                                                                                                    *                                                                                                                                 *                                                          ", true);
		} else if (random == 1) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                                *                                                                                            *                                                                                                                           *             *                                                                                                                                                                                                                                                                    *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               *                                                                                                                                                                                                                             *                                                    ", true);
		} else if (random == 2) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                 *                                                                                                                                 *                                                                                                        *                                                                                                                                                                                                                                                 *                        *                                                                                                                               *                                                                                                                                                                                                                        *                                                                                                                              *                                                                                                                  *                                                                                                                                      *                                     ", true);
		} else if (random == 3) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                              *                                                                                                                    *                                                                                                                                                                                                                                                 *                                                                                                                                                                                                                                            *                                                                                                                                                                                                                                           *                                                                                                                                                                                                                                           *          *                   *                                                                                                                                       *             *                                                                                                                   *                                                                                                                                   *                                                                                             *        *                          ", true);
		} else if (random == 4) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                       *                                                                                                                                                                                                                                               *                                                                                                                                                                                                                                                                          *                                                                                                                    *                                                                                                                              *                                                                                                                                          *                                                                                                               *           *                                                                                                                                                                                                                                   *                                                                                                                                                                                                                                         *                                    ", true);
		} else if (random == 5) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                      *                                                                                                                      *                                                                                                                                          *                                                                                                *                                    *                                                                                              *                                                                                                                                                                                                                       *                                                                                                                *                                                                                                                            *                                                                      ", true);
		} else if (random == 6) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                                 *                                                                                                                               *                                                                                                      *                                                                                                                     *                    *                                                                                                                                                                                                                                                     *                                                                                         *                                                                                                                                                                                                                                *              *                                                                                                    *                    *                                                                                                   *        *                                                                           ", true);
		} else if (random == 7) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             *                                                                                                                                     *                                                                                                                             *                                                                                            *                                                                                                                *                                                                                                                                                       *                            ", true);
		} else if (random == 8) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                        *  *                                                                                                                                         *                                                                                                                   *                                                                                                             *                                                                                                                                                                                                                                                               *                                                                                                      *                                                                                                                                                *                                                                                                                                                                                                                                        *                                                                                 *                                                              ", true);
		} else if (random == 9) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                                   *                                                                                                             *                                                                                                        *                              *                                                                                                              *                                                                                          *                                                                                                                                        *             *                                                                                                                                                                                                                               *                                                                                                                                           *                                                                                                                               *                                 ", true);
		} else if (random == 10) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                                               *                                                                                                                   *                                                                                         *                                                                                                                  *                *                                                                                                                                                                                                                                                                  *                                                                                                 *                                                                                                                                                                                                                                                     *       *                                               ", true);
		} else if (random == 11) {
			ActionSender.sendBox(player, "                                                                                                                                                                                                                                                                                                           *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   *                                                                                                                                                                                                                                               *                                                                                                                                                                                                                                                                                                                                                                   *                                                                                                                                                                                                                                                                 *                                 ", true);
		}
		selectedNumber = random;
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 926 && item.getCatalogId() == ItemId.KEEP_KEY.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 926 && item.getCatalogId() == ItemId.KEEP_KEY.id()) {
			player.message("The gate unlocks");
			player.message("The keep key is broken - I'll discard it");
			player.getCarriedItems().remove(new Item(ItemId.KEEP_KEY.id()));
			if (!player.getCache().hasKey("keep_key_gate")) {
				player.getCache().store("keep_key_gate", true);
			}
			Npc guard = ifnearvisnpc(player, NpcId.GOBLIN_GUARD.id(), 5);
			if (guard != null) {
				guard.setChasing(player);
			}
		}

	}
}
