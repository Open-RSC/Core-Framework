package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.DoorDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToObjectAction;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public class GameObjectWallAction implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		int pID = p.getID();
		int packetTwo = OpcodeIn.WALL_OBJECT_COMMAND1.getOpcode();

		if (player.isBusy()) {
			player.resetPath();
			return;
		}

		player.resetAll();
		final GameObject object = player.getViewArea().getWallObjectWithDir(Point.location(p.readShort(), p.readShort()), p.readByte());
		final int click = pID == packetTwo ? 0 : 1;
		if (object == null) {
			player.setSuspiciousPlayer(true, "game object wall has null object");
			return;
		}
		player.setStatus(Action.USING_DOOR);
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void execute() {
				DoorDef def = object.getDoorDef();
				if (player.isBusy() || player.isRanging() || def == null
					|| player.getStatus() != Action.USING_DOOR) {
					player.message("NULL");
					return;
				}

				if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(
					"WallObjectAction",
					new Object[]{object, click, player})) {
					return;
				}

				player.resetAll();
				String command = (click == 0 ? def.getCommand1() : def
					.getCommand2()).toLowerCase();
				Point telePoint = player.getWorld().getServer().getEntityHandler().getObjectTelePoint(
					object.getLocation(), command);
				if (telePoint != null) {
					player.teleport(telePoint.getX(), telePoint.getY(), false);
				}
			}
		});
	}
}
