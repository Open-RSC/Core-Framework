package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.custom.UndergroundPassMessages;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassAgilityObstacles implements OpLocTrigger {

	public static final int[] LEDGES = {862, 864, 863, 872, 865, 866};
	public static final int NORTH_STONE_STEP = 889;
	public static final int SOUTH_STONE_STEP = 921;
	public static final int FIRST_REMAINING_BRIDGE = 891;
	public static final int[] STONE_JUMP_BRIDGES = {898, 892, 896, 910, 906, 908, 902, 904, 900, 894};
	public static final int[] STONE_REMAINING_BRIDGES = {893, 907, 905, 909, 903, 901, 895, 899, 897};

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), LEDGES) || inArray(obj.getID(), STONE_JUMP_BRIDGES) || inArray(obj.getID(), STONE_REMAINING_BRIDGES)
				|| obj.getID() == FIRST_REMAINING_BRIDGE || obj.getID() == NORTH_STONE_STEP || obj.getID() == SOUTH_STONE_STEP;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (inArray(obj.getID(), LEDGES)) {
			mes("you climb the ledge");
			delay(3);
			if (succeed(player, 1)) {
				switch (obj.getID()) {
					case 862:
						player.teleport(730, 3494);
						break;
					case 864:
						player.teleport((player.getQuestStage(Quests.UNDERGROUND_PASS) >= 4) || (player.getQuestStage(Quests.UNDERGROUND_PASS) == -1) ? 751 : 734, 3496);
						break;
					case 863:
						player.teleport(763, 3442);
						break;
					case 872:
						player.teleport((player.getQuestStage(Quests.UNDERGROUND_PASS) >= 4) || (player.getQuestStage(Quests.UNDERGROUND_PASS) == -1) ? 765 : 748, 3497);
						break;
					case 865:
						player.teleport(728, 3499);
						break;
					case 866:
						player.teleport((player.getQuestStage(Quests.UNDERGROUND_PASS) >= 4) || (player.getQuestStage(Quests.UNDERGROUND_PASS) == -1) ? 755 : 738, 3501);
						break;
				}
				player.message("you drop down to the cave floor");
			} else {
				player.message("but you loose your footing");
				player.damage(2);
				say(player, null, "aargh");
			}
		}
		else if (obj.getID() == NORTH_STONE_STEP) {
			if (player.getQuestStage(Quests.UNDERGROUND_PASS) == 4) {
				failBlackAreaObstacle(player, obj); // fail directly, to get stage 5.
			} else {
				mes("you walk down the stone steps");
				delay(3);
				player.teleport(766, 585);

			}
		}
		else if (obj.getID() == SOUTH_STONE_STEP) {
			mes("you walk down the steps");
			delay(3);
			mes("they lead to a ladder, you climb down");
			delay(3);
			player.teleport(739, 667);
		}
		else if (obj.getID() == FIRST_REMAINING_BRIDGE) {
			mes("you attempt to walk over the remaining bridge..");
			delay(3);
			if (player.getQuestStage(Quests.UNDERGROUND_PASS) == 4) {
				failBlackAreaObstacle(player, obj); // fail directly, to get stage 5.
			} else {
				if (succeed(player, 1)) {
					if (obj.getX() == player.getX() + 1) {
						player.teleport(776, obj.getY());
					} else {
						player.teleport(773, obj.getY());
					}
					player.message("..you manage to cross safley");
				} else {
					failBlackAreaObstacle(player, obj);
				}
			}
		}
		else if (inArray(obj.getID(), STONE_REMAINING_BRIDGES) || inArray(obj.getID(), STONE_JUMP_BRIDGES)) {
			if (inArray(obj.getID(), STONE_JUMP_BRIDGES)) {
				mes("you attempt to jump across the gap..");
				delay(3);
			} else {
				mes("you attempt to walk over the remaining bridge..");
				delay(3);
			}
			if (succeed(player, 1)) {
				if (obj.getX() == player.getX() + 1) {
					player.teleport(obj.getX() + 3, obj.getY());
				} else if (obj.getX() == player.getX() - 3) {
					player.teleport(obj.getX() - 1, obj.getY());
				} else if (obj.getY() == player.getY() + 1) {
					player.teleport(obj.getX(), obj.getY() + 3);
				} else if (obj.getY() == player.getY() - 3) {
					player.teleport(obj.getX(), obj.getY() - 1);
				}
				player.message("..you manage to cross safley");
			} else {
				failBlackAreaObstacle(player, obj);
			}
			player.getWorld().getServer().getGameEventHandler()
				.add(new UndergroundPassMessages(player.getWorld(), player, config().GAME_TICK * DataConversions.random(5, 25)));
		}
	}

	boolean succeed(Player player, int req) {
		return Formulae.calcProductionSuccessfulLegacy(req, player.getSkills().getLevel(Skill.AGILITY.id()), false, req + 70);
	}

	private void failBlackAreaObstacle(Player player, GameObject obj) {
		player.message("..but you slip and tumble into the darkness");
		fallTeleportLocation(player, obj);
		player.damage(((int) getCurrentLevel(player, Skill.HITS.id()) / 5) + 5); // 6 lowest, 25 max.
		say(player, null, "ouch!");
		if (player.getQuestStage(Quests.UNDERGROUND_PASS) >= 4) {
			if (player.getQuestStage(Quests.UNDERGROUND_PASS) == 4) {
				player.updateQuestStage(Quests.UNDERGROUND_PASS, 5);
			}
			//only on "first-time" fail at stages 5, 8
			Npc koftik = ifnearvisnpc(player, NpcId.KOFTIK_RECOVERED.id(), 10);
			if (koftik != null &&
					(!player.getCache().hasKey("advised_koftik") || !player.getCache().getBoolean("advised_koftik")) ) {
				npcsay(player, koftik, "traveller is that you?.. my friend on a mission");
				say(player, koftik, "koftik, you're still here, you should leave");
				npcsay(player, koftik, "leave?...leave?..this is my home now",
					"home with my lord, he talks to me, he's my friend");
				player.message("koftik seems to be in a weak state of mind");
				say(player, koftik, "koftik you really should leave these caverns");
				npcsay(player, koftik, "not now, we're all the same down here",
					"now there's just you and those dwarfs to be converted");
				say(player, koftik, "dwarfs?");
				npcsay(player, koftik, "foolish dwarfs, still believing that they can resist",
					"no one resists iban, go traveller",
					"the dwarfs to the south, they're not safe in the south",
					"we'll show them, go slay them m'lord",
					"he'll be so proud, that's all i want");
				say(player, koftik, "i'll pray for you");
				player.getCache().store("advised_koftik", true);
			}
		}
	}

	private void fallTeleportLocation(Player player, GameObject obj) {
		switch (obj.getID()) {
			case NORTH_STONE_STEP:
			case FIRST_REMAINING_BRIDGE:
				player.teleport(738, 584);
				break;
			case 898:
				player.teleport(756, 591);
				break;
			case 893:
				player.teleport(753, 608);
				break;
			case 892:
				player.teleport(734, 596);
				break;
			case 896:
				player.teleport(734, 610);
				break;
			case 910:
				player.teleport(734, 662);
				break;
			case 907:
				player.teleport(733, 646);
				break;
			case 906:
				player.teleport(731, 639);
				break;
			case 905:
			case 904:
				player.teleport(742, 630);
				break;
			case 908:
				player.teleport(760, 638);
				break;
			case 909:
				player.teleport(745, 656);
				break;
			case 902:
				player.teleport(759, 664);
				break;
			case 903:
				player.teleport(761, 613);
				break;
			case 901:
				player.teleport(727, 617);
				break;
			case 895:
				player.teleport(727, 618);
				break;
			case 899:
				player.teleport(734, 619);
				break;
			case 900:
				player.teleport(734, 666);
				break;
			case 894:
				player.teleport(763, 613);
				break;
			case 897:
				player.teleport(753, 585);
				break;
		}
	}
}
