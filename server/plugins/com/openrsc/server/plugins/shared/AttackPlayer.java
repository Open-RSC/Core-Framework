package com.openrsc.server.plugins.shared;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;

import static com.openrsc.server.plugins.Functions.*;

public class AttackPlayer {
	public static boolean attackPrevented(Player player, Player affectedMob) {
		boolean prevented = false;
		if (player.getConfig().USES_PK_MODE) {
			if (affectedMob.getLocation().isInBank(player.getConfig().BASED_MAP_DATA)) {
				player.message("You cannot attack other players inside the bank");
				prevented = true;
			}

			Npc guard = ifnearvisnpc_(player, NpcId.GUARD.id(), 3);
			if (guard != null) {
				guard.getUpdateFlags().setChatMessage(new ChatMessage(guard, "Hey! No fighting!", player));
				delay(2);
				guard.startCombat(player);
				prevented = true;
			}
		}
		return prevented;
	}

	// don't ask
	private static Npc ifnearvisnpc_(Player player, int npcId, int radius) {
		final Iterable<Npc> npcsInView = player.getViewArea().getNpcsInView();
		Npc closestNpc = null;
		for (int next = 0; next < radius; next++) {
			for (final Npc n : npcsInView) {
				if (n.getID() == npcId && n.withinRange(player.getLocation(), next) && !n.isBusy()) {
					closestNpc = n;
				}
			}
		}
		return closestNpc;
	}
}
