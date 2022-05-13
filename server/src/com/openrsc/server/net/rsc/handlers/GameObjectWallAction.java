package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.DoorDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToObjectAction;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetObjectStruct;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;

public class GameObjectWallAction implements PayloadProcessor<TargetObjectStruct, OpcodeIn> {

	public void process(TargetObjectStruct payload, Player player) throws Exception {

		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}

		if (player.getDuel().isDueling()) {
			return;
		}

		if (player.isBusy()) {
			player.resetPath();
			return;
		}

		player.resetAll();

		final OpcodeIn pID = payload.getOpcode();

		if (pID == OpcodeIn.INTERACT_WITH_BOUNDARY) {
			player.click = 0;
		}
		else if (pID == OpcodeIn.INTERACT_WITH_BOUNDARY2) {
			player.click = 1;
		}
		else return;

		final int x = payload.coordObject.getX();
		final int y = payload.coordObject.getY();
		final int dir = payload.direction;
		if (x < 0 || y < 0 || dir < 0) {
			player.setSuspiciousPlayer(true, "bad game object wall coordinates");
			return;
		}

		final GameObject object = player.getViewArea().getWallObjectWithDir(Point.location(x, y), dir);
		if (object == null) {
			player.setSuspiciousPlayer(true, "game object wall has null object");
			return;
		}

		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void executeInternal() {
				DoorDef def = object.getDoorDef();
				if (getPlayer().isBusy() || getPlayer().isRanging() || def == null) {
					/*getPlayer().message(
						"Busy: " + getPlayer().isBusy() +
						" Ranging: " + getPlayer().isRanging() +
						" Status: " + getPlayer().getStatus()
					);*/
					return;
				}

				if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(
						OpBoundTrigger.class,
						getPlayer(),
						new Object[]{getPlayer(), object, getPlayer().click}, this)) {
					return;
				}

				getPlayer().resetAll();
				String command = (getPlayer().click == 0 ? def.getCommand1() : def
					.getCommand2()).toLowerCase();
				Point telePoint = getPlayer().getWorld().getServer().getEntityHandler().getObjectTelePoint(
					object.getLocation(), command);
				if (telePoint != null) {
					getPlayer().teleport(telePoint.getX(), telePoint.getY(), false);
				}
			}
		});
	}
}
