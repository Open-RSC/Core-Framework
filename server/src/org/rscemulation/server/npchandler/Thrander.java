package org.rscemulation.server.npchandler;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.ShortEvent;

public class Thrander implements NpcHandler {

	public void handleNpc(final Npc npc, Player player) throws Exception {
      		player.informOfNpcMessage(new ChatMessage(npc, "Hello i'm thrander the smith, I'm an expert in armour modification", player));
      		player.setBusy(true);
      		World.getDelayedEventHandler().add(new ShortEvent(player) {
      			public void action() {
      				owner.informOfNpcMessage(new ChatMessage(npc, "Give me your armour designed for men and I can convert it", owner));
      				World.getDelayedEventHandler().add(new ShortEvent(owner) {
      					public void action() {
      						owner.setBusy(false);
      						owner.informOfNpcMessage(new ChatMessage(npc, "Into something more comfortable for a woman, and vice versa", owner));
      						npc.unblock();
      					}
      				});
      			}
      		});
      		npc.blockedBy(player);
	}
	
}
