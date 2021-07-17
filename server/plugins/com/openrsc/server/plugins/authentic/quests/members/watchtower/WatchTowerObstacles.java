package com.openrsc.server.plugins.authentic.quests.members.watchtower;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class WatchTowerObstacles implements OpLocTrigger, OpBoundTrigger, UseNpcTrigger {


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
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
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
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == COMPLETED_QUEST_LADDER) {
			player.teleport(636, 1684);
		}
		else if (obj.getID() == TOWER_SECOND_FLOOR_LADDER) {
			if (player.getQuestStage(Quests.WATCHTOWER) == -1) {
				player.teleport(492, 3524);
			} else {
				player.teleport(636, 2628);
			}
		}
		else if (obj.getID() == TOWER_FIRST_FLOOR_LADDER) {
			Npc t_guard = ifnearvisnpc(player, NpcId.TOWER_GUARD.id(), 5);
			switch (player.getQuestStage(Quests.WATCHTOWER)) {
				case 0:
					if (t_guard != null) {
						npcsay(player, t_guard, "You can't go up there",
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
					if (t_guard != null) {
						npcsay(player, t_guard, "It is the wizards helping hand",
							"Let 'em up");
					}
					int[] coords = coordModifier(player, true, obj);
					player.teleport(coords[0], coords[1], false);
					break;
			}
		}
		else if (obj.getID() == OGRE_ENCLAVE_EXIT) {
			player.teleport(662, 788);
		}
		else if (obj.getID() == WATCHTOWER_LEVER_DOWNPOSITION) {
			player.message("The lever is stuck in the down position");
		}
		else if (obj.getID() == WATCHTOWER_LEVER) {
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), WATCHTOWER_LEVER_DOWNPOSITION, obj.getDirection(), obj.getType()));
			addloc(obj.getWorld(), obj.getLoc(), 2000);
			player.message("You pull the lever");
			if (player.getQuestStage(Quests.WATCHTOWER) == 10) {
				player.message("The magic forcefield activates");
				player.teleport(492, 3521);
				player.getCarriedItems().remove(new Item(ItemId.POWERING_CRYSTAL1.id()));
				player.getCarriedItems().remove(new Item(ItemId.POWERING_CRYSTAL2.id()));
				player.getCarriedItems().remove(new Item(ItemId.POWERING_CRYSTAL3.id()));
				player.getCarriedItems().remove(new Item(ItemId.POWERING_CRYSTAL4.id()));
				Npc wizard = ifnearvisnpc(player, NpcId.WATCHTOWER_WIZARD.id(), 6);
				if (wizard != null) {
					final QuestReward reward = Quest.WATCHTOWER.reward();
					incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
					for (XPReward xpReward : reward.getXpRewards()) {
						incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
					}
					npcsay(player, wizard, "Marvellous! it works!",
						"The town will now be safe",
						"Your help was invaluable",
						"Take this payment as a token of my gratitude...");
					player.message("The wizard gives you 5000 pieces of gold");
					give(player, ItemId.COINS.id(), 5000);
					npcsay(player, wizard, "Also, let me improve your magic level for you");
					player.message("The wizard lays his hands on you...");
					player.message("You feel magic power increasing");
					npcsay(player, wizard, "Here is a special item for you...");
					give(player, ItemId.SPELL_SCROLL.id(), 1);
					npcsay(player, wizard, "It's a new spell",
						"Read the scroll and you will be able",
						"To teleport yourself to here magically...");
					player.message("Congratulations, you have finished the watchtower quest");
					player.sendQuestComplete(Quests.WATCHTOWER);
					/* TODO REMOVE ALL CACHES AND USE QUEST -1 */
				} else {
					player.message("Seems like the wizards were busy, please go back and complete again");
				}
			} else {
				player.message("It had no effect");
			}
		}
		else if (inArray(obj.getID(), WRONG_BUSHES)) {
			if (player.getQuestStage(Quests.WATCHTOWER) == 0) {
				say(player, null, "I am not sure why I am searching this bush...");
				return;
			}
			say(player, null, "Hmmm, nothing here");
		}
		else if (inArray(obj.getID(), CORRECT_BUSHES)) {
			if (player.getQuestStage(Quests.WATCHTOWER) == 0) {
				say(player, null, "I am not sure why I am searching this bush...");
				return;
			}
			if (obj.getID() == CORRECT_BUSHES[0]) {
				say(player, null, "Here's Some armour, it could be evidence...");
				give(player, ItemId.ARMOUR.id(), 1);
			} else if (obj.getID() == CORRECT_BUSHES[1]) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.FINGERNAILS.id(), Optional.empty())) {
					say(player, null, "What's this ?",
						"Disgusting! some fingernails",
						"They may be a clue though... I'd better take them");
					give(player, ItemId.FINGERNAILS.id(), 1);
				} else {
					say(player, null, "I have already searched this place");
				}
			} else if (obj.getID() == CORRECT_BUSHES[2]) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.WATCH_TOWER_EYE_PATCH.id(), Optional.empty())) {
					say(player, null, "I've found an eyepatch, I better show this to the wizards");
					give(player, ItemId.WATCH_TOWER_EYE_PATCH.id(), 1);
				} else {
					say(player, null, "I have already searched this place");
				}
			} else if (obj.getID() == CORRECT_BUSHES[3]) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.ROBE.id(), Optional.empty())) {
					say(player, null, "Aha! a robe");
					give(player, ItemId.ROBE.id(), 1);
					say(player, null, "This could be a clue...");
				} else {
					say(player, null, "I have already searched this place");
				}
			} else if (obj.getID() == CORRECT_BUSHES[4]) {
				say(player, null, "Aha a dagger");
				give(player, ItemId.DAGGER.id(), 1);
				say(player, null, "I wonder if this is evidence...");
			}
		}

		else if (inArray(obj.getID(), TELEPORT_CAVES)) {
			if (player.getCarriedItems().hasCatalogID(ItemId.SKAVID_MAP.id(), Optional.of(false))) {
				//a light source
				if (player.getCarriedItems().hasCatalogID(ItemId.LIT_CANDLE.id(), Optional.of(false))
					|| player.getCarriedItems().hasCatalogID(ItemId.LIT_BLACK_CANDLE.id(), Optional.of(false))
					|| player.getCarriedItems().hasCatalogID(ItemId.LIT_TORCH.id(), Optional.of(false))) {
					player.message("You enter the cave");
					if (obj.getID() == TELEPORT_CAVES[0]) {
						player.teleport(650, 3555);
					} else if (obj.getID() == TELEPORT_CAVES[1]) {
						player.teleport(626, 3564);
					} else if (obj.getID() == TELEPORT_CAVES[2]) {
						player.teleport(627, 3591);
					} else if (obj.getID() == TELEPORT_CAVES[3]) {
						player.teleport(638, 3564);
					} else if (obj.getID() == TELEPORT_CAVES[4]) {
						player.teleport(629, 3574);
					} else if (obj.getID() == TELEPORT_CAVES[5]) {
						player.teleport(647, 3596);
					}
				} else {
					player.teleport(629, 3558);
					say(player, null, "Oh my! It's dark!",
						"All I can see are lots of rocks on the floor",
						"I suppose I better search them for a way out");
				}
			} else {
				player.message("There's no way I can find my way through without a map of some kind");
				if (obj.getID() == TELEPORT_CAVES[0] || obj.getID() == TELEPORT_CAVES[4]) {
					player.teleport(629, 777);
				} else if (obj.getID() == TELEPORT_CAVES[1]) {
					player.teleport(624, 807);
				} else if (obj.getID() == TELEPORT_CAVES[2]) {
					player.teleport(648, 769);
				} else if (obj.getID() == TELEPORT_CAVES[3]) {
					player.teleport(631, 789);
				} else if (obj.getID() == TELEPORT_CAVES[5]) {
					player.teleport(638, 777);
				}
			}
		}
		else if (obj.getID() == TUNNEL_CAVE) {
			player.message("You enter the cave");
			player.teleport(605, 803);
			say(player, null, "Wow! that tunnel went a long way");
		}
		else if (obj.getID() == TOBAN_CHEST_CLOSED) {
			if (player.getCarriedItems().hasCatalogID(ItemId.KEY.id(), Optional.of(false))) {
				player.message("You use the key Og gave you");
				player.getCarriedItems().remove(new Item(ItemId.KEY.id()));
				openChest(obj, 2000, TOBAN_CHEST_OPEN);
				if (player.getCarriedItems().hasCatalogID(ItemId.STOLEN_GOLD.id(), Optional.empty())) {
					mes("You have already got the stolen gold");
					delay(3);
				} else {
					player.message("You find a stash of gold inside");
					mes("You take the gold");
					delay(3);
					give(player, ItemId.STOLEN_GOLD.id(), 1);
				}
				player.message("The chest springs shut");
			} else {
				player.message("The chest is locked");
				say(player, null, "I think I need a key of some sort...");
			}
		}
		else if (obj.getID() == ISLAND_LADDER) {
			player.message("You climb down the ladder");
			player.teleport(669, 826);
		}
		else if (obj.getID() == WRONG_STEAL_COUNTER) {
			player.message("You find nothing to steal");
		}
		else if (obj.getID() == OGRE_CAVE_ENCLAVE) {
			if (player.getQuestStage(Quests.WATCHTOWER) == -1 &&
				!config().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE) {
				player.message("The ogres have blocked this entrance now");
				return;
			}
			Npc ogre_guard = ifnearvisnpc(player, NpcId.OGRE_GUARD_CAVE_ENTRANCE.id(), 5);
			if (ogre_guard != null) {
				npcsay(player, ogre_guard, "No you don't!");
				ogre_guard.startCombat(player);
			}
			else {
				// player must use nightshade on ogre
				player.message("Nothing interesting happens");
			}
		}
		else if (obj.getID() == ROCK_CAKE_COUNTER) {
			Npc ogre_trader = ifnearvisnpc(player, NpcId.OGRE_TRADER_ROCKCAKE.id(), 5);
			if (ogre_trader != null) {
				npcsay(player, ogre_trader, "Grr! get your hands off those cakes");
				ogre_trader.startCombat(player);
			} else {
				if (getCurrentLevel(player, Skill.THIEVING.id()) < 15) {
					player.message("You need a thieving level of 15 to steal from this stall");
					return;
				}
				player.message("You cautiously grab a cake from the stall");
				give(player, ItemId.ROCK_CAKE.id(), 1);
				player.incExp(Skill.THIEVING.id(), 64, true);
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), ROCK_CAKE_COUNTER_EMPTY, obj.getDirection(), obj.getType()));
				addloc(obj.getWorld(), obj.getLoc(), 5000);
			}
		}
		else if (obj.getID() == ROCK_CAKE_COUNTER_EMPTY) {
			player.message("The stall is empty at the moment");
		}
		else if (obj.getID() == CHEST_WEST) {
			randomizedChest(player, obj);
		}
		else if (obj.getID() == CHEST_EAST) {
			player.message("You open the chest");
			openChest(obj, 2000, 1002);
			player.message("Ahh! there is a poison spider inside");
			player.message("Someone's idea of a joke...");
			Npc spider = addnpc(player.getWorld(), NpcId.POISON_SPIDER.id(), obj.getX(), obj.getY() + 1, 60000 * 5);
			spider.startCombat(player);
			delay(3);
			player.message("The chest snaps shut");
		}
		else if (obj.getID() == ROCK_OVER || obj.getID() == ROCK_BACK) {
			if (command.equalsIgnoreCase("look at")) {
				player.message("The bridge has collapsed");
				player.message("It seems this rock is placed here to jump from");
			} else if (command.equalsIgnoreCase("jump over")) {
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 30) {
					player.message("You need agility level of 30 to attempt this jump");
					return;
				}
				if (obj.getID() == ROCK_BACK) {
					player.teleport(646, 805);
					say(player, null, "I'm glad that was easier on the way back!");
				} else {
					Npc ogre_guard = ifnearvisnpc(player, NpcId.OGRE_GUARD_BRIDGE.id(), 5);
					if (ogre_guard != null) {
						npcsay(player, ogre_guard, "Oi! Little thing, if you want to cross here",
							"You can pay me first - 20 gold pieces!");
						say(player, ogre_guard, "20 gold pieces to jump off a bridge!!?");
						npcsay(player, ogre_guard, "That's what I said, like it or lump it");
						int menu = multi(player, ogre_guard,
							"Okay i'll pay it",
							"Forget it, i'm not paying");
						if (menu == 0) {
							npcsay(player, ogre_guard, "A wise choice little thing");
							if (!ifheld(player, ItemId.COINS.id(), 20)) {
								npcsay(player, ogre_guard, "And where is your money ? Grrrr!",
									"Do you want to get hurt or something ?");
							} else {
								player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
								if (config().WANT_FATIGUE) {
									if (config().STOP_SKILLING_FATIGUED >= 1
										&& player.getFatigue() >= player.MAX_FATIGUE) {
										player.message("You are too tired to attempt this jump");
										return;
									}
								}
								player.message("You daringly jump across the chasm");
								player.teleport(647, 799);
								player.incExp(Skill.AGILITY.id(), 50, true);
								say(player, null, "Phew! I just made it");
							}
						} else if (menu == 1) {
							npcsay(player, ogre_guard, "In that case you're not crossing");
							player.message("The guard blocks your path");
						}
					} else {
						if (config().WANT_FATIGUE) {
							if (config().STOP_SKILLING_FATIGUED >= 1
								&& player.getFatigue() >= player.MAX_FATIGUE) {
								player.message("You are too tired to attempt this jump");
								return;
							}
						}
						player.message("You daringly jump across the chasm");
						player.teleport(647, 799);
						player.incExp(Skill.AGILITY.id(), 50, true);
						say(player, null, "Phew! I just made it");
					}
				}
			}
		}
		else if (obj.getID() == DARK_PLACE_ROCKS) {
			player.message("You search the rock");
			player.message("There's nothing here");
		}
		else if (obj.getID() == DARK_PLACE_TELEPORT_ROCK) {
			player.message("You search the rock");
			player.message("You uncover a tunnel entrance");
			player.teleport(638, 776);
			say(player, null, "Phew! At last i'm out...",
				"Next time I will take some light!");
		}
		else if (obj.getID() == YANILLE_HOLE) {
			say(player, null, "I can't get through this way",
				"This hole must lead to somewhere...");
		}
		else if (obj.getID() == SKAVID_HOLE) {
			if (config().WANT_FATIGUE) {
				if ((config().STOP_SKILLING_FATIGUED >= 2
					&& player.getFatigue() >= player.MAX_FATIGUE) ||
					(config().STOP_SKILLING_FATIGUED == 1
					&& player.getFatigue() >= 0.95*player.MAX_FATIGUE)) {
					player.message("You are too tired to enter this tunnel");
					return;
				}
			}
			player.playerServerMessage(MessageType.QUEST, "You enter the tunnel");
			player.message("So that's how the skavids are getting into yanille!");
			player.teleport(609, 742);
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == BATTLEMENT || obj.getID() == SOUTH_WEST_BATTLEMENT || inArray(obj.getID(), CAVE_EXITS);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (inArray(obj.getID(), CAVE_EXITS)) { // 6 WEBS!!
			if (obj.getID() == CAVE_EXITS[0]) {
				player.teleport(648, 769);
			} else if (obj.getID() == CAVE_EXITS[1]) {
				player.teleport(638, 777);
			} else if (obj.getID() == CAVE_EXITS[2]) {
				player.teleport(629, 777);
			} else if (obj.getID() == CAVE_EXITS[3]) {
				player.teleport(631, 789);
			} else if (obj.getID() == CAVE_EXITS[4]) {
				player.teleport(624, 807);
			} else if (obj.getID() == CAVE_EXITS[5]) {
				player.teleport(645, 812);
			}
		}
		else if (obj.getID() == BATTLEMENT) {
			say(player, null, "What's this ?",
				"The bridge is out - i'll need to find another way in",
				"I can see a ladder up there coming out of a hole",
				"Maybe I should check out some of these tunnels around here...");
		}
		else if (obj.getID() == SOUTH_WEST_BATTLEMENT) {
			Npc ogre_guard = ifnearvisnpc(player, NpcId.OGRE_GUARD_BATTLEMENT.id(), 5);
			if (player.getX() <= 664) {
				player.teleport(player.getX() + 1, player.getY());
			} else {
				if (player.getCache().hasKey("has_ogre_gift") || player.getQuestStage(Quests.WATCHTOWER) == -1) {
					if (ogre_guard != null) {
						npcsay(player, ogre_guard, "It's that creature again",
							"This time we will let it go...");
					}
					player.teleport(player.getX() - 1, player.getY());
					player.message("You climb over the battlement");
				} else if (player.getCache().hasKey("get_ogre_gift")) {
					if (ogre_guard != null) {
						cakeCheckGuard(player, ogre_guard);
					}
				} else {
					if (ogre_guard != null) {
						npcsay(player, ogre_guard, "Oi! where do you think you are going ?",
							"You are for the cooking pot!");
						int menu = multi(player, ogre_guard,
							"But I am a friend to ogres...",
							"Not if I can help it");
						if (menu == 0) {
							npcsay(player, ogre_guard, "Prove it to us with a gift",
								"Get us something from the market");
							say(player, ogre_guard, "Like what ?");
							npcsay(player, ogre_guard, "Surprise us...");
							player.getCache().store("get_ogre_gift", true);
						} else if (menu == 1) {
							npcsay(player, ogre_guard, "You can help by being tonight's dinner...",
								"Or you can go away, now what shall it be ?");
							int subMenu = multi(player, ogre_guard,
								"Okay, okay i'm going",
								"I tire of ogres, prepare to die!");
							if (subMenu == 0) {
								npcsay(player, ogre_guard, "back to whence you came");
								player.teleport(635, 774);
							} else if (subMenu == 1) {
								npcsay(player, ogre_guard, "Grrrrr!");
								ogre_guard.startCombat(player);
							}
						}
					}
				}
			}
		}
	}

	private void randomizedChest(Player player, GameObject o) {
		player.message("You open the chest");
		openChest(o, 2500, 1002);
		int[] randomChestReward = {NpcId.POISON_SCORPION.id(), NpcId.POISON_SPIDER.id(), NpcId.CHAOS_DWARF.id(), NpcId.RAT_LVL8.id(),
				ItemId.ROTTEN_APPLES.id(), ItemId.BONES.id(), ItemId.EMERALD.id(), ItemId.BURNT_PIKE.id()};
		int choosenReward = (int) (Math.random() * randomChestReward.length);
		if (choosenReward == 0) {
			say(player, null, "Hey! a scorpion is in here!");
			Npc scorp = addnpc(player.getWorld(), NpcId.POISON_SCORPION.id(), o.getX() - 1, o.getY(), 60000 * 5);
			scorp.startCombat(player);
		} else if (choosenReward == 1) {
			say(player, null, "Oh no, not one of these spider things!");
			Npc spider = addnpc(player.getWorld(), NpcId.POISON_SPIDER.id(), o.getX() - 1, o.getY(), 60000 * 5);
			spider.startCombat(player);
		} else if (choosenReward == 2) {
			say(player, null, "How on earth did this dwarf get in here ?");
			Npc dwarf = addnpc(player.getWorld(), NpcId.CHAOS_DWARF.id(), o.getX() - 1, o.getY(), 60000 * 5);
			dwarf.startCombat(player);
		} else if (choosenReward == 3) {
			say(player, null, "Ugh! a dirty rat!");
			addnpc(player.getWorld(), NpcId.RAT_LVL8.id(), o.getX() - 1, o.getY(), 60000 * 5);
		} else if (choosenReward == 4) {
			say(player, null, "Oh dear, I bet these apples taste disgusting");
			give(player, ItemId.ROTTEN_APPLES.id(), 1);
		} else if (choosenReward == 5) {
			say(player, null, "Oh great, some bones!");
			give(player, ItemId.BONES.id(), 1);
		} else if (choosenReward == 6) {
			say(player, null, "Wow, look at the size of this emerald!");
			give(player, ItemId.EMERALD.id(), 1);
		} else if (choosenReward == 7) {
			say(player, null, "Burnt fish - why did I bother ?");
			give(player, ItemId.BURNT_PIKE.id(), 1);
		}
		player.message("The chest snaps shut");
	}

	public static int[] coordModifier(Player player, boolean up, GameObject object) {
		if (object.getGameObjectDef().getHeight() <= 1) {
			return new int[]{player.getX(), Formulae.getNewY(player.getY(), up)};
		}
		int[] coords = {object.getX(), Formulae.getNewY(object.getY(), up)};
		switch (object.getDirection()) {
			case 0:
				coords[1] -= (up ? -object.getGameObjectDef().getHeight() : 1);
				break;
			case 2:
				coords[0] -= (up ? -object.getGameObjectDef().getHeight() : 1);
				break;
			case 4:
				coords[1] += (up ? -1 : object.getGameObjectDef().getHeight());
				break;
			case 6:
				coords[0] += (up ? -1 : object.getGameObjectDef().getHeight());
				break;
		}
		return coords;
	}

	private void cakeCheckGuard(Player player, Npc ogre_guard) {
		npcsay(player, ogre_guard, "Stop creature!... Oh its you",
			"Well what have you got for us then ?");
		if (player.getCarriedItems().hasCatalogID(ItemId.ROCK_CAKE.id(), Optional.of(false))) {
			say(player, ogre_guard, "How about this ?");
			player.message("You give the guard a rock cake");
			player.getCarriedItems().remove(new Item(ItemId.ROCK_CAKE.id()));
			npcsay(player, ogre_guard, "Well well, looks at this",
				"My favourite, rock cake!",
				"Okay we will let it through");
			player.teleport(663, 812);
			player.message("You climb over the battlement");
			player.getCache().remove("get_ogre_gift");
			player.getCache().store("has_ogre_gift", true);
		} else {
			say(player, ogre_guard, "I didn't bring anything");
			npcsay(player, ogre_guard, "Didn't bring anything!",
				"In that case shove off!");
			player.message("The guard pushes you out of the city");
			player.teleport(635, 774);
		}
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.OGRE_GUARD_BATTLEMENT.id()) {
			switch (ItemId.getById(item.getCatalogId())) {
				case OGRE_RELIC:
					npcsay(player, npc, "It's a relic, what of it ?");
					say(player, npc, "Ow!");
					player.playerServerMessage(MessageType.QUEST, "The guard gives you a smack around the head");
					npcsay(player, npc, "Bring me something good next time!");
					break;
				case ROCK_CAKE:
					Npc ogre_guard = ifnearvisnpc(player, NpcId.OGRE_GUARD_BATTLEMENT.id(), 5);
					if (player.getCache().hasKey("get_ogre_gift")) {
						cakeCheckGuard(player, ogre_guard);
					} else {
						say(player, npc, "Why am I giving this cake to this ogre ???");
					}
					break;
			}
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.OGRE_GUARD_BATTLEMENT.id() && !item.getNoted();
	}
}
