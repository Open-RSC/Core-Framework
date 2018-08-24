package com.openrsc.server.plugins.quests.members.shilovillage;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class ShiloVillageObjects implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	/* Objects */
	public static final int BUMPY_DIRT = 651;
	public static final int PILE_OF_RUBBLE = 670;
	public static final int SMASHED_TABLE = 697;
	public static final int WET_ROCKS = 696;
	public static final int CAVE_SACK = 783;
	public static final int ROTTEN_GALLOWS = 682;

	public static final int PILE_OF_RUBBLE_TATTERED_SCROLL = 683;
	public static final int BRIDGE_BLOCKADE = 691;

	public static final int WELL_STACKED_ROCKS = 688;

	public static final int TOMB_DOLMEN_HANDHOLDS = 690;

	public static final int SEARCH_TREE_FOR_ENTRANCE =  573;

	public static final int HILLSIDE_ENTRANCE = 572;

	public static final int RASH_EXIT_DOOR = 583;

	public static final int METALLIC_DUNGEON_GATE = 577;

	public static final int CLIMB_CAVE_ROCKS = 719;

	public static final int TOMB_DOORS = 794;

	/* Items */
	public static final int SPADE = 211;
	public static final int CRUMPLED_SCROLL = 960;
	public static final int ZADIMUS_CORPSE = 962;
	public static final int TATTERED_SCROLL = 959;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == BUMPY_DIRT) {
			return true;
		}
		if(obj.getID() == PILE_OF_RUBBLE) {
			return true;
		}
		if(obj.getID() == SMASHED_TABLE) {
			return true;
		}
		if(obj.getID() == WET_ROCKS) {
			return true;
		}
		if(obj.getID() == CAVE_SACK) {
			return true;
		}
		if(obj.getID() == ROTTEN_GALLOWS) {
			return true;
		}
		if(obj.getID() == PILE_OF_RUBBLE_TATTERED_SCROLL) {
			return true;
		}
		if(obj.getID() == BRIDGE_BLOCKADE && command.equalsIgnoreCase("Investigate")) {
			return true;
		}
		if(obj.getID() == WELL_STACKED_ROCKS) {
			return true;
		}
		if(obj.getID() == TOMB_DOLMEN_HANDHOLDS) {
			return true;
		}
		if(obj.getID() == SEARCH_TREE_FOR_ENTRANCE) {
			return true;
		}
		if(obj.getID() == HILLSIDE_ENTRANCE) {
			return true;
		}
		if(obj.getID() == RASH_EXIT_DOOR) {
			return true;
		}
		if(obj.getID() == METALLIC_DUNGEON_GATE) {
			return true;
		}
		if(obj.getID() == CLIMB_CAVE_ROCKS) {
			return true;
		}
		if(obj.getID() == TOMB_DOORS) {
			return true;
		}
		return false;
	}
	// 572
	// 377 3633
	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == TOMB_DOORS) {
			if(command.equalsIgnoreCase("Open")) {
				if(p.getCache().hasKey("tomb_door_shilo")) {
					if(p.getY() >= 3632) {
						replaceObjectDelayed(obj, 800, 497);
						p.teleport(377, 3631);
					} else {
						replaceObjectDelayed(obj, 800, 497);
						p.teleport(377, 3633);
					}
					return;
				}
				p.message("This door is completely sealed, it is very ornately carved.");
			} else if(command.equalsIgnoreCase("Search")) {
				message(p, "The door is ornately carved with depictions of skeletal warriors.",
						"You notice that some of the skeletal warriors depictions are not complete.",
						"Instead, there are reccesses were some of the bones should be.");
				p.message("There are three recesses.");
			}
		}
		if(obj.getID() == CLIMB_CAVE_ROCKS) {
			if(p.getFatigue() >= 7500) {
				p.message("You are too fatigued to go any further.");
				return;
			}
			p.setBusy(true);
			p.message("You carefully pick your way through the rocks.");
			p.teleport(349, 3618);
			if(ShiloVillageUtils.succeed(p, 32)) {
				sleep(1000);
				if(obj.getY() == 3619) {
					p.teleport(348, 3616);
					sleep(1000);
					p.message("You manage to carefully clamber up.");
				} else {
					p.teleport(348, 3620);
					sleep(1000);
					p.message("You manage to carefully clamber down.");
				}
			} else {
				p.message("@red@You fall!");
				sleep(1000);
				p.teleport(348, 3620);
				p.message("You take damage!");
				p.damage(3);
				sleep(1000);
				p.damage(0);
				playerTalk(p, null, "Ooooff!");
			}
			p.incExp(AGILITY, 1.0, true);
			p.setBusy(false);
		}
		if(obj.getID() == METALLIC_DUNGEON_GATE) {
			if(command.equalsIgnoreCase("Open")) {
				if(p.getY() >= 3616) {
					replaceObjectDelayed(obj, 2000, 181);
					p.teleport(348, 3614);
					return;
				}
				p.message("The gates feel unearthly cold to the touch!");
				p.message("Are you sure you want to go through?");
				int menu = showMenu(p,
						"Yes, I am completely fearless!",
						"Err, I'm having second thoughts now!");
				if(menu == 0) {
					if(!p.getInventory().wielding(852)) {
						replaceObjectDelayed(obj, 1800, 181);
						p.teleport(348, 3616);
						p.damage(getCurrentLevel(p, HITS) / 2 + 1);
						if(p.getStatus() != Action.DIED_FROM_DAMAGE) {
							message(p, "@red@You feel invisible hands starting to choke you...");
							p.teleport(348, 3614);
							sleep(1200);
							playerTalk(p, null, "*Cough*",
									"*Choke*");
							p.message("@red@You can barely manage to crawl back through the gates...");
							playerTalk(p, null, "*Cough*",
									"*Choke*",
									"*...*",
									"* Gaaaa....*");
						}
					} else {
						replaceObjectDelayed(obj, 2000, 181);
						p.teleport(348, 3616);
						displayTeleportBubble(p, p.getX(), p.getY(), false);
						p.message("@red@The Beads of the dead start to glow...");
					}
				} else if(menu == 1) {
					p.teleport(348, 3614);
					p.message("You manage to pull your spineless body away from the ancient gates.");
				}
			} else if(command.equalsIgnoreCase("Search")) {
				message(p, "There is an ancient symbol on the gate.",
						"It looks like a human figure with something around it's neck.");
				p.message("It looks pretty scary.");
			}
		}
		if(obj.getID() == RASH_EXIT_DOOR) {
			if(!p.getInventory().wielding(852)) {
				message(p, "@red@You feel invisible hands starting to choke you...");
				p.damage(18); // todo?
			}
			if(command.equalsIgnoreCase("Open")) {
				message(p, "The door seems to be locked!");
				playerTalk(p, null, "Oh no, I'm going to be stuck in here forever!",
						"How will I ever get out!",
						"I'm too young to die!");
			} else if(command.equalsIgnoreCase("Search")) {
				p.message("You can see a small recepticle, not unlike the one on the opposite side of the door!");
			}
		}
		if(obj.getID() == HILLSIDE_ENTRANCE) {
			if(command.equalsIgnoreCase("Open")) {
				message(p, "There seems to be some sort of recepticle,");
				p.message("perhaps it needs a key?");
				if(!p.getCache().hasKey("can_chisel_bone") && p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == 7) {
					p.getCache().store("can_chisel_bone", true);
				}
			} else if(command.equalsIgnoreCase("Search")) {
				message(p, "Examining the door, you see that it has a very strange lock.");
				p.message("Ewww...it seems to be made out of bone!");
				if(!p.getCache().hasKey("can_chisel_bone") && p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == 7) {
					p.getCache().store("can_chisel_bone", true);
				}
			}
		}
		if(obj.getID() == SEARCH_TREE_FOR_ENTRANCE) {
			if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == -1) {
				p.message("You find nothing significant.");
				return;
			}
			message(p, "You pull the trees apart...");
			p.message("...and reveal an ancient doorway set into the side of the hill!");
			GameObject ENTRANCE = new GameObject(Point.location(350, 782), HILLSIDE_ENTRANCE, 2, 0);
			registerObject(ENTRANCE);
			delayedSpawnObject(new GameObject(Point.location(350, 782), 398, 2, 0).getLoc(), 15000);
		}
		if(obj.getID() == TOMB_DOLMEN_HANDHOLDS) {
			message(p, "You start to climb up the side of the rock wall using the hand holds");
			if(ShiloVillageUtils.succeed(p, 32)) {
				p.message("You push your way through a cunningly designed trap door..");
				p.teleport(471, 836);
				sleep(600);
				p.message("And appear in bright sunshine and the salty sea air.");
			} else {
				p.message("You get halfway but loose your grip.");
				message(p, "You fall back to the floor.");
				p.teleport(380, 3692);
				playerTalk(p, null, "Ahhhhh!");
				p.damage(getCurrentLevel(p, HITS) / 10);
				message(p, "And it knocks the wind out of you.");
				p.damage(getCurrentLevel(p, HITS) / 10);
				p.teleport(467, 3674);
				playerTalk(p, null, "Oooff!");
			}
		}
		if(obj.getID() == WELL_STACKED_ROCKS) {
			if(command.equalsIgnoreCase("Investigate")) {
				p.message("Rocks that have been stacked uniformly.");
			} else if(command.equalsIgnoreCase("Search")) {
				if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == -1) {
					p.message("This tomb entrance seems to be completely flooded.");
					p.message("A great sense of peace pervades in this area.");
				} else if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) >= 5) {
					message(p, "You investigate the rocks and find a dank,narrow crawl-way.",
							"Do you want to crawl into this dank, dark, narrow,",
							"possibly dangerous hole?");
					int menu = showMenu(p, 
							"Yes please, I can think of nothing nicer !",
							"No way could you get me to go in there !");
					if(menu == 0) {
						p.message("You contort your body and prepare to squirm, worm like, into the hole.");
						message(p, "You struggle through the narrow crevice in the rocks");
						p.teleport(471, 3658);
						p.message("and drop to your feet into a narrow underground corridor");
						if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == 5) {
							p.updateQuestStage(Constants.Quests.SHILO_VILLAGE, 6);
						}
					} else if(menu == 1) {
						p.message("You decide that the surface is the place for you!");
					}
				} else {
					p.message("You find nothing of significance.");
					p.message("And it does look quite scarey.");
				}
			}
		}
		if(obj.getID() == BRIDGE_BLOCKADE && command.equalsIgnoreCase("Investigate")) {
			p.message("Someone has put this here to prevent access to the other side.");
			p.message("The remainder of the bridge looks even more rickety..");
		}
		if(obj.getID() == PILE_OF_RUBBLE_TATTERED_SCROLL) {
			message(p, "You can see that there is something hidden behind some of the rocks.",
					"Do you want to have a look?");
			p.message("It looks a bit dangerous because the ceiling doesn't look safe!");
			int menu = showMenu(p,
					"Yes, I'll carefully move the rocks to see what's behind them.",
					"No, I'll leave them, I don't like the look of that ceiling.");
			if(menu == 0) {
				if(hasItem(p, TATTERED_SCROLL)) {
					p.message("You see nothing here but an empty book case behind rocks.");
				} else {
					message(p, "You start to slowly move the rocks to one side.");
					if(ShiloVillageUtils.succeed(p, 32)) {
						message(p, "You carefully manage to remove enough rocks to see a book shelf.",
								"You gingerly remove a delicate scroll from the shelf");
						p.message("and place it carefully in your inventory.");
						addItem(p, TATTERED_SCROLL, 1);
					} else {
						message(p, "You acidently knock some rocks and the ceiling starts to cave in.");
						message(p, "Some rocks fall on you.");
						p.damage((int) (getCurrentLevel(p, HITS) * 0.1D + 1));
					}
					p.incExp(AGILITY, 2.5, true);
				}
			} else if(menu == 1) {
				message(p, "You decide to leave the rocks well alone.");
				p.message("The ceiling does look a little unsafe.");
			}
		}
		if(obj.getID() == ROTTEN_GALLOWS) {
			if(command.equalsIgnoreCase("Look")) {
				message(p, "You take a look at the Gallows.",
						"The gallows look pretty eerie.",
						"A grisly sight meets your eyes. A human corpse hangs from the noose.");
				if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == 3) {
					p.message("His hands have been tied behind his back.");
				}
			} else if(command.equalsIgnoreCase("Search")) {
				message(p, "You search the gallows.");
				if(hasItem(p, ZADIMUS_CORPSE) || p.getQuestStage(Constants.Quests.SHILO_VILLAGE) >= 4) {
					p.message("The gallows look pretty eerie. You search but find nothing.");
				} else {
					message(p, "You find a human corpse hanging in the noose.",
							"It looks as if the corpse will be removed easily.",
							"Would you like to remove the corpse from the noose?");
					int menu = showMenu(p,
							"I don't think so it might animate and attack me!",
							"Yes, I may find something else on the corpse");
					if(menu == 0) {
						message(p, "You move away from the corpse quietly and slowly...",
								"...you have an eerie feeling about this!");
						playerTalk(p, null, "** Gulp! **");
					} else if(menu == 1) {
						message(p, "You gently support the frame of the skeleton and lift the skull through the noose.",
								"You find an old sack and place the skeleton in this.",
								"Maybe Trufitus can give you some tips on what to do with it.",
								"You sense that there is a spirit that needs to be put to rest.");
						addItem(p, ZADIMUS_CORPSE, 1);
					}
				}
			}
		}
		if(obj.getID() == CAVE_SACK) {
			if(hasItem(p, CRUMPLED_SCROLL)) {
				p.message("You find nothing in the sacks.");
			} else {
				p.message("You find a tattatered, very ornate scroll.");
				p.message("Which you place carefully in your inventory.");
				addItem(p, CRUMPLED_SCROLL, 1);
			}
		}
		if(obj.getID() == WET_ROCKS) {
			message(p, "You see a huge waterfall blocking your path.",
					"The rocks look quite perilous but you could try scale them.");
			p.message("Or maybe you could use something to float through the waterfall?");
			int m = showMenu(p,
					"Yes, I'll try to climb out",
					"No, thanks, I'll look for another exit.");
			if(m == 0) {
				message(p, "You start searching for handholds in the slippery cave entrance...");
				p.teleport(342, 3684);
				if(ShiloVillageUtils.succeed(p, 32)) {
					message(p, "@red@*** YOU FALL ***",
							"You slip into the water and get washed out through the waterfall!",
							"You're pumelled as the thrashing water throws",
							"you against the rocks...");
					p.teleport(339, 808);
					message(p, "You are washed onto the waterfall river bank");
					p.message("barely alive!");
					p.damage((int) (getCurrentLevel(p, HITS) * 0.2D + 4));
				} else {
					message(p, "You manage to work your way along the slippery wall");
					message(p, "and avoid falling into the water below.");
					p.teleport(344, 808);
					message(p, "You make it out of the cave");
					p.message("and into the warmth of the jungle.");
				}
			} else if(m == 1) {
				message(p, "You decide to have another look around.");
				p.message("And see if you can find a better way to get out.");
			}
		}
		if(obj.getID() == SMASHED_TABLE) {
			// 698
			if(command.equalsIgnoreCase("Examine")) {
				message(p, "This table might be useful...");
				p.message("with some adjustment");
			} else if(command.equalsIgnoreCase("Craft")) {
				message(p, "You may be able to turn this delapidated table into ",
						"something that could help you to get out of this place.",
						"What would you like to try and turn this table into?");
				int sub = showMenu(p,
						"A ladder",
						"A crude raft",
						"A pole vault");
				if(sub == 0) {
					message(p, "Your experience in crafting tells you that");
					p.message("there isn't enough wood to complete this task.");
				} else if(sub == 1) {
					// WATER RAFT SCENE
					p.setBusy(true);
					message(p, "You see that this table already looks very sea worthy",
							"it takes virtually no time at all to help fix it into.");
					message(p, "a crude raft.");
					GameObject RAFT_ONE = new GameObject(Point.location(353, 3669), 698, 0, 0);
					registerObject(RAFT_ONE);
					p.teleport(353, 3669);
					message(p, "You place it carefully on the water!",
							"You board the raft!",
							"You push off!");
					removeObject(RAFT_ONE);

					GameObject RAFT_TWO = new GameObject(Point.location(357, 3673), 698, 0, 0);
					registerObject(RAFT_TWO);
					p.teleport(357, 3673);
					playerTalk(p, null, "Weeeeeeee!");
					sleep(500);
					removeObject(RAFT_TWO);

					GameObject RAFT_THREE = new GameObject(Point.location(356, 3678), 698, 0, 0);
					registerObject(RAFT_THREE);
					p.teleport(356, 3678);
					sleep(1000);
					removeObject(RAFT_THREE);

					GameObject RAFT_FOUR = new GameObject(Point.location(353, 3682), 698, 0, 0);
					registerObject(RAFT_FOUR);
					p.teleport(353, 3682);
					playerTalk(p, null, "Weeeeeeee!");
					sleep(500);
					removeObject(RAFT_FOUR);

					GameObject RAFT_FIVE = new GameObject(Point.location(349, 3685), 698, 0, 0);
					registerObject(RAFT_FIVE);
					p.teleport(349, 3685);
					sleep(500);
					removeObject(RAFT_FIVE);

					GameObject RAFT_SIX = new GameObject(Point.location(345, 3686), 698, 0, 0);
					registerObject(RAFT_SIX);
					p.teleport(345, 3686);
					p.message("You come to a huge waterfall...");
					playerTalk(p, null, "* Oh oh! *");
					sleep(500);
					removeObject(RAFT_SIX);

					GameObject RAFT_FINAL = new GameObject(Point.location(341, 3686), 698, 0, 0);
					registerObject(RAFT_FINAL);
					p.teleport(341, 3686);
					message(p, "...and plough through it!");
					removeObject(RAFT_FINAL);
					p.message("The raft soon breaks up.");
					sleep(500);
					p.teleport(341, 810);
					p.setBusy(false);
				} else if(sub == 2) {
					message(p, "Your experience in crafting tells you that");
					p.message("there isn't enough wood to complete this task.");
				}
			}
		}
		if(obj.getID() == PILE_OF_RUBBLE) {
			message(p, "You can see that there is a narrow gap through into darkness.",
					"You could try to wriggle through and see where it takes you.");
			int menu = showMenu(p,
					"Yes, I'll wriggle through.",
					"No, I'll stay here.");
			if(menu == 0) {
				p.message("You manage to wriggle through the rubble");
				if(obj.getX() == 348 && obj.getY() == 3708) {
					p.teleport(356, 3667);
				} else if(obj.getX() == 357 && obj.getY() == 3668) {
					p.teleport(347, 3709);
				}
			} else if(menu == 1) {
				p.message("You decide to stay where you are");
			}
		}
		if(obj.getID() == BUMPY_DIRT) {
			if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == -1) {
				p.message("The entrance seems to have caved in.");
			} else if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) >= 2) {
				if(p.getCache().hasKey("SV_DIG_BUMP")) {
					message(p, "You see a small fissure in the granite");
					p.message("It's very dark beyond the fissure.");
					ShiloVillageUtils.BUMPY_DIRT_HOLDER(p);
					return;
				}
				if(command.equalsIgnoreCase("Look")) {
					p.message("It looks as if something is buried here.");
				} else if(command.equalsIgnoreCase("Search")) {
					message(p, "It looks as if something is buried here.");
					p.message("It looks quite big, you may need some tools to excavate further.");
				}
			} else {
				p.message("It just looks like some bumpy ground");
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == BUMPY_DIRT && item.getID() == SPADE) {
			return true;
		}
		if(obj.getID() == HILLSIDE_ENTRANCE && item.getID() == 974) {
			return true;
		}
		if(obj.getID() == HILLSIDE_ENTRANCE && item.getID() == 835) {
			return true;
		}
		if(obj.getID() == RASH_EXIT_DOOR && item.getID() == 835) {
			return true;
		}
		if(obj.getID() == TOMB_DOORS && item.getID() == 20) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == TOMB_DOORS && item.getID() == 20) {
			if(!hasItem(p, 20, 3)) {
				p.message("You do not have enough bones for all the recesses.");
			} else {
				removeItem(p, 20, 3);
				message(p, "You fit the bones into the reccesses of the door.",
						"The door seems to change slightly.",
						"Two depictions of skeletal warriors turn their heads towards you.",
						"They are alive!",
						"The Skeletons wrench themselves free of the door.",
						"Stepping out of the door, with grinning teeth they push the huge doors open.");
				replaceObjectDelayed(obj, 800, 497);
				p.teleport(377, 3631);
				if(!p.getCache().hasKey("tomb_door_shilo")) {
					p.getCache().store("tomb_door_shilo", true);
				}
			}
		}
		if(obj.getID() == RASH_EXIT_DOOR && item.getID() == 835) {
			if(!p.getInventory().wielding(852)) {
				message(p, "@red@You feel invisible hands starting to choke you...");
				p.damage(getCurrentLevel(p, HITS) / 2);
			}
			message(p, "You unlock the doors with the key",
					"The doors creak open revealing bright day light.");
			p.message("You walk outside into the warmth of the Jungle heat.");
			p.teleport(350, 782);
			if(p.getCache().hasKey("tomb_door_shilo")) {
				p.getCache().remove("tomb_door_shilo");
			}
		}
		if(obj.getID() == HILLSIDE_ENTRANCE && item.getID() == 835) {
			p.setBusy(true);
			message(p, "You try the key with the lock.");
			message(p, "As soon as you push the key into the lock.");
			displayTeleportBubble(p, 350, 782, true);
			sleep(1000);
			p.message("A shimmering light dances over the doors, before you can blink, the doors creak open.");
			p.teleport(348, 3611);
			sleep(600);
			p.setBusy(false);
			message(p, "You feel a strange force pulling you inside.",
					"The doors close behind you with the sound of crunching bone.",
					"Before you stretches a winding tunnel blocked by an ancient gate.");
			if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == 7) {
				p.updateQuestStage(Constants.Quests.SHILO_VILLAGE, 8);
			}
		}
		if(obj.getID() == HILLSIDE_ENTRANCE && item.getID() == 974) {
			message(p, "You try to use the bone shard on the lock.",
					"Although it isabout the right size,");
			p.message("you find that it just doesn't fit the delicate lock mechanism.");
		}
		if(obj.getID() == BUMPY_DIRT && item.getID() == SPADE) {
			if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) == -1) {
				p.message("The entrance seems to have caved in.");
			}
			else if(p.getQuestStage(Constants.Quests.SHILO_VILLAGE) >= 2) {
				if(!p.getCache().hasKey("SV_DIG_BUMP")) {
					message(p, "You dig a small hole and almost immediately hit granite",
							"You excavate the hole a bit more and see that there is a small fissure");
					p.getCache().store("SV_DIG_BUMP", true);
					ShiloVillageUtils.BUMPY_DIRT_HOLDER(p);
				} else {
					p.message("You have already excavated this area.");
					p.message("Your spade clangs against the granite");
				}
			} else {
				message(p, "You start digging...",
						"But without knowing what you're digging for...");
				p.message("you decide to give up.");
			}
		}
	}

}
