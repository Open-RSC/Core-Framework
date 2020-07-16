package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class MagicGuildPortals implements OpBoundTrigger {

	private static int[] MAGIC_PORTALS = {147, 148, 149};

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return inArray(obj.getID(), MAGIC_PORTALS);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (inArray(obj.getID(), MAGIC_PORTALS)) {
			player.playerServerMessage(MessageType.QUEST, "you enter the magic portal");
			if (obj.getID() == MAGIC_PORTALS[0]) {
				player.teleport(212, 695);
			} else if (obj.getID() == MAGIC_PORTALS[1]) {
				player.teleport(511, 1452);
			} else if (obj.getID() == MAGIC_PORTALS[2]) {
				player.teleport(362, 1515);
			}
			delay();
			displayTeleportBubble(player, player.getX(), player.getY(), false);
		}
	}
}
