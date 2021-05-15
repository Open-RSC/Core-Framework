package com.openrsc.server.plugins.authentic.quests.members.legendsquest.obstacles;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestCaveAgility implements OpLocTrigger {

	private static final int ROCK_HEWN_STAIRS_1 = 1114;
	private static final int ROCK_HEWN_STAIRS_2 = 1123;
	private static final int ROCK_HEWN_STAIRS_3 = 1124;
	private static final int ROCK_HEWN_STAIRS_4 = 1125;

	private static final int ROCKY_WALKWAY_1 = 558;
	private static final int ROCKY_WALKWAY_2 = 559;
	private static final int ROCKY_WALKWAY_3 = 560;
	private static final int ROCKY_WALKWAY_4 = 561;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), ROCK_HEWN_STAIRS_1, ROCK_HEWN_STAIRS_2, ROCK_HEWN_STAIRS_3, ROCK_HEWN_STAIRS_4, ROCKY_WALKWAY_1, ROCKY_WALKWAY_2, ROCKY_WALKWAY_3, ROCKY_WALKWAY_4);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		switch (obj.getID()) {
			case ROCKY_WALKWAY_1:
			case ROCKY_WALKWAY_2:
			case ROCKY_WALKWAY_3:
			case ROCKY_WALKWAY_4:
				if (player.getX() == obj.getX() && player.getY() == obj.getY()) {
					player.message("You're standing there already!");
					return;
				}
				if (succeed(player, 50)) {
					player.message("You manage to keep your balance.");
					player.teleport(obj.getX(), obj.getY());
					player.incExp(Skill.AGILITY.id(), 20, true);
				} else {
					player.teleport(421, 3699);
					player.message("You slip and fall...");
					int failScene = DataConversions.random(0, 10);
					if (failScene == 0) {
						player.message("...but you luckily avoid any damage.");
					}
					else if (failScene <= 2) {
						player.damage(DataConversions.random(3, 6));
						player.message("...and take a bit of damage.");
					}
					else if (failScene <= 5) {
						player.damage(DataConversions.random(7, 11));
						player.message("...and take some damage.");
					}
					else if (failScene <= 7) {
						player.damage(DataConversions.random(12, 16));
						player.message("...and take damage.");
					}
					else if (failScene <= 9) {
						player.damage(DataConversions.random(17, 23));
						player.message("...and are injured.");
					}
					else {
						player.damage(DataConversions.random(24, 31));
						player.message("...and take some major damage.");
					}
					player.incExp(Skill.AGILITY.id(), 5, true);
				}
				break;
			case ROCK_HEWN_STAIRS_4:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 50) {
					player.message("You need an agility level of 50 to step these stairs");
					return;
				}
				if (succeed(player, 50)) {
					if (player.getX() <= 419) {
						player.message("You climb down the steps.");
						player.teleport(421, 3707);
						delay();
						player.incExp(Skill.AGILITY.id(), 20, true);
						player.teleport(423, 3707);
					} else {
						player.message("You climb up the stairs.");
						player.teleport(421, 3707);
						delay();
						player.incExp(Skill.AGILITY.id(), 20, true);
						player.teleport(419, 3707);
					}
				} else {
					player.message("You slip and fall...");
					player.damage(DataConversions.random(2, 3));
					player.teleport(421, 3707);
					delay();
					player.incExp(Skill.AGILITY.id(), 5, true);
					player.teleport(423, 3707);
				}
				break;
			case ROCK_HEWN_STAIRS_3:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 50) {
					player.message("You need an agility level of 50 to step these stairs");
					return;
				}
				if (succeed(player, 50)) {
					if (player.getY() <= 3702) {
						player.message("You climb down the steps.");
						player.teleport(419, 3704);
						delay();
						player.incExp(Skill.AGILITY.id(), 20, true);
						player.teleport(419, 3706);
					} else {
						player.message("You climb up the stairs.");
						player.teleport(419, 3704);
						delay();
						player.incExp(Skill.AGILITY.id(), 20, true);
						player.teleport(419, 3702);
					}
				} else {
					player.message("You slip and fall...");
					player.damage(DataConversions.random(2, 3));
					player.teleport(419, 3704);
					delay();
					player.incExp(Skill.AGILITY.id(), 5, true);
					player.teleport(419, 3706);
				}
				break;
			case ROCK_HEWN_STAIRS_2:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 50) {
					player.message("You need an agility level of 50 to step these stairs");
					return;
				}
				if (succeed(player, 50)) {
					if (player.getX() >= 426) {
						player.message("You climb down the steps.");
						player.teleport(424, 3702);
						delay();
						player.incExp(Skill.AGILITY.id(), 20, true);
						player.teleport(422, 3702);
					} else {
						player.message("You climb up the stairs.");
						player.teleport(424, 3702);
						delay();
						player.incExp(Skill.AGILITY.id(), 20, true);
						player.teleport(426, 3702);
					}
				} else {
					player.message("You slip and fall...");
					player.damage(DataConversions.random(2, 3));
					player.teleport(424, 3702);
					delay();
					player.incExp(Skill.AGILITY.id(), 5, true);
					player.teleport(422, 3702);
				}
				break;
			case ROCK_HEWN_STAIRS_1:
				if (getCurrentLevel(player, Skill.AGILITY.id()) < 50) {
					player.message("You need an agility level of 50 to step these stairs");
					return;
				}
				if (succeed(player, 50)) {
					if (player.getY() >= 3706) {
						player.message("You climb down the steps.");
						player.teleport(426, 3704);
						delay();
						player.incExp(Skill.AGILITY.id(), 20, true);
						player.teleport(426, 3702);
					} else {
						player.message("You climb up the stairs.");
						player.teleport(426, 3704);
						delay();
						player.incExp(Skill.AGILITY.id(), 20, true);
						player.teleport(426, 3706);
					}
				} else {
					player.message("You slip and fall...");
					player.damage(DataConversions.random(2, 3));
					player.teleport(426, 3704);
					delay();
					player.incExp(Skill.AGILITY.id(), 5, true);
					player.teleport(426, 3702);
				}
				break;
		}
	}

	boolean succeed(Player player, int req) {
		return Formulae.calcProductionSuccessfulLegacy(req, getCurrentLevel(player, Skill.AGILITY.id()), false, req + 30);
	}
}
