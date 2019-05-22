package com.openrsc.server.plugins.minigames.pets;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.external.PetId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class BabyBlueDragon implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == PetId.BABY_BLUE_DRAGON.id() && item.getID() == ItemId.A_GLOWING_RED_CRYSTAL.id();
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		if (Constants.GameServer.WANT_PETS) {
			/*if () {
				player.message("That's someone elses pet!");
				return;
			}*/
			npc.resetPath();
			player.setBusy(true);
			npc.face(player);
			player.face(npc);
			showBubble(player, item);
			player.message("You attempt to put the baby blue dragon in the crystal.");
			npc.setBusyTimer(1600);

			Server.getServer().getEventHandler().add(new ShortEvent(player) {
				public void action() {
					Npc nearbyNpc = getMultipleNpcsInArea(player, 5, NpcId.BABY_BLUE_DRAGON.id(), NpcId.BLUE_DRAGON.id(), NpcId.RED_DRAGON.id(), NpcId.DRAGON.id());
					if (nearbyNpc != null) {
						int selected = npc.getRandom().nextInt(5);
						if (selected == 0)
							npcYell(player, nearbyNpc, "roar!");
						else if (selected == 1)
							npcYell(player, nearbyNpc, "grrrr!");
						else if (selected == 2)
							npcYell(player, nearbyNpc, "growl!");
						else if (selected == 3)
							npcYell(player, nearbyNpc, "grr!");
						else if (selected == 4) {
							npcYell(player, nearbyNpc, "roar!");
						} else if (selected == 5) {
							npcYell(player, nearbyNpc, "grrrarrr!");
						}
						message(player, 1300, "The nearby " + (nearbyNpc.getDef().getName().contains("dragon") ? nearbyNpc.getDef().getName() : "" + nearbyNpc.getDef().getName().toLowerCase()) + " take a sudden dislike to you.");
						//nearbyNpc.setChasing(player);
						//transform(nearbyNpc, 11, true);
						attack(npc, nearbyNpc);
						//attack(nearbyNpc, npc);
					}
					/*if (random(0, 4) != 0) {
						player.message("You catch the baby blue dragon in the crystal.");
						removeItem(player, ItemId.A_GLOWING_RED_CRYSTAL.id(), 1);
						addItem(player, ItemId.A_RED_CRYSTAL.id(), 1);
						ActionSender.sendInventory(player);
						player.setBusy(false);
						npc.setBusyTimer(0);
						npc.remove();
					} else {
						player.message("The baby blue dragon manages to get away from you!");
						npc.setBusyTimer(0);
						player.setBusy(false);
					}*/
				}
			});
		}
	}
}
