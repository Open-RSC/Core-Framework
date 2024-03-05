package com.openrsc.server.plugins.authentic.skills.agility;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.SceneryId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class AgilityShortcuts implements OpLocTrigger,
	UseLocTrigger {

	private static final int SHORTCUT_FALADOR_HANDHOLD = 693;
	private static final int SHORTCUT_BRIMHAVEN_SWING = 694;
	private static final int SHORTCUT_BRIMHAVEN_BACK_SWING = 695;
	private static final int SHORTCUT_EDGE_DUNGEON_SWING = 684;
	private static final int SHORTCUT_EDGE_DUNGEON_BACK_SWING = 685;
	private static final int SHORTCUT_WEST_COALTRUCKS_LOG = 681;
	private static final int SHORTCUT_EAST_COALTRUCKS_LOG = 680;
	private static final int SHILO_VILLAGE_ROCKS_TO_BRIDGE = 710;
	private static final int SHILO_VILLAGE_BRIDGE_BLOCKADE_JUMP = 691;
	private static final int SHORTCUT_YANILLE_AGILITY_ROPESWING = 628;
	private static final int SHORTCUT_YANILLE_AGILITY_ROPESWING_BACK = 627;
	private static final int SHORTCUT_YANILLE_AGILITY_LEDGE = 614;
	private static final int SHORTCUT_YANILLE_AGILITY_LEDGE_BACK = 615;
	private static final int SHORTCUT_YANILLE_PILE_OF_RUBBLE = 636;
	private static final int SHORTCUT_YANILLE_PILE_OF_RUBBLE_UP = 633;
	private static final int SHORTCUT_YANILLE_PIPE = 656;
	private static final int SHORTCUT_YANILLE_PIPE_BACK = 657;
	private static final int GREW_ISLAND_ROPE_ATTACH = 662;
	private static final int GREW_ISLAND_ROPE_ATTACHED = 663;
	private static final int GREW_ISLAND_SWING_BACK = 664;
	private static final int EAST_KARAMJA_LOG = 692;
	private static final int EAST_KARAMJA_STONES = 701;
	private static final int YANILLE_CLIMBING_ROCKS = 1029;
	private static final int YANILLE_WATCHTOWER_HANDHOLDS = 658;
	private static final int TAVERLY_PIPE = 1236;
	private static final int TAVERLY_PIPE_RETURN = 1237;
	private static final int ENTRANA_RUBBLE = 1286;
	private static final int TAVERLY_STEPPING_STONE = 1287;
	private static final int CATHERBY_STEPPING_STONE = 1288;
	private static final int FALADOR_MEMBERS_EXIT_HANDHOLDS = 1290;
	private static final int KBD_TO_LAVADUNG_STEPPING_STONE = 1291;
	private static final int LAVADUNG_TO_KBD_STEPPING_STONE = 1292;
	private static final int SHILO_TO_NATURE_STEPPING_STONE = 1295;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), SHORTCUT_YANILLE_PIPE,
			SHORTCUT_YANILLE_PIPE_BACK,
			SHORTCUT_YANILLE_PILE_OF_RUBBLE,
			SHORTCUT_YANILLE_PILE_OF_RUBBLE_UP,
			SHORTCUT_YANILLE_AGILITY_LEDGE,
			SHORTCUT_YANILLE_AGILITY_LEDGE_BACK, SHORTCUT_FALADOR_HANDHOLD,
			SHORTCUT_BRIMHAVEN_SWING, SHORTCUT_BRIMHAVEN_BACK_SWING,
			SHORTCUT_EDGE_DUNGEON_SWING, SHORTCUT_EDGE_DUNGEON_BACK_SWING,
			SHORTCUT_WEST_COALTRUCKS_LOG, SHORTCUT_EAST_COALTRUCKS_LOG,
			SHORTCUT_YANILLE_AGILITY_ROPESWING,
			SHORTCUT_YANILLE_AGILITY_ROPESWING_BACK,
			GREW_ISLAND_ROPE_ATTACHED,
			GREW_ISLAND_SWING_BACK,
			EAST_KARAMJA_LOG,
			EAST_KARAMJA_STONES,
			YANILLE_CLIMBING_ROCKS,
			YANILLE_WATCHTOWER_HANDHOLDS,
			SHILO_VILLAGE_ROCKS_TO_BRIDGE,
			SHILO_VILLAGE_BRIDGE_BLOCKADE_JUMP,
			TAVERLY_PIPE,
			TAVERLY_PIPE_RETURN,
			ENTRANA_RUBBLE,
			TAVERLY_STEPPING_STONE,
			CATHERBY_STEPPING_STONE,
			FALADOR_MEMBERS_EXIT_HANDHOLDS,
			KBD_TO_LAVADUNG_STEPPING_STONE, LAVADUNG_TO_KBD_STEPPING_STONE,
			SHILO_TO_NATURE_STEPPING_STONE
		);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		int success = 0, damage = 0;
		switch (obj.getID()) {
			case SHILO_VILLAGE_BRIDGE_BLOCKADE_JUMP:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 32) {
					player.message("You need an agility level of 32 to climb the rocks");
					return;
				}
				mes("The bridge beyond this fence looks very unsafe.");
				delay(3);
				mes("However, you could try to negotiate it if you're feeling very agile.");
				delay(3);
				player.message("Would you like to try?");
				int jumpMenu = multi(player,
					"No thanks! It looks far too dangerous!",
					"Yes, I'm totally brave and quite agile!");
				if (jumpMenu == 0) {
					mes("You decide that common sense is the better part of valour.");
					delay(3);
					mes("And stop yourself from being hurled to what must be an ");
					delay(3);
					player.message("inevitable death.");
				} else if (jumpMenu == 1) {
					mes("You prepare to negotiate the bridge fence...");
					delay(3);
					mes("You run and jump...");
					delay(3);
					if (succeed(player, 32)) {
						player.message("...and land perfectly on the other side!");
						if (player.getX() >= 460) { // back
							player.teleport(458, 828);
						} else {
							player.teleport(460, 828);
						}
					} else {
						player.message("...slip and fall incompetently into the river below!");
						player.teleport(458, 832);
						say(player, null, "* Ahhhhhhhhhh! *");
						player.damage((getCurrentLevel(player, Skill.HITS.id()) / 10));
						delay();
						player.teleport(458, 836);
						player.damage((getCurrentLevel(player, Skill.HITS.id()) / 10));
						delay(2);
						say(player, null, "* Gulp! *");
						delay(3);
						player.teleport(459, 841);
						say(player, null, "* Gulp! *");
						delay(2);
						player.message("You just manage to drag your pitiful frame onto the river bank.");
						say(player, null, "* Gasp! *");
						player.damage((getCurrentLevel(player, Skill.HITS.id()) / 10));
						delay(2);
						player.message("Though you nearly drowned in the river!");
					}
				}
				break;
			case SHILO_VILLAGE_ROCKS_TO_BRIDGE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 32) {
					player.message("You need an agility level of 32 to climb the rocks");
					return;
				}
				mes("These rocks look quite dangerous to climb.");
				delay(3);
				mes("But you may be able to scale them.");
				delay(3);
				player.message("Would you like to try?");
				int menu = multi(player,
					"Yes, I can easily climb this!",
					"Nope, I'm sure I'll probably fall!");
				if (menu == 0) {
					if (succeed(player, 32)) {
						mes("You manage to climb the rocks succesfully and pick");
						delay(3);
						if (obj.getX() == 450) {
							player.message("a route though the trecherous embankment to the top.");
							player.teleport(452, 829);
						} else {
							player.message("a route though the trecherous embankment to the bottom.");
							player.teleport(449, 828);
						}
					} else {
						player.teleport(450, 828);
						mes("You fall and hurt yourself.");
						delay(3);
						player.damage((getCurrentLevel(player, Skill.HITS.id()) / 10));
						delay();
						player.teleport(449, 828);
					}
				} else if (menu == 1) {
					player.message("You decide not to climb the rocks.");
				}
				break;
			case SHORTCUT_FALADOR_HANDHOLD:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 5) {
					player.message("You need an agility level of 5 to climb the wall");
					return;
				}
				player.message("You climb over the wall");
				teleport(player, 338, 555);
				player.incExp(Skill.AGILITY.id(), 50, true);
				break;
			case SHORTCUT_BRIMHAVEN_SWING:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 10) {
					player.message("You need an agility level of 10 to attempt to swing on this vine");
					return;
				}
				player.message("You grab the vine and try and swing across");
				delay(2);
				teleport(player, 511, 669);
				player.message("You skillfully swing across the stream");
				say(player, null, "Aaaaahahah");
				player.incExp(Skill.AGILITY.id(), 20, true);
				break;
			case SHORTCUT_BRIMHAVEN_BACK_SWING:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 10) {
					player.message("You need an agility level of 10 to attempt to swing on this vine");
					return;
				}
				player.message("You grab the vine and try and swing across");
				delay(2);
				teleport(player, 508, 668);
				player.message("You skillfully swing across the stream");
				say(player, null, "Aaaaahahah");
				player.incExp(Skill.AGILITY.id(), 20, true);
				break;
			case SHORTCUT_EDGE_DUNGEON_SWING:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 15) {
					player.message("You need an agility level of 15 to attempt to swing on this rope");
					return;
				}
				delay(2);
				teleport(player, 207, 3221);
				player.message("You skillfully swing across the hole");
				player.incExp(Skill.AGILITY.id(), 40, true);
				break;
			case SHORTCUT_EDGE_DUNGEON_BACK_SWING:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 15) {
					player.message("You need an agility level of 15 to attempt to swing on this rope");
					return;
				}
				delay(2);
				teleport(player, 206, 3225);
				player.message("You skillfully swing across the hole");
				player.incExp(Skill.AGILITY.id(), 40, true);
				break;
			case SHORTCUT_WEST_COALTRUCKS_LOG:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 20) {
					player.message("You need an agility level of 20 to attempt balancing along this log");
					return;
				}
				player.message("You stand on the slippery log");
				for (int x = 595; x >= 592; x--) {
					teleport(player, x, 458);
					delay();
				}
				player.message("and you walk across");
				player.incExp(Skill.AGILITY.id(), 34, true);
				break;
			case SHORTCUT_EAST_COALTRUCKS_LOG:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 20) {
					player.message("You need an agility level of 20 to attempt balancing along this log");
					return;
				}
				player.message("You stand on the slippery log");
				for (int x = 595; x <= 598; x++) {
					teleport(player, x, 458);
					delay();
				}
				player.message("and you walk across");
				player.incExp(Skill.AGILITY.id(), 34, true);
				break;
			// CONTINUE SHORTCUTS.
			case SHORTCUT_YANILLE_AGILITY_ROPESWING:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 57) {
					player.message("You need an agility level of 57 to attempt to swing on this rope");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to swing on the rope");
						return;
					}
				}
				player.message("You grab the rope and try and swing across");
				delay(2);
				if (!succeed(player, 57, 77)) {
					mes("You miss the opposite side and fall to the level below");
					teleport(player, 596, 3534);
					return;
				}
				teleport(player, 596, 3581);
				player.message("You skillfully swing across the hole");
				player.incExp(Skill.AGILITY.id(), 110, true);
				break;
			case SHORTCUT_YANILLE_AGILITY_ROPESWING_BACK:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 57) {
					player.message("You need an agility level of 57 to attempt to swing on this rope");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to swing on the rope");
						return;
					}
				}
				player.message("You grab the rope and try and swing across");
				delay(2);
				if (!succeed(player, 57, 77)) {
					mes("You miss the opposite side and fall to the level below");
					teleport(player, 598, 3536);
					return;
				}
				teleport(player, 598, 3585);
				player.message("You skillfully swing across the hole");
				player.incExp(Skill.AGILITY.id(), 110, true);
				break;

			case SHORTCUT_YANILLE_AGILITY_LEDGE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 40) {
					player.message("You need an agility level of 40 to attempt balancing along this log");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to balance on the ledge");
						return;
					}
				}
				player.message("You put your foot on the ledge and try to edge across");
				delay(3);
				if (!succeed(player, 40, 65)) {
					mes("you lose your footing and fall to the level below");
					teleport(player, 603, 3520);
					player.damage((int)(getCurrentLevel(player, Skill.HITS.id()) * 0.2D)); // unknown if should depend on current hits (very likely) was taken at 47 hits
					return;
				}
				teleport(player, 601, 3563);
				player.message("You skillfully balance across the hole");
				player.incExp(Skill.AGILITY.id(), 90, true);
				break;
			case SHORTCUT_YANILLE_AGILITY_LEDGE_BACK:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 40) {
					player.message("You need an agility level of 40 to attempt balancing along this log");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to balance on the ledge");
						return;
					}
				}
				player.message("You put your foot on the ledge and try to edge across");
				delay(3);
				if (!succeed(player, 40, 65)) {
					mes("you lose your footing and fall to the level below");
					teleport(player, 603, 3520);
					player.damage((int)(getCurrentLevel(player, Skill.HITS.id()) * 0.2D)); // unknown if should depend on current hits (very likely) was taken at 47 hits
					return;
				}
				teleport(player, 601, 3557);
				player.message("You skillfully balance across the hole");
				player.incExp(Skill.AGILITY.id(), 90, true);
				break;

			case SHORTCUT_YANILLE_PILE_OF_RUBBLE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 67) {
					player.message("You need an agility level of 67 to attempt to climb down the rubble");
					return;
				}
				teleport(player, 580, 3525);
				player.message("You climb down the pile of rubble");
				break;
			case SHORTCUT_YANILLE_PILE_OF_RUBBLE_UP:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 67) {
					player.message("You need an agility level of 67 to attempt to climb up the rubble");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to climb up the rubble");
						return;
					}
				}
				teleport(player, 582, 3573);
				player.message("You climb up the pile of rubble");
				player.incExp(Skill.AGILITY.id(), 54, true);
				break;

			case SHORTCUT_YANILLE_PIPE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 49) {
					player.message("You need an agility level of 49 to attempt to squeeze through the pipe");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to squeeze through the pipe");
						return;
					}
				}
				player.message("You squeeze through the pipe");
				delay(2);
				teleport(player, 608, 3568);
				player.incExp(Skill.AGILITY.id(), 30, true);
				break;
			case SHORTCUT_YANILLE_PIPE_BACK:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 49) {
					player.message("You need an agility level of 49 to attempt to squeeze through the pipe");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to squeeze through the pipe");
						return;
					}
				}
				player.message("You squeeze through the pipe");
				delay(2);
				teleport(player, 605, 3568);
				player.incExp(Skill.AGILITY.id(), 30, true);
				break;
			case GREW_ISLAND_ROPE_ATTACHED:
				if (player.getX() == 664 && player.getY() == 755) {
					player.message("You can't reach the tree from here"); // not known authentic message
					return;
				}
				if (player.getFatigue() >= player.MAX_FATIGUE) {
					player.message("You are too tired to swing on the rope");
					return;
				}
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 30) {
					player.message("You need an agility level of 30 to attempt to swing across the stream");
					return;
				}
				player.message("You grab the rope and try and swing across");
				delay(2);
				teleport(player, 664, 755);
				player.message("You skillfully swing across the stream");
				player.incExp(Skill.AGILITY.id(), 50, true);
				break;
			case GREW_ISLAND_SWING_BACK:
				player.message("You grab the rope and try and swing across");
				delay(2);
				teleport(player, 666, 755);
				player.message("You skillfully swing across the stream");
				player.incExp(Skill.AGILITY.id(), 50, true);
				break;
			case EAST_KARAMJA_LOG:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 32) {
					player.message("You need an agility level of 32 to attempt balancing along this log");
					return;
				}
				player.message("You attempt to walk over the the slippery log..");
				delay(3);
				if (!succeed(player, 32)) {
					teleport(player, 368, 781);
					delay();
					player.message("@red@You fall into the stream!");
					player.message("You lose some health");
					teleport(player, 370, 776);
					player.damage(1);
					return;
				}
				if (player.getX() <= 367) {
					teleport(player, 368, 781);
					delay();
					teleport(player, 370, 781);
				} else {
					teleport(player, 368, 781);
					delay();
					teleport(player, 366, 781);
				}
				player.message("...and make it without any problems!");
				player.incExp(Skill.AGILITY.id(), 10, true);
				break;
			case EAST_KARAMJA_STONES:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 32) {
					player.message("You need an agility level of 32 to step on these stones");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too fatigued to continue.");
						return;
					}
				}
				player.message("You jump onto the rock");
				if (player.getY() <= 805) {
					teleport(player, 347, 806);
					delay();
					if (!succeed(player, 32)) {
						delay(2);
						teleport(player, 341, 809);
						player.message("@red@!!! You Fall !!!");
						mes("You get washed up on the other side of the river...");
						delay(3);
						mes("After being nearly half drowned");
						delay(3);
						player.damage((int) (player.getSkills().getLevel(Skill.HITS.id()) / 4) + 2);
						return;
					}
					teleport(player, 346, 808);
				} else {
					teleport(player, 346, 807);
					delay();
					if (!succeed(player, 32)) {
						delay(2);
						teleport(player, 341, 805);
						player.message("@red@!!! You Fall !!!");
						mes("You get washed up on the other side of the river...");
						delay(3);
						mes("After being nearly half drowned");
						delay(3);
						player.damage((int) (player.getSkills().getLevel(Skill.HITS.id()) / 4) + 2);
						return;
					}
					teleport(player, 347, 805);
				}
				player.message("And cross the water without problems.");
				player.incExp(Skill.AGILITY.id(), 10, true);
				break;
			case YANILLE_CLIMBING_ROCKS:
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to climb up the wall");
						return;
					}
				}
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 15) {
					player.message("You need an agility level of 15 to climb the wall");
					return;
				}
				player.message("You climb over the wall");
				teleport(player, 624, 741);
				player.incExp(Skill.AGILITY.id(), 40, true);
				break;
			case YANILLE_WATCHTOWER_HANDHOLDS:
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to climb up the wall");
						return;
					}
				}
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 18) {
					player.message("You need an agility level of 18 to climb the wall");
					return;
				}
				player.message("You climb up the wall");
				player.teleport(637, 1680);
				player.message("And climb in through the window");
				player.incExp(Skill.AGILITY.id(), 50, true);
				break;

			case TAVERLY_PIPE_RETURN:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 70) {
					player.message("You need an agility level of 70 to attempt to squeeze through the pipe");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to squeeze through the pipe");
						return;
					}
				}
				player.message("You squeeze through the pipe");
				teleport(player, 372, 3352);
				player.incExp(Skill.AGILITY.id(), 30, true);
				break;

			case TAVERLY_PIPE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 70) {
					player.message("You need an agility level of 70 to attempt to squeeze through the pipe");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to squeeze through the pipe");
						return;
					}
				}
				player.message("You squeeze through the pipe");
				teleport(player, 375, 3352);
				player.incExp(Skill.AGILITY.id(), 30, true);
				break;
			case ENTRANA_RUBBLE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 55) {
					player.message("You need an agility level of 55 to climb the rubble");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to climb the rubble");
						return;
					}
				}
				delay();
				if (player.getLocation().getY() < 550) {
					teleport(player, 434, 551);
					player.incExp(Skill.AGILITY.id(), 15, true);
				} else {
					teleport(player, 434, 549);
					player.incExp(Skill.AGILITY.id(), 15, true);
				}
				break;
			case TAVERLY_STEPPING_STONE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 50) {
					player.message("You need an agility level of 50 to use this shortcut");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to jump to the stone");
						return;
					}
				}
				player.teleport(395, 502);
				delay(1);
				player.face(397, 502);
				player.message("You sure your footing...");
				delay(3);
				teleport(player, 396, 502);
				player.message("and attempt to cross the stones...");
				delay(4);
				success = DataConversions.random(1, 100);
				if (success > 10 || wearingSkillcape(player)) {
					teleport(player, 397, 502);
					player.message("you make it to the shore of Catherby");
					player.incExp(Skill.AGILITY.id(), 60, true);
				} else {
					player.message("and fall into the water!");
					damage = getMaxLevel(player, Skill.HITS.id()) / 5;

					//If they are going to die from the hit, put their gear on the Taverly side
					if (damage >= getCurrentLevel(player, Skill.HITS.id()))
						player.teleport(394, 502);
					else
						player.teleport(388, 522);

					player.damage(damage);
				}
				break;
			case CATHERBY_STEPPING_STONE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 50) {
					player.message("You need an agility level of 50 to use this shortcut");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to jump to the stone");
						return;
					}
				}
				player.teleport(397, 502);
				delay(1);
				player.face(395, 502);
				player.message("You sure your footing...");
				delay(3);
				teleport(player, 396, 502);
				player.message("and attempt to cross the stones...");
				delay(4);
				success = DataConversions.random(1, 100);
				if (success > 10 || wearingSkillcape(player)) {
					teleport(player, 395, 502);
					player.message("you make it to the shore of Taverly");
					player.incExp(Skill.AGILITY.id(), 60, true);
				} else {
					player.message("and fall into the water!");
					damage = getMaxLevel(player, Skill.HITS.id()) / 5;

					//If they are going to die from the hit, put their gear on the Catherby side
					if (damage >= getCurrentLevel(player, Skill.HITS.id()))
						player.teleport(397, 501);
					else
						player.teleport(388, 522);
					player.damage(damage);
				}
				break;
			case FALADOR_MEMBERS_EXIT_HANDHOLDS:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 40) {
					player.message("You need an agility level of 40 to climb the wall");
					return;
				}
				player.message("You climb over the wall");
				teleport(player, 339, 544);
				player.incExp(Skill.AGILITY.id(), 80, true);
				break;
			case KBD_TO_LAVADUNG_STEPPING_STONE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 67) {
					player.message("You need an agility level of 67 to jump to the stone");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to jump to the stone");
						return;
					}
				}
				player.teleport(280,3015);
				player.face(274, 3015);
				player.message("You focus on not slipping...");
				delay(4);
				if (Formulae.calcProductionSuccessfulLegacy(19, getCurrentLevel(player, Skill.AGILITY.id()) - 48, false, 58, 26)
					|| wearingSkillcape(player)) {
					player.teleport(278, 3015);
					delay(3);
					player.teleport(276, 3015);
					delay(3);
					player.teleport(274, 3015);
					delay(3);
					player.teleport(272, 3015);
					player.face(272, 3013);
					delay(3);
					player.teleport(272,3012);
					player.message("and skillfully cross the lava");
					player.incExp(Skill.AGILITY.id(), 160, true);
				} else {
					player.message("but fall into the lava");
					int lavaDamage = (int) Math.round((player.getSkills().getLevel(Skill.HITS.id())) * 0.21D);
					player.teleport(281, 3016);
					player.damage(lavaDamage);
				}

				break;
			case LAVADUNG_TO_KBD_STEPPING_STONE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 67) {
					player.message("You need an agility level of 67 to jump to the stone");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to jump to the stone");
						return;
					}
				}
				player.teleport(272,3013);
				player.face(272, 3015);
				player.message("You focus on not slipping...");
				delay(4);
				if (Formulae.calcProductionSuccessfulLegacy(19, getCurrentLevel(player, Skill.AGILITY.id()) - 48, false, 58, 26)
					|| wearingSkillcape(player)) {
					player.teleport(272, 3015);
					player.face(280, 3015);
					delay(3);
					player.teleport(274, 3015);
					delay(3);
					player.teleport(276, 3015);
					delay(3);
					player.teleport(278, 3015);
					delay(3);
					player.teleport(281, 3015);
					player.message("and skillfully cross the lava");
					player.incExp(Skill.AGILITY.id(), 160, true);
				} else {
					player.message("but fall into the lava");
					int lavaDamage = (int) Math.round((player.getSkills().getLevel(Skill.HITS.id())) * 0.21D);
					player.teleport(271, 3012);
					player.damage(lavaDamage);
				}

				break;
			case SHILO_TO_NATURE_STEPPING_STONE:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 85) {
					player.message("You need an agility level of 85 to jump to the stone");
					return;
				}
				if (config().WANT_FATIGUE) {
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to jump to the stone");
						return;
					}
				}

				// Check if we're going to fail
				boolean cross = succeed(player, 85);
				damage = (int) Math.round((player.getSkills().getLevel(Skill.HITS.id())) * 0.20D);
				Point successPoint = player.getY() > 830 ? new Point(369, 829) : new Point(367, 831);
				Point failPoint = player.getY() > 830 ? new Point(383, 836) : new Point(383, 833);

				mes("You jump out onto the stone");
				player.teleport(368, 830);
				delay(3);
				if (cross) {
					mes("You successfully cross the river");
					player.teleport(successPoint.getX(), successPoint.getY());
					player.incExp(Skill.AGILITY.id(), 80, true);
				} else {
					mes("You slip and fall into the river");
					player.damage(damage);
					player.teleport(failPoint.getX(), failPoint.getY());
				}
				break;
		}
	}

	boolean wearingSkillcape(final Player player) {
		return player.getCarriedItems().getEquipment().hasEquipped(ItemId.AGILITY_CAPE.id());
	}

	boolean succeed(Player player, int req) {
		if (wearingSkillcape(player)) return true;
		return Formulae.calcProductionSuccessfulLegacy(req, getCurrentLevel(player, Skill.AGILITY.id()), false, req + 30);
	}

	boolean succeed(Player player, int req, int lvlStopFail) {
		if (wearingSkillcape(player)) return true;
		return Formulae.calcProductionSuccessfulLegacy(req, getCurrentLevel(player, Skill.AGILITY.id()), true, lvlStopFail);
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == GREW_ISLAND_ROPE_ATTACH && item.getCatalogId() == ItemId.ROPE.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == GREW_ISLAND_ROPE_ATTACH && item.getCatalogId() == ItemId.ROPE.id()) {
			player.message("you tie the rope to the tree");
			player.getCarriedItems().remove(new Item(ItemId.ROPE.id()));
			player.getWorld().replaceGameObject(obj,
				new GameObject(player.getWorld(), obj.getLocation(), 663, obj.getDirection(), obj
					.getType()));
			player.getWorld().delayedSpawnObject(obj.getLoc(), 60000);
		}
	}

	// HERRING SPAWN I CHEST ROOM SINISTER CHEST = 362, 614, 3564
}
