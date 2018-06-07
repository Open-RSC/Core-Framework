package org.openrsc.server.npchandler;

import org.openrsc.server.Config;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Point;
import org.openrsc.server.model.World;
import org.openrsc.server.event.ShortEvent;

public class Gnome_Pilot implements NpcHandler {
/*
castle 271 353
Dragon maze 268 197
Mage Arena 447 3371 --- 445 3373 (NPC)
Draynor 214 632 --- 220 637 (NPC)
Ardy 550 595
Lost City 130 3545
Varrock 130 508
Watch Tower Spawn 631 739 and 636 2624
Edge 216 452
Fally 313 541 and 316 540
*/
	
	private static final String[] destinationNames = {
		/*"Mountain",*/ "Al Kharid", "Karamja"
	};
	private static final Point[] destinationCoords = {
		/*Point.location(402, 461),*/ Point.location(87, 662), Point.location(337, 713)
	};

	public void handleNpc(final Npc npc, Player player) throws Exception 
	{
			/*
			 * Disable glider to F2P
			 * Users.
			 */
			if (!player.isSub())
			{
				player.sendMessage(Config.PREFIX + "Glider is only available to subscribers.");
				return;
			}
      		player.informOfNpcMessage(new ChatMessage(npc, "Where would you like to fly?", player));
      		player.setBusy(true);
      		World.getDelayedEventHandler().add(new ShortEvent(player) 
      		{
      			public void action() 
      			{
      				owner.setBusy(false);
      				owner.setMenuHandler(new MenuHandler(destinationNames) 
      				{
					public void handleReply(final int option, final String reply) 
					{
						if(owner.isBusy() || option < 0 || option >= destinationNames.length) 
						{
							npc.unblock();
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply + ".", npc));
						owner.setBusy(true);
						World.getDelayedEventHandler().add(new ShortEvent(owner) 
						{
							public void action() 
							{
      								World.getDelayedEventHandler().add(new ShortEvent(owner) 
      								{
      									public void action() 
      									{
      										Point p = destinationCoords[option];
      										owner.teleport(p.getX(), p.getY(), false);
      										owner.setBusy(false);
      										npc.unblock();
      									}
      								});
								}
							});
						}
      				});
      				owner.sendMenu(destinationNames);
      				}
      			});
      			npc.blockedBy(player);
			}	
		}