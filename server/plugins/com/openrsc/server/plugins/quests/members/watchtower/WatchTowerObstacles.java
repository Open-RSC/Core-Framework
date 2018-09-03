package com.openrsc.server.plugins.quests.members.watchtower;

import static com.openrsc.server.plugins.Functions.AGILITY;
import static com.openrsc.server.plugins.Functions.THIEVING;
import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.coordModifier;
import static com.openrsc.server.plugins.Functions.delayedSpawnObject;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.openChest;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.replaceObject;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

/**
 * 
 * @author Imposter/Fate
 *
 */
public class WatchTowerObstacles implements ObjectActionListener, ObjectActionExecutiveListener, WallObjectActionListener, WallObjectActionExecutiveListener {


	/** OBJECT IDs **/
	public static int TOWER_FIRST_FLOOR_LADDER = 659;
	public static int COMPLETED_QUEST_LADDER = 1017;
	public static int TOWER_SECOND_FLOOR_LADDER = 1021;
	public static int WATCHTOWER_LEVER = 1014;
	public static int WATCHTOWER_LEVER_DOWNPOSITION = 1015;
	public static int[] WRONG_BUSHES = { 960 };
	public static int[] CORRECT_BUSHES = { 993, 961, 992, 991, 990 };
	public static int[] TELEPORT_CAVES = { 970, 972, 950, 971, 949, 975 };
	public static int[] CAVE_EXITS = { 188, 189, 190, 191, 187, 192 };
	public static int TUNNEL_CAVE = 998;
	public static int TOBAN_CHEST = 978;
	public static int ISLAND_LADDER = 997;
	public static int BATTLEMENT = 201;
	public static int SOUTH_WEST_BATTLEMENT = 195;
	public static int WRONG_STEAL_COUNTER = 973;
	public static int OGRE_CAVE_ENCLAVE = 955;
	public static int ROCK_CAKE_COUNTER = 999;
	public static int ROCK_CAKE_COUNTER_EMPTY = 1034;
	public static int CHEST_WEST = 1003;
	public static int ROCK_OVER = 995;
	public static int ROCK_BACK = 996;
	public static int CHEST_EAST = 1001;
	public static int DARK_PLACE_ROCKS = 1007;
	public static int DARK_PLACE_TELEPORT_ROCK = 1008;
	public static int YANILLE_HOLE = 968;
	public static int SKAVID_HOLE = 969;
	public static int OGRE_ENCLAVE_EXIT = 1024;


	/** NPC IDs **/
	public static int TOWER_GUARD = 575;

	/** ITEM IDs **/
	public static int ARMOUR = 1235;
	public static int FINGERNAILS = 1036;
	public static int EYE_PATCH = 1237;
	public static int ROBE = 1234;
	public static int DAGGER = 1236;
	public static int STOLEN_GOLD = 1040;
	public static int ROCK_CAKE = 1061;
	public static int SKAVID_MAP = 1045;
	public static int LIT_CANDLE = 601;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == TOWER_FIRST_FLOOR_LADDER || obj.getID() == COMPLETED_QUEST_LADDER || obj.getID() == TOWER_SECOND_FLOOR_LADDER) {
			return true;
		}
		if(obj.getID() == WATCHTOWER_LEVER || obj.getID() == WATCHTOWER_LEVER_DOWNPOSITION) {
			return true;
		}
		if(inArray(obj.getID(), WRONG_BUSHES)) {
			return true;
		}
		if(inArray(obj.getID(), CORRECT_BUSHES)) {
			return true;
		}
		if(inArray(obj.getID(), TELEPORT_CAVES)) {
			return true;
		}
		if(obj.getID() == TUNNEL_CAVE || obj.getID() == TOBAN_CHEST || obj.getID() == ISLAND_LADDER) {
			return true;
		}
		if(obj.getID() == WRONG_STEAL_COUNTER || obj.getID() == OGRE_CAVE_ENCLAVE || obj.getID() == ROCK_CAKE_COUNTER || obj.getID() == ROCK_CAKE_COUNTER_EMPTY) {
			return true;
		}
		if(obj.getID() == CHEST_WEST || obj.getID() == CHEST_EAST) {
			return true;
		}
		if(obj.getID() == ROCK_OVER || obj.getID() == ROCK_BACK) {
			return true;
		}
		if(obj.getID() == DARK_PLACE_ROCKS || obj.getID() == DARK_PLACE_TELEPORT_ROCK) {
			return true;
		}
		if(obj.getID() == YANILLE_HOLE || obj.getID() == SKAVID_HOLE) {
			return true;
		}
		if(obj.getID() == OGRE_ENCLAVE_EXIT) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == COMPLETED_QUEST_LADDER) {
			p.teleport(636, 1684);
		}
		if(obj.getID() == TOWER_SECOND_FLOOR_LADDER) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				p.teleport(492, 3524);
			} else {
				p.teleport(636, 2628);
			}
		}
		if(obj.getID() == TOWER_FIRST_FLOOR_LADDER) {
			Npc t_guard = getNearestNpc(p, TOWER_GUARD, 5);
			switch(p.getQuestStage(Constants.Quests.WATCHTOWER)) {
			case 0:
				if(t_guard != null) {
					npcTalk(p,t_guard, "You can't go up there",
							"That's private that is");
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
			case 9:
			case 10:
			case -1:
				npcTalk(p,t_guard, "It is the wizards helping hand",
						"Let 'em up");
				int[] coords = coordModifier(p, true, obj);
				p.teleport(coords[0], coords[1], false);
				break;
			}
		}
		if(obj.getID() == OGRE_ENCLAVE_EXIT) {
			p.teleport(662, 788);
		}
		if(obj.getID() == WATCHTOWER_LEVER_DOWNPOSITION) {
			p.message("The lever is stuck in the down position");
		}
		if(obj.getID() == WATCHTOWER_LEVER) {
			replaceObject(obj, new GameObject(obj.getLocation(), 1015, obj.getDirection(), obj.getType()));
			delayedSpawnObject(obj.getLoc(), 2000);
			p.message("You pull the lever");
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 10) {
				p.message("The magic forcefield activates");
				p.teleport(492, 3521);
				removeItem(p, 1037, 1);
				removeItem(p, 1152, 1);
				removeItem(p, 1153, 1);
				removeItem(p, 1154, 1);
				Npc wizard = getNearestNpc(p, 672, 6);
				if(wizard != null) {
					p.incQuestPoints(4);
					p.message("@gre@You haved gained 4 quest points!");
					npcTalk(p, wizard, "Marvellous! it works!",
							"The town will now be safe",
							"Your help was invaluable",
							"Take this payment as a token of my gratitude...");
					p.message("The wizard gives you 5000 pieces of gold");
					addItem(p, 10, 5000);
					npcTalk(p, wizard, "Also, let me improve your magic level for you");
					p.message("The wizard lays his hands on you...");
					p.message("You feel magic power increasing");
					p.incQuestExp(6, (p.getSkills().getMaxStat(6) + 1) * 1000);
					npcTalk(p, wizard, "Here is a special item for you...");
					addItem(p, 1181, 1);
					npcTalk(p, wizard, "It's a new spell",
							"Read the scroll and you will be able",
							"To teleport yourself to here magically...");
					p.message("Congratulations, you have finished the watchtower quest");
					p.sendQuestComplete(Constants.Quests.WATCHTOWER);
					/* TODO REMOVE ALL CACHES AND USE QUEST -1 */
				} else {
					p.message("Seems like the wizards were busy, please go back and complete again");
				}
			} else {
				p.message("It had no effect");
			}
		}
		if(inArray(obj.getID(), WRONG_BUSHES)) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 0) {
				playerTalk(p,null, "I am not sure why I am searching this bush...");
				return;
			}
			playerTalk(p,null, "Hmmm, nothing here");
		}
		if(inArray(obj.getID(), CORRECT_BUSHES)) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 0) {
				playerTalk(p,null, "I am not sure why I am searching this bush...");
				return;
			}
			if(obj.getID() == CORRECT_BUSHES[0]) {
				playerTalk(p,null, "Here's Some armour, it could be evidence...");
				addItem(p, ARMOUR, 1);
			} else if(obj.getID() == CORRECT_BUSHES[1]) {
				if(!hasItem(p, FINGERNAILS)) {
					playerTalk(p,null, "What's this ?",
							"Disgusting! some fingernails",
							"They may be a clue though... I'd better take them");
					addItem(p, FINGERNAILS, 1);
				} else {
					playerTalk(p,null, "I have already searched this place");
				}
			} else if(obj.getID() == CORRECT_BUSHES[2]) {
				if(!hasItem(p, EYE_PATCH)) {
					playerTalk(p,null, "I've found an eyepatch, I better show this to the wizards");
					addItem(p, EYE_PATCH, 1);
				} else {
					playerTalk(p,null, "I have already searched this place");
				}
			} else if(obj.getID() == CORRECT_BUSHES[3]) {
				if(!hasItem(p, ROBE)) {
					playerTalk(p,null, "Aha! a robe");
					addItem(p, ROBE, 1);
					playerTalk(p,null, "This could be a clue...");
				} else {
					playerTalk(p,null, "I have already searched this place");
				}
			} else if(obj.getID() == CORRECT_BUSHES[4]) {
				playerTalk(p,null, "Aha a dagger");
				addItem(p, DAGGER, 1);
				playerTalk(p,null, "I wonder if this is evidence...");
			}
		}

		if(inArray(obj.getID(), TELEPORT_CAVES)) {
			if(hasItem(p, SKAVID_MAP)) {
				if(hasItem(p, LIT_CANDLE)) {
					p.message("You enter the cave");
					if(obj.getID() == TELEPORT_CAVES[0]) {
						p.teleport(650, 3555);
					} else if(obj.getID() == TELEPORT_CAVES[1]) {
						p.teleport(626, 3564);
					} else if(obj.getID() == TELEPORT_CAVES[2]) {
						p.teleport(627, 3591);
					} else if(obj.getID() == TELEPORT_CAVES[3]) {
						p.teleport(638, 3564);
					} else if(obj.getID() == TELEPORT_CAVES[4]) {
						p.teleport(629, 3574);
					} else if(obj.getID() == TELEPORT_CAVES[5]) {
						p.teleport(647, 3596);
					}
				} else {
					p.teleport(629, 3558);
					playerTalk(p,null, "Oh my! It's dark!",
							"All I can see are lots of rocks on the floor",
							"I suppose I better search them for a way out");
				}
			} else {
				p.message("There's no way I can find my way through without a map of some kind");
				if(obj.getID() == TELEPORT_CAVES[0] || obj.getID() == TELEPORT_CAVES[4]) {
					p.teleport(629, 777);
				} else if(obj.getID() == TELEPORT_CAVES[1]) {
					p.teleport(624, 807);
				} else if(obj.getID() == TELEPORT_CAVES[2]) {
					p.teleport(648, 769);
				} else if(obj.getID() == TELEPORT_CAVES[3]) {
					p.teleport(631, 789);
				} else if(obj.getID() == TELEPORT_CAVES[5]) {
					p.teleport(638, 777);
				} 
			}
		}
		if(obj.getID() == TUNNEL_CAVE) {
			p.message("You enter the cave");
			p.teleport(605, 803);
			playerTalk(p,null, "Wow! that tunnel went a long way");
		}
		if(obj.getID() == TOBAN_CHEST) {
			if(hasItem(p, WatchTowerDialogues.KEY, 1)) {
				p.message("You use the key Og gave you");
				removeItem(p, WatchTowerDialogues.KEY, 1);
				openChest(obj, 2000, 979);
				if(hasItem(p, STOLEN_GOLD)) {
					message(p, "You have already got the stolen gold");
				} else {
					p.message("You find a stash of gold inside");
					message(p, "You take the gold");
					addItem(p, STOLEN_GOLD, 1);
				}
				p.message("The chest springs shut");
			} else {
				p.message("The chest is locked");
				playerTalk(p,null, "I think I need a key of some sort...");
			}
		}
		if(obj.getID() == ISLAND_LADDER) {
			p.message("You climb down the ladder");
			p.teleport(669, 826);
		}
		if(obj.getID() == WRONG_STEAL_COUNTER) {
			p.message("You find nothing to steal");
		}
		if(obj.getID() == OGRE_CAVE_ENCLAVE) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				p.message("The ogres have blocked this entrance now");
				// TODO should we sell this entrance for 100,000 coins or etc?
				return;
			}
			Npc ogre_guard = getNearestNpc(p, 684, 5);
			if(ogre_guard != null) {
				npcTalk(p,ogre_guard, "No you don't!");
				ogre_guard.startCombat(p);
			}
		}
		if(obj.getID() == ROCK_CAKE_COUNTER) {
			Npc ogre_trader = getNearestNpc(p, 688, 5);
			if(ogre_trader != null) {
				npcTalk(p,ogre_trader, "Grr! get your hands off those cakes");
				ogre_trader.startCombat(p);
			} else {
				if(getCurrentLevel(p, THIEVING) < 15) {
					p.message("You need a thieving level of 15 to steal from this stall");
					return;
				}
				p.message("You cautiously grab a cake from the stall");
				addItem(p, ROCK_CAKE, 1);
				p.incExp(THIEVING, 64, true);
				replaceObject(obj, new GameObject(obj.getLocation(), ROCK_CAKE_COUNTER_EMPTY, obj.getDirection(), obj.getType()));
				delayedSpawnObject(obj.getLoc(), 5000);
			}
		}
		if(obj.getID() == ROCK_CAKE_COUNTER_EMPTY) {
			p.message("The stall is empty at the moment");
		}
		if(obj.getID() == CHEST_WEST) {
			randomizedChest(p, obj);
		}
		if(obj.getID() == CHEST_EAST) {
			p.message("You open the chest");
			openChest(obj, 2000, 1002);
			p.message("Ahh! there is a poison spider inside");
			p.message("Someone's idea of a joke...");
			Npc spider = spawnNpc(292, obj.getX(), obj.getY() + 1 , 60000 * 5);
			spider.startCombat(p);
			sleep(1600);
			p.message("The chest snaps shut");
		}
		if(obj.getID() == ROCK_OVER || obj.getID() == ROCK_BACK) {
			if(command.equalsIgnoreCase("look at")) {
				p.message("The bridge has collapsed");
				p.message("It seems this rock is placed here to jump from");
			} else if(command.equalsIgnoreCase("jump over")) {
				if(obj.getID() == ROCK_BACK) {
					p.teleport(646, 805);
					playerTalk(p,null, "I'm glad that was easier on the way back!");
				} else {
					Npc ogre_guard = getNearestNpc(p, 697, 5);
					if(ogre_guard != null) {
						npcTalk(p,ogre_guard, "Oi! Little thing, if you want to cross here",
								"You can pay me first - 20 gold pieces!");
						playerTalk(p,ogre_guard, "20 gold pieces to jump off a bridge!!?");
						npcTalk(p,ogre_guard, "That's what I said, like it or lump it");
						int menu = showMenu(p,ogre_guard,
								"Okay i'll pay it",
								"Forget it, i'm not paying");
						if(menu == 0) {
							npcTalk(p,ogre_guard, "A wise choice little thing");
							if(!hasItem(p, 10, 20)) {
								npcTalk(p,ogre_guard, "And where is your money ? Grrrr!",
										"Do you want to get hurt or something ?");
							} else {
								removeItem(p, 10, 20);
								p.message("You daringly jump across the chasm");
								p.teleport(647, 799);
								playerTalk(p,null, "Phew! I just made it");
							}
						} else if(menu == 1) {
							npcTalk(p,ogre_guard, "In that case you're not crossing");
							p.message("The guard blocks your path");
						}
					} else {
						p.message("You daringly jump across the chasm");
						p.teleport(647, 799);
						playerTalk(p,null, "Phew! I just made it");
					}
				}
			}
		}
		if(obj.getID() == DARK_PLACE_ROCKS) {
			p.message("You search the rock");
			p.message("There's nothing here");
		}
		if(obj.getID() == DARK_PLACE_TELEPORT_ROCK) {
			p.message("You search the rock");
			p.message("You uncover a tunnel entrance");
			p.teleport(638, 776);
			playerTalk(p,null, "Phew! At last i'm out...",
					"Next time I will take some light!");
		}
		if(obj.getID() == YANILLE_HOLE) { 
			playerTalk(p,null, "I can't get through this way",
					"This hole must lead to somewhere...");

		}
		if(obj.getID() == SKAVID_HOLE) {
			p.playerServerMessage(MessageType.QUEST, "You enter the tunnel");
			p.message("So that's how the skavids are getting into yanille!");
			p.teleport(609, 742);
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		if(obj.getID() == BATTLEMENT || obj.getID() == SOUTH_WEST_BATTLEMENT) {
			return true;
		}
		if(inArray(obj.getID(), CAVE_EXITS)) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if(inArray(obj.getID(), CAVE_EXITS)) { // 6 WEBS!!
			if(obj.getID() == CAVE_EXITS[0]) {
				p.teleport(648, 769);
			}
			else if(obj.getID() == CAVE_EXITS[1]) {
				p.teleport(638, 777);
			}
			else if(obj.getID() == CAVE_EXITS[2]) {
				p.teleport(629, 777);
			}
			else if(obj.getID() == CAVE_EXITS[3]) {
				p.teleport(631, 789);
			}
			else if(obj.getID() == CAVE_EXITS[4]) {
				p.teleport(624, 807);
			} 
			else if(obj.getID() == CAVE_EXITS[5]) {
				p.teleport(645, 812);
			}
		}
		if(obj.getID() == BATTLEMENT) {
			playerTalk(p,null, "What's this ?",
					"The bridge is out - i'll need to find another way in",
					"I can see a ladder up there coming out of a hole",
					"Maybe I should check out some of these tunnels around here...");
		}
		if(obj.getID() == SOUTH_WEST_BATTLEMENT) {
			Npc ogre_guard = getNearestNpc(p, 677, 5);
			if(p.getX() <= 664) {
				p.teleport(p.getX() + 1, p.getY());
			} else {
				if(p.getCache().hasKey("has_ogre_gift")) {
					npcTalk(p,ogre_guard, "It's that creature again",
							"This time we will let it go...");
					p.teleport(p.getX() - 1, p.getY());
					p.message("You climb over the battlement");
				} else if(p.getCache().hasKey("get_ogre_gift") || p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
					if(ogre_guard != null) {
						npcTalk(p,ogre_guard, "Stop creature!... Oh its you",
								"Well what have you got for us then ?");
						if(p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
							playerTalk(p,ogre_guard, "I didn't bring anything");
							npcTalk(p,ogre_guard, "Didn't bring anything!",
									"In that case shove off!");
							p.message("The guard pushes you out of the city");
							p.teleport(635, 774);
							return;
						}
						if(hasItem(p, ROCK_CAKE)) {
							playerTalk(p,ogre_guard, "How about this ?");
							p.message("You give the guard a rock cake");
							removeItem(p, ROCK_CAKE, 1);
							npcTalk(p,ogre_guard, "Well well, looks at this",
									"My favourite, rock cake!",
									"Okay we will let it through");
							p.teleport(663, 812);
							p.message("You climb over the battlement");
							p.getCache().remove("get_ogre_gift");
							p.getCache().store("has_ogre_gift", true);
						} else {
							playerTalk(p,ogre_guard, "I didn't bring anything");
							npcTalk(p,ogre_guard, "Didn't bring anything!",
									"In that case shove off!");
							p.message("The guard pushes you out of the city");
							p.teleport(635, 774);
						}
					}
				} else {
					if(ogre_guard != null) {
						npcTalk(p,ogre_guard, "Oi! where do you think you are going ?",
								"You are for the cooking pot!");
						int menu = showMenu(p,ogre_guard,
								"But I am a friend to ogres...",
								"Not if I can help it");
						if(menu == 0) {
							npcTalk(p,ogre_guard, "Prove it to us with a gift",
									"Get us something from the market");
							playerTalk(p,ogre_guard, "Like what ?");
							npcTalk(p,ogre_guard, "Surprise us...");
							p.getCache().store("get_ogre_gift", true);
						} else if(menu == 1) {
							npcTalk(p,ogre_guard, "You can help by being tonight's dinner...",
									"Or you can go away, now what shall it be ?");
							int subMenu = showMenu(p,ogre_guard,
									"Okay, okay i'm going",
									"I tire of ogres, prepare to die!");
							if(subMenu == 0) {
								npcTalk(p,ogre_guard, "back to whence you came");
								p.teleport(635, 774);
							} else if(subMenu == 1) {
								npcTalk(p,ogre_guard, "Grrrrr!");
								ogre_guard.startCombat(p);
							}
						}
					}
				}
			}
		}
	}
	private void randomizedChest(Player p, GameObject o) {
		p.message("You open the chest");
		openChest(o, 2500, 1002);
		int[] randomChestReward = { 271, 292, 190, 19, 801, 20, 163, 365 };
		int choosenReward = (int) (Math.random() * randomChestReward.length);
		if(choosenReward == 0) {
			playerTalk(p,null, "Hey! a scorpion is in here!");
			Npc scorp = spawnNpc(271, o.getX() - 1, o.getY(), 60000 * 5);
			scorp.startCombat(p);
		} else if(choosenReward == 1) {
			playerTalk(p,null, "Oh no, not one of these spider things!");
			Npc spider = spawnNpc(292, o.getX() - 1, o.getY(), 60000 * 5);
			spider.startCombat(p);
		} else if(choosenReward == 2) {
			playerTalk(p,null, "How on earth did this dwarf get in here ?");
			Npc dwarf = spawnNpc(190, o.getX() - 1, o.getY(), 60000 * 5);
			dwarf.startCombat(p);
		} else if(choosenReward == 3) {
			playerTalk(p,null, "Ugh! a dirty rat!");
			spawnNpc(19, o.getX() - 1, o.getY(), 60000 * 5);
		} else if(choosenReward == 4) {
			playerTalk(p,null, "Oh dear, I bet these apples taste disgusting");
			addItem(p, 801, 1);
		} else if(choosenReward == 5) {
			playerTalk(p,null, "Oh great, some bones!");
			addItem(p, 20, 1);
		} else if(choosenReward == 6) {
			playerTalk(p,null, "Wow, look at the size of this emerald!");
			addItem(p, 163, 1);
		} else if(choosenReward == 7) {
			playerTalk(p,null, "Burnt fish - why did I bother ?");
			addItem(p, 365, 1);
		}
		p.message("The chest snaps shut");
	}
}
