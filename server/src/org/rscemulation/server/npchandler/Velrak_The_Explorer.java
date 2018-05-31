package org.rscemulation.server.npchandler;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.InvItem;
import org.rscemulation.server.model.Item;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.SingleEvent;
import org.rscemulation.server.event.DelayedQuestChat;

public class Velrak_The_Explorer implements NpcHandler {

	public void handleNpc(final Npc npc, Player player) throws Exception {
		npc.blockedBy(player);
		player.setBusy(true);	
		if(player.getInventory().countId(596) > 0) {
			for(Player informee : player.getViewArea().getPlayersInView()) {
				informee.informOfChatMessage(new ChatMessage(player, "Are you still here?", npc));
			}
			String[] messagesA = {"Yes, I'm still plucking up courage", "To run out past those black knights"};
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, player, messagesA) {
				public void finished() {
					owner.setBusy(false);
					npc.unblock();
				}
			});
		} else {
			for(Player informee : player.getViewArea().getPlayersInView()) {
				informee.informOfNpcMessage(new ChatMessage(npc, "Thankyou for rescuing me", player));
			}
			String[] messagesB = {"It isn't comfy in this cell"};
			World.getDelayedEventHandler().add(new DelayedQuestChat(npc, player, messagesB) {
				public void finished() {
					npc.blockedBy(owner);				
					World.getDelayedEventHandler().add(new SingleEvent(owner, 3000) {
						public void action() {
							String[] optionsA = {"So do you know anywhere good to explore?", "Do I get a reward?"};
							owner.setBusy(false);
							owner.setMenuHandler(new MenuHandler(optionsA) {
								public void handleReply(final int option, final String reply) {
									for(Player informee : owner.getViewArea().getPlayersInView()) {
										informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
									}
									switch(option) {
										case 0:
											owner.setBusy(true);
											String[] messagesC = {"Well this dungeon was quite good to explore", "Till I got captured", "I got given a key to an inner part of this dungeon", "By a mysterious cloaked stranger", "It's rather to tough for me to get that far though", "I keep getting captured", "Would you like to give it a go"};
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messagesC) {
												public void finished() {
													npc.blockedBy(owner);												
													World.getDelayedEventHandler().add(new SingleEvent(owner, 3000) {
														public void action() {
															owner.setBusy(false);										
															String[] optionsB = {"Yes please", "No it's too dangerous for me too"};
															owner.setMenuHandler(new MenuHandler(optionsB) {
																public void handleReply(final int option, final String reply) {
																	for(Player informee : owner.getViewArea().getPlayersInView()) {
																		informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
																	}
																	switch(option) {
																		case 0:
																			owner.sendMessage("Velrak reaches inside of his boot and passes you a key");
																			if(!owner.getInventory().full()) {
																				owner.getInventory().add(new InvItem(596));
																				owner.sendInventory();
																			} else {
																				owner.sendMessage("The key falls to the ground");
																				World.registerEntity(new Item(596, owner.getX(), owner.getY(), 1, owner));
																			}
																			break;
																	}
																}
															});
															owner.sendMenu(optionsB);
														}
													});
												};
											});
											break;
										case 1:
											owner.setBusy(true);
											String[] messagesD = {"Well not really the black knights took all my stuff before throwing me in here"};
											World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, messagesD) {
												public void finished() {
													owner.setBusy(false);
													npc.unblock();
												}
											});
											break;
									}
								}
							});
							owner.sendMenu(optionsA);
						}
					});
				}
			});
		}
	}
}