package org.rscemulation.server.npchandler;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.DelayedQuestChat;
import org.rscemulation.server.event.ShortEvent;

public class Monk implements NpcHandler {

	public void handleNpc(final Npc npc, Player player) throws Exception {
      		player.informOfNpcMessage(new ChatMessage(npc, "Greetings traveller", player));
      		player.setBusy(true);
      		World.getDelayedEventHandler().add(new ShortEvent(player) {
      			public void action() {
      				owner.setBusy(false);
				String[] options = new String[]{"Can you heal me? I'm injured", "Isn't this place built a bit out of the way?"};
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if(owner.isBusy()) {
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
						owner.setBusy(true);
						World.getDelayedEventHandler().add(new ShortEvent(owner) {
							public void action() {
								if(option == 0) {
      									owner.informOfNpcMessage(new ChatMessage(npc, "Ok", owner));
      									owner.sendMessage("The monk places his hands on your head");
      									World.getDelayedEventHandler().add(new ShortEvent(owner) {
      										public void action() {
      											owner.setBusy(false);
      											owner.sendMessage("You feel a little better");
											int newHp = owner.getCurStat(3) + 10;
											if(newHp > owner.getMaxStat(3)) {
												newHp = owner.getMaxStat(3);
											}
										      	owner.setCurStat(3, newHp);
										      	owner.sendStat(3);
      											npc.unblock();
      										}
      									});
								} else if(option == 1) {
									World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"We like it that way", "We get distrubed less", "We still get a rather large amount of travellers", "looking for sanctuary and healing here as it is"}) {
										public void finished() {
											owner.setBusy(false);
											npc.unblock();
										}
									});
								} else {
									owner.setBusy(false);
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
