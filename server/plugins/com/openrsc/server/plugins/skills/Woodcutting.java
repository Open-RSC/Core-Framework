package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.ObjectWoodcuttingDef;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.showBubble;

public class Woodcutting implements ObjectActionListener,
	ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(final GameObject obj,
									 final String command, final Player player) {
		final ObjectWoodcuttingDef def = EntityHandler.getObjectWoodcuttingDef(obj.getID());
		return (command.equals("chop") && def != null && obj.getID() != 245 && obj.getID() != 204);
	}

	private void handleWoodcutting(final GameObject object, final Player owner,
								   final int click) {
		final ObjectWoodcuttingDef def = EntityHandler
			.getObjectWoodcuttingDef(object.getID());
		if (owner.isBusy()) {
			return;
		}
		if (!owner.withinRange(object, 2)) {
			return;
		}
		if (def == null) { // This shoudln't happen
			owner.message("Nothing interesting happens");
			return;
		}
		if (def.getReqLevel() > 1 && !Constants.GameServer.MEMBER_WORLD) {
			owner.message(owner.MEMBER_MESSAGE);
			return;
		}
		if (Constants.GameServer.WANT_FATIGUE) {
			if (owner.getFatigue() >= owner.MAX_FATIGUE) {
				owner.message("You are too tired to cut the tree");
				return;
			}
		}
		if (owner.getSkills().getLevel(Skills.WOODCUT) < def.getReqLevel()) {
			owner.message("You need a woodcutting level of " + def.getReqLevel() + " to axe this tree");
			return;
		}
		int axeId = -1;
		for (final int a : Formulae.woodcuttingAxeIDs) {
			if (owner.getInventory().countId(a) > 0) {
				axeId = a;
				break;
			}
		}
		if (axeId < 0) {
			owner.message("You need an axe to chop this tree down");
			return;
		}

		final int axeID = axeId;
		owner.message("You swing your " + EntityHandler.getItemDef(axeId).getName().toLowerCase() + " at the tree...");
		showBubble(owner, new Item(axeId));
		owner.setBatchEvent(new BatchEvent(owner, 1800, 1000, true) {
			public void action() {
				final Item log = new Item(def.getLogId());
				if (Constants.GameServer.WANT_FATIGUE) {
					if (owner.getFatigue() >= owner.MAX_FATIGUE) {
						owner.message("You are too tired to cut the tree");
						interrupt();
						return;
					}
				}

				if (getLog(def.getReqLevel(), owner.getSkills().getLevel(Skills.WOODCUT), axeID)) {
					//check if the tree is still up
					GameObject obj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
					if (obj == null) {
						owner.message("You slip and fail to hit the tree");
						interrupt();
					} else {
						owner.getInventory().add(log);
						owner.message("You get some wood");
						owner.incExp(Skills.WOODCUT, (int) def.getExp(), true);
					}
					if (DataConversions.random(1, 100) <= def.getFell()) {
						int stumpId;
						if (def.getLogId() == ItemId.LOGS.id() || def.getLogId() == ItemId.MAGIC_LOGS.id()) {
							stumpId = 4; //narrow tree stump
						} else {
							stumpId = 314; //wide tree stump
						}
						
						interrupt();
						if (obj != null && obj.getID() == object.getID()) {
							World.getWorld().replaceGameObject(object, new GameObject(object.getLocation(), stumpId, object.getDirection(), object.getType()));
							World.getWorld().delayedSpawnObject(object.getLoc(), def
								.getRespawnTime() * 1000);
						}
					}
				} else {
					owner.message("You slip and fail to hit the tree");
					if (getRepeatFor() > 1) {
						GameObject checkObj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if (checkObj == null) {
							interrupt();
						}
					}
				}
				if (!isCompleted() && !owner.getInventory().full()) {
					showBubble(owner, new Item(axeID));
					owner.message("You swing your " + EntityHandler.getItemDef(axeID).getName().toLowerCase() + " at the tree...");
				}
			}
		});
	}

	@Override
	public void onObjectAction(final GameObject object, final String command, final Player owner) {
		final ObjectWoodcuttingDef def = EntityHandler.getObjectWoodcuttingDef(object.getID());
		if (command.equals("chop") && def != null && object.getID() != 245 && object.getID() != 204) {
			handleWoodcutting(object, owner, owner.click);
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
