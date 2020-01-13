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
			public void executeInternal() {
				DoorDef def = object.getDoorDef();
				if (getPlayer().isBusy() || getPlayer().isRanging() || def == null
					|| getPlayer().getStatus() != Action.USING_DOOR) {
					getPlayer().message("NULL");
					return;
				}

				if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(
					getPlayer(),
					"WallObjectAction",
					new Object[]{object, click, getPlayer()}, this)) {
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
