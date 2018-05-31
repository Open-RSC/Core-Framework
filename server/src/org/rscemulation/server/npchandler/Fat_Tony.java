package org.rscemulation.server.npchandler;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.ShortEvent;

public class Fat_Tony implements NpcHandler {

	public void handleNpc(final Npc npc, Player player) throws Exception {
      		player.informOfNpcMessage(new ChatMessage(npc, "Would you like to buy some pizza dough? Only 10 gold.", player));
      		player.setBusy(true);
      		World.getDelayedEventHandler().add(new ShortEvent(player) {
      			public void action() {
      				owner.setBusy(false);
				String[] options = new String[]{"No thank you", "Yes please"};
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if(owner.isBusy()) {
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
						owner.setBusy(true);
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								owner.setBusy(false);
								if(option == 1) {
									if(owner.getInventory().remove(10, 10) > -1) {
										owner.sendMessage("You buy some pizza dough");
										owner.getInventory().add(new InvItem(321, 1));
										owner.sendInventory();
										npc.unblock();
									}
									else {
										owner.informOfChatMessage(new ChatMessage(owner, "Oops I forgot to bring any money with me", npc));
										owner.setBusy(true);
										World.getDelayedEventHandler().add(new ShortEvent(owner) {
											public void action() {
												owner.setBusy(false);
												owner.informOfNpcMessage(new ChatMessage(npc, "Come back when you have some", owner));
												npc.unblock();
											}
										});
									}
								}
								else {
									npc.unblock();
								}
							}
						});
					}
				});
				owner.sendMenu(options);
      			}
      		});
      		npc.blockedBy(player);
	}
	
}
