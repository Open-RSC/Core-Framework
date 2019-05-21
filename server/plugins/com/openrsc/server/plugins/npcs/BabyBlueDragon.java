package com.openrsc.server.plugins.npcs;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.PetId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class BabyBlueDragon implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == PetId.BABY_BLUE_DRAGON.id() && item.getID() == ItemId.A_GLOWING_RED_CRYSTAL.id();
	}
	
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
					if (random(0, 4) != 0) {
						player.message("You catch the baby blue dragon in the crystal.");
						removeItem(player, ItemId.A_GLOWING_RED_CRYSTAL.id(), 1);
						addItem(player, ItemId.A_RED_CRYSTAL.id(), 1);
						ActionSender.sendInventory(player);
						player.setBusy(false);
						npc.setBusyTimer(0);
						World.getWorld().unregisterNpc(npc);
						npc.remove();
					} else {
						player.message("The baby blue dragon manages to get away from you!");
						npc.setBusyTimer(0);
						player.setBusy(false);
					}
				}
			});
		}
	}
}
