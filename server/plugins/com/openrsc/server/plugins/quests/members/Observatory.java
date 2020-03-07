package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

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
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("@gre@You haved gained 2 quest points!");
		int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.OBSERVATORY_QUEST);
		questData[Quests.MAPIDX_SKILL] = Skills.CRAFTING;
		incQuestReward(p, questData, true);
		p.getCache().remove("keep_key_gate");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.OBSERVATORY_ASSISTANT.id() || n.getID() == NpcId.OBSERVATORY_PROFESSOR.id() || n.getID() == NpcId.PROFESSOR.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.PROFESSOR.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcsay(p, n, "Hello friend", "This is my poorly telescope",
						"It's been tampered with and is not working", "If your good at crafting",
						"I would appreciate your help!", "Come to the reception if you can");
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					npcsay(p, n, "Hello friend", "I hope you get all the parts soon",
						"Return to the reception", "When you have the things I need");
					break;
				case 6:
					npcsay(p, n, "Hello friend", "It's time to use the telescope");
					break;
				case -1:
					npcsay(p, n, "Hello friend", "The stars hold many secrets",
						"The moon rises in Scorpio...");
					break;
			}
		}
		else if (n.getID() == NpcId.OBSERVATORY_ASSISTANT.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcsay(p, n, "Hello wanderer",
						"Do you require any assistance ?");
					int first = multi(p, n, "Yes, what do you two do here ?",
						"No, just looking around thanks",
						"Can I have a look through that telescope ?");
					if (first == 0) {
						npcsay(p, n, "This is the observatory reception",
							"Up on the cliff is the observatory dome",
							"From here we view the heavens",
							"That is before the telescope was damaged",
							"By those monsters outside...");
					} else if (first == 1) {
						npcsay(p, n, "Okay, be my guest",
							"If you need any help let me know...");
					} else if (first == 2) {
						npcsay(p, n, "I'm sorry but it's broken!",
							"The Professor will explain if you speak to him");
					}
					break;
				case 1:
					npcsay(p, n, "How can I help you ?");
					int help = multi(p, n, "I can't find any planks!",
						"I dont need any help thanks");
					if (help == 0) {
						npcsay(p,
							n,
							"I understand planks can be found at the barbarian outpost",
							"To the north east of ardougne",
							"You will probably have to trek over there to find some...");
					} else if (help == 1) {
						npcsay(p, n, "Oh, okay then if you are sure");
						p.message("The assistant continues with his work");
					}
					break;
				case 2:
					npcsay(p, n, "How can I help you ?");
					int bronze = multi(p, n, "I can't see any bronze around",
						"I dont need any help thanks");
					if (bronze == 0) {
						npcsay(p,
							n,
							"You'll need to mix purified copper and tin together",
							"To produce this metal");
					} else if (bronze == 1) {
						npcsay(p, n, "Oh, okay then if you are sure");
						p.message("The assistant continues with his work");
					}
					break;
				case 3:
					npcsay(p, n, "How can I help you ?");
					int molten = multi(p, n,
						"I'm having problems getting glass",
						"I don't need any help thanks");
					if (molten == 0) {
						npcsay(p, n, "Don't you know how to make glass ?",
							"Unfortunately we dont have those skills",
							"I remember reading about that somewhere...");
					} else if (molten == 1) {
						npcsay(p, n, "Oh, okay then if you are sure");
						p.message("The assistant continues with his work");
					}
					break;
				case 4:
					npcsay(p, n, " How can I help you ?");
					int mould = multi(p, n, "I cant find the lens mould",
						"I don't need any help thanks");
					if (mould == 0) {
						npcsay(p,
							n,
							"Can't you find the mould ?",
							"I'm sure I heard one of those goblins talking about it...",
							"I bet they have hidden it somewhere");
					} else if (mould == 1) {
						npcsay(p, n, "Oh, okay then if you are sure");
						p.message("The assistant continues with his work");
					}
					break;
				case 5:
					npcsay(p, n, "How can I help you ?");
					int lens = multi(p, n, "I can't make the lens!",
						"I don't need any help thanks");
					if (lens == 0) {
						npcsay(p, n, "Crafting objects like this requires skill",
							"You may need to practice more first...");
					} else if (lens == 1) {
						npcsay(p, n, "Oh, okay then if you are sure");
						p.message("The assistant continues with his work");
					}
					break;
				case 6:
					npcsay(p, n, "Well hello again",
						"thanks for helping out the professor",
						"You've made my life much easier!",
						"Have a drink on me!");
					p.message("The assistant gives you some wine");
					say(p, n, "Thanks very much");
					give(p, ItemId.WINE.id(), 1);
					break;
				case -1:
					if (!p.getCache().hasKey("observatory_assistant_drink")) {
						npcsay(p, n, "Well hello again",
							"thanks for helping out the professor",
							"You've made my life much easier!",
							"Have a drink on me!");
						p.message("The assistant gives you some wine");
						give(p, ItemId.WINE.id(), 1);
						say(p, n, "Thanks very much");
						p.getCache().store("observatory_assistant_drink", true);
						return;
					}
					npcsay(p, n, "Thanks again");
					break;
			}
		}
		else if (n.getID() == NpcId.OBSERVATORY_PROFESSOR.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcsay(p, n, "Hello adventurer",
						"What brings you to these parts ?");
					int first = multi(p, n, "I am lost!!!",
						"I'd like to have a look through that telescope",
						"Whats the ladder over there for ?",
						"It is of no concern of yours...");
					if (first == 0) {
						npcsay(p,
							n,
							"Lost ? it must have been those gnomes that have lead you astray",
							"Head North-East to find the land Ardougne");
						say(p, n, "I'm sure I'll find the way",
							"Thanks for your help");
						npcsay(p, n, "No problem at all, come and visit again");
					} else if (first == 1) {
						npcsay(p, n, "So would I !!",
							"The trouble is, its not working");
						say(p, n, "What do you mean ?");
						npcsay(p, n, "Did you see those houses outside ?");
						say(p, n, "Yes, I've seen them");
						npcsay(p,
							n,
							"Well it's a family of goblins",
							"Since they moved here they cause me nothing but trouble",
							"Last week my telescope was tampered with",
							"And now parts need replacing before it can be used again",
							"Err, I don't suppose you would be willing to help?");
						int second = multi(p, n,
							"Sounds interesting, what can I do for you ?",
							"Oh sorry, I don't have time for that");
						if (second == 0) {
							npcsay(p,
								n,
								"Oh thanks so much!",
								"I need three new parts for the telescope so it can be used again",
								"I need wood to make a new tripod",
								"Bronze to make a new tube",
								"And glass for a replacement lens",
								"My assistant will help you obtaining these",
								"Ask him if you need any help");
							say(p, n, "Okay what do I need to do ?");
							npcsay(p, n,
								"First I need three planks of wood for the tripod");
							p.updateQuestStage(getQuestId(), 1);
						} else if (second == 1) {
							npcsay(p, n, "Oh dear, I really do need some help",
								"If you see anyone who can help please send them my way");
							p.message("The Professor carries on with his duties");
						}

					} else if (first == 2) {
						npcsay(p, n,
							"The ladder leads to the entrance of the cavern",
							"That leads from here to the observatory");
					} else if (first == 3) {
						npcsay(p, n, "Okay Okay, there's no need to be insulting!");
						p.message("The professor carries on with his studies");
					}
					break;
				case 1:
					npcsay(p, n, "I'ts my helping hand back again!",
						"Do you have the planks yet ?");
					int planks = multi(p, n, "Yes I've got them",
						"No, sorry not yet");
					if (planks == 0) {
						if (p.getCarriedItems().getInventory().countId(ItemId.PLANK.id()) >= 3) {
							npcsay(p,
								n,
								"Well done, I can start the tripod construction now",
								"Now for the bronze");
							p.getCarriedItems().remove(ItemId.PLANK.id(), 3);

							p.updateQuestStage(getQuestId(), 2);
						} else {
							npcsay(p, n, "You don't seem to have enough planks!",
								"I need three in total");
						}
					} else if (planks == 1) {
						npcsay(p, n, "Oh dear, well please bring them soon");
					}
					break;
				case 2:
					npcsay(p, n, " Hello again, do you have the bronze yet ?");
					int bronze = multi(p, n, "Yes I have it",
						"I'm still looking");
					if (bronze == 0) {
						if (p.getCarriedItems().getInventory().countId(ItemId.BRONZE_BAR.id()) >= 1) {
							npcsay(p, n, "Great, now all I need is the lens made",
								"Next on the list is molten glass");
							p.getCarriedItems().remove(ItemId.BRONZE_BAR.id(), 1);

							p.updateQuestStage(getQuestId(), 3);
						} else {
							npcsay(p, n, "That's not bronze!",
								"Please bring me some");
						}
					} else if (bronze == 1) {
						npcsay(p, n, "Please carry on trying to find some");
					}
					break;
				case 3:
					npcsay(p, n, "How are you getting on finding me some glass ?");
					int molten = multi(p, n, "Here it is!",
						"No luck yet I'm afraid");
					if (molten == 0) {
						if (p.getCarriedItems().getInventory().countId(ItemId.MOLTEN_GLASS.id()) >= 1) {
							npcsay(p,
								n,
								"Excellent! now all I need is to make the lens",
								"Oh no, I can't use this glass!",
								"Until I find the lens mould used to cast it");
							say(p, n, "What do you mean, lens mould");
							npcsay(p, n, "I need my lens mould",
								"Without it I'll never get the correct shape",
								"I'll have to ask you to try and find it");
							p.updateQuestStage(getQuestId(), 4);
						} else {
							npcsay(p, n,
								"Sorry, you don't have any glass with you",
								"Please don't tease me, I really need this part!");
						}
					} else if (molten == 1) {
						npcsay(p, n, "I hope you find some soon");
					}
					break;
				case 4:
					npcsay(p, n, "Did you bring me the mould ?");
					int mould = multi(p, n, "Yes, I've managed to find it",
						"I haven't found it yet", "I had it then lost it");
					if (mould == 0) {
						if (p.getCarriedItems().getInventory().countId(ItemId.LENS_MOULD.id()) >= 1) {
							npcsay(p, n,
								"At last you've brought all the items I need",
								"To repair the telescope",
								"Oh no! I can't do this");
							say(p, n, "What do you mean ?");
							npcsay(p, n, "My crafting skill is not good enough",
								"To finish this off",
								"Are you skilled at crafting ?");
							int craft = multi(p, n,
								"Yes I have much experience in crafting",
								"No sorry I'm not good at that");
							if (craft == 0) {
								npcsay(p, n, "Thank goodness for that!",
									"You can use the mould with molten glass",
									"To make a new lens",
									"As long as you have practised your crafting skills");
								p.updateQuestStage(getQuestId(), 5);
							} else if (craft == 1) {
								npcsay(p, n,
									"Oh dear, without the lens its useless",
									"Maybe you'll find someone who can Finish the job for you ?");
								p.updateQuestStage(getQuestId(), 5);
							}
						} else {
							npcsay(p,
								n,
								"Where is the mould! You dont even have it on you",
								"Please try and find it");
						}
					} else if (mould == 1) {
						npcsay(p, n, "Perhaps the goblins have stolen it ?");
					} else if (mould == 2) {
						npcsay(p, n, "Well, I wouldn't worry",
							"No doubt the goblins copied the design",
							"I'm sure if you checked again",
							"You'll find another one");
					}
					break;
				case 5:
					npcsay(p, n, "Is the lens finished ?");
					int finished = multi(p, n, "Yes here it is",
						"I haven't finished it yet");
					if (finished == 0) {
						if (p.getCarriedItems().getInventory().countId(ItemId.LENS.id()) >= 1) {
							remove(p, ItemId.LENS.id(), 1);
							npcsay(p, n,
								"Wonderful, at last I can fix the telescope");
							if (p.getCarriedItems().hasCatalogID(ItemId.LENS_MOULD.id(), Optional.of(false))) {
								npcsay(p, n,
									"I'll take back that mould for use again");
								remove(p, ItemId.LENS_MOULD.id(), 1);
							}
							npcsay(p, n, "Meet me at the Observatory later...");
							p.updateQuestStage(getQuestId(), 6);
						} else {
							npcsay(p, n, "Why do you tell lies ?",
								"Please come back when the lens is made");
						}
					} else if (finished == 1) {
						npcsay(p, n, "Oh, okay please hurry");
					}

					break;
				case 6:
					npcsay(p, n, "The telescope is now repaired",
						"Let's go to the Observatory");
					break;
				case -1:
					npcsay(p, n, "Aha, my friend returns",
						"Thanks for all your help with the telescope",
						"What can I do for you ?");
					int completedQuest = multi(p, n,
						"Do you have any more quests", "Nothing, thanks");
					if (completedQuest == 0) {
						npcsay(p, n, "No I'm all out of quests now",
							"But the stars may hold a secret for you...");
					} else if (completedQuest == 1) {
						npcsay(p, n, "Okay no problem");
					}
					break;
			}
		}
	} // DUNGEON SPIDER 656 poison

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return DataConversions.inArray(new int[] {928, 937, 936, 929, 917, 930, 919, 935, 934, 927, 925}, obj.getID())
				|| (obj.getID() == 926 && obj.getX() == 689 && obj.getY() == 3513);
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == 928) {
			if (p.getQuestStage(getQuestId()) == 0) {
				p.teleport(712, 3512, false);
				p.message("You climb down the ladder");
				return;
			}
			if (p.getQuestStage(getQuestId()) == 6
				|| p.getQuestStage(getQuestId()) == -1) {
				p.teleport(712, 3512, false);
				return;
			}
			if (p.getQuestStage(getQuestId()) >= 1
				|| p.getQuestStage(getQuestId()) <= 5) {
				Npc assistant = ifnearvisnpc(p, NpcId.OBSERVATORY_ASSISTANT.id(), 6);
				if (assistant != null) {
					npcsay(p, assistant, "Take great care down there",
						"Remember the goblins have taken over the cavern");
					say(p, assistant, "Oh, okay thanks for the warning");
					p.teleport(712, 3512, false);
				}
				return;
			}
		}
		else if (obj.getID() == 937) {
			p.message("You open the chest");
			p.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), 936, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 936) {
			p.message("You search the chest");
			p.message("The chest contains nothing");
			p.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), 937, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 929) {
			p.message("You open the chest");
			p.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), 917, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 917) {
			p.message("You search the chest");
			p.message("The chest contains a poisonous spider!");
			Npc spider = addnpc(p.getWorld(), NpcId.DUNGEON_SPIDER.id(), obj.getX(), obj.getY(), 120000);
			spider.setChasing(p);
			p.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 929, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 930) {
			p.message("You open the chest");
			p.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 919, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 919) { // KEY CHEST FOUND!
			p.message("You search the chest");
			p.message("You find a small key inside");
			if (p.getCarriedItems().hasCatalogID(ItemId.KEEP_KEY.id(), Optional.of(false))) {
				Functions.mes(p, "You already have a keep key",
					"Another one will have no use");
			} else {
				give(p, ItemId.KEEP_KEY.id(), 1);
			}
			p.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 930, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 935) {
			p.message("You open the chest");
			p.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 934, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 934) { // POISON CURE FOUND!
			p.message("You search the chest");
			p.message("The chest contains some poison cure");
			give(p, ItemId.ONE_CURE_POISON_POTION.id(), 1);
			p.getWorld().registerGameObject(
				new GameObject(obj.getWorld(), obj.getLocation(), 935, obj.getDirection(),
					obj.getType()));
		}
		else if (obj.getID() == 926 && obj.getX() == 689 && obj.getY() == 3513) { // 690
			// 3514
			if (p.getCache().hasKey("keep_key_gate")
				|| p.getQuestStage(getQuestId()) == -1) {
				if (p.getY() <= 3513) {
					p.teleport(690, 3514, false);
					say(p, null, "I'd better be quick",
						"There may be more guards about");
				} else {
					p.message("you go through the gate");
					p.teleport(690, 3513, false);
				}
			} else {
				p.message("The gate is locked");
			}
		}
		else if (obj.getID() == 927) {
			if (!p.getCarriedItems().hasCatalogID(ItemId.LENS_MOULD.id(), Optional.of(false))) {
				p.message("Underneath you find a peculiar mould");
				give(p, ItemId.LENS_MOULD.id(), 1);
			} else {
				p.message("You already have this lens mould");
				p.message("Another one will be of no use");
			}
		}
		else if (obj.getID() == 925) {
			if (p.getQuestStage(getQuestId()) == -1) {
				Npc professor = ifnearvisnpc(p, NpcId.PROFESSOR.id(), 10);
				if (professor != null) {
					p.message("You look through the telescope");
					constellation(p, p.getQuestStage(getQuestId()));
					int completedQuest = multi(p, professor,
						"I can see a constellation through the telescope",
						"I see something, but I don't know what it is");
					if (completedQuest == 0) {
						npcsay(p, professor,
							"Yes, I feel the stars have a message for you...");
					} else if (completedQuest == 1) {
						npcsay(p, professor, "With time you may come to learn",
							"The secrets of the stars");
					}
				}
				return;
			} else if (p.getQuestStage(getQuestId()) == 6) {
				Npc professor = ifnearvisnpc(p, NpcId.PROFESSOR.id(), 10);
				if (professor != null) {
					npcsay(p, professor, "Well done, well done!!",
						"Let's see what the stars have in store for us today");
					p.message("You look through the telescope");
					constellation(p, p.getQuestStage(getQuestId()));
					int telescop = multi(p, professor,
						"I can see a constellation", "What am I looking at ?");
					if (telescop == 0) {
						npcsay(p, professor, "Yes, with this device",
							"The heavens are opened to us...",
							"The constellation you saw was");
						constellationNameAndReward(p, professor);
						p.sendQuestComplete(Quests.OBSERVATORY_QUEST);
						npcsay(p, professor, "By Saradomin's earlobes!",
							"You must be a friend of the gods indeed");
						p.message("Well done, you have completed the Observatory quest");
						npcsay(p, professor,
							"Look in your backpack for your reward",
							"In payment for your work");
						p.message("After repairing the telescope you feel more knowledgable in the skill of crafting");
						npcsay(p, professor, "Now I have work to do...");
						p.message("The professor goes about his business");
					} else if (telescop == 1) {
						npcsay(p, professor, "This is the revealed sky",
							"The constellation you saw was");
						p.sendQuestComplete(Quests.OBSERVATORY_QUEST);
						npcsay(p, professor, "By Saradomin's earlobes!",
							"You must be a friend of the gods indeed");
						p.message("Well done, you have completed the Observatory quest");
						npcsay(p, professor,
							"Look in your backpack for your reward",
							"In payment for your work");
						p.message("After repairing the telescope you feel more knowledgable in the skill of crafting");
						npcsay(p, professor, "Now I have work to do...");
						p.message("The professor goes about his business");
					}
				}
			} else {
				p.message("It seems that the telescope is not operational");
				constellation(p, p.getQuestStage(getQuestId()));
			}
		}
	}

	private void constellationNameAndReward(Player p, Npc n) {
		int baseReductor = 2;
		int varReductor = 4;
		int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.OBSERVATORY_QUEST);
		questData[Quests.MAPIDX_BASE] /= baseReductor;
		questData[Quests.MAPIDX_VAR] /= varReductor;
		if (selectedNumber == 0) {
			npcsay(p, n, "Virgo the virtuous",
				"The strong and peaceful nature of virgo boosts your defence");
			questData[Quests.MAPIDX_SKILL] = Skills.DEFENSE;
			incQuestReward(p, questData, false);
		} else if (selectedNumber == 1) {
			npcsay(p, n, "Libra the scales",
				"The scales of justice award you with Law Runes");
			give(p, ItemId.LAW_RUNE.id(), 3);
		} else if (selectedNumber == 2) {
			npcsay(p, n, "Gemini the twins",
				"The double nature of Gemini awards you a two-handed weapon");
			give(p, ItemId.BLACK_2_HANDED_SWORD.id(), 1);
		} else if (selectedNumber == 3) {
			npcsay(p, n, "Pisces the fish",
				"The gods rain food from the sea on you");
			give(p, ItemId.TUNA.id(), 3);
		} else if (selectedNumber == 4) {
			npcsay(p, n, "Taurus the bull",
				"You are given the strength of a bull");
			give(p, ItemId.FULL_SUPER_STRENGTH_POTION.id(), 1);
		} else if (selectedNumber == 5) {
			npcsay(p, n, "Aquarius the water-bearer",
				"the Gods of water award you with water runes");
			give(p, ItemId.WATER_RUNE.id(), 25);
		} else if (selectedNumber == 6) {
			npcsay(p, n, "Scorpio the scorpion",
				"The scorpion gives you poison from it's sting");
			give(p, ItemId.WEAPON_POISON.id(), 1);
		} else if (selectedNumber == 7) {
			npcsay(p, n, "Aries the ram",
				"The ram's strength improves your attack abilities");
			questData[Quests.MAPIDX_SKILL] = Skills.ATTACK;
			incQuestReward(p, questData, false);
		} else if (selectedNumber == 8) {
			npcsay(p, n, "Sagittarius the Centaur",
				"The Gods award you a maple longbow");
			give(p, ItemId.MAPLE_LONGBOW.id(), 1);
		} else if (selectedNumber == 9) {
			npcsay(p, n, "Leo the lion",
				"The power of the lion has increased your hitpoints");
			questData[Quests.MAPIDX_SKILL] = Skills.HITS;
			incQuestReward(p, questData, false);
		} else if (selectedNumber == 10) {
			npcsay(p, n, "Capricorn the goat",
				"you are granted an increase in strength");
			questData[Quests.MAPIDX_SKILL] = Skills.STRENGTH;
			incQuestReward(p, questData, false);
		} else if (selectedNumber == 11) {
			npcsay(p, n, "Cancer the crab",
				"The armoured crab gives you an amulet of protection");
			give(p, ItemId.EMERALD_AMULET_OF_PROTECTION.id(), 1);
		}
		// all constellations give uncut sapphire
		give(p, ItemId.UNCUT_SAPPHIRE.id(), 1);
	}

	private void constellation(Player p, int stage) {
		selectedNumber = 0;

		// quest completed, always show scorpion
		if (stage == -1) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                 *                                                                                                                               *                                                                                                      *                                                                                                                     *                    *                                                                                                                                                                                                                                                     *                                                                                         *                                                                                                                                                                                                                                *              *                                                                                                    *                    *                                                                                                   *        *                                                                           ", true);
			return;
		}
		// show no image on telescope at this stage
		else if (stage < 6) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      ", true);
			return;
		}

		int random = DataConversions.random(0, 11);
		if (random == 0) { //
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                    *                                                                                               *                                                                                                                             *                                   *                                                                                                               *                  *                                                                     *         *                                                                                                                                                 *     *                                                                                  *                          *                                                                                                                    *                                                                                                    *                                                                                                                                 *                                                          ", true);
		} else if (random == 1) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                *                                                                                            *                                                                                                                           *             *                                                                                                                                                                                                                                                                    *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               *                                                                                                                                                                                                                             *                                                    ", true);
		} else if (random == 2) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                 *                                                                                                                                 *                                                                                                        *                                                                                                                                                                                                                                                 *                        *                                                                                                                               *                                                                                                                                                                                                                        *                                                                                                                              *                                                                                                                  *                                                                                                                                      *                                     ", true);
		} else if (random == 3) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                              *                                                                                                                    *                                                                                                                                                                                                                                                 *                                                                                                                                                                                                                                            *                                                                                                                                                                                                                                           *                                                                                                                                                                                                                                           *          *                   *                                                                                                                                       *             *                                                                                                                   *                                                                                                                                   *                                                                                             *        *                          ", true);
		} else if (random == 4) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                       *                                                                                                                                                                                                                                               *                                                                                                                                                                                                                                                                          *                                                                                                                    *                                                                                                                              *                                                                                                                                          *                                                                                                               *           *                                                                                                                                                                                                                                   *                                                                                                                                                                                                                                         *                                    ", true);
		} else if (random == 5) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                      *                                                                                                                      *                                                                                                                                          *                                                                                                *                                    *                                                                                              *                                                                                                                                                                                                                       *                                                                                                                *                                                                                                                            *                                                                      ", true);
		} else if (random == 6) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                 *                                                                                                                               *                                                                                                      *                                                                                                                     *                    *                                                                                                                                                                                                                                                     *                                                                                         *                                                                                                                                                                                                                                *              *                                                                                                    *                    *                                                                                                   *        *                                                                           ", true);
		} else if (random == 7) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             *                                                                                                                                     *                                                                                                                             *                                                                                            *                                                                                                                *                                                                                                                                                       *                            ", true);
		} else if (random == 8) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                        *  *                                                                                                                                         *                                                                                                                   *                                                                                                             *                                                                                                                                                                                                                                                               *                                                                                                      *                                                                                                                                                *                                                                                                                                                                                                                                        *                                                                                 *                                                              ", true);
		} else if (random == 9) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                   *                                                                                                             *                                                                                                        *                              *                                                                                                              *                                                                                          *                                                                                                                                        *             *                                                                                                                                                                                                                               *                                                                                                                                           *                                                                                                                               *                                 ", true);
		} else if (random == 10) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                               *                                                                                                                   *                                                                                         *                                                                                                                  *                *                                                                                                                                                                                                                                                                  *                                                                                                 *                                                                                                                                                                                                                                                     *       *                                               ", true);
		} else if (random == 11) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                           *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   *                                                                                                                                                                                                                                               *                                                                                                                                                                                                                                                                                                                                                                   *                                                                                                                                                                                                                                                                 *                                 ", true);
		}
		selectedNumber = random;
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item,
							   Player player) {
		return obj.getID() == 926 && item.getCatalogId() == ItemId.KEEP_KEY.id();
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == 926 && item.getCatalogId() == ItemId.KEEP_KEY.id()) {
			Npc guard = ifnearvisnpc(p, NpcId.GOBLIN_GUARD.id(), 5);
			if (guard != null) {
				p.message("The gate unlocks");
				p.message("The keep key is broken - I'll discard it");
				remove(p, ItemId.KEEP_KEY.id(), 1);
				if (!p.getCache().hasKey("keep_key_gate")) {
					p.getCache().store("keep_key_gate", true);
				}
				guard.setChasing(p);
			}
		}

	}
}
