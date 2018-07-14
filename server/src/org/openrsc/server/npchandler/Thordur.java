package org.openrsc.server.npchandler;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.event.ShortEvent;

public class Thordur implements NpcHandler {
	public void handleNpc(final Npc npc, Player player) throws Exception {
      		player.informOfNpcMessage(new ChatMessage(npc, "Would you like to see a black hole?", player));
      		player.informOfNpcMessage(new ChatMessage(npc, "I will sell you the key for only 10 gold coins", player));
      		player.setBusy(true);
      		World.getDelayedEventHandler().add(new ShortEvent(player) {
      			public void action() {
      				owner.setBusy(false);
				String[] options = new String[]{"Sounds scary. no", "Sounds like an adventure. Yes please!"};
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
									if(owner.getInventory().remove(10, 10) > -10) {
										owner.getActionSender().sendMessage("Thordur hands you a Disk of Returning");
										owner.getInventory().add(new InvItem(387, 1));
										owner.getActionSender().sendInventory();
										npc.unblock();
									}
									else {
										owner.informOfChatMessage(new ChatMessage(owner, "Oops, I forgot to bring money with me.", npc));
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
				owner.getActionSender().sendMenu(options);
      			}
      		});
      		npc.blockedBy(player);
	}
	
}