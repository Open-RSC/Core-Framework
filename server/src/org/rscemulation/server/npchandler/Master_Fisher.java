package org.rscemulation.server.npchandler;

import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;

public class Master_Fisher implements NpcHandler {
	public void handleNpc(final Npc npc, Player player) throws Exception {
		if (player.getInventory().canHold(1)) {
			player.setBusy(true);
			npc.blockedBy(player);
			int id = World.isP2PWilderness() ? 367 : 357;
			while (player.getInventory().size() < 30)
				player.getInventory().add(new InvItem(id, 1));
			player.informOfNpcMessage(new ChatMessage(npc, "Have some free fish...", player));
			player.sendInventory();
			player.setBusy(false);
			npc.unblock();
		}
	}
}