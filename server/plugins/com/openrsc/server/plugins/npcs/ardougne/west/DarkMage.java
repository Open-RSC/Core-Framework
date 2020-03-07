package com.openrsc.server.plugins.npcs.ardougne.west;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DarkMage implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.DARK_MAGE.id()) {
			say(p, n, "hello there");
			npcsay(p, n, "why do do you interupt me traveller?");
			say(p, n, "i just wondered what you're doing?");
			npcsay(p, n, "i experiment with dark magic",
				"it's a dangerous craft");
			if (p.getCarriedItems().hasCatalogID(ItemId.STAFF_OF_IBAN_BROKEN.id(), Optional.of(false)) && p.getQuestStage(Quests.UNDERGROUND_PASS) == -1) {
				say(p, n, "could you fix this staff?");
				p.message("you show the mage your staff of iban");
				npcsay(p, n, "almighty zamorak! the staff of iban!");
				say(p, n, "can you fix it?");
				npcsay(p, n, "this truly is dangerous magic traveller",
					"i can fix it, but it will cost you",
					"the process could kill me");
				say(p, n, "how much?");
				npcsay(p, n, "200,000 gold pieces, not a penny less");
				int menu = multi(p, n,
					"no chance, that's ridiculous",
					"ok then");
				if (menu == 0) {
					npcsay(p, n, "fine by me");
				} else if (menu == 1) {
					if (!ifheld(p, ItemId.COINS.id(), 200000)) {
						p.message("you don't have enough money");
						say(p, n, "oops, i'm a bit short");
					} else {
						Functions.mes(p, "you give the mage 200,000 coins",
							"and the staff of iban");
						remove(p, ItemId.COINS.id(), 200000);
						remove(p, ItemId.STAFF_OF_IBAN_BROKEN.id(), 1);
						p.message("the mage fixes the staff and returns it to you");
						give(p, ItemId.STAFF_OF_IBAN.id(), 1);
						say(p, n, "thanks mage");
						npcsay(p, n, "you be carefull with that thing");
					}
				}
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.DARK_MAGE.id();
	}

}
