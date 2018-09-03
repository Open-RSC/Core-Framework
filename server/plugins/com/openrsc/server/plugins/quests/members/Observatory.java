package com.openrsc.server.plugins.quests.members;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.spawnNpc;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

public class Observatory implements QuestInterface,TalkToNpcListener,
TalkToNpcExecutiveListener, ObjectActionListener,
ObjectActionExecutiveListener, InvUseOnObjectListener,
InvUseOnObjectExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.OBSERVATORY_QUEST;
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
		p.incQuestPoints(2);
		p.incQuestExp(12, (p.getSkills().getMaxStat(12) * 400) + 1000);
		p.getCache().remove("keep_key_gate");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 654) { // ASSISTANT
			return true;
		}
		if (n.getID() == 652) { // PROFFESOR
			return true;
		}
		if (n.getID() == 662) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 662) {
			switch (p.getQuestStage(this)) {
			case 6:
				npcTalk(p, n, "Hello friend", "It's time to use the telescope");
				break;
			case -1:
				npcTalk(p, n, "Hello friend", "The stars hold many secrets",
						"The moon rises in Scorpio...");
				break;
			}
		}
		if (n.getID() == 654) { // ASSISTANT
			switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "Hello wanderer",
						"Do you require any assistance ?");
				int first = showMenu(p, n, "Yes, what do you two do here ?",
						"No, just looking around thanks",
						"Can I have a look through that telescope ?");
				if (first == 0) {
					npcTalk(p, n, "This is the observatory reception",
							"Up on the cliff is the observatory dome",
							"From here we view the heavens",
							"That is before the telescope was damaged",
							"By those monsters outside...");
				} else if (first == 1) {
					npcTalk(p, n, "Okay, be my guest",
							"If you need any help let me know...");
				} else if (first == 2) {
					npcTalk(p, n, "I'm sorry but it's broken!",
							"The Professor will explain if you speak to him");
				}
				break;
			case 1:
				npcTalk(p, n, "How can I help you ?");
				int help = showMenu(p, n, "I can't find any planks!",
						"I dont need any help thanks");
				if (help == 0) {
					npcTalk(p,
							n,
							"I understand planks can be found at the barbarian outpost",
							"To the north east of ardougne",
							"You will probably have to trek over there to find some...");
				} else if (help == 1) {
					npcTalk(p, n, "Oh, okay then if you are sure");
					p.message("The assistant continues with his work");
				}
				break;
			case 2:
				npcTalk(p, n, "How can I help you ?");
				int bronze = showMenu(p, n, "I can't see any bronze around",
						"I dont need any help thanks");
				if (bronze == 0) {
					npcTalk(p,
							n,
							"You'll need to mix purified copper and tin together",
							"To produce this metal");
				} else if (bronze == 1) {
					npcTalk(p, n, "Oh, okay then if you are sure");
					p.message("The assistant continues with his work");
				}
				break;
			case 3:
				npcTalk(p, n, "How can I help you ?");
				int molten = showMenu(p, n,
						"I'm having problems getting glass",
						"I don't need any help thanks");
				if (molten == 0) {
					npcTalk(p, n, "Don't you know how to make glass ?",
							"Unfortunately we dont have those skills",
							"I remember reading about that somewhere...");
				} else if (molten == 1) {
					npcTalk(p, n, "Oh, okay then if you are sure");
					p.message("The assistant continues with his work");
				}
				break;
			case 4:
				npcTalk(p, n, " How can I help you ?");
				int mould = showMenu(p, n, "I cant find the lens mould",
						"I don't need any help thanks");
				if (mould == 0) {
					npcTalk(p,
							n,
							"Can't you find the mould ?",
							"I'm sure I heard one of those goblins talking about it...",
							"I bet they have hidden it somewhere");
				} else if (mould == 1) {
					npcTalk(p, n, "Oh, okay then if you are sure");
					p.message("The assistant continues with his work");
				}
				break;
			case 5:
				npcTalk(p, n, "How can I help you ?");
				int lens = showMenu(p, n, "I can't make the lens!",
						"I don't need any help thanks");
				if (lens == 0) {
					npcTalk(p, n, "Crafting objects like this requires skill",
							"You may need to practice more first...");
				} else if (lens == 1) {
					npcTalk(p, n, "Oh, okay then if you are sure");
					p.message("The assistant continues with his work");
				}
				break;
			case 6:
				npcTalk(p, n, "Well hello again",
						"thanks for helping out the professor",
						"You've made my life much easier!",
						"Have a drink on me!");
				p.message("The assistant gives you some wine");
				playerTalk(p, n, "Thanks very much");
				addItem(p, 142, 1);
				break;
			case -1:
				if(!p.getCache().hasKey("observatory_assistant_drink")) {
					npcTalk(p, n, "Well hello again",
							"thanks for helping out the professor",
							"You've made my life much easier!",
							"Have a drink on me!");
					p.message("The assistant gives you some wine");
					addItem(p, 142, 1);
					playerTalk(p, n, "Thanks very much");
					p.getCache().store("observatory_assistant_drink", true);
					return;
				}
				npcTalk(p, n, "Thanks again");
				break;
			}
		}
		if (n.getID() == 652) {
			switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "Hello adventurer",
						"What brings you to these parts ?");
				int first = showMenu(p, n, "I am lost!!!",
						"I'd like to have a look through that telescope",
						"Whats the ladder over there for ?",
						"It is of no concern of yours...");
				if (first == 0) {
					npcTalk(p,
							n,
							"Lost ? it must have been those gnomes that have lead you astray",
							"Head North-East to find the land Ardougne");
					playerTalk(p, n, "I'm sure I'll find the way",
							"Thanks for your help");
					npcTalk(p, n, "No problem at all, come and visit again");
				} else if (first == 1) {
					npcTalk(p, n, "So would I !!",
							"The trouble is, its not working");
					playerTalk(p, n, "What do you mean ?");
					npcTalk(p, n, "Did you see those houses outside ?");
					playerTalk(p, n, "Yes, I've seen them");
					npcTalk(p,
							n,
							"Well it's a family of goblins",
							"Since they moved here they cause me nothing but trouble",
							"Last week my telescope was tampered with",
							"And now parts need replacing before it can be used again",
							"Err, I don't suppose you would be willing to help?");
					int second = showMenu(p, n,
							"Sounds interesting, what can I do for you ?",
							"Oh sorry, I don't have time for that");
					if (second == 0) {
						npcTalk(p,
								n,
								"Oh thanks so much!",
								"I need three new parts for the telescope so it can be used again",
								"I need wood to make a new tripod",
								"Bronze to make a new tube",
								"And glass for a replacement lens",
								"My assistant will help you obtaining these",
								"Ask him if you need any help");
						playerTalk(p, n, "Okay what do I need to do ?");
						npcTalk(p, n,
								"First I need three planks of wood for the tripod");
						p.updateQuestStage(getQuestId(), 1);
					} else if (second == 1) {
						npcTalk(p, n, "Oh dear, I really do need some help",
								"If you see anyone who can help please send them my way");
						p.message("The Professor carries on with his duties");
					}

				} else if (first == 2) {
					npcTalk(p, n,
							"The ladder leads to the entrance of the cavern",
							"That leads from here to the observatory");
				} else if (first == 3) {
					npcTalk(p, n, "Okay Okay, there's no need to be insulting!");
					p.message("The professor carries on with his studies");
				}
				break;
			case 1:
				npcTalk(p, n, "I'ts my helping hand back again!",
						"Do you have the planks yet ?");
				int planks = showMenu(p, n, "Yes I've got them",
						"No, sorry not yet");
				if (planks == 0) {
					if (p.getInventory().countId(410) >= 3) {
						npcTalk(p,
								n,
								"Well done, I can start the tripod construction now",
								"Now for the bronze");
						p.getInventory().remove(410, 3);

						p.updateQuestStage(getQuestId(), 2);
					} else {
						npcTalk(p, n, "You don't seem to have enough planks!",
								"I need three in total");
					}
				} else if (planks == 1) {
					npcTalk(p, n, "Oh dear, well please bring them soon");
				}
				break;
			case 2:
				npcTalk(p, n, " Hello again, do you have the bronze yet ?");
				int bronze = showMenu(p, n, "Yes I have it",
						"I'm still looking");
				if (bronze == 0) {
					if (p.getInventory().countId(169) >= 1) {
						npcTalk(p, n, "Great, now all I need is the lens made",
								"Next on the list is molten glass");
						p.getInventory().remove(169, 1);

						p.updateQuestStage(getQuestId(), 3);
					} else {
						npcTalk(p, n, "That's not bronze!",
								"Please bring me some");
					}
				} else if (bronze == 1) {
					npcTalk(p, n, "Please carry on trying to find some");
				}
				break;
			case 3:
				npcTalk(p, n, "How are you getting on finding me some glass ?");
				int molten = showMenu(p, n, "Here it is!",
						"No luck yet I'm afraid");
				if (molten == 0) {
					if (p.getInventory().countId(623) >= 1) {
						npcTalk(p,
								n,
								"Excellent! now all I need is to make the lens",
								"Oh no, I can't use this glass!",
								"Until I find the lens mould used to cast it");
						playerTalk(p, n, "What do you mean, lens mould");
						npcTalk(p, n, "I need my lens mould",
								"Without it I'll never get the correct shape",
								"I'll have to ask you to try and find it");
						p.updateQuestStage(getQuestId(), 4);
					} else {
						npcTalk(p, n,
								"Sorry, you don't have any glass with you",
								"Please don't tease me, I really need this part!");
					}
				} else if (molten == 1) {
					npcTalk(p, n, "I hope you find some soon");
				}
				break;
			case 4:
				npcTalk(p, n, "Did you bring me the mould ?");
				int mould = showMenu(p, n, "Yes, I've managed to find it",
						"I haven't found it yet", "I had it then lost it");
				if (mould == 0) {
					if (p.getInventory().countId(1017) >= 1) {
						npcTalk(p, n,
								"At last you've brought all the items I need",
								"To repair the telescope",
								"Oh no! I can't do this");
						playerTalk(p, n, "What do you mean ?");
						npcTalk(p, n, "My crafting skill is not good enough",
								"To finish this off",
								"Are you skilled at crafting ?");
						int craft = showMenu(p, n,
								"Yes I have much experience in crafting",
								"No sorry I'm not good at that");
						if (craft == 0) {
							npcTalk(p, n, "Thank goodness for that!",
									"You can use the mould with molten glass",
									"To make a new lens",
									"As long as you have practised your crafting skills");
							p.updateQuestStage(getQuestId(), 5);
						} else if (craft == 1) {
							npcTalk(p, n,
									"Oh dear, without the lens its useless",
									"Maybe you'll find someone who can Finish the job for you ?");
							p.updateQuestStage(getQuestId(), 5);
						}
					} else {
						npcTalk(p,
								n,
								"Where is the mould! You dont even have it on you",
								"Please try and find it");
					}
				} else if (mould == 1) {
					npcTalk(p, n, "Perhaps the goblins have stolen it ?");
				} else if (mould == 2) {
					npcTalk(p, n, "Well, I wouldn't worry",
							"No doubt the goblins copied the design",
							"I'm sure if you checked again",
							"You'll find another one");
				}
				break;
			case 5:
				npcTalk(p, n, "Is the lens finished ?");
				int finished = showMenu(p, n, "Yes here it is",
						"I haven't finished it yet");
				if (finished == 0) {
					if (p.getInventory().countId(1018) >= 1) {
						npcTalk(p, n,
								"Wonderful, at last I can fix the telescope");
						if (hasItem(p, 1017)) {
							npcTalk(p, n,
									"I'll take back that mould for use again");
							removeItem(p, 1017, 1);
						}
						npcTalk(p, n, "Meet me at the Observatory later...");
						p.updateQuestStage(getQuestId(), 6);
					} else {
						npcTalk(p, n, "Why do you tell lies ?",
								"Please come back when the lens is made");
					}
				} else if (finished == 1) {
					npcTalk(p, n, "Oh, okay please hurry");
				}

				break;
			case 6:
				npcTalk(p, n, "The telescope is now repaired",
						"Let's go to the Observatory");
				break;
			case -1:
				npcTalk(p, n, "Aha, my friend returns",
						"Thanks for all your help with the telescope",
						"What can I do for you ?");
				int completedQuest = showMenu(p, n,
						"Do you have any more quests", "Nothing, thanks");
				if (completedQuest == 0) {
					npcTalk(p, n, "No I'm all out of quests now",
							"But the stars may hold a secret for you...");
				} else if (completedQuest == 1) {
					npcTalk(p, n, "Okay no problem");
				}
				break;
			}
		}
	} // DUNGEON SPIDER 656 poison

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 928) {
			return true;
		}
		if (obj.getID() == 937 || obj.getID() == 936) {
			return true;
		}
		if (obj.getID() == 929 || obj.getID() == 917) {
			return true;
		}
		if (obj.getID() == 930 || obj.getID() == 919) {
			return true;
		}
		if (obj.getID() == 935 || obj.getID() == 934) {
			return true;
		}
		if (obj.getID() == 926 && obj.getX() == 689 && obj.getY() == 3513) {
			return true;
		}
		if (obj.getID() == 927) {
			return true;
		}
		if (obj.getID() == 925) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
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
				Npc assistant = getNearestNpc(p, 654, 6);
				if (assistant != null) {
					npcTalk(p, assistant, "Take great care down there",
							"Remember the goblins have taken over the cavern");
					playerTalk(p, assistant, "Oh, okay thanks for the warning");
					p.teleport(712, 3512, false);
				}
				return;
			}
		}
		if (obj.getID() == 937) {
			p.message("You open the chest");
			World.getWorld().replaceGameObject(obj, 
					new GameObject(obj.getLocation(), 936, obj.getDirection(),
							obj.getType()));
		}
		if (obj.getID() == 936) {
			p.message("You search the chest");
			p.message("The chest contains nothing");
			World.getWorld().replaceGameObject(obj, 
					new GameObject(obj.getLocation(), 937, obj.getDirection(),
							obj.getType()));
		}
		if (obj.getID() == 929) {
			p.message("You open the chest");
			World.getWorld().replaceGameObject(obj, 
					new GameObject(obj.getLocation(), 917, obj.getDirection(),
							obj.getType()));
		}
		if (obj.getID() == 917) {
			p.message("You search the chest");
			p.message("The chest contains a poisonous spider!");
			Npc spider = spawnNpc(656, obj.getX(), obj.getY(), 120000);
			spider.setChasing(p);
			World.getWorld().registerGameObject(
					new GameObject(obj.getLocation(), 929, obj.getDirection(),
							obj.getType()));
		}
		if (obj.getID() == 930) {
			p.message("You open the chest");
			World.getWorld().registerGameObject(
					new GameObject(obj.getLocation(), 919, obj.getDirection(),
							obj.getType()));
		}
		if (obj.getID() == 919) { // KEY CHEST FOUND!
			p.message("You search the chest");
			p.message("You find a small key inside");
			addItem(p, 1012, 1);
			World.getWorld().registerGameObject(
					new GameObject(obj.getLocation(), 930, obj.getDirection(),
							obj.getType()));
		}
		if (obj.getID() == 935) {
			p.message("You open the chest");
			World.getWorld().registerGameObject(
					new GameObject(obj.getLocation(), 934, obj.getDirection(),
							obj.getType()));
		}
		if (obj.getID() == 934) { // POISON CURE FOUND!
			p.message("You search the chest");
			p.message("The chest contains some poison cure");
			addItem(p, 568, 1);
			World.getWorld().registerGameObject(
					new GameObject(obj.getLocation(), 935, obj.getDirection(),
							obj.getType()));
		}
		if (obj.getID() == 926 && obj.getX() == 689 && obj.getY() == 3513) { // 690
			// 3514
			if (p.getCache().hasKey("keep_key_gate")
					|| p.getQuestStage(getQuestId()) == -1) {
				if (p.getY() <= 3513) {
					p.teleport(690, 3514, false);
					playerTalk(p, null, "I'd better be quick",
							"There may be more guards about");
				} else {
					p.message("you go through the gate");
					p.teleport(690, 3513, false);
				}
			} else {
				p.message("The gate is locked");
			}
		}
		if (obj.getID() == 927) {
			if (!hasItem(p, 1017)) {
				p.message("Underneath you find a peculiar mould");
				addItem(p, 1017, 1);
			} else {
				p.message("You already have this lens mould");
				p.message("Another one will be of no use");
			}
		}
		if (obj.getID() == 925) {
			if (p.getQuestStage(getQuestId()) == -1) {
				Npc professor = getNearestNpc(p, 662, 10);
				if(professor != null) {
					p.message("You look through the telescope");
					constellation(p);
					int completedQuest = showMenu(p, professor,
							"I can see a constellation through the telescope",
							"I see something, but I don't know what it is");
					if (completedQuest == 0) {
						npcTalk(p, professor,
								"Yes, I feel the stars have a message for you...");
					} else if (completedQuest == 1) {
						npcTalk(p, professor, "With time you may come to learn",
								"The secrets of the stars");
					}
				}
				return;
			}
			if (p.getQuestStage(getQuestId()) == 6) {
				Npc professor = getNearestNpc(p, 662, 10);
				if(professor != null) {
					npcTalk(p, professor, "Well done, well done!!",
							"Let's see what the stars have in store for us today");
					p.message("You look through the telescope");
					constellation(p);
					int telescop = showMenu(p, professor,
							"I can see a constellation", "What am I looking at ?");
					if (telescop == 0) {
						npcTalk(p, professor, "Yes, with this device",
								"The heavens are opened to us...",
								"The constellation you saw was");
						constellationNameAndReward(p, professor);
						p.sendQuestComplete(Constants.Quests.OBSERVATORY_QUEST);
						npcTalk(p, professor, "By Saradomin's earlobes!",
								"You must be a friend of the gods indeed");
						p.message("Well done, you have completed the Observatory quest");
						npcTalk(p, professor,
								"Look in your backpack for your reward",
								"In payment for your work");
						p.message("After repairing the telescope you feel more knowledgable in the skill of crafting");
						npcTalk(p, professor, "Now I have work to do...");
						p.message("The professor goes about his business");
					} else if (telescop == 1) {
						npcTalk(p, professor, "This is the revealed sky",
								"The constellation you saw was");
						p.sendQuestComplete(Constants.Quests.OBSERVATORY_QUEST);
						npcTalk(p, professor, "By Saradomin's earlobes!",
								"You must be a friend of the gods indeed");
						p.message("Well done, you have completed the Observatory quest");
						npcTalk(p, professor,
								"Look in your backpack for your reward",
								"In payment for your work");
						p.message("After repairing the telescope you feel more knowledgable in the skill of crafting");
						npcTalk(p, professor, "Now I have work to do...");
						p.message("The professor goes about his business");
					}
				}
			}
		}
	}

	private int selectedNumber = 0;

	private void constellationNameAndReward(Player p, Npc n) {
		if(selectedNumber == 0) {
			npcTalk(p, n, "Virgo the virtuous",
					"you are granted an increase in defense");
			p.incQuestExp(1, p.getSkills().getMaxStat(1) * 100 + 500);
			addItem(p, 160, 1);
		} else if(selectedNumber == 1) {
			npcTalk(p, n, "Libra the scales",
					"The scales of justice award you with Law Runes");
			addItem(p, 42, 3);
			addItem(p, 160, 1);
		} else if(selectedNumber == 2) {
			npcTalk(p, n, "Gemini the twins",
					"The double nature of Gemini awards you a two-handed weapon");
			addItem(p, 426, 1);
			addItem(p, 160, 1);
		} else if(selectedNumber == 3) {
			npcTalk(p, n, "Pisces the fish");
			addItem(p, 367, 3);
			addItem(p, 160, 1);
		} else if(selectedNumber == 4) {
			npcTalk(p, n, "Taurus the bull");
			addItem(p, 492, 1);
			addItem(p, 160, 1);
		} else if(selectedNumber == 5) {
			npcTalk(p, n, "Aquarius the water-bearer");
			addItem(p, 32, 25);
			addItem(p, 160, 1);
		} else if(selectedNumber == 6) {
			npcTalk(p, n, "Scorpio the scorpion",
					"The scorpion gives you poison from it's sting");
			addItem(p, 572, 1);
			addItem(p, 160, 1);
		} else if(selectedNumber == 7) {
			npcTalk(p, n, "Aries the ram",
					"you are granted an increase in attack");
			p.incQuestExp(0, p.getSkills().getMaxStat(0) * 100 + 500);
			addItem(p, 160, 1);
		} else if(selectedNumber == 8) {
			npcTalk(p, n, "Sagittarius the Centaur");
			addItem(p, 652, 1);
			addItem(p, 160, 1);
		} else if(selectedNumber == 9) {
			npcTalk(p, n, "Leo the lion",
					"you are granted an increase in hits");
			p.incQuestExp(3, p.getSkills().getMaxStat(3) * 100 + 500);
			addItem(p, 160, 1);
		}  else if(selectedNumber == 10) {
			npcTalk(p, n, "Capricorn the goat",
					"you are granted an increase in strength");
			p.incQuestExp(2, p.getSkills().getMaxStat(2) * 100 + 500);
			addItem(p, 160, 1);
		} else if(selectedNumber == 11) {
			npcTalk(p, n, "Cancer the crab");
			addItem(p, 315, 1);
			addItem(p, 160, 1);
		}
	}

	private void constellation(Player p) {
		selectedNumber = 0;
		int random = DataConversions.random(0, 11);
		if(random == 0) { // 
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                    *                                                                                               *                                                                                                                             *                                   *                                                                                                               *                  *                                                                     *         *                                                                                                                                                 *     *                                                                                  *                          *                                                                                                                    *                                                                                                    *                                                                                                                                 *                                                          ", true);
		} else if(random == 1) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                *                                                                                            *                                                                                                                           *             *                                                                                                                                                                                                                                                                    *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               *                                                                                                                                                                                                                             *                                                    ", true);
		} else if(random == 2) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                 *                                                                                                                                 *                                                                                                        *                                                                                                                                                                                                                                                 *                        *                                                                                                                               *                                                                                                                                                                                                                        *                                                                                                                              *                                                                                                                  *                                                                                                                                      *                                     ", true);
		} else if(random == 3) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                              *                                                                                                                    *                                                                                                                                                                                                                                                 *                                                                                                                                                                                                                                            *                                                                                                                                                                                                                                           *                                                                                                                                                                                                                                           *          *                   *                                                                                                                                       *             *                                                                                                                   *                                                                                                                                   *                                                                                             *        *                          ", true);
		} else if(random == 4) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                       *                                                                                                                                                                                                                                               *                                                                                                                                                                                                                                                                          *                                                                                                                    *                                                                                                                              *                                                                                                                                          *                                                                                                               *           *                                                                                                                                                                                                                                   *                                                                                                                                                                                                                                         *                                    ", true);
		} else if(random == 5) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                      *                                                                                                                      *                                                                                                                                          *                                                                                                *                                    *                                                                                              *                                                                                                                                                                                                                       *                                                                                                                *                                                                                                                            *                                                                      ", true);
		} else if(random == 6) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                 *                                                                                                                               *                                                                                                      *                                                                                                                     *                    *                                                                                                                                                                                                                                                     *                                                                                         *                                                                                                                                                                                                                                *              *                                                                                                    *                    *                                                                                                   *        *                                                                           ", true);
		} else if(random == 7) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             *                                                                                                                                     *                                                                                                                             *                                                                                            *                                                                                                                *                                                                                                                                                       *                            ", true);
		} else if(random == 8) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                        *  *                                                                                                                                         *                                                                                                                   *                                                                                                             *                                                                                                                                                                                                                                                               *                                                                                                      *                                                                                                                                                *                                                                                                                                                                                                                                        *                                                                                 *                                                              ", true);
		} else if(random == 9) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                   *                                                                                                             *                                                                                                        *                              *                                                                                                              *                                                                                          *                                                                                                                                        *             *                                                                                                                                                                                                                               *                                                                                                                                           *                                                                                                                               *                                 ", true);
		}  else if(random == 10) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                                               *                                                                                                                   *                                                                                         *                                                                                                                  *                *                                                                                                                                                                                                                                                                  *                                                                                                 *                                                                                                                                                                                                                                                     *       *                                               ", true);
		} else if(random == 11) {
			ActionSender.sendBox(p, "                                                                                                                                                                                                                                                                                                           *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   *                                                                                                                                                                                                                                               *                                                                                                                                                                                                                                                                                                                                                                   *                                                                                                                                                                                                                                                                 *                                 ", true);
		}
		selectedNumber = random;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if (obj.getID() == 926 && item.getID() == 1012) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 926 && item.getID() == 1012) {
			Npc guard = getNearestNpc(p, 651, 5);
			if(guard != null) {
				p.message("The gate unlocks");
				p.message("The keep key is broken - I'll discard it");
				removeItem(p, 1012, 1);
				if(!p.getCache().hasKey("keep_key_gate")) {
					p.getCache().store("keep_key_gate", true);
				}
				guard.setChasing(p);
			}
		}

	}
}
