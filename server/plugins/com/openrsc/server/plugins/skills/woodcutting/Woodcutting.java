package com.openrsc.server.plugins.skills.woodcutting;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.external.ObjectWoodcuttingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Woodcutting implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(final Player player, final GameObject obj,
							  final String command) {
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(obj.getID());
		return (command.equals("chop") && def != null && obj.getID() != 245 && obj.getID() != 204);
	}

	private void handleWoodcutting(final GameObject object, final Player player,
								   final int click) {
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(object.getID());
		/*if (player.isBusy()) {
			return;
		}*/
		if (!player.withinRange(object, 2)) {
			return;
		}
		if (def == null) { // This shouldn't happen
			player.message("Nothing interesting happens");
			return;
		}
		if (def.getReqLevel() > 1 && !config().MEMBER_WORLD) {
			player.message(player.MEMBER_MESSAGE);
			return;
		}
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.playerServerMessage(MessageType.QUEST, "You are too tired to cut the tree");
				return;
			}
		}
		if (player.getSkills().getLevel(Skills.WOODCUT) < def.getReqLevel()) {
			player.message("You need a woodcutting level of " + def.getReqLevel() + " to axe this tree");
			return;
		}
		int axeId = -1;
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

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Formulae.getRepeatTimes(player, Skills.WOODCUT);
		}

		startbatch(repeat);
		batchWoodcutting(player, object, def, axeId);
	}

	private void batchWoodcutting(Player player, GameObject object, ObjectWoodcuttingDef def, int axeId) {
		player.playerServerMessage(MessageType.QUEST, "You swing your " + player.getWorld().getServer().getEntityHandler().getItemDef(axeId).getName().toLowerCase() + " at the tree...");
		thinkbubble(player, new Item(axeId));
		delay(config().GAME_TICK * 3);

		final Item log = new Item(def.getLogId());
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.playerServerMessage(MessageType.QUEST, "You are too tired to cut the tree");
				return;
			}
		}
		if (player.getSkills().getLevel(Skills.WOODCUT) < def.getReqLevel()) {
			player.message("You need a woodcutting level of " + def.getReqLevel() + " to axe this tree");
			return;
		}

		if (getLog(def.getReqLevel(), player.getSkills().getLevel(Skills.WOODCUT), axeId)) {
			//check if the tree is still up
			GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
			if (obj == null) {
				player.playerServerMessage(MessageType.QUEST, "You slip and fail to hit the tree");
			} else {
				player.getCarriedItems().getInventory().add(log);
				player.playerServerMessage(MessageType.QUEST, "You get some wood");
				player.incExp(Skills.WOODCUT, def.getExp(), true);
			}
			if (DataConversions.random(1, 100) <= def.getFell()) {
				obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				int stumpId;
				if (def.getLogId() == ItemId.LOGS.id() || def.getLogId() == ItemId.MAGIC_LOGS.id()) {
					stumpId = 4; //narrow tree stump
				} else {
					stumpId = 314; //wide tree stump
				}
				if (obj != null && obj.getID() == object.getID() && def.getRespawnTime() > 0) {
					GameObject newObject = new GameObject(player.getWorld(), object.getLocation(), stumpId, object.getDirection(), object.getType());
					player.getWorld().replaceGameObject(object, newObject);
					player.getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000);
				}
				return;
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "You slip and fail to hit the tree");
			if (!ifbatchcompleted()) {
				GameObject checkObj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (checkObj == null) {
					return;
				}
			}
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !ifbatchcompleted()) {
			delay(config().GAME_TICK);
			batchWoodcutting(player, object, def, axeId);
		}
	}

	@Override
	public void onOpLoc(final Player player, final GameObject object, final String command) {
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(object.getID());
		if (command.equals("chop") && def != null && object.getID() != 245 && object.getID() != 204) {
			handleWoodcutting(object, player, player.click);
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
				axeBonus = 0;
				break;
		}
		return axeBonus;
	}

	/**
	 * Should we get a log from the tree?
	 */
	private boolean getLog(int reqLevel, int woodcutLevel, int axeId) {
		return Formulae.calcGatheringSuccessful(reqLevel, woodcutLevel, calcAxeBonus(axeId));
	}
}
