package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.Constants;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.quests.members.undergroundpass.npcs.UndergroundPassKoftik;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassObstaclesMap1 implements ObjectActionListener, ObjectActionExecutiveListener {

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
	public boolean blockObjectAction(GameObject obj, String cmd, Player p) {
		return obj.getID() == UNDERGROUND_CAVE || obj.getID() == CRUMBLED_ROCK
				|| inArray(obj.getID(), READ_ROCKS) || inArray(obj.getID(), MAIN_ROCKS)
				|| inArray(obj.getID(), MAIN_LEDGE) || obj.getID() == FIRST_SWAMP
				|| inArray(obj.getID(), FAIL_SWAMP_ROCKS) || obj.getID() == PILE_OF_MUD_MAP_LEVEL_1
				|| obj.getID() == LEVER || obj.getID() == BLESSED_SPIDER_SWAMP_OBJ
				|| obj.getID() == CLEAR_ROCKS || obj.getID() == DROP_DOWN_LEDGE || inArray(obj.getID(), SPEAR_ROCKS);
	}

	@Override
	public void onObjectAction(GameObject obj, String cmd, Player p) {
		if (obj.getID() == UNDERGROUND_CAVE) {
			switch (p.getQuestStage(Constants.Quests.UNDERGROUND_PASS)) {
				case 0:
					if (p.getQuestStage(Constants.Quests.BIOHAZARD) != -1) {
						p.message("You must first complete the biohazard quest...");
						p.message("...before you can enter");
					}
					else {
						p.message("you must talk to king lathas before you can enter");
					}
					break;
				case 1:
					Npc koftik = getNearestNpc(p, NpcId.KOFTIK_ARDOUGNE.id(), 10);
					if (koftik != null) {
						UndergroundPassKoftik.koftikEnterCaveDialogue(p, koftik);
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
					message(p, "you cautiously enter the cave");
					p.teleport(673, 3420);
					break;
			}
		}
		else if (obj.getID() == CRUMBLED_ROCK) {
			message(p, "you climb the rock pile");
			p.teleport(713, 581);
		}
		else if (inArray(obj.getID(), READ_ROCKS)) {
			message(p, "the writing seems to have been scracthed...",
				"..into the rock with bare hands, it reads..");
			if (obj.getID() == 832) {
				ActionSender.sendBox(p, "@red@All those who thirst for knowledge%@red@Bow down to the lord.% %@red@All you that crave eternal life%@red@Come and meet your God.% %@red@For no man nor beast can cast a spell%@red@Against the wake of eternal hell.", true);
			} else if (obj.getID() == 833) {
				ActionSender.sendBox(p, "@red@Most men do live in fear of death%@red@That it might steal their soul.% %@red@Some work and pray to shield their life%@red@From the ravages of the cold.% %@red@But only those who embrace the end%@red@Can truly make their life extend.% %@red@And when all hope begins to fade% %@red@look above and use nature as your aid", true);
			} else if (obj.getID() == 834) {
				ActionSender.sendBox(p, "@red@And now our God has given us%@red@One who is from our own.% %@red@A saviour who once sat upon%@red@His father's glorious thrown.% %@red@It is in your name that we will lead the attack Iban%@red@son of Zamorak!", true);
			} else if (obj.getID() == 835) {
				ActionSender.sendBox(p, "@red@Here lies the sacred font%@red@Where the great Iban will bless all his disciples%@red@in the name of evil.% %@red@Here the forces of darkness are so concentrated they rise%@red@when they detect any positive force close by", true);
			} else if (obj.getID() == 923) {
				ActionSender.sendBox(p, "@red@Ibans Shadow% %@red@Then came the hard part: recreating the parts of a man%@red@that cannot be seen or touched: those intangible things%@red@that are life itself. Using all the mystical force that I could%@red@muster, I performed the ancient ritual of Incantia, a spell%@red@so powerful that it nearly stole the life from my frail and%@red@withered body. Opening my eyes again, I saw the three%@red@demons that had been summoned. Standing in a triangle,%@red@their energy was focused on the doll. These demons%@red@would be the keepers of Iban's shadow. Black as night,%@red@their shared spirit would follow his undead body like an%@red@angel of death.", true);
			} else if (obj.getID() == 922) {
				ActionSender.sendBox(p, "% %@red@Crumbling some of the dove's bones onto the doll, I cast%@red@my mind's eye onto Iban's body. My ritual was complete,%@red@soon he would be coming to life. I, Kardia, had resurrected%@red@the legendary Iban, the most powerful evil being ever to%@red@take human form. And I alone knew that the same%@red@process that I had used to create him, was also capable%@red@of destroying him.% %@red@But now I was exhausted. As I closed my eyes to sleep, I%@red@was settled by a strange feeling of contentment%@red@anticipation of the evil that Iban would soon unleash.", true);
			} else if (obj.getID() == 881) {
				ActionSender.sendBox(p, "@red@Leave this battered corpse be% %@red@For now he lives as spirit alone% %@red@Let his flesh rest and become one with the earth% %@red@As it is the soil that shall rise to protect him% %@red@Only as flesh becomes dust, as wood becomes ash...% %@red@..will Iban's corpse embrace nature and finally rest", true);
			}
		}
		else if (inArray(obj.getID(), MAIN_ROCKS)) {
			doRock(obj, p, (int) (getCurrentLevel(p, SKILLS.HITS.id()) / 42) + 1, true, -1);
		}
		else if (obj.getID() == FIRST_SWAMP) {
			message(p, "you try to cross but you're unable to",
				"the swamp seems to cling to your legs");
			p.message("you slowly feel yourself being dragged below");
			playerTalk(p, null, "gulp!");
			p.teleport(674, 3462);
			playerTalk(p, null, "aargh");
			p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) / 42) + 1);
			sleep(2000);
			p.teleport(677, 3462);
			sleep(650);
			p.teleport(680, 3465);
			sleep(650);
			p.teleport(682, 3462);
			sleep(650);
			p.teleport(683, 3465);
			sleep(650);
			p.teleport(685, 3464);
			sleep(650);
			p.teleport(687, 3462);
			sleep(650);
			playerTalk(p, null, "aargh");
			p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) / 42) + 1);
			p.teleport(690, 3461);
			message(p, "you tumble deep into the cravass",
				"and land battered and bruised at the base");
		}
		else if (inArray(obj.getID(), FAIL_SWAMP_ROCKS)) {
			doRock(obj, p, (int) (getCurrentLevel(p, SKILLS.HITS.id()) / 42) + 1, true, -1);
		}
		else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_1) {
			message(p, "you climb up the mud pile");
			p.teleport(685, 3420);
			message(p, "it leads into darkness, the stench is almost unbearable",
				"you surface by the swamp, covered in muck");
		}
		else if (inArray(obj.getID(), MAIN_LEDGE)) {
			doLedge(obj, p, (int) (getCurrentLevel(p, SKILLS.HITS.id()) / 42) + 1);
		}
		else if (obj.getID() == LEVER) {
			message(p, "you pull back on the old lever",
				"the bridge slowly lowers");
			GameObject bridge_open = new GameObject(Point.location(704, 3417), 727, 2, 0);
			GameObject bridge_closed = new GameObject(Point.location(704, 3417), 726, 2, 0);
			World.getWorld().registerGameObject(bridge_open);
			World.getWorld().delayedSpawnObject(bridge_closed.getLoc(), 10000);
			p.teleport(709, 3420);
			sleep(650);
			p.teleport(706, 3420);
			sleep(650);
			p.teleport(703, 3420);
			p.message("you cross the bridge");
		}
		else if (obj.getID() == BLESSED_SPIDER_SWAMP_OBJ) {
			message(p, "you step in rancid swamp",
				"it clings to your feet, you cannot cross");
		}
		else if (obj.getID() == CLEAR_ROCKS) {
			if (p.getX() == 695 && (p.getY() == 3436 || p.getY() == 3435)) {
				p.teleport(695, 3435);
				return;
			}
			message(p, "you move the rocks from your path");
			p.message("you hear a strange mechanical sound");
			World.getWorld().replaceGameObject(obj,
				new GameObject(obj.getLocation(), CLEAR_ROCKS + 1, obj.getDirection(), obj
					.getType()));
			World.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
			p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) * 0.2D));
			playerTalk(p, null, "aaarrghhh");
			message(p, "You've triggered a trap");
		}
		else if (inArray(obj.getID(), SPEAR_ROCKS)) {
			if (cmd.equalsIgnoreCase("step over")) {
				message(p, "you step over the rock");
				p.message("you feel a thread tug at your boot");
				p.message("it's a trap");
				p.teleport(obj.getX(), obj.getY());
				World.getWorld().replaceGameObject(obj,
					new GameObject(obj.getLocation(), 805, obj.getDirection(), obj
						.getType()));
				World.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
				p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) / 6) + 1);
				playerTalk(p, null, "aaarghh");
			} else {
				message(p, "you search the rock",
					"you find a trip wire");
				p.message("do you wish to disarm the trap?");
				int menu = showMenu(p, "yes, i'll have a go", "no chance");
				if (menu == 0) {
					message(p, "you carefully try and diconnect the trip wire");
					if (succeed(p, 1)) {
						p.message("you manage to delay the trap..");
						p.message("...long enough to cross the rocks");
						if (obj.getX() == p.getX() + 1)
							p.teleport(obj.getX() + 1, obj.getY());
						else
							p.teleport(obj.getX() - 1, obj.getY());
					}
					else {
						p.message("but the trap activates");
						p.teleport(obj.getX(), obj.getY());
						World.getWorld().replaceGameObject(obj,
							new GameObject(obj.getLocation(), 805, obj.getDirection(), obj
								.getType()));
						World.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
						p.damage((int) (getCurrentLevel(p, SKILLS.HITS.id()) / 6) + 1);
						playerTalk(p, null, "aaarghh");
					}

				} else if (menu == 1) {
					p.message("you back away from the trap");
				}
			}
		}
		else if (obj.getID() == DROP_DOWN_LEDGE) {
			p.message("you drop down to the cave floor");
			p.teleport(706, 3439);
		}
	}

	boolean succeed(Player player, int req) {
		int level_difference = getCurrentLevel(player, SKILLS.THIEVING.id()) - req;
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
}
