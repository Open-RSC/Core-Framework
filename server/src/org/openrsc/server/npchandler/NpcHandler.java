package org.openrsc.server.npchandler;

import org.openrsc.server.model.Player;
import org.openrsc.server.model.Npc;

public interface NpcHandler {
	public void handleNpc(final Npc npc, Player player) throws Exception;
}
