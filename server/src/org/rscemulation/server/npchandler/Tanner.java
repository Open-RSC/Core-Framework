package org.rscemulation.server.npchandler;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.DelayedEvent;
import org.rscemulation.server.event.ShortEvent;

public class Tanner implements NpcHandler {

	public void handleNpc(final Npc npc, Player player) throws Exception {
      		player.informOfNpcMessage(new ChatMessage(npc, "Greeting friend i'm a manufacturer of leather", player));
      		player.setBusy(true);
		World.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[]{"Can I buy some leather then?", "Here's some cow hides, can I buy some leather now?", "Leather is rather weak stuff"};
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if(owner.isBusy()) {
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
						owner.setBusy(true);
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								switch(option) {
									case 0:
										owner.informOfNpcMessage(new ChatMessage(npc, "I make leather from cow hides", owner));
										World.getDelayedEventHandler().add(new ShortEvent(owner) {
											public void action() {
												owner.setBusy(false);
												owner.informOfNpcMessage(new ChatMessage(npc, "Bring me some of them and a gold coin per hide", owner));
												npc.unblock();
											}
										});
										break;
									case 1:
										owner.informOfNpcMessage(new ChatMessage(npc, "Ok", owner));
										World.getDelayedEventHandler().add(new DelayedEvent(owner, 500) {
											public void run() {
												InvItem hides = owner.getInventory().get(owner.getInventory().getLastIndexById(147));
												if(hides == null) {
													owner.sendMessage("You have run out of cow hides");
													running = false;
													owner.setBusy(false);
												}
												else if(owner.getInventory().countId(10) < 1) {
													owner.sendMessage("You have run out of coins");
													running = false;
													owner.setBusy(false);
												}
												else if(owner.getInventory().remove(hides) > -1 && owner.getInventory().remove(10, 1) > -1) {
													owner.getInventory().add(new InvItem(148, 1));
													owner.sendInventory();
												}
												else {
													running = false;
													owner.setBusy(false);
												}
											}
										});
										npc.unblock();
										break;
									case 2:
										owner.setBusy(false);
										owner.informOfNpcMessage(new ChatMessage(npc, "Well yes if all you're concerned with is how much it will protect you in a fight", owner));
										npc.unblock();
										break;
									default:
										owner.setBusy(false);
										npc.unblock();
										break;
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
