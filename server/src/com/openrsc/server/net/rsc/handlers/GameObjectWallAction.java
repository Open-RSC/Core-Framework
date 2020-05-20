package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.DoorDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToObjectAction;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public class GameObjectWallAction implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception {

		int pID = packet.getID();
		int packetTwo = OpcodeIn.WALL_OBJECT_COMMAND1.getOpcode();

		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}
		if (player.isBusy()) {
			player.resetPath();
			return;
		}

		player.resetAll();
		final GameObject object = player.getViewArea().getWallObjectWithDir(Point.location(packet.readShort(), packet.readShort()), packet.readByte());
		final int click = pID == packetTwo ? 0 : 1;
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
					getPlayer(),
					"OpBound",
					new Object[]{getPlayer(), object, click}, this)) {
					return;
				}

				getPlayer().resetAll();
				String command = (click == 0 ? def.getCommand1() : def
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
