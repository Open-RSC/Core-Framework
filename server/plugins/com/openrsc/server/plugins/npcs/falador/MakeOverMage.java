package com.openrsc.server.plugins.npcs.falador;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MakeOverMage implements TalkNpcTrigger {
	@Override
	public void onTalkNpc(Player p, final Npc n) {
		npcsay(p, n, "Are you happy with your looks?",
			"If not I can change them for the cheap cheap price",
			"Of 3000 coins");
		int opt = multi(p, n, "I'm happy with how I look thank you",
			"Yes change my looks please");
		if (opt == 1) {
			if (!ifheld(p, ItemId.COINS.id(), 3000)) {
				say(p, n, "I'll just go and get the cash");
			} else {
				p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 3000));
				p.setChangingAppearance(true);
				ActionSender.sendAppearanceScreen(p);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.MAKE_OVER_MAGE.id();
	}

}
