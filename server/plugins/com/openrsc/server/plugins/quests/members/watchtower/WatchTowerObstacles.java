package com.openrsc.server.plugins.quests.members.watchtower;


import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.coordModifier;
import static com.openrsc.server.plugins.Functions.delayedSpawnObject;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.incQuestReward;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.openChest;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.replaceObject;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

/**
 * @author Imposter/Fate
 */
public class WatchTowerObstacles implements ObjectActionListener, ObjectActionExecutiveListener, WallObjectActionListener, WallObjectActionExecutiveListener {


	/**
	 * OBJECT IDs
	 **/
	private static int TOWER_FIRST_FLOOR_LADDER = 659;
	private static int COMPLETED_QUEST_LADDER = 1017;
	private static int TOWER_SECOND_FLOOR_LADDER = 1021;
	private static int WATCHTOWER_LEVER = 1014;
	private static int WATCHTOWER_LEVER_DOWNPOSITION = 1015;
	private static int[] WRONG_BUSHES = {960};
	private static int[] CORRECT_BUSHES = {993, 961, 992, 991, 990};
	private static int[] TELEPORT_CAVES = {970, 972, 950, 971, 949, 975};
	private static int[] CAVE_EXITS = {188, 189, 190, 191, 187, 192};
	private static int TUNNEL_CAVE = 998;
	private static final int TOBAN_CHEST_OPEN = 979;
	private static final int TOBAN_CHEST_CLOSED = 978;
	private static int ISLAND_LADDER = 997;
	private static int BATTLEMENT = 201;
	private static int SOUTH_WEST_BATTLEMENT = 195;
	private static int WRONG_STEAL_COUNTER = 973;
	private static int OGRE_CAVE_ENCLAVE = 955;
	private static int ROCK_CAKE_COUNTER = 999;
	private static int ROCK_CAKE_COUNTER_EMPTY = 1034;
	private static int CHEST_WEST = 1003;
	private static int ROCK_OVER = 995;
	private static int ROCK_BACK = 996;
	private static int CHEST_EAST = 1001;
	private static int DARK_PLACE_ROCKS = 1007;
	private static int DARK_PLACE_TELEPORT_ROCK = 1008;
	private static int YANILLE_HOLE = 968;
	private static int SKAVID_HOLE = 969;
	private static int OGRE_ENCLAVE_EXIT = 1024;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return (obj.getID() == TOWER_FIRST_FLOOR_LADDER || obj.getID() == COMPLETED_QUEST_LADDER || obj.getID() == TOWER_SECOND_FLOOR_LADDER)
				|| (obj.getID() == WATCHTOWER_LEVER || obj.getID() == WATCHTOWER_LEVER_DOWNPOSITION) || inArray(obj.getID(), WRONG_BUSHES)
				|| inArray(obj.getID(), CORRECT_BUSHES) || inArray(obj.getID(), TELEPORT_CAVES)
				|| (obj.getID() == TUNNEL_CAVE || obj.getID() == TOBAN_CHEST_CLOSED || obj.getID() == ISLAND_LADDER)
				|| (obj.getID() == WRONG_STEAL_COUNTER || obj.getID() == OGRE_CAVE_ENCLAVE || obj.getID() == ROCK_CAKE_COUNTER || obj.getID() == ROCK_CAKE_COUNTER_EMPTY)
				|| (obj.getID() == CHEST_WEST || obj.getID() == CHEST_EAST) || (obj.getID() == ROCK_OVER || obj.getID() == ROCK_BACK)
				|| (obj.getID() == DARK_PLACE_ROCKS || obj.getID() == DARK_PLACE_TELEPORT_ROCK) || (obj.getID() == YANILLE_HOLE || obj.getID() == SKAVID_HOLE)
				|| (obj.getID() == OGRE_ENCLAVE_EXIT);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == COMPLETED_QUEST_LADDER) {
			p.teleport(636, 1684);
		}
		else if (obj.getID() == TOWER_SECOND_FLOOR_LADDER) {
			if (p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				p.teleport(492, 3524);
			} else {
				p.teleport(636, 2628);
			}
		}
		else if (obj.getID() == TOWER_FIRST_FLOOR_LADDER) {
			Npc t_guard = getNearestNpc(p, NpcId.TOWER_GUARD.id(), 5);
			switch (p.getQuestStage(Constants.Quests.WATCHTOWER)) {
				case 0:
					if (t_guard != null) {
						npcTalk(p, t_guard, "You can't go up there",
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
					npcTalk(p, t_guard, "It is the wizards helping hand",
						"Let 'em up");
					int[] coords = coordModifier(p, true, obj);
					p.teleport(coords[0], coords[1], false);
					break;
			}
		}
		else if (obj.getID() == OGRE_ENCLAVE_EXIT) {
			p.teleport(662, 788);
		}
		else if (obj.getID() == WATCHTOWER_LEVER_DOWNPOSITION) {
			p.message("The lever is stuck in the down position");
		}
		else if (obj.getID() == WATCHTOWER_LEVER) {
			replaceObject(obj, new GameObject(obj.getLocation(), 1015, obj.getDirection(), obj.getType()));
			delayedSpawnObject(obj.getLoc(), 2000);
			p.message("You pull the lever");
			if (p.getQuestStage(Constants.Quests.WATCHTOWER) == 10) {
				p.message("The magic forcefield activates");
				p.teleport(492, 3521);
				removeItem(p, ItemId.POWERING_CRYSTAL1.id(), 1);
				removeItem(p, ItemId.POWERING_CRYSTAL2.id(), 1);
				removeItem(p, ItemId.POWERING_CRYSTAL3.id(), 1);
				removeItem(p, ItemId.POWERING_CRYSTAL4.id(), 1);
				Npc wizard = getNearestNpc(p, NpcId.WATCHTOWER_WIZARD.id(), 6);
				if (wizard != null) {
					p.message("@gre@You haved gained 4 quest points!");
					incQuestReward(p, Quests.questData.get(Quests.WATCHTOWER), true);
					npcTalk(p, wizard, "Marvellous! it works!",
						"The town will now be safe",
						"Your help was invaluable",
						"Take this payment as a token of my gratitude...");
					p.message("The wizard gives you 5000 pieces of gold");
					addItem(p, ItemId.COINS.id(), 5000);
					npcTalk(p, wizard, "Also, let me improve your magic level for you");
					p.message("The wizard lays his hands on you...");
					p.message("You feel magic power increasing");
					npcTalk(p, wizard, "Here is a special item for you...");
					addItem(p, ItemId.SPELL_SCROLL.id(), 1);
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
		else if (inArray(obj.getID(), WRONG_BUSHES)) {
			if (p.getQuestStage(Constants.Quests.WATCHTOWER) == 0) {
				playerTalk(p, null, "I am not sure why I am searching this bush...");
				return;
			}
			playerTalk(p, null, "Hmmm, nothing here");
		}
		else if (inArray(obj.getID(), CORRECT_BUSHES)) {
			if (p.getQuestStage(Constants.Quests.WATCHTOWER) == 0) {
				playerTalk(p, null, "I am not sure why I am searching this bush...");
				return;
			}
			if (obj.getID() == CORRECT_BUSHES[0]) {
				playerTalk(p, null, "Here's Some armour, it could be evidence...");
				addItem(p, ItemId.ARMOUR.id(), 1);
			} else if (obj.getID() == CORRECT_BUSHES[1]) {
				if (!hasItem(p, ItemId.FINGERNAILS.id())) {
					playerTalk(p, null, "What's this ?",
						"Disgusting! some fingernails",
						"They may be a clue though... I'd better take them");
					addItem(p, ItemId.FINGERNAILS.id(), 1);
				} else {
					playerTalk(p, null, "I have already searched this place");
				}
			} else if (obj.getID() == CORRECT_BUSHES[2]) {
				if (!hasItem(p, ItemId.WATCH_TOWER_EYE_PATCH.id())) {
					playerTalk(p, null, "I've found an eyepatch, I better show this to the wizards");
					addItem(p, ItemId.WATCH_TOWER_EYE_PATCH.id(), 1);
				} else {
					playerTalk(p, null, "I have already searched this place");
				}
			} else if (obj.getID() == CORRECT_BUSHES[3]) {
				if (!hasItem(p, ItemId.ROBE.id())) {
					playerTalk(p, null, "Aha! a robe");
					addItem(p, ItemId.ROBE.id(), 1);
					playerTalk(p, null, "This could be a clue...");
				} else {
					playerTalk(p, null, "I have already searched this place");
				}
			} else if (obj.getID() == CORRECT_BUSHES[4]) {
				playerTalk(p, null, "Aha a dagger");
				addItem(p, ItemId.DAGGER.id(), 1);
				playerTalk(p, null, "I wonder if this is evidence...");
			}
		}

		else if (inArray(obj.getID(), TELEPORT_CAVES)) {
			if (hasItem(p, ItemId.SKAVID_MAP.id())) {
				//a light source
				if (hasItem(p, ItemId.LIT_CANDLE.id()) || hasItem(p, ItemId.LIT_BLACK_CANDLE.id()) || hasItem(p, ItemId.LIT_TORCH.id())) {
					p.message("You enter the cave");
					if (obj.getID() == TELEPORT_CAVES[0]) {
						p.teleport(650, 3555);
					} else if (obj.getID() == TELEPORT_CAVES[1]) {
						p.teleport(626, 3564);
					} else if (obj.getID() == TELEPORT_CAVES[2]) {
						p.teleport(627, 3591);
					} else if (obj.getID() == TELEPORT_CAVES[3]) {
						p.teleport(638, 3564);
					} else if (obj.getID() == TELEPORT_CAVES[4]) {
						p.teleport(629, 3574);
					} else if (obj.getID() == TELEPORT_CAVES[5]) {
						p.teleport(647, 3596);
					}
				} else {
					p.teleport(629, 3558);
					playerTalk(p, null, "Oh my! It's dark!",
						"All I can see are lots of rocks on the floor",
						"I suppose I better search them for a way out");
				}
			} else {
				p.message("There's no way I can find my way through without a map of some kind");
				if (obj.getID() == TELEPORT_CAVES[0] || obj.getID() == TELEPORT_CAVES[4]) {
					p.teleport(629, 777);
				} else if (obj.getID() == TELEPORT_CAVES[1]) {
					p.teleport(624, 807);
				} else if (obj.getID() == TELEPORT_CAVES[2]) {
					p.teleport(648, 769);
				} else if (obj.getID() == TELEPORT_CAVES[3]) {
					p.teleport(631, 789);
				} else if (obj.getID() == TELEPORT_CAVES[5]) {
					p.teleport(638, 777);
				}
			}
		}
		else if (obj.getID() == TUNNEL_CAVE) {
			p.message("You enter the cave");
			p.teleport(605, 803);
			playerTalk(p, null, "Wow! that tunnel went a long way");
		}
		else if (obj.getID() == TOBAN_CHEST_CLOSED) {
			if (hasItem(p, ItemId.KEY.id(), 1)) {
				p.message("You use the key Og gave you");
				removeItem(p, ItemId.KEY.id(), 1);
				openChest(obj, 2000, TOBAN_CHEST_OPEN);
				if (hasItem(p, ItemId.STOLEN_GOLD.id())) {
					message(p, "You have already got the stolen gold");
				} else {
					p.message("You find a stash of gold inside");
					message(p, "You take the gold");
					addItem(p, ItemId.STOLEN_GOLD.id(), 1);
				}
				p.message("The chest springs shut");
			} else {
				p.message("The chest is locked");
				playerTalk(p, null, "I think I need a key of some sort...");
			}
		}
		else if (obj.getID() == ISLAND_LADDER) {
			p.message("You climb down the ladder");
			p.teleport(669, 826);
		}
		else if (obj.getID() == WRONG_STEAL_COUNTER) {
			p.message("You find nothing to steal");
		}
		else if (obj.getID() == OGRE_CAVE_ENCLAVE) {
			if (p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				p.message("The ogres have blocked this entrance now");
				// TODO should we sell this entrance for 100,000 coins or etc?
				return;
			}
			Npc ogre_guard = getNearestNpc(p, NpcId.OGRE_GUARD_CAVE_ENTRANCE.id(), 5);
			if (ogre_guard != null) {
				npcTalk(p, ogre_guard, "No you don't!");
				ogre_guard.startCombat(p);
			}
		}
		else if (obj.getID() == ROCK_CAKE_COUNTER) {
			Npc ogre_trader = getNearestNpc(p, NpcId.OGRE_TRADER_ROCKCAKE.id(), 5);
			if (ogre_trader != null) {
				npcTalk(p, ogre_trader, "Grr! get your hands off those cakes");
				ogre_trader.startCombat(p);
			} else {
				if (getCurrentLevel(p, SKILLS.THIEVING.id()) < 15) {
					p.message("You need a thieving level of 15 to steal from this stall");
					return;
				}
				p.message("You cautiously grab a cake from the stall");
				addItem(p, ItemId.ROCK_CAKE.id(), 1);
				p.incExp(SKILLS.THIEVING.id(), 64, true);
				replaceObject(obj, new GameObject(obj.getLocation(), ROCK_CAKE_COUNTER_EMPTY, obj.getDirection(), obj.getType()));
				delayedSpawnObject(obj.getLoc(), 5000);
			}
		}
		else if (obj.getID() == ROCK_CAKE_COUNTER_EMPTY) {
			p.message("The stall is empty at the moment");
		}
		else if (obj.getID() == CHEST_WEST) {
			randomizedChest(p, obj);
		}
		else if (obj.getID() == CHEST_EAST) {
			p.message("You open the chest");
			openChest(obj, 2000, 1002);
			p.message("Ahh! there is a poison spider inside");
			p.message("Someone's idea of a joke...");
			Npc spider = spawnNpc(NpcId.POISON_SPIDER.id(), obj.getX(), obj.getY() + 1, 60000 * 5);
			spider.startCombat(p);
			sleep(1600);
			p.message("The chest snaps shut");
		}
		else if (obj.getID() == ROCK_OVER || obj.getID() == ROCK_BACK) {
			if (command.equalsIgnoreCase("look at")) {
				p.message("The bridge has collapsed");
				p.message("It seems this rock is placed here to jump from");
			} else if (command.equalsIgnoreCase("jump over")) {
				if (getCurrentLevel(p, SKILLS.AGILITY.id()) < 30) {
					p.message("You need agility level of 30 to attempt this jump");
					return;
				}
				if (obj.getID() == ROCK_BACK) {
					p.teleport(646, 805);
					playerTalk(p, null, "I'm glad that was easier on the way back!");
				} else {
					Npc ogre_guard = getNearestNpc(p, NpcId.OGRE_GUARD_BRIDGE.id(), 5);
					if (ogre_guard != null) {
						npcTalk(p, ogre_guard, "Oi! Little thing, if you want to cross here",
							"You can pay me first - 20 gold pieces!");
						playerTalk(p, ogre_guard, "20 gold pieces to jump off a bridge!!?");
						npcTalk(p, ogre_guard, "That's what I said, like it or lump it");
						int menu = showMenu(p, ogre_guard,
							"Okay i'll pay it",
							"Forget it, i'm not paying");
						if (menu == 0) {
							npcTalk(p, ogre_guard, "A wise choice little thing");
							if (!hasItem(p, ItemId.COINS.id(), 20)) {
								npcTalk(p, ogre_guard, "And where is your money ? Grrrr!",
									"Do you want to get hurt or something ?");
							} else {
								removeItem(p, ItemId.COINS.id(), 20);
								if (Constants.GameServer.WANT_FATIGUE) {
									if (p.getFatigue() >= 7500) {
										p.message("You are too tired to attempt this jump");
										return;
									}
								}
								p.message("You daringly jump across the chasm");
								p.teleport(647, 799);
								p.incExp(SKILLS.AGILITY.id(), 50, true);
								playerTalk(p, null, "Phew! I just made it");
							}
						} else if (menu == 1) {
							npcTalk(p, ogre_guard, "In that case you're not crossing");
							p.message("The guard blocks your path");
						}
					} else {
						if (Constants.GameServer.WANT_FATIGUE) {
							if (p.getFatigue() >= 7500) {
								p.message("You are too tired to attempt this jump");
								return;
							}
						}
						p.message("You daringly jump across the chasm");
						p.teleport(647, 799);
						p.incExp(SKILLS.AGILITY.id(), 50, true);
						playerTalk(p, null, "Phew! I just made it");
					}
				}
			}
		}
		else if (obj.getID() == DARK_PLACE_ROCKS) {
			p.message("You search the rock");
			p.message("There's nothing here");
		}
		else if (obj.getID() == DARK_PLACE_TELEPORT_ROCK) {
			p.message("You search the rock");
			p.message("You uncover a tunnel entrance");
			p.teleport(638, 776);
			playerTalk(p, null, "Phew! At last i'm out...",
				"Next time I will take some light!");
		}
		else if (obj.getID() == YANILLE_HOLE) {
			playerTalk(p, null, "I can't get through this way",
				"This hole must lead to somewhere...");
		}
		else if (obj.getID() == SKAVID_HOLE) {
			p.playerServerMessage(MessageType.QUEST, "You enter the tunnel");
			p.message("So that's how the skavids are getting into yanille!");
			p.teleport(609, 742);
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return obj.getID() == BATTLEMENT || obj.getID() == SOUTH_WEST_BATTLEMENT || inArray(obj.getID(), CAVE_EXITS);
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (inArray(obj.getID(), CAVE_EXITS)) { // 6 WEBS!!
			if (obj.getID() == CAVE_EXITS[0]) {
				p.teleport(648, 769);
			} else if (obj.getID() == CAVE_EXITS[1]) {
				p.teleport(638, 777);
			} else if (obj.getID() == CAVE_EXITS[2]) {
				p.teleport(629, 777);
			} else if (obj.getID() == CAVE_EXITS[3]) {
				p.teleport(631, 789);
			} else if (obj.getID() == CAVE_EXITS[4]) {
				p.teleport(624, 807);
			} else if (obj.getID() == CAVE_EXITS[5]) {
				p.teleport(645, 812);
			}
		}
		else if (obj.getID() == BATTLEMENT) {
			playerTalk(p, null, "What's this ?",
				"The bridge is out - i'll need to find another way in",
				"I can see a ladder up there coming out of a hole",
				"Maybe I should check out some of these tunnels around here...");
		}
		else if (obj.getID() == SOUTH_WEST_BATTLEMENT) {
			Npc ogre_guard = getNearestNpc(p, NpcId.OGRE_GUARD_BATTLEMENT.id(), 5);
			if (p.getX() <= 664) {
				p.teleport(p.getX() + 1, p.getY());
			} else {
				if (p.getCache().hasKey("has_ogre_gift")) {
					npcTalk(p, ogre_guard, "It's that creature again",
						"This time we will let it go...");
					p.teleport(p.getX() - 1, p.getY());
					p.message("You climb over the battlement");
				} else if (p.getCache().hasKey("get_ogre_gift") || p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
					if (ogre_guard != null) {
						npcTalk(p, ogre_guard, "Stop creature!... Oh its you",
							"Well what have you got for us then ?");
						if (p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
							playerTalk(p, ogre_guard, "I didn't bring anything");
							npcTalk(p, ogre_guard, "Didn't bring anything!",
								"In that case shove off!");
							p.message("The guard pushes you out of the city");
							p.teleport(635, 774);
							return;
						}
						if (hasItem(p, ItemId.ROCK_CAKE.id())) {
							playerTalk(p, ogre_guard, "How about this ?");
							p.message("You give the guard a rock cake");
							removeItem(p, ItemId.ROCK_CAKE.id(), 1);
							npcTalk(p, ogre_guard, "Well well, looks at this",
								"My favourite, rock cake!",
								"Okay we will let it through");
							p.teleport(663, 812);
							p.message("You climb over the battlement");
							p.getCache().remove("get_ogre_gift");
							p.getCache().store("has_ogre_gift", true);
						} else {
							playerTalk(p, ogre_guard, "I didn't bring anything");
							npcTalk(p, ogre_guard, "Didn't bring anything!",
								"In that case shove off!");
							p.message("The guard pushes you out of the city");
							p.teleport(635, 774);
						}
					}
				} else {
					if (ogre_guard != null) {
						npcTalk(p, ogre_guard, "Oi! where do you think you are going ?",
							"You are for the cooking pot!");
						int menu = showMenu(p, ogre_guard,
							"But I am a friend to ogres...",
							"Not if I can help it");
						if (menu == 0) {
							npcTalk(p, ogre_guard, "Prove it to us with a gift",
								"Get us something from the market");
							playerTalk(p, ogre_guard, "Like what ?");
							npcTalk(p, ogre_guard, "Surprise us...");
							p.getCache().store("get_ogre_gift", true);
						} else if (menu == 1) {
							npcTalk(p, ogre_guard, "You can help by being tonight's dinner...",
								"Or you can go away, now what shall it be ?");
							int subMenu = showMenu(p, ogre_guard,
								"Okay, okay i'm going",
								"I tire of ogres, prepare to die!");
							if (subMenu == 0) {
								npcTalk(p, ogre_guard, "back to whence you came");
								p.teleport(635, 774);
							} else if (subMenu == 1) {
								npcTalk(p, ogre_guard, "Grrrrr!");
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
		int[] randomChestReward = {NpcId.POISON_SCORPION.id(), NpcId.POISON_SPIDER.id(), NpcId.CHAOS_DWARF.id(), NpcId.RAT_LVL8.id(),
				ItemId.ROTTEN_APPLES.id(), ItemId.BONES.id(), ItemId.EMERALD.id(), ItemId.BURNT_PIKE.id()};
		int choosenReward = (int) (Math.random() * randomChestReward.length);
		if (choosenReward == 0) {
			playerTalk(p, null, "Hey! a scorpion is in here!");
			Npc scorp = spawnNpc(NpcId.POISON_SCORPION.id(), o.getX() - 1, o.getY(), 60000 * 5);
			scorp.startCombat(p);
		} else if (choosenReward == 1) {
			playerTalk(p, null, "Oh no, not one of these spider things!");
			Npc spider = spawnNpc(NpcId.POISON_SPIDER.id(), o.getX() - 1, o.getY(), 60000 * 5);
			spider.startCombat(p);
		} else if (choosenReward == 2) {
			playerTalk(p, null, "How on earth did this dwarf get in here ?");
			Npc dwarf = spawnNpc(NpcId.CHAOS_DWARF.id(), o.getX() - 1, o.getY(), 60000 * 5);
			dwarf.startCombat(p);
		} else if (choosenReward == 3) {
			playerTalk(p, null, "Ugh! a dirty rat!");
			spawnNpc(NpcId.RAT_LVL8.id(), o.getX() - 1, o.getY(), 60000 * 5);
		} else if (choosenReward == 4) {
			playerTalk(p, null, "Oh dear, I bet these apples taste disgusting");
			addItem(p, ItemId.ROTTEN_APPLES.id(), 1);
		} else if (choosenReward == 5) {
			playerTalk(p, null, "Oh great, some bones!");
			addItem(p, ItemId.BONES.id(), 1);
		} else if (choosenReward == 6) {
			playerTalk(p, null, "Wow, look at the size of this emerald!");
			addItem(p, ItemId.EMERALD.id(), 1);
		} else if (choosenReward == 7) {
			playerTalk(p, null, "Burnt fish - why did I bother ?");
			addItem(p, ItemId.BURNT_PIKE.id(), 1);
		}
		p.message("The chest snaps shut");
	}
}
