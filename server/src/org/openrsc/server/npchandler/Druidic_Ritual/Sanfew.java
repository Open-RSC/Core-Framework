package org.openrsc.server.npchandler.Druidic_Ritual;

import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.World;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.model.Player;
import org.openrsc.server.npchandler.NpcHandler;

public class Sanfew implements NpcHandler {
	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"What can I do for you young 'un"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options2 = {"I've been sent to help purify the varrock stone circle", "Actually I don't need to speak to you"};
						owner.setBusy(false);
						owner.sendMenu(options2);
						owner.setMenuHandler(new MenuHandler(options2) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										stoneCircle(npc, owner);
										owner.setBusy(false);
										npc.unblock();
										break;
									case 1:
										owner.sendMessage("Sanfew grunts");
										owner.setBusy(false);
										npc.unblock();
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	private void stoneCircle(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well what I'm struggling with", "Is the meats I needed for the sacrifice to Guthix", "I need the raw meat from 4 different animals", "Which all need to be dipped in the cauldron of thunder"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options3 = {"Where can I find this cauldron?", "Ok I'll do that then"};
						owner.setBusy(false);
						owner.sendMenu(options3);
						owner.setMenuHandler(new MenuHandler(options3) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										whereCauldron(npc, owner);
										owner.setBusy(false);
										npc.unblock();
										break;
									case 1:
										owner.setBusy(false);
										npc.unblock();
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	private void whereCauldron(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"It is in the mysterious underground halls", "which are somewhere in the woods to the south of here"}) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
}