package com.openrsc.server.plugins.custom.minigames.pets;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class BabyBlueDragon implements UseNpcTrigger {

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.BABY_BLUE_DRAGON.id() && item.getCatalogId() == ItemId.A_GLOWING_RED_CRYSTAL.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (config().WANT_PETS) {
			npc.resetPath();
			//npc.resetRange();
			npc.face(player);
			player.face(npc);
			thinkbubble(item);
			player.message("You attempt to put the baby blue dragon in the crystal.");
			delay(2);
			/*Npc nearbyNpc = getMultipleNpcsInArea(player, 5, NpcId.BABY_BLUE_DRAGON.id(), NpcId.BLUE_DRAGON.id(), NpcId.RED_DRAGON.id(), NpcId.DRAGON.id());
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
				nearbyNpc.setChasing(player);
				//transform(nearbyNpc, 11, true);
				//attack(npc, nearbyNpc);
			}*/
			if (random(0, 4) != 0) {
				player.message("You catch the baby blue dragon in the crystal.");
				player.getCarriedItems().remove(new Item(ItemId.A_GLOWING_RED_CRYSTAL.id()));
				give(player, ItemId.A_RED_CRYSTAL.id(), 1);
				npc.remove();
			} else {
				player.message("The baby blue dragon manages to get away from you!");
			}
		}
	}
}
