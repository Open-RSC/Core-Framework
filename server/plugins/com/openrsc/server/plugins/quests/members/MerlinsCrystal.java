package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;

import static com.openrsc.server.plugins.Functions.*;

/**
 * 
 * @author n0m
 * @author complete dialogues & fixed functions - Davve
 * @author start of holy grail quest - davve
 */
public class MerlinsCrystal implements QuestInterface,TalkToNpcListener,
TalkToNpcExecutiveListener, ObjectActionListener,
ObjectActionExecutiveListener, WallObjectActionListener,
WallObjectActionExecutiveListener, PlayerKilledNpcListener,
PlayerKilledNpcExecutiveListener, InvUseOnObjectListener,
InvUseOnObjectExecutiveListener, DropListener, DropExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.MERLINS_CRYSTAL;
	}

	@Override
	public String getQuestName() {
		return "Merlin's crystal (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.getCache().remove("magic_words");
		player.message("Well done you have completed the Merlin's crystal quest");
		incQuestReward(player, Quests.questData.get(Quests.MERLINS_CRYSTAL), true);
		player.message("You haved gained 6 quest points!");
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		return (obj.getID() == 292 || obj.getID() == 293)
				|| obj.getID() == 291
				|| (obj.getID() == 296 && obj.getY() == 366 && command
				.equalsIgnoreCase("search"));
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 292 || obj.getID() == 293) {
			Npc arhein = getNearestNpc(p, 280, 10);
			if (arhein != null) {
				npcTalk(p, arhein, "Oi get away from there!");
			} else {
				p.teleport(456, 3352, false);
				message(p, "You hide away in the ship",
						"The ship starts to move.", "You are out at sea",
						"The ship comes to a stop");
				p.teleport(456, 520, false);
				message(p, "You sneak out of the ship");
			}
		} else if (obj.getID() == 291) {
			p.message("there are buckets in this crate");
			sleep(800);
			p.message("would you like a bucket?");
			int opt = showMenu(p, "Yes", "No");
			if (opt == 0) {
				p.message("you take a bucket.");
				addItem(p, 21, 1);
			}
		} else if (obj.getID() == 296) {
			message(p,
					"You find a small inscription at the bottom of the altar",
					"It reads Snarthon Candtrick Termanto");
			if(!p.getCache().hasKey("magic_words")) {
				p.getCache().store("magic_words", true);
			}
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == 276 && p.getQuestStage(this) == 2;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(n.getCombatEvent() != null) {
			n.getCombatEvent().resetCombat();
		}
		n.getSkills().setLevel(3, 5);
		Npc leFaye = spawnNpc(281, 461, 2407, 60000);
		sleep(500);
		npcTalk(p, leFaye, "Please spare my son");
		int option = showMenu(p, n, "Tell me how to untrap Merlin and I might",
				"No he deserves to die", "OK then");
		if (option == 0) {
			p.updateQuestStage(this, 3);
			npcTalk(p, leFaye,
					"You have guessed correctly that I'm responsible for that");
			npcTalk(p, leFaye,
					"I suppose I can live with that fool Merlin being loose");
			npcTalk(p, leFaye, "for the sake of my son");
			npcTalk(p, leFaye, "Setting him free won't be easy though");
			npcTalk(p, leFaye,
					"You will need to find a pentagram as close to the crystal as you can find");
			npcTalk(p, leFaye,
					"You will need to drop some bats bones in the pentagram");
			npcTalk(p, leFaye, "while holding a black candle");
			npcTalk(p, leFaye, "This will summon the demon Thrantax");
			npcTalk(p, leFaye, "You will need to bind him with magic words");
			npcTalk(p, leFaye,
					"Then you will need the sword Excalibur with which the spell was bound");
			npcTalk(p, leFaye, "Shatter the crystal with Excalibur");
			int sub_opt = showMenu(p, leFaye, "So where can I find Excalibur?",
					"OK I will do all that", "What are the magic words?");
			if (sub_opt == 0) {
				npcTalk(p, leFaye, "The lady of the lake has it");
				npcTalk(p, leFaye, "I don't know if she will give it you though");
				npcTalk(p, leFaye, "She can be rather temperamental");
				int sub_opt2 = showMenu(p, leFaye, "OK I will go do all that",
						"What are the magic words?");
				if (sub_opt2 == 1) {
					npcTalk(p, leFaye,
							" You will find the magic words at the base of one of the chaos altars");
					npcTalk(p, leFaye, " Which chaos altar I cannot remember");
				}
			} else if (sub_opt == 1) {
				p.message("Morgan Le Faye vanishes");
			} else if (sub_opt == 2) {
				npcTalk(p, leFaye,
						"You will find the magic words at the base of one of the chaos altars");
				npcTalk(p, leFaye, "Which chaos altar I cannot remember");
			}
		} else if (option == 1) {
			p.message("You kill Mordred");
			n.killedBy(p);
		} else if (option == 2) {
			p.message("Morgan Le Faye vanishes");
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		return obj.getID() == 294 || obj.getID() == 287 && item.getID() == 606;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 294) {
			if (item.getID() == 603) {
				message(p, "you squirt insect repellant on the beehive",
						"You see bees leaving the hive");
				if(!p.getCache().hasKey("squirt")) {
					p.getCache().store("squirt", true);
				}
			} else if (item.getID() == 21) {
				message(p, "You try to get some wax from the beehive");
				if (p.getCache().hasKey("squirt")) {
					message(p, "You get some wax from the hive",
							"The bees fly back to the hive as the repellant wears off");
					removeItem(p, 21, 1); 
					addItem(p, 605, 1);
					p.getCache().remove("squirt");
				} else {
					p.message("Suddenly bees fly out of the hive and sting you");
					p.damage(2);
				}
			}
		} else if (obj.getID() == 287 && item.getID() == 606) {
			if (p.getQuestStage(this) == 4) {
				message(p, "The crystal shatters");
				World.getWorld().unregisterGameObject(obj);
				World.getWorld().delayedSpawnObject(obj.getLoc(), 30000);
				Npc merlin = getNearestNpc(p, 287, 5);
				npcTalk(p, merlin, "Thankyou thankyou",
						"It's not fun being trapped in a giant crystal",
						"Go speak to King Arthur, I'm sure he'll reward you");

				p.message("You have set Merlin free now talk to king arthur");
				p.updateQuestStage(this, 5);
			} else {
				p.message("Nothing interesting happens");
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		return obj.getX() == 277 && obj.getY() == 632;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getX() == 277 && obj.getY() == 632) {
			if(p.getQuestStage(this) != 3 && !p.getCache().hasKey("lady_test")) {
				doDoor(obj, p);
				return;
			} else {
				Npc beggar = getNearestNpc(p, 286, 5);
				if(beggar == null) {
					beggar = spawnNpc(286, 276, 631, 60000, p);
				}
				sleep(600);
				if(beggar != null) {
					npcTalk(p, beggar, "Please sir, me and my family are starving",
							"Could you possibly give me a loaf of bread?");
					int opt = showMenu(p, beggar, "Yes certainly",
							"No I don't have any bread with me");
					if (opt == 0) {
						if (!p.getInventory().hasItemId(138)) {
							playerTalk(p, beggar,
									"Except that I don't have any bread at the moment");
							npcTalk(p, beggar,
									"Well if you get some you know where to come");
							doDoor(obj, p);
						}
						else if(p.getInventory().hasItemId(138)) {
							message(p, "You give the bread to the beggar.");
							removeItem(p, 138, 1);
							npcTalk(p, beggar, "Thankyou very much");
							if(p.getCache().hasKey("lady_test")) {
								p.message("The beggar has turned into the lady of the lake!");
								Npc lady = transform(beggar, 284, false);
								npcTalk(p, lady, "Well done you have passed the test",
										"Here is excalibur, guard it well");
								addItem(p, 606, 1);
								if(p.getCache().hasKey("lady_test")) {
									p.getCache().remove("lady_test");
								}
								lady.remove();
							}
						}
					} else if (opt == 1) {
						npcTalk(p, beggar,
								"Well if you get some you know where to come");
						doDoor(obj, p);
						beggar.remove();
					}
				}
			}
		}

	}

	@Override
	public boolean blockDrop(Player p, Item i) {
		return p.getX() == 448 && p.getY() == 435 && i.getID() == 604
				&& p.getCache().hasKey("magic_words") && hasItem(p, 602);
	}

	@Override
	public void onDrop(Player p, Item i) {
		p.getInventory().remove(i);
		Npc n = spawnNpc(288, p.getX(), p.getY(), 300000);
		n.displayNpcTeleportBubble(n.getX(), n.getY());
		p.message("Suddenly a demon appears");
		playerTalk(p, null, "Now what were those magic words?");
		int opt = showMenu(p, n, "Snarthtrick Candanto Termon",
				"Snarthon Candtrick Termanto", "Snarthanto Candon Termtrick");
		if (opt == 1) {
			npcTalk(p, n, "rarrrrgh", "You have me in your control",
					"What do you wish of me?",
					"So that I may return to the nether regions");
			playerTalk(p, n, "I wish to free Merlin from his giant crystal");
			npcTalk(p, n, "rarrrrgh",
					"It is done, you can now shatter Merlin's crystal with Excailbur");
			n.remove();
			p.updateQuestStage(this, 4);
		} else {
			n.getUpdateFlags().setChatMessage(new ChatMessage(n, "rarrrrgh", p));
			n.startCombat(p);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if ((n.getID() == 275 && !p.getLocation().inVarrock()) || n.getID() == 274 || n.getID() == 273) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 275 && !p.getLocation().inVarrock()) {
			switch (p.getQuestStage(Constants.Quests.THE_HOLY_GRAIL)) {
			case 1:
			case 2:
			case 3:
			case 5:
				npcTalk(p,n, "How goes thy quest?");
				if(hasItem(p, 746)) {
					playerTalk(p,n, "I have retrieved the grail");
					npcTalk(p,n, "wow incredible you truly are a splendid knight");
					removeItem(p, 746, 1);
					p.sendQuestComplete(Constants.Quests.THE_HOLY_GRAIL);
				} else {
					playerTalk(p,n, "I am making progress",
							"But I have not recovered the grail yet");
					npcTalk(p,n, "Well the grail is very elusive",
							"It may take some perserverance");
					if(p.getQuestStage(Constants.Quests.THE_HOLY_GRAIL) == 1) {
						npcTalk(p, n, "As I said before speak to Merlin",
								"in the workshop by the library");
					}
				}
				return;
			case 4:
				playerTalk(p,n, "Hello, do you have a knight named Sir Percival?");
				npcTalk(p,n, "Ah yes I remember, young percival",
						"He rode off on a quest a couple of months ago",
						"We are getting a bit worried, he's not back yet",
						"He was going to try and recover the golden boots of Arkaneeses");
				playerTalk(p,n, "Any idea which way that would be?");
				npcTalk(p,n, "Not exactly",
						"We discovered, some magic golden feathers",
						"They are said to point the way to the boots",
						"they certainly point somewhere",
						"just blowing gently on them",
						"Will make them show the way to go");
				if(!hasItem(p, 745)) {
					p.message("King arthur gives you a feather");
					addItem(p, 745, 1);
				}
				return;
			case -1:
				npcTalk(p,n, "Thankyou for retrieving the grail",
						"You shall be long remembered",
						"As one of the greatest heros",
						"Amongst the knights of the round table");
				return;
			}
			/** KING ARTHUR MERLINS CRYSTAL**/
			switch (p.getQuestStage(this)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				npcTalk(p, n, "Welcome to the court of King Arthur");
				npcTalk(p, n, "I am King Arthur");
				int option = showMenu(p, n,
						"I want to become a knight of the round table",
						"So what are you doing in Runescape?",
						"Thankyou very much");
				if (option == 0) {
					npcTalk(p,
							n,
							"Well I think you need to go on a quest to prove yourself worthy",
							"My knights like a good quest",
							"Unfortunately our current quest is to rescue Merlin",
							"Back in England he got himself trapped in some sort of magical Crystal",
							"We've moved him from the cave we found him in",
							"He's upstairs in his tower");
					playerTalk(p, n, "I will see what I can do then");
					npcTalk(p, n, "Talk to my knights if you need any help");
					if (p.getQuestStage(this) == 0) {
						p.updateQuestStage(Constants.Quests.MERLINS_CRYSTAL, 1);
					}
				} else if (option == 1) {
					npcTalk(p, n,
							"Well legend says we will return to Britain in it's time of greatest need");
					npcTalk(p, n, "But that's not for quite a while");
					npcTalk(p, n,
							"So we've moved the whole outfit here for now");
					npcTalk(p, n, "We're passing the time in Runescape");
				}
				break;
			case 5:
				playerTalk(p, n, "I have freed Merlin from his crystal");
				npcTalk(p, n, "Ah a good job well done", "I knight thee",
						"You are now a knight of the round table");
				p.sendQuestComplete(Constants.Quests.MERLINS_CRYSTAL);
				break;
			case -1:
				playerTalk(p, n, "Now i am a knight of the round table",
						"Do you have anymore quests for me?");
				npcTalk(p,
						n,
						"Aha, I'm glad you are here",
						"I am sending out various knights on an important quest",
						"I was wondering if you too would like to take up this quest?");
				int q = showMenu(p, n, "Tell me of this quest",
						"I am weary of questing for the time being");
				if (q == 0) {
					/******************************/
					/**START OF THE HOLY GRAIL QUEST**/
					/******************************/
					npcTalk(p,n, "Well we recently found out",
							"The holy grail has passed into the runescape world",
							"This is most fortuitous",
							"None of my knights ever did return with it last time",
							"Now we have the opportunity to give it another go",
							"Maybe this time we will have more luck");
					int startHoly = showMenu(p,n,
							"I'd enjoy trying that",
							"I may come back and try that later");
					if(startHoly == 0) {
						npcTalk(p,n, "Go speak to Merlin",
								"He may be able to give a better clue as to where it is",
								"Now you have freed him from the crystal",
								"He has set up his workshop in the room next to the library");
						p.updateQuestStage(Constants.Quests.THE_HOLY_GRAIL, 1);
					} else if(startHoly == 1) {
						npcTalk(p,n, "Be sure that you come speak to me soon then");
					}
				} else if (q == 1) {
					npcTalk(p, n, "Maybe later then");
					playerTalk(p, n, "Maybe so");
				}
				break;
			}
		}
		if (n.getID() == 274) {
			if (p.getCache().hasKey("talked_to_gawain")) {
				npcTalk(p, n, "Good day to you sir");
				int option = showMenu(
						p,
						n,
						"Any idea how to get into Morgan Le Faye's stronghold?",
						"Hello again");
				if (option == 0) {
					npcTalk(p, n, "No you've got me stumped there");
				}
				return;
			}
			switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "Good day to you sir");
				int opt = showMenu(p, n, "Good day",
						"Know you of any quests Sir knight?");
				if (opt == 1) {
					npcTalk(p, n,
							"The king is the man to talk to if you want a quest");
				}
				break;
			case 1:
				npcTalk(p, n, "Good day to you sir");
				int option = showMenu(p, n, "Good day",
						"Any ideas on how to get Merlin out that crystal?",
						"Do you know how Merlin got trapped");
				if (option == 1) {
					npcTalk(p, n, "I'm a little stumped myself",
							"We've tried opening it with anything and everything");
				} else if (option == 2) {
					npcTalk(p, n,
							"I would guess this is the work of the evil Morgan Le Faye");
					playerTalk(p, n, "And where can I find her?");
					npcTalk(p, n,
							"She lives in her stronghold to the south of here");
					npcTalk(p, n,
							"Guarded by some renegade knights led by Sir Mordred");
					p.getCache().store("talked_to_gawain", true);
					int sub_option = showMenu(
							p,
							n,
							"Any idea how to get into Morgan Le Faye's stronghold?",
							"Thankyou for the information",
							"Do you know how Merlin got trapped");
					if (sub_option == 0) {
						npcTalk(p, n, "No you've got me stumped there");
					}
				}
				break;
			case 2:
				npcTalk(p, n, "Good day to you sir");
				int op = showMenu(p, n, "Good day",
						"Know you of any quests Sir knight?");
				if (op == 1) {
					npcTalk(p, n,
							"The king is the man to talk to if you want a quest");
				}
				break;
			case -1:
				npcTalk(p, n, "Good day to you sir");
				int ope = showMenu(p, n, "Good day",
						"Know you of any quests Sir knight?");
				if (ope == 1) {
					npcTalk(p,n, "I think you've done the main quest we were on right now");
				}
				break;
			}
		}
		if (n.getID() == 273) {
			switch (p.getQuestStage(this)) {
			case 0:
			case 1:
				npcTalk(p,
						n,
						"Greetings I am Sir Lancelot the greatest knight in the land",
						"What do you want?");
				if (p.getCache().hasKey("talked_to_gawain")) {
					int opt = showMenu(p, n,
							"I want to get Merlin out of the crystal",
							"You're a little full of yourself aren't you?",
							"Any ideas on how to get into Morgan Le Faye's stronghold?");
					if (opt == 0) {
						npcTalk(p, n,
								" Well the knights of the round table can't manage it");
						npcTalk(p, n,
								" I can't see how a commoner like you could succeed where we have failed");
					} else if (opt == 1) {
						npcTalk(p, n,
								"I have every right to be proud of myself",
								"My prowess in battle is world renowned");
					} else if (opt == 2) {
						npcTalk(p,
								n,
								"That stronghold is built in a strong defensive position",
								"It's on a big rock sticking out into the sea",
								"There are two ways in that I know of, the large heavy front doors",
								"And the sea entrance, only penetrable by boat",
								"They take all their deliveries by boat");
						p.updateQuestStage(Constants.Quests.MERLINS_CRYSTAL, 2);
						p.getCache().remove("talked_to_gawain");
					}
				} else {
					int opt = showMenu(p, n,
							"I want to get Merlin out of the crystal",
							"You're a little full of yourself aren't you",
							"Thankyou very much");
					if (opt == 0) {
						npcTalk(p,
								n,
								"Well the knights of the round table can't manage it",
								"I can't see how a commoner like you could succeed where we have failed");
					} else if (opt == 1) {
						npcTalk(p, n,
								"I have every right to be proud of myself",
								"My prowess in battle is world renowned");
					}
				}
				break;
			case 2:
			case -1:
				npcTalk(p,
						n,
						"Greetings I am Sir Lancelot the greatest knight in the land",
						"What do you want?");
				int opt = showMenu(p, n,
						"You're a little full of yourself aren't you?",
						"I seek a quest");
				if (opt == 0) {
					npcTalk(p, n, "I have every right to be proud of myself",
							"My prowess in battle is world renowned");
				} else if (opt == 1) {
					npcTalk(p, n, "Leave questing to the profesionals",
							"Such as myself");
				}
				break;
			}

		}
	}

}
