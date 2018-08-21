package org.openrsc.server.npchandler;

import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.*;

public class Master_Crafter implements NpcHandler {
	public void handleNpc(final Npc npc, Player player) throws Exception {
        player.informOfNpcMessage(new ChatMessage(npc, "Hello welcome to the Crafter's guild", player));
	    String[] messages1 = {"Accomplished crafters all over the land come here","All to use our top notch workshops"};
        player.setBusy(true);
        npc.blockedBy(player);
        World.getDelayedEventHandler().add(new DelayedQuestChat(npc,player,messages1) {
            public void finished()
            {
                player.setBusy(false);
                npc.unblock();
            }
        });
		}
	}
