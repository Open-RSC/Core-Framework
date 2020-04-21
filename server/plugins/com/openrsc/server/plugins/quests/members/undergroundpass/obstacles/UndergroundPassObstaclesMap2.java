package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassObstaclesMap2 implements OpLocTrigger, OpBoundTrigger {

	/**
	 * OBJECT IDs
	 **/
	public static int[] PILE_OF_MUD_MAP_LEVEL_2 = {841, 843, 844, 845, 846, 847};
	public static int CRATE = 868;
	public static int[] RAILINGS = {167, 170, 169, 168};
	public static int[] DUG_UP_SOIL = {839, 840};
	public static int LEDGE = 837;
	public static int WALL_GRILL_EAST = 836;
	public static int WALL_GRILL_WEST = 838;
	public static int[] ROCKS = {849, 850, 851, 852, 860, 853, 854, 855, 859, 857, 858};
	public static int HIJACK_ROCK = 856;
	public static int PASSAGE = 873;
	public static int CAGE_REMAINS = 871;
	public static int GATE_OF_IBAN = 722;
	public static int FLAMES_OF_ZAMORAK = 830;
	public static int GATE_OF_ZAMORAK = 875;

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), PILE_OF_MUD_MAP_LEVEL_2) || obj.getID() == CRATE || inArray(obj.getID(), DUG_UP_SOIL)
				|| obj.getID() == LEDGE || obj.getID() == WALL_GRILL_EAST || obj.getID() == WALL_GRILL_WEST
				|| inArray(obj.getID(), ROCKS) || obj.getID() == HIJACK_ROCK || obj.getID() == PASSAGE || obj.getID() == CAGE_REMAINS
				|| obj.getID() == GATE_OF_IBAN || obj.getID() == FLAMES_OF_ZAMORAK || obj.getID() == GATE_OF_ZAMORAK;
	}
	// 753, 3475

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (inArray(obj.getID(), PILE_OF_MUD_MAP_LEVEL_2)) {
			if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[0]) {
				Functions.mes(p, "you climb the pile of mud...",
					"it leads to a small tunnel...");
				p.teleport(727, 3448);
				p.message("..ending at the well entrance");
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[1]) {
				Functions.mes(p, "you climb the pile of mud");
				p.teleport(753, 3481);
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[2]) {
				Functions.mes(p, "you climb the pile of mud");
				p.teleport(753, 3475);
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[3]) {
				Functions.mes(p, "you climb the pile of mud");
				p.teleport(743, 3483);
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[4]) {
				Functions.mes(p, "you climb the pile of mud");
				p.teleport(740, 3476);
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[5]) {
				Functions.mes(p, "you climb the pile of mud");
				p.teleport(735, 3478);
			}
		}
		else if (obj.getID() == CRATE) {
			Functions.mes(p, "you search the crate");
			if (!p.getCache().hasKey("crate_food")) {
				p.message("inside you find some food");
				give(p, ItemId.SALMON.id(), 2);
				give(p, ItemId.MEAT_PIE.id(), 2);
				p.getCache().store("crate_food", true);
			} else {
				p.message("but you find nothing");
			}
		}
		else if (inArray(obj.getID(), DUG_UP_SOIL)) {
			Functions.mes(p, "under the soil is a tunnel");
			p.message("would you like to enter?");
			int menu = multi(p,
				"no, im scared of small spaces", "yep, let's do it");
			if (menu == 1) {
				Functions.mes(p, "you climb into the small tunnel");
				if (obj.getID() == DUG_UP_SOIL[1])
					p.teleport(745, 3457);
				else
					p.teleport(747, 3470);
				p.message("and crawl into a small dark passage");
			}
		}
		else if (obj.getID() == LEDGE) {
			if (command.equalsIgnoreCase("climb up")) {
				p.message("you climb the ledge");
				if (DataConversions.getRandom().nextInt(10) <= 1) {
					p.message("but you slip");
					p.damage((int) (getCurrentLevel(p, Skills.HITS) / 42) + 1);
					say(p, null, "aargh");
				} else
					p.teleport(764, 3463);
			} else {
				p.message("you take a few paces back...");
				p.message("and run torwards the ledge...");
				p.teleport(764, 3461);
				delay(1600);
				p.message("you land way short of the other platform");
				p.damage((int) (getCurrentLevel(p, Skills.HITS) / 5) + 5);
				p.teleport(764, 3467);
				say(p, null, "ooof");
			}
		}
		else if (obj.getID() == WALL_GRILL_EAST) {
			if (!p.getCache().hasKey("rope_wall_grill")) {
				Functions.mes(p, "the wall grill is too high");
				p.message("you can't quite reach");
			} else {
				Functions.mes(p, "you use the rope tied to the grill to pull yourself up");
				p.message("you then climb across the grill to the otherside");
				p.teleport(762, 3472);
			}
		}
		else if (obj.getID() == WALL_GRILL_WEST) {
			Functions.mes(p, "you climb across the grill to the otherside");
			p.teleport(766, 3463);
		}
		else if (inArray(obj.getID(), ROCKS)) {
			switch (obj.getID()) {
				case 859:
				case 858:
					UndergroundPassObstaclesMap1.doRock(obj, p, (int) (getCurrentLevel(p, Skills.HITS) / 5) + 5, false, 5); // fall side 5.
					break;
				case 854:
				case 853:
				case 855:
				case 857:
					UndergroundPassObstaclesMap1.doRock(obj, p, (int) (getCurrentLevel(p, Skills.HITS) / 5) + 5, false, 4); // fall side 4.
					break;
				case 852:
					UndergroundPassObstaclesMap1.doRock(obj, p, (int) (getCurrentLevel(p, Skills.HITS) / 5) + 5, false, 3); // fall side 3.
					break;
				case 851:
					UndergroundPassObstaclesMap1.doRock(obj, p, (int) (getCurrentLevel(p, Skills.HITS) / 5) + 5, false, 2); // fall side 2.
					break;
				default:
					UndergroundPassObstaclesMap1.doRock(obj, p, (int) (getCurrentLevel(p, Skills.HITS) / 5) + 5, false, 1); // fall side 1
					break;
			}
		}
		else if (obj.getID() == HIJACK_ROCK) {
			p.setBusyTimer(p.getWorld().getServer().getConfig().GAME_TICK);
			p.message("you climb onto the rock");
			if (DataConversions.getRandom().nextInt(5) == 4) {
				p.message("but you slip");
				p.damage((int) (getCurrentLevel(p, Skills.HITS) / 5) + 5);
				p.teleport(734, 3483);
				say(p, null, "aargh");
			} else {
				if (p.getX() == 734) {
					p.teleport(735, 3479);
				} else {
					p.teleport(734, 3480);
				}
				p.message("and step down the other side");
			}
		}
		else if (obj.getID() == PASSAGE) {
			Functions.mes(p, "you walk down the passage way");
			p.message("you step on a pressure trigger");
			p.message("it's a trap");
			if (obj.getX() == 737 || obj.getX() == 735) {
				p.teleport(737, 3489);
			} else if (obj.getX() == 733) {
				p.teleport(733, 3489);
			}
			p.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), 826, obj.getDirection(), obj
					.getType()));
			p.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
			p.damage((int) (getCurrentLevel(p, Skills.HITS) / 5) + 5);
			say(p, null, "aaarghh");
		}
		else if (obj.getID() == CAGE_REMAINS) {
			if (p.getQuestStage(Quests.UNDERGROUND_PASS) >= 5 || p.getQuestStage(Quests.UNDERGROUND_PASS) == -1) {
				Functions.mes(p, "you search the cage remains");
				p.message("nothing remains");
				return;
			}
			if (!p.getCarriedItems().hasCatalogID(ItemId.UNDERGROUND_PASS_UNICORN_HORN.id(), Optional.empty())) {
				delay(1600);
				p.message("all that remains is a damaged horn");
				give(p, ItemId.UNDERGROUND_PASS_UNICORN_HORN.id(), 1);
			} else {
				p.message("nothing remains");
			}
		}
		else if (obj.getID() == GATE_OF_IBAN) {
			p.message("you pull on the great door");
			if ((p.getCache().hasKey("flames_of_zamorak1") && p.getCache().hasKey("flames_of_zamorak2") && (p.getCache().hasKey("flames_of_zamorak3") && p.getCache().getInt("flames_of_zamorak3") >= 2)) || atQuestStages(p, Quests.UNDERGROUND_PASS, 7, 8, -1)) {
				Functions.mes(p, "from behind the door you hear cry's and moans");
				p.message("the door slowly creeks open");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 723, obj.getDirection(), obj
					.getType()));
				Functions.addloc(obj.getWorld(), obj.getLoc(), 3000);
				p.teleport(766, 3417);
				delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
				p.teleport(770, 3417);
				p.message("you walk into the darkness");
			} else {
				p.message("the door refuses to open");
			}
		}
		else if (obj.getID() == GATE_OF_ZAMORAK) {
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 723, obj.getDirection(), obj
				.getType()));
			Functions.addloc(obj.getWorld(), obj.getLoc(), 3000);
			p.teleport(766, 3417);
			Functions.mes(p, "you open the huge wooden door");
			p.teleport(763, 3417);
			p.message("and walk through");
		}
		else if (obj.getID() == FLAMES_OF_ZAMORAK) {
			Functions.mes(p, "you search the stone structure");
			p.message("on the side you find an old inscription");
			p.message("it reads...");
			ActionSender.sendBox(p, "@red@While I sense the soft beating of a good heart I will not open% %@red@Feed me three crests of the blessed warriors, and the%@red@creatures remains% %@red@Throw them to me as an offering, a gift of hatred, a token% %@red@Then finally rejoice as all goodness dies in my flames", true);
		}
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player p) {
		return inArray(obj.getID(), RAILINGS);
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (inArray(obj.getID(), RAILINGS)) {
			if (click == 0) {
				if (obj.getID() == 168) {
					Functions.mes(p, "the cage door has been sealed shut");
					p.message("the poor unicorn can't escape");
					return;
				}
				p.message("you attempt to pick the lock");
				if (obj.getID() == 169 && getCurrentLevel(p, Skills.THIEVING) < 50) {
					p.message("you need a level of 50 thieving to pick this lock");
					return;
				}
				p.setBusyTimer(1600);
				p.message("You manage to pick the lock");
				p.message("you walk through");
				if (obj.getDirection() == 0) {
					if (obj.getY() == p.getY())
						p.teleport(obj.getX(), obj.getY() - 1);
					else
						p.teleport(obj.getX(), obj.getY());
				}
				if (obj.getDirection() == 1) {
					if (obj.getX() == p.getX())
						p.teleport(obj.getX() - 1, obj.getY());
					else
						p.teleport(obj.getX(), obj.getY());
				}
				p.incExp(Skills.THIEVING, 15, true);
				delay(1600);
				p.message("the cage slams shut behind you");
			} else if (click == 1) {
				if (obj.getID() == 168) {
					Functions.mes(p, "you search the cage");
					if (!p.getCarriedItems().hasCatalogID(ItemId.RAILING.id(), Optional.of(false))) {
						p.message("you find a loose railing lying on the floor");
						give(p, ItemId.RAILING.id(), 1);
					} else
						p.message("but you find nothing");
					return;
				}
				p.message("the cage has been locked");
			}
		}
	}
}
