package com.openrsc.server.plugins.authentic.npcs.falador;

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
	public void onTalkNpc(Player player, final Npc n) {
		npcsay(player, n, "Are you happy with your looks?",
			"If not I can change them for the cheap cheap price",
			"Of 3000 coins");
		int opt = multi(player, n, "I'm happy with how I look thank you",
			"Yes change my looks please");
		if (opt == 1) {
			if (!ifheld(player, ItemId.COINS.id(), 3000)) {
				say(player, n, "I'll just go and get the cash");
			} else {
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 3000));
				player.setChangingAppearance(true);
				ActionSender.sendAppearanceScreen(player);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MAKE_OVER_MAGE.id();
	}

}
