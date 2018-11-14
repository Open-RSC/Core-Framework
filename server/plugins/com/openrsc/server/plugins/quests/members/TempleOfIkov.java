package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class TempleOfIkov implements QuestInterface,TalkToNpcListener,
TalkToNpcExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener, PickupListener, PickupExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener, PlayerMageNpcListener, PlayerMageNpcExecutiveListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener, PlayerAttackNpcListener, PlayerAttackNpcExecutiveListener, PlayerRangeNpcListener, PlayerRangeNpcExecutiveListener {

	/** Quest Npcs **/
	public static int LUCIEN = 360;
	public static int EDGE_LUCIEN = 364;
	public static int WARRIOR_OF_LESARKUS = 361;
	public static int WINELDA = 365;

	/** Quest Objects **/
	public static int STAIR_DOWN = 370;
	public static int STAIR_UP = 369;
	public static int LEVER = 361;
	public static int LEVER_BRACKET = 367;
	public static int COMPLETE_LEVER = 368;

	/** Quest Items **/
	public static int PIECE_OF_LEVER = 724;
	public static int ICE_ARROW = 723;
	public static int PENDANT_OF_LUCIEN = 721;
	public static int BOOTS = 722; 
	public static int LIMPROOT = 220;
	public static int STAFF_OF_ARMADYL = 725;
	public static int GUARDIAN_PENDANT = 726;

	class Lucien {
		public static final int SOUNDSFUN = 0;
		public static final int PAYME = 1;
	}
	class Winelda {
		public static final int YES = 0;
	}
	class Guardian {
		public static final int WORKINGFORLUCIEN = 0;
	}


	@Override
	public int getQuestId() {
		return Constants.Quests.TEMPLE_OF_IKOV;
	}

	@Override
	public String getQuestName() {
		return "Temple of Ikov (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.getCache().remove("openSpiderDoor");
		p.getCache().remove("completeLever");
		p.getCache().remove("killedLesarkus");
		p.incQuestExp(RANGED, (p.getSkills().getMaxStat(RANGED) * 1000) + 2000);
		p.incQuestExp(FLETCHING, (p.getSkills().getMaxStat(FLETCHING) * 1000) + 2000);
		p.message("@gre@You haved gained 1 quest point!");
		p.incQuestPoints(1);
		p.message("Well done you have completed the temple of Ikov quest");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == LUCIEN || n.getID() == WINELDA || n.getID() == 363 || n.getID() == 362 || n.getID() == EDGE_LUCIEN) {
			return true;
		}
		return false;
	}

	private void lucienDialogue(Player p, Npc n, int cID) {
		if (n.getID() == LUCIEN) {
			if (cID == -1) {
				switch(p.getQuestStage(this)) {
				case 0:
					npcTalk(p,n, "I come here seeking a hero who can help me");
					int menu = showMenu(p,n,
							"I am a hero",
							"Yep lots of heroes about here");
					if(menu == 0) {
						npcTalk(p,n, "I need someone who can enter the tunnels under the deserted temple of Ikov",
								"Near Hemenster, to the north of here",
								"Kill the fire warrior of Lesarkus",
								"And retrieve the staff of Armardyl");
						int newMenu = showMenu(p,n,
								"Why can't you do it yourself?",
								"That sounds like fun",
								"That sounds too dangerous for me",
								"How much will you pay me?");
						if(newMenu == 0) {
							npcTalk(p,n, "The guardians of the staff of Armardyl fear me",
									"They know my kind is powerful",
									"So they have set up magical wards against are race");
							int newMenu2 = showMenu(p,n,
									"How much will you pay me?",
									"That sounds like fun",
									"Who are your kind?",
									"That sounds too dangerous for me");
							if(newMenu2 == 0) {
								lucienDialogue(p, n, Lucien.PAYME);
							} else if(newMenu2 == 1) {
								lucienDialogue(p, n, Lucien.SOUNDSFUN);
							} else if(newMenu2 == 2) {
								npcTalk(p,n, "An ancient and powerful race",
										"Back in the second age we held great influence in this world",
										"There are few of us left now");
							} else if(newMenu2 == 3) {
								npcTalk(p,n, "Fortune favours the bold");
							}

						} else if(newMenu == 1) {
							lucienDialogue(p, n, Lucien.SOUNDSFUN);
						} else if(newMenu == 2) {
							npcTalk(p,n, "Fortune favours the bold");
						} else if(newMenu == 3) {
							lucienDialogue(p, n, Lucien.PAYME);
						}
					}
					break;
				case 1:
				case 2:
				case -1:
					npcTalk(p,n, "I thought I told you not to meet me here again");
					if(hasItem(p, PENDANT_OF_LUCIEN)) {
						playerTalk(p,n, "Yes you did, sorry");
					} else {
						int lostAmuletMenu = showMenu(p,n,
								"I lost that pendant you gave me",
								"Yes you did sorry");
						if(lostAmuletMenu == 0) {
							npcTalk(p,n, "Hmm",
									"Imbecile");
							p.message("Lucien gives you another pendant");
							addItem(p, PENDANT_OF_LUCIEN, 1);
						} 
					}
					break;
				}
			} switch(cID) {
			case Lucien.SOUNDSFUN:
				npcTalk(p,n, "Well it's not that easy",
						"The fire warrior can only be killed with a weapon of ice",
						"And there are many other traps and hazards in those tunnels");
				playerTalk(p,n, "Well I am brave I shall give it a go");
				npcTalk(p,n, "Take this pendant you will need it to get through the chamber of fear");
				addItem(p, PENDANT_OF_LUCIEN, 1);
				npcTalk(p,n, "It is not safe for me to linger here much longer",
						"When you have done meet me in the forest north of Varrock",
						"I have a small holding up there");
				p.updateQuestStage(this, 1);
				break;
			case Lucien.PAYME:
				npcTalk(p,n, "Ah the mercenary type I see");
				playerTalk(p,n, "It's a living");
				npcTalk(p,n, "I shall adequately reward you",
						"With both money and power");
				playerTalk(p,n, "Sounds rather too vague for me");
				break;
			}
		}
	}
	private void wineldaDialogue(Player p, Npc n, int cID) {
		if(n.getID() == WINELDA) {
			if(cID == -1) {
				if(hasItem(p, LIMPROOT, 20)) {
					playerTalk(p,n, "I have the 20 limpwurt roots, now transport me please");
					npcTalk(p,n, "Oh marverlous",
							"Brace yourself then");
					removeItem(p, LIMPROOT, 20);
					p.teleport(557, 3290);
					sleep(650);
					ActionSender.sendTeleBubble(p, p.getX(), p.getY(), false);
				} else {
					npcTalk(p,n, "Hehe in a bit of a pickle are we?",
							"Want to be getting over the nasty lava stream do we?");
					int menu = showMenu(p,n,
							"Not really, no",
							"Yes we do",
							"Yes I do");
					if(menu == 0) {
						npcTalk(p,n, "Hehe ye'll come back later",
								"hey always come back later");
					} else if(menu == 1) {
						wineldaDialogue(p, n, Winelda.YES);
					} else if(menu == 2) {
						wineldaDialogue(p, n, Winelda.YES);
					}
				}
			} switch(cID) {
			case Winelda.YES:
				npcTalk(p,n, "Well keep it under your helmet",
						"But I'm knowing some useful magic tricks",
						"I could get you over there easy as that");
				playerTalk(p,n, "Okay get me over there");
				npcTalk(p,n, "Okay brace yourself");
				npcTalk(p,n, "Actually no no",
						"Why should I do it for free",
						"Bring me a bite to eat and I'll be a touch more helpful",
						"How about some nice tasty limpwurt roots to chew on",
						"Yes yes that's good, bring me 20 limpwurt roots and over you go");
				break;
			}
		}
	}

	private void guardianDialogue(Player p, Npc n, int cID) {
		if(n.getID() == 362 || n.getID() == 363) {
			if(cID == -1) {
				switch(p.getQuestStage(this)) {
				case 1:
					if(p.getInventory().wielding(PENDANT_OF_LUCIEN) && !hasItem(p, STAFF_OF_ARMADYL)) {
						npcTalk(p,n, "Ahh tis a foul agent of Lucien",
								"Get ye from our master's house");
						if(n != null) {
							n.startCombat(p);
						}
						return;
					} 
					if(hasItem(p, STAFF_OF_ARMADYL)) {
						npcTalk(p,n, "Stop",
								"You cannot take the staff of Armadyl");
						n.setChasing(p);
						return;
					}
					npcTalk(p,n, "Thou dost venture deep in the tunnels",
							"It has been many a year since someone has passed thus far");
					int menu = showMenu(p,n,
							"I seek the staff of Armadyl",
							"Out of my way fool",
							"Who are you?");
					if(menu == 0) {
						npcTalk(p,n, "We guard that here",
								"As did our fathers",
								"And our father's fathers",
								"Why dost thou seeketh it?");
						int seekMenu = showMenu(p,n,
								"A guy named Lucien is paying me",
								"Just give it to me",
								"I am a collector of rare and powerful artifacts");
						if(seekMenu == 0) {
							guardianDialogue(p, n, Guardian.WORKINGFORLUCIEN);
						} else if(seekMenu == 1) {
							npcTalk(p,n, "The staff is a sacred object",
									"Not to be given away to anyone who asks");

						} else if(seekMenu == 2) {
							npcTalk(p,n, "The staff is not yours to collect");
						}

					} else if(menu == 1) {
						npcTalk(p,n, "I may be a fool, but I will not step aside");
						int foolMenu = showMenu(p,n,
								"Why not?",
								"Then I must strike you down",
								"Then I guess I will turn back");
						if(foolMenu == 0) {
							npcTalk(p,n, "Only members of our order are allowed further");
						} else if(foolMenu == 1) {
							n.startCombat(p);
						}
					} else if(menu == 2) {
						npcTalk(p,n, "I am a guardian of Armadyl",
								"We have kept this place safe and holy",
								"For many generations",
								"Many evil souls would like to get their hands on what lies here",
								"Especially the Mahjarrat");
						int whoMenu = showMenu(p,n,
								"What is an Armadyl?",
								"Who are the Mahjarrat?",
								"Wow you must be old");
						if(whoMenu == 0) {
							npcTalk(p,n, "Armadyl is our God",
									"We are his servants",
									"Who have the honour to stay here",
									"And guard his artifacts",
									"Till he needs them to smite his enemies");
							int bla = showMenu(p,n,
									"Ok that's nice to know",
									"Someone told me there were only three gods");
							if(bla == 0) {
								playerTalk(p,n, "I am a collector of rare and powerful objects");
								npcTalk(p,n, "The staff is not yours to collect");
							} else if(bla == 1) {
								playerTalk(p,n, "Saradomin, Zamorak and Guthix");
								npcTalk(p,n, "Was that someone a Saradominist?",
										"I hear Saradominism is the principle doctrine",
										"Out in the world currently",
										"They only Acknowledge those three gods",
										"They are wrong",
										"Depending on what you define as a god",
										"We are aware of at least twenty");

							}
						} else if(whoMenu == 1) {
							npcTalk(p,n, "Ancient powerful beings",
									"They are very evil",
									"They were said to once dominate this plane of existance",
									"Zamorak was said to once have been of their stock",
									"They are few in number and have less power these days",
									"Some still have presence in this world in their liche forms",
									"Mahjarrat such as Lucien and Azzanadra would become extremely powerful",
									"If they got their hands on the staff of Armadyl");
							int maj = showMenu(p,n,
									"Did you say Lucien?",
									"You had better guard it well then");
							if(maj == 0) {
								playerTalk(p, n, "He's the one who sent me to fetch the staff");
								guardianDialogue(p, n, Guardian.WORKINGFORLUCIEN);
							} else if(maj == 1) {
								npcTalk(p,n, "Don't fret, for we shall");
							}
						} else if(whoMenu == 2) {
							npcTalk(p,n, "No no, I have not guarded here for all those generations",
									"Many generations of my family have though");
						}
					}
					break;
				case 2:
					npcTalk(p,n, "Any luck against Lucien?");
					if(!hasItem(p, GUARDIAN_PENDANT)) {
						int option = showMenu(p, n, "Not yet", "No I've lost the pendant you gave me");
						if(option == 0) {
							npcTalk(p,n, "Well good luck on your quest");
						} else if(option == 1) {
							npcTalk(p,n, "Thou art a careless buffoon",
									"Have another one");
							p.message("The guardian gives you a pendant");
							addItem(p, GUARDIAN_PENDANT, 1);
						}
					} else {
						playerTalk(p,n, "Not yet");
						npcTalk(p,n, "Well good luck on your quest");
					}
					break;
				case -1:
					playerTalk(p,n, "I have defeated Lucien");
					npcTalk(p,n, "Well done",
							"We can only hope that will keep him quiet for a while");
					break;
				case -2:
					npcTalk(p, n, "Get away from here", "Thou evil agent of Lucien");
					break;
				}
			} switch(cID) {
			case Guardian.WORKINGFORLUCIEN:
				npcTalk(p,n, "Thou art working for him?",
						"Thy fool",
						"Quick you must be cleansed to save your soul");
				int menu = showMenu(p,n,
						"How dare you call me a fool?",
						"Erm I think I'll be leaving now",
						"Yes I could do with a bath");
				if(menu == 0) {
					playerTalk(p,n, "I will work for who I please");
					npcTalk(p,n, "This one is too far gone",
							"He must be cut down to stop the spread of the blight");
					n.startCombat(p);
				} else if(menu == 1) {
					npcTalk(p,n, "We cannot allow an agent of Lucien to roam free");
					n.startCombat(p);
				} else if(menu == 2) {
					p.message("The guardian splashes holy water over you");
					npcTalk(p,n, "That should do the trick",
							"Now you say that Lucien sent you to retrieve the staff",
							"He must not get a hold of it",
							"He would become too powerful with the staff",
							"Hast thou heard of the undead necromancer?",
							"Who raised an undead army against Varrock a few years past",
							"That was Lucien",
							"If thou knowest where to find him maybe you can help us against him");
					int lastMenu = showMenu(p,n,
							"Ok I will help",
							"No I shan't turn against my employer",
							"I need time to consider this");
					if(lastMenu == 0) {
						npcTalk(p, n, "So you know where he lurks?");
						playerTalk(p, n, "Yes");
						npcTalk(p, n, "He must be growing in power again if he is after the staff",
								"If you can defeat him, it may weaken him for a time",
								"You will need to use this pendant to even be able to attack him");
						p.message("The guardian gives you a pendant");
						addItem(p, GUARDIAN_PENDANT, 1);
						p.updateQuestStage(this, 2);
					} else if(lastMenu == 1) {
						npcTalk(p,n, "This one is too far gone",
								"He must be cut down to stop the spread of the blight");
						n.startCombat(p);
					} else if(lastMenu == 2) {
						npcTalk(p,n, "Come back when you have made your choice");
					}
				}
				break;
			}
		}
	}


	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == EDGE_LUCIEN) {
			if(p.getQuestStage(this) == -1 || p.getQuestStage(this) == -2) {
				p.message("You have already completed this quest");
				return;
			}
			npcTalk(p,n, "Have you got the staff of Armadyl yet?");
			if(hasItem(p, 725)) {
				int menu = showMenu(p,n,
						"Yes here it is",
						"No not yet");
				if(menu == 0) {
					message(p, "You give the staff to Lucien");
					removeItem(p, STAFF_OF_ARMADYL, 1);
					npcTalk(p,n, "Muhahahaha",
							"Already I can feel the power of this staff running through my limbs",
							"Soon I shall be exceedingly powerful",
							"I suppose you would like a reward now",
							"I shall grant you much power");
					p.message("A glow eminates from Lucien's helmet");
					p.sendQuestComplete(Constants.Quests.TEMPLE_OF_IKOV);
					p.updateQuestStage(this, -2);
					npcTalk(p,n, "I must be away now to make preparations for my conquest",
							"Muhahahaha");
					n.remove();

				}
			} else {
				playerTalk(p,n, "No not yet");
			}	
		}
		if(n.getID() == LUCIEN) {
			lucienDialogue(p, n, -1);
		}
		if(n.getID() == WINELDA) {
			wineldaDialogue(p, n, -1);

		}
		if(n.getID() == 363 || n.getID() == 362) {
			guardianDialogue(p, n, -1);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == STAIR_DOWN || obj.getID() == STAIR_UP) {
			return true;
		}
		if(obj.getID() == LEVER || obj.getID() == COMPLETE_LEVER) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == STAIR_DOWN) {
			if(hasItem(p, 601)) {
				p.message("Your flame lights up the room");
				p.teleport(537, 3372);
			} else {
				p.message("You cannot see any further into the room");
				p.teleport(537, 3394);
				sleep(1600);
				p.message("It is too dark");
			}
		}
		if(obj.getID() == STAIR_UP) {
			p.teleport(536, 3338);
		}
		if(obj.getID() == LEVER) {
			if(command.equals("pull")) {
				if(!p.getCache().hasKey("ikovLever")) {
					p.message("You have activated a trap on the lever");
					p.damage(DataConversions.roundUp(p.getSkills().getLevel(3) / 5));
				} else {
					message(p, "You pull the lever",
							"You hear a clunk",
							"The trap on the lever resets");
					if(p.getCache().hasKey("ikovLever")) {
						p.getCache().remove("ikovLever");
					}
					if(!p.getCache().hasKey("openSpiderDoor") && (p.getQuestStage(this) != -1 || p.getQuestStage(this) != -2)) {
						p.getCache().store("openSpiderDoor", true);
					}
				}
			} else if(command.equals("searchfortraps")) {
				p.message("You search the lever for traps");
				if(p.getSkills().getMaxStat(17) < 42) {
					p.message("You have not high thieving enough to disable this trap");
					return;
				}
				message(p, "You find a trap on the lever",
						"You disable the trap");
				if(!p.getCache().hasKey("ikovLever")) {
					p.getCache().store("ikovLever", true);
				}
			}
		}
		if(obj.getID() == COMPLETE_LEVER) {
			message(p, "You pull the lever",
					"You hear the door next to you make a clunking noise");
			if(!p.getCache().hasKey("completeLever") && (p.getQuestStage(this) != -1 || p.getQuestStage(this) != -2)) {
				p.getCache().store("completeLever", true);
			}
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if(i.getID() == ICE_ARROW) {
			return true;
		}
		if(i.getID() == STAFF_OF_ARMADYL) {
			Npc guardian = getMultipleNpcsInArea(p, 5, 362, 363);
			if(guardian == null)
				return false;
			else 
				return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if(i.getID() == ICE_ARROW) {
			if(i.getX() == 560 && i.getY() == 3352 || i.getX() == 563 && i.getY() == 3354) {
				addItem(p, ICE_ARROW, 1);
				p.teleport(538, 3348);
				sleep(650);
				ActionSender.sendTeleBubble(p, p.getX(), p.getY(), false);
				sleep(1000);
				p.message("Suddenly your surroundings change");
			} else {
				message(p, "You can only take ice arrows from the cave of ice spiders",
						"In the temple of Ikov");
			}
		}
		if(i.getID() == STAFF_OF_ARMADYL) {
			if(p.getQuestStage(this) == 2 || p.getQuestStage(this) == -1 || p.getQuestStage(this) == -2) {
				p.message("I shouldn't steal this");
				return;
			}
			if(hasItem(p, STAFF_OF_ARMADYL)) {
				p.message("I already have one of those");
				return;
			}
			Npc n = getMultipleNpcsInArea(p, 5, 362, 363);
			if(n != null) {
				npcTalk(p,n, "That is not thine to take");
				n.setChasing(p);
				return;
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if(item.getID() == PIECE_OF_LEVER && obj.getID() == LEVER_BRACKET) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if(item.getID() == PIECE_OF_LEVER && obj.getID() == LEVER_BRACKET) {
			p.message("You fit the lever into the bracket");
			removeItem(p, PIECE_OF_LEVER, 1);
			World.getWorld().replaceGameObject(obj,
					new GameObject(obj.getLocation(), COMPLETE_LEVER, obj.getDirection(), obj
							.getType()));
			World.getWorld().delayedSpawnObject(obj.getLoc(), 15000);
		}
	}

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == WARRIOR_OF_LESARKUS) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if(n.getID() == WARRIOR_OF_LESARKUS) {
			if(p.getCache().hasKey("killedLesarkus") || p.getQuestStage(this) == -1 || p.getQuestStage(this) == -2) {
				p.message("You have already killed the fire warrior");
				return;
			}
			p.message("You need to kill the fire warrior with ice arrows");
		}
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == WARRIOR_OF_LESARKUS) {
			return true;
		}
		if(n.getID() == EDGE_LUCIEN) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == WARRIOR_OF_LESARKUS) {
			n.killedBy(p);
			if(!p.getCache().hasKey("killedLesarkus")) {
				p.getCache().store("killedLesarkus", true);
			}
		}
		if(n.getID() == EDGE_LUCIEN) {
			if(p.getQuestStage(this) == -1 || p.getQuestStage(this) == -2) {
				p.message("You have already completed this quest");
				n.getSkills().setLevel(3, n.getSkills().getMaxStat(3));
				return;
			}
			n.getSkills().setLevel(3, n.getSkills().getMaxStat(3));
			npcTalk(p, n, "You may have defeated me for now",
					"But I will be back");
			p.sendQuestComplete(Constants.Quests.TEMPLE_OF_IKOV);
			n.displayNpcTeleportBubble(n.getX(), n.getY());
			n.remove();
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if(n.getID() == EDGE_LUCIEN) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerAttackNpc(Player p, Npc n) {
		if(n.getID() == EDGE_LUCIEN) {
			if(p.getQuestStage(this) == -1 || p.getQuestStage(this) == -2) {
				p.message("You have already completed this quest");
			} else {
				if(p.getInventory().wielding(GUARDIAN_PENDANT)) {
					p.startCombat(n);
				} else {
					npcTalk(p,n, "I'm sure you don't want to attack me really",
							"I am your friend");
					message(p, "You decide you don't want to attack Lucien really",
							"He is your friend");
				}
			}
		}
	}

	@Override
	public boolean blockPlayerRangeNpc(Player p, Npc n) {
		if(n.getID() == EDGE_LUCIEN && (p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -1 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -2 || !p.getInventory().wielding(726))) {
			return true;
		}		
		if(n.getID() == WARRIOR_OF_LESARKUS && (p.getCache().hasKey("killedLesarkus") || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -1 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -2 )) {
			return true;
			}
			else { 
				if (n.getID() == WARRIOR_OF_LESARKUS && ((p.getInventory().hasItemId(723) && hasGoodBow(p)) || p.getCache().hasKey("shot_ice"))) {			
				p.getCache().store("shot_ice", true);
				return false;
			}
		}
		if(n.getID() == WARRIOR_OF_LESARKUS && !p.getCache().hasKey("shot_ice")) {					
			return true;			
		}
		return false;
	}
	
	public boolean hasGoodBow(Player p) {
		
		int[] allowedBowsIce = {654, 655, 656, 657};
		boolean hasGoodBow = false;
		
		for(int bow : allowedBowsIce) {
			hasGoodBow |= (p.getInventory().hasItemId(bow) && p.getInventory().wielding(bow));
		}
		
		return hasGoodBow;
	}

	@Override
	public void onPlayerRangeNpc(Player p, Npc n) {
		if(n.getID() == EDGE_LUCIEN) {
			if(p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -1 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -2) {
				p.message("You have already completed this quest");
				return;
			}
			if(!p.getInventory().wielding(726)) {
				p.message("You decide you don't want to attack Lucien really, He is your friend");
				return;
			}
		}
		if(n.getID() == WARRIOR_OF_LESARKUS) {			
			if((p.getCache().hasKey("killedLesarkus") || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -1 || p.getQuestStage(Constants.Quests.TEMPLE_OF_IKOV) == -2)) {
				p.message("You have already killed the fire warrior");
				return;
			}
			if(!p.getCache().hasKey("shot_ice")) {
				p.message("You need to kill the fire warrior with ice arrows");
				return;
			}
		}
	}
}
