package com.openrsc.server.plugins.quests.members.shilovillage;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ShiloVillageObjects implements OpLocTrigger, UseLocTrigger {

	/* Objects */
	private static final int SPEC_STONE = 674;
	private static final int BUMPY_DIRT = 651;
	private static final int PILE_OF_RUBBLE = 670;
	private static final int SMASHED_TABLE = 697;
	private static final int WET_ROCKS = 696;
	private static final int CAVE_SACK = 783;
	private static final int ROTTEN_GALLOWS = 682;

	private static final int PILE_OF_RUBBLE_TATTERED_SCROLL = 683;
	private static final int BRIDGE_BLOCKADE = 691;

	private static final int WELL_STACKED_ROCKS = 688;

	private static final int TOMB_DOLMEN_HANDHOLDS = 690;

	private static final int SEARCH_TREE_FOR_ENTRANCE = 573;

	private static final int HILLSIDE_ENTRANCE = 572;

	private static final int RASH_EXIT_DOOR = 583;

	private static final int METALLIC_DUNGEON_GATE = 577;

	private static final int CLIMB_CAVE_ROCKS = 719;

	private static final int TOMB_DOORS = 794;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), SPEC_STONE, BUMPY_DIRT, PILE_OF_RUBBLE, SMASHED_TABLE, WET_ROCKS, CAVE_SACK, ROTTEN_GALLOWS, PILE_OF_RUBBLE_TATTERED_SCROLL,
				WELL_STACKED_ROCKS, TOMB_DOLMEN_HANDHOLDS, SEARCH_TREE_FOR_ENTRANCE, HILLSIDE_ENTRANCE, RASH_EXIT_DOOR, METALLIC_DUNGEON_GATE, CLIMB_CAVE_ROCKS,
				TOMB_DOORS) || (obj.getID() == BRIDGE_BLOCKADE && command.equalsIgnoreCase("Investigate"));
	}

	// 572
	// 377 3633
	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == TOMB_DOORS) {
			if (command.equalsIgnoreCase("Open")) {
				if (player.getCache().hasKey("tomb_door_shilo")) {
					if (player.getY() >= 3632) {
						changeloc(obj, 800, 497);
						player.teleport(377, 3631);
					} else {
						changeloc(obj, 800, 497);
						player.teleport(377, 3633);
					}
					return;
				}
				player.message("This door is completely sealed, it is very ornately carved.");
			} else if (command.equalsIgnoreCase("Search")) {
				mes(player, "The door is ornately carved with depictions of skeletal warriors.",
					"You notice that some of the skeletal warriors depictions are not complete.",
					"Instead, there are reccesses were some of the bones should be.");
				player.message("There are three recesses.");
			}
		}
		else if (obj.getID() == CLIMB_CAVE_ROCKS) {
			if (config().WANT_FATIGUE) {
				if (config().STOP_SKILLING_FATIGUED >= 1
					&& player.getFatigue() >= player.MAX_FATIGUE) {
					player.message("You are too fatigued to go any further.");
					return;
				}
			}
			if(!player.getCarriedItems().getEquipment().hasEquipped(ItemId.BEADS_OF_THE_DEAD.id())) {
				//going down
				if(obj.getY() > player.getY()) {
					player.message("@red@You simply cannot concentrate enough to climb down the rocks.");
				}
				//going up
				else {
					player.message("@red@You simply cannot concentrate enough to climb up the rocks.");
				}
				return;
			}
			player.message("You carefully pick your way through the rocks.");
			player.teleport(349, 3618);
			if (ShiloVillageUtils.succeed(player, 32)) {
				delay(config().GAME_TICK * 2);
				if (obj.getY() == 3619) {
					player.teleport(348, 3616);
					delay(config().GAME_TICK * 2);
					player.message("You manage to carefully clamber up.");
				} else {
					player.teleport(348, 3620);
					delay(config().GAME_TICK * 2);
					player.message("You manage to carefully clamber down.");
				}
			} else {
				player.message("@red@You fall!");
				delay(config().GAME_TICK * 2);
				player.teleport(348, 3620);
				player.message("You take damage!");
				player.damage(3);
				delay(config().GAME_TICK * 2);
				player.damage(0);
				say(player, null, "Ooooff!");
			}
			player.incExp(Skills.AGILITY, 5, true);
		}
		else if (obj.getID() == METALLIC_DUNGEON_GATE) {
			if (command.equalsIgnoreCase("Open")) {
				if (player.getY() >= 3616) {
					changeloc(obj, config().GAME_TICK * 3, 181);
					player.teleport(348, 3614);
					return;
				}
				player.message("The gates feel unearthly cold to the touch!");
				player.message("Are you sure you want to go through?");
				int menu = multi(player,
					"Yes, I am completely fearless!",
					"Err, I'm having second thoughts now!");
				if (menu == 0) {
					if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.BEADS_OF_THE_DEAD.id())) {
						changeloc(obj, config().GAME_TICK * 3, 181);
						player.teleport(348, 3616);
						player.damage(getCurrentLevel(player, Skills.HITS) / 2 + 1);
						if (getCurrentLevel(player, Skills.HITS) > 0) {
							mes(player, "@red@You feel invisible hands starting to choke you...");
							player.teleport(348, 3614);
							delay(config().GAME_TICK * 2);
							say(player, null, "*Cough*",
								"*Choke*");
							player.message("@red@You can barely manage to crawl back through the gates...");
							say(player, null, "*Cough*",
								"*Choke*",
								"*...*",
								"* Gaaaa....*");
						}
					} else {
						changeloc(obj, config().GAME_TICK * 3, 181);
						player.teleport(348, 3616);
						displayTeleportBubble(player, player.getX(), player.getY(), false);
						player.message("@red@The Beads of the dead start to glow...");
					}
				} else if (menu == 1) {
					player.teleport(348, 3614);
					player.message("You manage to pull your spineless body away from the ancient gates.");
				}
			} else if (command.equalsIgnoreCase("Search")) {
				mes(player, "There is an ancient symbol on the gate.",
					"It looks like a human figure with something around it's neck.");
				player.message("It looks pretty scary.");
			}
		}
		else if (obj.getID() == RASH_EXIT_DOOR) {
			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.BEADS_OF_THE_DEAD.id())) {
				mes(player, "@red@You feel invisible hands starting to choke you...");
				player.damage(18); // todo?
			}
			if (command.equalsIgnoreCase("Open")) {
				mes(player, "The door seems to be locked!");
				say(player, null, "Oh no, I'm going to be stuck in here forever!",
					"How will I ever get out!",
					"I'm too young to die!");
			} else if (command.equalsIgnoreCase("Search")) {
				player.message("You can see a small recepticle, not unlike the one on the opposite side of the door!");
			}
		}
		else if (obj.getID() == HILLSIDE_ENTRANCE) {
			if (command.equalsIgnoreCase("Open")) {
				mes(player, "There seems to be some sort of recepticle,");
				player.message("perhaps it needs a key?");
				if (!player.getCache().hasKey("can_chisel_bone") && player.getQuestStage(Quests.SHILO_VILLAGE) == 7) {
					player.getCache().store("can_chisel_bone", true);
				}
			} else if (command.equalsIgnoreCase("Search")) {
				mes(player, "Examining the door, you see that it has a very strange lock.");
				player.message("Ewww...it seems to be made out of bone!");
				if (!player.getCache().hasKey("can_chisel_bone") && player.getQuestStage(Quests.SHILO_VILLAGE) == 7) {
					player.getCache().store("can_chisel_bone", true);
				}
			}
		}
		else if (obj.getID() == SEARCH_TREE_FOR_ENTRANCE) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1 &&
				!config().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE) {
				player.message("You find nothing significant.");
				return;
			}
			mes(player, "You pull the trees apart...");
			player.message("...and reveal an ancient doorway set into the side of the hill!");
			GameObject ENTRANCE = new GameObject(player.getWorld(), Point.location(350, 782), HILLSIDE_ENTRANCE, 2, 0);
			addloc(ENTRANCE);
			addloc(player.getWorld(), new GameObject(player.getWorld(), Point.location(350, 782), 398, 2, 0).getLoc(), 15000);
		}
		else if (obj.getID() == TOMB_DOLMEN_HANDHOLDS) {
			mes(player, "You start to climb up the side of the rock wall using the hand holds");
			if (ShiloVillageUtils.succeed(player, 32)) {
				player.message("You push your way through a cunningly designed trap door..");
				player.teleport(471, 836);
				delay(config().GAME_TICK);
				player.message("And appear in bright sunshine and the salty sea air.");
			} else {
				player.message("You get halfway but loose your grip.");
				mes(player, "You fall back to the floor.");
				player.teleport(380, 3692);
				say(player, null, "Ahhhhh!");
				player.damage(getCurrentLevel(player, Skills.HITS) / 10);
				mes(player, "And it knocks the wind out of you.");
				player.damage(getCurrentLevel(player, Skills.HITS) / 10);
				player.teleport(467, 3674);
				say(player, null, "Oooff!");
			}
		}
		else if (obj.getID() == WELL_STACKED_ROCKS) {
			if (command.equalsIgnoreCase("Investigate")) {
				player.message("Rocks that have been stacked uniformly.");
			} else if (command.equalsIgnoreCase("Search")) {
				if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
					player.message("This tomb entrance seems to be completely flooded.");
					player.message("A great sense of peace pervades in this area.");
				} else if (player.getQuestStage(Quests.SHILO_VILLAGE) >= 5) {
					mes(player, "You investigate the rocks and find a dank,narrow crawl-way.",
						"Do you want to crawl into this dank, dark, narrow,",
						"possibly dangerous hole?");
					int menu = multi(player,
						"Yes please, I can think of nothing nicer !",
						"No way could you get me to go in there !");
					if (menu == 0) {
						player.message("You contort your body and prepare to squirm, worm like, into the hole.");
						if (ShiloVillageUtils.succeed(player, 32)) {
							mes(player, "You struggle through the narrow crevice in the rocks");
							player.teleport(471, 3658);
							player.message("and drop to your feet into a narrow underground corridor");
							if (player.getQuestStage(Quests.SHILO_VILLAGE) == 5) {
								player.updateQuestStage(Quests.SHILO_VILLAGE, 6);
							}
						}
						else {
							mes(player, "You managed to get yourself stuck.",
									"You have to wrench yourself free to get out.",
									"You manage to pull yourself out, but hurt yourself in the process.");
							player.damage(3);
							delay(config().GAME_TICK * 2);
							player.damage(0);
							player.message("Maybe you'll have better luck next time?");
						}
					} else if (menu == 1) {
						player.message("You decide that the surface is the place for you!");
					}
				} else {
					player.message("You find nothing of significance.");
					player.message("And it does look quite scarey.");
				}
			}
		}
		else if (obj.getID() == BRIDGE_BLOCKADE && command.equalsIgnoreCase("Investigate")) {
			player.message("Someone has put this here to prevent access to the other side.");
			player.message("The remainder of the bridge looks even more rickety..");
		}
		else if (obj.getID() == PILE_OF_RUBBLE_TATTERED_SCROLL) {
			mes(player, "You can see that there is something hidden behind some of the rocks.",
				"Do you want to have a look?");
			player.message("It looks a bit dangerous because the ceiling doesn't look safe!");
			int menu = multi(player,
				"Yes, I'll carefully move the rocks to see what's behind them.",
				"No, I'll leave them, I don't like the look of that ceiling.");
			if (menu == 0) {
				if (player.getCarriedItems().hasCatalogID(ItemId.TATTERED_SCROLL.id(), Optional.of(false))) {
					player.message("You see nothing here but an empty book case behind rocks.");
				} else {
					mes(player, "You start to slowly move the rocks to one side.");
					if (ShiloVillageUtils.succeed(player, 32)) {
						mes(player, "You carefully manage to remove enough rocks to see a book shelf.",
							"You gingerly remove a delicate scroll from the shelf");
						player.message("and place it carefully in your inventory.");
						give(player, ItemId.TATTERED_SCROLL.id(), 1);
						if (!player.getCache().hasKey("obtained_shilo_info")) {
							player.getCache().store("obtained_shilo_info", true);
						}
						player.incExp(Skills.AGILITY, 15, true);
					} else {
						mes(player, "You acidently knock some rocks and the ceiling starts to cave in.");
						mes(player, "Some rocks fall on you.");
						player.damage((int) (getCurrentLevel(player, Skills.HITS) * 0.1D + 1));
						player.incExp(Skills.AGILITY, 5, true);
					}
				}
			} else if (menu == 1) {
				mes(player, "You decide to leave the rocks well alone.");
				player.message("The ceiling does look a little unsafe.");
			}
		}
		else if (obj.getID() == ROTTEN_GALLOWS) {
			if (command.equalsIgnoreCase("Look")) {
				mes(player, "You take a look at the Gallows.",
					"The gallows look pretty eerie.");
				if (player.getCarriedItems().hasCatalogID(ItemId.ZADIMUS_CORPSE.id(), Optional.of(false)) || player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
					mes(player, "An empty noose swings eerily in the half light of the tomb.");
				} else {
					mes(player, "A grisly sight meets your eyes. A human corpse hangs from the noose.",
							"His hands have been tied behind his back.");
				}
			} else if (command.equalsIgnoreCase("Search")) {
				mes(player, "You search the gallows.");
				if (player.getCarriedItems().hasCatalogID(ItemId.ZADIMUS_CORPSE.id(), Optional.of(false)) || player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
					player.message("The gallows look pretty eerie. You search but find nothing.");
				} else {
					mes(player, "You find a human corpse hanging in the noose.",
						"It looks as if the corpse will be removed easily.",
						"Would you like to remove the corpse from the noose?");
					int menu = multi(player,
						"I don't think so it might animate and attack me!",
						"Yes, I may find something else on the corpse");
					if (menu == 0) {
						mes(player, "You move away from the corpse quietly and slowly...",
							"...you have an eerie feeling about this!");
						say(player, null, "** Gulp! **");
					} else if (menu == 1) {
						mes(player, "You gently support the frame of the skeleton and lift the skull through the noose.",
							"You find an old sack and place the skeleton in this.",
							"Maybe Trufitus can give you some tips on what to do with it.",
							"You sense that there is a spirit that needs to be put to rest.");
						give(player, ItemId.ZADIMUS_CORPSE.id(), 1);
						if (!player.getCache().hasKey("obtained_shilo_info")) {
							player.getCache().store("obtained_shilo_info", true);
						}
					}
				}
			}
		}
		else if (obj.getID() == CAVE_SACK) {
			if (player.getCarriedItems().hasCatalogID(ItemId.CRUMPLED_SCROLL.id(), Optional.of(false))) {
				player.message("You find nothing in the sacks.");
			} else {
				player.message("You find a tattatered, very ornate scroll.");
				player.message("Which you place carefully in your inventory.");
				give(player, ItemId.CRUMPLED_SCROLL.id(), 1);
				if (!player.getCache().hasKey("obtained_shilo_info")) {
					player.getCache().store("obtained_shilo_info", true);
				}
			}
		}
		else if (obj.getID() == WET_ROCKS) {
			mes(player, "You see a huge waterfall blocking your path.",
				"The rocks look quite perilous but you could try scale them.");
			player.message("Or maybe you could use something to float through the waterfall?");
			int m = multi(player,
				"Yes, I'll try to climb out",
				"No, thanks, I'll look for another exit.");
			if (m == 0) {
				mes(player, "You start searching for handholds in the slippery cave entrance...");
				player.teleport(342, 3684);
				if (ShiloVillageUtils.succeed(player, 32)) {
					mes(player, "@red@*** YOU FALL ***",
						"You slip into the water and get washed out through the waterfall!",
						"You're pumelled as the thrashing water throws",
						"you against the rocks...");
					player.teleport(339, 808);
					mes(player, "You are washed onto the waterfall river bank");
					player.message("barely alive!");
					player.damage((int) (getCurrentLevel(player, Skills.HITS) * 0.2D + 4));
					player.incExp(Skills.AGILITY, 5, true);
				} else {
					mes(player, "You manage to work your way along the slippery wall");
					mes(player, "and avoid falling into the water below.");
					player.teleport(344, 808);
					mes(player, "You make it out of the cave");
					player.message("and into the warmth of the jungle.");
					player.incExp(Skills.AGILITY, 100, true);
				}
			} else if (m == 1) {
				mes(player, "You decide to have another look around.");
				player.message("And see if you can find a better way to get out.");
			}
		}
		else if (obj.getID() == SMASHED_TABLE) {
			// 698
			if (command.equalsIgnoreCase("Examine")) {
				mes(player, "This table might be useful...");
				player.message("with some adjustment");
			} else if (command.equalsIgnoreCase("Craft")) {
				mes(player, "You may be able to turn this delapidated table into ",
					"something that could help you to get out of this place.",
					"What would you like to try and turn this table into?");
				int sub = multi(player,
					"A ladder",
					"A crude raft",
					"A pole vault");
				if (sub == 0) {
					mes(player, "Your experience in crafting tells you that");
					player.message("there isn't enough wood to complete this task.");
				} else if (sub == 1) {
					// WATER RAFT SCENE
					mes(player, "You see that this table already looks very sea worthy",
						"it takes virtually no time at all to help fix it into.");
					mes(player, "a crude raft.");
					GameObject RAFT_ONE = new GameObject(player.getWorld(), Point.location(353, 3669), 698, 0, 0);
					addloc(RAFT_ONE);
					player.teleport(353, 3669);
					mes(player, "You place it carefully on the water!",
						"You board the raft!",
						"You push off!");
					delloc(RAFT_ONE);

					GameObject RAFT_TWO = new GameObject(player.getWorld(), Point.location(357, 3673), 698, 0, 0);
					addloc(RAFT_TWO);
					player.teleport(357, 3673);
					say(player, null, "Weeeeeeee!");
					delay(config().GAME_TICK);
					delloc(RAFT_TWO);

					GameObject RAFT_THREE = new GameObject(player.getWorld(), Point.location(356, 3678), 698, 0, 0);
					addloc(RAFT_THREE);
					player.teleport(356, 3678);
					delay(config().GAME_TICK * 2);
					delloc(RAFT_THREE);

					GameObject RAFT_FOUR = new GameObject(player.getWorld(), Point.location(353, 3682), 698, 0, 0);
					addloc(RAFT_FOUR);
					player.teleport(353, 3682);
					say(player, null, "Weeeeeeee!");
					delay(config().GAME_TICK);
					delloc(RAFT_FOUR);

					GameObject RAFT_FIVE = new GameObject(player.getWorld(), Point.location(349, 3685), 698, 0, 0);
					addloc(RAFT_FIVE);
					player.teleport(349, 3685);
					delay(config().GAME_TICK);
					delloc(RAFT_FIVE);

					GameObject RAFT_SIX = new GameObject(player.getWorld(), Point.location(345, 3686), 698, 0, 0);
					addloc(RAFT_SIX);
					player.teleport(345, 3686);
					player.message("You come to a huge waterfall...");
					say(player, null, "* Oh oh! *");
					delay(config().GAME_TICK);
					delloc(RAFT_SIX);

					GameObject RAFT_FINAL = new GameObject(player.getWorld(), Point.location(341, 3686), 698, 0, 0);
					addloc(RAFT_FINAL);
					player.teleport(341, 3686);
					mes(player, "...and plough through it!");
					delloc(RAFT_FINAL);
					player.message("The raft soon breaks up.");
					delay(config().GAME_TICK);
					player.teleport(341, 810);
				} else if (sub == 2) {
					mes(player, "You happily start hacking away at the table",
							"But realise that you won't have enough woood to properly finish the item off!");
					say(player, null, "Oops! Not enough wood left to do anything else with the table!");
					mes(player, "There isn't enough wood left in this table to make anything!");
				}
			}
		}
		else if (obj.getID() == PILE_OF_RUBBLE) {
			mes(player, "You can see that there is a narrow gap through into darkness.",
				"You could try to wriggle through and see where it takes you.");
			int menu = multi(player,
				"Yes, I'll wriggle through.",
				"No, I'll stay here.");
			if (menu == 0) {
				player.message("You manage to wriggle through the rubble");
				if (obj.getX() == 348 && obj.getY() == 3708) {
					player.teleport(356, 3667);
				} else if (obj.getX() == 357 && obj.getY() == 3668) {
					player.teleport(347, 3709);
				}
				player.incExp(Skills.AGILITY, 10, true);
			} else if (menu == 1) {
				player.message("You decide to stay where you are");
			}
		}
		else if (obj.getID() == BUMPY_DIRT) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				player.message("The entrance seems to have caved in.");
			} else if (player.getQuestStage(Quests.SHILO_VILLAGE) >= 2) {
				if (player.getCache().hasKey("SV_DIG_BUMP")) {
					mes(player, "You see a small fissure in the granite",
							"that you might just be able to crawl through.");
					if(!player.getCarriedItems().hasCatalogID(ItemId.LIT_CANDLE.id(), Optional.of(false)) && !player.getCache().hasKey("SV_DIG_LIT")) {
						player.message("It's very dark beyond the fissure.");
					}
					ShiloVillageUtils.BUMPY_DIRT_HOLDER(player);
					return;
				}
				if (command.equalsIgnoreCase("Look")) {
					player.message("It looks as if something is buried here.");
				} else if (command.equalsIgnoreCase("Search")) {
					mes(player, "It looks as if something is buried here.");
					player.message("It looks quite big, you may need some tools to excavate further.");
				}
			} else {
				player.message("It just looks like some bumpy ground");
			}
		}
		else if (obj.getID() == SPEC_STONE) {
			if (command.equalsIgnoreCase("Look Closer")) {
				player.message("This stone seems to have strange markings on it");
			}
			else if (command.equalsIgnoreCase("Investigate")) {
				mes(player, "This stone seems to have strange markings on it",
						"Maybe Trufitus can decipher them.",
						"The stone is too heavy to carry",
						"But the letters stand proud on a plaque",
						"Maybe you could seperate the plaque from the rock?");
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (obj.getID() == BUMPY_DIRT && item.getCatalogId() == ItemId.SPADE.id())
				|| (obj.getID() == BUMPY_DIRT && item.getCatalogId() == ItemId.LIT_CANDLE.id())
				|| (obj.getID() == BUMPY_DIRT && item.getCatalogId() == ItemId.ROPE.id())
				|| (obj.getID() == HILLSIDE_ENTRANCE && item.getCatalogId() == ItemId.BONE_SHARD.id())
				|| (obj.getID() == HILLSIDE_ENTRANCE && item.getCatalogId() == ItemId.BONE_KEY.id())
				|| (obj.getID() == RASH_EXIT_DOOR && item.getCatalogId() == ItemId.BONE_KEY.id())
				|| (obj.getID() == TOMB_DOORS && item.getCatalogId() == ItemId.BONES.id())
				|| (obj.getID() == SPEC_STONE && item.getCatalogId() == ItemId.CHISEL.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == TOMB_DOORS && item.getCatalogId() == ItemId.BONES.id()) {
			if (!ifheld(player, ItemId.BONES.id(), 3)) {
				player.message("You do not have enough bones for all the recesses.");
			} else {
				player.getCarriedItems().remove(new Item(ItemId.BONES.id(), 3));
				mes(player, "You fit the bones into the reccesses of the door.",
					"The door seems to change slightly.",
					"Two depictions of skeletal warriors turn their heads towards you.",
					"They are alive!",
					"The Skeletons wrench themselves free of the door.",
					"Stepping out of the door, with grinning teeth they push the huge doors open.");
				changeloc(obj, 800, 497);
				player.teleport(377, 3631);
				if (!player.getCache().hasKey("tomb_door_shilo")) {
					player.getCache().store("tomb_door_shilo", true);
				}
			}
		}
		else if (obj.getID() == RASH_EXIT_DOOR && item.getCatalogId() == ItemId.BONE_KEY.id()) {
			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.BEADS_OF_THE_DEAD.id())) {
				mes(player, "@red@You feel invisible hands starting to choke you...");
				player.damage(getCurrentLevel(player, Skills.HITS) / 2);
			}
			mes(player, "You insert the key into the lock and it merges with the door.",
					"The doors creak open revealing bright day light.");
			player.message("You walk outside into the warmth of the Jungle heat.");
			player.teleport(350, 782);
			if (player.getCache().hasKey("tomb_door_shilo")) {
				player.getCache().remove("tomb_door_shilo");
			}
		}
		else if (obj.getID() == HILLSIDE_ENTRANCE && item.getCatalogId() == ItemId.BONE_KEY.id()) {
			mes(player, "You try the key with the lock.");
			mes(player, "As soon as you push the key into the lock.");
			displayTeleportBubble(player, 350, 782, true);
			delay(config().GAME_TICK * 2);
			player.message("A shimmering light dances over the doors, before you can blink, the doors creak open.");
			player.teleport(348, 3611);
			delay(config().GAME_TICK);
			mes(player, "You feel a strange force pulling you inside.",
				"The doors close behind you with the sound of crunching bone.",
				"Before you stretches a winding tunnel blocked by an ancient gate.");
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == 7) {
				player.updateQuestStage(Quests.SHILO_VILLAGE, 8);
			}
		}
		else if (obj.getID() == HILLSIDE_ENTRANCE && item.getCatalogId() == ItemId.BONE_SHARD.id()) {
			mes(player, "You try to use the bone shard on the lock.",
				"Although it isabout the right size,");
			player.message("you find that it just doesn't fit the delicate lock mechanism.");
		}
		else if (obj.getID() == BUMPY_DIRT && item.getCatalogId() == ItemId.ROPE.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				player.message("The entrance seems to have caved in.");
			} else if (player.getQuestStage(Quests.SHILO_VILLAGE) >= 2) {
				// player has not lit place on bumpy dirt
				if (!player.getCache().hasKey("SV_DIG_LIT")) {
					player.message("It's too dark to see where to attach it.");
				}
				else if (!player.getCache().hasKey("SV_DIG_ROPE")) {
					mes(player, "You see where to attach the rope very clearly.",
							"You secure it well.");
					player.message("A rope is already secured there");
					player.getCache().store("SV_DIG_ROPE", true);
					player.getCarriedItems().remove(new Item(ItemId.ROPE.id()));
				}
				else {
					player.message("A rope is already secured there");
				}
			} else {
				//possibly had other behavior
				player.message("Nothing interesting happens");
			}
		}
		else if (obj.getID() == BUMPY_DIRT && item.getCatalogId() == ItemId.LIT_CANDLE.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				player.message("The entrance seems to have caved in.");
			} else if (player.getQuestStage(Quests.SHILO_VILLAGE) >= 2) {
				// player has not lit place on bumpy dirt
				if (!player.getCache().hasKey("SV_DIG_LIT")) {
					mes(player, "You hold the candle to the fissure and see that",
							"there is quite a large drop after you get through the hole.");
					if(!player.getCarriedItems().hasCatalogID(ItemId.ROPE.id(), Optional.of(false))) {
						player.message("It's a pity you don't have some rope");
					}
					else {
						player.message("Some rope might help here");
					}
					player.getCache().store("SV_DIG_LIT", true);
					player.getCarriedItems().remove(new Item(ItemId.LIT_CANDLE.id()));
				}
				else {
					//what's authentic behavior here?
					player.message("The spot is already lit");
				}
			} else {
				//possibly had other behavior
				player.message("Nothing interesting happens");
			}
		}
		else if (obj.getID() == BUMPY_DIRT && item.getCatalogId() == ItemId.SPADE.id()) {
			if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
				player.message("The entrance seems to have caved in.");
			} else if (player.getQuestStage(Quests.SHILO_VILLAGE) >= 2) {
				if (!player.getCache().hasKey("SV_DIG_BUMP")) {
					mes(player, "You dig a small hole and almost immediately hit granite",
						"You excavate the hole a bit more and see that there is a small fissure",
						"that you might just be able to crawl through.");
					if(!player.getCarriedItems().hasCatalogID(ItemId.LIT_CANDLE.id(), Optional.of(false)) && !player.getCache().hasKey("SV_DIG_LIT")) {
						player.message("It's very dark beyond the fissure.");
					}
					player.getCache().store("SV_DIG_BUMP", true);
					ShiloVillageUtils.BUMPY_DIRT_HOLDER(player);
				} else {
					player.message("You have already excavated this area.");
					player.message("Your spade clangs against the granite");
				}
			} else {
				mes(player, "You start digging...",
					"But without knowing what you're digging for...");
				player.message("you decide to give up.");
			}
		}
		else if (obj.getID() == SPEC_STONE && item.getCatalogId() == ItemId.CHISEL.id()) {
			mes(player, "You cleanly cut the plaque of letters away from the rock.",
					"You place it carefully into your inventory.");
			give(player, ItemId.STONE_PLAQUE.id(), 1);
			player.incExp(Skills.CRAFTING, 10, true);
		}
	}

}
