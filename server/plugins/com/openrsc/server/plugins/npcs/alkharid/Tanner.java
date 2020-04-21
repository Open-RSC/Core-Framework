package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Tanner implements TalkNpcTrigger {
	@Override
	public void onTalkNpc(Player p, final Npc n) {
		npcsay(p, n, "Greetings friend I'm a manufacturer of leather");
		int option = multi(p, n, false, //do not send over
			"Can I buy some leather then?",
			"Here's some cow hides, can I buy some leather now?",
			"Leather is rather weak stuff");

		switch (option) {
			case 0:
				say(p, n, "Can I buy some leather then?");
				npcsay(p, n, "I make leather from cow hides", "Bring me some of them and a gold coin per hide");
				break;
			case 1:
				say(p, n, "Here's some cow hides, Can I buy some leather");
				npcsay(p, n, "Ok");
				while (true) {
					delay(p.getWorld().getServer().getConfig().GAME_TICK);
					if (p.getCarriedItems().getInventory().countId(ItemId.COW_HIDE.id()) < 1) {
						say(p, n, "I don't have any cow hides left now");
						break;
					} else if (p.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < 1) {
						//message possibly non kosher
						say(p, n, "I don't have any coins left now");
						break;
					} else if (p.getCarriedItems().remove(new Item(ItemId.COW_HIDE.id())) > -1 && p.getCarriedItems().remove(new Item(ItemId.COINS.id())) > -1) {
						p.message("You swap a cow hide for a piece of leather");
						give(p, ItemId.LEATHER.id(), 1);
					} else {
						break;
					}
				}
				break;
			case 2:
				say(p, n, "Leather is rather weak stuff");
				npcsay(p, n, "Well yes if all you're concerned with is how much it will protect you in a fight");
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.TANNER.id();
	}

}
