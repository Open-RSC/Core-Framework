package org.rscemulation.server.npchandler;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.Point;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.ShortEvent;

public class Boat implements NpcHandler 
{
	private static final String[] destinationNames = {
		
		"Karamja"/*, "Brimhaven", "Port Sarim", "Ardougne",
		"Port Khazard", "Catherby", "Shilo"
		*/
	};
	
	private static final Point[] destinationCoords = {
		
		Point.location(324, 713)/*, Point.location(467, 649), Point.location(268, 650), Point.location(538, 616),
		Point.location(541, 702), Point.location(439, 506), Point.location(471, 853)
		*/
	};

	public void handleNpc(final Npc npc, Player owner) throws Exception 
	{
		owner.setBusy(true);
  		owner.informOfNpcMessage(new ChatMessage(npc, "G'day sailor, where would you like to go?", owner));
		npc.blockedBy(owner);
		owner.setBusy(false);
  		World.getDelayedEventHandler().add(new ShortEvent(owner) 
  		{
  			public void action() 
  			{
		  		final String[] option = { "Karamja" };
				owner.sendMenu(option);
				owner.setMenuHandler(new MenuHandler(option) 
				{
					public void handleReply(final int option, final String reply) 
					{
						if(owner.isBusy()) 
						{
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
						owner.setBusy(true);
				  		World.getDelayedEventHandler().add(new ShortEvent(owner) 
				  		{
				  			public void action() 
				  			{
								if (option == 0)
								{
							  		World.getDelayedEventHandler().add(new ShortEvent(owner) 
							  		{
							  			public void action() 
							  			{
							  		  		owner.informOfNpcMessage(new ChatMessage(npc, "Alright, that'll be 30 gold please", owner));
											owner.setBusy(true);
									  		World.getDelayedEventHandler().add(new ShortEvent(owner) 
									  		{
									  			public void action() 
									  			{
													if(owner.getInventory().contains(10,30))
													{
														if(owner.getInventory().remove(10, 30) > -1)
														{
															owner.sendMessage("You pay 30 gold");
													  		World.getDelayedEventHandler().add(new ShortEvent(owner) 
													  		{
													  			public void action() 
													  			{
													  				owner.sendInventory();
																	owner.sendMessage("You board the ship.");
															  		World.getDelayedEventHandler().add(new ShortEvent(owner) 
															  		{
															  			public void action() 
															  			{
															  				owner.teleport(324, 713);
																			owner.setBusy(false);
																			npc.unblock();
															  				owner.sendMessage("The ship arrives at Karamja");
															  			}
															  		});
													  			}
													  		});
														}
													}
													else
													{
												  		World.getDelayedEventHandler().add(new ShortEvent(owner) 
												  		{
												  			public void action() 
												  			{
																owner.informOfChatMessage(new ChatMessage(owner, "Oops I don't have enough gold on me.", npc));
																owner.setBusy(false);
																npc.unblock();
												  			}
												  		});
													}
									  			}
									  		});
							  			}
							  		});
								}
				  			}
				  		});
					}
				});
  			}
  		});
		owner.setBusy(false);
		npc.blockedBy(owner);
	}
}
