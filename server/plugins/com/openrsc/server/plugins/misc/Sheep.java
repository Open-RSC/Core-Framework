package com.openrsc.server.plugins.misc;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.random;
import static com.openrsc.server.plugins.Functions.showBubble;

public class Sheep implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.SHEEP.id() && item.getID() == ItemId.SHEARS.id();
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		npc.resetPath();

		npc.face(player);
		player.face(npc);
		showBubble(player, item);
		player.message("You attempt to shear the sheep");
		npc.setBusyTimer(1600);
		player.setBatchEvent(new BatchEvent(player, 1200, Formulae.getRepeatTimes(player, Skills.CRAFTING), true) {
			@Override
			public void action() {
				npc.setBusyTimer(1600);
				if (random(0, 4) != 0) {
					player.message("You get some wool");
					addItem(player, ItemId.WOOL.id(), 1);
				} else {
					player.message("The sheep manages to get away from you!");
					npc.setBusyTimer(0);
					interrupt();
				}
			}
		});
	}
}
