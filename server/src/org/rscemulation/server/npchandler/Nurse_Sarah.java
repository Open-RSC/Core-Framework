package org.rscemulation.server.npchandler;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.ChatMessage;
import org.rscemulation.server.model.MenuHandler;
import org.rscemulation.server.model.World;
import org.rscemulation.server.event.ShortEvent;

public class Nurse_Sarah implements NpcHandler {

	public void handleNpc(final Npc npc, Player player) throws Exception 
	{
			if (player.getCurStat(3) < player.getMaxStat(3))
			{
				player.informOfNpcMessage(new ChatMessage(npc, "You look wounded", player));
      			player.setBusy(true);
			}
			else
			{
				player.informOfNpcMessage(new ChatMessage(npc, "Greetings Traveller", player));
      			return;
      		}
      		
      		World.getDelayedEventHandler().add(new ShortEvent(player) 
      		{
      			public void action() 
      			{
      				owner.setBusy(false);
      				String[] options = new String[]{"Can you heal me please Nurse Sarah"};
      				owner.setMenuHandler(new MenuHandler(options) 
      				{
					public void handleReply(final int option, final String reply) 
					{
						if(owner.isBusy()) 
							return;
						
						owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
						owner.setBusy(true);
						
						World.getDelayedEventHandler().add(new ShortEvent(owner) 
						{
							public void action()
							{
								if(option == 0) 
								{
      									owner.informOfNpcMessage(new ChatMessage(npc, "Sure, try to be more careful next time.", owner));
      									owner.sendMessage("Sarah bandages your wounds.");
      									World.getDelayedEventHandler().add(new ShortEvent(owner) 
      									{
      										public void action() 
      										{
      											owner.setBusy(false);
      											owner.sendMessage("You feel better");
											int newHp = owner.getMaxStat(3);
											if(newHp > owner.getMaxStat(3)) 
											{
												newHp = owner.getMaxStat(3);
											}
										      	owner.setCurStat(3, newHp);
										      	owner.sendStat(3);
      											npc.unblock();
      										}
      									});
								} 
								else 
								{
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
