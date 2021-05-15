package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
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
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), PILE_OF_MUD_MAP_LEVEL_2) || obj.getID() == CRATE || inArray(obj.getID(), DUG_UP_SOIL)
				|| obj.getID() == LEDGE || obj.getID() == WALL_GRILL_EAST || obj.getID() == WALL_GRILL_WEST
				|| inArray(obj.getID(), ROCKS) || obj.getID() == HIJACK_ROCK || obj.getID() == PASSAGE || obj.getID() == CAGE_REMAINS
				|| obj.getID() == GATE_OF_IBAN || obj.getID() == FLAMES_OF_ZAMORAK || obj.getID() == GATE_OF_ZAMORAK;
	}
	// 753, 3475

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (inArray(obj.getID(), PILE_OF_MUD_MAP_LEVEL_2)) {
			if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[0]) {
				mes("you climb the pile of mud...");
				delay(3);
				mes("it leads to a small tunnel...");
				delay(3);
				player.teleport(727, 3448);
				player.message("..ending at the well entrance");
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[1]) {
				mes("you climb the pile of mud");
				delay(3);
				player.teleport(753, 3481);
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[2]) {
				mes("you climb the pile of mud");
				delay(3);
				player.teleport(753, 3475);
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[3]) {
				mes("you climb the pile of mud");
				delay(3);
				player.teleport(743, 3483);
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[4]) {
				mes("you climb the pile of mud");
				delay(3);
				player.teleport(740, 3476);
			}
			else if (obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[5]) {
				mes("you climb the pile of mud");
				delay(3);
				player.teleport(735, 3478);
			}
		}
		else if (obj.getID() == CRATE) {
			mes("you search the crate");
			delay(3);
			if (!player.getCache().hasKey("crate_food")) {
				player.message("inside you find some food");
				give(player, ItemId.SALMON.id(), 2);
				give(player, ItemId.MEAT_PIE.id(), 2);
				player.getCache().store("crate_food", true);
			} else {
				player.message("but you find nothing");
			}
		}
		else if (inArray(obj.getID(), DUG_UP_SOIL)) {
			mes("under the soil is a tunnel");
			delay(3);
			player.message("would you like to enter?");
			int menu = multi(player,
				"no, im scared of small spaces", "yep, let's do it");
			if (menu == 1) {
				mes("you climb into the small tunnel");
				delay(3);
				if (obj.getID() == DUG_UP_SOIL[1])
					player.teleport(745, 3457);
				else
					player.teleport(747, 3470);
				player.message("and crawl into a small dark passage");
			}
		}
		else if (obj.getID() == LEDGE) {
			if (command.equalsIgnoreCase("climb up")) {
				player.message("you climb the ledge");
				if (DataConversions.getRandom().nextInt(10) <= 1) {
					player.message("but you slip");
					player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 42) + 1);
					say(player, null, "aargh");
				} else
					player.teleport(764, 3463);
			} else {
				player.message("you take a few paces back...");
				player.message("and run torwards the ledge...");
				player.teleport(764, 3461);
				delay(3);
				player.message("you land way short of the other platform");
				player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5);
				player.teleport(764, 3467);
				say(player, null, "ooof");
			}
		}
		else if (obj.getID() == WALL_GRILL_EAST) {
			if (!player.getCache().hasKey("rope_wall_grill")) {
				mes("the wall grill is too high");
				delay(3);
				player.message("you can't quite reach");
			} else {
				mes("you use the rope tied to the grill to pull yourself up");
				delay(3);
				player.message("you then climb across the grill to the otherside");
				player.teleport(762, 3472);
			}
		}
		else if (obj.getID() == WALL_GRILL_WEST) {
			mes("you climb across the grill to the otherside");
			delay(3);
			player.teleport(766, 3463);
		}
		else if (inArray(obj.getID(), ROCKS)) {
			switch (obj.getID()) {
				case 859:
				case 858:
					UndergroundPassObstaclesMap1.doRock(obj, player, (int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5, false, 5); // fall side 5.
					break;
				case 854:
				case 853:
				case 855:
				case 857:
					UndergroundPassObstaclesMap1.doRock(obj, player, (int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5, false, 4); // fall side 4.
					break;
				case 852:
					UndergroundPassObstaclesMap1.doRock(obj, player, (int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5, false, 3); // fall side 3.
					break;
				case 851:
					UndergroundPassObstaclesMap1.doRock(obj, player, (int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5, false, 2); // fall side 2.
					break;
				default:
					UndergroundPassObstaclesMap1.doRock(obj, player, (int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5, false, 1); // fall side 1
					break;
			}
		}
		else if (obj.getID() == HIJACK_ROCK) {
			player.message("you climb onto the rock");
			if (DataConversions.getRandom().nextInt(5) == 4) {
				player.message("but you slip");
				player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5);
				player.teleport(734, 3483);
				say(player, null, "aargh");
			} else {
				if (player.getX() == 734) {
					player.teleport(735, 3479);
				} else {
					player.teleport(734, 3480);
				}
				player.message("and step down the other side");
			}
		}
		else if (obj.getID() == PASSAGE) {
			mes("you walk down the passage way");
			delay(3);
			player.message("you step on a pressure trigger");
			player.message("it's a trap");
			if (obj.getX() == 737 || obj.getX() == 735) {
				player.teleport(737, 3489);
			} else if (obj.getX() == 733) {
				player.teleport(733, 3489);
			}
			player.getWorld().replaceGameObject(obj,
				new GameObject(obj.getWorld(), obj.getLocation(), 826, obj.getDirection(), obj
					.getType()));
			player.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
			player.damage((int) (getCurrentLevel(player, Skill.HITS.id()) / 5) + 5);
			say(player, null, "aaarghh");
		}
		else if (obj.getID() == CAGE_REMAINS) {
			if (player.getQuestStage(Quests.UNDERGROUND_PASS) >= 5 || player.getQuestStage(Quests.UNDERGROUND_PASS) == -1) {
				mes("you search the cage remains");
				delay(3);
				player.message("nothing remains");
				return;
			}
			if (!player.getCarriedItems().hasCatalogID(ItemId.UNDERGROUND_PASS_UNICORN_HORN.id(), Optional.empty())) {
				delay(3);
				player.message("all that remains is a damaged horn");
				give(player, ItemId.UNDERGROUND_PASS_UNICORN_HORN.id(), 1);
			} else {
				player.message("nothing remains");
			}
		}
		else if (obj.getID() == GATE_OF_IBAN) {
			player.message("you pull on the great door");
			if ((player.getCache().hasKey("flames_of_zamorak1") && player.getCache().hasKey("flames_of_zamorak2") && (player.getCache().hasKey("flames_of_zamorak3") && player.getCache().getInt("flames_of_zamorak3") >= 2)) || atQuestStages(player, Quests.UNDERGROUND_PASS, 7, 8, -1)) {
				mes("from behind the door you hear cry's and moans");
				delay(3);
				player.message("the door slowly creeks open");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 723, obj.getDirection(), obj
					.getType()));
				addloc(obj.getWorld(), obj.getLoc(), 3000);
				player.teleport(766, 3417);
				delay(2);
				player.teleport(770, 3417);
				player.message("you walk into the darkness");
			} else {
				player.message("the door refuses to open");
			}
		}
		else if (obj.getID() == GATE_OF_ZAMORAK) {
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 723, obj.getDirection(), obj
				.getType()));
			addloc(obj.getWorld(), obj.getLoc(), 3000);
			player.teleport(766, 3417);
			mes("you open the huge wooden door");
			delay(3);
			player.teleport(763, 3417);
			player.message("and walk through");
		}
		else if (obj.getID() == FLAMES_OF_ZAMORAK) {
			mes("you search the stone structure");
			delay(3);
			player.message("on the side you find an old inscription");
			player.message("it reads...");
			ActionSender.sendBox(player, "@red@While I sense the soft beating of a good heart I will not open% %@red@Feed me three crests of the blessed warriors, and the%@red@creatures remains% %@red@Throw them to me as an offering, a gift of hatred, a token% %@red@Then finally rejoice as all goodness dies in my flames", true);
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return inArray(obj.getID(), RAILINGS);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (inArray(obj.getID(), RAILINGS)) {
			if (click == 0) {
				if (obj.getID() == 168) {
					mes("the cage door has been sealed shut");
					delay(3);
					player.message("the poor unicorn can't escape");
					return;
				}
				player.message("you attempt to pick the lock");
				if (obj.getID() == 169 && getCurrentLevel(player, Skill.THIEVING.id()) < 50) {
					player.message("you need a level of 50 thieving to pick this lock");
					return;
				}
				player.message("You manage to pick the lock");
				player.message("you walk through");
				if (obj.getDirection() == 0) {
					if (obj.getY() == player.getY())
						player.teleport(obj.getX(), obj.getY() - 1);
					else
						player.teleport(obj.getX(), obj.getY());
				}
				if (obj.getDirection() == 1) {
					if (obj.getX() == player.getX())
						player.teleport(obj.getX() - 1, obj.getY());
					else
						player.teleport(obj.getX(), obj.getY());
				}
				player.incExp(Skill.THIEVING.id(), 15, true);
				delay(3);
				player.message("the cage slams shut behind you");
			} else if (click == 1) {
				if (obj.getID() == 168) {
					mes("you search the cage");
					delay(3);
					if (!player.getCarriedItems().hasCatalogID(ItemId.RAILING.id(), Optional.of(false))) {
						player.message("you find a loose railing lying on the floor");
						give(player, ItemId.RAILING.id(), 1);
					} else
						player.message("but you find nothing");
					return;
				}
				player.message("the cage has been locked");
			}
		}
	}
}
