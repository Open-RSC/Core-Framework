package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.custom.UndergroundPassMessages;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.authentic.quests.members.undergroundpass.npcs.UndergroundPassKoftik;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassObstaclesMap1 implements OpLocTrigger {

	/**
	 * Quest Objects
	 **/
	public static int UNDERGROUND_CAVE = 725;
	public static int CRUMBLED_ROCK = 728;
	public static int FIRST_SWAMP = 754;
	public static int PILE_OF_MUD_MAP_LEVEL_1 = 767;
	public static int LEVER = 733;
	public static int BLESSED_SPIDER_SWAMP_OBJ = 795;
	public static int CLEAR_ROCKS = 772;
	public static int DROP_DOWN_LEDGE = 812;
	public static int CLEAR_ROCKS_INIT_WEST = 796;
	public static int CLEAR_ROCKS_INIT_EAST = 797;

	/**
	 * Main floor of the cave rocks
	 **/
	public static int[] MAIN_ROCKS = {731, 737, 738, 739, 740, 741, 742, 743, 744, 745, 746, 747, 748, 749};

	/**
	 * Main floor ledges
	 **/
	public static int[] MAIN_LEDGE = {732, 750, 751, 752, 753};

	/**
	 * From failing the swamp on main floor - rock riddle to get back to main floor.
	 **/
	public static int[] FAIL_SWAMP_ROCKS = {756, 757, 758, 759, 760, 762, 763, 764, 765, 766};

	/**
	 * Read rocks
	 **/
	public static int[] READ_ROCKS = {832, 833, 834, 835, 923, 922, 881};

	/**
	 * Spear rocks
	 **/
	public static int[] SPEAR_ROCKS = {806, 807, 808, 809, 810, 811, 882, 883};

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String cmd) {
		return obj.getID() == UNDERGROUND_CAVE || obj.getID() == CRUMBLED_ROCK
				|| inArray(obj.getID(), READ_ROCKS) || inArray(obj.getID(), MAIN_ROCKS)
				|| inArray(obj.getID(), MAIN_LEDGE) || obj.getID() == FIRST_SWAMP
				|| inArray(obj.getID(), FAIL_SWAMP_ROCKS) || obj.getID() == PILE_OF_MUD_MAP_LEVEL_1
				|| obj.getID() == LEVER || obj.getID() == BLESSED_SPIDER_SWAMP_OBJ
				|| obj.getID() == CLEAR_ROCKS || obj.getID() == CLEAR_ROCKS_INIT_WEST || obj.getID() == CLEAR_ROCKS_INIT_EAST
				|| obj.getID() == DROP_DOWN_LEDGE || inArray(obj.getID(), SPEAR_ROCKS);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String cmd) {
		if (obj.getID() == UNDERGROUND_CAVE) {
			switch (player.getQuestStage(Quests.UNDERGROUND_PASS)) {
				case 0:
					if (player.getQuestStage(Quests.BIOHAZARD) != -1) {
						player.message("You must first complete the biohazard quest...");
						player.message("...before you can enter");
					}
					else {
						player.message("you must talk to king lathas before you can enter");
					}
					break;
				case 1:
					Npc koftik = ifnearvisnpc(player, NpcId.KOFTIK_ARDOUGNE.id(), 10);
					if (koftik != null) {
						UndergroundPassKoftik.koftikEnterCaveDialogue(player, koftik);
					}
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case -1:
					mes("you cautiously enter the cave");
					delay(3);
					player.teleport(673, 3420);
					break;
			}
		}
		else if (obj.getID() == CRUMBLED_ROCK) {
			mes("you climb the rock pile");
			delay(3);
			player.teleport(713, 581);
		}
		else if (inArray(obj.getID(), READ_ROCKS)) {
			mes("the writing seems to have been scracthed...");
			delay(3);
			mes("..into the rock with bare hands, it reads..");
			delay(3);
			if (obj.getID() == 832) {
				ActionSender.sendBox(player, "@red@All those who thirst for knowledge%@red@Bow down to the lord.% %@red@All you that crave eternal life%@red@Come and meet your God.% %@red@For no man nor beast can cast a spell%@red@Against the wake of eternal hell.", true);
			} else if (obj.getID() == 833) {
				ActionSender.sendBox(player, "@red@Most men do live in fear of death%@red@That it might steal their soul.% %@red@Some work and pray to shield their life%@red@From the ravages of the cold.% %@red@But only those who embrace the end%@red@Can truly make their life extend.% %@red@And when all hope begins to fade% %@red@look above and use nature as your aid", true);
			} else if (obj.getID() == 834) {
				ActionSender.sendBox(player, "@red@And now our God has given us%@red@One who is from our own.% %@red@A saviour who once sat upon%@red@His father's glorious thrown.% %@red@It is in your name that we will lead the attack Iban%@red@son of Zamorak!", true);
			} else if (obj.getID() == 835) {
				ActionSender.sendBox(player, "@red@Here lies the sacred font%@red@Where the great Iban will bless all his disciples%@red@in the name of evil.% %@red@Here the forces of darkness are so concentrated they rise%@red@when they detect any positive force close by", true);
			} else if (obj.getID() == 923) {
				ActionSender.sendBox(player, "@red@Ibans Shadow% %@red@Then came the hard part: recreating the parts of a man%@red@that cannot be seen or touched: those intangible things%@red@that are life itself. Using all the mystical force that I could%@red@muster, I performed the ancient ritual of Incantia, a spell%@red@so powerful that it nearly stole the life from my frail and%@red@withered body. Opening my eyes again, I saw the three%@red@demons that had been summoned. Standing in a triangle,%@red@their energy was focused on the doll. These demons%@red@would be the keepers of Iban's shadow. Black as night,%@red@their shared spirit would follow his undead body like an%@red@angel of death.", true);
			} else if (obj.getID() == 922) {
				ActionSender.sendBox(player, "% %@red@Crumbling some of the dove's bones onto the doll, I cast%@red@my mind's eye onto Iban's body. My ritual was complete,%@red@soon he would be coming to life. I, Kardia, had resurrected%@red@the legendary Iban, the most powerful evil being ever to%@red@take human form. And I alone knew that the same%@red@process that I had used to create him, was also capable%@red@of destroying him.% %@red@But now I was exhausted. As I closed my eyes to sleep, I%@red@was settled by a strange feeling of contentment%@red@anticipation of the evil that Iban would soon unleash.", true);
			} else if (obj.getID() == 881) {
				ActionSender.sendBox(player, "@red@Leave this battered corpse be% %@red@For now he lives as spirit alone% %@red@Let his flesh rest and become one with the earth% %@red@As it is the soil that shall rise to protect him% %@red@Only as flesh becomes dust, as wood becomes ash...% %@red@..will Iban's corpse embrace nature and finally rest", true);
			}
		}
		else if (inArray(obj.getID(), MAIN_ROCKS)) {
			doRock(obj, player, (int) (getCurrentLevel(player, Skill.HITS.id()) / 42) + 1, true, -1);
		}
		else if (obj.getID() == FIRST_SWAMP) {
			mes("you try to cross but you're unable to");
			delay(3);
			mes("the swamp seems to cling to your legs");
			delay(3);
			player.message("you slowly feel yourself being dragged below");
			say(player, null, "gulp!");
			player.teleport(674, 3462);
			say(player, null, "aargh");
			player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 42) + 1);
			delay(3);
			player.teleport(677, 3462);
			delay();
			player.teleport(680, 3465);
			delay();
			player.teleport(682, 3462);
			delay();
			player.teleport(683, 3465);
			delay();
			player.teleport(685, 3464);
			delay();
			player.teleport(687, 3462);
			delay();
			say(player, null, "aargh");
			player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 42) + 1);
			player.teleport(690, 3461);
			mes("you tumble deep into the cravass");
			delay(3);
			mes("and land battered and bruised at the base");
			delay(3);
		}
		else if (inArray(obj.getID(), FAIL_SWAMP_ROCKS)) {
			doRock(obj, player, (int) (getCurrentLevel(player, Skill.HITS.id()) / 42) + 1, true, -1);
		}
		else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_1) {
			mes("you climb up the mud pile");
			delay(3);
			player.teleport(685, 3420);
			mes("it leads into darkness, the stench is almost unbearable");
			delay(3);
			mes("you surface by the swamp, covered in muck");
			delay(3);
		}
		else if (inArray(obj.getID(), MAIN_LEDGE)) {
			doLedge(obj, player, (int) (getCurrentLevel(player, Skill.HITS.id()) / 42) + 1);
		}
		else if (obj.getID() == LEVER) {
			mes("you pull back on the old lever");
			delay(3);
			mes("the bridge slowly lowers");
			delay(3);
			GameObject bridge_open = new GameObject(obj.getWorld(), Point.location(704, 3417), 727, 2, 0);
			GameObject bridge_closed = new GameObject(obj.getWorld(), Point.location(704, 3417), 726, 2, 0);
			bridge_open.getWorld().registerGameObject(bridge_open);
			bridge_closed.getWorld().delayedSpawnObject(bridge_closed.getLoc(), 10000);
			player.teleport(709, 3420);
			delay();
			player.teleport(706, 3420);
			delay();
			player.teleport(703, 3420);
			player.message("you cross the bridge");
		}
		else if (obj.getID() == BLESSED_SPIDER_SWAMP_OBJ) {
			mes("you step in rancid swamp");
			delay(3);
			mes("it clings to your feet, you cannot cross");
			delay(3);
		}
		else if (obj.getID() == CLEAR_ROCKS || obj.getID() == CLEAR_ROCKS_INIT_WEST || obj.getID() == CLEAR_ROCKS_INIT_EAST) {
			if (player.getX() == 695 && (player.getY() == 3436 || player.getY() == 3435)) {
				player.teleport(695, 3435);
				return;
			}
			if (obj.getID() == CLEAR_ROCKS_INIT_WEST && player.getX() > obj.getX() && player.getY() == obj.getY()) {
				player.teleport(obj.getX() + 1, obj.getY());
			}
			else if (obj.getID() == CLEAR_ROCKS_INIT_EAST && player.getX() < obj.getX() && player.getY() == obj.getY()) {
				player.teleport(obj.getX() + 1, obj.getY());
			}
			mes("you move the rocks from your path");
			delay(3);
			if (obj.getID() != CLEAR_ROCKS) {
				GameObject newRocks = new GameObject(player.getWorld(), obj.getLocation(), CLEAR_ROCKS, obj.getDirection(), obj.getType());
				changeloc(obj, newRocks);
			}
			player.message("you hear a strange mechanical sound");
			GameObject checkObj = player.getViewArea().getGameObject(CLEAR_ROCKS, obj.getX(), obj.getY());
			changeloc(checkObj, 3000, CLEAR_ROCKS + 1);
			player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) * 0.2D));
			say(player, null, "aaarrghhh");
			mes("You've triggered a trap");
			delay(3);
		}
		else if (inArray(obj.getID(), SPEAR_ROCKS)) {
			if (cmd.equalsIgnoreCase("step over")) {
				mes("you step over the rock");
				delay(3);
				player.message("you feel a thread tug at your boot");
				player.message("it's a trap");
				player.teleport(obj.getX(), obj.getY());
				obj.getWorld().replaceGameObject(obj,
					new GameObject(obj.getWorld(), obj.getLocation(), 805, obj.getDirection(), obj
						.getType()));
				obj.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
				player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 6) + 1);
				say(player, null, "aaarghh");
			} else {
				mes("you search the rock");
				delay(3);
				mes("you find a trip wire");
				delay(3);
				player.message("do you wish to disarm the trap?");
				int menu = multi(player, "yes, i'll have a go", "no chance");
				if (menu == 0) {
					mes("you carefully try and diconnect the trip wire");
					delay(3);
					if (succeed(player, 1)) {
						player.message("you manage to delay the trap..");
						player.message("...long enough to cross the rocks");
						if (obj.getX() == player.getX() + 1)
							player.teleport(obj.getX() + 1, obj.getY());
						else
							player.teleport(obj.getX() - 1, obj.getY());
					}
					else {
						player.message("but the trap activates");
						player.teleport(obj.getX(), obj.getY());
						obj.getWorld().replaceGameObject(obj,
							new GameObject(obj.getWorld(), obj.getLocation(), 805, obj.getDirection(), obj
								.getType()));
						obj.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
						player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 6) + 1);
						say(player, null, "aaarghh");
					}

				} else if (menu == 1) {
					player.message("you back away from the trap");
				}
			}
		}
		else if (obj.getID() == DROP_DOWN_LEDGE) {
			player.message("you drop down to the cave floor");
			player.teleport(706, 3439);
		}
	}

	boolean succeed(Player player, int req) {
		int level_difference = getCurrentLevel(player, Skill.THIEVING.id()) - req;
		int percent = random(1, 100);

		if (level_difference < 0)
			return true;
		if (level_difference >= 15)
			level_difference = 70;
		if (level_difference >= 20)
			level_difference = 80;
		else
			level_difference = 40 + level_difference;

		return percent <= level_difference;
	}

	public static void doLedge(final GameObject object, final Player player, int damage) {
		player.message("you climb the ledge");
		boolean failLedge = !Formulae.calcProductionSuccessfulLegacy(1, player.getSkills().getLevel(Skill.AGILITY.id()), false, 71);
		if (object != null && !failLedge) {
			if (object.getDirection() == 2 || object.getDirection() == 6) {
				if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) { // X
					if (object.getID() == 753) {
						player.message("and drop down to the cave floor");
						teleport(player, object.getX() - 2, object.getY());
					} else {
						player.message("and drop down to the cave floor");
						teleport(player, object.getX() - 1, object.getY());
					}
				} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) { // Y
					if (object.getID() == 753) {
						player.message("and drop down to the cave floor");
						teleport(player, object.getX() + 2, object.getY());
					} else {
						player.message("and drop down to the cave floor");
						teleport(player, object.getX() + 1, object.getY());
					}
				}
			}
			if (object.getDirection() == 4 || object.getDirection() == 0) {
				if (object.getX() == player.getX() && object.getY() == player.getY() + 1) { // X
					teleport(player, object.getX(), object.getY() + 1);
					player.message("and drop down to the cave floor");
				} else if (object.getX() == player.getX() && object.getY() == player.getY() - 1) { // Y
					teleport(player, object.getX(), object.getY() - 1);
				}
			}
		} else {
			player.message("but you slip");
			player.damage(damage);
			say(player, null, "aargh");
		}
	}

	public static void doRock(final GameObject object, final Player player, int damage, boolean eventMessage,
							  int spikeLocation) {
		player.message("you climb onto the rock");
		boolean failRock = !Formulae.calcProductionSuccessfulLegacy(1, player.getSkills().getLevel(Skill.AGILITY.id()), false, 71);
		if (object != null && !failRock) {
			if (object.getDirection() == 1 || object.getDirection() == 2 || object.getDirection() == 4
				|| object.getDirection() == 3) {
				if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) { // X
					teleport(player, object.getX() - 1, object.getY());
				} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) { // Y
					teleport(player, object.getX() + 1, object.getY());
				} else if (object.getX() == player.getX() && object.getY() == player.getY() + 1) { // left
					// side
					if (object.getID() == 749) {
						teleport(player, object.getX(), object.getY() + 1);
					} else {
						teleport(player, object.getX() + 1, object.getY());
					}
				} else if (object.getX() == player.getX() && object.getY() == player.getY() - 1) { // right
					// side.
					if (object.getID() == 749) {
						teleport(player, object.getX(), object.getY() - 1);
					} else {
						teleport(player, object.getX() + 1, object.getY());
					}
				}
			}
			if (object.getDirection() == 6) {
				if (object.getX() == player.getX() && object.getY() == player.getY() + 1) { // left
					// side
					teleport(player, object.getX(), object.getY() + 1);
				} else if (object.getX() == player.getX() && object.getY() == player.getY() - 1) { // right
					// side.
					teleport(player, object.getX(), object.getY() - 1);
				} else if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) {
					teleport(player, object.getX() + 1, object.getY() + 1);
				} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) {
					teleport(player, object.getX(), object.getY() + 1);
				}
			}
			if (object.getDirection() == 0) {
				if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) { // X
					teleport(player, object.getX() - 1, object.getY());
				} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) { // Y
					teleport(player, object.getX() + 1, object.getY());
				} else if (object.getX() == player.getX() && object.getY() == player.getY() + 1) { // left
					// side
					teleport(player, object.getX(), object.getY() + 1);
				} else if (object.getX() == player.getX() && object.getY() == player.getY() - 1) { // right
					// side.
					teleport(player, object.getX(), object.getY() - 1);
				}
			}
			if (object.getDirection() == 7) {
				if (object.getX() == player.getX() - 1 && object.getY() == player.getY()) { // X
					teleport(player, object.getX() - 1, object.getY() - 1);
				} else if (object.getX() == player.getX() + 1 && object.getY() == player.getY()) { // Y
					teleport(player, object.getX() + 1, object.getY());
				} else if (object.getX() == player.getX() && object.getY() == player.getY() + 1) { // left
					// side
					teleport(player, object.getX(), object.getY() + 1);
				} else if (object.getX() == player.getX() && object.getY() == player.getY() - 1) { // right
					// side.
					teleport(player, object.getX() + 1, object.getY());
				}
			}
			player.message("and step down the other side");
		} else {
			player.message("but you slip");
			player.damage(damage);
			if (spikeLocation == 1) {
				player.teleport(743, 3475);
			} else if (spikeLocation == 2) {
				player.teleport(748, 3482);
			} else if (spikeLocation == 3) {
				player.teleport(738, 3483);
			} else if (spikeLocation == 4) {
				player.teleport(736, 3475);
			} else if (spikeLocation == 5) {
				player.teleport(730, 3478);
			}
			say(player, null, "aargh");
		}
		if (eventMessage) {
			player.getWorld().getServer().getGameEventHandler()
				.add(new UndergroundPassMessages(player.getWorld(), player, config().GAME_TICK * DataConversions.random(3, 15)));
		}
	}
}
