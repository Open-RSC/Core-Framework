package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.GameObjectDef;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToObjectAction;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public class GameObjectAction implements PacketHandler {

	public void handlePacket(Packet p, Player player) {
		int pID = p.getID();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final GameObject object = player.getViewArea().getGameObject(Point.location(p.readShort(), p.readShort()));

		final int click = pID == OpcodeIn.OBJECT_COMMAND1.getOpcode() ? 0 : 1;
		player.click = click;
		if (object == null) {
			player.setSuspiciousPlayer(true, "game object action null object");
			return;
		}
		player.setStatus(Action.USING_OBJECT);
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void execute() {
				player.resetPath();
				GameObjectDef def = object.getGameObjectDef();
				if (player.isBusy() || !player.atObject(object) || player.isRanging() || def == null
					|| player.getStatus() != Action.USING_OBJECT) {
					return;
				}

				player.resetAll();
				String command = (click == 0 ? def.getCommand1() : def
					.getCommand2()).toLowerCase();
				if(!command.equalsIgnoreCase("chop")){
					player.face(object.getX(), object.getY());
				}
				if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(
					"ObjectAction",
					new Object[]{object, command, player})) {

					return;
				}
			}
		});
	}
}
