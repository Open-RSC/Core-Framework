package com.openrsc.server.plugins.misc;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class MagicGuildPortals implements WallObjectActionListener, WallObjectActionExecutiveListener {

	private static int[] MAGIC_PORTALS = {147, 148, 149};

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return inArray(obj.getID(), MAGIC_PORTALS);
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (inArray(obj.getID(), MAGIC_PORTALS)) {
			p.playerServerMessage(MessageType.QUEST, "you enter the magic portal");
			if (obj.getID() == MAGIC_PORTALS[0]) {
				p.teleport(212, 695);
			} else if (obj.getID() == MAGIC_PORTALS[1]) {
				p.teleport(511, 1452);
			} else if (obj.getID() == MAGIC_PORTALS[2]) {
				p.teleport(362, 1515);
			}
			sleep(600);
			displayTeleportBubble(p, p.getX(), p.getY(), false);
		}
	}
}
