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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameObjectAction implements PacketHandler {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

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
			public void executeInternal() {
				getPlayer().resetPath();
				GameObjectDef def = object.getGameObjectDef();
				if (getPlayer().isBusy() || !getPlayer().atObject(object) || getPlayer().isRanging() || def == null
					|| getPlayer().getStatus() != Action.USING_OBJECT) {
					return;
				}

				getPlayer().resetAll();
				String command = (click == 0 ? def.getCommand1() : def
					.getCommand2()).toLowerCase();

				int playerDirection = getPlayer().getSprite();
				if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(
					getPlayer(),
					"ObjectAction",
					new Object[]{object, command, getPlayer()},
					this)) {
					getPlayer().setSprite(playerDirection);
					return;
				}
			}
		});
	}
}
