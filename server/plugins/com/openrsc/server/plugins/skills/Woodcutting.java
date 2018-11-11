package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ObjectWoodcuttingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
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
		if (command.equals("chop") && def != null && obj.getID() != 245 && obj.getID() != 204) {
			return true;
		}
		return false;
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
		if(def.getReqLevel() > 1 && !Constants.GameServer.MEMBER_WORLD) {
			owner.message(owner.MEMBER_MESSAGE);
			return;
		}
		if (owner.getFatigue() >= owner.MAX_FATIGUE) {
			owner.message("You are too tired to cut the tree");
			return;
		}
		if (owner.getSkills().getLevel(8) < def.getReqLevel()) {
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
		int batchTimes = 1;
		switch (axeId) {
		case 87:
			batchTimes = 1;
			break;
		case 12:
			batchTimes = 2;
			break;
		case 88:
			batchTimes = 3;
			break;
		case 428:
			batchTimes = 4;
			break;
		case 203:
			batchTimes = 5;
			break;
		case 204:
			batchTimes = 8;
		case 405:
			batchTimes = 12;
			break;
		}
		
		final int axeID = axeId;
		showBubble(owner, new Item(axeId));
		owner.message("You swing your " + EntityHandler.getItemDef(axeId).getName().toLowerCase() + " at the tree...");
		owner.setBatchEvent(new BatchEvent(owner, 1800, batchTimes) {
			public void action() {
				if (owner.getFatigue() >= owner.MAX_FATIGUE) {
					owner.message("You are too tired to cut the tree");
					interrupt();
					return;
				}
				if (Formulae.getLog(def, owner.getSkills().getLevel(8), axeID)) {
					final Item log = new Item(def.getLogId());
					if(!owner.getInventory().full()) 
						owner.getInventory().add(log);
					else 
						World.getWorld().registerItem(new GroundItem(log.getID(), owner.getX(),
								owner.getY(), log.getAmount(), owner));
					owner.message("You get some wood");
					owner.incExp(8, (int) def.getExp(), true);
					if (DataConversions.random(1, 100) <= def.getFell()) {
						interrupt();
						GameObject obj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if(obj != null && obj.getID() == object.getID()) {
							World.getWorld().replaceGameObject(object, new GameObject(object.getLocation(), 4, object.getDirection(), object.getType()));
							World.getWorld().delayedSpawnObject(object.getLoc(), def
									.getRespawnTime() * 1000);
						}
					}
				} else {
					owner.message("You slip and fail to hit the tree");
					if(getRepeatFor() > 1) {
						GameObject checkObj = owner.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
						if(checkObj == null) {
							interrupt();
						}
					}
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
}
