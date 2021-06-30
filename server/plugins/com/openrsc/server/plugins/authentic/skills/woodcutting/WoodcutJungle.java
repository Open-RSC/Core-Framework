package com.openrsc.server.plugins.authentic.skills.woodcutting;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseBoundTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class WoodcutJungle implements OpLocTrigger,
	OpBoundTrigger, UseLocTrigger, UseBoundTrigger {

	private static int[] JUNGLE_TREES = {1086, 1100, 1099, 1092, 1091};

	private static int JUNGLE_VINE = 204;

	private static int JUNGLE_TREE_STUMP = 1087;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), JUNGLE_TREES) || obj.getID() == JUNGLE_TREE_STUMP;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (inArray(obj.getID(), JUNGLE_TREES)) {
			handleJungleWoodcut(obj, player);
		}
		if (obj.getID() == JUNGLE_TREE_STUMP) {
			player.teleport(obj.getX(), obj.getY());
		}
	}

	private void handleJungleWoodcut(GameObject obj, Player player) {
		if (config().STOP_SKILLING_FATIGUED >= 1
			&& player.getFatigue() >= player.MAX_FATIGUE) {
			player.playerServerMessage(MessageType.QUEST, "You are too tired to cut the " + (obj.getID() == JUNGLE_VINE ? "jungle vines" : "tree"));

			// Shilo side of the jungle
			if (player.getY() < 866) {
				return;
			} else if (player.getY() >= 866) { // Khazari side, force out.
				player.message("It takes you some time, but you eventually make your way");
				player.message("out of the Khazari jungle.");
				cutJungle(0, obj, player, true);
				return;
			}
		}

		//if(!hasItem(p, 1163) && !hasItem(p, 1233) && p.getQuestStage(Quests.LEGENDS_QUEST) != -1) { // the radimus scrolls.
		//	message(p, p.getConfig().GAME_TICK * 3, "This jungle is far too thick, you'll need a special map to go further.");
		//	return;
		//}

		if (getCurrentLevel(player, Skill.WOODCUTTING.id()) < 50) {
			player.message("You need a woodcutting level of 50 to axe this tree");
			return;
		}

		if (!player.getCarriedItems().hasCatalogID(ItemId.MACHETTE.id(), Optional.of(false))) {
			mes("This jungle is very thick, you'll need a machette to cut through.");
			delay(3);
			return;
		}

		int axeId = -1;
		if (obj.getID() != JUNGLE_VINE) {
			for (final int a : Formulae.woodcuttingAxeIDs) {
				if (player.getCarriedItems().hasCatalogID(a, Optional.of(false))) {
					axeId = a;
					break;
				}
			}
			if (axeId < 0) {
				player.playerServerMessage(MessageType.QUEST, "You need an axe to chop this tree down");
				return;
			}
		} else {
			axeId = ItemId.MACHETTE.id();
		}

		thinkbubble(new Item(axeId));
		player.playerServerMessage(MessageType.QUEST, "You swing your " + player.getWorld().getServer().getEntityHandler().getItemDef(axeId).getName().toLowerCase() + " at the " + (obj.getID() == JUNGLE_VINE ? "jungle vines" : "tree") + "...");
		if (player.getFatigue() >= 149840) {
			if (player.getFatigue() < player.MAX_FATIGUE) {
				player.message("You are getting very tired, you may get stuck if you continue into the jungle.");
			}
		}

		cutJungle(axeId, obj, player, false);
	}

	private void cutJungle(int axeId, GameObject obj, Player player, boolean force) {
		if (force || getLog(50, player.getSkills().getLevel(Skill.WOODCUTTING.id()), axeId)) {
			GameObject jungleObject = player.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
			if (jungleObject != null && jungleObject.getID() == obj.getID()) {
				if (obj.getID() == JUNGLE_VINE) {
					player.getWorld().unregisterGameObject(jungleObject);
					player.getWorld().delayedSpawnObject(obj.getLoc(), 5500); // 5.5 seconds.
					if (!force)
						// authentic does not send to quest tab
						mes("You hack your way through the jungle.");
						delay(2);
				} else {
					player.getWorld().replaceGameObject(obj, new GameObject(obj.getWorld(), obj.getLocation(), JUNGLE_TREE_STUMP, obj.getDirection(), obj.getType()));
					player.getWorld().delayedSpawnObject(obj.getLoc(), 60 * 1000); // 1 minute.
				}

				if (!force)
					player.incExp(Skill.WOODCUTTING.id(), 20, true);
			}
			if (DataConversions.random(0, 10) == 8) {
				final Item log = new Item(ItemId.LOGS.id());
				player.getCarriedItems().getInventory().add(log);
				player.playerServerMessage(MessageType.QUEST, "You get some wood");
			}
			player.teleport(obj.getX(), obj.getY());
			if (player.getY() > 871) {
				if (obj.getID() == JUNGLE_VINE)
					delay(6);
				player.message("You manage to hack your way into the Kharazi Jungle.");
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "You slip and fail to hit the " + (obj.getID() == JUNGLE_VINE ? "jungle vines" : "tree"));
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return obj.getID() == JUNGLE_VINE;
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == JUNGLE_VINE) {
			handleJungleWoodcut(obj, player);
		}
	}

	/**
	 * How much of a bonus does the woodcut axe give?
	 */
	public int calcAxeBonus(int axeId) {
		int axeBonus = 0;
		switch (ItemId.getById(axeId)) {
			case BRONZE_AXE:
				axeBonus = 0;
				break;
			case IRON_AXE:
				axeBonus = 1;
				break;
			case STEEL_AXE:
				axeBonus = 2;
				break;
			case BLACK_AXE:
				axeBonus = 3;
				break;
			case MITHRIL_AXE:
				axeBonus = 4;
				break;
			case ADAMANTITE_AXE:
				axeBonus = 8;
				break;
			case RUNE_AXE:
				axeBonus = 16;
				break;
			default:
				break;
		}
		return axeBonus;
	}

	/**
	 * Should we get a log from the tree?
	 */
	private boolean getLog(int reqLevel, int woodcutLevel, int axeId) {
		return Formulae.calcGatheringSuccessfulLegacy(reqLevel, woodcutLevel, calcAxeBonus(axeId));
	}

	@Override
	public void onUseLoc(Player player, GameObject object, Item item) {
		if (inArray(item.getCatalogId(), Formulae.woodcuttingAxeIDs)
			&& (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickWoodcut)) {
			if (inArray(object.getID(), JUNGLE_TREES)) {
				handleJungleWoodcut(object, player);
			}
			if (object.getID() == JUNGLE_TREE_STUMP) {
				player.teleport(object.getX(), object.getY());
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return inArray(item.getCatalogId(), Formulae.woodcuttingAxeIDs)
			&& (inArray(obj.getID(), JUNGLE_TREES) || obj.getID() == JUNGLE_TREE_STUMP)
			&& (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickWoodcut);
	}

	@Override
	public void onUseBound(Player player, GameObject object, Item item) {
		if (inArray(item.getCatalogId(), Formulae.woodcuttingAxeIDs)
			&& (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickWoodcut)) {
			if (object.getID() == JUNGLE_VINE) {
				handleJungleWoodcut(object, player);
			}
		}
	}

	@Override
	public boolean blockUseBound(Player player, GameObject obj, Item item) {
		return inArray(item.getCatalogId(), Formulae.woodcuttingAxeIDs) && (obj.getID() == JUNGLE_VINE)
			&& (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickWoodcut);
	}
}
