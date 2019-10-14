package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ObjectWoodcuttingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.showBubble;

public class Woodcutting implements ObjectActionListener,
	ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(final GameObject obj,
									 final String command, final Player player) {
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(obj.getID());
		return (command.equals("chop") && def != null && obj.getID() != 245 && obj.getID() != 204);
	}

	private void handleWoodcutting(final GameObject object, final Player player,
								   final int click) {
		final ObjectWoodcuttingDef def = player.getWorld().getServer().getEntityHandler().getObjectWoodcuttingDef(object.getID());
		if (player.isBusy()) {
			return;
		}
		if (!player.withinRange(object, 2)) {
			return;
		}
		if (def == null) { // This shouldn't happen
			player.message("Nothing interesting happens");
			return;
		}
		if (def.getReqLevel() > 1 && !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.message(player.MEMBER_MESSAGE);
			return;
		}
		if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
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
			if (hasItem(player, a)) {
				axeId = a;
				break;
			}
		}
		if (axeId < 0) {
			player.playerServerMessage(MessageType.QUEST, "You need an axe to chop this tree down");
			return;
		}

		final int axeID = axeId;
		player.playerServerMessage(MessageType.QUEST, "You swing your " + player.getWorld().getServer().getEntityHandler().getItemDef(axeId).getName().toLowerCase() + " at the tree...");
		showBubble(player, new Item(axeId));
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1800, "Woodcutting", Formulae.getRepeatTimes(player, Skills.WOODCUT), true) {
			@Override
			public void action() {
				final Item log = new Item(def.getLogId());
				if (getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
						&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
						getOwner().playerServerMessage(MessageType.QUEST, "You are too tired to cut the tree");
						interrupt();
						return;
					}
				}
				if (getOwner().getSkills().getLevel(Skills.WOODCUT) < def.getReqLevel()) {
					getOwner().message("You need a woodcutting level of " + def.getReqLevel() + " to axe this tree");
					interrupt();
					return;
				}

				if (getLog(def.getReqLevel(), getOwner().getSkills().getLevel(Skills.WOODCUT), axeID)) {
					//check if the tree is still up
					GameObject obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
					if (obj == null) {
						getOwner().playerServerMessage(MessageType.QUEST, "You slip and fail to hit the tree");
						interrupt();
					} else {
						getOwner().getInventory().add(log);
						getOwner().playerServerMessage(MessageType.QUEST, "You get some wood");
						getOwner().incExp(Skills.WOODCUT, def.getExp(), true);
					}
					if (DataConversions.random(1, 100) <= def.getFell()) {
						obj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						int stumpId;
						if (def.getLogId() == ItemId.LOGS.id() || def.getLogId() == ItemId.MAGIC_LOGS.id()) {
							stumpId = 4; //narrow tree stump
						} else {
							stumpId = 314; //wide tree stump
						}
						interrupt();
						if (obj != null && obj.getID() == object.getID() && def.getRespawnTime() > 0) {
							GameObject newObject = new GameObject(getWorld(), object.getLocation(), stumpId, object.getDirection(), object.getType());
							getWorld().replaceGameObject(object, newObject);
							getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000);
						}
					}
				} else {
					getOwner().playerServerMessage(MessageType.QUEST, "You slip and fail to hit the tree");
					if (getRepeatFor() > 1) {
						GameObject checkObj = getOwner().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if (checkObj == null) {
							interrupt();
						}
					}
				}
				if (!isCompleted()) {
					getOwner().playerServerMessage(MessageType.QUEST, "You swing your " + getWorld().getServer().getEntityHandler().getItemDef(axeID).getName().toLowerCase() + " at the tree...");
					showBubble(getOwner(), new Item(axeID));
				}
			}
		});
	}

	@Override
	public void onObjectAction(final GameObject object, final String command, final Player player) {
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
