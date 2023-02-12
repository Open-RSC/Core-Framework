package com.openrsc.server.plugins.authentic.skills.woodcutting;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.EnchantedCrowns;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.external.ObjectWoodcuttingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Woodcutting implements OpLocTrigger, UseLocTrigger {

	@Override
	public boolean blockOpLoc(final Player player, final GameObject obj,
							  final String command) {
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(obj.getID());
		return (command.equals("chop") && def != null && obj.getID() != 245);
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
		if (player.getSkills().getLevel(Skill.WOODCUTTING.id()) < def.getReqLevel()) {
			player.message("You need a woodcutting level of " + def.getReqLevel() + " to axe this tree");
			return;
		}

		// determine axe, highest tier axes are authentically searched for first
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
			repeat = Formulae.getRepeatTimes(player, Skill.WOODCUTTING.id());
		}

		startbatch(repeat);
		batchWoodcutting(player, object, def, axeId);
	}

	private void batchWoodcutting(Player player, GameObject object, ObjectWoodcuttingDef def, int axeId) {
		player.playerServerMessage(MessageType.QUEST, "You swing your " + player.getWorld().getServer().getEntityHandler().getItemDef(axeId).getName().toLowerCase() + " at the tree...");
		thinkbubble(new Item(axeId));
		delay(3);

		final Item log = new Item(def.getLogId());
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.playerServerMessage(MessageType.QUEST, "You are too tired to cut the tree");
				return;
			}
		}
		if (player.getSkills().getLevel(Skill.WOODCUTTING.id()) < def.getReqLevel()) {
			player.message("You need a woodcutting level of " + def.getReqLevel() + " to axe this tree");
			return;
		}

		// New trees update; map32 introduced new trees & made woodcut xp no longer be scaled
		boolean isOldWoodcut = (player.getConfig().SCALED_WOODCUT_XP || player.getConfig().BASED_MAP_DATA < 32) && def.getLogId() == ItemId.LOGS.id();
		if ((!isOldWoodcut && getLog(def, player.getSkills().getLevel(Skill.WOODCUTTING.id()), axeId))
			|| (isOldWoodcut && Formulae.chopLogs(player.getSkills().getLevel(Skill.WOODCUTTING.id())))) {
			//check if the tree is still up
			GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
			if (!player.getConfig().SHARED_GATHERING_RESOURCES || obj != null) {
				player.getCarriedItems().getInventory().add(log);
				player.playerServerMessage(MessageType.QUEST, "You get some wood");
				if (isOldWoodcut) {
					player.incExp(Skill.WOODCUTTING.id(), getExpRetro(player.getSkills().getMaxStat(Skill.WOODCUTTING.id()), 25), true);
				} else {
					player.incExp(Skill.WOODCUTTING.id(), def.getExp(), true);
				}

				if (EnchantedCrowns.shouldActivate(player, ItemId.CROWN_OF_THE_ITEMS)) {
					player.playerServerMessage(MessageType.QUEST, "Your crown shines and an extra item appears on the ground");
					player.getWorld().registerItem(
						new GroundItem(player.getWorld(), log.getCatalogId(), player.getX(), player.getY(), 1, player), player.getConfig().GAME_TICK * 50);
					EnchantedCrowns.useCharge(player, ItemId.CROWN_OF_THE_ITEMS);
				}
			} else {
				player.playerServerMessage(MessageType.QUEST, "You slip and fail to hit the tree");
			}
			if (DataConversions.random(1, 100) <= def.getFell() && !woodcuttingSkillcape(player)) {
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
			if (!isbatchcomplete()) {
				GameObject checkObj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (checkObj == null) {
					return;
				}
			}
		}

		// If tree has felled, stop the batch.
		GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
		if (obj == null) {
			stopbatch();
			return;
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchWoodcutting(player, object, def, axeId);
		}
	}

	@Override
	public void onOpLoc(final Player player, final GameObject object, final String command) {
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(object.getID());
		if (command.equals("chop") && def != null && object.getID() != 245) {
			if (player.getConfig().GATHER_TOOL_ON_SCENERY) {
				player.playerServerMessage(MessageType.QUEST, "You need to use the axe on the tree to chop it");
				return;
			}
			handleWoodcutting(object, player, player.click);
		}
	}

	/**
	 * Should we get a log from the tree?
	 */
	public boolean getLog(ObjectWoodcuttingDef def, int woodcutLevel, int axeId) {
		double roll = Math.random();
		return def.getRate(woodcutLevel, axeId) > roll;
	}

	public static int getExpRetro(int level, int baseExp) {
		return (int) ((baseExp + (level * 1.75)) * 4);
	}

	@Override
	public void onUseLoc(Player player, GameObject object, Item item) {
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(object.getID());
		if (inArray(item.getCatalogId(), Formulae.woodcuttingAxeIDs) && (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickWoodcut)
			&& def != null && object.getID() != 245) {
			handleWoodcutting(object, player, 0);
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(obj.getID());
		return (inArray(item.getCatalogId(), Formulae.woodcuttingAxeIDs) && (player.getConfig().GATHER_TOOL_ON_SCENERY || !player.getClientLimitations().supportsClickWoodcut)
			&& def != null && obj.getID() != 245);
	}

	private boolean woodcuttingSkillcape(final Player player) {
		if (SkillCapes.shouldActivate(player, ItemId.WOODCUTTING_CAPE)) {
			mes("@gre@Your woodcutting cape prevents the tree from falling");
			return true;
		}
		return false;
	}
}
