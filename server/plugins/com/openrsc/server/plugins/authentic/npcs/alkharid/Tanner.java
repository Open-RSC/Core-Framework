package com.openrsc.server.plugins.authentic.npcs.alkharid;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Tanner implements TalkNpcTrigger {
	@Override
	public void onTalkNpc(Player player, final Npc n) {
		npcsay(player, n, "Greetings friend I'm a manufacturer of leather");
		int option = multi(player, n, false, //do not send over
			"Can I buy some leather then?",
			"Here's some cow hides, can I buy some leather now?",
			"Leather is rather weak stuff");

		switch (option) {
			case 0:
				say(player, n, "Can I buy some leather then?");
				npcsay(player, n, "I make leather from cow hides", "Bring me some of them and a gold coin per hide");
				break;
			case 1:
				say(player, n, "Here's some cow hides, Can I buy some leather");
				npcsay(player, n, "Ok");
				while (true) {
					delay();
					if (player.getCarriedItems().getInventory().countId(ItemId.COW_HIDE.id()) < 1) {
						say(player, n, "I don't have any cow hides left now");
						break;
					} else if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < 1) {
						//message possibly non kosher
						say(player, n, "I don't have any coins left now");
						break;
					} else if (player.getCarriedItems().remove(new Item(ItemId.COW_HIDE.id())) > -1 && player.getCarriedItems().remove(new Item(ItemId.COINS.id())) > -1) {
						player.message("You swap a cow hide for a piece of leather");
						give(player, ItemId.LEATHER.id(), 1);
					} else {
						break;
					}
				}
				break;
			case 2:
				say(player, n, "Leather is rather weak stuff");
				npcsay(player, n, "Well yes if all you're concerned with how much it will protect you in a fight");
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.TANNER.id();
	}

}
